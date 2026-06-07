package common.network;
import common.models.City;
import java.io.Serializable;
import java.util.Collection;
public class Response implements Serializable{
    private static final long serialVersionUID = 1L;
    private String message;
    private Collection<City> collection;
    public Response(String message) {
        this.message = message;
    }
    public Response (String message, Collection<City> collection) {
        this.message = message;
        this.collection = collection;
    }
    public String getMessage() {
        return message;
    }
    public Collection <City> getCollection() {return collection; }
 }