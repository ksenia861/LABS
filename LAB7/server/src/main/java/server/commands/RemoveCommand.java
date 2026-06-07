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
        try {
            boolean removed = collectionManager.removeLast(req.getLogin());
            return removed ? new Response("Последний элемент удалён") : new Response("Последний элемент не принадлежит вам или коллекция пуста");
        } catch (Exception e) {
            return new Response("Ошибка при удалении последнего элемента");
        }
    }
}