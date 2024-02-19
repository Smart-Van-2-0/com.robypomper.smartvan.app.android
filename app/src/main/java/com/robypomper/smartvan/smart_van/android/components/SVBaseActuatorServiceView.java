package com.robypomper.smartvan.smart_van.android.components;

import com.robypomper.josp.jsl.android.handlers.view.JSLBaseActionViewHandler;

import java.util.Date;

/**
 * Base interface for all the actuator views.
 * <p>
 * This interface is used to define the common methods and handlers shared
 * between all the actuator views.
 * <p>
 * In particular, this interface is used to define the access to the action
 * handler and his fields.
 */
public interface SVBaseActuatorServiceView extends SVBaseControllerServiceView {

    /**
     * Get the component's last action sent from the {@link com.robypomper.josp.jsl.android.handlers.view.JSLBaseActionViewHandler}.
     *
     * @return the component's last action sent
     */
    Date getLastChange();

    /**
     * Get the action handler. Already implemented in the JSL*View classes.
     *
     * @return the action handler
     */
    JSLBaseActionViewHandler getActionHandler();

}
