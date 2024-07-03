package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLBaseActionHandler;
import com.robypomper.josp.jsl.objs.structure.JSLAction;

import java.text.DateFormat;
import java.util.Date;


/**
 * Base class for all {@link JSLAction} components view handlers.
 * <p>
 * Supported views:<br/>
 * Those views are defined here, but handled by subclasses:
 * <ul>
 *     <li>{@link #UI_COMP_LAST_CHANGE} ({@link TextView}): an alias for `UI_COMP_ACTION`</li>
 *     <li>{@link #UI_COMP_ACTION} ({@link TextView}): the component's action state, it's updated
 *     at every action change</li>
 *     <li>{@link #UI_COMP_ACTION_TIMEOUT} ({@link TextView}): the component's action timeout
 *     duration, it's updated when changed via {@link #setTimeoutMs(long)}</li>
 *     <li>{@link #UI_COMP_ACTION_TIMEOUT_MS} ({@link TextView}): the component's action timeout
 *     duration in milliseconds, it's updated when changed via {@link #setTimeoutMs(long)}</li>
 *     <li>{@link #UI_COMP_ACTION_TIMEOUT_SEC} ({@link TextView}): the component's action timeout
 *     duration in seconds, it's updated when changed via {@link #setTimeoutMs(long)}</li>
 *     <li>{@link #UI_COMP_ACTION_LAST_SENT} ({@link TextView}): the component's last action sent
 *     date, it's updated at every action sent</li>
 *     <li>{@link #UI_COMP_ACTION_LAST_FEEDBACK} ({@link TextView}): the component's last action
 *     feedback date, it's updated at every action feedback received</li>
 *     <li>{@link #UI_COMP_ACTION_LAST_TIMEOUT} ({@link TextView}): the component's last action
 *     timeout date, it's updated at every action timeout received</li>
 *     <li>{@link #UI_COMP_ACTION_WAITING_ICO} ({@link android.widget.ImageView}): the component's
 *     action waiting icon, it's shown when the handler is waiting</li>
 * </ul>
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_CMD_NOT_AVAILABLE_TXT}: default string to use as command label when the component is not available</li>
 *     <li>{@link #DEF_ACTION_WAITING_TXT}: default string to use as "waiting" text when the handler is waiting</li>
 *     <li>{@link #DEF_ACTION_NOT_WAITING_TXT}: default string to use as "not waiting" text when the handler is not waiting</li>
 *     <li>{@link #DEF_ACTION_NOT_AVAILABLE_TXT}: default string to use as "not available" text when the component is not available</li>
 * </ul>
 *
 * @noinspection unused
 */
public abstract class JSLBaseActionViewHandler
        extends JSLBaseComponentViewHandler {

    // Constants

    /**
     * The id for the component's last change {@link TextView}
     */
    public final static int UI_COMP_LAST_CHANGE = R.id.txtJSLCompLastChange;
    /**
     * The id for the component's action label {@link TextView}
     */
    public final static int UI_COMP_ACTION = R.id.txtJSLCompAction;
    /**
     * The id for the component's action timeout duration {@link TextView}
     */
    public final static int UI_COMP_ACTION_TIMEOUT = R.id.txtJSLCompActionTimeout;
    /**
     * The id for the component's action timeout duration in milliseconds {@link TextView}
     */
    public final static int UI_COMP_ACTION_TIMEOUT_MS = R.id.txtJSLCompActionTimeoutMs;
    /**
     * The id for the component's action timeout duration in seconds {@link TextView}
     */
    public final static int UI_COMP_ACTION_TIMEOUT_SEC = R.id.txtJSLCompActionTimeoutSec;
    /**
     * The id for the component's last action sent date {@link TextView}
     */
    public final static int UI_COMP_ACTION_LAST_SENT = R.id.txtJSLCompActionLastSent;
    /**
     * The id for the component's last action feedback date {@link TextView}
     */
    public final static int UI_COMP_ACTION_LAST_FEEDBACK = R.id.txtJSLCompActionLastFeedback;
    /**
     * The id for the component's last action timeout date {@link TextView}
     */
    public final static int UI_COMP_ACTION_LAST_TIMEOUT = R.id.txtJSLCompActionLastTimeout;
    /**
     * The id for the component's action waiting icon {@link android.widget.ImageView}
     */
    public final static int UI_COMP_ACTION_WAITING_ICO = R.id.icoJSLCompActionWaiting;
    /**
     * Default string to use as command label when the component is not available
     */
    public static final String DEF_CMD_NOT_AVAILABLE_TXT = "N/A";
    /**
     * Default string to use as "waiting" text when the handler is waiting
     */
    public static final String DEF_ACTION_WAITING_TXT = "Waiting...";
    /**
     * Default string to use as "not waiting" text when the handler is not waiting
     */
    public static final String DEF_ACTION_NOT_WAITING_TXT = "Not waiting";
    /**
     * Default string to use as "not available" text when the component is not available
     */
    public static final String DEF_ACTION_NOT_AVAILABLE_TXT = "Component not available";


    // Internal vars


    /**
     * String to use as command label when the component is not available
     */
    private String cmdNotAvailableTxt = DEF_CMD_NOT_AVAILABLE_TXT;
    /**
     * String to use as "waiting" text when the handler is waiting
     */
    private String actionWaitingTxt = DEF_ACTION_WAITING_TXT;
    /**
     * String to use as "not waiting" text when the handler is not waiting
     */
    private String actionNotWaitingTxt = DEF_ACTION_NOT_WAITING_TXT;
    /**
     * String to use as "not available" text when the component is not available
     */
    private String actionNotAvailableTxt = DEF_ACTION_NOT_AVAILABLE_TXT;


    // Constructors

    /**
     * Create a new JSLBaseStateViewHandlerInternal based on given mainView and component.
     *
     * @param ctx       the Context
     * @param mainView  Layout where the component will be added
     * @param component Component to handle
     */
    public JSLBaseActionViewHandler(Context ctx, ViewGroup mainView, JSLAction component) {
        this(ctx, mainView, component, null, null);
    }

    /**
     * Create a new JSLBaseStateViewHandlerInternal based on given mainView and component.
     *
     * @param ctx        the Context
     * @param mainView   Layout where the component will be added
     * @param component  Component to handle
     * @param formatTime the time format
     * @param formatDate the date format
     */
    public JSLBaseActionViewHandler(Context ctx, ViewGroup mainView, JSLAction component, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView, component, formatTime, formatDate);
    }


    // Getters and Setters

    /**
     * Get the handler to use to manage the component and show it on the UI.
     *
     * @return the handler to use to manage the component and show it on the UI
     */
    abstract public JSLBaseActionHandler getHandler();


    // Getter and Setter for command vars

    /**
     * Get the string to use as command label when the component is not available.
     *
     * @return the string to use as command label when the component is not available
     */
    public String getCmdNotAvailableTxt() {
        return cmdNotAvailableTxt;
    }

    /**
     * Set the string to use as command label when the component is not available.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param cmdNotAvailableTxt String to use as command label when the component is not available
     */
    public void setCmdNotAvailableTxt(String cmdNotAvailableTxt) {
        if (this.cmdNotAvailableTxt.equals(cmdNotAvailableTxt)) return;

        this.cmdNotAvailableTxt = cmdNotAvailableTxt;
        if (getComponent() != null) updateUILabelsRefresh();
    }


    // Getters and Setters for action vars

    public String getLastChangeTxt() {
        long lastActionSentMS = getLastActionSent() != null ? getLastActionSent().getTime() : 0;
        long lastActionFeedbackMS = getLastActionFeedback() != null ? getLastActionFeedback().getTime() : 0;
        long lastActionTimeoutMS = getLastActionTimeout() != null ? getLastActionTimeout().getTime() : 0;

        if (lastActionSentMS > lastActionFeedbackMS && lastActionSentMS > lastActionTimeoutMS)
            return "Sent @ " + getLastActionSentTxt();

        if (lastActionFeedbackMS > lastActionSentMS && lastActionFeedbackMS > lastActionTimeoutMS)
            return "Feedback @ " + getLastActionFeedbackTxt();

        if (lastActionTimeoutMS > lastActionSentMS && lastActionTimeoutMS > lastActionFeedbackMS)
            return "Timeout @ " + getLastActionTimeoutTxt();

        return JSLBaseViewHandler.DEF_INIT_NEVER;
    }

    /**
     * Get the component's action label ready to be used into the UI.
     *
     * @return the component's action, or the {@link #getActionNotAvailableTxt()} value,
     * if the component or the action is not available
     */
    public String getActionTxt() {
        if (getComponent() == null)
            return getActionNotAvailableTxt();

        if (isWaiting())
            return getActionWaitingTxt();

        return getActionNotWaitingTxt();
    }

    /**
     * Get the string to use as "waiting" text when the handler is waiting.
     *
     * @return the string to use as "waiting" text when the handler is waiting
     */
    public String getActionWaitingTxt() {
        return actionWaitingTxt;
    }

    /**
     * Set the string to use as "waiting" text when the handler is waiting.
     * <p>
     * If the handler is waiting, then the UI (action) is updated.
     *
     * @param actionWaitingTxt String to use as "waiting" text when the handler is waiting
     */
    public void setActionWaitingTxt(String actionWaitingTxt) {
        if (this.actionWaitingTxt.equals(actionWaitingTxt)) return;

        this.actionWaitingTxt = actionWaitingTxt;
        updateUIActionRefresh();
    }

    /**
     * Get the string to use as "not waiting" text when the handler is not waiting.
     *
     * @return the string to use as "not waiting" text when the handler is not waiting
     */
    public String getActionNotWaitingTxt() {
        return actionNotWaitingTxt;
    }

    /**
     * Set the string to use as "not waiting" text when the handler is not waiting.
     * <p>
     * If the handler is not waiting, then the UI (action) is updated.
     *
     * @param actionNotWaitingTxt String to use as "not waiting" text when the handler is not waiting
     */
    public void setActionNotWaitingTxt(String actionNotWaitingTxt) {
        if (this.actionNotWaitingTxt.equals(actionNotWaitingTxt)) return;

        this.actionNotWaitingTxt = actionNotWaitingTxt;
        updateUIActionRefresh();
    }

    /**
     * Get the string to use as "not available" text when the component is not available.
     *
     * @return the string to use as "not available" text when the component is not available
     */
    public String getActionNotAvailableTxt() {
        return actionNotAvailableTxt;
    }

    /**
     * Set the string to use as "not available" text when the component is not available.
     * <p>
     * If the handler's component is available, then the UI (action) is updated.
     *
     * @param actionNotAvailableTxt String to use as "not available" text when the component is not available
     */
    public void setActionNotAvailableTxt(String actionNotAvailableTxt) {
        if (this.actionNotAvailableTxt.equals(actionNotAvailableTxt)) return;

        this.actionNotAvailableTxt = actionNotAvailableTxt;
        updateUIActionRefresh();
    }


    // JSLBaseActionHandler getters and setters

    /**
     * Get the timeout in milliseconds for the action feedback handler.
     *
     * @return the timeout in milliseconds for the action feedback handler
     */
    public long getTimeoutMs() {
        return getHandler().getTimeoutMs();
    }

    /**
     * Set the timeout in milliseconds for the action feedback handler.
     *
     * @param timeoutMs the timeout in milliseconds for the action feedback handler
     */
    public void setTimeoutMs(long timeoutMs) {
        getHandler().setTimeoutMs(timeoutMs);
        updateUILabelsRefresh();
    }

    /**
     * Get the component's waiting status.
     *
     * @return the component's waiting status
     */
    public boolean isWaiting() {
        return getHandler().isWaitingForFeedback();
    }

    /**
     * Get the component's last action sent.
     *
     * @return the component's last action sent
     */
    public Date getLastActionSent() {
        return getHandler().getLastSent();
    }

    /**
     * Get the component's last action sent ready to be used into the UI.
     *
     * @return the component's last action sent, or the default ("never") value,
     * if the component or the last action sent is not available
     */
    public String getLastActionSentTxt() {
        return convertDate(getLastActionSent());
    }

    /**
     * Get the component's last action feedback.
     *
     * @return the component's last action feedback
     */
    public Date getLastActionFeedback() {
        return getHandler().getLastFeedbackReceived();
    }

    /**
     * Get the component's last action feedback ready to be used into the UI.
     *
     * @return the component's last action feedback, or the default ("never") value,
     * if the component or the last action feedback is not available
     */
    public String getLastActionFeedbackTxt() {
        return convertDate(getLastActionFeedback());
    }

    /**
     * Get the component's last action timeout.
     *
     * @return the component's last action timeout
     */
    public Date getLastActionTimeout() {
        return getHandler().getLastTimeout();
    }

    /**
     * Get the component's last action timeout ready to be used into the UI.
     *
     * @return the component's last action timeout, or the default ("never") value,
     * if the component or the last action timeout is not available
     */
    public String getLastActionTimeoutTxt() {
        return convertDate(getLastActionTimeout());
    }


    // UI Methods

    /**
     * Update all handled Views into the UI.
     * <p>
     * This method is called by the setComponent method, so you don't need to
     * call it manually. Although, you can call it manually to force the UI
     * update.
     */
    @Override
    public void updateUI() {
        super.updateUI();
        updateUIStateRefresh();
        updateUIActionRefresh();
        updateUILabelsRefresh();
    }

    /**
     * Update the views related to the current action into the UI.
     */
    protected void updateUIActionRefresh() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {

                trySetText(getMainView(), UI_COMP_LAST_CHANGE, getLastChangeTxt());

                trySetText(getMainView(), UI_COMP_ACTION, getActionTxt());
                trySetText(getMainView(), UI_COMP_ACTION_LAST_SENT, getLastActionSentTxt());
                trySetText(getMainView(), UI_COMP_ACTION_LAST_FEEDBACK, getLastActionFeedbackTxt());
                trySetText(getMainView(), UI_COMP_ACTION_LAST_TIMEOUT, getLastActionTimeoutTxt());
            }
        });
    }

    /**
     * Update the views related to the current state into the UI.
     * <p>
     * This is an abstract method, common to all action components handlers, that
     * must be implemented by the subclasses to update the UI state.
     */
    protected abstract void updateUIStateRefresh();

    /**
     * Update the views used as labels (all component's info not related with
     * states nor actions) into the UI.
     */
    @Override
    protected void updateUILabelsRefresh() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                trySetText(getMainView(), UI_COMP_ACTION_TIMEOUT, getTimeoutMs() + "ms");
                trySetText(getMainView(), UI_COMP_ACTION_TIMEOUT_MS, getTimeoutMs() + "ms");
                trySetText(getMainView(), UI_COMP_ACTION_TIMEOUT_SEC, (getTimeoutMs() / 1000) + "s");
            }
        });
    }

    /**
     * Update the UI interactive Views (buttons, sliders, switches..) and make
     * them inactive (locked) or active (unlocked).
     * <p>
     * This method must be called from all his subclasses' implementations.
     */
    protected void lockUIBecauseWaiting(boolean lock) {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                trySetVisibility(getMainView(), UI_COMP_ACTION_WAITING_ICO, lock ? View.VISIBLE : View.GONE);
            }
        });
    }


    // JSLBaseActionHandler.Observer

    /**
     * Listener for the handler's action start.
     * <p>
     * It locks the UI because the handler is waiting for feedback.
     *
     * @param handler the action handler
     */
    public void onStarted(JSLBaseActionHandler handler, int actionID) {
        lockUIBecauseWaiting(true);
        updateUIActionRefresh();
    }

    /**
     * Listener for the handler's action timeout.
     * <p>
     * It unlocks the UI because the handler is no more waiting for feedback.
     *
     * @param handler the action handler
     */
    public void onTimeout(JSLBaseActionHandler handler, int actionID) {
        // TODO add user feedback for timeout
        lockUIBecauseWaiting(false);
        updateUIActionRefresh();
    }

    /**
     * Listener for the handler's action feedback received.
     * <p>
     * It unlocks the UI because the handler is no more waiting for feedback.
     *
     * @param handler  the action handler
     * @param newState the new state received
     * @param oldState the old state received
     */
    public void onFeedbackReceived(JSLBaseActionHandler handler, int actionID, Object newState, Object oldState) {
        lockUIBecauseWaiting(false);
        updateUIActionRefresh();
    }

    /**
     * Listener for the handler's action feedback timeout.
     * <p>
     * It just update the UI.
     *
     * @param handler  the action handler
     * @param newState the new state received
     * @param oldState the old state received
     */
    public void onUpdateReceived(JSLBaseActionHandler handler, int actionID, Object newState, Object oldState) {
        updateUIStateRefresh();
    }

}
