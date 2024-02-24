package com.robypomper.smartvan.smart_van.android.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.robypomper.josp.jsl.android.components.JSLBooleanActionView;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.handlers.SVServiceCardHeaderViewHandler;
import com.robypomper.smartvan.smart_van.android.handlers.SVServiceIconViewHandler;

import java.util.Date;


/**
 * TODO document this class
 * <p>
 * View's default layout {@link R.layout#def_view_sv_switch_actuator}
 */
public class SVSwitchActuatorView
        extends JSLBooleanActionView
        implements SVBaseActuatorServiceView {

    // Constants

    private final static int LAYOUT = R.layout.def_view_sv_switch_actuator;


    // Internal vars

    /**
     * The handler for the service's card header.
     */
    private final SVServiceCardHeaderViewHandler cardHeaderHandler;
    /**
     * The handler for the service's icon.
     */
    private final SVServiceIconViewHandler iconHandler;


    // Constructors

    public SVSwitchActuatorView(@NonNull Context context) {
        this(context, null);
    }

    public SVSwitchActuatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SVSwitchActuatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null, LAYOUT);
    }

    public SVSwitchActuatorView(@NonNull Context context, JSLBooleanAction svService, @LayoutRes int layout) {
        this(context, null, svService, layout);
    }

    public SVSwitchActuatorView(@NonNull Context context, @Nullable AttributeSet attrs, JSLBooleanAction svService, @LayoutRes int layout) {
        this(context, attrs, 0, svService, layout);
    }

    public SVSwitchActuatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, JSLBooleanAction svService, @LayoutRes int layout) {
        super(context, attrs, defStyleAttr, svService, layout);
        iconHandler = new SVServiceIconViewHandler(context, this, svService);
        cardHeaderHandler = new SVServiceCardHeaderViewHandler(this, svService, getStateHandler(), getIconHandler());

    }


    // Subclass implementation

    /**
     * Update the handlers.
     *
     * @param newComp the new component
     * @param oldComp the old component
     */
    protected void updateHandlers(JSLComponent newComp, JSLComponent oldComp) {
        super.updateHandlers(newComp, oldComp);
        cardHeaderHandler.setComponent(newComp);
        iconHandler.setComponent(newComp);
    }

    /**
     * Refresh the view.
     * <p>
     * This method is used to update the view when the component's state is changed.
     * <p>
     * It, like his super-implementations, calls the local handler to update the UI.
     */
    public void refresh() {
        super.refresh();
        getCardHeaderHandler().updateUI();
        getIconHandler().updateUI();
    }


    // Getters and Setters

    /**
     * Get the card header handler
     *
     * @return the card header handler
     */
    @Override
    public SVServiceCardHeaderViewHandler getCardHeaderHandler() {
        return cardHeaderHandler;
    }

    /**
     * Get the icon handler
     *
     * @return the icon handler
     */
    @Override
    public SVServiceIconViewHandler getIconHandler() {
        return iconHandler;
    }

    /**
     * Get the value of the `isHighVoltage` field.
     *
     * @return the value of the `isHighVoltage` field
     */
    public boolean isHighVoltage() {
        return getCardHeaderHandler().isHighVoltage();
    }

    /**
     * Get the value used to override the component's name.
     * If `null`, the component's name is used.
     *
     * @return the value used to override the component's name
     */
    public String getSVName() {
        return getCardHeaderHandler().getSVName();
    }

    /**
     * Get the value used to override the component's type.
     * If `null`, the component's type is used.
     *
     * @return the value used to override the component's type
     */
    public String getSVType() {
        return getCardHeaderHandler().getSVType();
    }

    /**
     * Get the component's state ready to be used into the UI,  from the
     * {@link com.robypomper.josp.jsl.android.handlers.view.JSLBaseViewHandler}.
     *
     * @return the component's state as String
     */
    @Override
    public String getStateTxt() {
        return getStateHandler().getStateTxt();
    }

    /**
     * Get the component's last update from the {@link com.robypomper.josp.jsl.android.handlers.view.JSLBaseViewHandler}.
     *
     * @return the component's last update
     */
    @Override
    public Date getLastUpdate() {
        return getStateHandler().getLastUpdate();
    }

    /**
     * Get the component's last action sent from the {@link com.robypomper.josp.jsl.android.handlers.view.JSLBaseActionViewHandler}.
     *
     * @return the component's last action sent
     */
    @Override
    public Date getLastChange() {
        return getActionHandler().getLastActionSent();
    }

}

