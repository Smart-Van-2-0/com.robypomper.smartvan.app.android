package com.robypomper.josp.jsl.android.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.robypomper.java.JavaDate;
import com.robypomper.josp.consts.JOSPConstants;
import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.adapters.RemoteObjectEventsAdapter;
import com.robypomper.josp.jsl.android.components.EventDetailsBottomSheet;
import com.robypomper.josp.jsl.android.databinding.ActivityJslObjectDetailsBinding;
import com.robypomper.josp.jsl.android.utils.ThemeUtils;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.history.HistoryObjEvents;
import com.robypomper.josp.jsl.objs.remote.ObjComm;
import com.robypomper.josp.jsl.objs.remote.ObjPerms;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.protocol.JOSPEventGroup;
import com.robypomper.josp.protocol.JOSPPerm;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Activity to display remote object's details.
 * <p>
 * It shows the object's name, id, communication status, permissions and a list
 * of latest events. Then this activity allows user to change object's name,
 * show event's details and export them.
 * <p>
 * Moreover, this activity allows user to open activities specified to show more
 * details a d action on Communication, Permission and Events. (Not yet implemented)
 * <p>
 * TODO add listeners to show details activity for communication, permissions and events.
 * TODO add attributes to customize the UI like the TextAppearance.
 */
public abstract class JSLObjectDetailsActivity extends BaseRemoteObjectActivity {

    // Constants

    private static final String LOG_TAG = "JSLA.Actvt.ObjDetails";
    private static final boolean PREVENT_EVENT_COUNT_FETCH = true;
    private static DateFormat TIME_FORMAT;
    private static DateFormat DATE_FORMAT;
    private static int COLOR_ERROR;
    private static int COLOR_ORIGINAL;


    // UI widgets

    private ActivityJslObjectDetailsBinding binding;


    // Classes

    /**
     * ViewHolder for the RemoteObjectEventsAdapter.
     */
    private class ViewHolderImpl extends RemoteObjectEventsAdapter.ViewHolder {

        private final View view;

        public ViewHolderImpl(View view) {
            super(view);
            this.view = view;
        }

        @Override
        public void bind(JOSPEventGroup event) {
            TextView txtEventType = view.findViewById(R.id.txtEventType);
            txtEventType.setText(event.getPhase());
            if (COLOR_ORIGINAL ==0) COLOR_ORIGINAL = txtEventType.getCurrentTextColor();
            txtEventType.setTextColor(event.getErrorPayload() != null ? COLOR_ERROR : COLOR_ORIGINAL);

            TextView txtEventCount = view.findViewById(R.id.txtEventCount);
            txtEventCount.setVisibility(event.getCount() > 1 ? View.VISIBLE : View.GONE);
            txtEventCount.setText(String.valueOf(event.getCount()));

            TextView txtEventTitle = view.findViewById(R.id.txtEventTitle);
            String eventDateStr;
            if (event.getCount() > 1)
                eventDateStr = "Latest emitted at ";
            else
                eventDateStr = "Emitted at ";
            if (DateUtils.isToday(event.getEmittedAt().getTime()))
                eventDateStr += TIME_FORMAT.format(event.getEmittedAt());
            else
                eventDateStr += TIME_FORMAT.format(event.getEmittedAt()) + " " + DATE_FORMAT.format(event.getEmittedAt());
            txtEventTitle.setText(eventDateStr);
            view.setTag(event);

            // register new listener for current item
            // (automatically delete the old one)
            view.setOnClickListener(onEventItemClickListener);
        }

    }


    // Android

    /**
     * Setup the UI and add the main listener from the JSLClient.
     * <p>
     * It also checks if there are already available objects.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // assign static vars
        if (TIME_FORMAT == null || DATE_FORMAT == null) {
            TIME_FORMAT = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
            DATE_FORMAT = android.text.format.DateFormat.getMediumDateFormat(getApplicationContext());
            COLOR_ERROR = ThemeUtils.getAppColor(this, com.google.android.material.R.attr.colorError);
            COLOR_ORIGINAL = 0;
        }
        Log.d(LOG_TAG, String.format("JSLObjectDetailsActivity is being created to show `%s` object", getObjId()));

        // inflate ui
        binding = ActivityJslObjectDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up adapter for events list
        binding.listEvents.setAdapter(new RemoteObjectEventsAdapter(this, getJSLApplication(), getObjId()) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lay_remote_object_event_simple, viewGroup, false);
                return new ViewHolderImpl(view);
            }
        });
        binding.listEvents.setLayoutManager(new LinearLayoutManager(this));

        // set listeners
        binding.imgObjectNameEdit.setOnClickListener(onObjectNameEditClickListener);
    }

    /**
     * Remove the main listener from the JSLClient.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "JSLObjectDetailsActivity is being destroyed");
    }

    @Override
    protected void onResume() {
        // During the super.onResume() execution, it check if the remote object
        // is ready, and if it is ready, it calls the onRemoteObjectReady()
        // method. So, the registerRemoteObject() method is called by the
        // super.onResume() method, only if required.
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Deregister and update UI
        if (getRemoteObject() != null)
            deregisterRemoteObject();
    }


    // BaseRemoteObjectActivity

    @Override
    protected void onRemoteObjectReady() {
        registerRemoteObject();
    }

    @Override
    protected void onRemoteObjectNotReady() {
        if (getRemoteObject() != null)
            deregisterRemoteObject();
    }


    // Remote object management

    private void registerRemoteObject() {
        registerRemoteObjectListeners();
        updateRemoteObjectUI();
    }

    private void deregisterRemoteObject() {
        deregisterRemoteObjectListeners();
        updateRemoteObjectUI();
    }

    private void registerRemoteObjectListeners() {
        getRemoteObject().getComm().addListener(listenerComm);
        getRemoteObject().getPerms().addListener(listenerPerms);
    }

    private void deregisterRemoteObjectListeners() {
        getRemoteObject().getComm().removeListener(listenerComm);
        getRemoteObject().getPerms().removeListener(listenerPerms);
    }


    // Remote Object listeners

    private final ObjComm.RemoteObjectConnListener listenerComm = new ObjComm.RemoteObjectConnListener() {

        @Override
        public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            updateComm();
        }

        @Override
        public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
            updateComm();
        }

        @Override
        public void onCloudConnected(JSLRemoteObject obj) {
            updateComm();
        }

        @Override
        public void onCloudDisconnected(JSLRemoteObject obj) {
            updateComm();
        }

    };

    private final ObjPerms.RemoteObjectPermsListener listenerPerms = new ObjPerms.RemoteObjectPermsListener() {

        @Override
        public void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {
            updatePerms();
        }

        @Override
        public void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
            updatePerms();
        }

    };


    // UI widgets

    private void updateRemoteObjectUI() {
        updateRemoteObject();
        updateComm();
        updatePerms();
        updateEvents();
        updateStruct();
    }

    private void updateRemoteObject() {
        if (binding == null) return;

        if (getRemoteObject() == null)
            Log.i("SVEnergy", "updateRemoteObject() for unregistered remote object");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSLRemoteObject obj = getRemoteObject();
                String text = "WAITING";
                if (obj != null) text = obj.getName();
                binding.txtObjectName.setText(text);
                text = "WAITING";
                if (obj != null) text = obj.getId();
                binding.txtObjectId.setText(text);
            }
        });
    }

    private void updateComm() {
        if (binding == null) return;

        // TODO check communication module availability

        // update value and enable/disable
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSLRemoteObject obj = getRemoteObject();
                binding.layComm.setEnabled(obj != null);
                if (obj == null) return;

                String text = "N/A";
                int img;
                ObjComm comm = obj.getComm();
                if (comm.isLocalConnected() && comm.isCloudConnected()) {
                    text = "Object fully connected";
                    img = R.drawable.communication_both;
                } else if (comm.isLocalConnected() && !comm.isCloudConnected()) {
                    text = "Object connected\nvia DIRECT communication";
                    img = R.drawable.communication_direct;
                } else if (!comm.isLocalConnected() && comm.isCloudConnected()) {
                    text = "Object connected\nvia CLOUD communication";
                    img = R.drawable.communication_cloud;
                    //imgState = R.drawable.ic_online_cloud;
                } else {
                    text = "Object NOT connected";
                    img = R.drawable.communication_offline;
                }

                binding.imgComm.setImageResource(img);
            }
        });
    }

    private void updatePerms() {
        if (binding == null) return;

        // TODO check permissions module availability

        // update value and enable/disable
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSLRemoteObject obj = getRemoteObject();
                binding.layPerms.setEnabled(obj != null);
                if (obj == null) return;

                //int imgState = R.drawable.ic_not_available;
                String text = "N/A";
                int img = R.drawable.permission_anonymous;
                String objOwner = obj.getInfo().getOwnerId();
                if (objOwner.equals(JOSPConstants.ANONYMOUS_ID)) {
                    text = "Anonymous";
                    img = R.drawable.permission_anonymous;
                }else {
                    ObjPerms perms = obj.getPerms();
                    JOSPPerm.Type localPerm = perms.getServicePerm(JOSPPerm.Connection.OnlyLocal);
                    JOSPPerm.Type cloudPerm = perms.getServicePerm(JOSPPerm.Connection.LocalAndCloud);
                    JOSPPerm.Type maxPerm = localPerm.greaterThan(cloudPerm) ? localPerm : cloudPerm;
                    switch (maxPerm) {
                        case None:
                            text = "No permission";
                            img = R.drawable.permission_none;
                            break;
                        case Status:
                            text = "Status only";
                            img = R.drawable.permission_status;
                            break;
                        case Actions:
                            text = "Status & Actions";
                            img = R.drawable.permission_action;
                            break;
                        case CoOwner:
                            if (getJSLClient().getJSL().getUserMngr().getUserId().equals(objOwner)) {
                                text = "Owner";
                                img = R.drawable.permission_owner;
                            } else {
                                text = "CoOwner";
                                img = R.drawable.permission_coowner;
                            }
                            break;
                    }
                }
                binding.imgPerms.setImageResource(img);
                binding.txtPerms.setText(text);
            }
        });
    }

    private void updateEvents() {
        if (binding == null) return;

        // TODO check events module availability

        JSLRemoteObject obj = getRemoteObject();

        // update value and enable/disable
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.layEvents.setEnabled(obj != null);

                // values are updated by the events listener
            }
        });

        if (obj == null || PREVENT_EVENT_COUNT_FETCH) return;
        HistoryLimits limits = new HistoryLimits(null, null, null, null,
                JavaDate.getDateAltered(Calendar.DAY_OF_MONTH, -1), JavaDate.getNowDate(), null, null);
        getJSLApplication().runOnNetworkThread(new Runnable() {
            @Override
            public void run() {
                try {
                    obj.getInfo().getEventsHistory(limits, eventsListener);
                } catch (JSLRemoteObject.ObjectNotConnected e) {
                    Log.w(LOG_TAG, "Can't fetch events from '" + obj.getName() + "' because the object is not connected.");
                } catch (JSLRemoteObject.MissingPermission e) {
                    Log.w(LOG_TAG, "Can't fetch events from '" + obj.getName() + "' because missing required permission on the object.");
                }
            }
        });
    }

    private void updateStruct() {
        if (binding == null) return;

        // TODO check structure module availability

        // only enable/disable
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSLRemoteObject obj = getRemoteObject();
                binding.layStruct.setEnabled(obj != null);
            }
        });
    }

    private void showBottomSheetEventDetails(Context context, JOSPEvent eventGroup) {
        EventDetailsBottomSheet frmExportsBottomSheet = new EventDetailsBottomSheet(context, eventGroup);

        // Get the fragment manager
        FragmentManager fragmentMngr = getSupportFragmentManager();

        // Show the bottom sheet
        frmExportsBottomSheet.show(fragmentMngr, EventDetailsBottomSheet.SHEET_TAG);
    }


    // UI listeners

    private final View.OnClickListener onObjectNameEditClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getRemoteObject() == null) {
                Toast.makeText(JSLObjectDetailsActivity.this, "Object not available", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(JSLObjectDetailsActivity.this);
            builder.setTitle("Enter new object name");

            // Set up the input
            final EditText input = new EditText(JSLObjectDetailsActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newName = input.getText().toString();
                    getJSLApplication().runOnNetworkThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getRemoteObject().getInfo().setName(newName);
                            } catch (JSLRemoteObject.ObjectNotConnected e) {
                                throw new RuntimeException(e);
                            } catch (JSLRemoteObject.MissingPermission e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

    };

    private final View.OnClickListener onEventItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JOSPEventGroup event = (JOSPEventGroup) v.getTag();
            if (event == null) return;
            showBottomSheetEventDetails(JSLObjectDetailsActivity.this, event);
        }

    };

    private final HistoryObjEvents.EventsListener eventsListener = new HistoryObjEvents.EventsListener() {
        @Override
        public void receivedEvents(List<JOSPEvent> history) {


            // update value and enable/disable
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSLRemoteObject obj = getRemoteObject();
                    assert obj != null;

                    binding.txtEvents.setText(String.format("%d Events 24h", history.size()));
                }
            });
        }
    };

}