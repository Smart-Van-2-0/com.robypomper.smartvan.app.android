package com.robypomper.josp.jsl.android.handlers.base;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;


/**
 * Handler for {@link JSLRangeAction} components that manage commands feedback.
 * <p>
 * As subclass of {@link JSLBaseActionHandler}, this handler sets a timeout
 * for actions sent.
 * <p>
 * Main features:
 * - sendValue, sendDecrease, sendIncrease, sendMin and sendMax methods
 * - addListener and removeListener methods
 * - onStateChangeListener listener for {@link JSLRangeAction} component
 *
 * @noinspection unused
 */
public class JSLRangeActionHandler
        extends JSLBaseActionHandler {

    // Constants

    public enum ACTIONS {SET, DECREASE, INCREASE, MIN, MAX}

    // Handler observer

    /**
     * Observer for the {@link JSLRangeAction} components handler.
     */
    public interface Observer extends JSLBaseActionHandler.Observer {
    }


    // Constructors

    /**
     * Create a new handler for the given component.
     *
     * @param observer  the observer to notify when the state changes
     * @param component the component to handle
     */
    public JSLRangeActionHandler(Observer observer, JSLRangeAction component) {
        this(observer, component, DEF_TIMEOUT_MS);
    }

    /**
     * Create a new handler for the given component.
     *
     * @param observer  the observer to notify when the state changes
     * @param component the component to handle
     * @param timeoutMs the timeout for the commands
     */
    public JSLRangeActionHandler(Observer observer, JSLRangeAction component, long timeoutMs) {
        super(observer, component, timeoutMs);
    }

    /**
     * Create a new handler from the given one.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param other    the other handler to copy the extra info from and
     *                 the component reference
     */
    public JSLRangeActionHandler(Observer observer, JSLRangeActionHandler other) {
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
     * Get the component managed by this handler.
     *
     * @return the component managed by this handler
     */
    public JSLRangeAction getComponent() {
        return (JSLRangeAction) super.getComponent();
    }

    /**
     * Set the component to manage.
     * <p>
     * This method also manage the listener (owned by subclasses) to the component,
     * and resets the handler's data (also owned by subclasses).
     * <p>
     * Then, when everything is done, it notifies the observer
     * ({@link JSLBaseComponentHandler.Observer#onComponentChanged(JSLBaseComponentHandler, JSLComponent, JSLComponent)}).
     *
     * @param newComp the component to manage
     */
    public void setComponent(JSLRangeAction newComp) {
        super.setComponent(newComp);
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
        JSLRangeActionHandler other = (JSLRangeActionHandler) handler;
        // N/A
    }


    // Action id conversion

    /**
     * Convert the action id to a string using the {@link ACTIONS} enum.
     *
     * @param actionId the action id
     * @return the action id as a string
     */
    public String convertActionId(int actionId) {
        if (actionId == ACTIONS.SET.ordinal())
            return ACTIONS.SET.name();
        else if (actionId == ACTIONS.DECREASE.ordinal())
            return ACTIONS.DECREASE.name();
        else if (actionId == ACTIONS.INCREASE.ordinal())
            return ACTIONS.INCREASE.name();
        else if (actionId == ACTIONS.MIN.ordinal())
            return ACTIONS.MIN.name();
        else if (actionId == ACTIONS.MAX.ordinal())
            return ACTIONS.MAX.name();
        else
            return "UNKNOWN";
    }


    // Send command methods

    /**
     * Send set value command to the component to the remote object.
     *
     * @param value the value to set
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendValue(double value) {
        try {
            sendValue(value);
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send set value command to the component to the remote object.
     *
     * @param value the value to set
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendValue(double value) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.SET.ordinal());
        getComponent().execSetValue(value);
    }

    /**
     * Send set decrease command to the component to the remote object.
     *
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendDecrease() {
        try {
            sendDecrease();
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send set decrease command to the component to the remote object.
     *
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendDecrease() throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.DECREASE.ordinal());
        getComponent().execDecrease();
    }

    /**
     * Send set increase command to the component to the remote object.
     *
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendIncrease() {
        try {
            sendIncrease();
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send set increase command to the component to the remote object.
     *
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendIncrease() throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.INCREASE.ordinal());
        getComponent().execIncrease();
    }

    /**
     * Send set min value command to the component to the remote object.
     *
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendMin() {
        try {
            sendMin();
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send set min value command to the component to the remote object.
     *
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendMin() throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.MIN.ordinal());
        getComponent().execSetMin();
    }

    /**
     * Send set max value command to the component to the remote object.
     *
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendMax() {
        try {
            sendMax();
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send set max value command to the component to the remote object.
     *
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendMax() throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.MAX.ordinal());
        getComponent().execSetMax();
    }


    // Component listeners' methods

    /**
     * Add the listener to the component.
     * <p>
     * This method is called when the component is set from the super class.
     */
    protected void doAddListener() {
        if (onStateChangeListener == null) initOnStateChangeListener();
        getComponent().addListener(onStateChangeListener);
    }

    /**
     * Remove the listener from the component.
     * <p>
     * This method is called when the component is reset from the super class.
     */
    protected void doRemoveListener() {
        getComponent().removeListener(onStateChangeListener);
    }


    // Component Listeners

    /**
     * The listener for the {@link JSLRangeAction} component state changes.
     * <p>
     * It can not be final because it's initialized in the addListener() method,
     * called by the super class constructors, when the listener is not yet
     * initialized.
     */
    private JSLRangeState.RangeStateListener onStateChangeListener;

    /**
     * Initialize the listener for the component state changes.
     * <p>
     * This method is used to initialize the listener during super class
     * initialization, when it's not yet initialized.
     */
    private void initOnStateChangeListener() {
        onStateChangeListener = new JSLRangeState.RangeStateListener() {

            @Override
            public void onStateChanged(JSLRangeState component, double newState, double oldState) {
                processStateUpdate(newState, oldState);
            }

            @Override
            public void onMinReached(JSLRangeState component, double state, double min) {
            }

            @Override
            public void onMaxReached(JSLRangeState component, double state, double max) {
            }

        };
    }

}
