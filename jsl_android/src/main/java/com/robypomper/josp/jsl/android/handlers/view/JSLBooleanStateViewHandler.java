package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.view.ViewGroup;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLBaseComponentHandler;
import com.robypomper.josp.jsl.android.handlers.base.JSLBooleanStateHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;

import java.text.DateFormat;


/**
 * Class for {@link JSLBooleanState} components view handlers.
 * <p>
 * Supported views (see {@link R.layout#placeholder_jsl_base_state_view}):
 * <ul>
 *     <li>Already defined into the {@link JSLBaseStateViewHandler} class</li>
 * </ul>
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_VAL_TRUE_TXT}: default string to use as state when the state is `true`</li>
 *     <li>{@link #DEF_VAL_FALSE_TXT}: default string to use as state when the state is `false`</li>
 * </ul>
 *
 * @noinspection unused
 */
public class JSLBooleanStateViewHandler
        extends JSLBaseStateViewHandler
        implements JSLBooleanStateHandler.Observer {

    // Constants

    /**
     * Default string to use as state when the state is `true`
     */
    public final static String DEF_VAL_TRUE_TXT = "True";
    /**
     * Default string to use as state when the state is `false`
     */
    public final static String DEF_VAL_FALSE_TXT = "False";


    // Internal vars

    /**
     * The component's state handler
     */
    private final JSLBooleanStateHandler stateHandler;
    /**
     * String to use as state when the state is `true`
     */
    private String valTrueTxt = DEF_VAL_TRUE_TXT;
    /**
     * String to use as state when the state is `false`
     */
    private String valFalseTxt = DEF_VAL_FALSE_TXT;


    // Constructors

    /**
     * Create a new instance of {@link JSLBooleanStateViewHandler}.
     *
     * @param ctx       the context
     * @param mainView  the mainView
     * @param component the component
     */
    public JSLBooleanStateViewHandler(Context ctx, ViewGroup mainView, JSLBooleanState component) {
        this(ctx, mainView, component, null, null);
    }

    /**
     * Create a new instance of {@link JSLBooleanStateViewHandler}.
     *
     * @param ctx        the context
     * @param mainView   the mainView
     * @param component  the component
     * @param formatTime the time format
     * @param formatDate the date format
     */
    public JSLBooleanStateViewHandler(Context ctx, ViewGroup mainView, JSLBooleanState component, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView, component, formatTime, formatDate);
        stateHandler = new JSLBooleanStateHandler(this, getComponent());
        updateHandlers(getComponent(), null);
    }


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
     * Convert given state to a string using the {@link #getValTrueTxt()} and
     * {@link #getValFalseTxt()} methods.
     *
     * @return the string to use as component's state
     */
    @Override
    protected String convertState(Object state) {
        return (Boolean)state ? getValTrueTxt() : getValFalseTxt();
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

    /**
     * Get the component's state handler.
     *
     * @return the component's state handler
     */
    @Override
    public JSLBooleanStateHandler getHandler() {
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


    // Getters and Setters for values vars

    /**
     * Get the string to use as state when the component's state is `true`.
     *
     * @return the string to use as state when the component's state is `true`
     */
    public String getValTrueTxt() {
        return valTrueTxt;
    }

    /**
     * Set the string to use as state when the component's state is `true`.
     * <p>
     * If the handler's component is available and the state is `true`, then the UI (states) is updated.
     *
     * @param valTrueTxt the string to use as state when the component's state is `true`
     */
    public void setValTrueTxt(String valTrueTxt) {
        if (this.valTrueTxt.equals(valTrueTxt)) return;

        this.valTrueTxt = valTrueTxt;
        if (getComponent() != null && getComponent().getState()) updateUIStateRefresh();
    }

    /**
     * Get the string to use as state when the component's state is `false`.
     *
     * @return the string to use as state when the component's state is `false`
     */
    public String getValFalseTxt() {
        return valFalseTxt;
    }

    /**
     * Set the string to use as state when the component's state is `false`.
     * <p>
     * If the handler's component is available and the state is `false`, then the UI (states) is updated.
     *
     * @param valFalseTxt the string to use as state when the component's state is `false`
     */
    public void setValFalseTxt(String valFalseTxt) {
        if (this.valFalseTxt.equals(valFalseTxt)) return;

        this.valFalseTxt = valFalseTxt;
        if (getComponent() != null && !getComponent().getState()) updateUIStateRefresh();
    }

}
