package fr.eletutour.asgard.odin.patterns.creational;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Implémentation du pattern Object Pool utilisant la réflexion.
 * Permet de réutiliser des objets coûteux à créer.
 */
public class ReflectiveObjectPool<T> {
    private final Queue<T> pool;
    private final Class<T> objectClass;
    private final AtomicInteger created;
    private final AtomicInteger borrowed;
    private final int maxSize;
    private final Consumer<T> resetFunction;

    /**
     * Constructeur du pool d'objets.
     *
     * @param objectClass La classe des objets à gérer
     * @param maxSize La taille maximale du pool
     * @param resetFunction La fonction pour réinitialiser un objet
     */
    public ReflectiveObjectPool(Class<T> objectClass, int maxSize, Consumer<T> resetFunction) {
        if (objectClass == null) {
            throw new NullPointerException("La classe ne peut pas être null");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("La taille maximale doit être positive");
        }
        this.objectClass = objectClass;
        this.maxSize = maxSize;
        this.resetFunction = resetFunction;
        this.pool = new ConcurrentLinkedQueue<>();
        this.created = new AtomicInteger(0);
        this.borrowed = new AtomicInteger(0);
    }

    /**
     * Emprunte un objet du pool.
     *
     * @return Un objet du pool
     */
    public T borrow() {
        T object = pool.poll();
        if (object == null && created.get() < maxSize) {
            object = createNewObject();
        }
        if (object != null) {
            borrowed.incrementAndGet();
        }
        return object;
    }

    /**
     * Retourne un objet au pool.
     *
     * @param object L'objet à retourner
     */
    public void release(T object) {
        if (object != null) {
            if (resetFunction != null) {
                resetFunction.accept(object);
            }
            pool.offer(object);
            borrowed.decrementAndGet();
        }
    }

    /**
     * Crée un nouvel objet.
     *
     * @return Le nouvel objet créé
     */
    private T createNewObject() {
        try {
            Constructor<T> constructor = objectClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T object = constructor.newInstance();
            created.incrementAndGet();
            return object;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
            throw new RuntimeException("Erreur lors de la création d'un nouvel objet", e);
        }
    }

    /**
     * Obtient le nombre d'objets créés.
     *
     * @return Le nombre d'objets créés
     */
    public int getCreatedCount() {
        return created.get();
    }

    /**
     * Obtient le nombre d'objets empruntés.
     *
     * @return Le nombre d'objets empruntés
     */
    public int getBorrowedCount() {
        return borrowed.get();
    }

    /**
     * Obtient le nombre d'objets disponibles.
     *
     * @return Le nombre d'objets disponibles
     */
    public int getAvailableCount() {
        return pool.size();
    }

    /**
     * Vérifie si le pool est vide.
     *
     * @return true si le pool est vide, false sinon
     */
    public boolean isEmpty() {
        return pool.isEmpty();
    }

    /**
     * Vérifie si le pool est plein.
     *
     * @return true si le pool est plein, false sinon
     */
    public boolean isFull() {
        return created.get() >= maxSize;
    }
} 