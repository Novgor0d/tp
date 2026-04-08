package linuxlingo.shell.command;

import java.util.Map;
import linuxlingo.shell.CommandResult;
import linuxlingo.shell.ShellSession;

/**
 * Creates or displays shell aliases.
 * Syntax: alias [name=value]
 *
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>{@code alias}            — lists all currently defined aliases.</li>
 *   <li>{@code alias name=value} — defines a new alias, stripping surrounding quotes.</li>
 *   <li>{@code alias name}       — displays the value of a specific alias.</li>
 * </ul>
 */
public class AliasCommand implements Command {

    @Override
    public CommandResult execute(ShellSession session, String[] args, String stdin) {
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
     * Lists all currently defined aliases, one per line in {@code alias name='value'} format.
     *
     * @param session the active shell session
     * @return a {@link CommandResult} containing the formatted alias list, or empty if none defined
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
     * Parses a {@code name=value} definition and stores it in the session's alias map.
     * Surrounding single or double quotes are stripped from the value.
     *
     * @param session    the active shell session
     * @param definition the raw alias definition (e.g. {@code ll=ls -la} or {@code ll='ls -la'})
     * @return a {@link CommandResult} indicating success or a descriptive error
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

    /**
     * Displays the value of a single named alias.
     *
     * @param session the active shell session
     * @param name    the alias name to look up
     * @return a {@link CommandResult} with the alias definition, or an error if not found
     */
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
