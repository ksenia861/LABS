package commands;
import managers.CollectionManager;
import util.Response;
import util.Req;

public class InfoCommand implements Command{
    private CollectionManager collectionManager;
    public InfoCommand(CollectionManager collectionManager){
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "info";
    }
    public String getDescription() {
        return "информация о коллекции";
    }
    public Response execute(Req req){
        return new Response(collectionManager.info());
    }
}