package models;
import java.time.LocalDateTime;
public class Human {
    private LocalDateTime birthday;
    public Human(LocalDateTime birthday) {
        this.birthday = birthday;
    }
    public LocalDateTime getBirthday() {
        return birthday;
    }
    @Override
    public String toString() {
        return "Human{birthday=" + birthday + "}";
    }
}