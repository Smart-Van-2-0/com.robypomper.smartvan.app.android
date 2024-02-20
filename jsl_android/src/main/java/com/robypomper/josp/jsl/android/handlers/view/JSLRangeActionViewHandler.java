package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLBaseComponentHandler;
import com.robypomper.josp.jsl.android.handlers.base.JSLRangeActionHandler;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;

import java.text.DateFormat;


/**
 * Class for {@link JSLRangeAction} components view handlers.
 * <p>
 * Supported views (see {@link R.layout#placeholder_jsl_range_action_view}):
 * <ul>
 *     <li>{@link #UI_COMP_SEEKBAR} ({@link SeekBar}): the component's action seek bar</li>
 *     <li>{@link #UI_COMP_DECREASE} ({@link Button}): the component's action decrease button</li>
 *     <li>{@link #UI_COMP_INCREASE} ({@link Button}): the component's action increase button</li>
 *     <li>{@link #UI_COMP_SET_MIN} ({@link Button}): the component's action set min button</li>
 *     <li>{@link #UI_COMP_SET_MAX} ({@link Button}): the component's action set max button</li>
 *     <li>Already defined into the {@link JSLBaseActionViewHandler} class</li>
 * </ul>
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_CMD_DECREASE_TXT}: default string to use as "decrease" text</li>
 *     <li>{@link #DEF_CMD_INCREASE_TXT}: default string to use as "increase" text</li>
 *     <li>{@link #DEF_CMD_SET_MIN_TXT}: default string to use as "set min" text</li>
 *     <li>{@link #DEF_CMD_SET_MAX_TXT}: default string to use as "set max" text</li>
 * </ul>
 *
 * @noinspection unused
 */
public class JSLRangeActionViewHandler
        extends JSLBaseActionViewHandler
        implements JSLRangeActionHandler.Observer {

    // Constants

    private final static String LOG_TAG = "JSLA.Hndl.JSLRActnView";
    /**
     * The id for the component's action seek bar {@link SeekBar}
     */
    public final static int UI_COMP_SEEKBAR = R.id.sbJSLCompAction;
    /**
     * The id for the component's action decrease button {@link Button}
     */
    public final static int UI_COMP_DECREASE = R.id.btnJSLCompActionDecrease;
    /**
     * The id for the component's action increase button {@link Button}
     */
    public final static int UI_COMP_INCREASE = R.id.btnJSLCompActionIncrease;
    /**
     * The id for the component's action set min button {@link Button}
     */
    public final static int UI_COMP_SET_MIN = R.id.btnJSLCompActionSetMin;
    /**
     * The id for the component's action set max button {@link Button}
     */
    public final static int UI_COMP_SET_MAX = R.id.btnJSLCompActionSetMax;
    /**
     * Default string to use as "decrease" text
     */
    public final static String DEF_CMD_DECREASE_TXT = "Decrease";
    /**
     * Default string to use as "increase" text
     */
    public final static String DEF_CMD_INCREASE_TXT = "Increase";
    /**
     * Default string to use as "set min" text
     */
    public final static String DEF_CMD_SET_MIN_TXT = "Set Min";
    /**
     * Default string to use as "set max" text
     */
    public final static String DEF_CMD_SET_MAX_TXT = "Set Max";


    // Internal vars

    /**
     * Action command handler for feedback timeout.
     */
    private final JSLRangeActionHandler actionHandler;
    /**
     * String to use as "decrease" text
     */
    private String cmdDecreaseTxt = DEF_CMD_DECREASE_TXT;
    /**
     * String to use as "increase" text
     */
    private String cmdIncreaseTxt = DEF_CMD_INCREASE_TXT;
    /**
     * String to use as "set min" text
     */
    private String cmdSetMinTxt = DEF_CMD_SET_MIN_TXT;
    /**
     * String to use as "set max" text
     */
    private String cmdSetMaxTxt = DEF_CMD_SET_MAX_TXT;
    /**
     * The state required va user's interaction and to use during waiting
     */
    private Double requestState;


    // Constructors

    /**
     * Create a new instance of {@link JSLRangeActionViewHandler}.
     *
     * @param ctx       the context
     * @param mainView  the mainView
     * @param component the component
     */
    public JSLRangeActionViewHandler(Context ctx, ViewGroup mainView, JSLRangeAction component) {
        this(ctx, mainView, component, JSLRangeActionHandler.DEF_TIMEOUT_MS);
    }

    /**
     * Create a new instance of {@link JSLRangeActionViewHandler}.
     *
     * @param ctx       the context
     * @param mainView  the mainView
     * @param component the component
     * @param timeoutMs the timeout in milliseconds
     */
    public JSLRangeActionViewHandler(Context ctx, ViewGroup mainView, JSLRangeAction component, long timeoutMs) {
        this(ctx, mainView, component, timeoutMs, null, null);
    }

    /**
     * Create a new instance of {@link JSLRangeActionViewHandler}.
     *
     * @param ctx        the context
     * @param mainView   the mainView
     * @param component  the component
     * @param timeoutMs  the timeout in milliseconds
     * @param formatTime the time format
     * @param formatDate the date format
     */
    public JSLRangeActionViewHandler(Context ctx, ViewGroup mainView, JSLRangeAction component, long timeoutMs, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView, component, formatTime, formatDate);
        actionHandler = new JSLRangeActionHandler(this, getComponent());
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
    public JSLRangeAction getComponent() {
        return (JSLRangeAction) super.getComponent();
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
    public void setComponent(JSLRangeAction newComp) {
        super.setComponent(newComp);
    }

    /**
     * Get the component's action handler.
     *
     * @return the component's action handler
     */
    @Override
    public JSLRangeActionHandler getHandler() {
        return actionHandler;
    }

    /**
     * Convert the component's state to percent value.
     *
     * @return the component's state as percent
     */
    protected double getStatePercent() {
        return getPercent(getComponent().getState());
    }

    /**
     * Convert the given state to percent value.
     *
     * @param value the value to convert
     * @return the given state as percent
     */
    protected double getPercent(double value) {
        double min = getComponent().getMin();
        double max = getComponent().getMax();

        if (value < min) value = min;
        if (value > max) value = max;

        return (value - min) / (max - min) * 100;
    }

    /**
     * Convert the percent value to the component's state.
     *
     * @param percent the percent value
     * @return the component's state
     */
    protected double fromPercentToState(int percent) {
        double min = getComponent().getMin();
        double max = getComponent().getMax();
        return min + (max - min) * percent / 100;
    }


    // Getters and Setters for command vars

    /**
     * Get the string to use as "decrease" text.
     *
     * @return the string to use as "decrease" text
     */
    public String getCmdDecreaseTxt() {
        return cmdDecreaseTxt;
    }

    /**
     * Set the string to use as "decrease" text.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param cmdDecreaseTxt the string to use as "decrease" text
     */
    public void setCmdDecreaseTxt(String cmdDecreaseTxt) {
        if (this.cmdDecreaseTxt.equals(cmdDecreaseTxt)) return;

        this.cmdDecreaseTxt = cmdDecreaseTxt;
        if (getComponent() != null) updateUILabelsRefresh();
    }

    /**
     * Get the string to use as "increase" text.
     *
     * @return the string to use as "increase" text
     */
    public String getCmdIncreaseTxt() {
        return cmdIncreaseTxt;
    }

    /**
     * Set the string to use as "increase" text.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param cmdIncreaseTxt the string to use as "increase" text
     */
    public void setCmdIncreaseTxt(String cmdIncreaseTxt) {
        if (this.cmdIncreaseTxt.equals(cmdIncreaseTxt)) return;

        this.cmdIncreaseTxt = cmdIncreaseTxt;
        if (getComponent() != null) updateUILabelsRefresh();
    }

    /**
     * Get the string to use as "set min" text.
     *
     * @return the string to use as "set min" text
     */
    public String getCmdSetMinTxt() {
        return cmdSetMinTxt;
    }

    /**
     * Set the string to use as "set min" text.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param cmdSetMinTxt the string to use as "set min" text
     */
    public void setCmdSetMinTxt(String cmdSetMinTxt) {
        if (this.cmdSetMinTxt.equals(cmdSetMinTxt)) return;

        this.cmdSetMinTxt = cmdSetMinTxt;
        if (getComponent() != null) updateUILabelsRefresh();
    }

    /**
     * Get the string to use as "set max" text.
     *
     * @return the string to use as "set max" text
     */
    public String getCmdSetMaxTxt() {
        return cmdSetMaxTxt;
    }

    /**
     * Set the string to use as "set max" text.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param cmdSetMaxTxt the string to use as "set max" text
     */
    public void setCmdSetMaxTxt(String cmdSetMaxTxt) {
        if (this.cmdSetMaxTxt.equals(cmdSetMaxTxt)) return;

        this.cmdSetMaxTxt = cmdSetMaxTxt;
        if (getComponent() != null) updateUILabelsRefresh();
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
                double state = getComponent().getState();
                trySetProgress(getMainView(), UI_COMP_SEEKBAR, (int) getStatePercent());
                trySetEnabled(getMainView(), UI_COMP_DECREASE, state > getComponent().getMin());
                trySetEnabled(getMainView(), UI_COMP_INCREASE, state < getComponent().getMax());
                trySetEnabled(getMainView(), UI_COMP_SET_MIN, state != getComponent().getMin());
                trySetEnabled(getMainView(), UI_COMP_SET_MAX, state != getComponent().getMax());
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
                trySetText(getMainView(), UI_COMP_DECREASE, getCmdDecreaseTxt());
                trySetText(getMainView(), UI_COMP_INCREASE, getCmdIncreaseTxt());
                trySetText(getMainView(), UI_COMP_SET_MIN, getCmdSetMinTxt());
                trySetText(getMainView(), UI_COMP_SET_MAX, getCmdSetMaxTxt());
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
        trySetOnSeekBarChangeListener(getMainView(), UI_COMP_SEEKBAR, getOnValueChangeListener());
        trySetOnClickListener(getMainView(), UI_COMP_DECREASE, getOnClickDecreaseListener());
        trySetOnClickListener(getMainView(), UI_COMP_INCREASE, getOnClickIncreaseListener());
        trySetOnClickListener(getMainView(), UI_COMP_SET_MIN, getOnClickMinListener());
        trySetOnClickListener(getMainView(), UI_COMP_SET_MAX, getOnClickMaxListener());
    }

    /**
     * Enable/disable the entire View.
     * <p>
     * This method enables/disables handled interactive Views, but it also
     * calls the {@link View#setEnabled(boolean)} method on the layout.
     */
    public void setEnabled(boolean enabled) {
        double state = getComponent().getState();
        if (getMainView().isEnabled() != enabled)
            getMainView().setEnabled(enabled);
        trySetEnabled(getMainView(), UI_COMP_SEEKBAR, enabled);
        trySetEnabled(getMainView(), UI_COMP_DECREASE, enabled && state > getComponent().getMin());
        trySetEnabled(getMainView(), UI_COMP_INCREASE, enabled && state < getComponent().getMax());
        trySetEnabled(getMainView(), UI_COMP_SET_MIN, enabled && state != getComponent().getMin());
        trySetEnabled(getMainView(), UI_COMP_SET_MAX, enabled && state != getComponent().getMax());
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
                JSLRangeActionViewHandler.super.lockUIBecauseWaiting(lock);
                double stateToUse = lock ? requestState : getComponent().getState();

                getMainView().setEnabled(!lock);
                trySetEnabled(getMainView(), UI_COMP_SEEKBAR, !lock);
                trySetProgress(getMainView(), UI_COMP_SEEKBAR, (int) getPercent(stateToUse));
                trySetEnabled(getMainView(), UI_COMP_DECREASE, !lock && stateToUse > getComponent().getMin());
                trySetEnabled(getMainView(), UI_COMP_INCREASE, !lock && stateToUse < getComponent().getMax());
                trySetEnabled(getMainView(), UI_COMP_SET_MIN, !lock && stateToUse != getComponent().getMin());
                trySetEnabled(getMainView(), UI_COMP_SET_MAX, !lock && stateToUse != getComponent().getMax());
            }
        });
    }


    // UI Listeners

    /**
     * User interaction' listener for the value change of the handled seek bar.
     */
    private final SeekBar.OnSeekBarChangeListener onValueChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean userChange) {
            if (getComponent() == null) return;
            if (userChange) {
                processOnValueChange(seekBar, (double) progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    };

    /**
     * User interaction' listener for the handled decrease button.
     */
    private final View.OnClickListener onClickDecreaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getComponent() == null) return;

            processOnValueDecrease(v);
        }
    };

    /**
     * User interaction' listener for the handled increase button.
     */
    private final View.OnClickListener onClickIncreaseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getComponent() == null) return;

            processOnValueIncrease(v);
        }
    };

    /**
     * User interaction' listener for the handled set min button.
     */
    private final View.OnClickListener onClickMinListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getComponent() == null) return;

            processOnValueMin(v);
        }
    };

    /**
     * User interaction' listener for the handled set max button.
     */
    private final View.OnClickListener onClickMaxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getComponent() == null) return;

            processOnValueMax(v);
        }
    };

    /**
     * Process the user's value change of the handled seek bar.
     *
     * @param v     the view that triggered the event
     * @param value the new value
     */
    private void processOnValueChange(View v, Double value) {
        // TODO add checks and error handling
        if (getComponent() == null) return;
        if (isWaiting()) return;
        if (getComponent().getState() == value) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestState = value;
                    getHandler().sendValue(value);

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
     * Process the user's click on handled decrease button.
     *
     * @param v the view that triggered the event
     */
    private void processOnValueDecrease(View v) {
        // TODO add checks and error handling
        if (getComponent() == null) return;
        if (isWaiting()) return;
        if (getComponent().getState() <= getComponent().getMin()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestState = getComponent().getState() - getComponent().getStep();
                    getHandler().sendDecrease();

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
     * Process the user's click on handled increase button.
     *
     * @param v the view that triggered the event
     */
    private void processOnValueIncrease(View v) {
        // TODO add checks and error handling
        if (getComponent() == null) return;
        if (isWaiting()) return;
        if (getComponent().getState() >= getComponent().getMax()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestState = getComponent().getState() + getComponent().getStep();
                    getHandler().sendIncrease();

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
     * Process the user's click on handled set min button.
     *
     * @param v the view that triggered the event
     */
    private void processOnValueMin(View v) {
        // TODO add checks and error handling
        if (getComponent() == null) return;
        if (isWaiting()) return;
        if (getComponent().getState() == getComponent().getMin()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestState = getComponent().getMin();
                    getHandler().sendMin();

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
     * Process the user's click on handled set max button.
     *
     * @param v the view that triggered the event
     */
    private void processOnValueMax(View v) {
        // TODO add checks and error handling
        if (getComponent() == null) return;
        if (isWaiting()) return;
        if (getComponent().getState() == getComponent().getMax()) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestState = getComponent().getMax();
                    getHandler().sendMax();

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
     * Get the user interaction' listener for the value change of the handled seek bar.
     *
     * @return the user interaction' listener for the value change of the handled seek bar
     */
    public SeekBar.OnSeekBarChangeListener getOnValueChangeListener() {
        return onValueChangeListener;
    }

    /**
     * Get the user interaction' listener for the handled decrease button.
     *
     * @return the user interaction' listener for the handled decrease button
     */
    public View.OnClickListener getOnClickDecreaseListener() {
        return onClickDecreaseListener;
    }

    /**
     * Get the user interaction' listener for the handled increase button.
     *
     * @return the user interaction' listener for the handled increase button
     */
    public View.OnClickListener getOnClickIncreaseListener() {
        return onClickIncreaseListener;
    }

    /**
     * Get the user interaction' listener for the handled set min button.
     *
     * @return the user interaction' listener for the handled set min button
     */
    public View.OnClickListener getOnClickMinListener() {
        return onClickMinListener;
    }

    /**
     * Get the user interaction' listener for the handled set max button.
     *
     * @return the user interaction' listener for the handled set max button
     */
    public View.OnClickListener getOnClickMaxListener() {
        return onClickMaxListener;
    }

}
