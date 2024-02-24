package com.robypomper.josp.jsl.android.handlers.base;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.objs.structure.JSLAction;

import java.util.Date;


/**
 * Base handler for {@link JSLAction} components that manage commands feedback.
 * <p>
 * It provides all methods to handle the action feedback. For every command
 * sent (from a subclass) to the remote object, this class starts a timeout.
 * If the timeout expires before a feedback is received, then the handler notifies
 * the timeout. Otherwise, the handler notifies the feedback received.
 * <p>
 * Client classes can initiate the action feedback management by calling the
 * {@link #startTimeoutThread(int)} method. This method updates the action sent
 * date, starts the timeout thread and notifies the timeout has been started.
 * <p>
 * When a feedback is received, the handler updates the feedback received date,
 * interrupts the timeout thread and notifies the feedback received.
 * <p>
 * If the timeout runs out, the handler updates the timeout date and notifies
 * the timeout. The timeout thread sleeps for {@link #getTimeoutMs()} milliseconds
 * and, if not interrupted, will update the timeout date and notify the timeout.
 * <p>
 * Main features:
 * - single observer
 * - support component switch
 * - onComponentChanged
 * - onStarted
 * - onTimeout
 * - onFeedbackReceived
 * - settable timeout duration
 * <p>
 * Main properties
 * - action id
 * - action txt
 * - is waiting for feedback
 * - last action sent date
 * - last feedback received date
 * - last timeout date
 *
 * @noinspection unused
 */
public abstract class JSLBaseActionHandler
        extends JSLBaseComponentHandler {

    // Constants

    /**
     * Default timeout duration in milliseconds
     */
    public final static long DEF_TIMEOUT_MS = 5000;


    // Internal vars

    /**
     * The last action sent id, or the current action id if the handler is
     * waiting for feedback. It's `-1` if no action has been sent.
     * <p>
     * This value is updates only when the handler starts a new action or when
     * it switches to a new component.
     */
    private int actionId = -1;
    /**
     * Timeout duration in milliseconds
     */
    private long timeoutMs;
    /**
     * Datetime for last action send to the object
     */
    private Date lastSent;
    /**
     * Datetime for last feedback received from the object
     */
    private Date lastFeedbackReceived;
    /**
     * Datetime for last timeout
     */
    private Date lastTimeout;
    /**
     * Timeout thread, if null then the handler is not waiting.
     */
    private Thread timeoutThread;


    // Handler observer

    /**
     * Observer for the {@link JSLAction} components handler.
     */
    public interface Observer
            extends JSLBaseComponentHandler.Observer {

        void onStarted(JSLBaseActionHandler handler, int actionId);

        void onTimeout(JSLBaseActionHandler handler, int actionId);

        void onFeedbackReceived(JSLBaseActionHandler handler, int actionId, Object newState, Object oldState);

        void onUpdateReceived(JSLBaseActionHandler handler, int actionId, Object newState, Object oldState);

    }


    // Constructors

    /**
     * Create a new handler for the given component.
     *
     * @param observer  the observer to notify when the state changes
     * @param component the component to handle
     */
    public JSLBaseActionHandler(Observer observer, JSLAction component) {
        this(observer, component, DEF_TIMEOUT_MS);
    }

    /**
     * Create a new handler for the given component.
     *
     * @param observer  the observer to notify when the state changes
     * @param component the component to handle
     * @param timeoutMs the timeout duration in milliseconds
     */
    public JSLBaseActionHandler(Observer observer, JSLAction component, long timeoutMs) {
        super(observer, component);
        this.timeoutMs = timeoutMs;
    }

    /**
     * Create a new handler from the given one.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param other    the other handler to copy the extra info from and
     *                 the component reference
     */
    public JSLBaseActionHandler(Observer observer, JSLBaseActionHandler other) {
        super(observer, other);
        if (other != null)
            setComponent(other.getComponent());
    }

    /**
     * Interrupt the timeout thread, if any, when the handler is finalized.
     *
     * @throws Throwable the Exception raised by this method
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (isWaitingForFeedback())
            timeoutThread.interrupt();
    }


    // Copy extra info

    /**
     * This method copy the extra info from the given handler to the current
     * handler.
     *
     * @param handler the handler to copy the extra info from
     * @throws IllegalArgumentException if the given handler is not an instance
     *                                  of this class
     */
    @Override
    public void copy(JSLBaseHandler handler) {
        super.copy(handler);
        if (!this.getClass().isInstance(handler))
            throw new IllegalArgumentException("The given handler is not an instance of " + getClass().getName());
        JSLBaseActionHandler other = (JSLBaseActionHandler) handler;
        this.actionId = other.actionId;
        this.lastSent = other.lastSent;
        this.lastFeedbackReceived = other.lastFeedbackReceived;
        this.lastTimeout = other.lastTimeout;
        this.timeoutMs = other.timeoutMs;
    }


    // Getters and Setters

    /**
     * Get the observer for this handler.
     *
     * @return the observer for this handler
     */
    @Override
    protected Observer getObserver() {
        return (Observer) super.getObserver();
    }

    /**
     * Get the timeout duration in milliseconds.
     *
     * @return the timeout duration in milliseconds
     */
    public long getTimeoutMs() {
        return timeoutMs;
    }

    /**
     * Set the timeout duration in milliseconds.
     *
     * @param timeoutMs the timeout duration in milliseconds
     */
    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    /**
     * Get the last action sent id, or the current action id if the handler is
     * waiting for feedback.
     *
     * @return the last action sent id, or `-1` if no action has been sent
     */
    public int getActionID() {
        return actionId;
    }

    /**
     * Get the last action sent id as a string.
     *
     * @return the last action sent id as a string
     */
    public String getActionIDTxt() {
        return convertActionId(actionId);
    }

    /**
     * Get the last action sent date.
     *
     * @return the last action sent date
     */
    public Date getLastSent() {
        return lastSent;
    }

    /**
     * Get the last feedback received date.
     *
     * @return the last feedback received date
     */
    public Date getLastFeedbackReceived() {
        return lastFeedbackReceived;
    }

    /**
     * Set the last feedback received date to the current date and time.
     * <p>
     * This method also interrupts the timeout thread and notifies the feedback
     * received.
     *
     * @param newState the new state used to notify the observer
     * @param oldState the old state used to notify the observer
     */
    private void setFeedbackReceivedDate(Object newState, Object oldState) {
        timeoutThread.interrupt();
        this.lastFeedbackReceived = JavaDate.getNowDate();
        notifyFeedbackReceived(newState, oldState);
    }

    /**
     * Get the last timeout date.
     *
     * @return the last timeout date
     */
    public Date getLastTimeout() {
        return lastTimeout;
    }

    /**
     * Set the last action sent date to the current date and time.
     * <p>
     * This method also starts the timeout thread and notifies the action has
     * been started.
     *
     * @param actionId the sent action's id
     */
    private void setActionSent(int actionId) {
        this.actionId = actionId;
        this.lastSent = JavaDate.getNowDate();
        timeoutThread.start();
        notifyStarted();
    }

    /**
     * Set the last timeout date to the current date and time.
     * <p>
     * This method also notifies the timeout.
     */
    private void setActionTimeout() {
        timeoutThread = null; // updated as soon as possible
        this.lastTimeout = JavaDate.getNowDate();
        notifyTimeout();
    }


    //

    /**
     * Convert the action id to a string.
     *
     * @param actionId the action id
     * @return the action id as a string
     */
    public abstract String convertActionId(int actionId);


    // Timeout Timer

    /**
     * Check if current handler is waiting for a feedback.
     *
     * @return true if current handler is waiting for a feedback, false otherwise
     */
    public boolean isWaitingForFeedback() {
        return timeoutThread != null;
    }

    /**
     * First part of the action feedback management: start the timeout thread.
     * <p>
     * This method updates the action sent date, starts the timeout thread and
     * notifies the timeout has been started.
     * <p>
     * Started thread will sleep for {@link #getTimeoutMs()} milliseconds and,
     * if not interrupted, will update the timeout date and notify the timeout.
     *
     * @param actionId the sent action's id
     */
    protected void startTimeoutThread(int actionId) {
        if (isWaitingForFeedback())
            throw new IllegalStateException("Already waiting for feedback");

        // Prepare timer's thread
        timeoutThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(getTimeoutMs());
                    setActionTimeout();
                } catch (InterruptedException ignore) {
                }
                timeoutThread = null;
            }
        };

        // Update, Start and notify
        setActionSent(actionId);
    }


    // Component listeners' methods

    /**
     * Second part of the action feedback management: process any state change
     * from current component.
     * <p>
     * This method updates the feedback received date, interrupts the timeout
     * thread and notifies the feedback received.
     * <p>
     * If the timeout thread is not running, then this method does nothing.
     *
     * @param newState the new state
     * @param oldState the old state
     */
    protected void processStateUpdate(Object newState, Object oldState) {
        if (!isWaitingForFeedback()) {
            // Only notify the update
            notifyUpdateReceived(newState, oldState);
            return;
        }

        // Notify feedback received
        setFeedbackReceivedDate(newState, oldState);
    }

    /**
     * Reset the handler's `lastSent`, `lastFeedbackReceived` and `lastTimeout`.
     * <p>
     * This method also interrupts the timeout thread, if any.
     */
    @Override
    public void doReset() {
        // halt thread, if any
        if (isWaitingForFeedback())
            timeoutThread.interrupt();

        actionId = -1;
        lastSent = null;
        lastFeedbackReceived = null;
        lastTimeout = null;
    }


    // Notify methods

    /**
     * Notify the observer that the component has changed.
     *
     * @param oldComp the old component
     */
    private void notifyComponentChanged(JSLAction oldComp) {
        if (getObserver() == null) return;
        getObserver().onComponentChanged(this, getComponent(), oldComp);
    }

    /**
     * Notify the observer that the action command has been sent.
     */
    private void notifyStarted() {
        getObserver().onStarted(this, getActionID());
    }

    /**
     * Notify the observer that the timeout has expired.
     */
    private void notifyTimeout() {
        getObserver().onTimeout(this, getActionID());
    }

    /**
     * Notify the observer that the feedback has been received.
     *
     * @param newState the new state
     * @param oldState the old state
     */
    private void notifyFeedbackReceived(Object newState, Object oldState) {
        getObserver().onFeedbackReceived(this, getActionID(), newState, oldState);
    }

    /**
     * Notify the observer that the state has been updated.
     *
     * @param newState the new state
     * @param oldState the old state
     */
    private void notifyUpdateReceived(Object newState, Object oldState) {
        getObserver().onUpdateReceived(this, getActionID(), newState, oldState);
    }

}
