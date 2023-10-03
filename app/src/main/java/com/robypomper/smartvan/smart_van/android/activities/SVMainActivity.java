package com.robypomper.smartvan.smart_van.android.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjStruct;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.states.JSLState;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.app.SVApplication;
import com.robypomper.smartvan.smart_van.android.app.SVJSLClient;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvmainBinding;


public class SVMainActivity extends AppCompatActivity {

    public final static String PARAM_OBJ_ID = SVDefinitions.PARAM_ACTIVITY_SVMAIN_OBJID;
    public final static String COMP_POWER_PATH = SVDefinitions.COMP_MAIN_POWER;
    public final static String COMP_PANELS_PATH = SVDefinitions.Components.POWER_PANELS_POWER;
    private static final String COMP_SERVICES_PATH = SVDefinitions.Components.POWER_LOAD_CURRENT;

    private ActivitySvmainBinding binding;
    private SVJSLClient jslClient;
    private String objId = null;
    private JSLRemoteObject remObj;
    private JSLRangeState powerComp;
    private JSLRangeState panelsComp;
    private JSLRangeState servicesComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        binding = ActivitySvmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get object id
        objId = null;
        if (getIntent().getExtras() != null)
            objId = getIntent().getExtras().getString(PARAM_OBJ_ID);
        if (objId == null && savedInstanceState!=null)
            objId = savedInstanceState.getString(PARAM_OBJ_ID);
        // TODO implement obj id storage into shared preferences
        //if (objId == null)
        // look into shared preferences
        if (objId == null)
            throw new RuntimeException(String.format("Can't init SVMainActivity without specify '%s' param", PARAM_OBJ_ID));
        binding.txtTitleName.setText(objId);

        // Check JSL state, registerRemoteObject
        SVApplication app = ((SVApplication) this.getApplication());
        jslClient = app.getJSLClient();
        if (jslClient.getJSLState() == JSLState.RUN) {
            // Search for remote object by obj's id
            JSLObjsMngr objsMngr = jslClient.getJSL().getObjsMngr();
            JSLRemoteObject obj = objsMngr.getById(objId);
            if (obj != null)
                // register remote object
                registerRemoteObject(obj);
        }
        // register JSL ObjsMngrListenersByID
        jslClient.getJSLListeners().addObjsMngrListenersByID(objId, remObjByIdListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (powerComp != null)
            deregisterPowerComp();
        if (panelsComp != null)
            deregisterPanelsComp();
        if (servicesComp != null)
            deregisterServicesComp();

        if (remObj != null)
            deregisterRemoteObject();

        jslClient.getJSLListeners().addObjsMngrListenersByID(objId, remObjByIdListener);
    }


    // Remote obj and comp

    private void registerRemoteObject(JSLRemoteObject obj) {
        remObj = obj;

        JSLRangeState component = searchComponent(remObj, COMP_POWER_PATH);
        if (component != null)
            registerPowerComp(component);
        component = searchComponent(remObj, COMP_PANELS_PATH);
        if (component != null)
            registerPanelsComp(component);
        component = searchComponent(remObj, COMP_SERVICES_PATH);
        if (component != null)
            registerServicesComp(component);

        remObj.getComm().addListener(remObjConnListener);
        remObj.getStruct().addListener(remObjStructListener);

        updateRemoteObject(remObj);
        updateConnectionWidgets(remObj);
    }

    private void deregisterRemoteObject() {
        if (powerComp != null)
            deregisterPowerComp();
        if (panelsComp != null)
            deregisterPanelsComp();
        if (servicesComp != null)
            deregisterServicesComp();

        remObj.getComm().removeListener(remObjConnListener);
        remObj.getStruct().removeListener(remObjStructListener);

        remObj = null;
        updateRemoteObject(null);
        updateConnectionWidgets(null);
    }

    private JSLRangeState searchComponent(JSLRemoteObject obj, String path) {
        return (JSLRangeState) obj.getStruct().getComponent(path);
    }

    private void registerPowerComp(JSLRangeState component) {
        powerComp = component;

        powerComp.addListener(listenerPowerComp);

        updatePowerComp(powerComp);
    }

    private void deregisterPowerComp() {
        powerComp.removeListener(listenerPowerComp);

        powerComp = null;

        updatePowerComp(null);
    }

    private void registerPanelsComp(JSLRangeState component) {
        panelsComp = component;

        panelsComp.addListener(listenerPanelsComp);

        updatePanelsComp(panelsComp);
    }

    private void deregisterPanelsComp() {
        panelsComp.removeListener(listenerPanelsComp);

        panelsComp = null;

        updatePanelsComp(null);
    }

    private void registerServicesComp(JSLRangeState component) {
        servicesComp = component;

        servicesComp.addListener(listenerServicesComp);

        updateServicesComp(servicesComp);
    }

    private void deregisterServicesComp() {
        servicesComp.removeListener(listenerServicesComp);

        servicesComp = null;

        updateServicesComp(null);
    }


    // Remote listeners

    private final JSLObjsMngr.ObjsMngrListener remObjByIdListener = new JSLObjsMngr.ObjsMngrListener(){

        @Override
        public void onObjAdded(JSLRemoteObject obj) {
            if (remObj != null)
                return;
            registerRemoteObject(obj);
        }

        @Override
        public void onObjRemoved(JSLRemoteObject obj) {
            if (remObj == null || remObj != obj)
                return;
            deregisterRemoteObject();
        }

    };

    private final ObjComm.RemoteObjectConnListener remObjConnListener = new ObjComm.RemoteObjectConnListener() {

        @Override
        public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            updateConnectionWidgets(remObj);
        }

        @Override
        public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            updateConnectionWidgets(remObj);
        }

        @Override
        public void onCloudConnected(JSLRemoteObject obj) {
            updateConnectionWidgets(remObj);
        }

        @Override
        public void onCloudDisconnected(JSLRemoteObject obj) {
            updateConnectionWidgets(remObj);
        }

    };

    private final ObjStruct.RemoteObjectStructListener remObjStructListener = new ObjStruct.RemoteObjectStructListener(){

        @Override
        public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
            if (powerComp == null) {
                JSLRangeState component = searchComponent(obj, COMP_POWER_PATH);
                if (component != null)
                    registerPowerComp(component);
            }
            if (panelsComp == null) {
                JSLRangeState component = searchComponent(obj, COMP_PANELS_PATH);
                if (component != null)
                    registerPanelsComp(component);
            }
            if (servicesComp == null) {
                JSLRangeState component = searchComponent(obj, COMP_SERVICES_PATH);
                if (component != null)
                    registerServicesComp(component);
            }
        }

    };

    private final JSLRangeState.RangeStateListener listenerPowerComp = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            updatePowerComp(component);
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {}

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {}

    };

    private final JSLRangeState.RangeStateListener listenerPanelsComp = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            updateServicesComp(component);
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {}

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {}

    };

    private final JSLRangeState.RangeStateListener listenerServicesComp = new JSLRangeState.RangeStateListener() {

        @Override
        public void onStateChanged(JSLRangeState component, double newState, double oldState) {
            updatePanelsComp(component);
        }

        @Override
        public void onMinReached(JSLRangeState component, double state, double min) {}

        @Override
        public void onMaxReached(JSLRangeState component, double state, double max) {}

    };


    // UI widgets

    private void updateRemoteObject(JSLRemoteObject obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "WAITING";
                if (obj != null)
                    text = obj.getName();

                binding.txtTitleName.setText(text);
            }
        });
    }

    private void updateConnectionWidgets(JSLRemoteObject obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int imgState = R.drawable.ic_not_available;
                String text = "N/A";
                if (obj != null) {
                    ObjComm comm = obj.getComm();
                    if (comm.isLocalConnected() && comm.isCloudConnected()) {
                        text = "Object fully connected";
                        imgState = R.drawable.ic_online_full;
                    } else if (comm.isLocalConnected() && !comm.isCloudConnected()) {
                        text = "Object connected\nvia DIRECT communication";
                        imgState = R.drawable.ic_online_direct;
                    } else if (!comm.isLocalConnected() && comm.isCloudConnected()) {
                        text = "Object connected\nvia CLOUD communication";
                        imgState = R.drawable.ic_online_cloud;
                    } else {
                        text = "Object NOT connected";
                        imgState = R.drawable.ic_offline;
                    }
                }
                binding.txtConnectionState.setImageDrawable(AppCompatResources.getDrawable(SVMainActivity.this, imgState));
                binding.txtConnectionText.setText(text);
            }
        });
    }

    private void updatePowerComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (comp!=null)
                    text = String.format("%.2f", comp.getState() / 1000); // mV to V
                binding.txtPowerValue.setText(text);
            }
        });
    }

    private void updatePanelsComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (comp!=null)
                    text = String.format("%.2f", comp.getState() / 1000); // mV to V
                binding.txtPanelsValue.setText(text);
            }
        });
    }

    private void updateServicesComp(JSLRangeState comp) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String text = "N/A";
                if (comp!=null)
                    text = String.format("%.2f", comp.getState() / 1000); // mV to V
                binding.txtServicesValue.setText(text);
            }
        });
    }

}