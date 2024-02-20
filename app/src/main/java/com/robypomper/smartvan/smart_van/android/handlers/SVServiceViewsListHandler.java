package com.robypomper.smartvan.smart_van.android.handlers;

import android.view.ViewGroup;

import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.smartvan.smart_van.android.components.SVBaseActuatorServiceView;
import com.robypomper.smartvan.smart_van.android.components.SVBaseControllerServiceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * Class to handle a list of SV Service views.
 * <p>
 * Main goal for this class is to provide an easy way to sort and manage a list
 * of SV Service views.
 * <p>
 * A client class can initialize this class, specifying a main container: where
 * all the service's view are placed. Then it can add or remove SV Service views
 * and sort them by name, value, last update, last change, component name or
 * component path.
 * <p>
 * Handler's default layout NONE
 *
 * @noinspection unused
 */
public class SVServiceViewsListHandler {

    private final ViewGroup mainContainer;
    private final Map<JSLComponent, SVBaseControllerServiceView> views = new HashMap<>();
    private final Map<JSLComponent, ViewGroup> containers = new HashMap<>();
    private boolean keepUpdated = true;
    private long updateIntervalMs = 500;
    private Timer updateTimer;
    private boolean isReverse = false;
    private boolean isNameSorted = false;
    private boolean isValueSorted = false;
    private boolean isLastUpdateSorted = false;
    private boolean isLastChangeSorted = false;
    private boolean isCompNameSorted = false;
    private boolean isCompPathSorted = false;


    // Constructors

    public SVServiceViewsListHandler(ViewGroup mainContainer) {
        this.mainContainer = mainContainer;

        // force start/stop timer
        setKeepUpdated(isKeepUpdated());
    }


    // Add and remove components

    public void addComponent(JSLComponent component, SVBaseControllerServiceView view) {
        addComponent(component, view, null);
    }

    public void addComponent(JSLComponent component, SVBaseControllerServiceView view, ViewGroup container) {
        addComponent(component, view, container, -1);  // added as the last view of the view group
    }

    public void addComponent(JSLComponent component, SVBaseControllerServiceView view, ViewGroup container, int position) {
        if (container == null) container = (ViewGroup) view;

        // Add the component's view
        views.put(component, view);

        // Add the component's container
        containers.put(component, container);

        // Add the component's container to the main container
        final ViewGroup finalContainer = container;
        mainContainer.post(new Runnable() {
            @Override
            public void run() {
                mainContainer.addView(finalContainer, position);
            }
        });
    }

    public void removeComponent(JSLComponent component) {
        // Remove the component's view
        views.remove(component);

        // Remove the component's container
        ViewGroup container = containers.remove(component);

        // Remove the component's container from the main container
        if (container != null) mainContainer.removeView(container);
    }

    public void removeAllComponents() {
        List<JSLComponent> components = new ArrayList<>(views.keySet());
        for (JSLComponent component : components)
            removeComponent(component);
    }


    // Getters and Setters

    public boolean isKeepUpdated() {
        return keepUpdated;
    }

    public void setKeepUpdated(boolean keepUpdated) {
        this.keepUpdated = keepUpdated;
        if (keepUpdated) startUpdateTimer();
        else stopUpdateTimer();
    }

    public long getUpdateIntervalMs() {
        return updateIntervalMs;
    }

    public void setUpdateIntervalMs(long updateIntervalMs) {
        this.updateIntervalMs = updateIntervalMs;
        if (keepUpdated) {
            stopUpdateTimer();
            startUpdateTimer();
        }
    }

    public List<JSLComponent> getComponents() {
        return new ArrayList<>(views.keySet());
    }

    public boolean isSorted() {
        return isNameSorted
                || isValueSorted
                || isLastUpdateSorted
                || isLastChangeSorted
                || isCompNameSorted
                || isCompPathSorted;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public boolean isNameSorted() {
        return isNameSorted;
    }

    public boolean isValueSorted() {
        return isValueSorted;
    }

    public boolean isLastUpdateSorted() {
        return isLastUpdateSorted;
    }

    public boolean isLastChangeSorted() {
        return isLastChangeSorted;
    }

    public boolean isCompNameSorted() {
        return isCompNameSorted;
    }

    public boolean isCompPathSorted() {
        return isCompPathSorted;
    }


    // Sort methods

    public void reSort() {
        if (isNameSorted) sortByName(isReverse);
        else if (isValueSorted) sortByValue(isReverse);
        else if (isLastUpdateSorted) sortByLastUpdate(isReverse);
        else if (isLastChangeSorted) sortByLastChange(isReverse);
        else if (isCompNameSorted) sortByComponentName(isReverse);
        else if (isCompPathSorted) sortByComponentPath(isReverse);
        else sortByName(isReverse); // default sort
    }

    public void sortByName(boolean reverse) {
        List<JSLComponent> orderedComps = getComponents();
        Collections.sort(orderedComps, (c1, c2) -> getName(c1).compareTo(getName(c2)));
        if (reverse) Collections.reverse(orderedComps);
        sortViews(orderedComps);

        resetSortFlags();
        isNameSorted = true;
        isReverse = reverse;
    }

    public void sortByValue(boolean reverse) {
        List<JSLComponent> orderedComps = getComponents();
        Collections.sort(orderedComps, new Comparator<JSLComponent>() {
            @Override
            public int compare(JSLComponent o1, JSLComponent o2) {
                int diff = getValue(o1).compareTo(getValue(o2));
                if (diff != 0) return diff;
                return getName(o1).compareTo(getName(o2));
            }
        });
        if (reverse) Collections.reverse(orderedComps);
        sortViews(orderedComps);

        resetSortFlags();
        isValueSorted = true;
        isReverse = reverse;
    }

    public void sortByLastUpdate(boolean reverse) {
        List<JSLComponent> orderedComps = getComponents();
        Collections.sort(orderedComps, new Comparator<JSLComponent>() {
            @Override
            public int compare(JSLComponent o1, JSLComponent o2) {
                int diff = getLastUpdate(o1).compareTo(getLastUpdate(o2));
                diff = diff * -1;   // most recent first
                if (diff != 0) return diff;
                return getName(o1).compareTo(getName(o2));
            }
        });
        if (reverse) Collections.reverse(orderedComps);
        sortViews(orderedComps);

        resetSortFlags();
        isLastUpdateSorted = true;
        isReverse = reverse;
    }

    public void sortByLastChange(boolean reverse) {
        List<JSLComponent> orderedComps = getComponents();
        Collections.sort(orderedComps, new Comparator<JSLComponent>() {
            @Override
            public int compare(JSLComponent o1, JSLComponent o2) {
                int diff = getLastChange(o1).compareTo(getLastChange(o2));
                diff = diff * -1;   // most recent first
                if (diff != 0) return diff;
                return getName(o1).compareTo(getName(o2));
            }
        });
        if (reverse) Collections.reverse(orderedComps);
        sortViews(orderedComps);

        resetSortFlags();
        isLastChangeSorted = true;
        isReverse = reverse;
    }

    public void sortByComponentName(boolean reverse) {
        List<JSLComponent> orderedComps = getComponents();
        Collections.sort(orderedComps, new Comparator<JSLComponent>() {
            @Override
            public int compare(JSLComponent o1, JSLComponent o2) {
                return getName(o1).compareTo(getName(o2));
            }
        });
        if (reverse) Collections.reverse(orderedComps);
        sortViews(orderedComps);

        resetSortFlags();
        isCompNameSorted = true;
        isReverse = reverse;
    }

    public void sortByComponentPath(boolean reverse) {
        List<JSLComponent> orderedComps = getComponents();
        Collections.sort(orderedComps, new Comparator<JSLComponent>() {
            @Override
            public int compare(JSLComponent o1, JSLComponent o2) {
                return o1.getPath().getString().compareTo(o2.getPath().getString());
            }
        });
        if (reverse) Collections.reverse(orderedComps);
        sortViews(orderedComps);

        resetSortFlags();
        isCompPathSorted = true;
        isReverse = reverse;
    }

    private void resetSortFlags() {
        isReverse = false;
        isNameSorted = false;
        isValueSorted = false;
        isLastUpdateSorted = false;
        isLastChangeSorted = false;
        isCompNameSorted = false;
        isCompPathSorted = false;
    }

    private void sortViews(List<JSLComponent> orderedComps) {
        mainContainer.post(new Runnable() {
            @Override
            public void run() {
                for (JSLComponent comp : orderedComps) {
                    SVBaseControllerServiceView view = views.get(comp);
                    ViewGroup container = containers.get(comp);
                    mainContainer.removeView(container);
                    mainContainer.addView(container);
                }
            }
        });
    }


    // Extract info from components and views

    private String getName(JSLComponent comp) {
        SVBaseControllerServiceView v = views.get(comp);
        assert v != null;

        return v.getSVName();
    }

    private String getValue(JSLComponent comp) {
        SVBaseControllerServiceView v = views.get(comp);
        assert v != null;

        return v.getStateTxt();
    }

    private Date getLastUpdate(JSLComponent comp) {
        SVBaseControllerServiceView v = views.get(comp);
        assert v != null;

        return v.getLastUpdate() != null ? v.getLastUpdate() : new Date(0);
    }

    private Date getLastChange(JSLComponent comp) {
        SVBaseControllerServiceView v = views.get(comp);
        assert v != null;

        assert v instanceof SVBaseActuatorServiceView;
        return ((SVBaseActuatorServiceView) v).getLastChange() != null ? ((SVBaseActuatorServiceView) v).getLastChange() : new Date(0);
    }


    // Update timer

    private void startUpdateTimer() {
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                reSort();
            }
        }, updateIntervalMs, updateIntervalMs);
    }

    private void stopUpdateTimer() {
        updateTimer.cancel();
        updateTimer.purge();
    }

    public SVBaseControllerServiceView getViewByComponent(JSLComponent comp) {
        return views.get(comp);
    }

    public ViewGroup getContainerByComponent(JSLComponent comp) {
        return containers.get(comp);
    }

    public JSLComponent getComponentByView(SVBaseControllerServiceView view) {
        for (Map.Entry<JSLComponent, SVBaseControllerServiceView> entry : views.entrySet()) {
            if (entry.getValue().equals(view)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public JSLComponent getComponentByContainer(ViewGroup view) {
        for (Map.Entry<JSLComponent, ViewGroup> entry : containers.entrySet()) {
            if (entry.getValue().equals(view)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
