package server;

import server.managers.CollectionManager;
import server.managers.CommandManager;
import server.managers.FileManager;
import server.network.ServerNetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    public static void main(String[] args) {
        logger.info("=== Запуск сервера ===");
        String fileName = System.getenv("COLLECTION_FILE");
        if (fileName == null) {
            fileName = "data.json";
        }
        FileManager fileManager = new FileManager(fileName);
        CollectionManager collectionManager = new CollectionManager(fileManager);
        CommandManager commandManager = new CommandManager(collectionManager);
        ServerNetworkHandler networkHandler = new ServerNetworkHandler(collectionManager, commandManager);
        Thread networkThread = new Thread(networkHandler);
        networkThread.start();
        Scanner scanner = new Scanner(System.in);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Получен сигнал завершения работы");
            collectionManager.save();
            logger.info("Коллекция сохранена");
        }));
        logger.info("Сервер запущен. Введите команды (save, exit):");
        while (true) {
            if (!scanner.hasNextLine()) {
                logger.info("EOF (Ctrl+D) получен. Завершение сервера...");
                collectionManager.save();
                networkHandler.close();
                break;
            }
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                logger.info("Команда exit получена в консоли сервера");
                collectionManager.save();
                networkHandler.close();
                break;
            } else if (input.equals("save")) {
                collectionManager.save();
                logger.info("Коллекция сохранена по команде из консоли");
            } else {
                logger.warn("Неизвестная команда сервера: {}", input);
            }
        }
        logger.info("Сервер остановлен");
    }
}