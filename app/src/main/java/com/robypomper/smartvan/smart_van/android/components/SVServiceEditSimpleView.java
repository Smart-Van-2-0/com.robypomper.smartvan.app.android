package com.robypomper.smartvan.smart_van.android.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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


    //private String name;
    //private String iconName;

    private TextInputEditText txtName;
    //private AutoCompleteTextView txtIcon;
    private Spinner txtIcon;

    public interface OnServiceEditedListener {
        void onServiceEdited(SVServiceEditSimpleView view, JSLComponent service, String name, String iconName);
    }

    public static void show(Context ctx, JSLComponent service, OnServiceEditedListener observer) {
        SVStorage storage = SVStorageSingleton.getInstance();
        String remObjId = storage.getCurrentObjectId();

        String srvPath = service.getPath().getString();
        String srvName = storage.getPreferencesServices(remObjId).getName(srvPath);
        if (srvName == null) srvName = service.getName();
        String srvIconName = storage.getPreferencesServices(remObjId).getIconName(srvPath);
        if (srvIconName == null) srvIconName = SVServiceIcons.DEF_ICON_TXT;

        SVServiceEditSimpleView editView = new SVServiceEditSimpleView(ctx);   // TODO inflate layout with Dialog theme @see AlertDialog.Builder::setView
        editView.setName(srvName);
        editView.setIconName(srvIconName);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Store edited service name and icon
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        String newName = editView.getName();
                        String newIcon = editView.getIconName();
                        storage.getPreferencesServices(remObjId).setName(srvPath, newName);
                        storage.getPreferencesServices(remObjId).setIconName(srvPath, newIcon);
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
        //txtIcon.setThreshold(0);
        /*txtIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                txtIcon.showDropDown();
            }
        });
        txtIcon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
                setIconName(item.get("name"));
            }
        });*/
        txtIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
        return txtName.getText().toString();
    }

    public void setName(String name) {
        txtName.setText(name);
    }

    public String getIconName() {
        //return txtIcon.getText().toString();
        Object o = txtIcon.getSelectedItem();
        if (o == null) return null;
        if (o instanceof Map) return ((Map<String, String>) o).get("name");
        return o.toString();
    }

    public void setIconName(String iconName) {
        //txtIcon.setText(iconName);
        //txtIcon.setCompoundDrawablesWithIntrinsicBounds(0, 0, SVServiceIcons.iconString2Res(iconName), 0);
        for (int i = 0; i < txtIcon.getCount(); i++) {
            Map<String, String> item = (Map<String, String>) txtIcon.getItemAtPosition(i);
            if (item.get("name").equals(iconName)) {
                txtIcon.setSelection(i);
                break;
            }
        }
    }

}
