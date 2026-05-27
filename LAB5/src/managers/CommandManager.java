package managers;

import commands.*;
import util.Req;
import util.Response;
import java.util.HashMap;
import java.util.Map;
import util.InputManager;
public class CommandManager {
    private InputManager inputManager;
    private final Map<String, Command> commands = new HashMap<>();
    public CommandManager(CollectionManager collectionManager, InputManager inputManager) {

        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("remove", new RemoveCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        commands.put("save", new SaveCommand(collectionManager));
        commands.put("execute_script", new ExecuteScriptCommand(inputManager,this));
        commands.put("exit", new ExitCommand());
        commands.put("insert_at", new InsertAtCommand(collectionManager));
        commands.put("remove_greater", new RemoveGreaterCommand(collectionManager));
        commands.put("reorder", new ReorderCommand(collectionManager));
        commands.put("max_by_id", new MaxByIdCommand(collectionManager));
        commands.put("print_descending", new PrintDescendingCommand(collectionManager));
        commands.put("print_field_descending_governor", new PrintFieldDescendingGovernorCommand(collectionManager));
    }
    public Response execute(String name, Req req) {
        Command command = commands.get(name);
        if(command == null) {
            return new Response("неизвестная команда");
        }
        return command.execute(req);
    }
}