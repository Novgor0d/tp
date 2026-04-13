# Purav Mahesh – Project Portfolio Page

## LinuxLingo - Exam Module

I was the **primary developer** of the **Exam module** in **LinuxLingo**, an educational CLI app teaching Linux commands through practice and exam-style questions. The module allows students to take structured exams with multiple question types (MCQ, fill-in-the-blanks, and practical shell exercises) and receive instant feedback with scoring. I designed the **end-to-end exam experience**, from question data models and parsing pipelines, through session management and interactive workflows, to comprehensive grading, feedback, and results reporting—emphasizing robustness, maintainability, and seamless CLI user experience.

## Contributions

**Code:** [RepoSense Dashboard](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=puma-31&breakdown=true)

**Enhancements:**
- **Exam Workflow:** Orchestration system supporting three entry modes (interactive, direct CLI args, random question selection). Implemented `ExamSession`, `ExamCommandParser`, and state transitions across three question types (MCQ, FITB, PRAC) with context-aware control commands (`quit`, `abort`, `exit`) enabling flexible exam interruption.
- **Question Parsing:** Built robust, extensible pipeline for loading pipe-delimited question files with type-specific parsers and validation. Handles regex-based splitting, escaped pipes, 5 checkpoint verification types, and setup item parsing for PRAC initialization. Includes graceful error handling that skips malformed lines with warnings.
- **Session Management:** Implemented state machine orchestrating complete exam sessions using `ExamSession.runExam()` with `QuestionInteraction` for centralized answer collection. Applied VFS factory pattern for test isolation in practical questions, defensive programming (null checks, state validation), and supports partial completion when users abort mid-exam.
- **Grading & Feedback:** Polymorphic `Question.checkAnswer()` with type-specific logic — `McqQuestion` (case-insensitive A-D matching), `FitbQuestion` (exact string matching), `PracQuestion` (VFS verification). Calculates score, percentage, letter grade, and displays structured feedback (✓/✗ + explanation).
- **Practical Questions:** PRAC question system enabling hands-on, auto-graded shell exercises with `Checkpoint` supporting 5 verification types (DIR, FILE, NOT_EXISTS, CONTENT_EQUALS, PERM) and `SetupItem` for VFS pre-configuration (MKDIR, FILE, PERM). Factory pattern creates fresh VFS per question for test isolation.
- **Testing & Quality:** Comprehensive unit tests covering parsing, grading, session logic, and edge cases (invalid inputs, malformed questions, skipped/aborted exams). Achieved high coverage for critical exam paths; refactored code to eliminate duplication and ensure separation of concerns.

**Documentation:**
- **User Guide:** Authored comprehensive [Exam System](https://ay2526s2-cs2113-t10-2.github.io/tp/UserGuide.html#exam-system) section documenting exam modes, commands (4 invocation formats with examples), interactive flow, in-exam control commands with context-dependent behavior table (MCQ/FITB vs. PRAC), question types with answer instructions, PRAC workflow examples, and edge case handling.
- **Developer Guide:** Authored 3 major sections (~400 lines) — [Exam Session Flow](https://ay2526s2-cs2113-t10-2.github.io/tp/DeveloperGuide.html#exam-session-flow), Exam Component (Practical Questions), and Question Parsing and Loading. Covered exam module architecture (entry points, orchestration, integration), question types & grading logic, control command design decisions, parsing pipeline details (tokenization, type-specific rules, error handling), and checkpoint verification examples. Created 8 UML diagrams: 2 class diagrams (`ExamClassDiagram.puml`, `PracQuestionClassDiagram.puml`), 4 sequence diagrams (`ExamSessionSequence.puml`, `ExamControlCommandsSequence.puml`, `PracQuestionSequence.puml`, `McqFitbAnswerInteractionSequence.puml`), and 2 activity diagrams (`PracQuestionSetupActivity.puml`, `McqFitbLineParsingActivity.puml`).

**Team Contributions:**
- Code reviews: [#152](https://github.com/AY2526S2-CS2113-T10-2/tp/pull/152), [#212](https://github.com/AY2526S2-CS2113-T10-2/tp/pull/212), [#94](https://github.com/AY2526S2-CS2113-T10-2/tp/pull/94)
- Infrastructure: Question parsing framework, exam command handling, VFS factory pattern
- Issue management for Exam module, thorough testing & bug identification, infrastructure enhancements

**Contributions Beyond the Team**
- Reported 10 bugs in another team's project: [PE-D Bug Reports](https://github.com/NUS-CS2113-AY2526-S2/ped-puma-31/issues)
