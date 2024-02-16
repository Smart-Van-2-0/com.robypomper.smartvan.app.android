package com.robypomper.josp.jsl.android.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.view.JSLBooleanActionViewHandler;
import com.robypomper.josp.jsl.android.handlers.view.JSLBooleanStateViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;


/**
 * View to show a {@link JSLBooleanAction} components.
 * <p>
 * Handlers:
 * - stateHandler (`JSLRangeStateViewHandler`): the handler for the component's state.
 * - actionHandler (`JSLRangeActionViewHandler`): the handler for the component's action.
 *
 * @noinspection unused
 */
public class JSLBooleanActionView
        extends JSLBaseComponentView {

    // Constants

    /**
     * The default layout to inflate for the view.
     */
    public final static @LayoutRes int DEF_LAYOUT = R.layout.view_jsl_boolean_action;
    /**
     * The default value for the `isHighVoltage` field.
     */
    public final static boolean DEF_IS_HIGH_VOLTAGE = false;


    // Internal vars

    /**
     * The handler for the component's state.
     */
    private final JSLBooleanStateViewHandler stateHandler;
    /**
     * The handler for the component's action.
     */
    private final JSLBooleanActionViewHandler actionHandler;


    // Constructors

    /**
     * Create a new `JSLBooleanActionView` instance.
     *
     * @param context the context
     */
    public JSLBooleanActionView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Create a new `JSLBooleanActionView` instance.
     *
     * @param context the context
     * @param attrs   the attributes
     */
    public JSLBooleanActionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Create a new `JSLBooleanActionView` instance.
     *
     * @param context      the context
     * @param attrs        the attributes
     * @param defStyleAttr the default style attribute
     */
    public JSLBooleanActionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, DEF_LAYOUT);
    }

    /**
     * Create a new `JSLBooleanActionView` instance.
     *
     * @param context   the context
     * @param component the component
     * @param layout    the layout
     */
    public JSLBooleanActionView(@NonNull Context context, JSLBooleanAction component, @LayoutRes int layout) {
        this(context, null, component, layout);
    }

    /**
     * Create a new `JSLBooleanActionView` instance.
     *
     * @param context   the context
     * @param attrs     the attributes
     * @param component the component
     * @param layout    the layout
     */
    public JSLBooleanActionView(@NonNull Context context, @Nullable AttributeSet attrs, JSLBooleanAction component, @LayoutRes int layout) {
        this(context, attrs, 0, component, layout);
    }

    /**
     * Create a new `JSLBooleanActionViewInternal` instance.
     *
     * @param context      the context
     * @param attrs        the attributes
     * @param defStyleAttr the default style attribute
     * @param component    the component
     * @param layout       the layout
     */
    public JSLBooleanActionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, JSLBooleanAction component, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr, component, layout);
        stateHandler = new JSLBooleanStateViewHandler(getContext(), this, getComponent());
        actionHandler = new JSLBooleanActionViewHandler(getContext(), this, getComponent());
    }


    // Subclass implementation

    /**
     * Update the handlers.
     *
     * @param newComp the new component
     * @param oldComp the old component
     */
    @Override
    protected void updateHandlers(JSLComponent newComp, JSLComponent oldComp) {
        stateHandler.setComponent(newComp);
        actionHandler.setComponent(newComp);
    }


    // Getters and Setters

    /**
     * Get the component managed by this handler.
     *
     * @return the component managed by this handler
     */
    public JSLBooleanAction getComponent() {
        return (JSLBooleanAction) super.getComponent();
    }

    /**
     * Set the component handled by this view and update all handlers,
     * including subclasses' handlers.
     *
     * @param newComp the component to manage
     */
    public void setComponent(JSLBooleanAction newComp) {
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

    /**
     * Get the action handler.
     *
     * @return the action handler
     */
    public JSLBooleanActionViewHandler getActionHandler() {
        return actionHandler;
    }

    /**
     * Set the enabled status of the view and his handlers.
     *
     * @param enabled the new enabled status
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        actionHandler.setEnabled(enabled);
    }

}
