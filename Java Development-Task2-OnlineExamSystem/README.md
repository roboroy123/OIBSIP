# Online Examination System (Java Swing)

## Run
- Windows: double-click `run.bat`
- macOS/Linux: `./run.sh`
- Any OS: `java -jar ExamSystem.jar`  (needs Java 8 or newer)

## Demo logins
- student / pass123
- alice / alice123

## Rebuild
`./build.sh` (needs a JDK) or `javac -d out src/ExamSystem.java`

## Customize
In `src/ExamSystem.java`: `EXAM_MINUTES` (set to 1 to test auto-submit) and `seedQuestions()`.
