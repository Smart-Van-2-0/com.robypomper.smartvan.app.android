package com.robypomper.smartvan.smart_van.android.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Adapter to show a list of {@link SVSpec} into a {@link android.widget.ListView}.
 * <p>
 * This adapter can be used to show a list of {@link SVSpecView}s into a
 * {@link android.widget.ListView}. The adapter must be configured to use a specific
 * {@link JSLRemoteObject} to show his SV Specs, otherwise the specs values will be
 * shown as not provided.
 */
public class SVSpecsListAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<SVSpec> specsList;
    private final List<SVSpecView> specsViews;
    private JSLRemoteObject remoteObj;

    public SVSpecsListAdapter(Context context, List<SVSpec> specsList) {
        this(context, specsList, null);
    }

    public SVSpecsListAdapter(Context context, List<SVSpec> specsList, JSLRemoteObject remoteObj) {
        super();
        this.mContext=context;
        this.specsList = specsList;
        this.specsViews = new ArrayList<>(Collections.nCopies(specsList.size(), null));;
        this.remoteObj = remoteObj;
    }

    @Override
    public int getCount() {
        return specsList.size();
    }

    @Override
    public SVSpec getItem(int i) {
        return specsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SVSpecView specView = specsViews.get(i);
        if (specView == null) {
            specView = new SVSpecView(mContext);
            specView.setSpecType(specsList.get(i));
            if (remoteObj != null)
                specView.setRemoteObj(remoteObj);
            specsViews.set(i, specView);
        }
        return specView;
    }

    public JSLRemoteObject getRemoteObj() {
        return remoteObj;
    }

    public void setRemoteObj(JSLRemoteObject remoteObj) {
        this.remoteObj = remoteObj;
        for (SVSpecView specView : specsViews)
            if (specView != null)
                specView.setRemoteObj(remoteObj);
    }

}
