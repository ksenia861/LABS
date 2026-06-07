package server.commands;

import common.network.Req;
import common.network.Response;

public class ExitCommand implements Command {
    public String getName() { return "exit"; }
    public String getDescription() { return "завершить работу (реализовано на клиенте)"; }
    public Response execute(Req req) {
        String login = req.getLogin();

        return new Response("Команда exit выполняется на клиенте");
    }
}