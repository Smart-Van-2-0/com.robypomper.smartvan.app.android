package com.robypomper.josp.jsl.android.activities;

import android.os.Bundle;

import com.robypomper.josp.jsl.android.JSLAndroid;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;


/**
 * Base class for all activities that need to work with a single JSLRemoteObject.
 * <p>
 * Like the {@link BaseJSLActivity} class, this class extends the {@link androidx.appcompat.app.AppCompatActivity},
 * and provides the specified {@link JSLRemoteObject} reference that can be used by the
 * activities subclasses.
 * <p>
 * Subclasses can reimplement the {@link #onRemoteObjectReady()} and the
 * {@link #onRemoteObjectNotReady()} methods to handle the remote object ready/not ready
 * events and update the activity's UI. As heper methods, this class provides the
 * {@link #isRemoteObjectReady()} method that returns true if the remote object is ready.
 * <p>
 * During his creation, the class looks for the object ID of the remote object
 * that must be used by the activity from the activity's intent (like activity's
 * argument), from the activity's saved instance state or from the shared
 * preferences (last one is not yet implemented). If the object ID is not found,
 * the method throws a RuntimeException.
 * <p>
 * Every time the JSL Service become ready, or during the activity's resume, if
 * the JSL Service is already ready, this class looks for the specified object
 * ID. If the object ID is found, the class try to emits the {@link #onRemoteObjectReady()}
 * event.<br/>
 * The event {@link #onRemoteObjectReady()} is emitted only if the remote object
 * is ready. It means that the remote object has been registered, and its structure
 * has been received from the remote object.
 * <p>
 * Finally, this class provides also a set of methods that can be used by the
 * subclasses to find the {@link JSLComponent} objects that are exposed by the
 * remote object that is used by the activity. Those methods allow the subclasses
 * to look for the desired component by its path.
 */
public class BaseRemoteObjectActivity extends BaseJSLActivity {

    /**
     * The name of the parameter that is used to pass the object ID to the activity.
     */
    public final static String PARAM_OBJ_ID = JSLAndroid.Params.OBJID;
    private String objId = null;
    private JSLRemoteObject remObj;

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
        super.onCreate(savedInstanceState);

        lookForObjId(savedInstanceState);
    }

    /**
     * Called when the activity is no longer visible to the user. This method deregisters itself
     * as a listener for the remote object that is used by the activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // During the super.onResume() execution, it check if the JSL instance is ready, and if
        // it is ready, it calls the onJSLReady() method. So, the onJSLReady() method is called
        // by the super.onResume() method.
        super.onResume();

        // Register remote object (by obj's id)
        getJSLListeners().addObjsMngrListenersByID(objId, remObjByIdListener);
        getJSLListeners().addObjsMngr_StructListeners(remObjStructListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Deregister remote object (by obj's id)
        getJSLListeners().removeObjsMngrListenersByID(objId, remObjByIdListener);
        getJSLListeners().removeObjsMngr_StructListeners(remObjStructListener);
    }


    // BaseJSLActivity

    @Override
    protected void onJSLReady() {
        super.onJSLReady();

        // If already set, use old remote object reference
        JSLRemoteObject obj = remObj;
        // Search for remote object by obj's id
        if (obj == null || obj.getId().compareTo(getObjId()) != 0) {
            JSLObjsMngr objsMngr = getJSLClient().getJSL().getObjsMngr();
            obj = objsMngr.getById(objId);
        }
        if (obj != null) {
            remObj = null;  // force the onRemoteObjectReady() event
            emitOnRemoteObjectReady(obj);
        }
        else emitOnRemoteObjectNotReady();
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

    // TODO add getRemoteObjectName(), getServicePermissionOnObject() and more...

    /**
     * The method returns true if the remote object is ready. It means that the remote object has
     * been registered, and its structure has been received.
     */
    protected boolean isRemoteObjectReady() {
        return remObj != null;
    }


    // Events emitters

    private void emitOnRemoteObjectReady(JSLRemoteObject obj) {
        assert obj != null : "can't emit onRemoteObjectReady with null obj";
        assert remObj == null;

        if (obj.getStruct() != null
                && obj.getStruct().getStructure() != null) {
            remObj = obj;
            onRemoteObjectReady();
        }
    }

    private void emitOnRemoteObjectNotReady() {
        // the method is called also when the remObj is null, like
        // on the onJSLReady() event when the object is not found
        // assert remObj != null;

        onRemoteObjectNotReady();
        remObj = null;
    }


    // Events methods (to be overridden by sub-classes)

    /**
     * The method is called when the remote object is ready. It means that the
     * remote object has been registered, and its structure has been received.
     * <p>
     * This method is emitted every time an activity is resumed or when the
     * remote object is added to the JSL Service. But always when the activity
     * is resumed.
     * <p>
     * The value returned by the isRemoteObjectReady method is updated before
     * this method is called.
     */
    protected void onRemoteObjectReady() {
    }

    /**
     * The method is called when the remote object become not ready. It means
     * that the remote object has been deregistered.
     * <p>
     * The value returned by the isRemoteObjectReady method is updated after this method is called.
     */
    protected void onRemoteObjectNotReady() {
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


    // Remote obj mngm

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


    // Remote listeners

    private final JSLObjsMngr.ObjsMngrListener remObjByIdListener = new JSLObjsMngr.ObjsMngrListener() {

        @Override
        public void onObjAdded(JSLRemoteObject obj) {
            if (remObj != null) return;
            emitOnRemoteObjectReady(obj);
        }

        @Override
        public void onObjRemoved(JSLRemoteObject obj) {
            if (remObj == null || remObj != obj) return;
            emitOnRemoteObjectNotReady();
        }

    };

    private final ObjStruct.RemoteObjectStructListener remObjStructListener = new ObjStruct.RemoteObjectStructListener() {

        @Override
        public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
            if (obj.getId().compareTo(getObjId()) != 0) return;
            emitOnRemoteObjectReady(obj);
        }

    };

}
