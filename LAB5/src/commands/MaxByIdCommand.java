package commands;

import managers.CollectionManager;
import util.Response;
import util.Req;

public class MaxByIdCommand implements Command {

    private CollectionManager collectionManager;

    public MaxByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "max_by_id";
    }
    public String getDescription() {
        return "вывести город с максимальным id";
    }
    public Response execute(Req req) {
        return new Response(collectionManager.maxById());
    }
}