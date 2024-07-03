package com.robypomper.smartvan.smart_van.android.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.commons.SVServiceIcons;
import com.robypomper.smartvan.smart_van.android.storage.SVStorage;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SVServiceEditSimpleView extends LinearLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_sv_service_edit_simple;


    // Internal vars

    private TextInputEditText txtName;
    private Spinner txtIcon;


    public interface OnServiceEditedListener {
        void onServiceEdited(SVServiceEditSimpleView view, JSLComponent service, String name, String iconName);
    }


    public static void show(Context ctx, JSLComponent service, OnServiceEditedListener observer) {
        SVStorage storage = SVStorageSingleton.getInstance();

        String srvPath = service.getPath().getString();
        String srvName = storage.getCurrentPreferencesServices().getName(srvPath);
        if (srvName == null) srvName = service.getName();
        String srvIconName = storage.getCurrentPreferencesServices().getIconName(srvPath);
        if (srvIconName == null) srvIconName = SVServiceIcons.DEF_ICON_TXT;

        SVServiceEditSimpleView editView = new SVServiceEditSimpleView(ctx);   // TODO inflate layout with Dialog theme @see AlertDialog.Builder::setView
        editView.setName(srvName);
        editView.setIconName(srvIconName);

        // Store edited service name and icon
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        String newName = editView.getName();
                        String newIcon = editView.getIconName();
                        storage.getCurrentPreferencesServices().setName(srvPath, newName);
                        storage.getCurrentPreferencesServices().setIconName(srvPath, newIcon);
                        observer.onServiceEdited(editView, service, newName, newIcon);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Edit service name and icon")
                .setView(editView)
                .setPositiveButton("Ok", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }


    // Constructors

    public SVServiceEditSimpleView(Context context) {
        super(context);
        initUI();
    }

    public SVServiceEditSimpleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public SVServiceEditSimpleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI() {

        // Inflate ui
        inflate(getContext(), LAYOUT, this);
        txtName = findViewById(R.id.txtSVServiceName);
        txtIcon = findViewById(R.id.txtSVServiceIcon);

        // Generate data for icon selector
        List<Map<String, String>> iconsData = new ArrayList<>();
        for (String name : SVServiceIcons.getIconNames()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("icon", Integer.toString(SVServiceIcons.iconString2Res(name)));
            iconsData.add(map);
        }

        // Setup icon selector
        SimpleAdapter adapter = new SimpleAdapter(getContext(),
                iconsData,
                R.layout.lay_sv_service_edit_simple_icon_list_item,
                new String[]{"name", "icon"},
                new int[]{R.id.txtSVServiceName, R.id.iconSVServiceIcon});
        txtIcon.setAdapter(adapter);
        txtIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //noinspection unchecked
                Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
                setIconName(item.get("name"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    // Getters and setters

    public String getName() {
        return txtName.getText() != null ? txtName.getText().toString() : null;
    }

    public void setName(String name) {
        txtName.setText(name);
    }

    public String getIconName() {
        //return txtIcon.getText().toString();
        Object o = txtIcon.getSelectedItem();
        if (o == null) return null;
        if (o instanceof Map)
            //noinspection unchecked
            return ((Map<String, String>) o).get("name");
        return o.toString();
    }

    public void setIconName(String iconName) {
        if (iconName == null) iconName = SVServiceIcons.DEF_ICON_TXT;
        for (int i = 0; i < txtIcon.getCount(); i++) {
            //noinspection unchecked
            Map<String, String> item = (Map<String, String>) txtIcon.getItemAtPosition(i);
            if (iconName.equals(item.get("name"))) {
                txtIcon.setSelection(i);
                break;
            }
        }
    }

}
