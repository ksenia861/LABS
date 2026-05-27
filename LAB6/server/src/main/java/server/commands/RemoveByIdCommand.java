package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class RemoveByIdCommand implements Command {
    private CollectionManager collectionManager;
    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "remove_by_id"; }
    public String getDescription() { return "удалить элемент по id"; }
    public Response execute(Req req) {
        try {
            int id = Integer.parseInt(req.getArgument());
            boolean removed = collectionManager.removeById(id);
            return new Response(removed ? "Город удален" : "Город не найден");
        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом");
        }
    }
}