package commands;

import managers.CollectionManager;
import util.Response;
import util.Req;

public class PrintDescendingCommand implements Command {
    private CollectionManager collectionManager;
    public PrintDescendingCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "print_descending";
    }
    public String getDescription() {
        return "вывести элементы по убыванию";
    }
    public Response execute(Req req) {
        return new Response(collectionManager.printDescending());
    }
}