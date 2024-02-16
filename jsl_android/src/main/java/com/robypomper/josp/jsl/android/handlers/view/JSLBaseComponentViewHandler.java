package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.handlers.base.JSLBaseComponentHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;

import java.text.DateFormat;


/**
 * Base class to handle the view of a {@link JSLComponent}.
 * <p>
 * This handler, like his subclasses, is used to update the UI of a component
 * into a view. It's used to update the UI when the component changes his values
 * or states.
 * <p>
 * In order to keep the UI updated, the handler uses a ViewGroup to access the
 * UI components and a JSLComponent to get the data to show. The ViewGroup is
 * given as a parameter to the constructor and it can not be changed after the
 * construction. The JSLComponent is given as a parameter to the constructor,
 * but it can be changed after the construction (this class support the
 * component switch so you can easily recycle the view).
 * <p>
 * The ViewHandler classes are use the given layout to look for specific Views,
 * View that are used to show the component's data. The ViewHandler classes
 * are not responsible to create the view, but only to update the view's data.
 * Any ViewHandler class has a set of constants to identify the views to update
 * please, check those constants to know which views you can include into your
 * layout.
 * <p>
 * Supported views (see {@link R.layout#placeholder_jsl_base_component_view}):
 * <ul>
 *     <li>{@link #UI_COMP_NAME} ({@link TextView}): the component's name</li>
 *     <li>{@link #UI_COMP_TYPE} ({@link TextView}): the component's type</li>
 *     <li>{@link #UI_COMP_PATH} ({@link TextView}): the component's path</li>
 *     <li>{@link #UI_COMP_DESCR} ({@link TextView}): the component's description</li>
 * <p>
 * For each supported view, the handler provides the default values (used when
 * the component, or other fields, is not available) and the default formats to
 * use to convert the data to string. Client class, can change those default
 * values using the provided setters.
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_INIT_NAME}: default component's name used when the component is not available</li>
 *     <li>{@link #DEF_INIT_TYPE}: default component's type used when the component is not available</li>
 *     <li>{@link #DEF_INIT_PATH}: default component's path used when the component is not available</li>
 *     <li>{@link #DEF_INIT_DESCR}: default component's description used when the component is not available</li>
 * </ul>
 * <p>
 * Subclass conventions - ViewHandler's Constants:
 * - UI_XY: ids for the component's view
 * - DEF_INIT_XY: values used when no component is set
 * - DEF_FORMAT_XY: default formats to use for conversion to string
 * - DEF_VAL_XY: default strings to use when the state/value is unknown
 * - DEF_CMD_XY: default strings used as command labels (only for actions)
 * - DEF_{OTHER}_: like DEF_ACTION_ as strings to describe action states
 * <p>
 * TODO add remote object info handling
 *
 * @noinspection unused
 */
public abstract class JSLBaseComponentViewHandler
        extends JSLBaseViewHandler {

    // Constants

    /**
     * The id for the component's name {@link TextView}
     */
    public final static int UI_COMP_NAME = R.id.txtJSLCompName;
    /**
     * The id for the component's type {@link TextView}
     */
    public final static int UI_COMP_TYPE = R.id.txtJSLCompType;
    /**
     * The id for the component's path {@link TextView}
     */
    public final static int UI_COMP_PATH = R.id.txtJSLCompPath;
    /**
     * The id for the component's description {@link TextView}
     */
    public final static int UI_COMP_DESCR = R.id.txtJSLCompDescr;
    /**
     * Default component's name used when the component is not available
     */
    public final static String DEF_INIT_NAME = "N/A";
    /**
     * Default component's type used when the component is not available
     */
    public final static String DEF_INIT_TYPE = "N/A";
    /**
     * Default component's path used when the component is not available
     */
    public final static String DEF_INIT_PATH = "Component not available";
    /**
     * Default component's description used when the component is not available
     */
    public final static String DEF_INIT_DESCR = "Component not available";


    // Internal vars

    /**
     * The component to show, it's not final to allows view recycling.
     */
    private JSLComponent component;
    /**
     * Component's name used when the component is not available
     */
    private String initName = DEF_INIT_NAME;
    /**
     * Component's type used when the component is not available
     */
    private String initType = DEF_INIT_TYPE;
    /**
     * Component's path used when the component is not available
     */
    private String initPath = DEF_INIT_PATH;
    /**
     * Component's description used when the component is not available
     */
    private String initDescr = DEF_INIT_DESCR;


    // Constructors

    /**
     * Create a new JSLBaseViewHandler based on given mainView and component.
     * <p>
     * This constructor uses the default time and date formats.
     *
     * @param ctx       the context
     * @param mainView  the mainView to use to look for component's Views
     * @param component the component to show
     */
    public JSLBaseComponentViewHandler(Context ctx, ViewGroup mainView, JSLComponent component) {
        this(ctx, mainView, component, DEF_FORMAT_TIME, DEF_FORMAT_DATE);
    }

    /**
     * Create a new JSLBaseViewHandler.
     *
     * @param ctx        the context
     * @param mainView   the mainView to use to look for component's Views
     * @param component  the component to show
     * @param formatTime the time format to use for date (only time) to string conversion
     * @param formatDate the time format to use for date (only date) to string conversion
     */
    public JSLBaseComponentViewHandler(Context ctx, ViewGroup mainView, JSLComponent component, DateFormat formatTime, DateFormat formatDate) {
        super(ctx, mainView);
        setComponent(component);
    }


    // Subclasses abstract methods

    /**
     * Method to allow subclasses to register the component to their handlers.
     * <p>
     * When this method is executed, the new component is already set.
     *
     * @param newComp the new component
     * @param oldComp the old component
     */
    protected abstract void updateHandlers(JSLComponent newComp, JSLComponent oldComp);


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
        updateHandlers(newComp, oldComp);
    }

    /**
     * Get the component's name ready to be used into the UI.
     *
     * @return the component's name, or the default name if the component is
     * not available
     */
    public String getName() {
        return getComponent() != null ? getComponent().getName() : getInitName();
    }

    /**
     * Get the component's type ready to be used into the UI.
     *
     * @return the component's type, or the default type if the component is
     * not available
     */
    public String getType() {
        return getComponent() != null ? getComponent().getType() : getInitType();
    }

    /**
     * Get the component's path ready to be used into the UI.
     *
     * @return the component's path, or the default path if the component is
     * not available
     */
    public String getPath() {
        return getComponent() != null ? getComponent().getPath().getString() : getInitPath();
    }

    /**
     * Get the component's description ready to be used into the UI.
     *
     * @return the component's description, or the default description if the
     * component is not available
     */
    public String getDescr() {
        return getComponent() != null ? getComponent().getDescr() : getInitDescr();
    }


    // Getter and Setter for init vars

    /**
     * Get the component's name used when the component is not available.
     *
     * @return the component's name used when the component is not available
     */
    public String getInitName() {
        return initName;
    }

    /**
     * Set the component's name used when the component is not available.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param initName the new name, if null the default name is used
     */
    public void setInitName(String initName) {
        if (this.initName.equals(initName)) return;

        this.initName = initName;
        if (getComponent() == null) updateUILabelsRefresh();
    }

    /**
     * Get the component's type used when the component is not available.
     *
     * @return the component's type used when the component is not available
     */
    public String getInitType() {
        return initType;
    }

    /**
     * Set the component's type used when the component is not available.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param initType the new type, if null the default type is used
     */
    public void setInitType(String initType) {
        if (this.initType.equals(initType)) return;

        this.initType = initType;
        if (getComponent() == null) updateUILabelsRefresh();
    }

    /**
     * Get the component's path used when the component is not available.
     *
     * @return the component's path used when the component is not available
     */
    public String getInitPath() {
        return initPath;
    }

    /**
     * Set the component's path used when the component is not available.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param initPath the new path, if null the default path is used
     */
    public void setInitPath(String initPath) {
        if (this.initPath.equals(initPath)) return;

        this.initPath = initPath;
        if (getComponent() == null) updateUILabelsRefresh();
    }

    /**
     * Get the component's description used when the component is not available.
     *
     * @return the component's description used when the component is not available
     */
    public String getInitDescr() {
        return initDescr;
    }

    /**
     * Set the component's description used when the component is not available.
     * <p>
     * If the handler's component is available, then the UI (labels) is updated.
     *
     * @param initDescr the new description, if null the default description is used
     */
    public void setInitDescr(String initDescr) {
        if (this.initDescr.equals(initDescr)) return;

        this.initDescr = initDescr;
        if (getComponent() == null) updateUILabelsRefresh();
    }


    // UI Methods

    /**
     * Update all handled Views into the UI.
     * <p>
     * This method is called by the setComponent method, so you don't need to
     * call it manually. Although, you can call it manually to force the UI
     * update.
     */
    public void updateUI() {
        updateUILabelsRefresh();
    }

    /**
     * Update the views used as labels (all component's info not related with
     * states nor actions) into the UI.
     */
    protected void updateUILabelsRefresh() {
        getMainView().post(new Runnable() {
            @Override
            public void run() {
                trySetText(getMainView(), UI_COMP_NAME, getName());
                trySetText(getMainView(), UI_COMP_TYPE, getType());
                trySetText(getMainView(), UI_COMP_PATH, getPath());
                trySetText(getMainView(), UI_COMP_DESCR, getDescr());
            }
        });
    }


    // JSLBaseComponentHandler.Observer

    /**
     * Listener for the handler's component switch.
     * <p>
     * Ignored.
     *
     * @param handler the handler
     * @param newComp the new component
     * @param oldComp the old component
     */
    public void onComponentChanged(JSLBaseComponentHandler handler, JSLComponent newComp, JSLComponent oldComp) {
    }

}
