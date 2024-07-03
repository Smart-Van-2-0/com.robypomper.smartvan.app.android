package com.robypomper.josp.jsl.android.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.view.JSLBooleanStateViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;


/**
 * View to show a {@link JSLBooleanState} components.
 * <p>
 * Handlers:
 * - stateHandler (`JSLBooleanStateViewHandler`): the handler for the component's state.
 *
 * @noinspection unused
 */
public class JSLBooleanStateView
        extends JSLBaseComponentView {

    // Constants

    /**
     * The default layout to inflate for the view.
     */
    public final static @LayoutRes int DEF_LAYOUT = R.layout.view_jsl_boolean_state;


    // Internal vars

    /**
     * The handler for the component's state.
     */
    private final JSLBooleanStateViewHandler stateHandler;


    // Constructors

    /**
     * Create a new `JSLBooleanStateView` instance.
     *
     * @param context the context
     */
    public JSLBooleanStateView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Create a new `JSLBooleanStateView` instance.
     *
     * @param context the context
     * @param attrs   the attributes
     */
    public JSLBooleanStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Create a new `JSLBooleanStateView` instance.
     *
     * @param context      the context
     * @param attrs        the attributes
     * @param defStyleAttr the default style attribute
     */
    public JSLBooleanStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, DEF_LAYOUT);
    }

    /**
     * Create a new `JSLBooleanStateView` instance.
     *
     * @param context   the context
     * @param component the component
     * @param layout    the layout
     */
    public JSLBooleanStateView(@NonNull Context context, JSLBooleanState component, @LayoutRes int layout) {
        this(context, null, component, layout);
    }

    /**
     * Create a new `JSLBooleanStateView` instance.
     *
     * @param context   the context
     * @param attrs     the attributes
     * @param component the component
     * @param layout    the layout
     */
    public JSLBooleanStateView(@NonNull Context context, @Nullable AttributeSet attrs, JSLBooleanState component, @LayoutRes int layout) {
        this(context, attrs, 0, component, layout);
    }

    /**
     * Create a new `JSLBooleanStateView` instance.
     *
     * @param context      the context
     * @param attrs        the attributes
     * @param defStyleAttr the default style attribute
     * @param component    the component
     * @param layout       the layout
     */
    public JSLBooleanStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, JSLBooleanState component, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr, component, layout);
        stateHandler = new JSLBooleanStateViewHandler(getContext(), this, getComponent());
    }


    // Subclass implementation

    /**
     * Update the handlers.
     *
     * @param newComp the new component
     * @param oldComp the old component
     */
    protected void updateHandlers(JSLComponent newComp, JSLComponent oldComp) {
        stateHandler.setComponent(newComp);
    }

    /**
     * Refresh the view.
     * <p>
     * This method is used to update the view when the component's state is changed.
     * <p>
     * It, like his super-implementations, calls the local handler to update the UI.
     */
    protected void refresh() {
        super.refresh();
        getStateHandler().updateUI();
    }


    // Getters and Setters

    /**
     * Get the component managed by this handler.
     *
     * @return the component managed by this handler
     */
    public JSLBooleanState getComponent() {
        return (JSLBooleanState) super.getComponent();
    }

    /**
     * Set the component handled by this view and update all handlers,
     * including subclasses' handlers.
     *
     * @param newComp the component to manage
     */
    public void setComponent(JSLBooleanState newComp) {
        super.setComponent(newComp);
    }

    /**
     * Get the state handler.
     *
     * @return the state handler
     */
    public JSLBooleanStateViewHandler getStateHandler() {
        return stateHandler;
    }

}
