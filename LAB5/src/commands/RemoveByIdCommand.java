package commands;

import managers.CollectionManager;
import util.Response;
import util.Req;

public class RemoveByIdCommand implements Command {

    private CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "remove_by_id";
    }
    public String getDescription() {
        return "удалить город по id";
    }
    public Response execute(Req req) {
        int id;
        try{
            id = Integer.parseInt(req.getArg());
        } catch (Exception e) {
            return new Response("id должен быть числом");
        }
        boolean result = collectionManager.removeById(id);
        if (result) {
            return new Response("город удален");
        }
        return new Response("город не найден");
    }
}
