package com.robypomper.smartvan.smart_van.android.app;

import android.content.Context;

import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.smartvan.smart_van.android.service.SVService;

public class SVJSLClient extends JSLClient<SVService> {

    /**
     * Create a new JSLClient and starts his internal thread.
     *
     * @param context the Android context to use for current JSLClient.
     */
    public SVJSLClient(Context context) {
        super(context);
    }

    @Override
    protected Class<?> getJSLServiceClass() {
        return SVService.class;
    }

}
