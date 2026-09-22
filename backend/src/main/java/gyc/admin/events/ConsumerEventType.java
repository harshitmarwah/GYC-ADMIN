package gyc.admin.events;

/** Types published on {@code consumer.events} by the Consumer backend, consumed here. */
public enum ConsumerEventType {
    REQUEST_SUBMITTED,
    ACTION_COMPLETED,
    SESSION_FLAGGED
}
