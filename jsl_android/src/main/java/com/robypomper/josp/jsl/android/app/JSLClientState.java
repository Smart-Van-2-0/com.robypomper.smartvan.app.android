package com.robypomper.josp.jsl.android.app;


/**
 * JSL state representations.
 * @noinspection unused
 */
public enum JSLClientState {

    // JSL states (From JSLState)

    /**
     * JSL library instance is started and operative.
     */
    RUN,

    /**
     * JSL library instance is starting, when finish the status become
     * {@link #RUN} or {@link #STOP} if error occurs.
     */
    STARTING,

    /**
     * JSL library instance is stopped.
     */
    STOP,

    /**
     * JSL library instance is disconnecting, when finish the status
     * become {@link #STOP}.
     */
    SHOUTING,

    /**
     * JSL library instance is shouting down and startup, when finish the status
     * become {@link #RUN} or {@link #STOP} if error occurs.
     */
    RESTARTING,


    // JSLClient states

    /**
     * JSLService is not yet bounded.
     */
    NOT_BOUND,

    /**
     * JSLService is bounding.
     */
    BOUNDING,

    /**
     * JSL Service is bound but the JSL instance has not been initialized yet.
     */
    NOT_INIT_JSL,

    /**
     * JSLService is unbounding.
     */
    UNBOUNDING,

}
