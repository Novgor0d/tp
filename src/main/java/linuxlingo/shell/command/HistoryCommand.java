package linuxlingo.shell.command;

import java.util.List;
import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;

/**
 * Displays the in-session command history.
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>{@code history}    — lists all commands, numbered from 1.</li>
 *   <li>{@code history N}  — shows the last N commands.</li>
 *   <li>{@code history -c} — clears the command history.</li>
 * </ul>
 */
public class HistoryCommand implements Command {

    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        // [v2.0 STUB] TODO: Implement history command.
        // No args: list numbered history entries.
        // -c flag: clear history.
        // Numeric arg N: show last N commands.
        // Fall back to session.getCommandHistory() if no ShellLineReader.

        List<String> history = session.getCommandHistory();

        if (args.length > 0 && args[0].equals("-c")) {
            history.clear();
            return CommandResult.success("");
        }

        if (args.length > 0) {
            return showLastN(history, args[0]);
        }

        return formatHistory(history, 0);
    }

    /**
     * Returns the last {@code n} history entries as a formatted result.
     * Returns an error result if the argument is not a valid non-negative integer.
     *
     * @param history the current command history list
     * @param nStr    the raw argument string representing {@code N}
     * @return a {@link CommandResult} with the last N entries, or an error
     */
    private CommandResult showLastN(List<String> history, String nStr) {
        int n;
        try {
            n = Integer.parseInt(nStr);
        } catch (NumberFormatException e) {
            return CommandResult.error("history: numeric argument required");
        }

        if ( n < 0) {
            return CommandResult.error("history: invalid option: " + nStr);
        }

        int startIndex = Math.max(0, history.size() - n);
        return formatHistory(history, startIndex);
    }

    /**
     * Formats history entries as a numbered list starting from {@code fromIndex}.
     *
     * @param history   the full history list
     * @param fromIndex the index to start listing from (inclusive)
     * @return a {@link CommandResult} containing the formatted output
     */
    private CommandResult formatHistory(List<String> history, int fromIndex) {
        if (history.isEmpty()) {
            return CommandResult.success("");
        }

        StringBuilder sbuild = new StringBuilder();
        for (int i = fromIndex; i < history.size(); i++) {
            if (sbuild.length() > 0) {
                sbuild.append('\n');
            }
            sbuild.append(String.format("%5d %s", i + 1, history.get(i)));
        }
        return CommandResult.success(sbuild.toString());
    }
    @Override
    public String getUsage() {
        return "history [-c] [N]";
    }

    @Override
    public String getDescription() {
        return "Display or manage command history";
    }
}
