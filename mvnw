#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Wrapper startup shell script
#
# Required ENV vars:
# JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars:
# MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ]; then
  if [ -f /etc/mavenrc ]; then
    . /etc/mavenrc
  fi

  if [ -f "$HOME/.mavenrc" ]; then
    . "$HOME/.mavenrc"
  fi
fi

# ----------------------------------------------------------------------------
# Maven Wrapper specific commands
# ----------------------------------------------------------------------------

wrapper_jar=".mvn/wrapper/maven-wrapper.jar"
wrapper_launcher="org.apache.maven.wrapper.MavenWrapperMain"

if [ -z "$JAVA_HOME" ]; then
  java_exe="java"
else
  java_exe="$JAVA_HOME/bin/java"
fi

if [ ! -f "$wrapper_jar" ]; then
    echo "Error: Wrapper JAR not found." >&2
    exit 1
fi

"$java_exe" $MAVEN_OPTS -classpath "$wrapper_jar" "$wrapper_launcher" "$@"
