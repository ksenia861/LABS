package commands;

import managers.CollectionManager;
import util.Response;
import util.Req;

public class RemoveCommand implements Command {
    private CollectionManager collectionManager;
    public RemoveCommand(CollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }
    @Override
    public String getName() {
        return "remove";
    }
    @Override
    public String getDescription() {
        return "удалить последний город из коллекции";
    }
    @Override
    public Response execute(Req req) {
        if (collectionManager.getCollection().isEmpty()) {
            return new Response("Коллекция пустая");
        }
        collectionManager.getCollection().pop();
        return new Response("Последний элемент удалён");
    }
}