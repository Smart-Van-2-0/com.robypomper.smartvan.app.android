package com.robypomper.josp.jsl.android.handlers.base;


/**
 * Base class for JSL Handlers.
 * <p>
 * An handler is a class that provides a set of methods to handle a specific
 * aspect of the JSL library like the communication with the remote object,
 * the state and the action of a component, etc.
 * <p>
 * Basically the handler host the logic used to fetch, to show... an aspect
 * of the JSL library.
 * <p>
 * Examples of handlers usages can be found in the JSL views. That use
 * the handlers to show the remote object's connection status, or to show
 * the component's state and to handle its actions.<br/>
 * Handlers can be combined to provide a complete view of a component.
 * <p>
 * Any handler is strictly linked to a specific observer, so, if you need
 * another observer, you need to create another handler. To help with the
 * creation of a new handler, this class provides a copy method that can be
 * used to copy the extra info from another handler.
 * <p>
 * In order to implement a new handler, it's necessary to extend this class
 * and to implement the specific logic in the new class.<br/>
 * Generally, the new class will have a final observer, a settable observed
 * instance and some extra info. Use the following list as a generic list of
 * properties that a handler can manage:
 * <ul>
 *     <li>observer<br/>
 *     as final property<br/>
 *     definitions of events
 *     </li>
 *     <li>observed instances<br/>
 *     settable in order to recycle the handler<br/>
 *     listeners initialization (via method)<br/>
 *     listeners removal (via finalize)
 *     </li>
 *     <li>extra info<br/>
 *     getters and setters(opt)
 *     copy method
 *     </li>
 * </ul>
 */
public abstract class JSLBaseHandler {

    // Internal vars

    /**
     * Component's observer
     */
    private final Observer observer;


    // Handler observer

    /**
     * Base, empty, observer for the {@link JSLBaseHandler}.
     */
    public interface Observer {
    }

    // Constructors

    /**
     * Create a new handler.
     *
     * @param observer the observer for the handler
     */
    public JSLBaseHandler(Observer observer) {
        this(observer, null);
    }

    /**
     * Create a new handler.
     *
     * @param other the handler to copy the extra info from
     */
    public JSLBaseHandler(Observer observer, JSLBaseHandler other) {
        this.observer = observer;
        if (other != null)
            copy(other);
    }


    // Getters and setters

    /**
     * Get the observer for the handler.
     *
     * @return the observer for the handler
     */
    protected Observer getObserver() {
        return observer;
    }


    // Copy extra info

    /**
     * This method copy the extra info from the given handler to the current
     * handler.
     *
     * @param handler the handler to copy the extra info from
     */
    public abstract void copy(JSLBaseHandler handler);

}


