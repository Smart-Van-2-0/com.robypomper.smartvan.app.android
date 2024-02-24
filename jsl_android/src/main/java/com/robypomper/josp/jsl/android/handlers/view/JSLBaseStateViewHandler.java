package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLBaseStateHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLState;

import java.text.DateFormat;
import java.util.Date;


/**
 * Base class for all {@link JSLState} components view handlers.
 * <p>
 * Supported views:<br/>
 * Those views are defined here, but handled by subclasses:
 * <ul>
 *     <li>{@link #UI_COMP_STATE} ({@link TextView}): the component's state, it's updated
 *     at every state change, or when some "value's txt/format" is set</li>
 *     <li>{@link #UI_COMP_STATE_NEW} ({@link TextView}): the component's state, it's like
 *     UI_COMP_STATE</li>
 *     <li>{@link #UI_COMP_STATE_OLD} ({@link TextView}): the component's old state, it's
 *     updated at every state change (except the first one) and when some
 *     "value's txt/format" is set</li>
 *     <li>{@link #UI_COMP_LAST_UPDATE} ({@link TextView}): the component's last
 *     update date, it's updated at every state change</li>
 * </ul>
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_INIT_STATE_TXT}: default component's state used when the component is not available</li>
 *     <li>{@link #DEF_INIT_STATE_OLD_TXT}: default component's old state used when the old state is not available</li>
 * </ul>
 *
 * @noinspection unused
 */
public abstract class JSLBaseStateViewHandler
        extends JSLBaseComponentViewHandler {

    // Constants

    /**
     * The id for the component's state {@link TextView}
     */
    public final static int UI_COMP_STATE = R.id.txtJSLCompState;
    /**
     * The id for the component's new state {@link TextView} (alias for {@link #UI_COMP_STATE}
     */
    public final static int UI_COMP_STATE_NEW = R.id.txtJSLCompStateNew;
    /**
     * The id for the component's old state {@link TextView}
     */
    public final static int UI_COMP_STATE_OLD = R.id.txtJSLCompStateOld;
    /**
     * The id for the component's last update date {@link TextView}
     */
    public final static int UI_COMP_LAST_UPDATE = R.id.txtJSLCompLastUpdate;
    /**
     * Default component's state used when the component's state is not available
     */
    public final static String DEF_INIT_STATE_TXT = "Unknown";
    /**
     * Default component's old state used when the old state is not available
     */
    public final static String DEF_INIT_STATE_OLD_TXT = "N/A";


    // Internal vars

    /**
     * String to use as state when the component's state is not available
     */
    private String initStateTxt = DEF_INIT_STATE_TXT;
    /**
     * String to use as old state when the old state is not available
     */
    private String initStateOldTxt = DEF_INIT_STATE_OLD_TXT;


    // Constructors

    /**
     * Create a new JSLBaseStateViewHandlerInternal based on given mainView and component.
     *
     * @param ctx       the Context
     * @param mainView  Layout where the component will be added
     * @param component Component to handle
     */
    public JSLBaseStateViewHandler(Context ctx, ViewGroup mainView, JSLState component) {
        this(ctx, mainView, component, null, null);
    }

    /**
     * Create a new JSLBaseStateViewHandlerInternal based on given mainView and component.
     *
     * @param ctx        the Context
     * @param mainView   Layout where the component will be added
     * @param component  Component to handle
     * @param formatTime Format to use to convert the component's last update date to string
     * @param formatDate Format to use to convert the component's last update date to string
     */
    public JSLBaseStateViewHandler(Context ctx, ViewGroup mainView, JSLState component, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView, component, formatTime, formatDate);
    }


    // Subclasses abstract methods

    /**
     * Convert given state to a string.
     *
     * @return the string to use as component's state
     */
    protected abstract String convertState(Object state);


    // Getters and Setters

    /**
     * Get the handler to use to manage the component and show it on the UI.
     *
     * @return the handler to use to manage the component and show it on the UI
     */
    abstract public JSLBaseStateHandler getHandler();

    /**
     * Get the component's state ready to be used into the UI.
     *
     * @return the component's state, or the {@link #getInitStateTxt()} value,
     * if the component or the state is not available
     */
    public abstract String getStateTxt();


    // Getter and Setter for init vars

    /**
     * Get the string to use as state when the component's state is not available.
     *
     * @return String to use as state when the component's state is not available
     */
    public String getInitStateTxt() {
        return initStateTxt;
    }

    /**
     * Set the string to use as state when the component's state is not available.
     * <p>
     * If the handler's component is NOT available, then the UI (states) is updated.
     *
     * @param initStateTxt String to use as state when the component's state is not available
     */
    public void setInitStateTxt(String initStateTxt) {
        if (this.initStateTxt.equals(initStateTxt)) return;

        this.initStateTxt = initStateTxt;
        if (getComponent() == null) updateUIStateRefresh();
    }

    /**
     * Get the string to use as old state when the component's old state is not available.
     *
     * @return String to use as old state when the component's old state is not available
     */
    public String getInitStateOldTxt() {
        return initStateOldTxt;
    }

    /**
     * Set the string to use as old state when the component's old state is not available.
     * <p>
     * If the handler's component is NOT available, then the UI (states) is updated.
     *
     * @param initStateOldTxt String to use as old state when the component's old state is not available
     */
    public void setInitStateOldTxt(String initStateOldTxt) {
        if (this.initStateOldTxt.equals(initStateOldTxt)) return;

        this.initStateOldTxt = initStateOldTxt;
        if (getComponent() == null) updateUIStateRefresh();
    }


    // JSLBaseStateViewHandler Getters and Setters

    /**
     * Get the component's old state.
     *
     * @return the component's old state
     */
    public Object getStateOld() {
        return getHandler().getStateOld();
    }

    /**
     * Get the component's old state ready to be used into the UI.
     *
     * @return the component's old state, or the {@link #getInitStateOldTxt()} value,
     * if the component or the old state is not available
     */
    public String getStateOldTxt() {
        if (getComponent() == null) return getInitStateOldTxt();
        if (getStateOld() == null) return getInitStateOldTxt();
        return convertState(getStateOld());
    }

    /**
     * Get the component's last update.
     *
     * @return the component's last update
     */
    public Date getLastUpdate() {
        return getHandler().getLastUpdate();
    }

    /**
     * Get the component's last update ready to be used into the UI.
     *
     * @return the component's last update, or the {@link #convertDate(Date)} value,
     * if the component or the last update is not available
     */
    public String getLastUpdateTxt() {
        return convertDate(getLastUpdate());
    }


    // UI Methods

    /**
     * Update all handled Views into the UI.
     * <p>
     * This method is called by the {@link #setComponent(JSLComponent)}  method, so
     * you don't need to call it manually. Although, you can call it manually
     * to force the UI update.
     */
    @Override
    public void updateUI() {
        super.updateUI();
        updateUIStateRefresh();
    }


    // UI Methods

    /**
     * Update the UI state (text, color, etc) based on the component's state.
     */
    protected void updateUIStateRefresh() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                trySetText(getMainView(), UI_COMP_STATE, getStateTxt());
                trySetText(getMainView(), UI_COMP_STATE_NEW, getStateTxt());
                trySetText(getMainView(), UI_COMP_STATE_OLD, getStateOldTxt());
                trySetText(getMainView(), UI_COMP_LAST_UPDATE, getLastUpdateTxt());
            }
        });
    }


    // JSLBaseStateHandler.Observer

    /**
     * Listener for the component state update (from the internal handler).
     * <p>
     * Calls the {@link #updateUIStateRefresh()} method.
     *
     * @param handler the handler
     */
    public void onStateDataUpdated(JSLBaseStateHandler handler, Object newState, Object oldState) {
        updateUIStateRefresh();
    }

}
