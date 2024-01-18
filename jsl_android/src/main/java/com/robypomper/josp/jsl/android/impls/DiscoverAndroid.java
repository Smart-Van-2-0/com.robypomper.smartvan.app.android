package com.robypomper.josp.jsl.android.impls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.robypomper.discovery.DiscoverAbs;
import com.robypomper.discovery.DiscoveryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class implements the {@link com.robypomper.discovery.Discover} interface
 * using the Android {@link NsdManager} class.
 * <p>
 * This class is used by the JSLService to discover JSL instances.
 */
public class DiscoverAndroid extends DiscoverAbs {

    private final String LOG_TAG = "JSLA.Discover";

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DiscoverAndroid.class);

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private static NsdManager nsdManager;
    private final NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {

        // Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String serviceType) {
            Log.i(LOG_TAG, String.format("Services discovery started (%s)", serviceType));
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
            Log.d(LOG_TAG, "Service discovered " + serviceInfo);
            if (!serviceInfo.getServiceType().startsWith(getServiceType())) {
                Log.d(LOG_TAG, String.format("Unknown Service Type '%s' with '%s' name instead %s", serviceInfo.getServiceType(), serviceInfo.getServiceName(), getServiceType()));
                return;
            }
            try {
                nsdManager.resolveService(serviceInfo, resolveListener);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, String.format("Error resolving service '%s' with '%s' name because listener already in use", serviceInfo.getServiceType(), serviceInfo.getServiceName()), e);
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
            Log.d(LOG_TAG, "Service lost:      " + serviceInfo);
            deregisterService(new DiscoveryService(serviceInfo.getServiceName(), serviceInfo.getServiceType(), null, null, null, null, null));
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(LOG_TAG, String.format("Services discovery stopped (%s)", serviceType));
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.d(LOG_TAG, String.format("Services discovery error on startup (errCode: %d; %s)", errorCode, serviceType));
            nsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.d(LOG_TAG, String.format("Services discovery error on shutdown (errCode: %d; %s)", errorCode, serviceType));
            nsdManager.stopServiceDiscovery(this);
        }
    };

    private final NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(LOG_TAG, String.format("Service resolution failed (errCode: %d; %s)", errorCode, serviceInfo));
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d(LOG_TAG, "Service resolved   " + serviceInfo);

            serviceInfo.getHost();
            String attributesAsString = ""; // not available before the Android API level 21
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                List<String> attributesStrings = new ArrayList<>();
                Map<String, byte[]> attributes = serviceInfo.getAttributes();
                for (String key : attributes.keySet())
                    attributesStrings.add(String.format("%s=%s", key, attributes.get(key) != null ? new String(attributes.get(key), StandardCharsets.UTF_8) : ""));
                attributesAsString = String.join(",", attributesStrings);
            }

            registerService(new DiscoveryService(serviceInfo.getServiceName(), serviceInfo.getServiceType(), "NsdManager", "IPv4", serviceInfo.getHost(), serviceInfo.getPort(), attributesAsString));
        }
    };


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    public DiscoverAndroid(String srvType) {
        super(srvType);
    }

    /**
     * Set the context to use for discovery.
     *
     * @param context the context to use.
     */
    public static void setContext(Context context) {
        ctx = context;
        nsdManager = (NsdManager) ctx.getSystemService(Context.NSD_SERVICE);
    }


    // Discovery mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws DiscoveryException {
        if (getState().isRunning()) return;

        if (getState().isStartup()) return;

        if (ctx == null) {
            Log.e(LOG_TAG, "Can't init DiscoverAndroid because context not set to DiscoveryAndroid.");
            throw new DiscoveryException("Can't init DiscoverAndroid because context not set to DiscoveryAndroid.");
        }

        emitOnStarting(this, log);

        nsdManager.discoverServices(getServiceType(), NsdManager.PROTOCOL_DNS_SD, discoveryListener);

        Log.i(LOG_TAG, String.format("DiscoverAndroid started for '%s' service type", getServiceType()));
        emitOnStart(this, log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (getState().isStopped()) return;

        if (getState().isShutdown()) return;

        emitOnStopping(this, log);

        nsdManager.stopServiceDiscovery(discoveryListener);

        Log.i(LOG_TAG, String.format("DiscoverAndroid stopped for '%s' service type", getServiceType()));
        emitOnStop(this, log);
    }

}
