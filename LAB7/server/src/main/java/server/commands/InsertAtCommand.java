package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class InsertAtCommand implements Command {
    private CollectionManager collectionManager;
    public InsertAtCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "insert_at"; }
    public String getDescription() { return "добавить элемент на указанную позицию"; }
    public Response execute(Req req) {
        try {
            int index = Integer.parseInt(req.getArgument());
            String login = req.getLogin();
            collectionManager.insertAt(index, req.getCity(), login);
            return new Response("Город добавлен на позицию " + index);
        } catch (NumberFormatException e) {
            return new Response("Индекс должен быть числом");
        } catch (IndexOutOfBoundsException e) {
            return new Response("Неверный индекс: " + e.getMessage());
        }
    }
}