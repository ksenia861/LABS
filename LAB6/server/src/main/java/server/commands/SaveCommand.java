package server.commands;

import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

public class SaveCommand implements Command {
    private CollectionManager collectionManager;
    public SaveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "save"; }
    public String getDescription() { return "сохранить коллекцию в файл (только сервер)"; }
    public Response execute(Req req) {
        collectionManager.save();
        return new Response("Коллекция сохранена");
    }
}