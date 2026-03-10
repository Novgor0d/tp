# LinuxLingo — Development Guide

This document describes what each team member is responsible for, what infrastructure is already provided, and which APIs to use. Every stub file contains detailed inline TODOs — this guide gives the big picture so you can start coding without coordination overhead.

---

## Architecture Overview

```plaintext
linuxlingo/
├── LinuxLingo.java              ← main(), wires everything (infra)
├── cli/
│   ├── Ui.java                  ← all I/O: readLine, println, printError (infra)
│   └── MainParser.java          ← top-level REPL: shell / exam / exec / help / exit (infra)
├── shell/
│   ├── ShellParser.java         ← tokenize + build ParsedPlan (stub — A)
│   ├── ShellSession.java        ← REPL + plan execution engine (stub — A)
│   ├── CommandRegistry.java     ← name → Command mapping (infra)
│   ├── CommandResult.java       ← stdout / stderr / exitCode (infra)
│   ├── command/
│   │   ├── Command.java         ← interface: execute / getUsage / getDescription (infra)
│   │   ├── EchoCommand.java     ← reference implementation (infra)
│   │   ├── MkdirCommand.java    ← reference implementation (infra)
│   │   ├── PwdCommand.java      ← reference implementation (infra)
│   │   ├── TouchCommand.java    ← reference implementation (infra)
│   │   ├── [9 B-owned stubs]
│   │   └── [12 C-owned stubs]
│   └── vfs/                     ← Virtual File System (all infra)
│       ├── VirtualFileSystem.java
│       ├── FileNode.java
│       ├── Directory.java
│       ├── RegularFile.java
│       ├── Permission.java
│       └── VfsException.java
├── exam/
│   ├── ExamSession.java         ← exam orchestrator (stub — D)
│   ├── QuestionBank.java        ← topic → questions map (stub — D)
│   ├── ExamResult.java          ← score tracking (infra)
│   ├── Checkpoint.java          ← VFS verifier for PRAC questions (infra)
│   └── question/
│       ├── Question.java        ← abstract base: present / checkAnswer (infra)
│       ├── McqQuestion.java     ← Multiple Choice (stub — D)
│       ├── FitbQuestion.java    ← Fill In The Blank (stub — D)
│       └── PracQuestion.java    ← Practical / VFS-check (stub — D)
└── storage/
    ├── Storage.java             ← generic file I/O utilities (infra)
    ├── StorageException.java    ← checked exception for I/O errors (infra)
    ├── VfsSerializer.java       ← VFS ↔ .env file conversion (stub — B)
    ├── QuestionParser.java      ← .txt → Question parsing (stub — D)
    └── ResourceExtractor.java   ← JAR resource extraction (stub — D)
```

---

## Shared Infrastructure

The following files are **fully implemented** and ready to use. Do **not** modify them unless discussed with the team.

### `Ui` — All User I/O

| Method | Description |
| -------- | ------------- |
| `readLine()` | Read one line from stdin |
| `readLine(String prompt)` | Print prompt, then read |
| `println(String)` | Print to stdout |
| `printError(String)` | Print to stderr |
| `printWelcome()` | Print the ASCII logo |
| `clearScreen()` | ANSI clear |
| `confirm(String)` | Prompt y/n, return boolean |

### `CommandResult` — Command Output

| Factory | Description |
| --------- | ------------- |
| `CommandResult.success(String stdout)` | exit code 0 |
| `CommandResult.error(String stderr)` | exit code 1 |
| `CommandResult.exit()` | signal session exit |
| `getStdout()` / `getStderr()` / `getExitCode()` / `isSuccess()` / `shouldExit()` | Getters |

### `Command` Interface

```java
CommandResult execute(ShellSession session, String[] args, String stdin);
String getUsage();
String getDescription();
```

- **Error convention:** `CommandResult.error("cmdName: message")` — always prefix with the command name.
- **Stdin priority:** If file args are given → use files (ignore stdin). If no file args and `stdin != null` → use stdin. If neither → error.

### `CommandRegistry`

| Method | Description |
| -------- | ------------- |
| `get(String name)` | Look up a command by name; returns `null` if not found |
| `getAllNames()` | Sorted set of all registered command names |
| `getHelpText()` | Ordered map of name → description |

### `VirtualFileSystem` (VFS) — The Core Simulation Layer

This is the in-memory file system that all commands operate on. **All path operations go through VFS** — never use `java.io` or `java.nio.file` for simulated files.

| Method | Signature | Description |
| -------- | ----------- | ------------- |
| resolve | `resolve(path, workingDir)` → `FileNode` | Resolve path; throws `VfsException` if not found |
| resolveParent | `resolveParent(path, workingDir)` → `Directory` | Get parent directory |
| exists | `exists(path, workingDir)` → `boolean` | Check path existence |
| createFile | `createFile(path, workingDir)` → `RegularFile` | Create or touch a file |
| createDirectory | `createDirectory(path, workingDir, parents)` → `Directory` | Create dir; `parents=true` for `-p` |
| delete | `delete(path, workingDir, recursive, force)` | Remove node; `recursive` for `-r`, `force` for `-f` |
| copy | `copy(src, dest, workingDir, recursive)` | Copy file/dir |
| move | `move(src, dest, workingDir)` | Move/rename |
| readFile | `readFile(path, workingDir)` → `String` | Read file content |
| writeFile | `writeFile(path, workingDir, content, append)` | Write or append |
| listDirectory | `listDirectory(path, workingDir, showHidden)` → `List<FileNode>` | List children |
| findByName | `findByName(startPath, workingDir, pattern)` → `List<FileNode>` | Recursive glob search |
| getAbsolutePath | `getAbsolutePath(path, workingDir)` → `String` | Normalize to absolute |
| getRoot | `getRoot()` → `Directory` | Root node for tree walking |
| deepCopy | `deepCopy()` → `VirtualFileSystem` | Full clone |

**VFS Node Types:**

- `FileNode` — abstract: `getName()`, `isDirectory()`, `getAbsolutePath()`, `getPermission()`, `getParent()`
- `Directory` — `getChildren()`, `getChild(name)`, `hasChild(name)`, `addChild(node)`, `removeChild(name)`
- `RegularFile` — `getContent()`, `setContent(s)`, `appendContent(s)`, `getSize()`
- `Permission` — `fromOctal("755")`, `fromSymbolic("rwxr-xr-x")`, `toString()`, `canOwnerRead/Write/Execute()`

### `Storage` — Real Disk I/O

| Method | Description |
| -------- | ------------- |
| `readFile(Path)` → `String` | Read entire file |
| `readLines(Path)` → `List<String>` | Read all lines |
| `writeFile(Path, String)` | Write (creates parent dirs) |
| `appendFile(Path, String)` | Append |
| `exists(Path)` → `boolean` | Check existence |
| `delete(Path)` → `boolean` | Delete if exists |
| `listFiles(Path dir, String ext)` → `List<Path>` | Filter by extension |
| `ensureDirectory(Path)` | Create dirs recursively |
| `getDataDir()` → `Path` | `data/` |
| `getDataSubDir(String)` → `Path` | e.g. `data/environments/` |

### `Question` (abstract base)

| Member | Description |
| -------- | ------------- |
| `QuestionType` enum | `MCQ`, `FITB`, `PRAC` |
| `Difficulty` enum | `EASY`, `MEDIUM`, `HARD` |
| `present()` | Abstract — format for display |
| `checkAnswer(String)` | Abstract — verify user answer |
| `formatHeader()` | Returns `"(MCQ · EASY)"` style header |
| `getQuestionText()` / `getExplanation()` / `getDifficulty()` / `getType()` | Getters |

### `Checkpoint` — PRAC Question Verifier

| Method | Description |
|--------|-------------|
| `matches(VirtualFileSystem)` → `boolean` | Check if path exists with correct type (DIR/FILE) |
| `getPath()` / `getExpectedType()` | Getters |

### `ExamResult` — Score Tracker

| Method | Description |
| -------- | ------------- |
| `addResult(Question, String userAnswer, boolean correct)` | Record one outcome |
| `getScore()` / `getTotal()` / `getPercentage()` | Stats |
| `display()` → `String` | `"Score: 7/10 (70%)"` |

### Reference Command Implementations

Four fully-implemented commands are provided as coding examples:

| Command | Key Pattern Demonstrated |
| -------- | -------------------------- |
| `EchoCommand` | Simplest command — just joins args and returns `success()` |
| `PwdCommand` | One-liner reading state from `session.getWorkingDir()` |
| `TouchCommand` | VFS mutation with `VfsException` → `CommandResult.error()` |
| `MkdirCommand` | Flag parsing (`-p`), VFS call, exception wrapping |

Study these before implementing your own commands. The pattern is always:

1. Parse flags/arguments from `args[]`
2. Call VFS methods on `session.getVfs()`
3. Catch `VfsException` → return `CommandResult.error("cmdName: " + e.getMessage())`
4. Return `CommandResult.success(output)`

---

## Member A — Shell Parser & Session Engine

### Scope

| File | What to Implement |
|------|-------------------|
| `ShellParser.java` | `parse(String input)` — tokenizer + segment builder |
| `ShellSession.java` | `start()`, `executeOnce()`, `executePlan()`, `executePlanSilent()` |

### What's Provided

- **`ShellParser`**: All data classes are defined — `Token`, `TokenType`, `RedirectInfo`, `Segment`, `ParsedPlan`. You only need to implement the `parse()` method.
- **`ShellSession`**: Constructor, all getters/setters (including `setLastExitCode(int)`, `setWorkingDir()`, `setPreviousDir()`, `replaceVfs()`), `getPrompt()`, `isRunning()`. You only need to implement the 4 stub methods.
- **`CommandRegistry`**: Fully wired — use `session.getRegistry().get(commandName)` to look up commands.
- **`Ui`**: Use `session.getUi().readLine(prompt)` for input, `session.getUi().println()` for output.

### Key Tasks

**`parse(String input)`** — The tokenizer is the most complex algorithm in the project. A detailed step-by-step state machine is provided in the inline TODO:

1. Char-by-char iteration with 3 states: `NORMAL`, `IN_SINGLE_QUOTE`, `IN_DOUBLE_QUOTE`
2. Recognize operators: `|` (PIPE), `>` (REDIRECT), `>>` (APPEND), `&&` (AND), `;` (SEMICOLON)
3. Split tokens into `Segment` objects separated by inter-segment operators
4. Extract `RedirectInfo` when `>` or `>>` appears (next WORD is the target file)

**`executePlan(String input)`** — The execution engine:

1. Call `new ShellParser().parse(input)` to get a `ParsedPlan`
2. Iterate `plan.segments`, checking `plan.operators` between them:
   - `PIPE` → feed previous stdout as `stdin` to next command
   - `AND` → skip remaining if `lastExitCode != 0`
   - `SEMICOLON` → always continue
3. Look up command via `session.getRegistry().get(segment.commandName)` — if `null`, print error and set exit code 127
4. Call `command.execute(session, segment.args, stdin)`
5. Handle redirect: if `segment.redirect != null`, call `vfs.writeFile(target, workingDir, stdout, isAppend)`
6. Update `setLastExitCode(result.getExitCode())`
7. Print final stdout via `ui.println()`

**`executePlanSilent(String input)`** — Same logic as `executePlan`, but return `CommandResult` instead of printing.

**`executeOnce(String input)`** — Trivial: `return executePlanSilent(input);`

**`start()`** — Interactive REPL: read input in a loop, handle "back"/"exit"/"done" keywords, delegate to `executePlan()`.

### Dependencies on Other Members

- Depends on **B & C's commands** being registered in `CommandRegistry`. Commands that are not yet implemented will throw `UnsupportedOperationException` — you can test with the 4 reference commands (`echo`, `pwd`, `mkdir`, `touch`).
- Does **not** depend on the exam module.

---

## Member B — Navigation Commands & Environment Persistence

### Scope

| File | What to Implement |
| ------ | ------------------- |
| `CdCommand.java` | Change working directory (`cd`, `cd -`, `cd ~`) |
| `LsCommand.java` | List directory (`ls`, `ls -a`, `ls -l`) |
| `ClearCommand.java` | Clear screen |
| `HelpCommand.java` | Display all commands and descriptions |
| `SaveCommand.java` | Save VFS snapshot to file |
| `LoadCommand.java` | Load VFS snapshot from file |
| `ResetCommand.java` | Reset VFS to default state |
| `EnvListCommand.java` | List saved environments |
| `EnvDeleteCommand.java` | Delete a saved environment |
| `VfsSerializer.java` | VFS ↔ `.env` text format + file operations |

### What's Provided

- **`Command` interface**: Implement `execute(ShellSession, String[], String)`, `getUsage()`, `getDescription()`. Every stub already has the class skeleton and detailed TODOs.
- **4 reference commands** (`EchoCommand`, `MkdirCommand`, `PwdCommand`, `TouchCommand`): Study these for the coding pattern.
- **`VirtualFileSystem`**: All VFS operations are ready — `resolve()`, `listDirectory()`, `getAbsolutePath()`, etc.
- **`Storage`**: All disk I/O is ready — `readFile()`, `writeFile()`, `listFiles()`, `getDataSubDir()`, etc.
- **`VfsSerializer`**: The `DeserializedVfs` inner class is provided. The `.env` file format and escaping rules are fully documented in Javadoc.
- **`ShellSession` getters**: `getVfs()`, `getWorkingDir()`, `setWorkingDir()`, `getPreviousDir()`, `setPreviousDir()`, `getUi()`, `getRegistry()`, `replaceVfs()`.

### Key Tasks

**Navigation Commands:**
- `CdCommand`: Resolve target with `vfs.resolve()`, verify it's a directory, update `session.setWorkingDir()` and `session.setPreviousDir()`. Handle `cd -` (swap with previousDir) and `cd ~` (go to `/home/user`).
- `LsCommand`: Call `vfs.listDirectory(path, workingDir, showHidden)`. Parse `-a` and `-l` flags. For `-l` format, use `node.getPermission()`, `node.isDirectory()`, and `((RegularFile) node).getSize()`.

**Environment Commands (depend on VfsSerializer):**

- `SaveCommand`: `VfsSerializer.saveToFile(session.getVfs(), session.getWorkingDir(), name)`
- `LoadCommand`: `VfsSerializer.loadFromFile(name)` → `session.replaceVfs()` + `session.setWorkingDir()`
- `ResetCommand`: `session.replaceVfs(new VirtualFileSystem())` + `session.setWorkingDir("/")`
- `EnvListCommand`: `VfsSerializer.listEnvironments()`
- `EnvDeleteCommand`: `VfsSerializer.deleteEnvironment(name)`

**VfsSerializer (the biggest piece):**

- `serialize()`: Walk `vfs.getRoot()` depth-first, emit `TYPE | PATH | PERMS [| CONTENT]` lines
- `deserialize()`: Parse lines, rebuild VFS tree with `new Directory()` / `new RegularFile()` / `new Permission()`
- `saveToFile()` / `loadFromFile()` / `listEnvironments()` / `deleteEnvironment()`: Thin wrappers around `Storage.*` calls
- `escapeContent()` / `unescapeContent()`: Handle `\n`, `\|`, `\\` escaping

### Dependencies on Other Members

- `VfsSerializer` is used by `SaveCommand`, `LoadCommand`, `EnvListCommand`, `EnvDeleteCommand` (all yours).
- Also used by `MainParser.handleExec()` (infra) for the `exec -e` feature — implement `loadFromFile()` first.
- Does **not** depend on A's parser or D's exam module.

---

## Member C — File Operations & Text Processing

### Scope

| File | What to Implement |
| ------ | ------------------- |
| `CatCommand.java` | Display file contents (supports stdin) |
| `RmCommand.java` | Remove files/directories (`-r`, `-f` flags) |
| `CpCommand.java` | Copy files/directories (`-r` flag) |
| `MvCommand.java` | Move or rename files |
| `HeadCommand.java` | First N lines (`-n` flag, default 10, supports stdin) |
| `TailCommand.java` | Last N lines (`-n` flag, default 10, supports stdin) |
| `GrepCommand.java` | Pattern search (`-i`, `-v`, `-n` flags, supports stdin) |
| `FindCommand.java` | Find files by name (`-name` flag) |
| `ChmodCommand.java` | Change permissions (octal or symbolic) |
| `WcCommand.java` | Count lines/words/chars (`-l`, `-w`, `-c` flags, supports stdin) |
| `SortCommand.java` | Sort lines (`-r` flag, supports stdin) |
| `UniqCommand.java` | Remove adjacent duplicates (`-c` flag, supports stdin) |

### What's Provided

- **`Command` interface**: Same as B. Every stub has full inline TODOs.
- **4 reference commands**: Same as B. `MkdirCommand` is the best reference for flag parsing + VFS error handling.
- **`VirtualFileSystem`**: `readFile()`, `writeFile()`, `delete()`, `copy()`, `move()`, `findByName()`, `resolve()`, `listDirectory()`.
- **`Permission`**: `fromOctal("755")`, `fromSymbolic("rwxr-xr-x")`, `canOwnerRead()`, `canOwnerWrite()`, `canOwnerExecute()`.

### Key Patterns

**File argument vs stdin:** 7 of your 12 commands accept both file arguments and piped stdin (`cat`, `head`, `tail`, `grep`, `sort`, `uniq`, `wc`). Follow this pattern:

```java
if (fileArgs.length > 0) {
    content = vfs.readFile(fileArgs[0], workingDir);
} else if (stdin != null) {
    content = stdin;
} else {
    return CommandResult.error("cmdName: missing operand");
}
```

**Text processing:** Work on `String` content. Split by `\n` for line-based operations. Return the result via `CommandResult.success(output)`.

**Flag parsing:** Iterate `args[]`, extract flags (e.g. `-n`, `-r`, `-i`, `-v`), collect remaining args as file paths. See `MkdirCommand` for the pattern.

### Dependencies on Other Members

- Does **not** depend on A, B, or D. All VFS APIs are infrastructure.
- Your commands will be exercised by A's execution engine, but you can unit-test them independently by constructing a `ShellSession` with a test VFS.

---

## Member D — Exam Module

### Scope

| File | What to Implement |
| ------ | ------------------- |
| `McqQuestion.java` | `present()`, `checkAnswer()` |
| `FitbQuestion.java` | `present()`, `checkAnswer()` |
| `PracQuestion.java` | `present()`, `checkVfs()` |
| `QuestionBank.java` | `load()`, `getRandomQuestion()`, `getQuestions(topic, count, random)` |
| `ExamSession.java` | `startInteractive()`, `startWithArgs()`, `runOneRandom()`, `listTopics()`, `runExam()`, `presentQuestion()`, `handlePracQuestion()` |
| `QuestionParser.java` | `parseFile()`, `parseMcq()`, `parseFitb()`, `parsePrac()`, `parseDifficulty()`, `getTopicName()` |
| `ResourceExtractor.java` | `extractIfNeeded()`, `extractResource()` |

### What's Provided

- **`Question` (abstract base)**: Constructor, `formatHeader()`, all getters. You implement `present()` and `checkAnswer()` in each subclass.
- **`Checkpoint`**: Fully implemented — `matches(VirtualFileSystem)` checks a path+type. Used by `PracQuestion.checkVfs()`.
- **`ExamResult`**: Fully implemented — `addResult()`, `display()`, score calculation. Just call it from `ExamSession.runExam()`.
- **`Storage`**: `readLines(Path)` for reading question bank files, `ensureDirectory(Path)` for creating directories.
- **`Ui`**: `readLine()`, `println()`, `printError()` for exam I/O.
- **`ShellSession` + `VirtualFileSystem`**: For PRAC questions — create a temp session, let user type commands, then check VFS.
- **Question bank data files** (5 `.txt` files in `data/questions/` and `src/main/resources/questions/`):
  - `navigation.txt`, `file-management.txt`, `text-processing.txt`, `permissions.txt`, `piping-redirection.txt`

### Key Tasks

**Question Types:**

- `McqQuestion.present()`: Display header + question text + lettered options (A/B/C/D)
- `McqQuestion.checkAnswer()`: Compare first char of user input to `correctAnswer`
- `FitbQuestion.present()`: Display header + question text
- `FitbQuestion.checkAnswer()`: Check if `answer.trim()` is in `acceptedAnswers` list
- `PracQuestion.present()`: Display header + question text
- `PracQuestion.checkVfs()`: Iterate `checkpoints`, call `checkpoint.matches(vfs)` for each

**QuestionParser:** Parse `.txt` files line by line. Format: `TYPE | DIFFICULTY | TEXT | ANSWER | OPTIONS | EXPLANATION`. Split by `" | "` with limit 6. Dispatch to `parseMcq()`, `parseFitb()`, or `parsePrac()` based on type.

**QuestionBank:** Load all `.txt` files from `data/questions/` using `Storage.listFiles(dir, ".txt")` + `QuestionParser.parseFile()`. Organize into `Map<String, List<Question>>`.

**ExamSession:** The orchestrator with 3 entry points:

- `startInteractive()` — prompt for topic + count, call `runExam()`, display `ExamResult`
- `startWithArgs(topic, count, random)` — direct mode from CLI args
- `runOneRandom()` — one question from any topic
- For PRAC questions: `handlePracQuestion()` creates a temp `ShellSession` with `vfsFactory.get()`, lets user type commands until "done", then calls `q.checkVfs(tempVfs)`

**ResourceExtractor:** Extract bundled `.txt` files from JAR to `data/questions/` on first run using `Class.getResourceAsStream()`.

### Dependencies on Other Members

- `handlePracQuestion()` creates a `ShellSession` + calls `start()` — depends on **A's** `ShellSession.start()`. If A hasn't finished, you can temporarily stub this with a manual VFS manipulation for testing.
- PRAC questions also depend on **B & C's commands** to actually work. You can test VFS verification independently using `vfs.createDirectory()` / `vfs.createFile()`.
- `QuestionParser` and `ResourceExtractor` are **self-contained** — implement these first.

---

## Quick Reference: Who Owns What

| Package | File | Owner | Status |
|---------|------|-------|--------|
| `cli` | `Ui.java` | Infra | ✅ Complete |
| `cli` | `MainParser.java` | Infra | ✅ Complete |
| — | `LinuxLingo.java` | Infra | ✅ Complete |
| `shell` | `ShellParser.java` | **A** | 🔲 Stub |
| `shell` | `ShellSession.java` | **A** | 🔲 Stub |
| `shell` | `CommandRegistry.java` | Infra | ✅ Complete |
| `shell` | `CommandResult.java` | Infra | ✅ Complete |
| `shell.command` | `Command.java` | Infra | ✅ Complete |
| `shell.command` | `EchoCommand.java` | Infra | ✅ Reference |
| `shell.command` | `MkdirCommand.java` | Infra | ✅ Reference |
| `shell.command` | `PwdCommand.java` | Infra | ✅ Reference |
| `shell.command` | `TouchCommand.java` | Infra | ✅ Reference |
| `shell.command` | `CdCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `LsCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `ClearCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `HelpCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `SaveCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `LoadCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `ResetCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `EnvListCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `EnvDeleteCommand.java` | **B** | 🔲 Stub |
| `shell.command` | `CatCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `RmCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `CpCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `MvCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `HeadCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `TailCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `GrepCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `FindCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `ChmodCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `WcCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `SortCommand.java` | **C** | 🔲 Stub |
| `shell.command` | `UniqCommand.java` | **C** | 🔲 Stub |
| `shell.vfs` | `VirtualFileSystem.java` | Infra | ✅ Complete |
| `shell.vfs` | `FileNode.java` | Infra | ✅ Complete |
| `shell.vfs` | `Directory.java` | Infra | ✅ Complete |
| `shell.vfs` | `RegularFile.java` | Infra | ✅ Complete |
| `shell.vfs` | `Permission.java` | Infra | ✅ Complete |
| `shell.vfs` | `VfsException.java` | Infra | ✅ Complete |
| `exam` | `ExamSession.java` | **D** | 🔲 Stub |
| `exam` | `QuestionBank.java` | **D** | 🔲 Stub |
| `exam` | `ExamResult.java` | Infra | ✅ Complete |
| `exam` | `Checkpoint.java` | Infra | ✅ Complete |
| `exam.question` | `Question.java` | Infra | ✅ Complete |
| `exam.question` | `McqQuestion.java` | **D** | 🔲 Stub |
| `exam.question` | `FitbQuestion.java` | **D** | 🔲 Stub |
| `exam.question` | `PracQuestion.java` | **D** | 🔲 Stub |
| `storage` | `Storage.java` | Infra | ✅ Complete |
| `storage` | `StorageException.java` | Infra | ✅ Complete |
| `storage` | `VfsSerializer.java` | **B** | 🔲 Stub |
| `storage` | `QuestionParser.java` | **D** | 🔲 Stub |
| `storage` | `ResourceExtractor.java` | **D** | 🔲 Stub |

**Summary:** 19 infrastructure files (complete) · 4 reference implementations · 27 stubs (A: 2, B: 10, C: 12, D: 7)
