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
    public String getDescription() { return "удалить ваши элементы, которые больше заданного"; }
    public Response execute(Req req) {
        String login = req.getLogin();
        collectionManager.removeGreater(req.getCity(), login);
        return new Response("Ваши города, превышающие заданный, удалены");
    }
}