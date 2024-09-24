package utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ObservableSupport implements Observable {
    private Set<Observer> observers = ConcurrentHashMap.newKeySet();

    @Override
    public void addObserver(Observer obs) {
        this.observers.add(obs);
    }

    @Override
    public void removeObserver(Observer obs) {
        this.observers.remove(obs);
    }

    public void update() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
