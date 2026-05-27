package commands;

import managers.CollectionManager;
import util.Response;
import util.Req;

public class SaveCommand implements Command {
    private CollectionManager collectionManager;
    public SaveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public String getName() {
        return "save";
    }
    @Override
    public String getDescription() {
        return "сохранить коллекцию в файл";
    }
    @Override
    public Response execute(Req req) {
        return new Response(collectionManager.save());
    }
}