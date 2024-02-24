package com.robypomper.josp.jsl.android.handlers.base;

import com.robypomper.josp.jsl.objs.structure.JSLComponent;


/**
 * Base handler for {@link JSLComponent} that manage the observer and the
 * component's reference.
 * <p>
 * For convenience, this handler provides also abstract methods to let the
 * subclasses to add and remove the listener to the component during the component
 * switch (or initialization/finalization).
 * In addition, this class define the {@link #doReset()} method, called on
 * internal component switch that allows the subclasses to reset his own data.
 * <p>
 * Main features:
 * - single observer
 * - support component switch
 * - onComponentChanged
 *
 * @noinspection unused
 */
public abstract class JSLBaseComponentHandler
        extends JSLBaseHandler {

    // Internal vars

    /**
     * The component to show, it's not final to allows view recycling.
     */
    private JSLComponent component;


    // Handler observer

    /**
     * Observer for the {@link JSLBaseComponentHandler} components handler.
     */
    public interface Observer extends JSLBaseHandler.Observer {

        void onComponentChanged(JSLBaseComponentHandler handler, JSLComponent newComp, JSLComponent oldComp);

    }

    // Constructors

    /**
     * Create a new handler for the given component.
     *
     * @param component the component to handle
     */
    public JSLBaseComponentHandler(Observer observer, JSLComponent component) {
        super(observer);
        setComponent(component);
    }

    /**
     * Create a new handler from the given one.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param other    the other handler to copy the extra info from and
     *                 the component reference
     */
    public JSLBaseComponentHandler(Observer observer, JSLBaseComponentHandler other) {
        super(observer, other);
        if (other != null)
            setComponent(other.getComponent());
    }

    /**
     * Deregister the listener and clean the component reference.
     *
     * @throws Throwable the Exception raised by this method
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (component != null) {
            doRemoveListener();
            component = null;
        }
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
    public JSLComponent getComponent() {
        return component;
    }

    /**
     * Set the component to manage.
     * <p>
     * This method also manage the listener (owned by subclasses) to the component,
     * and resets the handler's data (also owned by subclasses).
     * <p>
     * Then, when everything is done, it notifies the observer
     * ({@link Observer#onComponentChanged(JSLBaseComponentHandler, JSLComponent, JSLComponent)}).
     *
     * @param newComp the component to manage
     */
    public void setComponent(JSLComponent newComp) {
        if (getComponent() == newComp) return;

        // clean
        JSLComponent oldComp = getComponent();
        if (oldComp != null)
            doRemoveListener();

        // set
        this.component = newComp;

        // configure
        if (newComp != null)
            doAddListener();

        // notify
        notifyComponentChanged(oldComp);
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
        if (!this.getClass().isInstance(handler))
            throw new IllegalArgumentException("The given handler is not an instance of " + getClass().getName());
        JSLBaseComponentHandler other = (JSLBaseComponentHandler) handler;
        // N/A
    }


    // Component listeners

    /**
     * Reset the handler.
     * <p>
     * This method is called when the component is unset and must be implemented
     * by the subclasses to reset his own (component's related) data
     */
    protected abstract void doReset();

    /**
     * Add the listener to the component.
     * <p>
     * This method is called when the component is set and must be implemented
     * by the subclasses to attach his own listener to the component.
     */
    protected abstract void doAddListener();

    /**
     * Remove the listener from the component.
     * <p>
     * This method is called when the component is unset and must be implemented
     * by the subclasses to detach his own listener from the component.
     */
    protected abstract void doRemoveListener();


    // Notify methods

    /**
     * Notify the observer that the component has changed.
     *
     * @param oldComp the old component
     */
    private void notifyComponentChanged(JSLComponent oldComp) {
        if (getObserver() == null) return;
        getObserver().onComponentChanged(this, getComponent(), oldComp);
    }

}


