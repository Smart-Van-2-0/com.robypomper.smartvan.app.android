package com.robypomper.smartvan.smart_van.android.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecs;

/**
 * View to show a {@link SVSpec} and if it was provided by the Smart Van Box.
 * <p>
 * This view can be used to show a {@link SVSpec} and if it was provided by the Smart Van Box.
 * The view can be configured to use a specific {@link JSLRemoteObject} to check if the spec
 * was provided by the Smart Van Box, otherwise the spec will be shown as not provided.
 * <p>
 * This view show the spec name, the spec path and an icon to show if the spec was provided
 * by the Smart Van Box. The view is based on the {@link R.layout#view_sv_spec} layout.
 * @noinspection unused
 */
public class SVSpecView extends RelativeLayout {

    // Constants

    private final static int LAYOUT = R.layout.view_sv_spec;
    private final static int FOUND = android.R.drawable.presence_online;
    private final static int NOT_FOUND = android.R.drawable.presence_busy;
    private final static int OBJECT_NOT_SET = android.R.drawable.presence_away;
    private final static int UNKNOWN_SPEC = android.R.drawable.presence_offline;


    // Attributes

    private String specPath;
    private SVSpec specType;
    private JSLRemoteObject remoteObj;


    // UI Components

    private ImageView icoStatus;
    private TextView txtName;
    private TextView txtPath;


    // Constructors

    public SVSpecView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SVSpecView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SVSpecView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    // UI Methods

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Inflate ui
        LayoutInflater.from(context).inflate(LAYOUT, this, true);
        txtName = findViewById(R.id.txtName);
        txtPath = findViewById(R.id.txtPath);
        icoStatus = findViewById(R.id.icoStatus);

        // Update UI
        updateUI();
    }

    private void updateUI() {
        txtName.setText(specType == null ? "Unknown spec" : specType.getName());
        txtPath.setText(specType == null ? specPath : specType.getPath());

        int newIconId;
        if (remoteObj == null)
            newIconId = OBJECT_NOT_SET;
        else if (specType == null)
            newIconId = UNKNOWN_SPEC;
        else if (specType.checkObject(remoteObj))
            newIconId = FOUND;
        else
            newIconId = NOT_FOUND;

        icoStatus.setImageResource(newIconId);
    }


    // Getters and Setters

    public String getSpecPath() {
        return specPath;
    }

    public void setSpecPath(String specPath) {
        this.specPath = specPath == null ? "Missing 'specPath' attribute" : specPath;
        specType = SVSpecs.fromPath(this.specPath);

        updateUI();
    }

    public SVSpec getSpecType() {
        return specType;
    }

    public void setSpecType(SVSpec specType) {
        this.specType = specType;
        specPath = specType == null ? "Set a null 'specType'" : specType.getPath();

        updateUI();
    }

    public JSLRemoteObject getRemoteObject() {
        return remoteObj;
    }

    public void setRemoteObj(JSLRemoteObject remoteObj) {
        this.remoteObj = remoteObj;

        updateUI();
    }

}