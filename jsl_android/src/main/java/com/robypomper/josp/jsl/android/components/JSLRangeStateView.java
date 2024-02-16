package com.robypomper.josp.jsl.android.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.view.JSLRangeStateViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;


/**
 * View to show a {@link JSLRangeState} components.
 * <p>
 * Handlers:
 * - stateHandler (`JSLRangeStateViewHandler`): the handler for the component's state.
 *
 * @noinspection unused
 */
public class JSLRangeStateView
        extends JSLBaseComponentView {

    // Constants

    /**
     * The default layout to inflate for the view.
     */
    public final static @LayoutRes int DEF_LAYOUT = R.layout.view_jsl_range_state;


    // Internal vars

    /**
     * The handler for the component's state.
     */
    private final JSLRangeStateViewHandler stateHandler;


    // Constructors

    /**
     * Create a new `JSLRangeStateView` instance.
     *
     * @param context the context
     */
    public JSLRangeStateView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Create a new `JSLRangeStateView` instance.
     *
     * @param context the context
     * @param attrs   the attributes
     */
    public JSLRangeStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Create a new `JSLRangeStateView` instance.
     *
     * @param context      the context
     * @param attrs        the attributes
     * @param defStyleAttr the default style attribute
     */
    public JSLRangeStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, DEF_LAYOUT);
    }

    /**
     * Create a new `JSLRangeStateView` instance.
     *
     * @param context   the context
     * @param component the component
     * @param layout    the layout
     */
    public JSLRangeStateView(@NonNull Context context, JSLRangeState component, @LayoutRes int layout) {
        this(context, null, component, layout);
    }

    /**
     * Create a new `JSLRangeStateView` instance.
     *
     * @param context   the context
     * @param attrs     the attributes
     * @param component the component
     * @param layout    the layout
     */
    public JSLRangeStateView(@NonNull Context context, @Nullable AttributeSet attrs, JSLRangeState component, @LayoutRes int layout) {
        this(context, attrs, 0, component, layout);
    }

    /**
     * Create a new `JSLBooleanStateViewInternal` instance.
     *
     * @param context      the context
     * @param attrs        the attributes
     * @param defStyleAttr the default style attribute
     * @param component    the component
     * @param layout       the layout
     */
    public JSLRangeStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, JSLRangeState component, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr, component, layout);
        stateHandler = new JSLRangeStateViewHandler(getContext(), this, getComponent());
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


    // Getters and Setters

    /**
     * Get the component managed by this handler.
     *
     * @return the component managed by this handler
     */
    public JSLRangeState getComponent() {
        return (JSLRangeState) super.getComponent();
    }

    /**
     * Set the component handled by this view and update all handlers,
     * including subclasses' handlers.
     *
     * @param newComp the component to manage
     */
    public void setComponent(JSLRangeState newComp) {
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

}
