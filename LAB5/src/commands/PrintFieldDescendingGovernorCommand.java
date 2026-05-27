package commands;

import managers.CollectionManager;
import util.Response;
import util.Req;

public class PrintFieldDescendingGovernorCommand implements Command {
    private CollectionManager collectionManager;
    public PrintFieldDescendingGovernorCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() {
        return "print_field_descending_governor";
    }
    public String getDescription() {
        return "вывести губернаторов по убыванию";
    }
    public Response execute(Req req) {
        return new Response(collectionManager.printFieldDescendingGovernor());
    }
}