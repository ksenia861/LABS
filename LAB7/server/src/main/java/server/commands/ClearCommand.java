package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class ClearCommand implements Command {
    private CollectionManager collectionManager;
    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "clear"; }
    public String getDescription() { return "очистить ваши элементы из коллекции"; }

    public Response execute(Req req) {
        String login = req.getLogin();
        collectionManager.clear(login);
        return new Response("Все ваши города удалены из коллекции");
    }
}