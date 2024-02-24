package com.robypomper.smartvan.smart_van.android.components;

import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.smartvan.smart_van.android.handlers.SVServiceCardHeaderViewHandler;
import com.robypomper.smartvan.smart_van.android.handlers.SVServiceIconViewHandler;

/**
 * Base interface for all SV*ServiceView classes.
 * <p>
 * This interface is used to define the common methods for all SV*ServiceView
 * classes. Those interfaces are required because the SV*ServiceView classes
 * must extends the JSL*View classes. Then those interfaces are used to define
 * the common methods (and handlers) for all SV*ServiceView classes.
 * <p>
 * View's default layout NONE
 */
public interface SVBaseServiceView
        extends SVServiceCardHeaderViewHandler.Service,
        SVServiceIconViewHandler.Service {

    /**
     * Set the component handled by this view and update all handlers,
     * including subclasses' handlers. Already implemented in the JSL*View
     * classes.
     *
     * @param newComp the new component
     */
    void setComponent(JSLComponent newComp);

}
