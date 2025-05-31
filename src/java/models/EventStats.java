package models;

public class EventStats {
    private int spotsLeft;
    private int registeredCount;

    public EventStats(int spotsLeft, int registeredCount) {
        this.spotsLeft = spotsLeft;
        this.registeredCount = registeredCount;
    }

    public int getSpotsLeft() {
        return spotsLeft;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }
}
