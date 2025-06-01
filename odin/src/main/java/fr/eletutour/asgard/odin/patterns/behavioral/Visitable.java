package fr.eletutour.asgard.odin.patterns.behavioral;

public interface Visitable<T> {
    void accept(Visitor<T> visitor);
} 