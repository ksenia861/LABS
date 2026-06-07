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
            String login = req.getLogin();
            boolean removed = collectionManager.removeById(id, login);
            if (removed) {
                return new Response("Город удалён");
            } else {
                return new Response("Город не найден или принадлежит другому пользователю");
            }
        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом");
        }
    }
}