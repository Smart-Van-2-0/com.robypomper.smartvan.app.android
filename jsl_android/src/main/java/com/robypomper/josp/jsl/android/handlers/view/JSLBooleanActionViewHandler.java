package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLBaseComponentHandler;
import com.robypomper.josp.jsl.android.handlers.base.JSLBooleanActionHandler;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;

import java.text.DateFormat;


/**
 * Class for {@link JSLBooleanAction} components view handlers.
 * <p>
 * Supported views:
 * Already defined into {@link JSLBaseStateViewHandler}.
 * <ul>
 *     <li>{@link #UI_COMP_SWITCH} ({@link Button}): the component's switch button</li>
 *     <li>{@link #UI_COMP_SWITCH_TRUE} ({@link Button}): the component's switch true button</li>
 *     <li>{@link #UI_COMP_SWITCH_FALSE} ({@link Button}): the component's switch false button</li>
 *     <li>{@link #UI_COMP_TOGGLE} ({@link ToggleButton}): the component's toggle button</li>
 *     <li>{@link #UI_COMP_TOGGLE_SWITCH} ({@link SwitchMaterial}): the component's toggle switch</li>
 *     <li>Already defined into the {@link JSLBaseActionViewHandler} class</li>
 * </ul>
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_VAL_TRUE_TXT}: default string to use as state when the state is `true`</li>
 *     <li>{@link #DEF_VAL_FALSE_TXT}: default string to use as state when the state is `false`</li>
 *     <li>{@link #DEF_CMD_SWITCH_TRUE_TXT}: default string to use as command label
 *     when the component's state is `false`</li>
 *     <li>{@link #DEF_CMD_SWITCH_FALSE_TXT}: default string to use as command label
 *     when the component's state is `true`</li>
 * </ul>
 *
 * @noinspection unused
 */
public class JSLBooleanActionViewHandler
        extends JSLBaseActionViewHandler
        implements JSLBooleanActionHandler.Observer {

    // Constants

    private final static String LOG_TAG = "JSLA.Hndl.JSLBActnView";
    /**
     * The id for the component's switch button {@link Button}
     */
    public final static int UI_COMP_SWITCH = R.id.btnJSLCompSwitch;
    /**
     * The id for the component's switch true button {@link Button}
     */
    public final static int UI_COMP_SWITCH_TRUE = R.id.btnJSLCompSwitchTrue;
    /**
     * The id for the component's switch false button {@link Button}
     */
    public final static int UI_COMP_SWITCH_FALSE = R.id.btnJSLCompSwitchFalse;
    /**
     * The id for the component's toggle button {@link ToggleButton}
     */
    public final static int UI_COMP_TOGGLE = R.id.btnJSLCompToggle;
    /**
     * The id for the component's toggle switch {@link SwitchMaterial}
     */
    public final static int UI_COMP_TOGGLE_SWITCH = R.id.btnJSLCompToggleSwitch;
    /**
     * Default string to use as state when the state is `true`
     */
    public final static String DEF_VAL_TRUE_TXT = JSLBooleanStateViewHandler.DEF_VAL_TRUE_TXT;
    /**
     * Default string to use as state when the state is `false`
     */
    public final static String DEF_VAL_FALSE_TXT = JSLBooleanStateViewHandler.DEF_VAL_FALSE_TXT;
    /**
     * Default string to use as command label when the component's state is `false`
     */
    public final static String DEF_CMD_SWITCH_TRUE_TXT = "Switch On";
    /**
     * Default string to use as command label when the component's state is `true`
     */
    public final static String DEF_CMD_SWITCH_FALSE_TXT = "Switch Off";


    // Internal vars

    /**
     * The component action handler
     */
    private final JSLBooleanActionHandler actionHandler;
    /**
     * String to use as state when the state is `true`
     */
    private String valTrueTxt = DEF_VAL_TRUE_TXT;
    /**
     * String to use as state when the state is `false`
     */
    private String valFalseTxt = DEF_VAL_FALSE_TXT;
    /**
     * String to use as "turn true" text, p.e. for switch button's label
     */
    private String cmdSwitchTrueTxt = DEF_CMD_SWITCH_TRUE_TXT;
    /**
     * String to use as "turn false" text, p.e. for switch button's label
     */
    private String cmdSwitchFalseTxt = DEF_CMD_SWITCH_FALSE_TXT;


    // Constructors

    /**
     * Create a new instance of {@link JSLBooleanActionViewHandler}.
     *
     * @param ctx       the context
     * @param mainView  the mainView
     * @param component the component
     */
    public JSLBooleanActionViewHandler(Context ctx, ViewGroup mainView, JSLBooleanAction component) {
        this(ctx, mainView, component, JSLBooleanActionHandler.DEF_TIMEOUT_MS);
    }

    /**
     * Create a new instance of {@link JSLBooleanActionViewHandler}.
     *
     * @param ctx       the context
     * @param mainView  the mainView
     * @param component the component
     * @param timeoutMs the timeout in milliseconds
     */
    public JSLBooleanActionViewHandler(Context ctx, ViewGroup mainView, JSLBooleanAction component, long timeoutMs) {
        this(ctx, mainView, component, timeoutMs, null, null);
    }

    /**
     * Create a new instance of {@link JSLBooleanActionViewHandler}.
     *
     * @param ctx        the context
     * @param mainView   the mainView
     * @param component  the component
     * @param timeoutMs  the timeout in milliseconds
     * @param formatTime the time format
     * @param formatDate the date format
     */
    public JSLBooleanActionViewHandler(Context ctx, ViewGroup mainView, JSLBooleanAction component, long timeoutMs, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView, component, formatTime, formatDate);
        actionHandler = new JSLBooleanActionHandler(this, getComponent());
        updateHandlers(getComponent(), null);
        setTimeoutMs(timeoutMs);
        setupListenersUI();
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
        if (actionHandler == null) return;
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
    public void setComponent(JSLBooleanAction newComp) {
        super.setComponent(newComp);
    }

    /**
     * Get the component's action handler.
     *
     * @return the component's action handler
     */
    @Override
    public JSLBooleanActionHandler getHandler() {
        return actionHandler;
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
     * If the handler's component is available and the state is `true`, then the UI (labels) is updated.
     *
     * @param valTrueTxt the string to use as state when the component's state is `true`
     */
    public void setValTrueTxt(String valTrueTxt) {
        if (this.valTrueTxt.equals(valTrueTxt)) return;

        this.valTrueTxt = valTrueTxt;
        if (getComponent() != null && getComponent().getState()) updateUILabelsRefresh();
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
     * If the handler's component is available and the state is `false`, then the UI (labels) is updated.
     *
     * @param valFalseTxt the string to use as state when the component's state is `false`
     */
    public void setValFalseTxt(String valFalseTxt) {
        if (this.valFalseTxt.equals(valFalseTxt)) return;

        this.valFalseTxt = valFalseTxt;
        if (getComponent() != null && !getComponent().getState()) updateUIStateRefresh();
    }


    // Getters and Setters for command vars

    /**
     * Get the component's command label ready to be used into the UI.
     *
     * @return the component's command, or the {@link #getCmdNotAvailableTxt()} value,
     * if the component or the command is not available
     */
    public String getCmdSwitchTxt() {
        if (getComponent() == null)
            return getCmdNotAvailableTxt();

        if (getComponent().getState())
            return getCmdSwitchFalseTxt();
        return getCmdSwitchTrueTxt();
    }

    /**
     * Get the string to use as command label when the component's state is `false`.
     *
     * @return the string to use as command label when the component's state is `false`
     */
    public String getCmdSwitchTrueTxt() {
        return cmdSwitchTrueTxt;
    }

    /**
     * Set the string to use as command label when the component's state is `false`.
     * <p>
     * If the handler's component is available and his state is `false`, then the UI (labels) is updated.
     *
     * @param cmdSwitchTrueTxt String to use as command label when the component's state is `false`
     */
    public void setCmdSwitchTrueTxt(String cmdSwitchTrueTxt) {
        if (this.cmdSwitchTrueTxt.equals(cmdSwitchTrueTxt)) return;

        this.cmdSwitchTrueTxt = cmdSwitchTrueTxt;
        if (getComponent() != null && !getComponent().getState()) updateUILabelsRefresh();
    }

    /**
     * Get the string to use as command label when the component's state is `true`.
     *
     * @return the string to use as command label when the component's state is `true`
     */
    public String getCmdSwitchFalseTxt() {
        return cmdSwitchFalseTxt;
    }

    /**
     * Set the string to use as command label when the component's state is `true`.
     * <p>
     * If the handler's component is available and his state is `true`, then the UI (labels) is updated.
     *
     * @param cmdSwitchFalseTxt String to use as command label when the component's state is `true`
     */
    public void setCmdSwitchFalseTxt(String cmdSwitchFalseTxt) {
        if (this.cmdSwitchFalseTxt.equals(cmdSwitchFalseTxt)) return;

        this.cmdSwitchFalseTxt = cmdSwitchFalseTxt;
        if (getComponent() != null && getComponent().getState()) updateUILabelsRefresh();
    }


    // UI Methods

    /**
     * Update the views related to the current state into the UI.
     */
    @Override
    protected void updateUIStateRefresh() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                trySetText(getMainView(), UI_COMP_SWITCH, getCmdSwitchTxt());
                trySetEnabled(getMainView(), UI_COMP_SWITCH_TRUE, !getComponent().getState());
                trySetEnabled(getMainView(), UI_COMP_SWITCH_FALSE, getComponent().getState());
                trySetChecked(getMainView(), UI_COMP_TOGGLE, getComponent().getState());
                trySetChecked(getMainView(), UI_COMP_TOGGLE_SWITCH, getComponent().getState());
            }
        });
    }

    /**
     * Update the views used as labels (all component's info not related with
     * states nor actions) into the UI.
     */
    @Override
    protected void updateUILabelsRefresh() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                trySetText(getMainView(), UI_COMP_SWITCH, getCmdSwitchTxt());
                trySetText(getMainView(), UI_COMP_SWITCH_TRUE, getCmdSwitchTrueTxt());
                trySetText(getMainView(), UI_COMP_SWITCH_FALSE, getCmdSwitchFalseTxt());
                if (getMainView().findViewById(UI_COMP_TOGGLE) != null) {
                    ((ToggleButton) getMainView().findViewById(UI_COMP_TOGGLE)).setTextOn(getValTrueTxt());
                    ((ToggleButton) getMainView().findViewById(UI_COMP_TOGGLE)).setTextOff(getValFalseTxt());
                }
            }
        });
    }

    /**
     * Set the user interactions' listeners into the UI.
     * <p>
     * This method is called by the constructor, so you don't need to call it
     * manually. Although, you can call it manually to force the UI update.
     */
    private void setupListenersUI() {
        // Set user interactions' listeners
        trySetOnClickListener(getMainView(), UI_COMP_SWITCH, getOnClickListener());
        trySetOnClickListener(getMainView(), UI_COMP_SWITCH_TRUE, getOnClickListener());
        trySetOnClickListener(getMainView(), UI_COMP_SWITCH_FALSE, getOnClickListener());
        trySetOnClickListener(getMainView(), UI_COMP_TOGGLE, getOnClickListener());
        trySetOnClickListener(getMainView(), UI_COMP_TOGGLE_SWITCH, getOnClickListener());
    }

    /**
     * Enable/disable the entire View.
     * <p>
     * This method enables/disables handled interactive Views, but it also
     * calls the {@link View#setEnabled(boolean)} method on the layout.
     */
    public void setEnabled(boolean enabled) {
        boolean state = getComponent().getState();
        if (getMainView().isEnabled() != enabled)
            getMainView().setEnabled(enabled);
        trySetEnabled(getMainView(), UI_COMP_SWITCH, enabled);
        trySetEnabled(getMainView(), UI_COMP_SWITCH_TRUE, enabled && !getComponent().getState());
        trySetEnabled(getMainView(), UI_COMP_SWITCH_FALSE, enabled && getComponent().getState());
        trySetEnabled(getMainView(), UI_COMP_TOGGLE, enabled);
        trySetEnabled(getMainView(), UI_COMP_TOGGLE_SWITCH, enabled);
    }

    /**
     * Update the UI interactive Views (buttons, sliders, switches..) and make
     * them inactive (locked) or active (unlocked).
     */
    @Override
    protected void lockUIBecauseWaiting(boolean lock) {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                JSLBooleanActionViewHandler.super.lockUIBecauseWaiting(lock);
                //boolean stateToUse = lock ? !getComponent().getState() : getComponent().getState();
                boolean stateToUse = lock != getComponent().getState();

                getMainView().setEnabled(!lock);
                trySetText(getMainView(), UI_COMP_SWITCH, stateToUse ? getCmdSwitchFalseTxt() : getCmdSwitchTrueTxt());
                trySetEnabled(getMainView(), UI_COMP_SWITCH_TRUE, !lock && !stateToUse);
                trySetEnabled(getMainView(), UI_COMP_SWITCH_FALSE, !lock && stateToUse);
                trySetChecked(getMainView(), UI_COMP_TOGGLE, stateToUse);
                trySetChecked(getMainView(), UI_COMP_TOGGLE_SWITCH, stateToUse);
            }
        });
    }


    // UI Listeners

    /**
     * User interactions' listener for handled Views.
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getComponent() == null) return;

            if (v.getId() == UI_COMP_SWITCH_TRUE) {
                processOnClick(true);
            } else if (v.getId() == UI_COMP_SWITCH_FALSE) {
                processOnClick(false);
            } else {
                // UI_COMP_SWITCH
                // UI_COMP_TOGGLE
                // UI_COMP_TOGGLE_SWITCH
                processOnClick(!getComponent().getState());
            }
        }
    };

    /**
     * Process the user's click on a button.
     * <p>
     * This method sends the action command to the component.
     *
     * @param newState the new state to send to the component
     */
    private void processOnClick(boolean newState) {
        // TODO add checks and error handling
        if (getComponent() == null) return;
        if (isWaiting()) return;
        if (getComponent().getState() == newState) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (newState) getHandler().sendTrue();
                    else getHandler().sendFalse();

                } catch (JSLRemoteObject.ObjectNotConnected e) {
                    // TODO add user error for object not connected
                    Log.e(LOG_TAG, "Error while sending action command, object not connected", e);
                } catch (JSLRemoteObject.MissingPermission e) {
                    // TODO add user error for missing permission
                    Log.e(LOG_TAG, "Error while sending action command, missing permissions on object", e);
                }
            }
        }).start();
    }

    /**
     * Get the user interactions' listener for handled Views.
     *
     * @return the user interactions' listener for handled Views
     */
    private View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

}
