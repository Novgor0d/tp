package linuxlingo.shell.command;

import java.util.Map;
import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;

/**
 * Creates or displays shell aliases.
 * Syntax: alias [name=value]
 *
 * <p><b>Owner: A — stub; to be implemented.</b></p>
 *
 * TODO: Member A should implement:
 * - No args: list all aliases
 * - name=value: set an alias
 * - name (without =): show specific alias
 * - Strip surrounding quotes from values
 */
public class AliasCommand implements Command {

    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
        // [v2.0 STUB] TODO: Implement alias command.
        // No args: list all aliases.
        // name=value: set an alias (strip surrounding quotes from value).
        // name (without =): show that specific alias.
        if (args.length == 0) {
            return listAliases(session);
        }

        // name without = : show that specific alias
        if (!args[0].contains("=")) {
            return showAlias(session, args[0]);
        }

        return setAlias(session, args[0]);
    }

    /**
     * Print all current aliases
     * format for printing is name = 'value'
     */
    private CommandResult listAliases(ShellSession session) {
        Map<String, String > aliases = session.getAliases();
        if (aliases.isEmpty()) {
            return CommandResult.success("");
        }

        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            if (!output.isEmpty()) {
                output.append('\n');
            }
            output.append("alias ").append(entry.getKey()).append("='").append(entry.getValue()).append("'");
        }
        return CommandResult.success(output.toString());
    }

    /**
     * Parses name=value or name='value and stores them in the aliases map
     * @param definition the raw alias definition argument
     * @return
     */
    private CommandResult setAlias(ShellSession session, String definition) {
        int eqIndex = definition.indexOf('=');
        if (eqIndex <= 0) {
            return CommandResult.error("alias: invalid format: '" + definition + "' (expected name=value)");
        }

        String name = definition.substring(0, eqIndex);
        String value = definition.substring(eqIndex + 1);
        value = stripSurroundingQuotes(value);

        if (name.isBlank()) {
            return CommandResult.error("alias: name must not be blank");
        }

        session.getAliases().put(name, value);
        return CommandResult.success("");
    }

    private String stripSurroundingQuotes(String s) {
        if (s.length() >= 2 && s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'') {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private CommandResult showAlias(ShellSession session, String name) {
        String value = session.getAliases().get(name);
        if (value == null) {
            return CommandResult.error("alias: " + name + ": not found");
        }
        return CommandResult.success("alias " + name + "='" + value + "'");
    }

    @Override
    public String getUsage() {
        return "alias [name=value]";
    }

    @Override
    public String getDescription() {
        return "Create or display shell aliases";
    }
}
