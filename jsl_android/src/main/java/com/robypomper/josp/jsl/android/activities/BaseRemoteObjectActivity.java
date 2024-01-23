package com.robypomper.josp.jsl.android.activities;

import android.os.Bundle;

import com.robypomper.josp.jsl.android.JSLAndroid;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;


/**
 * Base class for all activities that need to work with a JSLRemoteObject.
 * <p>
 * The BaseRemoteObjectActivity class is an abstract class that extends AppCompatActivity. This class
 * serves as a base class for other activities in the application that require common
 * functionalities related to JSL remote objects.
 * <p>
 * The BaseRemoteObjectActivity class provides the following functionalities:
 * <p>
 * - It provides a reference to the JSLClient object that is used to communicate with the JSLService
 * - It look for the object ID of the remote object that must be used by the activity from the
 * activity's intent, or from the activity's saved instance state.
 * - It provides a reference to the JSLRemoteObject object that represents the remote object
 * that is used by the activity
 * - It provides a set of methods to handle the events related with the remote object
 * - It provides a set of methods to find the JSLComponent exposed by the activity's remote object
 * <p>
 * This class registers itself as a listener for the JSLRemoteObject object that is used by the
 * activity. When the JSLRemoteObject object is ready, the class emits the onRemoteObjectReady
 * event. This event is used by the activity to initialize its UI.<br/>
 * The onRemoteObjectReady event is emitted only when the remote object is ready. It means that
 * the remote object has been registered, and its structure has been received from the JSLService.
 * To get notified when the remote object is (de)registered or his structure is changed, the
 * activity must implement the onRemoteObjectRegistered, onRemoteObjectDeregistered or the
 * onRemoteObjectStructureChanged methods.
 * <p>
 * This class also registers itself as a listener for the JSLRemoteObject object that is used by
 * the activity. When the JSLRemoteObject object is connected or disconnected, the class emits the
 * onRemoteObjectConnected and onRemoteObjectDisconnected events. These events are used by the
 * activity to update its UI.<br/>
 * As expected those events are emitted only when the connection status of the remote object
 * changes. Regardless of the connection type (local or cloud) the object is using.<br/>
 * The class also emits the Local/Cloud versions of the previous events.
 */
public class BaseRemoteObjectActivity extends BaseJSLActivity {

    /**
     * The name of the parameter that is used to pass the object ID to the activity.
     */
    public final static String PARAM_OBJ_ID = JSLAndroid.Params.OBJID;
    private String objId = null;
    private JSLRemoteObject remObj;
    private boolean onReadyEmitted = false;

    // Android

    /**
     * Called when the activity is starting. This method looks for the object ID of the remote
     * object that must be used by the activity from the activity's intent, or from the activity's
     * saved instance state. If the object ID is not found, the method throws a RuntimeException.
     * <p>
     * The method also checks the state of the JSLClient object. If the JSLClient object is in the
     * RUN state, the method looks for the remote object that has the specified object ID. If the
     * remote object is found, the method registers itself as a listener for the remote object.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). Note: Otherwise it is
     *                           null. This value may be null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onReadyEmitted = false;
        lookForObjId(savedInstanceState);

        super.onCreate(savedInstanceState);

        getJSLClient().getJSLListeners().addObjsMngrListenersByID(objId, remObjByIdListener);
    }

    /**
     * Called when the activity is no longer visible to the user. This method deregisters itself
     * as a listener for the remote object that is used by the activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (remObj != null) deregisterRemoteObject();

        getJSLClient().getJSLListeners().removeObjsMngrListenersByID(objId, remObjByIdListener);
    }

    @Override
    protected void onJSLReady() {
        super.onJSLReady();

        // Search for remote object by obj's id
        JSLObjsMngr objsMngr = getJSLClient().getJSL().getObjsMngr();
        JSLRemoteObject obj = objsMngr.getById(objId);
        if (obj != null)
            // register remote object
            registerRemoteObject(obj);
    }

    private void lookForObjId(Bundle savedInstanceState) {
        // get object id
        objId = null;
        if (getIntent().getExtras() != null)
            objId = getIntent().getExtras().getString(PARAM_OBJ_ID);
        if (objId == null && savedInstanceState != null)
            objId = savedInstanceState.getString(PARAM_OBJ_ID);
        // TODO implement obj id storage into shared preferences
        //if (objId == null)
        // look into shared preferences
        if (objId == null)
            throw new RuntimeException(String.format("Can't init '%s' without specify '%s' param", this.getLocalClassName(), PARAM_OBJ_ID));
    }


    // Getters

    /**
     * @return the object ID of the remote object that is used by the activity.
     */
    protected String getObjId() {
        return objId;
    }

    /**
     * The method returns the JSLRemoteObject object that is used by the activity.
     * <p>
     * The method returns null if the remote object has never been connected to the application.
     * The returned object is not null even if the remote object is not ready yet. To check if the
     * remote object is ready, the activity must call the isRemoteObjectReady method or use the
     * onRemoteObjectReady event.
     *
     * @return the JSLRemoteObject object that is used by the activity.
     */
    protected JSLRemoteObject getRemoteObject() {
        return remObj;
    }

    /**
     * The method returns true if the remote object is ready. It means that the remote object has
     * been registered, and its structure has been received.
     */
    protected boolean isRemoteObjectReady() {
        return onReadyEmitted;
    }

    // Events methods for sub-classes

    private void emitOnRemoteObjectReady() {
        if (!onReadyEmitted && remObj != null && remObj.getStruct() != null && remObj.getStruct().getStructure() != null) {
            onReadyEmitted = true;
            onRemoteObjectReady();
        }
    }

    private void resetOnRemoteObjectReady() {
        onReadyEmitted = false;
    }

    /**
     * The method is called when the remote object is ready. It means that the remote object has
     * been registered, and its structure has been received.
     * <p>
     * The value returned by the isRemoteObjectReady method is updated before this method is called.
     */
    protected void onRemoteObjectReady() {
    }

    /**
     * The method is called when the remote object is registered.
     * <p>
     * In the John ecosystem, that correspond to the JOD object connection to the current JSL
     * service, and the reception of the object presentation message. So, after this method is
     * called, the remote object can provide is info and the connection status, but the structure
     * of the object is not yet available.
     */
    protected void onRemoteObjectRegistered() {
    }

    /**
     * The method is called when the remote object is deregistered.
     * <p>
     * In the John ecosystem, that correspond to the JOD object disconnection from the current JSL
     * service. So, after this method is called, the remote object can still provide object's info,
     * connection status and structure, but it will not receive any update.
     */
    protected void onRemoteObjectDeregistered() {
    }

    /**
     * The method is called when the structure of the remote object is changed.
     * <p>
     * In the John ecosystem, that correspond to the reception of the object structure message.
     * This message is sent by the JSL service immediately after the object presentation message.
     */
    protected void onRemoteObjectStructureChanged() {
    }

    /**
     * The method is called when the remote object is connected.
     * <p>
     * In the John ecosystem, that correspond to the JOD object connection to the current JSL
     * service. So, after this method is called, the remote object can send updates to the current
     * JSL service.
     * <p>
     * This method is called only if the remote object is not already connected via local or cloud.
     * So, it will be called only if the remote object was not connected nor via local or cloud.
     */
    protected void onRemoteObjectConnected() {
    }

    /**
     * The method is called when the remote object is connected via local.
     * <p>
     * In the John ecosystem, that correspond to the JOD object connection to the current JSL
     * service via local communication.
     */
    protected void onRemoteObjectConnectedLocal() {
    }

    /**
     * The method is called when the remote object is connected via cloud.
     * <p>
     * In the John ecosystem, that correspond to the JOD object connection to the current JSL
     * service via cloud communication.
     */
    protected void onRemoteObjectConnectedCloud() {
    }

    /**
     * The method is called when the remote object is disconnected.
     * <p>
     * In the John ecosystem, that correspond to the JOD object disconnection from the current JSL
     * service. So, after this method is called, the remote object can't send updates to the current
     * JSL service.
     * <p>
     * This method is called only if the remote object is not already disconnected via local or
     * cloud. So, it will be called only if the remote object was not disconnected nor via local
     * or cloud.
     */
    protected void onRemoteObjectDisconnected() {
    }

    /**
     * The method is called when the remote object is disconnected via local.
     * <p>
     * In the John ecosystem, that correspond to the JOD object disconnection from the current JSL
     * service via local communication.
     */
    protected void onRemoteObjectDisconnectedLocal() {
    }

    /**
     * The method is called when the remote object is disconnected via cloud.
     * <p>
     * In the John ecosystem, that correspond to the JOD object disconnection from the current JSL
     * service via cloud communication.
     */
    protected void onRemoteObjectDisconnectedCloud() {
    }


    // Utils for sub-classes

    private JSLComponent findComponent(String path) {
        return remObj.getStruct().getComponent(path);
    }

    /**
     * The method returns the JSLComponent object that is exposed by the remote object that is used
     * by the activity.
     * <p>
     * The method returns null if the remote object is not ready yet. To check if the remote object
     * is ready, the activity must call the isRemoteObjectReady method or use the onRemoteObjectReady
     * event.
     *
     * @param path the path of the component to find.
     * @return the JSLComponent object that is exposed by the remote object that is used by the
     * activity.
     */
    protected JSLContainer findContainerComponent(String path) {
        return (JSLContainer) findComponent(path);
    }

    /**
     * The method returns the JSLBooleanState object that is exposed by the remote object that is
     * used by the activity.
     * <p>
     * The method returns null if the remote object is not ready yet. To check if the remote object
     * is ready, the activity must call the isRemoteObjectReady method or use the onRemoteObjectReady
     * event.
     *
     * @param path the path of the component to find.
     * @return the JSLBooleanState object that is exposed by the remote object that is used by the
     * activity.
     */
    protected JSLBooleanState findBooleanStateComponent(String path) {
        return (JSLBooleanState) findComponent(path);
    }

    /**
     * The method returns the JSLBooleanAction object that is exposed by the remote object that is
     * used by the activity.
     * <p>
     * The method returns null if the remote object is not ready yet. To check if the remote object
     * is ready, the activity must call the isRemoteObjectReady method or use the onRemoteObjectReady
     * event.
     *
     * @param path the path of the component to find.
     * @return the JSLBooleanAction object that is exposed by the remote object that is used by the
     * activity.
     */
    protected JSLBooleanAction findBooleanActionComponent(String path) {
        return (JSLBooleanAction) findComponent(path);
    }

    /**
     * The method returns the JSLRangeState object that is exposed by the remote object that is
     * used by the activity.
     * <p>
     * The method returns null if the remote object is not ready yet. To check if the remote object
     * is ready, the activity must call the isRemoteObjectReady method or use the onRemoteObjectReady
     * event.
     *
     * @param path the path of the component to find.
     * @return the JSLRangeState object that is exposed by the remote object that is used by the
     * activity.
     */
    protected JSLRangeState findRangeStateComponent(String path) {
        return (JSLRangeState) findComponent(path);
    }

    /**
     * The method returns the JSLRangeAction object that is exposed by the remote object that is
     * used by the activity.
     * <p>
     * The method returns null if the remote object is not ready yet. To check if the remote object
     * is ready, the activity must call the isRemoteObjectReady method or use the onRemoteObjectReady
     * event.
     *
     * @param path the path of the component to find.
     * @return the JSLRangeAction object that is exposed by the remote object that is used by the
     * activity.
     */
    protected JSLRangeAction findRangeActionComponent(String path) {
        return (JSLRangeAction) findComponent(path);
    }


    // Remote obj

    private void registerRemoteObject(JSLRemoteObject obj) {
        remObj = obj;

        remObj.getComm().addListener(remObjConnListener);
        remObj.getStruct().addListener(remObjStructListener);

        onRemoteObjectRegistered();
        emitOnRemoteObjectReady();
    }

    private void deregisterRemoteObject() {
        onRemoteObjectDeregistered();
        resetOnRemoteObjectReady();

        remObj.getComm().removeListener(remObjConnListener);
        remObj.getStruct().removeListener(remObjStructListener);

        remObj = null;
    }


    // Remote listeners

    private final JSLObjsMngr.ObjsMngrListener remObjByIdListener = new JSLObjsMngr.ObjsMngrListener() {

        @Override
        public void onObjAdded(JSLRemoteObject obj) {
            if (remObj != null) return;
            registerRemoteObject(obj);
        }

        @Override
        public void onObjRemoved(JSLRemoteObject obj) {
            if (remObj == null || remObj != obj) return;
            deregisterRemoteObject();
        }

    };

    private final ObjComm.RemoteObjectConnListener remObjConnListener = new ObjComm.RemoteObjectConnListener() {

        @Override
        public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            // emit only if not already connected via cloud
            if (!obj.getComm().isCloudConnected()) onRemoteObjectConnected();
            onRemoteObjectConnectedLocal();
        }

        @Override
        public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            // emit only if not connected via cloud
            if (!obj.getComm().isCloudConnected()) onRemoteObjectConnected();
            onRemoteObjectDisconnectedLocal();
        }

        @Override
        public void onCloudConnected(JSLRemoteObject obj) {
            // emit only if not already connected via local
            if (!obj.getComm().isLocalConnected()) onRemoteObjectConnected();
            onRemoteObjectConnectedCloud();
        }

        @Override
        public void onCloudDisconnected(JSLRemoteObject obj) {
            // emit only if not connected via local
            if (!obj.getComm().isLocalConnected()) onRemoteObjectDisconnected();
            onRemoteObjectDisconnectedCloud();
        }

    };

    private final ObjStruct.RemoteObjectStructListener remObjStructListener = new ObjStruct.RemoteObjectStructListener() {

        @Override
        public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
            onRemoteObjectStructureChanged();
            emitOnRemoteObjectReady();
        }

    };

}
