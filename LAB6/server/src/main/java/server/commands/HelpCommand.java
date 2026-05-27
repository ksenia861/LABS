package server.commands;

import common.network.Req;
import common.network.Response;

import java.util.Map;

public class HelpCommand implements Command {
    private Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    public String getName() { return "help"; }
    public String getDescription() { return "вывести справку по доступным командам"; }

    public Response execute(Req req) {
        StringBuilder help = new StringBuilder("Доступные команды:\n");
        commands.values().stream()
                .forEach(cmd -> help.append(cmd.getName()).append(": ").append(cmd.getDescription()).append("\n"));
        return new Response(help.toString());
    }
}