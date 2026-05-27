package commands;

import managers.CollectionManager;
import util.Req;
import util.Response;

public class UpdateCommand implements Command {
    private CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;

    }
    public String getName() {
        return "update";
    }
    public String getDescription() {
        return "обновить город по id";
    }
    public Response execute(Req req) {
        int id;
        try {
            id = Integer.parseInt(req.getArg());
        } catch (Exception e) {
            return new Response("id должен быть числом");
        }
        boolean result = collectionManager.updateCity(id, req.getCity());
        if (result) {
            return new Response("город обновлен");
        }
        return new Response("ород не найден");
    }
}