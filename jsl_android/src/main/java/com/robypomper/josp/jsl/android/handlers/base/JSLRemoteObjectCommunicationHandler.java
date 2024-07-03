package com.robypomper.josp.jsl.android.handlers.base;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;

import java.util.Date;


/**
 * Handler for a <b>remote object's communication status</b>.
 * <p>
 * This class provides a set of methods to handle the remote object's connection
 * status, and to notify an observer when the connection status changes.
 * <p>
 * In addition to the connection status, this class also provides the last
 * connection and disconnection date.
 * @noinspection unused
 */
public class JSLRemoteObjectCommunicationHandler extends JSLBaseHandler {

    // Internal vars

    /**
     * The component to show, it's not final to allows view recycling.
     */
    private JSLRemoteObject remObj;
    /**
     * Last object connection date
     */
    private Date lastConnection;
    /**
     * Last object disconnection date
     */
    private Date lastDisconnection;


    // Handler observer

    /**
     * Observer for a remote object's connection status.
     */
    public interface Observer extends JSLBaseHandler.Observer {

        void onRemoteObjectChanged(JSLRemoteObjectCommunicationHandler handler, JSLRemoteObject newRemObj, JSLRemoteObject oldRemObj);

        void onRemoteObjectConnection(JSLRemoteObjectCommunicationHandler handler);

        void onRemoteObjectDisconnection(JSLRemoteObjectCommunicationHandler handler);

    }


    // Constructors

    /**
     * Create a new handler for the given remote object.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param remObj   the remote object to handle
     */
    public JSLRemoteObjectCommunicationHandler(Observer observer, JSLRemoteObject remObj) {
        super(observer);
        setRemoteObject(remObj);
    }

    /**
     * Create a new handler from the given one.
     *
     * @param observer the observer to notify when the object's connection changes
     * @param other    the other handler to copy the extra info from and
     *                 the remote object reference
     */
    public JSLRemoteObjectCommunicationHandler(Observer observer, JSLRemoteObjectCommunicationHandler other) {
        super(observer, other);
        if (other != null)
            setRemoteObject(other.getRemoteObject());
    }


    /**
     * Deregister the listener and clean the remote object reference.
     *
     * @throws Throwable the Exception raised by this method
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (remObj != null) {
            removeListener();
            remObj = null;
        }
    }


    // Getters and Setters

    /**
     * Get the observer for the handler.
     *
     * @return the observer for the handler
     */
    @Override
    protected Observer getObserver() {
        return (Observer) super.getObserver();
    }

    /**
     * Get the remote object managed by this handler.
     *
     * @return the remote object managed by this handler
     */
    public JSLRemoteObject getRemoteObject() {
        return remObj;
    }

    /**
     * Set the remote object to manage.
     * <p>
     * This method also manage the object's connection listener, resets the
     * handlers data, and notifies the observer that the object has changed.
     *
     * @param newRemObj the remote object to manage
     */
    public void setRemoteObject(JSLRemoteObject newRemObj) {
        if (getRemoteObject() == newRemObj) return;

        // clean
        JSLRemoteObject oldObj = getRemoteObject();
        if (oldObj != null) {
            removeListener();
            lastConnection = null;
            lastDisconnection = null;
        }

        // set
        this.remObj = newRemObj;

        // configure
        if (newRemObj != null) {
            if (newRemObj.getComm().isConnected()) notifyRemObjConnection();
            else notifyRemObjDisconnection();
            addListener();
        }

        // notify
        notifyRemObjChanged(oldObj);
    }

    /**
     * Get the last object's connection date.
     *
     * @return the last object's connection date
     */
    public Date getLastConnection() {
        return lastConnection;
    }

    /**
     * Set the last object's connection date to the current date and time.
     * <p>
     * This method also notify the observer that the connection has been updated.
     */
    protected void setLastConnection() {
        lastConnection = JavaDate.getNowDate();
        notifyRemObjConnection();
    }

    /**
     * Get the last object's disconnection date.
     *
     * @return the last object's disconnection date
     */
    public Date getLastDisconnection() {
        return lastDisconnection;
    }

    /**
     * Set the last object's disconnection date to the current date and time.
     * <p>
     * This method also notify the observer that the disconnection has been updated.
     */
    protected void setLastDisconnection() {
        lastDisconnection = JavaDate.getNowDate();
        notifyRemObjDisconnection();
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
        if (!this.getClass().isInstance(handler))
            throw new IllegalArgumentException("The given handler is not an instance of " + getClass().getName());
        JSLRemoteObjectCommunicationHandler other = (JSLRemoteObjectCommunicationHandler) handler;
        lastConnection = other.lastConnection;
        lastDisconnection = other.lastDisconnection;
    }


    // Object Listeners

    /**
     * The listener for the remote object's connection status.
     * <p>
     * It can not be final because it's initialized in the addListener() method,
     * called by the super class constructors, when the listener is not yet
     * initialized.
     */
    private ObjComm.RemoteObjectConnListener objectConnListener;

    /**
     * Add the listener to the remote object communication manager.
     */
    private void addListener() {
        if (objectConnListener == null) initObjectConnListener();
        if (getRemoteObject() == null) return;
        getRemoteObject().getComm().addListener(objectConnListener);
    }

    /**
     * Remove the listener from the remote object communication manager.
     */
    private void removeListener() {
        if (getRemoteObject() == null) return;
        getRemoteObject().getComm().removeListener(objectConnListener);
    }

    /**
     * Initialize the listener for the remote object's connection status.
     * <p>
     * This method is used to initialize the listener during super class
     * initialization, when it's not yet initialized.
     */
    private void initObjectConnListener() {
        objectConnListener = new ObjComm.RemoteObjectConnListener() {
            @Override
            public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                if (obj == getRemoteObject())
                    processObjectConnUpdate();
            }

            @Override
            public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                if (obj == getRemoteObject())
                    processObjectConnUpdate();
            }

            @Override
            public void onCloudConnected(JSLRemoteObject obj) {
                if (obj == getRemoteObject())
                    processObjectConnUpdate();
            }

            @Override
            public void onCloudDisconnected(JSLRemoteObject obj) {
                if (obj == getRemoteObject())
                    processObjectConnUpdate();
            }
        };
    }

    /**
     * Method called by object connection listener (added by addListener() method) when
     * the connection status changes.
     * <p>
     * This method updates the last connection/disconnection date, and then
     * notifies the observer ({@link Observer#onRemoteObjectConnection(JSLRemoteObjectCommunicationHandler)}
     * and {@link Observer#onRemoteObjectDisconnection(JSLRemoteObjectCommunicationHandler)}).
     */
    private void processObjectConnUpdate() {
        if (getRemoteObject().getComm().isConnected())
            setLastConnection();
        else
            setLastDisconnection();
    }


    // Notify methods

    /**
     * Notify the observer that the remote object has changed.
     *
     * @param oldRemObj the old remote object
     */
    private void notifyRemObjChanged(JSLRemoteObject oldRemObj) {
        if (getObserver() == null) return;
        getObserver().onRemoteObjectChanged(this, getRemoteObject(), oldRemObj);
    }

    /**
     * Notify the observer that the remote object has been connected.
     */
    private void notifyRemObjConnection() {
        if (getRemoteObject() == null) return;
        if (getObserver() == null) return;
        getObserver().onRemoteObjectConnection(this);
    }

    /**
     * Notify the observer that the remote object has been disconnected.
     */
    private void notifyRemObjDisconnection() {
        if (getRemoteObject() == null) return;
        if (getObserver() == null) return;
        getObserver().onRemoteObjectDisconnection(this);
    }

}
