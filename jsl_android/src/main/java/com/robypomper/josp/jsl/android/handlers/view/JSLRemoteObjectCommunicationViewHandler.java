package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLRemoteObjectCommunicationHandler;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Class for {@link JSLRemoteObject}'s communication view handlers.
 * <p>
 * Supported views:
 * <ul>
 *     <li>{@link #UI_REM_OBJ_COMM_STATUS} ({@link TextView}): the remote object's communication status</li>
 *     <li>{@link #UI_REM_OBJ_COMM_LAST} ({@link TextView}): the remote object's last connection or disconnection</li>
 *     <li>{@link #UI_REM_OBJ_COMM_LAST_CONN} ({@link TextView}): the remote object's last connection date</li>
 *     <li>{@link #UI_REM_OBJ_COMM_LAST_DISC} ({@link TextView}): the remote object's last disconnection date</li>
 * </ul>
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_INIT_STATUS_TXT}: default string to use as communication status when the remote object is not available</li>
 *     <li>{@link #DEF_CONNECTED}: default string to use as communication status when the remote object is connected</li>
 *     <li>{@link #DEF_DISCONNECTED}: default string to use as communication status when the remote object is disconnected</li>
 *     <li>{@link #DEF_FORMAT_LAST_CONN}: default string format to use for UI_REM_OBJ_COMM_LAST field,  when the remote object is connected</li>
 *     <li>{@link #DEF_FORMAT_LAST_DISC}: default string format to use for UI_REM_OBJ_COMM_LAST field,  when the remote object is disconnected</li>
 * </ul>
 *
 * @noinspection unused
 */
public class JSLRemoteObjectCommunicationViewHandler
        extends JSLBaseViewHandler
        implements JSLRemoteObjectCommunicationHandler.Observer {

    // Constants

    /**
     * The id for the object's communication status {@link TextView}
     */
    public final static int UI_REM_OBJ_COMM_STATUS = R.id.txtJSLRemObjCommStatus;
    /**
     * The id for the object's communication last {@link TextView}
     * <p>
     * His value is formatted using the {@link #getFormatLastConn()} and
     * {@link #getFormatLastDisc()} methods.
     */
    public final static int UI_REM_OBJ_COMM_LAST = R.id.txtJSLRemObjCommLast;
    /**
     * The id for the object's communication last connection {@link TextView}
     */
    public final static int UI_REM_OBJ_COMM_LAST_CONN = R.id.txtJSLRemObjCommLastConn;
    /**
     * The id for the object's communication last disconnection {@link TextView}
     */
    public final static int UI_REM_OBJ_COMM_LAST_DISC = R.id.txtJSLRemObjCommLastDisc;
    /**
     * Default object's communication status used when the remote object is not available
     */
    public final static String DEF_INIT_STATUS_TXT = "Unknown";
    /**
     * Default string format to use when the remote object is connected.
     * <p>
     * This string can contains a single `%s` to represent the last connection
     * date.
     */
    public final static String DEF_FORMAT_LAST_CONN = "Last connection @ %s";
    /**
     * Default string format to use when the remote object is disconnected.
     * <p>
     * This string can contains a single `%s` to represent the last disconnection
     * date.
     */
    public final static String DEF_FORMAT_LAST_DISC = "Last disconnection @ %s";
    /**
     * Default string to use as status when the remote object is connected.
     */
    public final static String DEF_CONNECTED = "Connected";
    /**
     * Default string to use as status when the remote object is disconnected.
     */
    public final static String DEF_DISCONNECTED = "Disconnected";
    /**
     * Default background to use on container view when the remote object is disconnected.
     */
    private static final Drawable DEF_DISC_BACKGROUND = new ColorDrawable(Color.argb(128, 255, 255, 255));


    // Internal vars


    /**
     * The object's state handler, it's not final to allows view recycling.
     */
    private final JSLRemoteObjectCommunicationHandler objectCommHandler;
    /**
     * The container of the view, if `null` then the main View is used as container.
     */
    private ViewGroup container;
    /**
     * The original background of the container (used when the remote object is connected).
     */
    private Drawable containerOriginalBackground;
    /**
     * The background to use on container view when the remote object is disconnected.
     */
    private Drawable containerDisconnectedBackground = DEF_DISC_BACKGROUND;
    /**
     * String to use as status when the remote object is not available
     */
    private String initStatusTxt = DEF_INIT_STATUS_TXT;
    /**
     * String to use as status when the remote object is connected.
     */
    private String valConnectedTxt = DEF_CONNECTED;
    /**
     * String to use as status when the remote object is disconnected.
     */
    private String valDisconnectedTxt = DEF_DISCONNECTED;
    /**
     * String format to use when the remote object is connected.
     */
    private String formatLastConn = DEF_FORMAT_LAST_CONN;
    /**
     * String format to use when the remote object is disconnected.
     */
    private String formatLastDisc = DEF_FORMAT_LAST_DISC;


    // Constructors

    /**
     * Create a new JSLRemoteObjectConnectionViewHandler based on given mainView
     * and remote object.
     * <p>
     * This constructor uses the default time and date formats.
     *
     * @param ctx      the context
     * @param mainView the mainView to use to look for component's Views
     * @param remObj   the remote object to show
     */
    public JSLRemoteObjectCommunicationViewHandler(Context ctx, ViewGroup mainView, JSLRemoteObject remObj) {
        this(ctx, mainView, remObj, DEF_FORMAT_TIME, DEF_FORMAT_DATE);
    }

    /**
     * Create a new JSLRemoteObjectConnectionViewHandler based on given mainView
     * and remote object.
     *
     * @param ctx        the context
     * @param mainView   the mainView to use to look for component's Views
     * @param remObj     the remote object to show
     * @param formatTime the time format to use for date (only time) to string conversion
     * @param formatDate the time format to use for date (only date) to string conversion
     */
    public JSLRemoteObjectCommunicationViewHandler(Context ctx, ViewGroup mainView, JSLRemoteObject remObj, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView, formatTime, formatDate);
        objectCommHandler = new JSLRemoteObjectCommunicationHandler(this, remObj);
        setRemoteObject(remObj);
    }


    // Getters and Setters

    /**
     * Get the remote object managed by this handler.
     *
     * @return the remote object managed by this handler
     */
    public JSLRemoteObject getRemoteObject() {
        return objectCommHandler.getRemoteObject();
    }

    /**
     * Set the remote object to show.
     * <p>
     * This method is used to change the remote object to show. It's used to
     * recycle the view, when the view is recycled, the newComp is changed
     * and the handler is updated to show the new newComp.
     *
     * @param newRemObj the remote object to show
     */
    public void setRemoteObject(JSLRemoteObject newRemObj) {
        if (getRemoteObject() == newRemObj) return;

        objectCommHandler.setRemoteObject(newRemObj);

        updateUI();
    }

    /**
     * Get the remote object communication status.
     *
     * @return the remote object communication status
     */
    public boolean getStatus() {
        return getRemoteObject().getComm().isConnected();
    }

    /**
     * Get the remote object communication status ready to be used into the UI.
     *
     * @return the remote object communication status, or the {@link #getInitStatusTxt()} value,
     * if the remote object is not available
     */
    public String getStatusTxt() {
        if (getRemoteObject() == null) return getInitStatusTxt();
        return convertValue(getStatus());
    }

    /**
     * Convert given status to a string using the {@link #getValConnectedTxt()}  and
     * {@link #getValDisconnectedTxt()}  methods.
     *
     * @return the string to use as remote object communication status
     */
    private String convertValue(Boolean status) {
        return status ? getValConnectedTxt() : getValDisconnectedTxt();
    }

    /**
     * Get the container of the view.
     *
     * @return the container of the view, or the main View if the container is `null`
     */
    protected ViewGroup getContainer() {
        return container != null ? container : getMainView();
    }

    /**
     * Set the container of the view. If given container is `null`, then
     * the main View is used as container.
     *
     * @param container the container of the view
     */
    public void setContainer(ViewGroup container) {
        this.container = container;
        this.containerOriginalBackground = container.getBackground();
    }

    /**
     * Get the background to use on container view when the remote object is disconnected.
     *
     * @return the background to use on container view when the remote object is disconnected
     */
    public Drawable getContainerDisconnectedBackground() {
        return containerDisconnectedBackground;
    }

    /**
     * Set the background to use on container view when the remote object is disconnected.
     *
     * @param containerDisconnectedBackground the background to use on container view when the remote object is disconnected
     */
    public void setContainerDisconnectedBackground(Drawable containerDisconnectedBackground) {
        this.containerDisconnectedBackground = containerDisconnectedBackground;
    }


    // Getters and Setters for format vars

    /**
     * Get the string format to use when the remote object is connected.
     *
     * @return the string format to use when the remote object is connected
     */
    public String getFormatLastConn() {
        return formatLastConn;
    }

    /**
     * Set the string format to use when the remote object is connected.
     * <p>
     * If the handler's remote object is available, then the UI (states) is updated.
     *
     * @param formatLastConn the string format to use when the remote object is
     *                       connected
     */
    public void setFormatLastConn(String formatLastConn) {
        if (this.formatLastConn.equals(formatLastConn)) return;

        this.formatLastConn = formatLastConn;
        if (getRemoteObject() != null) updateUIStateRefresh();
    }

    /**
     * Get the string format to use when the remote object is disconnected.
     *
     * @return the string format to use when the remote object is disconnected
     */
    public String getFormatLastDisc() {
        return formatLastDisc;
    }

    /**
     * Set the string format to use when the remote object is disconnected.
     * <p>
     * If the handler's remote object is available, then the UI (states) is updated.
     *
     * @param formatLastDisc the string format to use when the remote object is
     *                       disconnected
     */
    public void setFormatLastDisc(String formatLastDisc) {
        if (this.formatLastDisc.equals(formatLastDisc)) return;

        this.formatLastDisc = formatLastDisc;
        if (getRemoteObject() != null) updateUIStateRefresh();
    }


    // Getter and Setter for init vars

    /**
     * Get the string to use as status when the remote object is not available.
     *
     * @return String to use as status when the remote object is not available
     */
    public String getInitStatusTxt() {
        return initStatusTxt;
    }

    /**
     * Set the string to use as status when the remote object is not available.
     * <p>
     * If the handler's remote object is NOT available, then the UI (states) is updated.
     *
     * @param initStatusTxt String to use as status when the remote object is not available
     */
    public void setInitStatusTxt(String initStatusTxt) {
        if (this.initStatusTxt.equals(initStatusTxt)) return;

        this.initStatusTxt = initStatusTxt;
        if (getRemoteObject() == null) updateUIStateRefresh();
    }


    // Getters and Setters for values vars

    /**
     * Get the string to use as status when the remote object is connected.
     *
     * @return the string to use as state when the remote object is connected
     */
    public String getValConnectedTxt() {
        return valConnectedTxt;
    }

    /**
     * Set the string to use as status when the remote object is connected.
     * <p>
     * If the handler's remote object is NOT available and the communication status is
     * `true`, then the UI (states) is updated.
     *
     * @param valConnectedTxt the string to use as status when the remote object is connected
     */
    public void setValConnectedTxt(String valConnectedTxt) {
        if (this.valConnectedTxt.equals(valConnectedTxt)) return;

        this.valConnectedTxt = valConnectedTxt;
        if (getRemoteObject() != null && getStatus()) updateUIStateRefresh();
    }

    /**
     * Get the string to use as status when the remote object is connected.
     *
     * @return the string to use as state when the remote object is connected
     */
    public String getValDisconnectedTxt() {
        return valDisconnectedTxt;
    }

    /**
     * Set the string to use as status when the remote object is disconnected.
     * <p>
     * If the handler's remote object is NOT available and the communication status is
     * `false`, then the UI (states) is updated.
     *
     * @param valDisconnectedTxt the string to use as status when the remote object is disconnected
     */
    public void setValDisconnectedTxt(String valDisconnectedTxt) {
        if (this.valDisconnectedTxt.equals(valDisconnectedTxt)) return;

        this.valDisconnectedTxt = valDisconnectedTxt;
        if (getRemoteObject() != null && !getStatus()) updateUIStateRefresh();
    }


    // JSLRemoteObjectConnectionHandler Getters and Setters

    /**
     * Get the remote object last connection or disconnection ready to be used
     * into the UI.
     *
     * @return the remote object last connection or disconnection, or the
     * {@link #convertDate(Date)} value, if the component or the last update is
     * not available
     */
    public String getLastTxt() {
        if (getStatus())
            return String.format(getFormatLastConn(), getLastConnectionTxt());
        else
            return String.format(getFormatLastDisc(), getLastDisconnectionTxt());
    }

    /**
     * Get the remote object last connection.
     *
     * @return the remote object last connection
     */
    public Date getLastConnection() {
        return objectCommHandler.getLastConnection();
    }

    /**
     * Get the remote object last connection ready to be used into the UI.
     *
     * @return the remote object last connection, or the {@link #convertDate(Date)} value,
     * if the component or the last update is not available
     */
    public String getLastConnectionTxt() {
        return convertDate(getLastConnection());
    }

    /**
     * Get the remote object last disconnection.
     *
     * @return the remote object last disconnection
     */
    public Date getLastDisconnection() {
        return objectCommHandler.getLastDisconnection();
    }

    /**
     * Get the remote object last disconnection ready to be used into the UI.
     *
     * @return the remote object last disconnection, or the {@link #convertDate(Date)} value,
     * if the component or the last update is not available
     */
    public String getLastDisconnectionTxt() {
        return convertDate(getLastDisconnection());
    }


    // UI Methods

    /**
     * Update all handled Views into the UI.
     * <p>
     * This method is called by the setComponent method, so you don't need to
     * call it manually. Although, you can call it manually to force the UI
     * update.
     */
    public void updateUI() {
        updateUIStateRefresh();
    }

    /**
     * Update the views used as labels (all component's info not related with
     * states nor actions) into the UI.
     */
    protected void updateUIStateRefresh() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                trySetText(getMainView(), UI_REM_OBJ_COMM_STATUS, getStatusTxt());
                trySetText(getMainView(), UI_REM_OBJ_COMM_LAST, getLastTxt());
                trySetText(getMainView(), UI_REM_OBJ_COMM_LAST_CONN, getLastConnectionTxt());
                trySetText(getMainView(), UI_REM_OBJ_COMM_LAST_DISC, getLastDisconnectionTxt());
            }
        });
    }

    /**
     * Update the UI Views disabled (lock) or enabled (unlock).
     * <p>
     * This method disable both the main View and the container, and change the
     * background color of the container to gray when the remote object is
     * disconnected.
     */
    protected void lockUIBecauseDisconnection(boolean lock) {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                getContainer().setEnabled(!lock);
                getMainView().setEnabled(!lock);

                //getContainer().setBackgroundColor(lock ? 0xFFCCCCCC : 0xFFFFFFFF);
                getContainer().setBackground(lock ? containerDisconnectedBackground : containerOriginalBackground);
            }
        });
    }


    // JSLRemoteObjectConnectionHandler.Observer

    /**
     * Listener for the handler's remote object change.
     * <p>
     * Calls the {@link #updateUIStateRefresh()} method and the
     * {@link #lockUIBecauseDisconnection(boolean)} method.
     *
     * @param handler   the handler
     * @param newRemObj the new remote object
     * @param oldRemObj the old remote object
     */
    @Override
    public void onRemoteObjectChanged(JSLRemoteObjectCommunicationHandler handler, JSLRemoteObject newRemObj, JSLRemoteObject oldRemObj) {
        updateUIStateRefresh();
        boolean availableAndConnected = newRemObj != null && newRemObj.getComm().isConnected();
        lockUIBecauseDisconnection(!availableAndConnected);
    }

    /**
     * Listener for the handler's remote object connection.
     * <p>
     * Starts the UI unlock for connection chain.
     *
     * @param handler the handler
     */
    @Override
    public void onRemoteObjectConnection(JSLRemoteObjectCommunicationHandler handler) {
        updateUIStateRefresh();
        lockUIBecauseDisconnection(false);
    }

    /**
     * Listener for the handler's remote object disconnection.
     * <p>
     * Starts the UI lock for disconnection chain.
     *
     * @param handler the handler
     */
    @Override
    public void onRemoteObjectDisconnection(JSLRemoteObjectCommunicationHandler handler) {
        updateUIStateRefresh();
        lockUIBecauseDisconnection(true);
    }
}
