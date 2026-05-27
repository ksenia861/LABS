package models;

public class Coordinates {
    private double x;
    private Double y;

    public Coordinates(double x, Double y){
        if (x <= -501) {
            throw new IllegalArgumentException("х должен быть больше -501");
        }
        if (y == null) {
            throw new IllegalArgumentException("y не может быть null");
        }
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
