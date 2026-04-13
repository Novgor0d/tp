# Purav Mahesh – Project Portfolio Page

## LinuxLingo - Exam Module

I was the **primary developer** of the **Exam module** in **LinuxLingo**, an educational CLI app teaching Linux commands through practice and exam-style questions. I designed the **end-to-end exam experience**, from question data models and parsing, through session management and interactive workflows, to grading and results reporting.

## Contributions

**Code:** [RepoSense Dashboard](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=puma-31&breakdown=true)

**Enhancements:**
- **Exam Workflow:** Three entry modes (interactive, CLI args, random) with `ExamSession`, `ExamCommandParser`, state transitions across MCQ/FITB/PRAC types, context-aware control commands (`quit`, `abort`, `exit`).
- **Question Parsing:** Robust pipeline loading pipe-delimited questions; type-specific parsers with validation and graceful error handling (regex splitting, escaped pipes, 5 checkpoint types, setup items for PRAC initialization).
- **Session Management:** State machine orchestrating exams with `ExamSession.runExam()`, `QuestionInteraction`, VFS factory pattern for PRAC isolation, defensive programming, supports partial completion.
- **Grading & Feedback:** Polymorphic `Question.checkAnswer()` — `McqQuestion` (case-insensitive A-D), `FitbQuestion` (exact match), `PracQuestion` (VFS verification); displays score, grade, structured feedback (✓/✗ + explanation).
- **Practical Questions:** PRAC system with auto-graded shell exercises; `Checkpoint` supporting 5 types (DIR, FILE, NOT_EXISTS, CONTENT_EQUALS, PERM); `SetupItem` for pre-configuration (MKDIR, FILE, PERM).
- **Testing:** Unit tests covering parsing, grading, session logic with edge cases; high coverage for critical exam paths; refactored to eliminate duplication.

**Documentation:**
- **User Guide:** Authored [Exam System](https://ay2526s2-cs2113-t10-2.github.io/tp/UserGuide.html#exam-system) section — exam modes, commands (4 formats), control commands with behavior table, PRAC workflow, topic management.
- **Developer Guide:** Authored 3 sections — [Exam Session Flow](https://ay2526s2-cs2113-t10-2.github.io/tp/DeveloperGuide.html#exam-session-flow), exam architecture (entry points, orchestration), question types & grading, parsing pipeline, PRAC checkpoints. Created 8 UML diagrams: `ExamClassDiagram.puml`, `PracQuestionClassDiagram.puml`, `ExamSessionSequence.puml`, `ExamControlCommandsSequence.puml`, `PracQuestionSequence.puml`, `McqFitbAnswerInteractionSequence.puml`, `PracQuestionSetupActivity.puml`, `McqFitbLineParsingActivity.puml`.

**Team Contributions:**
- Code reviews: [#152](https://github.com/AY2526S2-CS2113-T10-2/tp/pull/152), [#212](https://github.com/AY2526S2-CS2113-T10-2/tp/pull/212), [#94](https://github.com/AY2526S2-CS2113-T10-2/tp/pull/94)
- Infrastructure: Question parsing framework, exam command handling, VFS factory pattern
- Issue management, thorough testing & bug identification, infrastructure enhancements

**Contributions Beyond the Team**
- Reported 10 bugs in another team's project: [PE-D Bug Reports](https://github.com/NUS-CS2113-AY2526-S2/ped-puma-31/issues)
