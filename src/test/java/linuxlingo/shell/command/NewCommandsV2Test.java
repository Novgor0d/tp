package linuxlingo.shell.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import linuxlingo.cli.Ui;
import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;
import linuxlingo.shell.vfs.VirtualFileSystem;

/**
 * Tests for all v2.0 new commands (stub verification).
 *
 * <h3>v2.0 (stub)</h3>
 * <p>Each new command's execute() currently returns "not yet implemented".
 * Tests marked {@code @Disabled} document the expected behaviour once
 * the feature is fully implemented.</p>
 */
public class NewCommandsV2Test {
    private ShellSession session;
    private VirtualFileSystem vfs;

    @BeforeEach
    public void setUp() {
        vfs = new VirtualFileSystem();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), out);
        session = new ShellSession(vfs, ui);
        session.setWorkingDir("/home/user");
    }

    // ─── Stub verification: every new command returns "not yet implemented" ──

    @Test
    public void manCommand_stub_returnsNotImplemented() {
        CommandResult r = new ManCommand().execute(session, new String[]{"ls"}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void treeCommand_stub_returnsNotImplemented() {
        CommandResult r = new TreeCommand().execute(session, new String[]{}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void whichCommand_stub_returnsNotImplemented() {
        CommandResult r = new WhichCommand().execute(session, new String[]{"ls"}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void whoamiCommand_stub_returnsNotImplemented() {
        CommandResult r = new WhoamiCommand().execute(session, new String[]{}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void dateCommand_stub_returnsNotImplemented() {
        CommandResult r = new DateCommand().execute(session, new String[]{}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void aliasCommand_stub_returnsNotImplemented() {
        CommandResult r = new AliasCommand().execute(session, new String[]{}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void unaliasCommand_stub_returnsNotImplemented() {
        CommandResult r = new UnaliasCommand().execute(session, new String[]{"ll"}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void teeCommand_stub_returnsNotImplemented() {
        CommandResult r = new TeeCommand().execute(session, new String[]{"out.txt"}, "data");
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void diffCommand_stub_returnsNotImplemented() {
        vfs.createFile("/home/user/a.txt", "/");
        vfs.createFile("/home/user/b.txt", "/");
        CommandResult r = new DiffCommand().execute(session, new String[]{"a.txt", "b.txt"}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    @Test
    public void historyCommand_stub_returnsNotImplemented() {
        CommandResult r = new HistoryCommand().execute(session, new String[]{}, null);
        assertFalse(r.isSuccess());
        assertTrue(r.getStderr().contains("not yet implemented"));
    }

    // ─── Metadata: getUsage / getDescription are kept ───────────

    @Test
    public void allNewCommands_haveUsageAndDescription() {
        Command[] cmds = {
            new ManCommand(), new TreeCommand(), new WhichCommand(),
            new WhoamiCommand(), new DateCommand(), new AliasCommand(),
            new UnaliasCommand(), new TeeCommand(), new DiffCommand(),
            new HistoryCommand()
        };
        for (Command c : cmds) {
            assertFalse(c.getUsage().isEmpty(), c.getClass().getSimpleName() + " usage empty");
            assertFalse(c.getDescription().isEmpty(), c.getClass().getSimpleName() + " desc empty");
        }
    }

    // ─── @Disabled: document expected full behaviour ────────────

    @Nested
    @Disabled("v2.0 — ManCommand to be implemented")
    class ManExpectedBehaviour {
        @Test
        public void man_knownCommand_showsManPage() {
            CommandResult result = new ManCommand().execute(session, new String[]{"ls"}, null);
            assertTrue(result.isSuccess());
            assertTrue(result.getStdout().contains("NAME"));
        }
    }

    @Nested
    @Disabled("v2.0 — TreeCommand to be implemented")
    class TreeExpectedBehaviour {
        @Test
        public void tree_defaultDir_showsTree() {
            vfs.createDirectory("/home/user/project", "/", true);
            CommandResult result = new TreeCommand().execute(session, new String[]{}, null);
            assertTrue(result.isSuccess());
            assertTrue(result.getStdout().contains("project"));
        }
    }

    @Nested
    @Disabled("v2.0 — AliasCommand to be implemented")
    class AliasExpectedBehaviour {
        @Test
        public void alias_setAlias_storesCorrectly() {
            CommandResult result = new AliasCommand().execute(session, new String[]{"ll=ls -la"}, null);
            assertTrue(result.isSuccess());
        }
    }

    @Nested
    @Disabled("v2.0 — TeeCommand to be implemented")
    class TeeExpectedBehaviour {
        @Test
        public void tee_writesStdinToFileAndStdout() {
            CommandResult result = new TeeCommand().execute(session, new String[]{"out.txt"}, "hello");
            assertTrue(result.isSuccess());
        }
    }

    @Nested
    @Disabled("v2.0 — DiffCommand to be implemented")
    class DiffExpectedBehaviour {
        @Test
        public void diff_identicalFiles_returnsEmpty() {
            vfs.createFile("/home/user/a.txt", "/");
            vfs.createFile("/home/user/b.txt", "/");
            vfs.writeFile("/home/user/a.txt", "/", "same", false);
            vfs.writeFile("/home/user/b.txt", "/", "same", false);
            CommandResult result = new DiffCommand().execute(session, new String[]{"a.txt", "b.txt"}, null);
            assertTrue(result.isSuccess());
        }
    }
}
