package commands;
import util.Response;
import util.Req;

public interface Command {
    String getName();
    String getDescription();
    Response execute(Req req);
}