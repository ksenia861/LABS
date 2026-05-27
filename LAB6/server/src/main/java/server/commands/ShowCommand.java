package server.commands;
import common.models.City;
import server.managers.CollectionManager;
import common.network.Req;
import common.network.Response;

import java.util.Comparator;
import java.util.Stack;
import java.util.stream.Collectors;

public class ShowCommand implements Command {
    private CollectionManager collectionManager;
    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    public String getName() { return "show"; }
    public String getDescription() { return "показать все элементы коллекции"; }
    public Response execute(Req req) {
        Stack<City> sorted = collectionManager.getCollection().stream()
            .sorted(Comparator
                    .comparing((City c) -> c.getCoordinates().getX())
                    .thenComparing(c -> c.getCoordinates().getY()))
            .collect(Collectors.toCollection(Stack::new));

        if (sorted.isEmpty()) {
            return new Response("Коллекция пуста");
        }
        return new Response("OK", sorted);
    }
}