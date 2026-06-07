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
        String login = req.getLogin();
        if(city == null) {
            return new Response("Город не передан");
        }
        if (login == null) {
            return new Response("Вы не авторизованы!");
        }
        boolean success = collectionManager.addCity(city, login);
        if(success) {
            return new Response("Город успешно добавлен в базу данных и коллекцию");
        } else {
            return new Response("Ошибка базы данных. Город не добавлен");
        }
    }
}