package com.robypomper.josp.jsl.android.handlers.base;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;

import java.util.Date;


/**
 * Base handler for {@link JSLState} components that manage component's state.
 * <p>
 * Current handler, do not only notify the observer when the state changes, but
 * also it provides additional information about the state, like the last update
 * date and the old state.
 * <p>
 * When a subclass, detects a state change, it can call the {@link #processStateUpdate(Object, Object)}
 * method, so this base class can update theadditional info and trigger the
 * observer with the /{@link Observer#onStateDataUpdated(JSLBaseStateHandler, Object, Object)}
 * event.
 * <p>
 * Main features:
 * - single observer
 * - support component switch
 * - onComponentChanged
 * - onStateDataUpdated
 * <p>
 * Main properties:
 * - last update date
 * - old state
 *
 * @noinspection unused
 */
public abstract class JSLBaseStateHandler
        extends JSLBaseComponentHandler {


    // Internal vars

    /**
     * Last status update date (from remote object)
     */
    private Date lastUpdate;
    /**
     * Local storage of the component's old status.
     * <p>
     * It's an Object because when the component is not available or current
     * handler did not receive any update yet, it's not possible to know the
     * old status. So it's set to null.
     */
    private Object oldState;


    // Handler observer

    /**
     * Observer for the {@link JSLBooleanState} components handler.
     */
    public interface Observer
            extends JSLBaseComponentHandler.Observer {

        void onStateDataUpdated(JSLBaseStateHandler handler, Object newState, Object oldState);

    }

    // Constructors

    /**
     * Create a new handler for the given component.
     *
     * @param observer  the observer to notify when the state changes
     * @param component the component to handle
     */
    public JSLBaseStateHandler(Observer observer, JSLState component) {
        super(observer, (JSLComponent) component);
    }

    /**
     * Create a new handler from the given one.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param other    the other handler to copy the extra info from and
     *                 the component reference
     */
    public JSLBaseStateHandler(Observer observer, JSLBaseStateHandler other) {
        super(observer, other);
        if (other != null)
            setComponent(other.getComponent());
    }


    // Getters and Setters

    /**
     * Get the observer for this handler.
     *
     * @return the observer for this handler
     */
    @Override
    protected Observer getObserver() {
        return (Observer) super.getObserver();
    }

    /**
     * Get the previous (before last update) state received from the component.
     *
     * @return the previous state received from the component
     */
    public Object getStateOld() {
        return oldState;
    }

    /**
     * Get the last update date.
     *
     * @return the last update date
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Set the last update date to the current date and time.
     * <p>
     * This method also notify the observer that the state data has been updated.
     */
    protected void setLastUpdate(Object newState) {
        lastUpdate = JavaDate.getNowDate();
        notifyDataUpdated(newState);
    }


    // Copy extra info

    /**
     * This method copy the extra info from the given handler to the current
     * handler.
     *
     * @param handler the handler to copy the extra info from
     * @throws IllegalArgumentException if the given handler is not an instance
     *                                  of this class
     */
    @Override
    public void copy(JSLBaseHandler handler) {
        super.copy(handler);
        if (!this.getClass().isInstance(handler))
            throw new IllegalArgumentException("The given handler is not an instance of " + getClass().getName());
        JSLBaseStateHandler other = (JSLBaseStateHandler) handler;
        this.oldState = other.oldState;
        this.lastUpdate = other.lastUpdate;
    }


    // Component listeners' methods

    /**
     * Method called by component listener (added by addListener() method) when
     * the state changes.
     * <p>
     * This method updates the last update date and the old state, and then
     * notifies the observer ({@link Observer#onStateDataUpdated(JSLBaseStateHandler, Object, Object)}).
     *
     * @param newState the new state
     * @param oldState the old state
     */
    protected void processStateUpdate(Object newState, Object oldState) {
        this.oldState = oldState;
        setLastUpdate(newState);
    }

    /**
     * Reset the handler's `oldState` and `lastUpdate`.
     */
    @Override
    public void doReset() {
        oldState = null;
        lastUpdate = null;
    }


    // Notify methods

    /**
     * Notify the observer that the state data has been updated.
     */
    private void notifyDataUpdated(Object newState) {
        if (getComponent() == null) return;
        if (getObserver() == null) return;
        getObserver().onStateDataUpdated(this, newState, oldState);
    }

}


