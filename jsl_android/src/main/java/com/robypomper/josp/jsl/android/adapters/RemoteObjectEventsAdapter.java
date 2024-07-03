package com.robypomper.josp.jsl.android.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.robypomper.josp.protocol.JOSPEventGroup;
import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.josp.jsl.android.app.JSLClientState;
import com.robypomper.josp.jsl.android.service.JSLService;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.HistoryObjEvents;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@link RecyclerView} adapter to show Remote Object's events.
 * <p>
 * This class, let the {@link #onCreateViewHolder(ViewGroup, int)} method as
 * abstract method, so RemoteObjectAdapter client's can customize the ViewHolder
 * used by this class.
 * <p>
 * This class can works with ViewHolders subclassing the {@link ViewHolder} and
 * implements the {@link ViewHolder#bind(JOSPEventGroup)} method. Into this
 * method, client's can populate their own View with Remote Objects values and,
 * is also possible add listeners to the UI widgets like the
 * {#link {@link View#setOnClickListener(View.OnClickListener)}}.
 * <p>
 * By default, this class fetch the latest 50 events, but it is possible
 * customize this behavior using the {@link #setLimitsHistory(HistoryLimits, boolean)}
 * method.
 * <p>
 * TODO: implement a way to add an overlay to the RecyclerView when the adapter is fetching events.
 * TODO add attributes to customize the UI like the TextAppearance.
 * @noinspection unused
 */
public abstract class RemoteObjectEventsAdapter extends RecyclerView.Adapter<RemoteObjectEventsAdapter.ViewHolder> {

    // Constants

    private static final String LOG_TAG = RemoteObjectEventsAdapter.class.getSimpleName();
    /** The default number of events to fetch */
    private static final int LATEST_COUNT = 50;


    // Internal vars

    private final Activity parent;
    private final JSLApplication<? extends JSLService> jslApp;
    private final JSLClient<? extends JSLService> jslClient;
    /** The Remote Object ID */
    private final String objId;
    /** The limits to use to fetch events */
    private HistoryLimits limitsHistory = new HistoryLimits(LATEST_COUNT, null, null, null, null, null, null, null);
    /** The cached events as a JOSPEventGroup list. */
    private final List<JOSPEventGroup> _cached_events = new ArrayList<>();


    // Classes

    /**
     * Main class for {@link RemoteObjectEventsAdapter} ViewHolders.
     * <p>
     * Reimplement this class to customize the ViewHolder used by
     * {@link RemoteObjectEventsAdapter}:
     * <ul>
     *     <li>Implement the {@link #bind(JOSPEventGroup)} method to populate
     *     the view with Remote Object's values.</li>
     *     <li>Implement the {@link #ViewHolder(View)} constructor to initialize
     *     the view.</li>
     * </ul>
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
         * @param event the Event object to display into the view.
         */
        public abstract void bind(JOSPEventGroup event);

    }


    // Constructor

    /**
     * Initialize the adapter and, if the object is reachable, fetch the latest
     * 50 events.
     *
     * @param parent the parent activity.
     * @param jslApp the JSLApplication instance.
     * @param objId the Remote Object ID.
     */
    public RemoteObjectEventsAdapter(Activity parent, JSLApplication<? extends JSLService> jslApp, String objId) {
        this.parent = parent;
        this.jslApp = jslApp;
        this.jslClient = jslApp.getJSLClient();
        this.objId = objId;

        if (jslClient.getState() == JSLClientState.RUN)
            if (getRemoteObject() != null)
                fetchRemoteObjectEvents();

        jslClient.getJSLListeners().addObjsMngrListeners(remObjsListener);
    }

    @Override
    protected void finalize() {
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
    public void onBindViewHolder(RemoteObjectEventsAdapter.ViewHolder viewHolder, final int position) {
        JOSPEventGroup event = getCachedObjectEvents(position);
        viewHolder.bind(event);
    }

    /**
     * @return the size of your dataset (invoked by the layout manager).
     */
    @Override
    public int getItemCount() {
        return _cached_events.size();
    }


    // Getters / Setters

    /**
     * @return the Remote Object ID.
     */
    public String getObjId() {
        return objId;
    }

    /**
     * @return the Remote Object instance.
     */
    private JSLRemoteObject getRemoteObject() {
        return jslClient.getJSL().getObjsMngr().getById(objId);
    }

    /**
     * @return the limits used to fetch events.
     */
    public HistoryLimits getLimitsHistory() {
        return limitsHistory;
    }

    /**
     * Set the limits to use to fetch events.
     * <p>
     * If the autoFetch parameter is true, the adapter will fetch the events
     * using the new limits.
     *
     * @param limitsHistory the new limits to use.
     * @param autoFetch if true, the adapter will fetch the events using the
     *                  new limits.
     */
    public void setLimitsHistory(HistoryLimits limitsHistory, boolean autoFetch) {
        this.limitsHistory = limitsHistory;
        if (autoFetch)
            fetchRemoteObjectEvents();
    }


    // Cache access

    private void fetchRemoteObjectEvents() {
        jslApp.runOnNetworkThread(new Runnable() {
            @Override
            public void run() {
                JSLRemoteObject obj = getRemoteObject();
                try {
                    obj.getInfo().getEventsHistory(getLimitsHistory(), eventsListener);
                } catch (JSLRemoteObject.ObjectNotConnected e) {
                    Log.e(LOG_TAG, String.format("Can't fetch events because object '%s' is not connected", objId), e);
                } catch (JSLRemoteObject.MissingPermission e) {
                    Log.e(LOG_TAG, String.format("Can't fetch events because current service/user permission not granted on object '%s'", objId), e);
                }
            }
        });

    }

    private JOSPEventGroup getCachedObjectEvents(int position) {
        return _cached_events.get(position);
    }

    private void processReceivedEvents(List<JOSPEvent> history) {
        boolean grouping = true;    // for debug purpose

        if (!grouping)
            for (JOSPEvent event : history) {
                System.out.println("EVENT: " + event.getId() + " " + event.getEmittedAt());
                addCachedObjectEvents(new JOSPEventGroup(event));
            }

        else {
            JOSPEventGroup groupEvent = null;
            for (JOSPEvent event : history) {
                System.out.println("EVENT: " + event.getId() + " " + event.getEmittedAt());
                if (groupEvent == null) {
                    groupEvent = new JOSPEventGroup(event);
                } else if (groupEvent.canBeInGroup(event)) {
                    groupEvent.addEvent(event);
                } else {
                    System.out.println("GROUP: " + groupEvent);
                    addCachedObjectEvents(groupEvent);
                    groupEvent = new JOSPEventGroup(event);
                }
            }
            if (groupEvent != null)
                addCachedObjectEvents(groupEvent);
        }
    }

    private void addCachedObjectEvents(JOSPEventGroup event) {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO: FILTER - check if obj comply with adapter filter
                _cached_events.add(event);
                // TODO: SORTING - re-sorting

                notifyItemInserted(_cached_events.indexOf(event));
            }
        });
    }

    private void updateCachedObjectEvents(JOSPEventGroup event) {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(_cached_events.indexOf(event));
            }
        });
    }

    private void removeCachedObjectEvents(JOSPEventGroup event) {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int old_pos = _cached_events.indexOf(event);
                _cached_events.remove(event);

                notifyItemRemoved(old_pos);
            }
        });
    }

    private void resetCachedObjectEvents() {
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int old_events_count = getItemCount();
                _cached_events.clear();

                notifyItemRangeRemoved(0, old_events_count);
            }
        });
    }


    // Remote listeners

    private final JSLObjsMngr.ObjsMngrListener remObjsListener = new JSLObjsMngr.ObjsMngrListener() {

        @Override
        public void onObjAdded(JSLRemoteObject obj) {
            if (!obj.getId().equals(objId))  return;
            fetchRemoteObjectEvents();
        }

        @Override
        public void onObjRemoved(JSLRemoteObject obj) {}

    };

    private final HistoryObjEvents.EventsListener eventsListener = new HistoryObjEvents.EventsListener() {

        @Override
        public void receivedEvents(List<JOSPEvent> history) {
            processReceivedEvents(history);
        }

    };

}
