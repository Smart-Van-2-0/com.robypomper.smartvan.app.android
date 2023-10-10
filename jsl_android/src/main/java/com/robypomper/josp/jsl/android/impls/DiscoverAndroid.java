package com.robypomper.josp.jsl.android.impls;

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
 * JmDNS discover.
 */
public class DiscoverAndroid extends DiscoverAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DiscoverAndroid.class);

    private static Context ctx;
    private static NsdManager nsdManager;
    private final NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {

        // Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d("J_Android_Discovery", "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            Log.d("J_Android_Discovery", "Service discovery success" + service);
            if (!service.getServiceType().startsWith(getServiceType())) {
                Log.d("J_Android_Discovery", String.format("Unknown Service Type '%s' with '%s' name instead %s", service.getServiceType(), service.getServiceName(), getServiceType()));
                return;
            }
            nsdManager.resolveService(service, resolveListener);
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            Log.d("J_Android_Discovery", "service lost: " + service);
            deregisterService(new DiscoveryService(service.getServiceName(), service.getServiceType(), null, null, null, null, null));
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i("J_Android_Discovery", "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e("J_Android_Discovery", "Discovery failed: Error code:" + errorCode);
            nsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e("J_Android_Discovery", "Discovery failed: Error code:" + errorCode);
            nsdManager.stopServiceDiscovery(this);
        }
    };
    private final NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e("J_Android_Discovery", "Resolve failed: " + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d("J_Android_Discovery", "Resolve Succeeded. " + serviceInfo);

            serviceInfo.getHost();
            String attributesAsString = ""; // not available before the Android API level 21
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                List<String> attributesStrings = new ArrayList<>();
                Map<String, byte[]> attributes = serviceInfo.getAttributes();
                for (String key : attributes.keySet())
                    attributesStrings.add(
                            String.format("%s=%s",
                                    key,
                                    attributes.get(key) != null ?
                                            new String(attributes.get(key), StandardCharsets.UTF_8)
                                            :""));
                attributesAsString = String.join(",", attributesStrings);
            }

            registerService(new DiscoveryService(
                    serviceInfo.getServiceName(),
                    serviceInfo.getServiceType(),
                    "NsdManager",
                    "IPv4",
                    serviceInfo.getHost(),
                    serviceInfo.getPort(),
                    attributesAsString));
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
        if (getState().isRunning())
            return;

        if (getState().isStartup())
            return;

        if (ctx == null)
            throw new DiscoveryException("Context not set to DiscoveryAndroid.");

        emitOnStarting(this, log);

        nsdManager.discoverServices(getServiceType(), NsdManager.PROTOCOL_DNS_SD, discoveryListener);

        emitOnStart(this, log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (getState().isStopped())
            return;

        if (getState().isShutdown())
            return;

        emitOnStopping(this, log);

        nsdManager.stopServiceDiscovery(discoveryListener);

        emitOnStop(this, log);
    }

}
