package com.robypomper.josp.jsl.android.app;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.robypomper.josp.jsl.android.R;
import com.robypomper.josp.jsl.android.service.JSLService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Base class for any application JOSP/Android based.
 * <p>
 * This class provide a global instance of the {@link JSLClient} object, so any
 * application's component can get and use it. With the JSLClient, developers
 * can bound/unbound the JSLService, but also register listeners to the JSLService.
 * It is also possible register listeners to the JSL instance, even before it
 * will be ready (before the service bound or the service's JSL instance
 * initialization).
 * <p>
 * Moreover, the JSLApplication, on his create method, copies some resources to
 * local application's storage. So, the JSL instance can access to those files
 * (such as the `jsl.yml' or the `local_ks.jsk`.
 *
 * @param <T>
 */
public abstract class JSLApplication<T extends JSLService> extends MultiDexApplication {

    private JSLClient<T> jslClient;
    ExecutorService executors_network = Executors.newFixedThreadPool(5);


    // Android

    /**
     * Create the application, copy required resources and initialize global
     * JSLClient.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("J_Android", "JSLApplication creating...");

        //if (!new File(getFilesDir(), "jsl.yml").exists())
        copyResourceToLocalStorage(R.raw.jsl, new File(getFilesDir(), "jsl.yml"));
        copyResourceToLocalStorage(R.raw.local_ks, new File(getFilesDir(), "local_ks.jks"));

        initJSLClient();
    }

    /**
     * If required, unbound the JSLService.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i("J_Android", "JSLApplication terminating...");

        if (jslClient != null)
            jslClient.unboundService();
    }


    // JSL Service Client

    /** @return current application's JSLClient instance. */
    public JSLClient<T> getJSLClient() {
        return jslClient;
    }

    /**
     * Initialize the application's JSLClient.
     * The instance create is based on the class specified from the
     * {@link #getJSLClientClass()} method.
     */
    private void initJSLClient() {
        try {
            if (jslClient == null) {
                Constructor<? extends JSLClient<T>> constructor = getJSLClientClass().getConstructor(Context.class);
                Object obj = constructor.newInstance(this);
                jslClient = getJSLClientClass().cast(obj);
                if (jslClient != null)
                    jslClient.boundService();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the {@link JSLClient} subclass to be used from current application.
     */
    protected abstract Class<? extends JSLClient<T>> getJSLClientClass();


    // Utils

    /** Copies the specified resource file into application's local storage. */
    private void copyResourceToLocalStorage(int resID, File destFile) {
        try {
            InputStream fromRes = getResources().openRawResource(resID);
            OutputStream toFile = new FileOutputStream(destFile);
            byte[] buf = new byte[8192];
            int length;
            while ((length = fromRes.read(buf)) != -1) {
                toFile.write(buf, 0, length);
            }
            toFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void runOnNetworkThread(Runnable action) {
        executors_network.execute(action);
    }

}
