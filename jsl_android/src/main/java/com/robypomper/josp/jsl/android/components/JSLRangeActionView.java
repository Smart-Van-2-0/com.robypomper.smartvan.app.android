package com.robypomper.josp.jsl.android.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.view.JSLRangeActionViewHandler;
import com.robypomper.josp.jsl.android.handlers.view.JSLRangeStateViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;


/**
 * View to show a {@link JSLRangeAction} components.
 * <p>
 * Handlers:
 * - stateHandler (`JSLBooleanStateViewHandler`): the handler for the component's state.
 * - actionHandler (`JSLBooleanActionViewHandler`): the handler for the component's action.
 *
 * @noinspection unused
 */
public class JSLRangeActionView
        extends JSLBaseComponentView {

    // Constants

    /**
     * The default layout to inflate for the view.
     */
    public final static @LayoutRes int DEF_LAYOUT = R.layout.view_jsl_range_action;
    /**
     * The default value for the `isHighVoltage` field.
     */
    public final static boolean DEF_IS_HIGH_VOLTAGE = false;


    // Internal vars

    /**
     * The handler for the component's state.
     */
    private final JSLRangeStateViewHandler stateHandler;
    /**
     * The handler for the component's action.
     */
    private final JSLRangeActionViewHandler actionHandler;


    // Constructors

    /**
     * Create a new `JSLRangeActionView` instance.
     *
     * @param context the context
     */
    public JSLRangeActionView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Create a new `JSLRangeActionView` instance.
     *
     * @param context the context
     * @param attrs   the attributes
     */
    public JSLRangeActionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Create a new `JSLRangeActionView` instance.
     *
     * @param context      the context
     * @param attrs        the attributes
     * @param defStyleAttr the default style attribute
     */
    public JSLRangeActionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, DEF_LAYOUT);
    }

    /**
     * Create a new `JSLRangeActionView` instance.
     *
     * @param context   the context
     * @param component the component
     * @param layout    the layout
     */
    public JSLRangeActionView(@NonNull Context context, JSLRangeAction component, @LayoutRes int layout) {
        this(context, null, component, layout);
    }

    /**
     * Create a new `JSLRangeActionView` instance.
     *
     * @param context   the context
     * @param attrs     the attributes
     * @param component the component
     * @param layout    the layout
     */
    public JSLRangeActionView(@NonNull Context context, @Nullable AttributeSet attrs, JSLRangeAction component, @LayoutRes int layout) {
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
    public JSLRangeActionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, JSLRangeAction component, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr, component, layout);
        stateHandler = new JSLRangeStateViewHandler(getContext(), this, getComponent());
        actionHandler = new JSLRangeActionViewHandler(getContext(), this, getComponent());
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
    public JSLRangeAction getComponent() {
        return (JSLRangeAction) super.getComponent();
    }

    /**
     * Set the component handled by this view and update all handlers,
     * including subclasses' handlers.
     *
     * @param newComp the component to manage
     */
    public void setComponent(JSLRangeAction newComp) {
        super.setComponent(newComp);
    }

    /**
     * Get the state handler.
     *
     * @return the state handler
     */
    public JSLRangeStateViewHandler getStateHandler() {
        return stateHandler;
    }

    /**
     * Get the action handler.
     *
     * @return the action handler
     */
    public JSLRangeActionViewHandler getActionHandler() {
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
        // only action handler
        actionHandler.setEnabled(enabled);
    }

}
