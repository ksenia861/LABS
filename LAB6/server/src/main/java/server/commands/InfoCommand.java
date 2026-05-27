package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class InfoCommand implements Command {
    private CollectionManager collectionManager;
    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "info"; }
    public String getDescription() { return "информация о коллекции"; }
    public Response execute(Req req) {
        return new Response(collectionManager.info());
    }
}