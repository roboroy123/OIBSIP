#!/bin/sh
# Rebuild from source (requires a JDK: javac + jar)
cd "$(dirname "$0")"
mkdir -p out && javac -d out src/ExamSystem.java \
 && printf 'Main-Class: ExamSystem\n' > manifest.txt \
 && jar cfm ExamSystem.jar manifest.txt -C out . && rm -rf out manifest.txt \
 && echo "Built ExamSystem.jar"
