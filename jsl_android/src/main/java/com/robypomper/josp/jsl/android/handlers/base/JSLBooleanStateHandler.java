package com.robypomper.josp.jsl.android.handlers.base;

import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;


/**
 * Handler for {@link JSLBooleanState} components based on {@link JSLBaseStateHandler}.
 * <p>
 * As subclass of {@link JSLBaseStateHandler}, this handler notifies the superclass
 * when the state changes using the `processStateUpdate(Object, Object)` method.
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
 */
public class JSLBooleanStateHandler extends JSLBaseStateHandler {

    // Handler observer

    /**
     * Observer for the {@link JSLBooleanState} components handler.
     */
    public interface Observer extends JSLBaseStateHandler.Observer {
    }


    // Constructors

    /**
     * Create a new handler for the given component.
     *
     * @param observer  the observer to notify when the state changes
     * @param component the component to handle
     */
    public JSLBooleanStateHandler(Observer observer, JSLBooleanState component) {
        super(observer, component);
    }

    /**
     * Create a new handler from the given one.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param other    the other handler to copy the extra info from and
     *                 the component reference
     */
    public JSLBooleanStateHandler(Observer observer, JSLBooleanStateHandler other) {
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
    public JSLBooleanState getComponent() {
        return (JSLBooleanState) super.getComponent();
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
    public void setComponent(JSLBooleanState newComp) {
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
        JSLBooleanStateHandler other = (JSLBooleanStateHandler) handler;
        // N/A
    }


    // Component listeners' methods

    /**
     * Add the listener to the component.
     * <p>
     * This method is called when the component is set from the super class.
     */
    @Override
    protected void doAddListener() {
        if (onStateChangeListener == null) initOnStateChangeListener();
        getComponent().addListener(onStateChangeListener);
    }

    /**
     * Remove the listener from the component.
     * <p>
     * This method is called when the component is reset from the super class.
     */
    @Override
    protected void doRemoveListener() {
        if (getComponent() == null) return;
        getComponent().removeListener(onStateChangeListener);
    }


    // Component Listeners

    /**
     * The listener for the {@link JSLBooleanState} component state changes.
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
