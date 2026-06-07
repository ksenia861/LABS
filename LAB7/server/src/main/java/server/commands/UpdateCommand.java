package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class UpdateCommand implements Command {
    private CollectionManager collectionManager;
    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "update"; }
    public String getDescription() { return "обновить элемент по id"; }
    public Response execute(Req req) {
        try {
            int id = Integer.parseInt(req.getArgument());
            String login = req.getLogin();
            boolean updated = collectionManager.updateCity(id, req.getCity(), login);
            if (updated) {
                return new Response("Город обновлён");
            } else {
                return new Response("Город не найден или принадлежит другому пользователю");
            }
        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом");
        }
    }
}