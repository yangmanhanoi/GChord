package model;

public class Pair {
    private String key;
    private double value;

    private int resourceId;

    private int imagePos;

    public Pair(String key, double value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getImagePos() {
        return imagePos;
    }

    public void setImagePos(int imagePos) {
        this.imagePos = imagePos;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
