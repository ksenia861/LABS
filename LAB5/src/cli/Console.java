package cli;

import managers.CommandManager;
import models.City;
import util.InputManager;
import util.Req;
import util.Response;
import java.util.Scanner;

public class Console{
    private CommandManager commandManager;
    private InputManager inputManager;

    public Console(CommandManager commandManager, InputManager inputManager) {
        this.commandManager = commandManager;
        this.inputManager = inputManager;
    }
    public void start() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            String line;
            try {
                line = scanner.nextLine();
            } catch (Exception e) {
                System.out.println("программа завершена");
                return;
            }
            String[] parts = line.split(" ",2);
            String commandName = parts[0];
            String arg = "";
            if (parts.length > 1){
                arg = parts[1];
            }
            City city = null;
            if (commandName.equals("add")
            || commandName.equals("insert_at") || commandName.equals("remove_greater")) {
                city = inputManager.createCity();
            }
            Req req = new Req(arg,city);
            Response response = commandManager.execute(commandName,req);
            if (response != null) {
                System.out.println(response.getMessage());
            }
        }
    }
}
