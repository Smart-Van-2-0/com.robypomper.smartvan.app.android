package com.robypomper.smartvan.smart_van.android.handlers;

import android.view.View;
import android.view.ViewGroup;

import com.robypomper.josp.jsl.android.handlers.view.JSLBaseStateViewHandler;
import com.robypomper.josp.jsl.android.handlers.view.JSLBaseViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.smartvan.smart_van.android.R;


/**
 * View handler for the card header of a service.
 * <p>
 * Card header is the top part of the card, it contains the service's name and
 * type, an optional menu and an icon to show if the service is high voltage.
 * <p>
 * Differently to other view handlers, that host their own handlers, this
 * class use an external state handler to get the service name and type (SVName
 * and SVType). This is because the SVServiceCardHeaderViewHandler is always
 * used as complementary ViewHandlers. That means it manage only a subset of
 * the component's fields, so (like JSLBaseServiceView and his children) and
 * the state handler is already initialized by the supported SVBaseServiceView
 * instance.
 * <p>
 * The SVServiceCardHeaderViewHandler is used by the SVBaseServiceView to manage
 * and show the card header of a service.
 * <p>
 * Handler's default layout {@link R.layout#lay_service_card_header}
 */
public class SVServiceCardHeaderViewHandler {

    // Constants

    /**
     * The ID of the `btnSVServiceMenu` field.
     */
    public final static int UI_SV_SERVICE_MENU = R.id.btnSVServiceMenu;
    /**
     * The ID of the `icoSVServiceHighVoltage` field.
     */
    public final static int UI_SV_SERVICE_HIGH_VOLTAGE = R.id.icoSVServiceHighVoltage;
    /**
     * The ID of the `txtSVServiceName` field.
     */
    public final static int UI_SV_SERVICE_NAME = R.id.txtSVServiceName;
    /**
     * The ID of the `txtSVServiceType` field.
     */
    public final static int UI_SV_SERVICE_TYPE = R.id.txtSVServiceType;
    /**
     * The default value for the `isHighVoltage` field.
     */
    public final static boolean DEF_IS_HIGH_VOLTAGE = false;


    // Internal vars


    /**
     * The ViewGroup to use to look for handler's Views.
     */
    private final ViewGroup mainView;
    /**
     * The component to show, it's not final to allows view recycling.
     */
    private JSLComponent component;
    /**
     * The handler for the component's state, in this case the handler is NOT
     * managed by this view handler.
     * <p>
     * This reference to the state handler is used only to get the service name
     * and type (SVName and SVType) and keep them synchronized.
     */
    private final JSLBaseStateViewHandler stateHandler;
    /**
     * The value of the `isHighVoltage` field.
     */
    private boolean isHighVoltage = DEF_IS_HIGH_VOLTAGE;
    /**
     * The value used to override the component's name.
     * If `null`, the component's name is used.
     */
    private String svName;
    /**
     * The value used to override the component's type.
     * If `null`, the component's type is used.
     */
    private String svType;


    // Client interface

    /**
     * The service interface for the SVServiceCardHeaderViewHandler.
     * <p>
     * Interface to be implemented by the classes that uses the
     * SVServiceCardHeaderViewHandler.
     */
    public interface Service {

        /**
         * Get the card header handler
         *
         * @return the card header handler
         */
        SVServiceCardHeaderViewHandler getCardHeaderHandler();

        /**
         * Get the value of the `isHighVoltage` field.
         *
         * @return the value of the `isHighVoltage` field
         */
        boolean isHighVoltage();

        /**
         * Get the value used to override the component's name.
         * If `null`, the component's name is used.
         *
         * @return the value used to override the component's name
         */
        String getSVName();

        /**
         * Get the value used to override the component's type.
         * If `null`, the component's type is used.
         *
         * @return the value used to override the component's type
         */
        String getSVType();

    }


    // Constructors

    /**
     * Create a new `SVServiceCardHeaderViewHandler` instance.<br/>
     * The `stateHandler` is used only to get the service name and type (SVName
     * and SVType) and keep them synchronized.
     *
     * @param mainView     the ViewGroup to use to look for handler's Views
     * @param component    the component to show
     * @param stateHandler the handler for the component's state
     */
    public SVServiceCardHeaderViewHandler(ViewGroup mainView, JSLComponent component, JSLBaseStateViewHandler stateHandler) {
        this.mainView = mainView;
        this.component = component;
        this.stateHandler = stateHandler;
        JSLBaseViewHandler.trySetOnClickListener(getMainView(), UI_SV_SERVICE_MENU, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onMenuClick();
                System.out.println("################################### Show menu ###################################");
            }
        });
    }


    // Getters and Setters

    /**
     * Get the component managed by this handler.
     *
     * @return the component managed by this handler
     */
    public JSLComponent getComponent() {
        return component;
    }

    /**
     * Set the component to show.
     * <p>
     * This method is used to change the component to show. It's used to recycle
     * the view, when the view is recycled, the newComp is changed and the
     * handler is updated to show the new newComp.
     *
     * @param newComp the newComp to show
     */
    public void setComponent(JSLComponent newComp) {
        if (getComponent() == newComp) return;

        JSLComponent oldComp = getComponent();
        component = newComp;

        updateUI();
        // no handler to update
    }

    /**
     * Get the ViewGroup to use to look for handler's Views.
     *
     * @return the ViewGroup to use to look for handler's Views
     */
    public ViewGroup getMainView() {
        return mainView;
    }

    /**
     * Get the value of the `isHighVoltage` field.
     *
     * @return the value of the `isHighVoltage` field
     */
    public boolean isHighVoltage() {
        return isHighVoltage;
    }

    /**
     * Set the value of the `isHighVoltage` field.
     *
     * @param isHighVoltage the new value of the `isHighVoltage` field
     */
    public void setHighVoltage(boolean isHighVoltage) {
        if (isHighVoltage() == isHighVoltage) return;

        this.isHighVoltage = isHighVoltage;
        updateUI();
    }

    /**
     * Get the value used to override the component's name.
     * If `null`, the component's name is used.
     *
     * @return the value used to override the component's name
     */
    public String getSVName() {
        if (svName != null) return svName;
        return stateHandler.getName();
    }

    /**
     * Set the value used to override the component's name.
     * If `null`, the component's name is used.
     *
     * @param svName the value used to override the component's name
     */
    public void setSVName(String svName) {
        this.svName = svName;
        updateUI();
    }

    /**
     * Get the value used to override the component's type.
     * If `null`, the component's type is used.
     *
     * @return the value used to override the component's type
     */
    public String getSVType() {
        if (svType != null) return svType;
        return stateHandler.getType();
    }

    /**
     * Set the value used to override the component's type.
     * If `null`, the component's type is used.
     *
     * @param svType the value used to override the component's type
     */
    public void setSVType(String svType) {
        this.svType = svType;
        updateUI();
    }


    // UI Methods

    public void updateUI() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                JSLBaseViewHandler.trySetText(getMainView(), UI_SV_SERVICE_NAME, getSVName());
                JSLBaseViewHandler.trySetText(getMainView(), UI_SV_SERVICE_TYPE, getSVType());
                JSLBaseViewHandler.trySetVisibility(getMainView(), UI_SV_SERVICE_HIGH_VOLTAGE, isHighVoltage() ? View.VISIBLE : View.GONE);
            }
        });
    }

}
