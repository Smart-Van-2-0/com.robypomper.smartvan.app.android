package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.view.ViewGroup;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLBaseComponentHandler;
import com.robypomper.josp.jsl.android.handlers.base.JSLRangeStateHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

import java.text.DateFormat;


/**
 * Class for {@link JSLRangeState} components view handlers.
 * <p>
 * Supported views (see {@link R.layout#placeholder_jsl_base_state_view}):
 * <ul>
 *     <li>Already defined into the {@link JSLBaseStateViewHandler} class</li>
 * </ul>
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_FORMAT_VALUE}: default string format to use to convert the component's value to string</li>
 * </ul>
 *
 * @noinspection unused
 */
public class JSLRangeStateViewHandler
        extends JSLBaseStateViewHandler
        implements JSLRangeStateHandler.Observer {

    // Constants

    /**
     * Default string format to use to convert the component's value to string.
     * <p>
     * This string can contains a single `%f` to represent the value, plus any
     * other string to represent the unit.
     */
    public final static String DEF_FORMAT_VALUE = "%.2f %%";


    // Internal vars

    /**
     * The component's state handler
     */
    private final JSLRangeStateHandler stateHandler;
    /**
     * String format to use to convert the component's value to string
     */
    private String formatValue = DEF_FORMAT_VALUE;


    // Constructors

    /**
     * Create a new instance of {@link JSLRangeStateViewHandler}.
     *
     * @param ctx       the context
     * @param mainView  the mainView
     * @param component the component
     */
    public JSLRangeStateViewHandler(Context ctx, ViewGroup mainView, JSLRangeState component) {
        this(ctx, mainView, component, null, null);
    }

    /**
     * Create a new instance of {@link JSLRangeStateViewHandler}.
     *
     * @param ctx        the context
     * @param mainView   the mainView
     * @param component  the component
     * @param formatTime the time format
     * @param formatDate the date format
     */
    public JSLRangeStateViewHandler(Context ctx, ViewGroup mainView, JSLRangeState component, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView, component, formatTime, formatDate);
        stateHandler = new JSLRangeStateHandler(this, getComponent());
        updateHandlers(getComponent(), null);
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
        if (stateHandler == null) return;
        stateHandler.setComponent(newComp);
    }

    /**
     * Convert given state to a string using the {@link #getFormatValue()} method.
     *
     * @return the string representation of the given state
     */
    @Override
    protected String convertState(Object state) {
        return String.format(getFormatValue(), state);
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
    public void setComponent(JSLRangeState newComp) {
        super.setComponent(newComp);
    }

    /**
     * Get the component's state handler.
     *
     * @return the component's state handler
     */
    @Override
    public JSLRangeStateHandler getHandler() {
        return stateHandler;
    }

    /**
     * Get the component's state ready to be used into the UI.
     *
     * @return the component's state, or the {@link #getInitStateTxt()} value,
     * if the component or the state is not available
     */
    @Override
    public String getStateTxt() {
        if (getComponent() == null) return getInitStateTxt();
        return convertState(getComponent().getState());
    }


    // Getters and Setters for format vars

    /**
     * Get the string format to use to convert the component's value to string.
     *
     * @return the string format to use to convert the component's value to string
     */
    public String getFormatValue() {
        return formatValue;
    }

    /**
     * Set the string format to use to convert the component's value to string.
     * <p>
     * If the handler's component is available, then the UI (states) is updated.
     *
     * @param formatValue the string format to use to convert the component's value to string
     */
    public void setFormatValue(String formatValue) {
        if (this.formatValue.equals(formatValue)) return;

        this.formatValue = formatValue;
        if (getComponent() != null) updateUIStateRefresh();
    }

}
