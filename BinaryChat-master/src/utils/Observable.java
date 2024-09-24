package utils;
public interface Observable {
    void addObserver(Observer obs);
    void removeObserver(Observer obs);
}
