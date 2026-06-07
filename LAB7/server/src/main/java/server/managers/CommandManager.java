package server.managers;

import server.commands.*;
import common.network.Req;
import common.network.Response;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final DatabaseManager dbManager;
    public CommandManager(CollectionManager collectionManager, DatabaseManager dbManager) {
        this.dbManager = dbManager;
        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("remove", new RemoveCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        commands.put("exit", new ExitCommand());
        commands.put("insert_at", new InsertAtCommand(collectionManager));
        commands.put("remove_greater", new RemoveGreaterCommand(collectionManager));
        commands.put("reorder", new ReorderCommand(collectionManager));
        commands.put("max_by_id", new MaxByIdCommand(collectionManager));
        commands.put("print_descending", new PrintDescendingCommand(collectionManager));
        commands.put("print_field_descending_governor", new PrintFieldDescendingGovernorCommand(collectionManager));
    }
    public Response execute(String name, Req req) {
        if (name.equals("login")) {
            boolean success = dbManager.validateUser(req.getLogin(), req.getPassword());
            return success ? new Response("Вход выполнен успешно!")
                    : new Response("Неверный логин или пароль.");
        }
        if (name.equals("register")) {
            boolean success = dbManager.registerUser(req.getLogin(), req.getPassword());
            return success ? new Response("Регистрация прошла успешно!")
                    : new Response("Пользователь уже существует.");
        }
        if (!dbManager.validateUser(req.getLogin(), req.getPassword())) {
            return new Response("Ошибка: неверный логин или пароль. Доступ запрещён.");
        }
        Command command = commands.get(name);
        if (command == null) {
            return new Response("Неизвестная команда");
        }

        return command.execute(req);
    }
}