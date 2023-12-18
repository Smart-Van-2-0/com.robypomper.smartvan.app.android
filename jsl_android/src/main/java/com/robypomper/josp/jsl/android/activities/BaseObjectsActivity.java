package com.robypomper.josp.jsl.android.activities;

import android.os.Bundle;

import com.robypomper.josp.jsl.android.JSLAndroid;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Base class for all activities that need to work with a set of JSLRemoteObjects.
 *
 * ...
 */
public class BaseObjectsActivity extends BaseJSLActivity {

    /**
     * The name of the parameter that is used to pass the object ID to the activity.
     */
    public final static String PARAM_OBJS_MODELS = JSLAndroid.Params.OBJS_MODELS;
    public final static String PARAM_OBJS_BRANDS = JSLAndroid.Params.OBJS_BRANDS;
    public final static String PARAM_OBJS_PILLARS = JSLAndroid.Params.OBJS_PILLARS;
    private List<String> reqModels;
    private List<String> reqBrands;
    private List<String> reqPillars;
    private final List<String> objsIds = new ArrayList<>();

    /**
     * Activity constructor that looks for the required fields into the activity's intent,
     * saved instance state and shared preferences.
     * <p>
     * If a field set is not found, the activity will not filter the remote objects by that field.
     */
    public BaseObjectsActivity() {
        super();
        this.reqModels = null;
        this.reqBrands = null;
        this.reqPillars = null;
    }

    /**
     * Activity constructor that get the required fields from constructor params.
     * <p>
     * If a field set is not found, the activity will not filter the remote objects by that field.
     *
     * @param reqModels list of object's models to filter
     * @param reqBrands list of object's brands to filter
     * @param reqCompPaths list of object's component paths to filter
     */
    public BaseObjectsActivity(List<String> reqModels, List<String> reqBrands, List<String> reqCompPaths) {
        super();
        this.reqModels = reqModels;
        if (reqModels == null)
            this.reqModels = new ArrayList<>();
        this.reqBrands = reqBrands;
        if (reqBrands == null)
            this.reqBrands = new ArrayList<>();
        this.reqPillars = reqCompPaths;
        if (reqCompPaths == null)
            this.reqPillars = new ArrayList<>();
    }

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
        lookForObjsFilters(savedInstanceState);

        super.onCreate(savedInstanceState);

        for (String m : reqModels)
            getJSLClient().getJSLListeners().addObjsMngrListenersByModel(m, remObjsListener);
        for (String b : reqBrands)
            getJSLClient().getJSLListeners().addObjsMngrListenersByBrand(b, remObjsListener);
        for (String c : reqPillars)
            getJSLClient().getJSLListeners().addObjsMngrListenersByCompPath(c, remObjsListener);
    }

    /**
     * Called when the activity is no longer visible to the user. This method deregisters itself
     * as a listener for the remote object that is used by the activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (String m : reqModels)
            getJSLClient().getJSLListeners().removeObjsMngrListenersByModel(m, remObjsListener);
        for (String b : reqBrands)
            getJSLClient().getJSLListeners().removeObjsMngrListenersByBrand(b, remObjsListener);
        for (String c : reqPillars)
            getJSLClient().getJSLListeners().removeObjsMngrListenersByCompPath(c, remObjsListener);
    }

    @Override
    protected void onJSLReady() {
        super.onJSLReady();

        JSLObjsMngr objsMngr = getJSLClient().getJSL().getObjsMngr();
        for (JSLRemoteObject remObj : objsMngr.getAllObjects()) {
            if (checkRemoteObject(remObj))
                registerRemoteObject(remObj);
        }
    }

    private void lookForObjsFilters(Bundle savedInstanceState) {
        // get models list
        reqModels = null;
        if (getIntent().getExtras() != null)
            reqModels = getIntent().getExtras().getStringArrayList(PARAM_OBJS_MODELS);
        if (reqModels == null && savedInstanceState != null)
            reqModels = savedInstanceState.getStringArrayList(PARAM_OBJS_MODELS);
        // TODO implement required models storage into shared preferences
        //if (reqModels == null)
        // look into shared preferences
        if (reqModels == null)
            reqModels = new ArrayList<>();

        // get brands list
        reqBrands = null;
        if (getIntent().getExtras() != null)
            reqBrands = getIntent().getExtras().getStringArrayList(PARAM_OBJS_BRANDS);
        if (reqBrands == null && savedInstanceState != null)
            reqBrands = savedInstanceState.getStringArrayList(PARAM_OBJS_BRANDS);
        // TODO implement required brands storage into shared preferences
        //if (reqBrands == null)
        // look into shared preferences
        if (reqBrands == null)
            reqBrands = new ArrayList<>();

        // get pillars list
        reqPillars = null;
        if (getIntent().getExtras() != null)
            reqPillars = getIntent().getExtras().getStringArrayList(PARAM_OBJS_PILLARS);
        if (reqPillars == null && savedInstanceState != null)
            reqPillars = savedInstanceState.getStringArrayList(PARAM_OBJS_PILLARS);
        // TODO implement required pillars storage into shared preferences
        //if (reqPillars == null)
        // look into shared preferences
        if (reqPillars == null)
            reqPillars = new ArrayList<>();
    }

    private boolean checkRemoteObject(JSLRemoteObject remObj) {
        String model = remObj.getInfo().getModel();
        if (reqModels.contains(model))
            return true;

        String brand = remObj.getInfo().getBrand();
        if (reqBrands.contains(brand))
            return true;

        JSLRoot root = remObj.getStruct().getStructure();
        if (root != null) {
            for (String compPath : reqPillars) {
                JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
                JSLComponent comp = DefaultJSLComponentPath.searchComponent(root, componentPath);
                if (comp != null) return true;
            }
        }

        return false;
    }


    // Getters

    protected List<String> getObjsIds() {
        return Collections.unmodifiableList(objsIds);
    }


    // Events methods for sub-classes

    private void emitOnRemoteObjectAdded(JSLRemoteObject remObj) {
        String newObjId = remObj.getId();
        if (objsIds.contains(newObjId))
            return;

        objsIds.add(newObjId);
        if (objsIds.size() == 1)
            onFirstRemoteObjectAdded(remObj);
        onRemoteObjectAdded(remObj);
    }

    private void emitOnRemoteObjectRemoved(JSLRemoteObject remObj) {
        String newObjId = remObj.getId();
        if (!objsIds.contains(newObjId))
            return;

        objsIds.remove(newObjId);
        if (objsIds.size() == 0)
            onLastRemoteObjectRemoved(remObj);
        onRemoteObjectRemoved(remObj);
    }

    protected void onFirstRemoteObjectAdded(JSLRemoteObject remObj) {
    }

    protected void onRemoteObjectAdded(JSLRemoteObject remObj) {
    }

    protected void onRemoteObjectRemoved(JSLRemoteObject remObj) {
    }

    protected void onLastRemoteObjectRemoved(JSLRemoteObject remObj) {
    }


    // Remote obj

    private void registerRemoteObject(JSLRemoteObject remObj) {
        emitOnRemoteObjectAdded(remObj);
    }

    private void deregisterRemoteObject(JSLRemoteObject remObj) {
        emitOnRemoteObjectRemoved(remObj);
    }


    // Remote listeners

    private final JSLObjsMngr.ObjsMngrListener remObjsListener = new JSLObjsMngr.ObjsMngrListener() {

        @Override
        public void onObjAdded(JSLRemoteObject remObj) {
            registerRemoteObject(remObj);
        }

        @Override
        public void onObjRemoved(JSLRemoteObject remObj) {
            deregisterRemoteObject(remObj);
        }

    };

}
