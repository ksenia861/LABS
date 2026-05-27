package commands;

import managers.CommandManager;
import util.Req;
import util.Response;
import models.City;
import util.InputManager;

import java.io.BufferedReader;
import java.io.FileReader;


public class ExecuteScriptCommand implements Command {
    private final CommandManager commandManager;
    private final InputManager inputManager;
    private static final java.util.Set<String> runningScripts = new java.util.HashSet<>();

    public ExecuteScriptCommand(InputManager inputManager, CommandManager commandManager) {
        this.inputManager = inputManager;
        this.commandManager = commandManager;
    }
    public String getName() {
        return "execute_script";
    }

    public String getDescription() {
        return "выполнить команды из файла";
    }

    public Response execute(Req req) {
        String fileName = req.getArg().trim();
        runningScripts.add(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] parts = line.split(" ", 2);
                String commandName = parts[0];
                String arg = "";
                if (parts.length > 1) {
                    arg = parts[1];
                }
                if (commandName.equals("execute_script")) {

                    if (runningScripts.contains(arg)) {
                        System.out.println("Ошибка: рекурсивный вызов скрипта " + arg);
                        continue;
                    }
                }
                City city = null;
                if (commandName.equals("add")
                        || commandName.equals("insert_at")
                        || commandName.equals("update")
                        || commandName.equals("remove_greater")) {
                    city = inputManager.readCityFromScript(reader);
                }
                Req scriptReq = new Req(arg, city);
                Response response = commandManager.execute(commandName, scriptReq);
                if (response != null) {
                    System.out.println(response.getMessage());
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            return new Response("Ошибка чтения файла");
        }
        runningScripts.remove(fileName);
        return new Response("Скрипт выполнен");
    }

}