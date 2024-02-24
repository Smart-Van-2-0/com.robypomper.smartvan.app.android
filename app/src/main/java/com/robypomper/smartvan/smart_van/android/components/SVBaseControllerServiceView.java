package com.robypomper.smartvan.smart_van.android.components;

import com.robypomper.josp.jsl.android.handlers.view.JSLBaseStateViewHandler;

import java.util.Date;


/**
 * Base interface for all the controller views.
 * <p>
 * This interface is used to define the common methods and handlers shared
 * between all the controller views.
 * <p>
 * In particular, this interface is used to define the access to the state
 * handler and his fields.
 */
public interface SVBaseControllerServiceView extends SVBaseServiceView {

    /**
     * Get the component's state ready to be used into the UI,  from the
     * {@link com.robypomper.josp.jsl.android.handlers.view.JSLBaseViewHandler}.
     *
     * @return the component's state as String
     */
    String getStateTxt();

    /**
     * Get the component's last update from the {@link com.robypomper.josp.jsl.android.handlers.view.JSLBaseViewHandler}.
     *
     * @return the component's last update
     */
    Date getLastUpdate();

    /**
     * Get the state handler. Already implemented in the JSL*View classes.
     *
     * @return the state handler
     */
    JSLBaseStateViewHandler getStateHandler();

    /**
     * Refresh the view. TODO check and document this method.
     */
    void refresh();

}
