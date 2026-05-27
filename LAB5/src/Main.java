import managers.CollectionManager;
import managers.CommandManager;
import cli.Console;
import util.InputManager;
import managers.FileManager;

import java.util.Stack;

import models.City;

public class Main {
    public static void main(String[] args) {
        FileManager fileManager = new FileManager("data.json");
        CollectionManager collectionManager = new CollectionManager();
        Stack<City> cities = fileManager.loadCollection();
        collectionManager.setCollection(cities);
        InputManager inputManager = new InputManager();
        CommandManager commandManager = new CommandManager(collectionManager, inputManager);
        Console console = new Console(commandManager, inputManager);
        console.start();

    }
}