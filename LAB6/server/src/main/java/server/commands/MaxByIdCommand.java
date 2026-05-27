package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class MaxByIdCommand implements Command {
    private CollectionManager collectionManager;
    public MaxByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "max_by_id"; }
    public String getDescription() { return "вывести элемент с максимальным id"; }
    public Response execute(Req req) {
        return new Response(collectionManager.maxById());
    }
}