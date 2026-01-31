package fr.kosmosuniverse.kuffle.utils;

/**
 * @author KosmosUniverse
 */
@FunctionalInterface
public interface Function3arity<A, B, C> {
    void apply(A a, B b, C c) throws IllegalAccessException;
}
