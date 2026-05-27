package server.commands;
import common.network.Req;
import common.network.Response;

public interface Command {
    String getName();
    String getDescription();
    Response execute(Req req);
}