package fr.eletutour.asgard.odin.patterns.behavioral;

public class VisitableWrapper<T> implements Visitable<T> {
    private final T wrappedObject;

    public VisitableWrapper(T object) {
        this.wrappedObject = object;
    }

    @Override
    public void accept(Visitor<T> visitor) {
        visitor.visit(wrappedObject);
    }

    public T getWrappedObject() {
        return wrappedObject;
    }
} 