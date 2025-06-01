package fr.eletutour.asgard.odin.patterns.creational;

public interface AbstractFactory<T> {
    T create(String type);
} 