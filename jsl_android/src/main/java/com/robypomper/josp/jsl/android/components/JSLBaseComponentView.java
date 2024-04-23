package com.robypomper.josp.jsl.android.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robypomper.josp.jsl.android.handlers.view.JSLRemoteObjectCommunicationViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;


/**
 * Base class for {@link JSLComponent} View.
 * <p>
 * As base class, it provides the communication status (about the component's
 * remote object) handler, the state/action handler and the component's management.
 * <p>
 * More over, it inflate the given layout (via constructor) and define the
 * {#link #updateHandlers(JSLComponent, JSLComponent)} method, that must be
 * implemented by subclasses to update their handlers when the component is
 * changed.
 * <p>
 * // TODO add error handling method that can be used also by subclasses
 */
public abstract class JSLBaseComponentView
        extends LinearLayout {

    // Internal vars

    /**
     * View handler for the object's communication status.
     * <p>
     * It's used to enable/disable the view when the object is connected/disconnected.
     */
    private final JSLRemoteObjectCommunicationViewHandler remObjCommHandler;
    /**
     * The component shown by this view.
     */
    private JSLComponent component;                          // TODO convert to view's attribute (string > comp's path)


    // Constructors

    /**
     * Create a new SVComponentView.
     *
     * @param context   the context
     * @param layout    the layout used by this view
     * @param component the component handled by this view
     */
    public JSLBaseComponentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, JSLComponent component, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr);
        this.component = component;

        // Inflate ui
        inflate(context, layout, this);
        setBackgroundColor(Color.argb(0, 0, 0, 0));

        // Init handlers
        remObjCommHandler = new JSLRemoteObjectCommunicationViewHandler(context, this, component != null ? component.getRemoteObject() : null);

        // Parse attributes
        // TODO add view's attributes management
    }


    // Subclasses abstract methods

    /**
     * Method to allow subclasses to register the component to their handlers.
     * <p>
     * When this method is executed, the new component is already set.
     *
     * @param newComp the new component
     * @param oldComp the old component
     */
    protected abstract void updateHandlers(JSLComponent newComp, JSLComponent oldComp);

    /**
     * Refresh the view.
     * <p>
     * This method is used to update the view when the component's state is changed.
     * <p>
     * It, like his super-implementations, calls the local handler to update the UI.
     */
    protected void refresh() {
        remObjCommHandler.updateUI();
    }


    // Getters and Setters

    /**
     * @return the component handled by this view
     */
    public JSLComponent getComponent() {
        return component;
    }

    /**
     * Set the component handled by this view and update all handlers,
     * including subclasses' handlers.
     * <p>
     * This method also manage the listener (owned by subclasses) to the component,
     * and resets the handler's data (also owned by subclasses).
     *
     * @param newComp the new component
     */
    public void setComponent(JSLComponent newComp) {
        if (getComponent() == newComp) return;

        JSLComponent oldComp = getComponent();
        component = newComp;

        remObjCommHandler.setRemoteObject(newComp != null ? newComp.getRemoteObject() : null);
        updateHandlers(newComp, oldComp);
    }

    /**
     * @return the object's communication status handler
     */
    public JSLRemoteObjectCommunicationViewHandler getRemObjCommHandler() {
        return remObjCommHandler;
    }

}
