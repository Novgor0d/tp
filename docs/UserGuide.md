# LinuxLingo User Guide

LinuxLingo is a **command-line application for learning Linux commands** through an interactive shell simulator and a built-in quiz system. It runs entirely in your terminal and uses a virtual file system (VFS), so you can practice Linux commands safely without affecting your real files.

If you are a Computer Science student looking to build confidence with the Linux command line, LinuxLingo lets you learn by doing — type real commands, see real output, and test your knowledge with quizzes.

- [Quick Start](#quick-start)
- [Modes of Operation](#modes-of-operation)
- [Main Menu Commands](#main-menu-commands)
  - [Entering the Shell Simulator: `shell`](#entering-the-shell-simulator-shell)
  - [Starting an Exam: `exam`](#starting-an-exam-exam)
  - [One-Shot Command Execution: `exec`](#one-shot-command-execution-exec)
  - [Viewing Help: `help`](#viewing-help-help)
  - [Exiting the Application: `exit`](#exiting-the-application-exit)
- [Shell Simulator](#shell-simulator)
  - [Notes About Shell Input](#notes-about-shell-input)
  - [Navigation Commands](#navigation-commands)
  - [File Operation Commands](#file-operation-commands)
  - [Text Processing Commands](#text-processing-commands)
  - [Permission Commands](#permission-commands)
  - [Environment Management Commands](#environment-management-commands)
  - [Utility Commands](#utility-commands)
  - [Piping and Redirection](#piping-and-redirection)
  - [Command Chaining](#command-chaining)
- [Exam System](#exam-system)
  - [Interactive Exam](#interactive-exam)
  - [Exam with CLI Arguments](#exam-with-cli-arguments)
  - [Random Question](#random-question)
  - [Listing Topics](#listing-topics)
  - [Question Types](#question-types)
- [Data Storage](#data-storage)
- [FAQ](#faq)
- [Known Issues](#known-issues)
- [Command Summary](#command-summary)

---

## Quick Start

1. Ensure you have **Java 17** or above installed.
2. Download the latest `LinuxLingo.jar` from the [LinuxLingo releases page](https://github.com/AY2526S2-CS2113-T10-2/tp/releases).
3. Copy the file to a folder you want to use as the home folder for LinuxLingo.
4. Open a command terminal, `cd` into the folder you put the JAR file in, and run:

   ```sh
   java -jar LinuxLingo.jar
   ```

5. A welcome banner and a `linuxlingo>`prompt should appear. Type `help` and press Enter to see available commands.
6. Some example commands to try:
   - `shell` — enter the Shell Simulator to practice Linux commands.
   - `exam` — start a quiz on Linux topics.
   - `exit` — exit the application.
7. Refer to the sections below for details on each feature.

---

## Modes of Operation

LinuxLingo supports two modes:

| Mode | How to Enter | Description |
| ---- | ----------- | ----------- |
| **Interactive mode** | Run `java -jar LinuxLingo.jar` (no arguments) | Starts the main REPL. You can freely switch between shell, exam, and other commands. |
| **One-shot mode** | Run with command-line arguments (e.g., `java -jar LinuxLingo.jar exec "ls"`) | Executes a single command and exits. Useful for scripting or quick checks. |

One-shot mode supports the following arguments:

| Arguments | Description |
| --------- | ----------- |
| `shell` | Enter the Shell Simulator directly |
| `exec "COMMAND"` | Execute a shell command and print the result |
| `exec -e ENV_NAME "COMMAND"` | Execute a shell command in a saved environment |
| `exam` | Start an interactive exam |
| `exam -t TOPIC` | Start an exam on a specific topic |
| `exam -t TOPIC -n COUNT` | Start an exam with a specific number of questions |
| `exam -t TOPIC -n COUNT -random` | Start an exam with questions in random order |
| `exam -random` | Answer one random question |
| `exam -topics` | List all available exam topics |

---

## Main Menu Commands

When in interactive mode, the `linuxlingo>` prompt accepts the following top-level commands.

### Entering the Shell Simulator: `shell`

Enters the interactive Shell Simulator where you can practice Linux commands on the virtual file system.

Format: `shell`

- You will see a shell prompt like `user@linuxlingo:/$`.
- Type `exit`, `back`, or `done` to return to the main menu.

### Starting an Exam: `exam`

Starts an exam session. See the [Exam System](#exam-system) section for full details.

Format: `exam [-t TOPIC] [-n COUNT] [-random] [-topics]`

Examples:

- `exam` — start an interactive exam (prompts you to choose a topic).
- `exam -t navigation -n 5` — 5 questions from the "navigation" topic.
- `exam -random` — one random question from any topic.
- `exam -topics` — list all available topics.

### One-Shot Command Execution: `exec`

Executes a single shell command and prints the result, without entering the Shell Simulator.

Format: `exec "COMMAND"` or `exec -e ENV_NAME "COMMAND"`

- The command string should be enclosed in quotes.
- Use `-e ENV_NAME` to run the command in a previously saved environment.

Examples:

- `exec "echo hello"` — prints `hello`.
- `exec -e myenv "ls"` — lists files in the saved environment `myenv`.

### Viewing Help: `help`

Displays a list of available main menu commands.

Format: `help`

### Exiting the Application: `exit`

Exits LinuxLingo.

Format: `exit`

Alternatives: `quit`

---

## Shell Simulator

The Shell Simulator provides a Linux-like command-line environment backed by an in-memory virtual file system (VFS). All file and directory operations are performed within this VFS — **no real files on your computer are created, modified, or deleted** by shell commands.

When you enter the Shell Simulator, you will see a prompt like:

```text
user@linuxlingo:/$
```

The prompt shows your current working directory. Type Linux commands and press Enter to execute them.

### Notes About Shell Input

> 💡 **Notes about command syntax:**
>
> - Words in `UPPER_CASE` are parameters to be supplied by the user.
> - Items in square brackets `[...]` are optional.
> - Items with `...` after them can be used multiple times.
> - Flags (e.g., `-r`, `-l`) can appear in any order before file arguments.
> - Both single quotes (`'...'`) and double quotes (`"..."`) can be used to treat text as a single argument (e.g., `echo "hello world"`).

### Navigation Commands

#### Printing the working directory: `pwd`

Prints the absolute path of the current working directory.

Format: `pwd`

Example:

```text
user@linuxlingo:/home/user$ pwd
/home/user
```

#### Changing directory: `cd`

Changes the current working directory.

Format: `cd [DIRECTORY]`

- `cd` or `cd ~` — go to `/home/user`.
- `cd ..` — go to the parent directory.
- `cd -` — go to the previous working directory.
- `cd /absolute/path` — go to an absolute path.
- `cd relative/path` — go to a path relative to the current directory.

Examples:

```text
user@linuxlingo:/$ cd /home/user
user@linuxlingo:/home/user$ cd ..
user@linuxlingo:/home$ cd -
user@linuxlingo:/home/user$
```

#### Listing directory contents: `ls`

Lists the contents of a directory.

Format: `ls [-a] [-l] [DIRECTORY]`

- `-a` — show all files, including hidden files (names starting with `.`).
- `-l` — use long listing format, showing permissions, size, and name.
- If no directory is given, lists the current directory.

Examples:

```text
user@linuxlingo:/$ ls
home/
tmp/

user@linuxlingo:/$ ls -l /home
rwxr-xr-x  0  user/

user@linuxlingo:/$ ls -a
./
../
home/
tmp/
.hidden_file
```

### File Operation Commands

#### Creating directories: `mkdir`

Creates one or more directories.

Format: `mkdir [-p] DIRECTORY [DIRECTORY...]`

- `-p` — create parent directories as needed (no error if they already exist).

Examples:

```text
user@linuxlingo:/$ mkdir projects
user@linuxlingo:/$ mkdir -p projects/java/src
```

#### Creating files: `touch`

Creates an empty file, or updates the timestamp of an existing file.

Format: `touch FILE [FILE...]`

Example:

```text
user@linuxlingo:/$ touch readme.txt
```

#### Displaying file contents: `cat`

Displays the contents of one or more files. Also accepts piped input.

Format: `cat FILE [FILE...]`

- If multiple files are given, their contents are concatenated.
- Supports piped stdin (e.g., `echo "text" | cat`).

Example:

```text
user@linuxlingo:/$ cat readme.txt
Hello, world!
```

#### Outputting text: `echo`

Prints text to the terminal. Often used with redirection to write to files.

Format: `echo [TEXT...]`

Examples:

```text
user@linuxlingo:/$ echo Hello World
Hello World

user@linuxlingo:/$ echo "Hello World" > greeting.txt
```

#### Removing files or directories: `rm`

Removes files or directories.

Format: `rm [-r] [-f] FILE [FILE...]`

- `-r` — remove directories and their contents recursively.
- `-f` — force removal, ignore non-existent files without errors.

Examples:

```text
user@linuxlingo:/$ rm file.txt
user@linuxlingo:/$ rm -r projects/
user@linuxlingo:/$ rm -rf old_dir/
```

#### Copying files or directories: `cp`

Copies files or directories.

Format: `cp [-r] SOURCE DESTINATION`

- `-r` — copy directories recursively.

Examples:

```text
user@linuxlingo:/$ cp file.txt backup.txt
user@linuxlingo:/$ cp -r src/ src_backup/
```

#### Moving or renaming files: `mv`

Moves or renames files and directories.

Format: `mv SOURCE DESTINATION`

Examples:

```text
user@linuxlingo:/$ mv old_name.txt new_name.txt
user@linuxlingo:/$ mv file.txt /home/user/docs/
```

### Text Processing Commands

#### Displaying the first lines: `head`

Displays the first N lines of a file (default: 10). Also accepts piped input.

Format: `head [-n COUNT] [FILE]`

- `-n COUNT` — number of lines to display. A negative value (e.g., `-n -3`) displays all lines except the last 3.

Examples:

```text
user@linuxlingo:/$ head -n 5 logfile.txt
user@linuxlingo:/$ cat longfile.txt | head -n 3
```

#### Displaying the last lines: `tail`

Displays the last N lines of a file (default: 10). Also accepts piped input.

Format: `tail [-n COUNT] [FILE]`

Example:

```text
user@linuxlingo:/$ tail -n 20 logfile.txt
```

#### Searching for patterns: `grep`

Searches for lines matching a pattern in files. Also accepts piped input.

Format: `grep [-i] [-v] [-n] [-c] PATTERN [FILE...]`

- `-i` — case-insensitive matching.
- `-v` — invert match (show lines that do NOT match).
- `-n` — prefix each matching line with its line number.
- `-c` — only print a count of matching lines.

Examples:

```text
user@linuxlingo:/$ grep "error" logfile.txt
user@linuxlingo:/$ grep -in "warning" logfile.txt
3:Warning: disk space low

user@linuxlingo:/$ cat data.txt | grep -v "comment"
```

#### Finding files: `find`

Searches for files by name within a directory tree.

Format: `find [DIRECTORY] -name PATTERN`

- The pattern supports basic glob matching.

Example:

```text
user@linuxlingo:/$ find /home -name "*.txt"
/home/user/readme.txt
/home/user/notes.txt
```

#### Counting lines, words, and characters: `wc`

Counts lines, words, and characters in files. Also accepts piped input.

Format: `wc [-l] [-w] [-c] [FILE...]`

- `-l` — count lines only.
- `-w` — count words only.
- `-c` — count characters only.
- No flags — show all three counts.

Example:

```text
user@linuxlingo:/$ wc readme.txt
5 20 120 readme.txt

user@linuxlingo:/$ echo "hello world" | wc -w
2
```

#### Sorting lines: `sort`

Sorts lines of text in a file. Also accepts piped input.

Format: `sort [-r] [-n] [FILE]`

- `-r` — sort in reverse order.
- `-n` — sort numerically.

Example:

```text
user@linuxlingo:/$ sort names.txt
user@linuxlingo:/$ cat numbers.txt | sort -rn
```

#### Removing adjacent duplicates: `uniq`

Removes adjacent duplicate lines from input. Also accepts piped input.

Format: `uniq [-c] [FILE]`

- `-c` — prefix each line with the number of occurrences.

> 💡 **Tip:** `uniq` only removes *adjacent* duplicates. Use `sort | uniq` to remove all duplicates.

Example:

```text
user@linuxlingo:/$ sort data.txt | uniq -c
      3 apple
      1 banana
      2 cherry
```

### Permission Commands

#### Changing file permissions: `chmod`

Changes the permissions of a file or directory.

Format: `chmod MODE FILE`

Supports two permission formats:

| Format | Example | Description |
| ------ | ------- | ----------- |
| Octal | `chmod 755 script.sh` | Sets permissions using a 3-digit octal number (owner/group/others). |
| Symbolic | `chmod u+x script.sh` | Modifies specific permissions using `[ugoa][+-=][rwx]` syntax. |

**Octal reference:**

| Digit | Permission |
| ----- | ---------- |
| 7 | `rwx` (read + write + execute) |
| 6 | `rw-` (read + write) |
| 5 | `r-x` (read + execute) |
| 4 | `r--` (read only) |
| 0 | `---` (no permissions) |

**Symbolic reference:**

- `u` = owner, `g` = group, `o` = others, `a` = all
- `+` = add, `-` = remove, `=` = set exactly
- `r` = read, `w` = write, `x` = execute

Examples:

```text
user@linuxlingo:/$ chmod 644 data.txt
user@linuxlingo:/$ chmod u+x script.sh
user@linuxlingo:/$ chmod go-w sensitive.txt
```

### Environment Management Commands

These commands let you save, load, and manage snapshots of the virtual file system, so you can preserve your work across sessions.

#### Saving an environment: `save`

Saves the current VFS state and working directory as a named snapshot.

Format: `save NAME`

- The name can only contain letters, digits, hyphens, and underscores.
- Snapshots are stored in the `data/environments/` folder.

Example:

```text
user@linuxlingo:/home/user$ save my-workspace
Environment saved: my-workspace
```

#### Loading an environment: `load`

Restores a previously saved VFS snapshot.

Format: `load NAME`

Example:

```text
user@linuxlingo:/$ load my-workspace
Environment loaded: my-workspace
user@linuxlingo:/home/user$
```

#### Resetting the environment: `reset`

Resets the VFS to its default initial state (empty file system with standard directories).

Format: `reset`

#### Listing saved environments: `envlist`

Lists all saved environment snapshots.

Format: `envlist`

Example:

```text
user@linuxlingo:/$ envlist
Saved environments:
  my-workspace
  lab-exercise-3
```

#### Deleting a saved environment: `envdelete`

Deletes a saved environment snapshot.

Format: `envdelete NAME`

Example:

```text
user@linuxlingo:/$ envdelete old-env
Environment deleted: old-env
```

### Utility Commands

#### Viewing command help: `help`

Displays a list of all available shell commands, or detailed usage for a specific command.

Format: `help [COMMAND]`

Examples:

```text
user@linuxlingo:/$ help
Available commands:
  cat   - Display file contents
  cd    - Change working directory
  ...

user@linuxlingo:/$ help grep
Usage: grep [-i] [-v] [-n] [-c] PATTERN [FILE...]
Search for lines matching a pattern
```

#### Clearing the screen: `clear`

Clears the terminal screen.

Format: `clear`

### Piping and Redirection

LinuxLingo supports piping and output redirection, just like a real Linux shell.

**Piping (`|`):** Connects the output of one command to the input of the next.

```text
user@linuxlingo:/$ echo "apple banana cherry" | wc -w
3
```

**Output redirection (`>`):** Writes command output to a file, overwriting existing content.

```text
user@linuxlingo:/$ echo "Hello" > greeting.txt
```

**Append redirection (`>>`):** Appends command output to a file.

```text
user@linuxlingo:/$ echo "World" >> greeting.txt
user@linuxlingo:/$ cat greeting.txt
Hello
World
```

### Command Chaining

You can chain multiple commands using operators:

| Operator | Behavior | Example |
| -------- | -------- | ------- |
| `&&` | Run the next command only if the previous one **succeeded** | `mkdir dir && cd dir` |
| `;` | Run the next command **regardless** of whether the previous one succeeded | `echo start ; ls ; echo done` |

Example:

```text
user@linuxlingo:/$ mkdir projects && cd projects && touch readme.txt
user@linuxlingo:/projects$
```

---

## Exam System

LinuxLingo includes a built-in exam system to test your knowledge of Linux commands. Questions are drawn from a question bank covering multiple topics.

### Interactive Exam

Start an interactive exam from the main menu.

Format: `exam`

1. A list of available topics is displayed (with question counts).
2. You are prompted to select a topic by number or name.
3. You are prompted for how many questions to attempt (press Enter for all).
4. Questions are presented one at a time. After each answer, you receive immediate feedback (`✓ Correct!` or `✗ Incorrect.`) along with an explanation.
5. At the end, your score is displayed (e.g., `Score: 7/10 (70%)`).

> 💡 **Tip:** Type `quit` to skip a question during an exam.

### Exam with CLI Arguments

You can start an exam directly with arguments for a faster workflow.

Format: `exam -t TOPIC [-n COUNT] [-random]`

- `-t TOPIC` — specify the topic (e.g., `navigation`, `file-management`).
- `-n COUNT` — number of questions (default: all questions in the topic).
- `-random` — randomize question order.

Example: `exam -t text-processing -n 5 -random`

### Random Question

Presents a single random question from any topic — great for a quick knowledge check.

Format: `exam -random`

### Listing Topics

Shows all available exam topics and how many questions each contains.

Format: `exam -topics`

Example output:

```text
Available topics:
  1. file-management (16 questions)
  2. navigation (10 questions)
  3. permissions (10 questions)
  4. piping-redirection (12 questions)
  5. text-processing (12 questions)
```

### Question Types

| Type | Description | How to Answer |
| ---- | ----------- | ------------- |
| **MCQ** (Multiple Choice) | Choose the correct option from A, B, C, D. | Type the letter of your answer (e.g., `B`). |
| **FITB** (Fill In The Blank) | Fill in the missing command or argument. | Type the missing text exactly (e.g., `pwd`). |
| **PRAC** (Practical) | Perform a task in a temporary shell session. | Execute commands in the temporary shell, then type `exit` when done. The VFS is automatically checked. |

**PRAC question example flow:**

1. The question describes a task (e.g., "Create a directory at `/home/project` and a file at `/home/project/readme.txt`").
2. You enter a temporary shell session with a fresh VFS.
3. You type the required commands (e.g., `mkdir -p /home/project`, `touch /home/project/readme.txt`).
4. Type `exit` to submit.
5. LinuxLingo checks whether the VFS matches the expected state and gives feedback.

---

## Data Storage

- **Saved environments** are stored as `.env` files in the `data/environments/` directory, relative to where you run the JAR.
- **Question banks** are stored as `.txt` files in the `data/questions/` directory. They are automatically extracted from the JAR on first run.
- You can safely back up or transfer these directories to another computer.

> ⚠️ **Caution:** Manually editing `.env` or question bank files may cause errors if the format becomes invalid. Only edit these files if you understand the expected format.

---

## FAQ

**Q: How do I transfer my data to another computer?**

A: Copy the `data/` folder (which contains `environments/` and `questions/`) to the same location relative to the JAR file on the other computer.

**Q: Do shell commands affect real files on my computer?**

A: No. All shell commands operate on an in-memory virtual file system (VFS). No real files are created, modified, or deleted by shell commands. Only the `save` command writes data to disk (in `data/environments/`).

**Q: Can I add my own exam questions?**

A: Yes. Add `.txt` files to the `data/questions/` directory following the existing pipe-delimited format. They will be automatically loaded on the next startup.

**Q: What happens if the question bank directory already exists on startup?**

A: LinuxLingo will not overwrite existing question files, so any custom questions you have added are preserved.

**Q: Why is the VFS empty after restarting LinuxLingo?**

A: The VFS is in-memory and resets each time the application starts. Use the `save` command to persist your environment, and `load` to restore it in a future session.

---

## Known Issues

1. **ANSI escape codes on Windows:** The `clear` command uses ANSI escape sequences which may not work correctly on older Windows command prompts. Use Windows Terminal or PowerShell for best results.
2. **PRAC questions require shell implementation:** Practical exam questions depend on shell commands being fully functional. If a command is not yet implemented, the PRAC question may not work as expected.

---

## Command Summary

### Main Menu

| Command | Format |
| ------- | ------ |
| Enter Shell | `shell` |
| Start Exam | `exam [-t TOPIC] [-n COUNT] [-random]` |
| List Topics | `exam -topics` |
| Random Question | `exam -random` |
| One-shot Exec | `exec "COMMAND"` |
| Exec in Environment | `exec -e ENV_NAME "COMMAND"` |
| Help | `help` |
| Exit | `exit` |

### Shell Commands

| Command | Format | Description |
| ------- | ------ | ----------- |
| `pwd` | `pwd` | Print working directory |
| `cd` | `cd [DIRECTORY]` | Change directory |
| `ls` | `ls [-a] [-l] [DIRECTORY]` | List directory contents |
| `mkdir` | `mkdir [-p] DIRECTORY...` | Create directories |
| `touch` | `touch FILE...` | Create empty files |
| `cat` | `cat FILE...` | Display file contents |
| `echo` | `echo [TEXT...]` | Print text |
| `rm` | `rm [-r] [-f] FILE...` | Remove files/directories |
| `cp` | `cp [-r] SOURCE DEST` | Copy files/directories |
| `mv` | `mv SOURCE DEST` | Move/rename files |
| `head` | `head [-n COUNT] [FILE]` | Display first N lines |
| `tail` | `tail [-n COUNT] [FILE]` | Display last N lines |
| `grep` | `grep [-i] [-v] [-n] [-c] PATTERN [FILE...]` | Search for pattern |
| `find` | `find [DIR] -name PATTERN` | Find files by name |
| `wc` | `wc [-l] [-w] [-c] [FILE...]` | Count lines/words/chars |
| `sort` | `sort [-r] [-n] [FILE]` | Sort lines |
| `uniq` | `uniq [-c] [FILE]` | Remove adjacent duplicates |
| `chmod` | `chmod MODE FILE` | Change permissions |
| `save` | `save NAME` | Save VFS snapshot |
| `load` | `load NAME` | Load VFS snapshot |
| `reset` | `reset` | Reset VFS to default |
| `envlist` | `envlist` | List saved environments |
| `envdelete` | `envdelete NAME` | Delete saved environment |
| `help` | `help [COMMAND]` | Show help |
| `clear` | `clear` | Clear screen |
