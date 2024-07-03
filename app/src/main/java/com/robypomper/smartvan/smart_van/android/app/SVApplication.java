package com.robypomper.smartvan.smart_van.android.app;

import com.robypomper.josp.jsl.android.app.JSLApplication;
import com.robypomper.josp.jsl.android.app.JSLClient;
import com.robypomper.smartvan.smart_van.android.service.SVService;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;
import com.robypomper.smartvan.smart_van.android.storage.local.SVLocalStorage;

public class SVApplication extends JSLApplication<SVService> {


    // Constructors

    /**
     * Initialize the application's storage object.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        new SVLocalStorage(getApplicationContext());
        SVStorageSingleton.getInstance().setCurrentObjectId(null);
    }


    // JSLClient management

    /**
     * @return the {@link JSLClient} subclass to be used from current application.
     */
    @Override
    protected Class<? extends JSLClient<SVService>> getJSLClientClass() {
        return SVJSLClient.class;
    }


    /**
     * @return current application's JSLClient instance.
     */
    @Override
    public SVJSLClient getJSLClient() {
        return (SVJSLClient) super.getJSLClient();
    }

}
