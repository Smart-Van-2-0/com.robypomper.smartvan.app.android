package com.robypomper.josp.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A JOSPEventGroup is a JOSPEvent with a counter and a list of ids and
 * emittedAt dates.
 * <p>
 * This class is used to group events with the same type, phase, srcId,
 * srcType, payload and errorPayload.
 */
public class JOSPEventGroup extends JOSPEvent {

    /**
     * The number of events in the group
     */
    private int count = 0;
    /**
     * The list of events IDs
     */
    private final List<Long> ids = new ArrayList<>();
    /**
     * The list of events emittedAt dates
     */
    private final List<Date> emittedAt = new ArrayList<>();


    // Constructors

    /**
     * Create a new JOSPEventGroup with the given event.
     *
     * @param event the event to add to the group.
     */
    public JOSPEventGroup(JOSPEvent event) {
        super(event.getId(), event.getType(), event.getSrcId(), event.getSrcType(), event.getEmittedAt(), event.getPhase(), event.getPayload(), event.getErrorPayload());
        addEvent(event);
    }


    // Getters

    /**
     * Check if the given event can be in the same group of this.
     *
     * @param other the event to check.
     * @return true if the given event can be in the same group of this.
     */
    public boolean canBeInGroup(JOSPEvent other) {
        return other.getType().equals(getType())
                && other.getPhase().equals(getPhase())
                && other.getSrcId().equals(getSrcId())
                && other.getSrcType().equals(getSrcType())
                && other.getPayload().equals(getPayload())
                && other.getErrorPayload().equals(getErrorPayload());
    }

    /**
     * @return the number of events in the group.
     */
    public int getCount() {
        return count;
    }

    /**
     * @return the list of events IDs.
     */
    public List<Long> getIds() {
        return ids;
    }

    /**
     * @return the list of events emittedAt dates.
     */
    public List<Date> getEmittedAtList() {
        return emittedAt;
    }

    /**
     * Add the given event to the group.
     *
     * @param event the event to add.
     */
    public void addEvent(JOSPEvent event) {
        count++;
        this.ids.add(event.getId());
        this.emittedAt.add(event.getEmittedAt());
    }

}
