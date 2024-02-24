package com.robypomper.smartvan.smart_van.android.components;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.smartvan.smart_van.android.R;


/**
 * Class that represents a bottom sheet for a SV service.
 * <p>
 * It's a {@link BottomSheetDialogFragment} that contains a {@link SVBaseServiceView}
 * to show the service's data. The service view is populated with the given
 * service's data and can support all the service types.
 * <p>
 * This class provides also a listener to listen for the onDetach event. That
 * event is fired when the bottom sheet is detached from the activity.
 * <p>
 * View's default layout {@link R.layout#bottom_sheet_svservice}.
 * Then, depending on the SV Service type, then it's inflated the corresponding
 * sub-layout.
 */
public class SVServiceBottomSheet extends BottomSheetDialogFragment {

    // Constants

    public static final String TAG = SVServiceBottomSheet.class.getSimpleName();
    private final static int LAYOUT = R.layout.bottom_sheet_svservice;
    private static final int LAY_BIN_CTRL_CARD = R.layout.bottom_sheet_sv_binary_controller;
    private static final int LAY_PERC_CTRL_CARD = R.layout.bottom_sheet_sv_percent_controller;
    private static final int LAY_SWITCH_ACT_CARD = R.layout.bottom_sheet_sv_switch_actuator;
    private static final int LAY_DIMMER_ACT_CARD = R.layout.bottom_sheet_sv_dimmer_actuator;


    // Internal vars

    private final Context context;
    private JSLComponent svService;
    private final SVBaseControllerServiceView compView;
    private OnDetachListener onDetachListener;


    // Constructors

    public SVServiceBottomSheet(Context context) {
        this(context, null, 0);
    }

    public SVServiceBottomSheet(Context context, JSLComponent svService) {
        this(context, null, svService);
    }

    public SVServiceBottomSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVServiceBottomSheet(Context context, AttributeSet attrs, JSLComponent svService) {
        this(context, attrs, 0, svService);
    }

    public SVServiceBottomSheet(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    public SVServiceBottomSheet(Context context, AttributeSet attrs, int defStyle, JSLComponent svService) {
        super();
        this.context = context;
        this.svService = svService;

        // Parse attributes
        // ...

        // Init svService view, so it's available before onCreateView is called
        compView = createComponentView();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((View) compView).setLayoutParams(params);
        ((View) compView).setBackgroundColor(Color.argb(0, 0, 0, 0));
    }


    // Android lifecycle methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(LAYOUT, container, false);

        assert v instanceof ViewGroup;
        ViewGroup mainLayout = (ViewGroup) v;
        mainLayout.addView((View) compView);

        return v;
    }

    private SVBaseControllerServiceView createComponentView() {
        if (svService instanceof JSLBooleanAction)
            return new SVSwitchActuatorView(context, (JSLBooleanAction) svService, LAY_SWITCH_ACT_CARD);
        if (svService instanceof JSLRangeAction)
            return new SVDimmerActuatorView(context, (JSLRangeAction) svService, LAY_DIMMER_ACT_CARD);
        if (svService instanceof JSLBooleanState)
            return new SVBinaryControllerView(context, (JSLBooleanState) svService, LAY_BIN_CTRL_CARD);
        if (svService instanceof JSLRangeState)
            return new SVPercentControllerView(context, (JSLRangeState) svService, LAY_PERC_CTRL_CARD);

        throw new IllegalArgumentException("Service type not supported");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (onDetachListener != null)
            onDetachListener.onDetach(this);
    }


    // Getters and setters

    /**
     * Get the service handled by this view.
     *
     * @return the service/JSL component handled by this view
     */
    public JSLComponent getService() {
        return svService;
    }

    /**
     * Set the service handled by this view and update all handlers.
     *
     * @param newService the new service/JSL component to set
     */
    public void setService(JSLComponent newService) {
        this.svService = newService;
        compView.setComponent(newService);
    }

    /**
     * Get the service view.
     * <p>
     * Returned type is the base type, so it's necessary to cast it to the
     * specific type depending on the service.
     *
     * @return the service view
     */
    public SVBaseControllerServiceView getCompView() {
        return compView;
    }


    // OnDetach Listener

    /**
     * Set the onDetach listener.
     *
     * @param onDetachListener the onDetach listener
     */
    public void setOnDetachListener(OnDetachListener onDetachListener) {
        this.onDetachListener = onDetachListener;
    }

    /**
     * Interface to listen for the onDetach event.
     * <p>
     * This interface is used to listen for the onDetach event, so it's possible
     * to perform some actions when the view is detached.
     */
    public interface OnDetachListener {
        void onDetach(SVServiceBottomSheet frmComponentBottomSheet);
    }

}
