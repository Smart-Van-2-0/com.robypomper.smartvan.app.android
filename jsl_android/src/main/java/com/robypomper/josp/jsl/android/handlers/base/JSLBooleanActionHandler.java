package com.robypomper.josp.jsl.android.handlers.base;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;


/**
 * Handler for {@link JSLBooleanAction} components based on {@link JSLBaseActionHandler}.
 * <p>
 * As subclass of {@link JSLBaseActionHandler}, this handler implements methods
 * to send actions to the remote object and to manage the feedback.
 * <p>
 * Main features:
 * - single observer
 * - support component switch
 * - onComponentChanged
 * - onStarted
 * - onTimeout
 * - onFeedbackReceived
 * - settable timeout duration
 * <p>
 * Main properties
 * - is waiting for feedback
 * - last action sent date
 * - last feedback received date
 * - last timeout date
 */

/**
 * Handler for {@link JSLBooleanAction} components that manage commands feedback.
 * <p>
 * As subclass of {@link JSLBaseActionHandler}, this handler sets a timeout
 * for actions sent.
 * <p>
 * Main features:
 * - sendSwitch, sendTrue and sendFalse methods
 * - addListener and removeListener methods
 * - onStateChangeListener listener for {@link JSLBooleanAction} component
 *
 * @noinspection unused
 * @noinspection Convert2Lambda
 */
public class JSLBooleanActionHandler
        extends JSLBaseActionHandler {

    // Constants

    public enum ACTIONS {TRUE, FALSE, SWITCH}


    // Handler observer

    /**
     * Observer for the {@link JSLBooleanAction} components handler.
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
    public JSLBooleanActionHandler(Observer observer, JSLBooleanAction component) {
        this(observer, component, DEF_TIMEOUT_MS);
    }

    /**
     * Create a new handler for the given component.
     *
     * @param observer  the observer to notify when the state changes
     * @param component the component to handle
     * @param timeoutMs the timeout for the commands
     */
    public JSLBooleanActionHandler(Observer observer, JSLBooleanAction component, long timeoutMs) {
        super(observer, component, timeoutMs);
    }

    /**
     * Create a new handler from the given one.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param other    the other handler to copy the extra info from and
     *                 the component reference
     */
    public JSLBooleanActionHandler(Observer observer, JSLBooleanActionHandler other) {
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
    public JSLBooleanAction getComponent() {
        return (JSLBooleanAction) super.getComponent();
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
    public void setComponent(JSLBooleanAction newComp) {
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
        JSLBooleanActionHandler other = (JSLBooleanActionHandler) handler;
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
        if (actionId == ACTIONS.TRUE.ordinal())
            return ACTIONS.TRUE.name();
        else if (actionId == ACTIONS.FALSE.ordinal())
            return ACTIONS.FALSE.name();
        else if (actionId == ACTIONS.SWITCH.ordinal())
            return ACTIONS.SWITCH.name();
        else
            return "UNKNOWN";
    }


    // Send command methods

    /**
     * Send set true command to the component to the remote object.
     *
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendTrue() {
        try {
            sendTrue();
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send set true command to the component to the remote object.
     *
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendTrue() throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.TRUE.ordinal());
        getComponent().execSetTrue();
    }

    /**
     * Send set false command to the component to the remote object.
     *
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendFalse() {
        try {
            sendFalse();
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send set false command to the component to the remote object.
     *
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendFalse() throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.FALSE.ordinal());
        getComponent().execSetFalse();
    }

    /**
     * Send switch command to the component to the remote object.
     *
     * @return true if the command was sent, false if the component is not set
     * @throws IllegalStateException if the component is not set or current
     *                               handler is already waiting for a feedback
     */
    public boolean trySendSwitch() {
        try {
            sendSwitch();
            return true;
        } catch (JSLRemoteObject.ObjectNotConnected | JSLRemoteObject.MissingPermission e) {
            return false;
        }
    }

    /**
     * Send switch state command to the component to the remote object.
     *
     * @throws IllegalStateException              if the component is not set or current
     *                                            handler is already waiting for a feedback
     * @throws JSLRemoteObject.ObjectNotConnected if the remote object is not connected
     * @throws JSLRemoteObject.MissingPermission  if current service/user has not
     *                                            the permission to execute the command
     */
    public void sendSwitch() throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        if (getComponent() == null) throw new IllegalStateException("Component not set");
        startTimeoutThread(ACTIONS.SWITCH.ordinal());
        getComponent().execSwitch();
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
     * The listener for the {@link JSLBooleanAction} component state changes.
     * <p>
     * It can not be final because it's initialized in the addListener() method,
     * called by the super class constructors, when the listener is not yet
     * initialized.
     */
    private JSLBooleanState.BooleanStateListener onStateChangeListener;

    /**
     * Initialize the listener for the component state changes.
     * <p>
     * This method is used to initialize the listener during super class
     * initialization, when it's not yet initialized.
     */
    private void initOnStateChangeListener() {
        onStateChangeListener = new JSLBooleanState.BooleanStateListener() {
            @Override
            public void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState) {
                processStateUpdate(newState, oldState);
            }
        };
    }

}
