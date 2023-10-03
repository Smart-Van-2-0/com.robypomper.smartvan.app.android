package com.robypomper.smartvan.smart_van.android.app;

import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.smartvan.smart_van.android.service.SVService;

public class SVApplication extends JSLApplication<SVService> {

    @Override
    protected Class<? extends JSLClient<SVService>> getJSLClientClass() {
        return SVJSLClient.class;
    }

    @Override
    public SVJSLClient getJSLClient() {
        return (SVJSLClient) super.getJSLClient();
    }

}
