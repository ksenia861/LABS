package server.commands;
import common.models.City;
import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class AddCommand implements Command{
    private CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "add";
    }
    public String getDescription() {
        return "добавить новый город";
    }
    public Response execute(Req req) {
        City city = req.getCity();
        if(city == null) {
            return new Response("Город не передан");
        }
        collectionManager.addCity(city);
        return new Response("город добавлен");
    }
}