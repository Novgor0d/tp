package linuxlingo.shell.command;

import java.util.Map;
import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;

/**
 * Removes shell aliases.
 * Syntax: unalias &lt;name&gt; [-a]
 *
 * <p><b>Owner: A — stub; to be implemented.</b></p>
 *
 * TODO: Member A should implement:
 * - Remove a named alias
 * - -a flag to clear all aliases
 * - Error for non-existent aliases
 */
public class UnaliasCommand implements Command {

    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        // [v2.0 STUB] TODO: Implement unalias command.
        // Remove a named alias, or use -a to clear all aliases.
        // Return error for missing args or non-existent aliases.
        if (args.length == 0) {
            return CommandResult.error("unalias: usage: unalias name [name ...]");
        }

        Map<String, String> aliases = session.getAliases();

        // -a flag clears all aliases
        if (args[0].equals("-a")) {
            aliases.clear();
            return CommandResult.success("");
        }


        StringBuilder errors = new StringBuilder();

        for (String name : args) {
            if (!aliases.containsKey(name)) {
                if (errors.length() > 0) {
                    errors.append('\n');
                }
                errors.append("unalias: ").append(name).append(": not found");
            } else {
                aliases.remove(name);
            }
        }

        return errors.length() > 0 ? CommandResult.error(errors.toString()) : CommandResult.success("");
    }

    @Override
    public String getUsage() {
        return "unalias [-a] <name>";
    }

    @Override
    public String getDescription() {
        return "Remove shell aliases";
    }
}
