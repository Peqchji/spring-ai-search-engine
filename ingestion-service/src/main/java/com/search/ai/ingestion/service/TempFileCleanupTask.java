package com.search.ai.ingestion.service;

import com.search.ai.shared.util.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Slf4j
@Service
public class TempFileCleanupTask {

    @Value(AppConstants.PROP_TEMP_FILE_RETENTION_DAYS)
    private long retentionDays;

    @Value(AppConstants.PROP_TEMP_FILE_PREFIX)
    private String tempFilePrefix;

    /**
     * Background worker that runs every hour to clean up temporary ingestion files
     * that are older than the configured retention period. This prevents the OS
     * temp directory from filling
     * up while allowing files to live securely longer than the active ingestion
     * request lifecycle.
     */
    @Scheduled(fixedRateString = AppConstants.PROP_TEMP_FILE_RATE_MS)
    public void cleanupOldTempFiles() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        Path tmpDirPath = Path.of(tmpDir);
        Instant cutoffTime = Instant.now().minus(retentionDays, ChronoUnit.DAYS);

        log.info("Starting cleanup of temporary ingest files in {} older than {} days ({})", tmpDir, retentionDays,
                cutoffTime);

        try (Stream<Path> files = Files.list(tmpDirPath)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith(tempFilePrefix))
                    .forEach(path -> {
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                            if (attrs.lastModifiedTime().toInstant().isBefore(cutoffTime)) {
                                Files.deleteIfExists(path);
                                log.info("Deleted old temporary file: {}", path);
                            }
                        } catch (IOException e) {
                            log.warn("Failed to read attributes or delete file: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to list temporary directory for cleanup", e);
        }
    }
}
