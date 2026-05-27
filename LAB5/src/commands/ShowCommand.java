package commands;

import managers.CollectionManager;
import util.Response;
import util.Req;

public class ShowCommand implements Command {
    private CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "show";
    }
    public String getDescription() {
        return "показать се элементы коллекции";
    }
    public Response execute(Req req) {
        return new Response(collectionManager.showAllCities());
    }
}