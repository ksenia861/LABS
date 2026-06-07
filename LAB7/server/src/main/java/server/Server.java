package server;

import server.managers.CollectionManager;
import server.managers.CommandManager;
import server.managers.DatabaseManager;
import server.network.ServerNetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        logger.info("=== Запуск сервера ===");
        String dbUrl = "jdbc:postgresql://pg:5432/studs";
        String dbUser = "s527929";
        String dbPass = "0tx07kquP8fcN3Pz";


        DatabaseManager dbManager = new DatabaseManager(dbUrl, dbUser, dbPass);
        dbManager.connect();

        CollectionManager collectionManager = new CollectionManager(dbManager);
        CommandManager commandManager = new CommandManager(collectionManager, dbManager);
        ServerNetworkHandler networkHandler = new ServerNetworkHandler(collectionManager, commandManager);
        Thread networkThread = new Thread(networkHandler);
        networkThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Получен сигнал завершения работы");
            networkHandler.close();
        }));
        logger.info("Сервер запущен. Введите команды (exit): ");

        Scanner scanner = new Scanner(System.in);
        while (isRunning && !scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                logger.info("Команда exit получена в консоли сервера");
                isRunning = false;
                networkHandler.close();
                break;
            } else {
                logger.warn("Неизвестная команда сервера: {}", input);
            }
        }
        logger.info("Сервер недоступен");

    }
}