package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class RemoveGreaterCommand implements Command {
    private CollectionManager collectionManager;
    public RemoveGreaterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "remove_greater"; }
    public String getDescription() { return "удалить элементы больше заданного"; }
    public Response execute(Req req) {
        collectionManager.removeGreater(req.getCity());
        return new Response("Элементы удалены");
    }
}