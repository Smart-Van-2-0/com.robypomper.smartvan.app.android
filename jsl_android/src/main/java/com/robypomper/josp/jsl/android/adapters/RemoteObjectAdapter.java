package com.robypomper.josp.jsl.android.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.josp.states.JSLState;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@link RecyclerView} adapter to show Remote Objects.
 * <p>
 * This class , let the {@link #onCreateViewHolder(ViewGroup, int)} method as
 * abstract method, so RemoteObjectAdapter client's can customize the ViewHolder
 * used by this class.
 * <p>
 * This class can works with ViewHolders subclassing the {@link ViewHolder} and
 * implements the {@link ViewHolder#bind(JSLRemoteObject)} method. Into this
 * method, client's can populate their own View with Remote Objects values and,
 * is also possible add listeners to the UI widgets like the
 * {#link {@link View#setOnClickListener(View.OnClickListener)}}.
 */
public abstract class RemoteObjectAdapter extends RecyclerView.Adapter<RemoteObjectAdapter.ViewHolder> {

    private final Activity parent;
    private final JSLClient<? extends JSLService> jslClient;
    private final List<JSLRemoteObject> _cached_objs = new ArrayList<>();

    /**
     * Main class for {@link RemoteObjectAdapter} ViewHolders.
     */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
        }

        /**
         * Replace the contents of the current view.
         * <p>
         * Get Remote Object info from the given parameter and replace the
         * contents of the view with that element
         *
         * @param obj the Remote Object to display into the view.
         */
        public abstract void bind(JSLRemoteObject obj);

    }


    // Constructor

    /**
     * Initialize the dataset of the Adapter
     *
     * @param jslClient JSLClient<JSLService> the client to JSL Service.
     */
    public RemoteObjectAdapter(Activity parent, JSLClient<? extends JSLService> jslClient) {
        this.parent = parent;
        this.jslClient = jslClient;

        if (jslClient.getJSLState() == JSLState.RUN)
            _cached_objs.addAll(jslClient.getJSL().getObjsMngr().getAllObjects());

        jslClient.getJSLListeners().addJSLStateListener(jslStateListener);
        jslClient.getJSLListeners().addObjsMngrListeners(remObjsListener);
    }

    @Override
    protected void finalize() {
        jslClient.getJSLListeners().removeJSLStateListener(jslStateListener);
        jslClient.getJSLListeners().removeObjsMngrListeners(remObjsListener);
    }


    // Android

    /**
     * Delegate the replacement of a view to the ViewHolder.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the
     *                   item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RemoteObjectAdapter.ViewHolder viewHolder, final int position) {
        JSLRemoteObject remObj = getCachedObject(position);
        viewHolder.bind(remObj);
    }

    /**
     * @return the size of your dataset (invoked by the layout manager).
     */
    @Override
    public int getItemCount() {
        return _cached_objs.size();
    }


    // Cache access

    private JSLRemoteObject getCachedObject(int position) {
        return _cached_objs.get(position);
    }

    private void addCachedObject(JSLRemoteObject obj) {
        // TODO: FILTER - check if obj comply with adapter filter
        _cached_objs.add(obj);
        // TODO: SORTING - re-sorting

        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemInserted(_cached_objs.indexOf(obj));
            }
        });
    }

    private void updateCachedObject(JSLRemoteObject obj) {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(_cached_objs.indexOf(obj));
            }
        });
    }

    private void removeCachedObject(JSLRemoteObject obj) {
        int old_pos = _cached_objs.indexOf(obj);
        _cached_objs.remove(obj);

        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(old_pos);
            }
        });
    }

    private void resetCachedObject() {
        int old_objs_count = _cached_objs.size();
        _cached_objs.clear();

        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemRangeRemoved(0, old_objs_count);
            }
        });
    }


    // Remote listeners

    private final JSL.JSLStateListener jslStateListener = new JSL.JSLStateListener() {
        @Override
        public void onJSLStateChanged(JSLState newState, JSLState oldState) {
            if (newState == JSLState.RUN) resetCachedObject();
        }
    };

    private final JSLObjsMngr.ObjsMngrListener remObjsListener = new JSLObjsMngr.ObjsMngrListener() {

        @Override
        public void onObjAdded(JSLRemoteObject obj) {
            addCachedObject(obj);
            obj.getInfo().addListener(remObjInfoListener);
        }

        @Override
        public void onObjRemoved(JSLRemoteObject obj) {
            removeCachedObject(obj);
            obj.getInfo().removeListener(remObjInfoListener);
        }

    };

    private final ObjInfo.RemoteObjectInfoListener remObjInfoListener = new ObjInfo.RemoteObjectInfoListener() {
        @Override
        public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
            updateCachedObject(obj);
        }

        @Override
        public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
            updateCachedObject(obj);
        }

        @Override
        public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
            updateCachedObject(obj);
        }

        @Override
        public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
            updateCachedObject(obj);
        }

        @Override
        public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
            updateCachedObject(obj);
        }

        @Override
        public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {
            updateCachedObject(obj);
        }
    };

}
