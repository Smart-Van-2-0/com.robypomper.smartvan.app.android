package com.robypomper.smartvan.smart_van.android.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.DrawableRes;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.robypomper.josp.jsl.android.activities.BaseRemoteObjectActivity;
import com.robypomper.josp.jsl.android.components.JSLBaseComponentView;
import com.robypomper.josp.jsl.android.handlers.view.JSLBaseActionViewHandler;
import com.robypomper.josp.jsl.android.handlers.view.JSLBaseStateViewHandler;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.smartvan.smart_van.android.R;
import com.robypomper.smartvan.smart_van.android.commons.SVDefinitions;
import com.robypomper.smartvan.smart_van.android.commons.SVServiceIcons;
import com.robypomper.smartvan.smart_van.android.commons.SVSpec;
import com.robypomper.smartvan.smart_van.android.commons.SVSpecs;
import com.robypomper.smartvan.smart_van.android.components.SVBaseActuatorServiceView;
import com.robypomper.smartvan.smart_van.android.components.SVBaseControllerServiceView;
import com.robypomper.smartvan.smart_van.android.components.SVBinaryControllerView;
import com.robypomper.smartvan.smart_van.android.components.SVDimmerActuatorView;
import com.robypomper.smartvan.smart_van.android.components.SVPercentControllerView;
import com.robypomper.smartvan.smart_van.android.components.SVServiceBottomSheet;
import com.robypomper.smartvan.smart_van.android.components.SVSwitchActuatorView;
import com.robypomper.smartvan.smart_van.android.databinding.ActivitySvservicesBinding;
import com.robypomper.smartvan.smart_van.android.handlers.SVServiceViewsListHandler;
import com.robypomper.smartvan.smart_van.android.storage.SVPreferencesServices;
import com.robypomper.smartvan.smart_van.android.storage.SVStorageSingleton;


/** @noinspection unused*/
public class SVServicesActivity
        extends BaseRemoteObjectActivity
        implements SVServiceBottomSheet.OnDetachListener {

    // Constants

    private static final String LOG_TAG = "JSLA.Actvt.Services";
    public final static String PARAM_OBJ_ID = SVDefinitions.PARAM_ACTIVITY_SVMAIN_OBJID;
    /**
     * TODO document component origins
     */
    public final static SVSpec CONT_CONT_BIN = SVSpecs.SVBox.Services.Controllers.Binary;
    /**
     * TODO document component origins
     */
    public final static SVSpec CONT_CONT_PERC = SVSpecs.SVBox.Services.Controllers.Percentage;
    /**
     * TODO document component origins
     */
    public final static SVSpec CONT_ACT_SWITCH_LOW = SVSpecs.SVBox.Services.Actuators.SwitchLow;
    /**
     * TODO document component origins
     */
    public final static SVSpec CONT_ACT_SWITCH_HIGH = SVSpecs.SVBox.Services.Actuators.SwitchHigh;
    /**
     * TODO document component origins
     */
    public final static SVSpec CONT_ACT_DIMM_LOW = SVSpecs.SVBox.Services.Actuators.DimmerLow;
    /**
     * TODO document component origins
     */
    public final static SVSpec CONT_ACT_DIMM_HIGH = SVSpecs.SVBox.Services.Actuators.DimmerHigh;
    private static final int LAY_BIN_CTRL_CARD = R.layout.lay_service_card_binary_controller;
    private static final int LAY_PERC_CTRL_CARD = R.layout.lay_service_card_percent_controller;
    private static final int LAY_SWITCH_ACT_CARD = R.layout.lay_service_card_switch_actuator;
    private static final int LAY_DIMMER_ACT_CARD = R.layout.lay_service_card_dimmer_actuator;
    private static final int MENU_FILTER_CONTROLLERS = R.menu.activity_svservices_controllers_filter_menu;
    private static final int MENU_FILTER_ACTUATORS = R.menu.activity_svservices_actuators_filter_menu;
    private static final int MENU_SORT = R.menu.activity_svservices_sort_menu;


    // Internal variables

    private SVServiceViewsListHandler binaryViewsHandlers;
    private SVServiceViewsListHandler percentViewsHandlers;
    private SVServiceViewsListHandler switchViewsHandlers;
    private SVServiceViewsListHandler dimmerViewsHandlers;
    private boolean isOnlyHighVoltageVisible = false;
    private boolean isOnlyLowVoltageVisible = false;


    // UI widgets

    private ActivitySvservicesBinding binding;


    // Android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflate ui
        binding = ActivitySvservicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layBinaryControllers.removeAllViews();
        binding.layPercentControllers.removeAllViews();
        binding.laySwitchActuators.removeAllViews();
        binding.layDimmerActuators.removeAllViews();
        binaryViewsHandlers = new SVServiceViewsListHandler(binding.layBinaryControllers);
        percentViewsHandlers = new SVServiceViewsListHandler(binding.layPercentControllers);
        switchViewsHandlers = new SVServiceViewsListHandler(binding.laySwitchActuators);
        switchViewsHandlers.setKeepUpdated(false);
        dimmerViewsHandlers = new SVServiceViewsListHandler(binding.layDimmerActuators);
        dimmerViewsHandlers.setKeepUpdated(false);

        // set up filters and sort buttons
        binding.btnControllersFilter.setOnClickListener(onFilterOrSortClickListener);
        binding.btnControllersSort.setOnClickListener(onFilterOrSortClickListener);
        binding.btnActuatorsFilter.setOnClickListener(onFilterOrSortClickListener);
        binding.btnActuatorsSort.setOnClickListener(onFilterOrSortClickListener);

        // set up action bar
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        else if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            Log.w(LOG_TAG, "No ActionBar available for this activity");
    }

    @Override
    protected void onResume() {
        // During the super.onResume() execution, it check if the remote object
        // is ready, and if it is ready, it calls the onRemoteObjectReady()
        // method. So, the registerRemoteObject() method is called by the
        // super.onResume() method, only if required.
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Deregister, update UI and remove component from chart
        deregisterRemoteObject();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getParentActivityIntent() == null) {
                Log.w(LOG_TAG, "You have forgotten to specify the parentActivityName in the AndroidManifest!");
                //onBackPressed();
                getOnBackPressedDispatcher().onBackPressed();
            } else
                NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // BaseRemoteObjectActivity

    @Override
    protected void onRemoteObjectReady() {
        // Register, update UI and add component to chart
        new Thread(this::registerRemoteObject).start();
        //registerRemoteObject();
    }

    @Override
    protected void onRemoteObjectNotReady() {
        deregisterRemoteObject();
    }


    // Remote object management

    private void registerRemoteObject() {
        // Cleanup service's views containers
        binaryViewsHandlers.removeAllComponents();
        percentViewsHandlers.removeAllComponents();
        switchViewsHandlers.removeAllComponents();
        dimmerViewsHandlers.removeAllComponents();

        // Generate and add SVBinaryControllerView
        JSLContainer controllersBinaryCont = findContainerComponent(CONT_CONT_BIN.getPath());
        if (controllersBinaryCont != null)
            for (JSLComponent c : controllersBinaryCont.getComponents()) {
                if (!(c instanceof JSLBooleanState)) continue;
                SVBinaryControllerView v = createBinaryControlView((JSLBooleanState) c);
                ViewGroup card = wrapComponentView(v);
                binaryViewsHandlers.addComponent((JSLBooleanState) c, v, card);
            }

        // Generate and add SVPercentControllerView
        JSLContainer controllersPercentCont = findContainerComponent(CONT_CONT_PERC.getPath());
        if (controllersPercentCont != null)
            for (JSLComponent c : controllersPercentCont.getComponents()) {
                if (!(c instanceof JSLRangeState)) continue;
                SVPercentControllerView v = createPercentControlView((JSLRangeState) c);
                ViewGroup card = wrapComponentView(v);
                percentViewsHandlers.addComponent((JSLRangeState) c, v, card);
            }

        // Generate and add SVSwitchActuatorView
        JSLContainer actuatorsBinaryLowCont = findContainerComponent(CONT_ACT_SWITCH_LOW.getPath());
        if (actuatorsBinaryLowCont != null)
            for (JSLComponent c : actuatorsBinaryLowCont.getComponents()) {
                if (!(c instanceof JSLBooleanAction)) continue;
                SVSwitchActuatorView v = createSwitchActuatorView((JSLBooleanAction) c, false);
                ViewGroup card = wrapComponentView(v);
                switchViewsHandlers.addComponent((JSLBooleanAction) c, v, card);
            }
        JSLContainer actuatorsBinaryHighCont = findContainerComponent(CONT_ACT_SWITCH_HIGH.getPath());
        if (actuatorsBinaryHighCont != null)
            for (JSLComponent c : actuatorsBinaryHighCont.getComponents()) {
                if (!(c instanceof JSLBooleanAction)) continue;
                SVSwitchActuatorView v = createSwitchActuatorView((JSLBooleanAction) c, true);
                ViewGroup card = wrapComponentView(v);
                switchViewsHandlers.addComponent((JSLBooleanAction) c, v, card);
            }

        // Generate and add SVDimmerActuatorView
        JSLContainer actuatorsPercentLowCont = findContainerComponent(CONT_ACT_DIMM_LOW.getPath());
        if (actuatorsPercentLowCont != null)
            for (JSLComponent c : actuatorsPercentLowCont.getComponents()) {
                if (!(c instanceof JSLRangeAction)) continue;
                SVDimmerActuatorView v = createDimmerActuatorView((JSLRangeAction) c, false);
                ViewGroup card = wrapComponentView(v);
                dimmerViewsHandlers.addComponent((JSLRangeAction) c, v, card);
            }
        JSLContainer actuatorsPercentHighCont = findContainerComponent(CONT_ACT_DIMM_HIGH.getPath());
        if (actuatorsPercentHighCont != null)
            for (JSLComponent c : actuatorsPercentHighCont.getComponents()) {
                if (!(c instanceof JSLRangeAction)) continue;
                SVDimmerActuatorView v = createDimmerActuatorView((JSLRangeAction) c, true);
                ViewGroup card = wrapComponentView(v);
                dimmerViewsHandlers.addComponent((JSLRangeAction) c, v, card);
            }

        // Order service's views containers
        binaryViewsHandlers.reSort();
        percentViewsHandlers.reSort();
        switchViewsHandlers.reSort();
        dimmerViewsHandlers.reSort();
    }

    private void deregisterRemoteObject() {
        // Cleanup service's views containers
        binaryViewsHandlers.removeAllComponents();
        percentViewsHandlers.removeAllComponents();
        switchViewsHandlers.removeAllComponents();
        dimmerViewsHandlers.removeAllComponents();
    }


    // UI Filter and sort menus

    /**
     * For each menu's button, there is a corresponding menu id.
     *
     * @param menuButtonId The id of the menu's button.
     * @return The id of the menu.
     */
    private static int menuButton2MenuId(int menuButtonId) {
        if (R.id.btnControllersFilter == menuButtonId)
            return MENU_FILTER_CONTROLLERS;
        else if (R.id.btnActuatorsFilter == menuButtonId)
            return MENU_FILTER_ACTUATORS;
        else if (R.id.btnControllersSort == menuButtonId || R.id.btnActuatorsSort == menuButtonId)
            return MENU_SORT;
        else
            throw new RuntimeException("Unknown menu id for menuButton2MenuId: " + menuButtonId);
    }

    /**
     * Refresh the popup menu.
     * <p>
     * This method acts as a dispatcher for the refreshPopupMenu method.
     *
     * @param menuButtonId The id of the menu's button.
     * @param popupMenu    The popup menu to refresh.
     */
    private void refreshPopupMenu(int menuButtonId, PopupMenu popupMenu) {
        if (R.id.btnControllersFilter == menuButtonId)
            refreshControllersFilterPopupMenu(popupMenu);
        else if (R.id.btnActuatorsFilter == menuButtonId)
            refreshActuatorsFilterPopupMenu(popupMenu);
        else if (R.id.btnControllersSort == menuButtonId)
            refreshSortPopupMenu(popupMenu, binaryViewsHandlers);
        else if (R.id.btnActuatorsSort == menuButtonId)
            refreshSortPopupMenu(popupMenu, switchViewsHandlers);
        else
            throw new RuntimeException("Unknown menu id for refreshPopupMenu: " + menuButtonId);
    }

    /**
     * For each menu's button, there is a corresponding listener
     * for the popup menu's items.
     * <p>
     * This method acts as a dispatcher for the menus' listeners.
     *
     * @param menuButtonId The id of the menu's button.
     * @return The listener for the popup menu's items.
     */
    private PopupMenu.OnMenuItemClickListener getOnPopupMenuItemCLickListener(int menuButtonId) {
        if (R.id.btnControllersFilter == menuButtonId)
            return onControllerFilterMenuItemClick;
        else if (R.id.btnControllersSort == menuButtonId)
            return onControllerSortMenuItemClick;
        else if (R.id.btnActuatorsFilter == menuButtonId)
            return onActuatorFilterMenuItemClick;
        else if (R.id.btnActuatorsSort == menuButtonId)
            return onActuatorSortMenuItemClick;
        else
            throw new RuntimeException("Unknown menu id for getOnPopupMenuItemCLickListener: " + menuButtonId);
    }

    private void refreshSortPopupMenu(PopupMenu popupMenu, SVServiceViewsListHandler serviceViewsListHandler) {
        MenuItem byName = popupMenu.getMenu().findItem(R.id.sortByName);
        byName.setChecked(serviceViewsListHandler.isNameSorted());
        byName.setEnabled(!serviceViewsListHandler.isNameSorted());

        MenuItem byLastUpdate = popupMenu.getMenu().findItem(R.id.sortByLastUpdate);
        byLastUpdate.setChecked(serviceViewsListHandler.isLastUpdateSorted());
        byLastUpdate.setEnabled(!serviceViewsListHandler.isLastUpdateSorted());

        if (serviceViewsListHandler == switchViewsHandlers || serviceViewsListHandler == dimmerViewsHandlers) {
            MenuItem byLastChange = popupMenu.getMenu().findItem(R.id.sortByLastChange);
            byLastChange.setChecked(serviceViewsListHandler.isLastChangeSorted());
            byLastChange.setEnabled(!serviceViewsListHandler.isLastChangeSorted());
        } else {
            popupMenu.getMenu().removeItem(R.id.sortByLastChange);
        }
    }

    private void refreshControllersFilterPopupMenu(PopupMenu popupMenu) {
        MenuItem onlyBinaries = popupMenu.getMenu().findItem(R.id.filterOnlyBinaries);
        boolean onlyBinariesEnabled = binding.layBinaryControllers.getVisibility() == View.VISIBLE
                && binding.layPercentControllers.getVisibility() == View.GONE;
        onlyBinaries.setChecked(onlyBinariesEnabled);
        onlyBinaries.setEnabled(!onlyBinariesEnabled);

        MenuItem onlyPercent = popupMenu.getMenu().findItem(R.id.filterOnlyPercent);
        boolean onlyPercentEnabled = binding.layBinaryControllers.getVisibility() == View.GONE
                && binding.layPercentControllers.getVisibility() == View.VISIBLE;
        onlyPercent.setChecked(onlyPercentEnabled);
        onlyPercent.setEnabled(!onlyPercentEnabled);

        MenuItem reset = popupMenu.getMenu().findItem(R.id.filterReset);
        boolean resetEnabled = binding.layBinaryControllers.getVisibility() == View.VISIBLE
                && binding.layPercentControllers.getVisibility() == View.VISIBLE;
        reset.setChecked(resetEnabled);
        reset.setEnabled(!resetEnabled);
    }

    private void refreshActuatorsFilterPopupMenu(PopupMenu popupMenu) {
        MenuItem onlySwitches = popupMenu.getMenu().findItem(R.id.filterOnlySwitches);
        boolean onlySwitchesEnabled = binding.laySwitchActuators.getVisibility() == View.VISIBLE
                && binding.layDimmerActuators.getVisibility() == View.GONE
                && !this.isOnlyHighVoltageVisible && !this.isOnlyLowVoltageVisible;
        onlySwitches.setChecked(onlySwitchesEnabled);
        onlySwitches.setEnabled(!onlySwitchesEnabled);

        MenuItem onlyDimmers = popupMenu.getMenu().findItem(R.id.filterOnlyDimmers);
        boolean onlyDimmersEnabled = binding.laySwitchActuators.getVisibility() == View.GONE
                && binding.layDimmerActuators.getVisibility() == View.VISIBLE
                && !this.isOnlyHighVoltageVisible && !this.isOnlyLowVoltageVisible;
        onlyDimmers.setChecked(onlyDimmersEnabled);
        onlyDimmers.setEnabled(!onlyDimmersEnabled);

        MenuItem onlyHigh = popupMenu.getMenu().findItem(R.id.filterOnlyHigh);
        boolean onlyHighEnabled = binding.laySwitchActuators.getVisibility() == View.VISIBLE
                && binding.layDimmerActuators.getVisibility() == View.VISIBLE
                && this.isOnlyHighVoltageVisible && !this.isOnlyLowVoltageVisible;
        onlyHigh.setChecked(onlyHighEnabled);
        onlyHigh.setEnabled(!onlyHighEnabled);

        MenuItem onlyLow = popupMenu.getMenu().findItem(R.id.filterOnlyLow);
        boolean onlyLowEnabled = binding.laySwitchActuators.getVisibility() == View.VISIBLE
                && binding.layDimmerActuators.getVisibility() == View.VISIBLE
                && !this.isOnlyHighVoltageVisible && this.isOnlyLowVoltageVisible;
        onlyLow.setChecked(onlyLowEnabled);
        onlyLow.setEnabled(!onlyLowEnabled);

        MenuItem reset = popupMenu.getMenu().findItem(R.id.filterReset);
        boolean resetEnabled = binding.laySwitchActuators.getVisibility() == View.VISIBLE
                && binding.layDimmerActuators.getVisibility() == View.VISIBLE
                && !this.isOnlyHighVoltageVisible && !this.isOnlyLowVoltageVisible;
        reset.setChecked(resetEnabled);
        reset.setEnabled(!resetEnabled);
    }

    private void hideBinaryControllers(boolean hide) {
        binding.layBinaryControllers.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    private void hidePercentControllers(boolean hide) {
        binding.layPercentControllers.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    private void hideSwitchActuators(boolean hide) {
        binding.laySwitchActuators.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    private void hideDimmerActuators(boolean hide) {
        binding.layDimmerActuators.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    private void enableOnlyHighVoltageVisible() {
        this.isOnlyHighVoltageVisible = true;
        for (int i = 0; i < binding.laySwitchActuators.getChildCount(); i++) {
            ViewGroup card = (ViewGroup) binding.laySwitchActuators.getChildAt(i);
            JSLComponent comp = switchViewsHandlers.getComponentByContainer(card);
            SVBaseControllerServiceView c = switchViewsHandlers.getViewByComponent(comp);
            boolean isHighVoltageFromContainer = c instanceof SVBaseActuatorServiceView && c.getCardHeaderHandler().isHighVoltage();
            card.setVisibility(isHighVoltageFromContainer ? View.VISIBLE : View.GONE);
        }
        for (int i = 0; i < binding.layDimmerActuators.getChildCount(); i++) {
            ViewGroup card = (ViewGroup) binding.layDimmerActuators.getChildAt(i);
            JSLComponent comp = dimmerViewsHandlers.getComponentByContainer(card);
            SVBaseControllerServiceView c = dimmerViewsHandlers.getViewByComponent(comp);
            boolean isHighVoltageFromContainer = c instanceof SVBaseActuatorServiceView && c.getCardHeaderHandler().isHighVoltage();
            card.setVisibility(isHighVoltageFromContainer ? View.VISIBLE : View.GONE);
        }
    }

    private void resetOnlyHighVoltageVisible() {
        this.isOnlyHighVoltageVisible = false;
        for (int i = 0; i < binding.laySwitchActuators.getChildCount(); i++)
            binding.laySwitchActuators.getChildAt(i).setVisibility(View.VISIBLE);
        for (int i = 0; i < binding.layDimmerActuators.getChildCount(); i++)
            binding.laySwitchActuators.getChildAt(i).setVisibility(View.VISIBLE);
    }

    private void enableOnlyLowVoltageVisible() {
        this.isOnlyLowVoltageVisible = true;
        for (int i = 0; i < binding.laySwitchActuators.getChildCount(); i++) {
            ViewGroup card = (ViewGroup) binding.laySwitchActuators.getChildAt(i);
            JSLComponent comp = switchViewsHandlers.getComponentByContainer(card);
            SVBaseControllerServiceView c = switchViewsHandlers.getViewByComponent(comp);
            boolean isHighVoltageFromContainer = c instanceof SVBaseActuatorServiceView && c.getCardHeaderHandler().isHighVoltage();
            card.setVisibility(!isHighVoltageFromContainer ? View.VISIBLE : View.GONE);
        }
        for (int i = 0; i < binding.layDimmerActuators.getChildCount(); i++) {
            ViewGroup card = (ViewGroup) binding.layDimmerActuators.getChildAt(i);
            JSLComponent comp = dimmerViewsHandlers.getComponentByContainer(card);
            SVBaseControllerServiceView c = dimmerViewsHandlers.getViewByComponent(comp);
            boolean isHighVoltageFromContainer = c instanceof SVBaseActuatorServiceView && c.getCardHeaderHandler().isHighVoltage();
            card.setVisibility(!isHighVoltageFromContainer ? View.VISIBLE : View.GONE);
        }
    }

    private void resetOnlyLowVoltageVisible() {
        this.isOnlyLowVoltageVisible = false;
        for (int i = 0; i < binding.laySwitchActuators.getChildCount(); i++)
            binding.laySwitchActuators.getChildAt(i).setVisibility(View.VISIBLE);
        for (int i = 0; i < binding.layDimmerActuators.getChildCount(); i++)
            binding.laySwitchActuators.getChildAt(i).setVisibility(View.VISIBLE);
    }

    private final View.OnClickListener onFilterOrSortClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int menuToShow = menuButton2MenuId(view.getId());
            PopupMenu popupMenu = new PopupMenu(getBaseContext(), view);
            popupMenu.getMenuInflater().inflate(menuToShow, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(getOnPopupMenuItemCLickListener(view.getId()));
            refreshPopupMenu(view.getId(), popupMenu);
            popupMenu.show();
        }
    };

    private final PopupMenu.OnMenuItemClickListener onControllerFilterMenuItemClick = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (R.id.filterOnlyBinaries == menuItem.getItemId()) {
                hideBinaryControllers(false);
                hidePercentControllers(true);
                return true;
            }

            if (R.id.filterOnlyPercent == menuItem.getItemId()) {
                hideBinaryControllers(true);
                hidePercentControllers(false);
                return true;
            }

            if (R.id.filterReset == menuItem.getItemId()) {
                hideBinaryControllers(false);
                hidePercentControllers(false);
                return true;
            }

            throw new RuntimeException("Unknown or invalid menu item id for onControllerFilterMenuItemClick: " + menuItem.getItemId());
        }
    };

    private final PopupMenu.OnMenuItemClickListener onControllerSortMenuItemClick = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (R.id.sortByName == menuItem.getItemId()) {
                binaryViewsHandlers.sortByName(binaryViewsHandlers.isNameSorted() && !binaryViewsHandlers.isReverse());
                percentViewsHandlers.sortByName(percentViewsHandlers.isNameSorted() && !percentViewsHandlers.isReverse());
                return true;
            }

            if (R.id.sortByLastUpdate == menuItem.getItemId()) {
                binaryViewsHandlers.sortByLastUpdate(binaryViewsHandlers.isLastUpdateSorted() && !binaryViewsHandlers.isReverse());
                percentViewsHandlers.sortByLastUpdate(percentViewsHandlers.isLastUpdateSorted() && !percentViewsHandlers.isReverse());
                return true;
            }

            throw new RuntimeException("Unknown or invalid menu item id for onControllerSortMenuItemClick: " + menuItem.getItemId());
        }
    };

    private final PopupMenu.OnMenuItemClickListener onActuatorFilterMenuItemClick = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (R.id.filterOnlySwitches == menuItem.getItemId()) {
                hideSwitchActuators(false);
                hideDimmerActuators(true);
                resetOnlyHighVoltageVisible();
                resetOnlyLowVoltageVisible();
                return true;
            }

            if (R.id.filterOnlyDimmers == menuItem.getItemId()) {
                hideSwitchActuators(true);
                hideDimmerActuators(false);
                resetOnlyHighVoltageVisible();
                resetOnlyLowVoltageVisible();
                return true;
            }

            if (R.id.filterOnlyHigh == menuItem.getItemId()) {
                hideSwitchActuators(false);
                hideDimmerActuators(false);
                resetOnlyLowVoltageVisible();
                enableOnlyHighVoltageVisible();
                return true;
            }

            if (R.id.filterOnlyLow == menuItem.getItemId()) {
                hideSwitchActuators(false);
                hideDimmerActuators(false);
                resetOnlyHighVoltageVisible();
                enableOnlyLowVoltageVisible();
                return true;
            }

            if (R.id.filterReset == menuItem.getItemId()) {
                hideSwitchActuators(false);
                hideDimmerActuators(false);
                resetOnlyHighVoltageVisible();
                resetOnlyLowVoltageVisible();
                return true;
            }

            throw new RuntimeException("Unknown or invalid menu item id for onActuatorFilterMenuItemClick: " + menuItem.getItemId());
        }
    };

    private final PopupMenu.OnMenuItemClickListener onActuatorSortMenuItemClick = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (R.id.sortByName == menuItem.getItemId()) {
                switchViewsHandlers.sortByName(switchViewsHandlers.isNameSorted() && !switchViewsHandlers.isReverse());
                dimmerViewsHandlers.sortByName(dimmerViewsHandlers.isNameSorted() && !dimmerViewsHandlers.isReverse());
                return true;
            }

            if (R.id.sortByLastUpdate == menuItem.getItemId()) {
                switchViewsHandlers.sortByLastUpdate(switchViewsHandlers.isLastUpdateSorted() && !switchViewsHandlers.isReverse());
                dimmerViewsHandlers.sortByLastUpdate(dimmerViewsHandlers.isLastUpdateSorted() && !dimmerViewsHandlers.isReverse());
                return true;
            }

            if (R.id.sortByLastChange == menuItem.getItemId()) {
                switchViewsHandlers.sortByLastChange(switchViewsHandlers.isLastChangeSorted() && !switchViewsHandlers.isReverse());
                dimmerViewsHandlers.sortByLastChange(dimmerViewsHandlers.isLastChangeSorted() && !dimmerViewsHandlers.isReverse());
                return true;
            }

            throw new RuntimeException("Unknown or invalid menu item id for onActuatorSortMenuItemClick: " + menuItem.getItemId());
        }
    };


    // UI Component Bottom Sheet

    private void showComponentBottomSheet(Context context, JSLComponent originComp) {
        SVBaseControllerServiceView activityCompView = findViewFromComponent(originComp);

        SVServiceBottomSheet frmComponentBottomSheet = new SVServiceBottomSheet(context, originComp);
        SVBaseControllerServiceView fragmentCompView = frmComponentBottomSheet.getCompView();

        // setup card header + icon, from activity to bottom sheet
        // copy card header + icon, from activity to bottom sheet
        fragmentCompView.getCardHeaderHandler().setSVName(getServiceNameFromPrefs(originComp));
        fragmentCompView.getCardHeaderHandler().setSVType(component2ServiceType(originComp));
        fragmentCompView.getCardHeaderHandler().setHighVoltage(activityCompView.getCardHeaderHandler().isHighVoltage());
        fragmentCompView.getIconHandler().setIcon(getServiceIconFromPrefs(originComp));

        // copy state, from activity to bottom sheet
        JSLBaseStateViewHandler activityStateHandler = activityCompView.getStateHandler();
        JSLBaseStateViewHandler fragmentStateHandler = fragmentCompView.getStateHandler();
        fragmentStateHandler.getHandler().copy(activityStateHandler.getHandler());

        if (fragmentCompView instanceof SVBaseActuatorServiceView) {
            // copy activity, from activity to bottom sheet
            JSLBaseActionViewHandler activityActionHandler = ((SVBaseActuatorServiceView) activityCompView).getActionHandler();
            JSLBaseActionViewHandler fragmentActionHandler = ((SVBaseActuatorServiceView) fragmentCompView).getActionHandler();
            fragmentActionHandler.getHandler().copy(activityActionHandler.getHandler());
        }

        // Get the fragment manager
        if (!(context instanceof FragmentActivity))
            throw new IllegalStateException("Context must be an Activity");
        FragmentManager fragmentMngr = ((FragmentActivity) context).getSupportFragmentManager();
        frmComponentBottomSheet.setOnDetachListener(this);

        // Show the bottom sheet
        frmComponentBottomSheet.show(fragmentMngr, SVServiceBottomSheet.TAG);

    }

    @Override
    public void onDetach(SVServiceBottomSheet frmComponentBottomSheet) {
        // update activity's view from bottom sheet's view
        SVBaseControllerServiceView activityCompView = findViewFromComponent(frmComponentBottomSheet.getService());
        SVBaseControllerServiceView fragmentCompView = frmComponentBottomSheet.getCompView();
        JSLComponent originComp = fragmentCompView.getStateHandler().getComponent();

        // copy card header + icon, from bottom sheet to activity
        activityCompView.getCardHeaderHandler().setSVName(getServiceNameFromPrefs(originComp));
        //activityCompView.getCardHeaderHandler().setSVType(getServiceTypeFromPrefs(originComp));
        //activityCompView.getCardHeaderHandler().setHighVoltage(fragmentCompView.getCardHeaderHandler().isHighVoltage());
        activityCompView.getIconHandler().setIcon(getServiceIconFromPrefs(originComp));

        // copy state, from bottom sheet to activity
        JSLBaseStateViewHandler activityStateHandler = activityCompView.getStateHandler();
        JSLBaseStateViewHandler fragmentStateHandler = fragmentCompView.getStateHandler();
        activityStateHandler.getHandler().copy(fragmentStateHandler.getHandler());

        if (fragmentCompView instanceof SVBaseActuatorServiceView) {
            // copy action, from bottom sheet to activity
            JSLBaseActionViewHandler activityActionHandler = ((SVBaseActuatorServiceView) activityCompView).getActionHandler();
            JSLBaseActionViewHandler fragmentActionHandler = ((SVBaseActuatorServiceView) fragmentCompView).getActionHandler();
            activityActionHandler.getHandler().copy(fragmentActionHandler.getHandler());
        }

        // update activity's view
        activityCompView.refresh();
    }

    private final View.OnClickListener onCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup container = (ViewGroup) view;
            JSLComponent comp = findComponentFromContainerView(container);
            showComponentBottomSheet(SVServicesActivity.this, comp);
        }
    };


    // UI Service cards creation

    private SVBinaryControllerView createBinaryControlView(JSLBooleanState c) {
        SVBinaryControllerView view = new SVBinaryControllerView(this, c, LAY_BIN_CTRL_CARD);
        view.getCardHeaderHandler().setSVName(getServiceNameFromPrefs(c));
        view.getCardHeaderHandler().setSVType(component2ServiceType(c));
        view.getIconHandler().setIcon(getServiceIconFromPrefs(c));
        return view;
    }

    private SVPercentControllerView createPercentControlView(JSLRangeState c) {
        SVPercentControllerView view = new SVPercentControllerView(this, c, LAY_PERC_CTRL_CARD);
        view.getCardHeaderHandler().setSVName(getServiceNameFromPrefs(c));
        view.getCardHeaderHandler().setSVType(component2ServiceType(c));
        view.getIconHandler().setIcon(getServiceIconFromPrefs(c));
        return view;
    }

    private SVSwitchActuatorView createSwitchActuatorView(JSLBooleanAction c, boolean isHighVoltage) {
        SVSwitchActuatorView view = new SVSwitchActuatorView(this, c, LAY_SWITCH_ACT_CARD);
        view.getCardHeaderHandler().setHighVoltage(isHighVoltage);
        view.getCardHeaderHandler().setSVName(getServiceNameFromPrefs(c));
        view.getCardHeaderHandler().setSVType(component2ServiceType(c));
        view.getIconHandler().setIcon(getServiceIconFromPrefs(c));
        return view;
    }

    private SVDimmerActuatorView createDimmerActuatorView(JSLRangeAction c, boolean isHighVoltage) {
        SVDimmerActuatorView view = new SVDimmerActuatorView(this, c, LAY_DIMMER_ACT_CARD);
        view.getCardHeaderHandler().setHighVoltage(isHighVoltage);
        view.getCardHeaderHandler().setSVName(getServiceNameFromPrefs(c));
        view.getCardHeaderHandler().setSVType(component2ServiceType(c));
        view.getIconHandler().setIcon(getServiceIconFromPrefs(c));
        return view;
    }

    private ViewGroup wrapComponentView(View view) {
        // From dp into pixel
        int pixelMargins = dp2px(getApplicationContext(), 8);
        int pixelPadding = dp2px(getApplicationContext(), 16);
        int pixelRadius = dp2px(getApplicationContext(), 20);

        // setup view
        ViewGroup.LayoutParams viewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(viewParams);

        // setup CardView
        LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layParams.setMargins(pixelMargins, pixelMargins, pixelMargins, pixelMargins);
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(layParams);
        cardView.setContentPadding(pixelPadding, pixelPadding, pixelPadding, pixelPadding);
        //cardView.setRadius(pixelRadius);
        cardView.setOnClickListener(onCardClickListener);

        // setup hierarchy
        if (view instanceof JSLBaseComponentView)
            ((JSLBaseComponentView) view).getRemObjCommHandler().setContainer(cardView);

        cardView.addView(view);
        return cardView;
    }


    // Overridden service's info

    private String getServiceNameFromPrefs(JSLComponent comp) {
        return getServiceNameFromPrefs(SVStorageSingleton.getInstance().getCurrentPreferencesServices(), comp);
    }

    private @DrawableRes int getServiceIconFromPrefs(JSLComponent comp) {
        return getServiceIconFromPrefs(SVStorageSingleton.getInstance().getCurrentPreferencesServices(), comp);
    }

    public static String component2ServiceType(JSLComponent comp) {
        String compPath = comp.getPath().getString();
        compPath = compPath.replace(SVSpecs.SEPARATOR, SVSpecs.SEPARATOR_FORMATTED);
        if (compPath.contains(CONT_CONT_BIN.getPath()))
            return "Binary Controller";
        if (compPath.contains(CONT_CONT_PERC.getPath()))
            return "Percent Controller";
        if (compPath.contains(CONT_ACT_SWITCH_LOW.getPath())
                || compPath.contains(CONT_ACT_SWITCH_HIGH.getPath()))
            return "Switch Actuator";
        if (compPath.contains(CONT_ACT_DIMM_LOW.getPath())
                || compPath.contains(CONT_ACT_DIMM_HIGH.getPath()))
            return "Dimmer Actuator";
        return "Unknown";
    }

    public static String getServiceNameFromPrefs(SVPreferencesServices preferencesServices, JSLComponent comp) {
        String compPath = comp.getPath().getString();
        String savedName = preferencesServices.getName(compPath);
        return savedName != null ? savedName : comp.getName();
    }

    public static @DrawableRes int getServiceIconFromPrefs(SVPreferencesServices preferencesServices, JSLComponent comp) {
        String compPath = comp.getPath().getString();
        String iconName = preferencesServices.getIconName(compPath);
        return SVServiceIcons.iconString2Res(iconName);
    }


    // Utils

    private static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    // Given a ViewGroup, looks for it into the binary, percent, switch and
    // dimmer views handlers; and returns the corresponding JSLComponent
    private JSLComponent findComponentFromContainerView(ViewGroup container) {
        JSLComponent comp = null;
        //noinspection ConstantValue
        if (comp == null) comp = binaryViewsHandlers.getComponentByContainer((ViewGroup) container);
        if (comp == null)
            comp = percentViewsHandlers.getComponentByContainer((ViewGroup) container);
        if (comp == null) comp = switchViewsHandlers.getComponentByContainer((ViewGroup) container);
        if (comp == null) comp = dimmerViewsHandlers.getComponentByContainer((ViewGroup) container);
        if (comp == null)
            throw new RuntimeException("Unknown component for container view " + container);
        return comp;
    }

    private JSLComponent findComponentFromView(SVBaseControllerServiceView compView) {
        JSLComponent comp = null;
        //noinspection ConstantValue
        if (comp == null) comp = binaryViewsHandlers.getComponentByView(compView);
        if (comp == null) comp = percentViewsHandlers.getComponentByView(compView);
        if (comp == null) comp = switchViewsHandlers.getComponentByView(compView);
        if (comp == null) comp = dimmerViewsHandlers.getComponentByView(compView);
        if (comp == null)
            throw new RuntimeException("Unknown component for component view " + compView);
        return comp;
    }

    private SVBaseControllerServiceView findViewFromComponent(JSLComponent comp) {
        SVBaseControllerServiceView view = null;
        //noinspection ConstantValue
        if (view == null) view = binaryViewsHandlers.getViewByComponent(comp);
        if (view == null) view = percentViewsHandlers.getViewByComponent(comp);
        if (view == null) view = switchViewsHandlers.getViewByComponent(comp);
        if (view == null) view = dimmerViewsHandlers.getViewByComponent(comp);
        if (view == null) throw new RuntimeException("Unknown view for component " + comp);
        return view;
    }

    private ViewGroup findContainerFromComponent(JSLComponent comp) {
        ViewGroup container = null;
        //noinspection ConstantValue
        if (container == null) container = binaryViewsHandlers.getContainerByComponent(comp);
        if (container == null) container = percentViewsHandlers.getContainerByComponent(comp);
        if (container == null) container = switchViewsHandlers.getContainerByComponent(comp);
        if (container == null) container = dimmerViewsHandlers.getContainerByComponent(comp);
        if (container == null)
            throw new RuntimeException("Unknown container for component " + comp);
        return container;
    }

}
