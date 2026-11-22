package model;

/**
 * Interface: anything that can be cancelled (Event, Registration).
 * Added during coding so two unrelated classes share one behaviour.
 */
public interface Cancellable {
    boolean cancel(String reason);
    boolean isCancelled();
}
