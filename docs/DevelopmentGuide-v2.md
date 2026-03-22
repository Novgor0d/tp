# LinuxLingo v2.0 — Development Guide

This document describes what each team member is responsible for in v2.0, what new
infrastructure is available, and how to implement each stub. Every stub file contains
detailed inline TODOs — this guide gives the big picture so you can start coding without
coordination overhead.

> **Pre‑requisite:** read `docs/DevelopmentGuide.md` (the v1.0 guide) first. It covers
> the shared infrastructure (VFS, `CommandResult`, `Command` interface, `Ui`, `Storage`,
> reference command implementations). This document only covers **v2.0 additions**.

---

## What Changed from v1.0 to v2.0

| Area | v1.0 | v2.0 Addition |
|------|------|---------------|
| Shell parser | `\|`, `>`, `>>`, `&&`, `;` | `\|\|` (OR operator), `<` (input redirect) |
| Shell session | Basic REPL + plan execution | Alias resolution, command history, glob expansion, "Did you mean?" suggestions, JLine integration |
| Commands | 24 commands | +7 new commands, +13 command enhancements |
| Checkpoint | `DIR`, `FILE` | `NOT_EXISTS`, `CONTENT_EQUALS`, `PERM` |
| PracQuestion | Basic VFS check | Setup items (MKDIR/FILE/PERM) applied before user interaction |
| QuestionParser | Parses MCQ/FITB/PRAC | Handles setup items and new checkpoint types |
| Tab completion | — | JLine `ShellCompleter` for commands and paths |
| Line reader | `Ui.readLine()` | JLine `ShellLineReader` with history |

---

## Architecture Overview (v2.0 additions highlighted)

```
linuxlingo/
├── LinuxLingo.java                  (infra)
├── cli/
│   ├── Ui.java                      (infra)
│   └── MainParser.java              (infra)
├── shell/
│   ├── ShellParser.java             (infra — v2.0 tokens already implemented)
│   ├── ShellSession.java            (Owner A + Owner B — split by method)
│   ├── ShellCompleter.java          ★ NEW (Owner B — stub)
│   ├── ShellLineReader.java         ★ NEW (Owner B — stub)
│   ├── CommandRegistry.java         (infra — v2.0 commands pre-registered)
│   ├── CommandResult.java           (infra)
│   ├── command/
│   │   ├── Command.java             (infra)
│   │   ├── AliasCommand.java        ★ NEW (Owner A — stub)
│   │   ├── UnaliasCommand.java      ★ NEW (Owner A — stub)
│   │   ├── HistoryCommand.java      ★ NEW (Owner A — stub)
│   │   ├── ManCommand.java          ★ NEW (Owner C — stub)
│   │   ├── TreeCommand.java         ★ NEW (Owner C — stub)
│   │   ├── WhichCommand.java        ★ NEW (Owner C — stub)
│   │   ├── WhoamiCommand.java       ★ NEW (Owner C — stub)
│   │   ├── DateCommand.java         ★ NEW (Owner C — stub)
│   │   ├── TeeCommand.java          ★ NEW (Owner C — stub)
│   │   ├── DiffCommand.java         ★ NEW (Owner C — stub)
│   │   ├── CatCommand.java          (Owner C — v2.0 enhancement: -n)
│   │   ├── EchoCommand.java         (Owner C — v2.0 enhancement: -n)
│   │   ├── GrepCommand.java         (Owner C — v2.0 enhancement: -E)
│   │   ├── FindCommand.java         (Owner C — v2.0 enhancement: -type/-size)
│   │   ├── HeadCommand.java         (Owner C — v2.0 enhancement: multi-file)
│   │   ├── TailCommand.java         (Owner C — v2.0 enhancement: multi-file)
│   │   ├── WcCommand.java           (Owner C — v2.0 enhancement: multi-file)
│   │   ├── SortCommand.java         (Owner C — v2.0 enhancement: -u)
│   │   ├── UniqCommand.java         (Owner C — v2.0 enhancement: -d)
│   │   ├── ChmodCommand.java        (Owner C — v2.0 enhancement: -R)
│   │   ├── LsCommand.java           (Owner B — v2.0 enhancement: -R)
│   │   ├── MkdirCommand.java        (Owner B — v2.0 enhancement: multi-dir)
│   │   ├── TouchCommand.java        (Owner B — v2.0 enhancement: multi-file)
│   │   └── [9 v1.0 commands — infra, unchanged]
│   └── vfs/                         (infra — unchanged)
├── exam/
│   ├── Checkpoint.java              (infra — v2.0 types already implemented)
│   ├── ExamResult.java              (infra)
│   ├── ExamSession.java             (infra)
│   └── question/
│       ├── Question.java            (infra)
│       └── PracQuestion.java        (Owner D — v2.0 stub: applySetup)
└── storage/
    ├── QuestionParser.java          (Owner D — v2.0 stub: new types + setup)
    └── [Storage.java, etc. — infra]
```

---

## New Infrastructure (ready to use)

These components are **fully implemented**. Do **not** modify them.

### ShellParser — New Token Types

The tokenizer already recognises two new operators:

| Token | `TokenType` | Meaning |
|-------|-------------|---------|
| `\|\|` | `OR` | Execute right side only if left side fails (exit code ≠ 0) |
| `<` | `INPUT_REDIRECT` | Read stdin from a file |

The `Segment` record now has an `inputRedirect` field (nullable `String`):

```java
Segment seg = plan.segments.get(i);
seg.inputRedirect   // e.g. "input.txt" or null
seg.redirect         // output redirect (from v1.0)
seg.commandName
seg.args
```

### Checkpoint — New Verification Types

| `NodeType` | `matches(vfs)` Logic | Example Checkpoint String |
|------------|----------------------|--------------------------|
| `DIR` | Path exists and is a directory | `DIR:/home/user/docs` |
| `FILE` | Path exists and is a file | `FILE:/home/user/file.txt` |
| `NOT_EXISTS` | Path does **not** exist | `NOT_EXISTS:/tmp/old` |
| `CONTENT_EQUALS` | File content equals expected value | `CONTENT_EQUALS:/f.txt=hello world` |
| `PERM` | Permission string matches | `PERM:/f.txt=rwxr-xr-x` |

### PracQuestion — Setup Items

The `PracQuestion.SetupItem` inner class is defined:

```java
public static class SetupItem {
    public enum Type { MKDIR, FILE, PERM }
    private final Type type;
    private final String path;
    private final String value; // content for FILE, permission for PERM, null for MKDIR
}
```

### ShellSession — New Fields

These getters/setters are already implemented:

| Field | Getter | Setter | Purpose |
|-------|--------|--------|---------|
| `aliases` | `getAliases()` → `Map<String, String>` | — | Command alias map (e.g. `ll` → `ls -la`) |
| `commandHistory` | `getCommandHistory()` → `List<String>` | — | Ordered command history |
| `lineReader` | `getLineReader()` | `setLineReader()` | Optional JLine reader |

### CommandRegistry — `getAllNames()`

Returns a `SortedSet<String>` of all registered command names. Use this for:
- `suggestCommand()` — iterate names to find closest match
- `ShellCompleter` — provide command name completions

---

## Task Distribution

### Zero-Coupling Design

Each owner works on **separate files** with **no shared dependencies** between owners.
The only file touched by two owners is `ShellSession.java`, but Owner A and Owner B
edit completely different methods in different regions of the file — git will auto-merge.

```
Owner A: methods runPlan() (lines 270–300), start() (line 143)
Owner B: methods startInteractive() (line 159), suggestCommand() (line 446),
         editDistance() (line 461), expandGlobs() (line 479), expandSingleGlob() (line 495)
```

No owner depends on another owner's stub. All stubs depend only on
infrastructure code.

---

## Owner A — Shell Session Core (~30%)

### Files to Edit

| File | What to Implement |
|------|-------------------|
| `ShellSession.java` | OR operator logic, `<` input redirect, alias resolution, history tracking |
| `AliasCommand.java` | `execute()` — list/set/remove aliases |
| `UnaliasCommand.java` | `execute()` — remove one or more aliases |
| `HistoryCommand.java` | `execute()` — show/clear/limit history |

### Implementation Guide

**ShellSession.runPlan() — OR operator** (around line 276):
```java
// When operator is OR:
// if (lastExitCode != 0) → execute next segment
// if (lastExitCode == 0) → skip next segment
```

**ShellSession.runPlan() — Input redirect** (around line 292):
```java
// If segment.inputRedirect != null:
//   stdin = vfs.readFile(segment.inputRedirect, workingDir);
// This overrides any piped stdin for that segment.
```

**ShellSession.runPlan() — Alias resolution** (around line 297):
```java
// Before looking up the command in the registry:
//   String resolvedName = segment.commandName;
//   while (aliases.containsKey(resolvedName)) {
//       resolvedName = aliases.get(resolvedName);
//   }
// Then use resolvedName for the registry lookup.
```

**ShellSession.start() — History tracking** (around line 143):
```java
// After reading input and before executePlan():
//   commandHistory.add(trimmed);
```

**AliasCommand.execute():**
- `alias` (no args) → list all aliases as `name='value'`
- `alias name='value'` → set alias
- Parse `name=value` or `name='value'` format

**UnaliasCommand.execute():**
- `unalias name [name2...]` → remove each alias from `session.getAliases()`
- Error if name not found

**HistoryCommand.execute():**
- `history` → print numbered history from `session.getCommandHistory()`
- `history -c` → clear history
- `history N` → show last N entries

### Testing

Your v2.0 tests are in:
- `ShellSessionV2Test.java` — `@Disabled` tests for `AliasResolution`, `OrOperator`, `InputRedirect`
- `HistoryCommandTest.java` — `@Disabled` tests for `BasicHistory`, `HistoryLimit`, `HistoryClear`

**Workflow:** implement the feature → remove `@Disabled` from the corresponding test class → run `./gradlew test`.

---

## Owner B — JLine Integration & Algorithms (~30%)

### Files to Edit

| File | What to Implement |
|------|-------------------|
| `ShellSession.java` | `startInteractive()`, `suggestCommand()`, `editDistance()`, `expandGlobs()`, `expandSingleGlob()` |
| `ShellCompleter.java` | `complete()`, `completeCommandName()`, `completePath()`, `getCommandCompletions()`, `getPathCompletions()` |
| `ShellLineReader.java` | `create()`, `readLine()`, `getHistory()`, `getHistorySize()`, `addToHistory()`, `close()` |
| `LsCommand.java` | `-R` recursive listing enhancement |
| `MkdirCommand.java` | Multi-directory creation enhancement |
| `TouchCommand.java` | Multi-file creation enhancement |

### Implementation Guide

**ShellSession.startInteractive():**
```java
// 1. ShellLineReader reader = ShellLineReader.create(this);
// 2. setLineReader(reader);
// 3. try { start(); } finally { reader.close(); setLineReader(null); }
// If ShellLineReader.create() throws → fall back to plain start().
```

**ShellSession.start() — lineReader integration** (around line 121):
```java
// If getLineReader() != null:
//   input = getLineReader().readLine(getPrompt());
// Else:
//   input = ui.readLine(getPrompt());  // existing v1.0 code
```

**suggestCommand(String typo):**
```java
// Iterate registry.getAllNames(), compute editDistance(typo, name).
// Return "Did you mean 'X'?" for the closest match if distance ≤ 3.
```

**editDistance(String a, String b):**
```java
// Classic Levenshtein DP:
// dp[i][j] = min(dp[i-1][j]+1, dp[i][j-1]+1, dp[i-1][j-1] + (a[i]==b[j] ? 0 : 1))
```

**expandGlobs(String[] args):**
```java
// For each arg containing '*' or '?':
//   matches = expandSingleGlob(arg);
//   if matches is empty → keep the original arg
//   else → add all matches
```

**expandSingleGlob(String pattern):**
```java
// Split pattern into directory prefix + file glob.
// Use vfs.findByName(prefix, workingDir, glob) for matching.
```

**ShellCompleter.complete():**
```java
// Implements JLine's Completer interface.
// If cursor is at position 0 (first word) → completeCommandName()
// Else → completePath()
```

**ShellLineReader.create(ShellSession session):**
```java
// Terminal terminal = TerminalBuilder.builder().system(true).build();
// LineReader reader = LineReaderBuilder.builder()
//     .terminal(terminal)
//     .completer(new ShellCompleter(session))
//     .build();
// Return new ShellLineReader wrapping terminal + reader.
```

### Testing

Your v2.0 tests are in:
- `ShellSessionV2Test.java` — `@Disabled` tests for `GlobExpansion`, `DidYouMeanSuggestion`; `EditDistance` tests (partially enabled)
- `ShellCompleterTest.java` — `@Disabled` test classes for `CommandCompletion`, `PathCompletion`, `EmptyInput`
- `ShellLineReaderTest.java` — `@Disabled` tests for `AddToHistory`, `HistoryOrder`, `CloseReleasesResources`

**Workflow:** implement → remove `@Disabled` → `./gradlew test`.

---

## Owner C — New Commands & Enhancements (~25%)

### Files to Edit

**7 new command stubs** (all return `CommandResult.error("not yet implemented")`):

| Command | File | Synopsis |
|---------|------|----------|
| `man` | `ManCommand.java` | `man <command>` — display manual page |
| `tree` | `TreeCommand.java` | `tree [path]` — display directory tree |
| `which` | `WhichCommand.java` | `which <command>` — show if command exists |
| `whoami` | `WhoamiCommand.java` | `whoami` — print current username |
| `date` | `DateCommand.java` | `date [+format]` — display date/time |
| `tee` | `TeeCommand.java` | `tee [-a] <file>` — write stdin to file and stdout |
| `diff` | `DiffCommand.java` | `diff <file1> <file2>` — compare two files |

**13 command enhancements** (v1.0 logic preserved, add v2.0 features):

| Command | Enhancement | Key Change |
|---------|-------------|------------|
| `cat` | `-n` line numbering | Prepend `"%6d\t%s"` per line |
| `echo` | `-n` no trailing newline | Suppress `\n` at end |
| `grep` | `-E` extended regex | Use `Pattern.compile()` with regex |
| `find` | `-type f/d`, `-size +N/-N` | Filter by type and size |
| `head` | Multi-file support | Print `==> file <==` headers |
| `tail` | Multi-file support | Print `==> file <==` headers |
| `wc` | Multi-file + total line | Sum totals across files |
| `sort` | `-u` unique | Remove duplicates after sort |
| `uniq` | `-d` duplicates only | Print only duplicate lines |
| `chmod` | `-R` recursive | Walk directory tree |
| `ls` | `-R` recursive | List subdirectories recursively |
| `mkdir` | Multi-dir creation | Create multiple directories at once |
| `touch` | Multi-file creation | Create/touch multiple files at once |

### Stub Pattern for New Commands

Each new command stub looks like:

```java
public CommandResult execute(ShellSession session, String[] args, String stdin) {
    // [v2.0 STUB] TODO: Implement <command>.
    // <detailed instructions in comments>
    return CommandResult.error("not yet implemented");
}
```

Replace the stub body with your implementation. Keep `getUsage()` and `getDescription()`
as-is (already implemented).

### Stub Pattern for Enhanced Commands

Each enhanced command has v1.0 code marked with `// ===== v1.0 implementation =====`.
The v2.0 TODO is at the top of `execute()`:

```java
// TODO [v2.0]: Parse -X flag from args to enable <feature>.
//  - <step-by-step instructions>

// ===== v1.0 implementation =====
// ... existing working code ...
```

Add your flag parsing **above** the v1.0 code, then weave the new behaviour
into the existing logic. Do **not** delete the v1.0 code — extend it.

### Testing

Your v2.0 tests are in:
- `NewCommandsV2Test.java` — stub verification tests (pass now) + `@Disabled` expected-behaviour tests
- `CommandEnhancementV2Test.java` — entire class `@Disabled`

**Workflow:** implement → remove `@Disabled` → `./gradlew test`.

---

## Owner D — Question Parser & PracQuestion Setup (~15%)

### Files to Edit

| File | What to Implement |
|------|-------------------|
| `QuestionParser.java` | `findTypeColon()`, `parseSetupItem()`, new checkpoint type parsing in `parseCheckpoint()` |
| `PracQuestion.java` | `applySetup()` |

### Implementation Guide

**QuestionParser.parseCheckpoint()** — currently handles `DIR` and `FILE` only:
```java
// TODO: Handle NOT_EXISTS, CONTENT_EQUALS=value, PERM=value
// Use findTypeColon() to correctly locate the colon separator
// (important: paths may contain colons in the value part).
//
// NOT_EXISTS:/path     → new Checkpoint(path, NodeType.NOT_EXISTS)
// CONTENT_EQUALS:/path=value → new Checkpoint(path, NodeType.CONTENT_EQUALS, value)
// PERM:/path=rwxr-xr-x → new Checkpoint(path, NodeType.PERM, "rwxr-xr-x")
```

**QuestionParser.findTypeColon(String checkpoint):**
```java
// Locate the colon that separates TYPE from PATH.
// Must skip colons inside the path (e.g. "CONTENT_EQUALS:/home/user:file=data").
// Strategy: find the first colon after the type prefix.
```

**QuestionParser.parseSetupItem(String item):**
```java
// Parse "MKDIR:/path", "FILE:/path=content", "PERM:/path=rwxr-xr-x"
// Return new PracQuestion.SetupItem(type, path, value)
```

**QuestionParser.parsePrac() — setup items** (in the options field):
```java
// The options field contains semicolon-separated setup items.
// Split by ";" and call parseSetupItem() on each part.
// Collect into a List<SetupItem> and pass to PracQuestion constructor.
```

**PracQuestion.applySetup(VirtualFileSystem vfs):**
```java
// For each SetupItem:
//   MKDIR → vfs.createDirectory(path, "/", true)
//   FILE  → vfs.createFile(path, "/"); if value != null → vfs.writeFile(path, "/", value, false)
//   PERM  → vfs.resolve(path, "/").setPermission(Permission.fromSymbolic(value))
```

### Testing

Your v2.0 tests are in:
- `QuestionParserV2Test.java` — `@Disabled` tests for new checkpoint types and setup items
- `PracQuestionV2Test.java` — `@Disabled` tests for `applySetup()`

**Workflow:** implement → remove `@Disabled` → `./gradlew test`.

---

## Ownership Map — Complete Reference

| Package | File | Owner | v2.0 Status |
|---------|------|-------|-------------|
| `shell` | `ShellSession.java` | **A + B** | 🔲 Stubs (split by method) |
| `shell` | `ShellCompleter.java` | **B** | 🔲 New file — all stub |
| `shell` | `ShellLineReader.java` | **B** | 🔲 New file — all stub |
| `shell.command` | `AliasCommand.java` | **A** | 🔲 New command stub |
| `shell.command` | `UnaliasCommand.java` | **A** | 🔲 New command stub |
| `shell.command` | `HistoryCommand.java` | **A** | 🔲 New command stub |
| `shell.command` | `ManCommand.java` | **C** | 🔲 New command stub |
| `shell.command` | `TreeCommand.java` | **C** | 🔲 New command stub |
| `shell.command` | `WhichCommand.java` | **C** | 🔲 New command stub |
| `shell.command` | `WhoamiCommand.java` | **C** | 🔲 New command stub |
| `shell.command` | `DateCommand.java` | **C** | 🔲 New command stub |
| `shell.command` | `TeeCommand.java` | **C** | 🔲 New command stub |
| `shell.command` | `DiffCommand.java` | **C** | 🔲 New command stub |
| `shell.command` | `CatCommand.java` | **C** | 🔲 Enhancement (-n) |
| `shell.command` | `EchoCommand.java` | **C** | 🔲 Enhancement (-n) |
| `shell.command` | `GrepCommand.java` | **C** | 🔲 Enhancement (-E) |
| `shell.command` | `FindCommand.java` | **C** | 🔲 Enhancement (-type/-size) |
| `shell.command` | `HeadCommand.java` | **C** | 🔲 Enhancement (multi-file) |
| `shell.command` | `TailCommand.java` | **C** | 🔲 Enhancement (multi-file) |
| `shell.command` | `WcCommand.java` | **C** | 🔲 Enhancement (multi-file) |
| `shell.command` | `SortCommand.java` | **C** | 🔲 Enhancement (-u) |
| `shell.command` | `UniqCommand.java` | **C** | 🔲 Enhancement (-d) |
| `shell.command` | `ChmodCommand.java` | **C** | 🔲 Enhancement (-R) |
| `shell.command` | `LsCommand.java` | **B** | 🔲 Enhancement (-R) |
| `shell.command` | `MkdirCommand.java` | **B** | 🔲 Enhancement (multi-dir) |
| `shell.command` | `TouchCommand.java` | **B** | 🔲 Enhancement (multi-file) |
| `storage` | `QuestionParser.java` | **D** | 🔲 Enhancement (new types + setup) |
| `exam.question` | `PracQuestion.java` | **D** | 🔲 Enhancement (applySetup) |

**Summary:** Owner A: 4 files · Owner B: 6 files · Owner C: 20 files · Owner D: 2 files

---

## Test Files

| Test File | Covers | Status |
|-----------|--------|--------|
| `ShellSessionV2Test.java` | Alias, OR, InputRedirect, Glob, DidYouMean, EditDistance | Mostly `@Disabled` |
| `ShellCompleterTest.java` | Command/path tab completion | Mostly `@Disabled` |
| `ShellLineReaderTest.java` | JLine reader, history | Partially `@Disabled` |
| `HistoryCommandTest.java` | History command variants | Partially `@Disabled` |
| `NewCommandsV2Test.java` | 7 new commands | Stub tests pass; behaviour tests `@Disabled` |
| `CommandEnhancementV2Test.java` | 13 enhanced commands | Entire class `@Disabled` |
| `QuestionParserV2Test.java` | New checkpoint types + setup items | `@Disabled` |
| `PracQuestionV2Test.java` | `applySetup()` | `@Disabled` |
| `CheckpointV2Test.java` | NOT_EXISTS, CONTENT_EQUALS, PERM | ✅ All pass (infra) |
| `FindCommandTest.java` | v1.0 find tests | ✅ All pass |

**Current test status:** 320 total, 260 passed, 60 skipped (`@Disabled`), 0 failed.

After implementing all v2.0 features, **all 320 tests should pass with 0 skipped**.

---

## Development Workflow

### 1. Find Your TODOs

Search for your owner label:
```bash
grep -rn "Owner A\|Owner: A\|Member A" src/main/java/
```

### 2. Implement One Stub at a Time

Each inline TODO describes exactly what to implement. Follow the steps in order.

### 3. Enable and Run Tests

After implementing a feature, remove `@Disabled` from its test class:

```java
// Before:
@Disabled("v2.0 stub — enable after implementing alias resolution")
@Nested class AliasResolution { ... }

// After:
@Nested class AliasResolution { ... }
```

Then run:
```bash
./gradlew test
```

### 4. Check Code Style

```bash
./gradlew checkstyleMain checkstyleTest
```

The project uses [SE-EDU checkstyle rules](https://se-education.org/guides/conventions/java/basic.html).
Key rules: 120-char line limit, no star imports, Javadoc on public methods, braces required.

### 5. Commit

Commit only **your** files. Example:
```bash
git add src/main/java/linuxlingo/shell/command/AliasCommand.java
git commit -m "Implement alias command"
```

---

## Build & Run

```bash
# Build
./gradlew build

# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "linuxlingo.shell.ShellSessionV2Test"

# Run checkstyle
./gradlew checkstyleMain checkstyleTest

# Build shadow JAR
./gradlew shadowJar
java -jar build/libs/LinuxLingo.jar
```

---

## FAQ

**Q: Can I modify infrastructure files?**
A: No. Files marked "infra" in the ownership map are shared and should not be changed.

**Q: What if I need a method that another owner is responsible for?**
A: You don't. The stubs are designed so each owner's code is self-contained. If you hit
a missing dependency, check that you're using the correct infrastructure API.

**Q: How do I test commands without the parser being implemented?**
A: Construct a `ShellSession` directly and call `command.execute(session, args, stdin)`.
All v1.0 command tests do this — look at any existing test for the pattern.

**Q: How do I test parser features without all commands?**
A: Use the 4 reference commands (`echo`, `pwd`, `mkdir`, `touch`) that are already
fully implemented.

**Q: What does the `@Disabled` annotation mean in tests?**
A: It marks tests for features that are not yet implemented. Remove it after you
implement the feature. If a test still fails after removal, your implementation
has a bug — do not re-add `@Disabled`.

**Q: Will there be git merge conflicts on ShellSession.java?**
A: Unlikely. Owner A edits `runPlan()` and `start()` (lines 143, 270–300). Owner B
edits `startInteractive()`, `suggestCommand()`, `editDistance()`, `expandGlobs()`,
`expandSingleGlob()` (lines 159, 446–499). These are in different regions and git
can auto-merge them.
