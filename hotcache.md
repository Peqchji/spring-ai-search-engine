# Hot Cache — Context & Cheat Sheet

## 📍 Current State
*   **Active Branch**: `feat/query-expansion`
*   **Active Phase**: Phase 2 (Query Expansion Service)
*   **Target LLM Model**: `llama3.2` (via local Ollama)

---

## 🏗 Microservice Ports & Topology
| Service | Port | Description | Status |
|---|---|---|---|
| `api-gateway` | `8080` | Edge API Router | Ready |
| `search-orchestrator` | `8081` | Pipeline coordinator (Redis state) | Ready |
| `ingestion-service` | `8085` | Document ingestion / TEI indexing | Ready |
| `query-expansion-service` | TBD | LLM Query Expansion | **In Progress (Phase 2)** |
| `hybrid-retrieval-service`| TBD | ES RRF vector + BM25 search | Planned (Phase 3) |
| `ranker-service` | TBD | LTR scoring reranker | Planned (Phase 4) |

---

## ⚙️ Shared Infrastructure (Compose)
*   **Kafka**: `localhost:9092` (Topic Broker)
*   **Elasticsearch**: `localhost:9200` (Vector & Keyword Store)
*   **Redis**: `localhost:6379` (Profile Cache & State Machine broadcast)
*   **Ollama**: `localhost:11434` (Llama 3.2 engine)
*   **TEI Sidecar**: `localhost:8080` (HuggingFace Embeddings)
*   **Kafka UI**: `localhost:8090` (Topic monitor)

---

## 🛠 Developer Commands

### Compile & Build
```bash
# Compile and build runnable JARs skipping tests
./mvnw.cmd clean package -DskipTests
```

### Run Infrastructure
```bash
# Start all supporting services
docker-compose up -d
```

### Core Search API Endpoint
```bash
# Query endpoint (POST through gateway)
curl -X POST http://localhost:8080/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "What is the refund policy?",
    "userContext": {
      "userId": "user-123",
      "location": { "lat": 13.7563, "lon": 100.5018 },
      "preferences": ["electronics", "warranty"],
      "recentSearches": ["return laptop", "shipping damage"]
    }
  }'
```

---

## 📜 Roadmap Status
1.  [x] **Phase 1**: Stabilize Ingestion service & direct Elasticsearch indexing.
2.  [x] **Phase 1.5**: Integrate `search-orchestrator` in Maven modules & `docker-compose.yml`.
3.  [/] **Phase 2**: Create `query-expansion-service` (Spring AI + Ollama).
4.  [ ] **Phase 3**: Create `hybrid-retrieval-service` (Elasticsearch RRF + boosting).
5.  [ ] **Phase 4**: Create `ranker-service` (LTR model).
6.  [ ] **Phase 5**: Complete integration & end-to-end trace validation.
