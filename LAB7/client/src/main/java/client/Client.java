package client;

import client.network.ClientNetworkHandler;
import client.utils.InputManager;
import common.models.City;
import common.network.Req;
import common.network.Response;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Client {
    private final ClientNetworkHandler networkHandler;
    private final InputManager inputManager;
    private final Set<String> executingScripts = new HashSet<>();
    private String currentLogin = null;
    private String currentPassword = null;
    private boolean isRunning = true;

    public Client() {
        networkHandler = new ClientNetworkHandler("127.0.0.1", 12345);
        inputManager = new InputManager();
    }
    public void start() {
        System.out.println("=== Клиент запущен ===");
        Scanner scanner = new Scanner(System.in);

        authenticateUser(scanner);

        if (currentLogin == null) {
            System.out.println("Авторизация не пройдена. Завершение работы.");
            return;
        }

        System.out.println("Вы успешно авторизованы! Введите команду (help для справки):");

        while (isRunning) {
            System.out.print("> ");
            String input = readLineSafe(scanner);

            if (input == null) {
                System.out.println("\nCtrl+D получен. Завершение клиента...");
                isRunning = false;
                continue;
            }
            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 2);
            String commandName = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;

            if (commandName.equals("exit")) {
                System.out.println("Завершение работы клиента");
                isRunning = false;
                continue;
            }
            if (commandName.equals("execute_script")) {
                if (argument == null) {
                    System.out.println("Укажите имя файла");
                    continue;
                }
                executeScript(argument);
                continue;
            }

            City city = null;
            if (commandName.equals("add") || commandName.equals("update") ||
                    commandName.equals("insert_at") || commandName.equals("remove_greater")) {
                System.out.println("Введите данные города:");
                city = inputManager.createCity();
            }

            Req request = new Req(commandName, city, argument, currentLogin, currentPassword);
            Response response = networkHandler.sendRequest(request);
            printResponse(response);
        }
        networkHandler.close();
    }

    private void authenticateUser(Scanner scanner) {
        while (currentLogin == null) {
            System.out.println("\n1 - Войти\n2 - Зарегистрироваться");
            System.out.print("Выберите действие: ");
            String choice = readLineSafe(scanner);
            if (choice == null) {
                System.out.println("\nCtrl+D получен во время авторизации. Завершение...");
                isRunning = false;
                return;
            }
            if (!choice.equals("1") && !choice.equals("2")) {
                System.out.println("Неверный выбор!");
                continue;
            }

            System.out.print("Введите логин: ");
            String login = readLineSafe(scanner);
            if (login == null) { isRunning = false; return; }

            System.out.print("Введите пароль: ");
            String password = readLineSafe(scanner);
            if (password == null) { isRunning = false; return; }

            if (login.isEmpty() || password.isEmpty()) {
                System.out.println("Логин и пароль не могут быть пустыми!");
                continue;
            }

            String commandName = choice.equals("1") ? "login" : "register";
            Req authRequest = new Req(commandName, null, null, login, password);

            System.out.println("Отправка запроса на сервер...");
            Response response = networkHandler.sendRequest(authRequest);

            if (response != null && response.getMessage().toLowerCase().contains("успешно")) {
                System.out.println(response.getMessage());
                this.currentLogin = login;
                this.currentPassword = password;
            } else if (response != null) {
                System.out.println("Ошибка: " + response.getMessage());
            } else {
                System.out.println("Сервер недоступен (таймаут или сервер не запущен). Проверьте соединение.");
                isRunning = false;
                return;
            }
        }
    }
    private String readLineSafe(Scanner scanner) {
        try {
            if (!scanner.hasNextLine()) {
                return null;
            }
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            return null;
        }
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
                Req request = new Req(commandName, city, argument, currentLogin, currentPassword);
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