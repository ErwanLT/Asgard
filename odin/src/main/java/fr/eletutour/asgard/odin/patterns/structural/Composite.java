package fr.eletutour.asgard.odin.patterns.structural;

import java.util.List;

public interface Composite<T> {
    void add(T component);
    void remove(T component);
    List<T> getChildren();
    void operation();
} 