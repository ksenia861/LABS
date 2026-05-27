package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class RemoveCommand implements Command {
    private CollectionManager collectionManager;
    public RemoveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "remove"; }
    public String getDescription() { return "удалить последний элемент"; }
    public Response execute(Req req) {
        if (collectionManager.getCollection().isEmpty()) {
            return new Response("Коллекция пуста");
        }
        collectionManager.getCollection().pop();
        return new Response("Последний элемент удалён");
    }
}