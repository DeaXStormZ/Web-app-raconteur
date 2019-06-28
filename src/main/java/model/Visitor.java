package model;

public class Visitor {
    private final History currentHistory;

    public Visitor(History currentHistory) {
        this.currentHistory = currentHistory;
    }

    public History getCurrentHistory() {
        return currentHistory;
    }
}
