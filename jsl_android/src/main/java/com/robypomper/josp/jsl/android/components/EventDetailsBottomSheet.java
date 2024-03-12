package com.robypomper.josp.jsl.android.components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.protocol.JOSPEventGroup;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A BottomSheetDialogFragment that shows the details of a JOSPEvent.
 * <p>
 * The this fragment is created with a JOSPEvent, it shows the details of the event
 * like the type, the phase, the payload, the error payload, etc.
 * If the event is a JOSPEventGroup, it shows also the number of events in the group.
 * <p>
 * Moreover, it allows to export the event details to the clipboard.
 * <p>
 * TODO add attributes to customize the UI like the TextAppearance.
 * @noinspection unused
 */
public class EventDetailsBottomSheet extends BottomSheetDialogFragment {

    // Constants

    private static final String LOG_TAG = EventDetailsBottomSheet.class.getSimpleName();
    public static final String SHEET_TAG = EventDetailsBottomSheet.class.getSimpleName();
    private static final int LAYOUT = R.layout.bottom_sheet_event_details;


    // Internal vars

    private final Context currentContext;
    private final JOSPEvent event;


    // View args

    private TextView txtEventPhase;
    private ImageView imgEventInfo;
    private TextView txtEventDate;
    private TextView txtEventDescriptionLabel;
    private TextView txtEventCountLabel;
    private TextView txtEventCount;
    private TextView txtEventId;
    private TextView txtEventSrcType;
    private TextView txtEventSrcId;
    private TextView txtEventErrorTypeLabel;
    private TextView txtEventErrorType;
    private TextView txtEventErrorMsgLabel;
    private TextView txtEventErrorMsg;
    private TableLayout txtEventPayloadTable;
    private Button btnOk;


    // Constructors

    /**
     * Create a new EventDetailsBottomSheet with the given event.
     *
     * @param context the context.
     * @param event   the event to show.
     */
    public EventDetailsBottomSheet(Context context, JOSPEvent event) {
        this(context, null, event);
    }

    /**
     * Create a new EventDetailsBottomSheet with the given event.
     *
     * @param context the context.
     * @param attrs   the attributes.
     * @param event   the event to show.
     */
    public EventDetailsBottomSheet(Context context, AttributeSet attrs, JOSPEvent event) {
        this(context, attrs, 0, event);
    }

    /**
     * Create a new EventDetailsBottomSheet with the given event.
     *
     * @param context      the context.
     * @param attrs        the attributes.
     * @param defStyle     the style.
     * @param event        the event to show.
     */
    public EventDetailsBottomSheet(Context context, AttributeSet attrs, int defStyle, JOSPEvent event) {
        super();
        this.currentContext = context;
        this.event = event;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(LAYOUT, container, false);

        // Get UI components
        txtEventPhase = v.findViewById(R.id.txtEventPhase);
        imgEventInfo = v.findViewById(R.id.imgEventInfo);
        txtEventDate = v.findViewById(R.id.txtEventDate);
        txtEventDescriptionLabel = v.findViewById(R.id.txtEventDescription);
        txtEventCountLabel = v.findViewById(R.id.txtEventCountLabel);
        txtEventCount = v.findViewById(R.id.txtEventCount);
        txtEventId = v.findViewById(R.id.txtEventId);
        txtEventSrcType = v.findViewById(R.id.txtEventSrcType);
        txtEventSrcId = v.findViewById(R.id.txtEventSrcId);
        txtEventErrorTypeLabel = v.findViewById(R.id.txtEventErrorTypeLabel);
        txtEventErrorType = v.findViewById(R.id.txtEventErrorType);
        txtEventErrorMsgLabel = v.findViewById(R.id.txtEventErrorMsgLabel);
        txtEventErrorMsg = v.findViewById(R.id.txtEventErrorMsg);
        txtEventPayloadTable = v.findViewById(R.id.txtEventPayloadTable);

        // Setting onClick behavior to the button
        imgEventInfo.setOnClickListener(onEventInfoClickListener);

        // setup Apply|Cancel buttons
        btnOk = v.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(onBtnOkClickListener);

        updateEventValues();
        //updateTitleTextAppearance();
        //updateTxtTextAppearance();
        //updateTglTextAppearance();
        //updateBtnTextAppearance();

        return v;
    }


    // Getters/Setters

    private JOSPEvent getEvent() {
        return event;
    }


    // UI

    private final View.OnClickListener onEventInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(currentContext, imgEventInfo);

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.bottom_sheet_event_details_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.copyEvent)
                        copyEventToClipboard();
                    else if (menuItem.getItemId() == R.id.copyPayload)
                        copyEventPayloadToClipboard();
                    else if (menuItem.getItemId() == R.id.copyErrorMsg)
                        copyEventErrorMsgToClipboard();
                    else if (menuItem.getItemId() == R.id.copyErrorPayload)
                        copyEventErrorPayloadToClipboard();
                    else if (menuItem.getItemId() == R.id.copyErrorStack)
                        copyEventErrorStackToClipboard();
                    else
                        return false;
                    return true;
                }
            });

            if (event.getErrorPayload() == null) {
                popupMenu.getMenu().findItem(R.id.copyErrorMsg).setVisible(false);
                popupMenu.getMenu().findItem(R.id.copyErrorPayload).setVisible(false);
                popupMenu.getMenu().findItem(R.id.copyErrorStack).setVisible(false);
            }

            // Showing the popup menu
            popupMenu.show();
        }
    };

    private void copyEventToClipboard() {
        copyStringToClipboard("Event", JOSPEvent.toString(event).replace(";", ";\n"));
    }

    private void copyEventPayloadToClipboard() {
        copyStringToClipboard("Event Payload", event.getPayload().replace("{","{\n").replace("}","\n}").replace(",", ",\n"));
    }

    private void copyEventErrorMsgToClipboard() {
        Map<String, String> errorPayloadMap = parsePayload(event.getErrorPayload());
        copyStringToClipboard("Error Message", errorPayloadMap.get("msg"));
    }

    private void copyEventErrorPayloadToClipboard() {
        copyStringToClipboard("Error Payload", event.getErrorPayload().replace("{","{\n").replace("}","\n}").replace(",", ",\n"));
    }

    private void copyEventErrorStackToClipboard() {
        Map<String, String> errorPayloadMap = parsePayload(event.getErrorPayload());
        String stackStr = errorPayloadMap.get("stack");
        if (stackStr == null)
            Log.w(LOG_TAG, "Given event's error payload not formatted as expected, it missing the 'stack' field");
        else
            copyStringToClipboard("Error Stack", stackStr.replace("[","[\n").replace("]","\n]"));
    }

    private void copyStringToClipboard(String label, String str) {
        ClipboardManager clipboard = (ClipboardManager) currentContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, str);
        clipboard.setPrimaryClip(clip);
    }

    private final View.OnClickListener onBtnOkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };


    // Events processing

    @SuppressLint("DefaultLocale")
    private void updateEventValues() {
        boolean isGroup = event instanceof JOSPEventGroup && ((JOSPEventGroup) event).getCount() > 1;
        boolean isError = event.getErrorPayload() != null;

        JOSPEventGroup group = null;
        List<Date> emittedList = null;
        if (isGroup) {
            group = (JOSPEventGroup) event;
            emittedList = group.getEmittedAtList();
        }

        String dateStr;
        if (!isGroup) {
            dateStr = String.format("Emitted at %s", formatDate(event.getEmittedAt()));
        } else
            dateStr = String.format("Emitted from %s to %s", formatDate(emittedList.get(0)), formatDate(emittedList.get(emittedList.size() - 1)));

        txtEventPhase.setText(event.getPhase());
        txtEventDate.setText(dateStr);

        if (!isGroup) txtEventDescriptionLabel.setText(R.string.bottom_sheet_event_details_event_descr);
        else txtEventDescriptionLabel.setText(R.string.bottom_sheet_event_details_event_descr_group);

        if (!isGroup) {
            txtEventCountLabel.setVisibility(View.GONE);
            txtEventCount.setVisibility(View.GONE);
        } else txtEventCount.setText(String.valueOf(group.getCount()));

        if (!isGroup) txtEventId.setText(String.format("# %d", event.getId()));
        else txtEventId.setText(String.format("#s %s", group.getIds().toString().replace('[',' ').replace(']', ' ')));

        txtEventSrcType.setText(event.getSrcType().name());
        txtEventSrcId.setText(event.getSrcId());

        if (!isError) {
            txtEventErrorTypeLabel.setVisibility(View.GONE);
            txtEventErrorType.setVisibility(View.GONE);
            txtEventErrorMsgLabel.setVisibility(View.GONE);
            txtEventErrorMsg.setVisibility(View.GONE);
        } else {
            Map<String, String> errorPayloadMap = parsePayload(event.getErrorPayload());
            txtEventErrorType.setText(errorPayloadMap.get("type"));
            txtEventErrorMsg.setText(errorPayloadMap.get("msg"));
        }

        populate(txtEventPayloadTable, parsePayload(event.getPayload()));
    }

    private String formatDate(Date date) {
        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(currentContext);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(currentContext);

        if (DateUtils.isToday(date.getTime()))
            return timeFormat.format(date);
        else
            return timeFormat.format(date) + " " + dateFormat.format(date);
    }

    /**
     * Convert given JSON string into a Map<String, String> containing the key-value pairs.
     * <p>
     * Keys are the JSON object keys, values are the JSON object values.
     *
     * @param payload string to parse that contains a JSON object with key-value pairs.
     * @return a Map<String, String> containing the key-value pairs.
     */
    private Map<String, String> parsePayload(String payload) {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayInputStream from = new ByteArrayInputStream(payload.getBytes());
        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

        HashMap<String,Object> map;
        try {
            map = mapper.readValue(from, typeRef);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error parsing payload: " + payload, e);
            return new HashMap<>();
        }

        HashMap<String,String> mapString = new HashMap<>();
        addRecursively("", map, mapString);

        return mapString;
    }

    private void addRecursively(String prefix, Map<String, Object> map, Map<String, String> mapString) {
        String prefixDotted = prefix.isEmpty() ? "" : prefix + ".";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map)
                //noinspection unchecked
                addRecursively(prefixDotted + entry.getKey(), (Map<String, Object>) entry.getValue(), mapString);
            else
                mapString.put(prefixDotted + entry.getKey(), entry.getValue().toString());
        }
    }

    private void populate(TableLayout table, Map<String, String> map) {
        // From dp into pixel
        int dp = 4;
        int pixel = (int) (dp * currentContext.getResources().getDisplayMetrics().density + 0.5f);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            TableRow row = new TableRow(currentContext);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.setPadding(pixel, pixel, pixel, pixel);

            TextView key = new TextView(currentContext);
            key.setText(entry.getKey());
            key.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(key);

            TextView value = new TextView(currentContext);
            value.setText(entry.getValue());
            value.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(value);

            table.addView(row);
        }
    }

}
