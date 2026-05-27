package client;

import client.network.ClientNetworkHandler;
import client.utils.InputManager;
import common.models.City;
import common.network.Req;
import common.network.Response;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Client {
    private final ClientNetworkHandler networkHandler;
    private final InputManager inputManager;
    private final Set<String> executingScripts = new HashSet<>();
    public Client() {
        networkHandler = new ClientNetworkHandler("127.0.0.1", 12345);
        inputManager = new InputManager();
    }
    public void start() {
        System.out.println("=== Клиент запущен ===");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                System.out.println("\nCtrl+D detected. Завершение клиента...");
                break;
            }
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            String[] parts = input.split(" ", 2);
            String commandName = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;
            if (commandName.equals("exit")) {
                System.out.println("Завершение работы клиента");
                break;
            }
            if (commandName.equals("execute_script")){
                if(argument == null){
                    System.out.println("Укажите имя файла");
                    continue;
                }
                executeScript(argument);
                continue;
            }
            City city = null;
            if (commandName.equals("add") ||
                    commandName.equals("update") ||
                    commandName.equals("insert_at") ||
                    commandName.equals("remove_greater")) {
                System.out.println("Введите данные города:");
                city = inputManager.createCity();
            }
            Req request = new Req(commandName, city, argument);
            Response response = networkHandler.sendRequest(request);
            printResponse(response);
        }
        networkHandler.close();
    }
    private void executeScript(String filename) {
        if (executingScripts.contains(filename)){
            System.out.println("Ошибка, рекурсивный вызов скрипта" + filename);
            return;
        }
        executingScripts.add(filename);
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                System.out.println("> " + line);
                String[] parts = line.split(" ", 2);
                String commandName = parts[0];
                String argument = parts.length > 1 ? parts[1] : null;
                if (commandName.equals("execute_script")) {
                    if (argument == null) {
                        System.out.println("Укажите имя файла");
                        continue;
                    }
                    executeScript(argument);
                    continue;
                }
                City city = null;
                if (commandName.equals("add") ||
                        commandName.equals("update") ||
                        commandName.equals("insert_at") ||
                        commandName.equals("remove_greater")) {
                    System.out.println("Чтение данных из скрипта");
                    city = inputManager.readCityFromScript(reader);
                    if(city == null){
                        System.out.println("Ошибка чтения города из скрипта, пропускаем команду");
                        continue;
                    }
                }
                Req request = new Req(commandName, city, argument);
                Response response = networkHandler.sendRequest(request);
                printResponse(response);
            }
        } catch (Exception e) {
            System.out.println("Ошибка выполнения скрипта: " + e.getMessage());
        } finally {
            executingScripts.remove(filename);
        }
    }
    private void printResponse(Response response) {
        if (response != null) {
            System.out.println(response.getMessage());
            if (response.getCollection() != null) {
                var sorted = networkHandler.sortByLocation(response.getCollection());
                sorted.forEach(System.out::println);
            }
        } else {
            System.out.println("Сервер временно недоступен");
        }
    }
    public static void main(String[] args) {
        new Client().start();
    }
}