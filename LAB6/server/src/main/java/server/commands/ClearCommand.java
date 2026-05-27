package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class ClearCommand implements Command {
    private CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "clear";
    }
    public String getDescription() {
        return "очистить коллекцию";
    }
    public Response execute(Req req) {
        collectionManager.clear();
        return new Response("коллекция очищена");
    }
}