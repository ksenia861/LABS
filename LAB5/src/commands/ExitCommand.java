package commands;
import util.Response;
import util.Req;
public class ExitCommand implements Command {
    public String getName() {
        return "exit";
    }
    public String getDescription() {
        return "завершить программу";
    }
    public Response execute(Req req) {
        System.exit(0);
        return new Response("выход");
    }
}