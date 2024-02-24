package com.robypomper.smartvan.smart_van.android.handlers;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.robypomper.josp.jsl.android.handlers.view.JSLBaseViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.smartvan.smart_van.android.R;

/**
 * View handler for service's icon.
 * <p>
 * Each service have an icon associated, and this handler manage the
 * corresponding ImageView.
 * <p>
 * The SVServiceIconViewHandler is used by the SVBaseServiceView to set and show
 * the service's icon.
 * <p>
 * Handler's default layout N/A.
 */
public class SVServiceIconViewHandler {

    // Constants

    /**
     * The ID of the `icoSVService` field.
     */
    public final static int UI_SV_SERVICE_ICON = R.id.icoSVService;
    /**
     * The default value for the `isHighVoltage` field.
     */
    public final static int DEF_ICON = R.drawable.ic_srv_na;


    // Internal vars

    /**
     * The Context to use to load resources from their ID.
     */
    private final Context ctx;
    /**
     * The ViewGroup to use to look for handler's Views.
     */
    private final ViewGroup mainView;   // maintained only because it can be used in the future
    /**
     * The component to show, it's not final to allows view recycling.
     */
    private JSLComponent component;     // maintained only because it can be used in the future
    /**
     * The drawable of the `icon` field.
     */
    private Drawable icon;


    // Client interface

    /**
     * The service interface for the SVServiceIconViewHandler.
     * <p>
     * Interface to be implemented by the classes that uses the
     * SVServiceIconViewHandler.
     */
    public interface Service {

        /**
         * Get the card header handler
         *
         * @return the card header handler
         */
        SVServiceIconViewHandler getIconHandler();

    }


    // Constructors

    /**
     * Create a new `SVServiceIconViewHandler` instance.
     * <p>
     * Note: different from the other handlers' constructors, this one update
     * the UI.
     *
     * @param mainView     the ViewGroup to use to look for handler's Views
     * @param component    the component to show
     */
    public SVServiceIconViewHandler(Context ctx, ViewGroup mainView, JSLComponent component) {
        this(ctx, mainView, component, false);
    }

    /**
     * Create a new `SVServiceIconViewHandler` instance.
     *
     * @param mainView     the ViewGroup to use to look for handler's Views
     * @param component    the component to show
     * @param avoidUpdateUI if `true`, the UI is not updated
     */
    public SVServiceIconViewHandler(Context ctx, ViewGroup mainView, JSLComponent component, boolean avoidUpdateUI) {
        this.ctx = ctx;
        this.mainView = mainView;
        this.component = component;
        this.icon = AppCompatResources.getDrawable(ctx, DEF_ICON);

        if (!avoidUpdateUI) updateUI();
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

        component = newComp;

        updateUI();
        // no sub-handler to update
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
     * Get the drawable of the `icon` field.
     *
     * @return the drawable of the `icon` field
     */
    public Drawable getIcon() {
        return icon;
    }

    /**
     * Set the drawable of the `icon` field.
     *
     * @param iconRes the new drawable of the `icon` field
     */
    public void setIcon(@DrawableRes int iconRes) {
        setIcon(AppCompatResources.getDrawable(ctx, iconRes));
    }

    /**
     * Set the drawable of the `icon` field.
     *
     * @param icon the new drawable of the `icon` field
     */
    public void setIcon(Drawable icon) {
        if (this.icon == icon) return;
        this.icon = icon;
        updateUI();
    }


    // UI Methods

    public void updateUI() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                JSLBaseViewHandler.trySetImage(getMainView(), UI_SV_SERVICE_ICON, getIcon());
            }
        });
    }

}
