package com.robypomper.smartvan.smart_van.android.service;


import com.robypomper.josp.jsl.android.service.JSLServiceAutoStart;

/**
 * Implementation of the {@link JSLServiceAutoStart} class.
 */
public class SVServiceAutoStart extends JSLServiceAutoStart {

    protected Class<?> getServiceClass() {
        return SVService.class;
    }

}

