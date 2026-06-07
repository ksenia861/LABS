package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class ReorderCommand implements Command {
    private CollectionManager collectionManager;
    public ReorderCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "reorder"; }
    public String getDescription() { return "перевернуть коллекцию"; }
    public Response execute(Req req) {
        collectionManager.reorder();
        return new Response("Коллекция перевернута");
    }
}