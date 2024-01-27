package com.robypomper.josp.jsl.android.components.charts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.robypomper.java.JavaDate;
import com.robypomper.josp.jsl.android.charts.R;
import com.robypomper.josp.jsl.android.components.charts.adapters.ChartViewAdapter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.ChartAdapterObserver;
import com.robypomper.josp.jsl.android.components.charts.utils.ChartExportable;
import com.robypomper.josp.jsl.android.components.charts.utils.TimeRangeLimits;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * fetch()
 * -> registerNewFetch()
 * <p>
 * notifyDataSetReceived()
 * -> updateDataSetToChart()
 * -> registerFetchReceived()
 * -> doPrepareDataSet()
 * -> registerFetchedCompletion()
 * -> registerFetchDataSet()
 * <p>
 * fetchTimerTimeout
 * -> registerFetchTimeout()
 * -> registerFetchDataSet()
 * <p>
 * TODO: chart predefined settings from menu
 * TODO: chart settings zoomer (+ and - buttons for Unit and Qty)
 * @noinspection unused
 */
public abstract class ChartBaseView extends ConstraintLayout implements ChartAdapterObserver, ChartExportable {

    // Constants

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat LOG_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    // Internal vars

    /**
     * The adapter that provides the data sets to display.
     * <p>
     * This view requires the adapter to be set as soon as possible.
     */
    private ChartViewAdapter adapter;
    /**
     * The activity that contains this view.
     * <p>
     * This view requires the adapter to be set as soon as possible.
     */
    private Activity activity;
    /**
     * Variable used to avoid the fetch of the data sets during the
     * initialization of the view.
     * <p>
     * It's initialized with 'true' value and set to 'false' when both
     * the activity and the adapter are set.
     */
    private boolean isInitializing = true;
    /**
     * Variable used to avoid multiple updates of the time range to the chart.
     * <p>
     * During the fetching process, this variable is set to `false` when the
     * internal settings have been updated, and set to `true` when the chart
     * has been updated.
     * <p>
     * This variable is used to execute the `runOnUiThread()` action only once,
     * also if this method has been called multiple times (fetches from different
     * data set).
     */
    boolean isTimeRangeToChartUpdated = true;
    /**
     * The timer used to schedule the fetch time outs.
     */
    private final Timer timer = new Timer("TIMER_FOR_CHART_BASE_VIEW");
    /**
     * List of current fetch timeout's tasks.
     * <p>
     * Added when a fetch is started, removed when the fetch is completed or
     * timed out.
     */
    private final Map<String, TimerTask> fetchTimeouts = new HashMap<>();
    private final List<String> fetchingDataSet = new ArrayList<>();
    private Map<String, DataSet<?>> fetchedDataSet = new HashMap<>();
    private Map<String, DataSet<?>> chartData = new HashMap<>();
    private final Map<String, DataSet<?>> rawData = new HashMap<>();
    private long fetchTimeoutMs = 10 * 1000;
    private boolean isTimeSettingsViewEnabled = true;
    private boolean isTimeNavigatorViewEnabled = true;
    private boolean isTimeSettingsBottomSheetEnabled = true;
    private boolean isExportsBottomSheetEnabled = true;

    /**
     * The time unit to consider for the current time range.
     * Valid units are:
     * <ul>
     *     <li>{@link Calendar#MINUTE}</li>
     *     <li>{@link Calendar#HOUR_OF_DAY}</li>
     *     <li>{@link Calendar#DAY_OF_MONTH}</li>
     *     <li>{@link Calendar#MONTH}</li>
     *     <li>{@link Calendar#YEAR}</li>
     * </ul>
     * <p>
     * Default 'Calendar.MINUTE'.
     */
    private int rangeUnit = Calendar.HOUR_OF_DAY;
    /**
     * The number of range units to consider for the current time range.
     * <p>
     * Default '30'.
     */
    private int rangeQty = 1;
    /**
     * The offset to apply to the current time range.
     * <p>
     * The offset is applied to the current time range, so it can be used to
     * shift the time range back and forth.
     *
     * <p>
     * Default '0'.
     */
    private int rangeOffset = 0;
    /**
     * The number of items to reduced the data set.
     * <p>
     * Regardless of the items contained into the data set, this view will
     * reduce the data set to this number of items. The reduction is performed
     * by the {@link MPAndroidChartUtils#reduceMiddleValueDataSet(DataSet, int, boolean)}
     * that parts the original data set into a number of chunks equals to the
     * filter count. Then it calculates the middle time of each chunk and
     * creates a new data set with the average value of each chunk.
     * <p>
     * Set it to -1 to disable the dataset reduction.
     * <p>
     * Default '20'.
     */
    private int rangePartitions;


    // UI Components

    private final TimeSettingsView timeSettingsView;
    private final TimeNavigatorView timeNavigatorView;
    private final Button btnTimeSettings;
    private final Button btnExports;


    // Constructors

    /**
     * Public constructor of the view. It calls the super constructor and the
     * init() method to inflate the layout and setup the UI.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public ChartBaseView(Context context) {
        this(context, null);
    }

    /**
     * Public constructor of the view. It calls the super constructor and the
     * init() method to inflate the layout and setup the UI.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public ChartBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Public constructor of the view. It calls the super constructor and the
     * init() method to inflate the layout and setup the UI.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
    public ChartBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Parse attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ChartBaseView, defStyleAttr, 0);
        if (a.hasValue(R.styleable.ChartBaseView_chart_range_offset))
            try {
                rangeOffset = a.getInt(R.styleable.ChartBaseView_chart_range_offset, rangeOffset);
            } catch (NumberFormatException ignore) {
            }
        if (a.hasValue(R.styleable.ChartBaseView_chart_range_unit))
            try {
                rangeUnit = a.getInt(R.styleable.ChartBaseView_chart_range_unit, rangeUnit);    // todo check if value is one of allow Calendar constants
            } catch (NumberFormatException ignore) {
            }
        if (a.hasValue(R.styleable.ChartBaseView_chart_range_qty))
            try {
                rangeQty = a.getInt(R.styleable.ChartBaseView_chart_range_qty, rangeQty);
            } catch (NumberFormatException ignore) {
            }
        if (a.hasValue(R.styleable.ChartBaseView_chart_reduction_count))
            try {
                rangePartitions = a.getInt(R.styleable.ChartBaseView_chart_reduction_count, rangePartitions);
            } catch (NumberFormatException ignore) {
            }
        else
            rangePartitions = getDefaultPartitionsByUnitAndQty(rangeUnit, rangeQty);
        if (a.hasValue(R.styleable.ChartBaseView_chart_fetch_timeout_ms))
            try {
                fetchTimeoutMs = a.getInt(R.styleable.ChartBaseView_chart_fetch_timeout_ms, (int) fetchTimeoutMs);
            } catch (NumberFormatException ignore) {
            }
        isTimeSettingsViewEnabled = a.getBoolean(R.styleable.ChartBaseView_chart_enable_time_settings_view, isTimeSettingsViewEnabled);
        isTimeNavigatorViewEnabled = a.getBoolean(R.styleable.ChartBaseView_chart_enable_time_navigator_view, isTimeNavigatorViewEnabled);
        isTimeSettingsBottomSheetEnabled = a.getBoolean(R.styleable.ChartBaseView_chart_enable_time_settings_bottom_sheet, isTimeSettingsBottomSheetEnabled);
        isExportsBottomSheetEnabled = a.getBoolean(R.styleable.ChartBaseView_chart_enable_exports_bottom_sheet, isExportsBottomSheetEnabled);
        a.recycle();

        // Inflate ui
        LayoutInflater.from(context).inflate(getLayout(), this, true);

        // Get ui components
        timeSettingsView = findViewById(R.id.timeSettingsView);
        timeNavigatorView = findViewById(R.id.timeNavigatorView);

        // Setup timeSettingsView
        timeSettingsView.setRangeUnit(rangeUnit);
        timeSettingsView.setRangeQty(rangeQty);
        timeSettingsView.addUnitListener(new TimeSettingsView.UnitListener() {
            @Override
            public void onUnitChanged(int newUnit, int oldUnit) {
                setRangeUnit(newUnit);
                //setRangeQty(newQty);
            }
        });
        timeSettingsView.addQtyListener(new TimeSettingsView.QtyListener() {
            @Override
            public void onQtyChanged(int newQty, int oldQty) {
                setRangeQty(newQty);
            }
        });

        // Setup timeNavigatorView
        timeNavigatorView.setRangeUnit(rangeUnit);
        timeNavigatorView.setRangeQty(rangeQty);
        timeNavigatorView.setRangeOffset(rangeOffset);
        timeNavigatorView.addOffsetListener(new TimeNavigatorView.OffsetListener() {
            @Override
            public void onOffsetChanged(int newOffset, int oldOffset) {
                setRangeOffset(newOffset);
            }

        });

        // Setup BottomSheetTimeSetting
        btnTimeSettings = findViewById(R.id.btnTimeSettings);
        btnTimeSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetTimeSetting(context);
            }
        });

        // Setup BottomSheetExports
        btnExports = findViewById(R.id.btnExports);
        btnExports.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetExports(context);
            }
        });

        // updateUI();  // called by setActivity() or setAdapter()
    }


    // Getters and Setters

    /**
     * @return the activity that contains this view.
     */
    protected Activity getActivity() {
        assert activity != null : "Activity not set";
        return activity;
    }

    /**
     * Set the activity that contains this view.
     * <p>
     * This view requires the activity to be set as soon as possible.
     * <p>
     * When set, and the adapter is already set, the view starts to fetch the
     * data sets from the adapter.
     * <p>
     * TODO check if it can replaced.
     *
     * @param activity the activity that contains this view.
     */
    public void setActivity(Activity activity) {
        assert this.activity == null : "Activity already set";
        assert activity != null : "Activity cannot be null";
        this.activity = activity;

        if (adapter != null) {
            doInit();
            isInitializing = false;
            updateUI();
            fetch();
        }
    }

    /**
     * @return the adapter that provides the data sets to display.
     */
    protected ChartViewAdapter getAdapter() {
        assert adapter != null : "Adapter not set";
        return adapter;
    }

    /**
     * Set the adapter that provides the data sets to display.
     * <p>
     * This view requires the adapter to be set as soon as possible.
     * <p>
     * When set, and the activity is already set, the view starts to fetch the
     * data sets from the adapter.
     * <p>
     * TODO check if it can replaced.
     *
     * @param adapter the adapter that provides the data sets to display.
     */
    public void setAdapter(ChartViewAdapter adapter) {
        assert this.adapter == null : "Adapter already set";
        assert adapter != null : "Adapter cannot be null";
        this.adapter = adapter;

        if (activity != null) {
            doInit();
            isInitializing = false;
            updateUI();
            fetch();
        }
    }

    /**
     * Method to implement to return the layout id to inflate.
     * <p>
     * NB: the layout must include at least the following components:
     * <ul>
     *     <li>{@link TimeSettingsView}: timeSettingsView</li>
     *     <li>{@link TimeNavigatorView}: timeNavigatorView</li>
     * </ul>
     *
     * @return the xml layout id to inflate.
     */
    protected abstract int getLayout();

    protected abstract ViewGroup getOverlayView();

    protected abstract TextView getOverlayText();

    /**
     * This method calls the {@link TimeRangeLimits#calculateTimeRangeLimits(Date, int, int, int)}
     * method.
     *
     * @return the time range limits calculated from the current time range
     * settings (unit, qty and offset).
     */
    private TimeRangeLimits getTimeRangeLimits() {
        return TimeRangeLimits.calculateTimeRangeLimits(JavaDate.getNowDate(), rangeUnit, rangeOffset, rangeQty);
    }

    /**
     * @param rangeUnit the time range unit used to calculate the
     *                  {@link #getTimeRangeLimits()}.
     * @param rangeQty  the time range quantity used to calculate the
     *                  {@link #getTimeRangeLimits()}.
     * @return a Date format pattern based on current time unit and quantity.
     */
    public String getDefaultUnitPattern(int rangeUnit, int rangeQty) {
        int unitToTest = rangeUnit;
        if (rangeQty <= 3)
            unitToTest = TimeRangeLimits.getLowerTimeUnit(rangeUnit);

        switch (unitToTest) {
            case Calendar.MILLISECOND:
                if (rangeUnit <= 3)
                    return "ss.SSS";
                else
                    return "mm:ss.SSS";
            case Calendar.SECOND:
                return "mm:ss";
            case Calendar.MINUTE:
                return "HH:mm";
            case Calendar.HOUR_OF_DAY:
                return "dd/MM HH:00";
            case Calendar.DAY_OF_MONTH:
                return "dd/MM";
            case Calendar.MONTH:
                return "MM/YYYY";
            case Calendar.YEAR:
                return "YYYY";
            default:
                throw new IllegalArgumentException("Invalid unit");
        }
    }

    public static int getDefaultQtyByUnit(int unit) {
        switch (unit) {
            case Calendar.MINUTE:
                return 15;
            case Calendar.HOUR_OF_DAY:
                return 6;
            case Calendar.DAY_OF_MONTH:
                return 7;
            case Calendar.MONTH:
                return 3;
            case Calendar.YEAR:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid unit");
        }
    }

    public static int getDefaultPartitionsByUnitAndQty(int unit, int qty) {
        // Partition counts for vertical screens
        // (MAX 12 partitions)
        if (unit == Calendar.MINUTE && qty == 1)
            return qty * 12;           // 12 - 12   an item every 5 seconds
        if (unit == Calendar.MINUTE && qty <= 3)
            return qty * 3;            //  6 -  9   an item every 20 seconds
        if (unit == Calendar.MINUTE && qty <= 5)
            return qty * 2;            //  8 - 10   an item every 30 seconds
        if (unit == Calendar.MINUTE && qty <= 12)
            return qty;               //  6 - 12   an item every minute
        if (unit == Calendar.MINUTE && qty == 15)
            return qty / 3;           //  5        an item every 5 minutes
        if (unit == Calendar.MINUTE && qty == 20)
            return qty / 2;           //  10       an item every 2 minutes
        if (unit == Calendar.MINUTE && qty <= 24)
            return qty / 2;           //  6. - 12  an item every ~2 minute
        if (unit == Calendar.MINUTE && qty == 30)
            return qty / 3;           //  10       an item every 3 minutes
        if (unit == Calendar.MINUTE && qty <= 48)
            return qty / 4;           //  6. - 12  an item every ~4 minute
        if (unit == Calendar.MINUTE)
            return 10;                             //  10       an item every qty/10 minute (start at 1 item each ~5 minutes)

        if (unit == Calendar.HOUR_OF_DAY && qty == 1)
            return qty * 12;      // 12 - 12   an item every 5 minutes
        if (unit == Calendar.HOUR_OF_DAY && qty <= 3)
            return qty * 3;       //  6 -  9   an item every 20 minutes
        if (unit == Calendar.HOUR_OF_DAY && qty <= 5)
            return qty * 2;       //  8 - 10   an item every 30 minutes
        if (unit == Calendar.HOUR_OF_DAY && qty <= 12)
            return qty;          //  6 - 12   an item every hour
        if (unit == Calendar.HOUR_OF_DAY && qty == 15)
            return qty / 3;      //  5        an item every 5 minutes
        if (unit == Calendar.HOUR_OF_DAY && qty == 20)
            return qty / 2;      //  10       an item every 2 hour
        if (unit == Calendar.HOUR_OF_DAY && qty <= 24)
            return qty / 2;      //  6. - 12  an item every ~2 hour
        if (unit == Calendar.HOUR_OF_DAY && qty == 30)
            return qty / 3;      //  10       an item every 3 hour
        if (unit == Calendar.HOUR_OF_DAY && qty <= 48)
            return qty / 4;      //  6. - 12  an item every ~4 hour
        if (unit == Calendar.HOUR_OF_DAY)
            return 10;                        //  10       an item every qty/10 hour (start at 1 item each ~5 hours)

        if (unit == Calendar.DAY_OF_MONTH && qty == 1)
            return qty * 12;     // 12 - 12   an item every 2 hours
        if (unit == Calendar.DAY_OF_MONTH && qty <= 3)
            return qty * 3;      //  6 -  9   an item every 8 hours
        if (unit == Calendar.DAY_OF_MONTH && qty <= 5)
            return qty * 2;      //  8 - 10   an item every 12 hours
        if (unit == Calendar.DAY_OF_MONTH && qty <= 12)
            return qty;         //  6 - 12   an item every day
        if (unit == Calendar.DAY_OF_MONTH && qty == 15)
            return qty / 3;     //  5        an item every 3 days
        if (unit == Calendar.DAY_OF_MONTH && qty == 20)
            return qty / 2;     //  10       an item every 2 days
        if (unit == Calendar.DAY_OF_MONTH && qty <= 24)
            return qty / 2;     //  6. - 12  an item every ~2 days
        if (unit == Calendar.DAY_OF_MONTH && qty == 30)
            return qty / 3;     //  10       an item every 3 days
        if (unit == Calendar.DAY_OF_MONTH && qty <= 48)
            return qty / 4;     //  6. - 12  an item every ~4 days
        if (unit == Calendar.DAY_OF_MONTH)
            return 10;                       //  10       an item every qty/10 day (start at 1 item each ~5 days)

        if (unit == Calendar.MONTH && qty == 1)
            return qty * 10;            // 10 - 10   an item every ~3 days
        if (unit == Calendar.MONTH && qty <= 3)
            return qty * 4;             //  8 - 12   an item every ~7.5 days
        if (unit == Calendar.MONTH && qty <= 5)
            return qty * 2;             //  8 - 10   an item every ~15 days
        if (unit == Calendar.MONTH && qty <= 12)
            return qty;                //  6 - 12   an item every month
        if (unit == Calendar.MONTH && qty == 15)
            return qty / 3;            //  5        an item every 3 months
        if (unit == Calendar.MONTH && qty == 20)
            return qty / 2;            //  10       an item every 2 months
        if (unit == Calendar.MONTH && qty <= 24)
            return qty / 2;            //  6. - 12  an item every ~2 months
        if (unit == Calendar.MONTH && qty == 30)
            return qty / 3;            //  10       an item every 3 months
        if (unit == Calendar.MONTH && qty <= 48)
            return qty / 4;            //  6. - 12  an item every ~4 months
        if (unit == Calendar.MONTH)
            return 10;                              //  10       an item every qty/10 month (start at 1 item each ~5 months)

        if (unit == Calendar.YEAR && qty == 1)
            return qty * 12;             // 12 - 12   an item every 1 month
        if (unit == Calendar.YEAR && qty <= 3)
            return qty * 4;              //  8 - 12   an item every 3 months
        if (unit == Calendar.YEAR && qty <= 5)
            return qty * 2;              //  8 - 10   an item every 6 months
        if (unit == Calendar.YEAR && qty <= 12)
            return qty;                //  6 - 12   an item every month
        if (unit == Calendar.YEAR && qty == 15)
            return qty / 3;            //  5        an item every 3 years
        if (unit == Calendar.YEAR && qty == 20)
            return qty / 2;            //  10       an item every 2 years
        if (unit == Calendar.YEAR && qty <= 24)
            return qty / 2;            //  6. - 12  an item every ~2 years
        if (unit == Calendar.YEAR && qty == 30)
            return qty / 3;            //  10       an item every 3 years
        if (unit == Calendar.YEAR && qty <= 48)
            return qty / 4;            //  6. - 12  an item every ~4 years
        if (unit == Calendar.YEAR)
            return 10;                              //  10       an item every qty/10 year (start at 1 item each ~5 years)

        throw new IllegalArgumentException("Invalid unit");
    }

    public boolean isFetching() {
        return !fetchingDataSet.isEmpty();
    }

    public boolean isFetching(String dataSetName) {
        return fetchingDataSet.contains(dataSetName);
    }

    public int getFetchCounter() {
        return fetchingDataSet.size();
    }

    public List<String> getProcessingDataSets() {
        List<String> pDS = new ArrayList<>(fetchingDataSet);
        pDS.removeAll(getWaitingDataSets());
        return pDS;
    }

    public List<String> getWaitingDataSets() {
        return new ArrayList<>(fetchTimeouts.keySet());
    }

    public List<String> getCompletedDataSets() {
        return new ArrayList<>(fetchedDataSet.keySet());
    }

    private long getFetchTimeoutMs() {
        return fetchTimeoutMs;
    }

    /**
     * Set the fetch timeout in milliseconds.
     * <p>
     * If this method is called during a fetch, the timeout is applied to the
     * next fetch.
     *
     * @param fetchTimeoutMs the fetch timeout in milliseconds.
     */
    public void setFetchTimeoutMs(long fetchTimeoutMs) {
        this.fetchTimeoutMs = fetchTimeoutMs;
    }

    /**
     * @return the current time range offset used to calculate the
     * {@link #getTimeRangeLimits()}.
     */
    public int getRangeOffset() {
        return rangeOffset;
    }

    /**
     * Set the time range offset used to calculate the {@link #getTimeRangeLimits()}.
     * <p>
     * The offset is applied to the current time range, so it can be used to
     * shift the time range back and forth.
     * <p>
     * When the offset is changed, the chart is automatically updated with a
     * new fetch using the updated {@link #getTimeRangeLimits()}.
     *
     * @param offset the time range offset used to calculate the
     *               {@link #getTimeRangeLimits()}.
     */
    public void setRangeOffset(int offset) {
        // Set range offset
        rangeOffset = offset;

        // Update sub views
        Log.v("ChartBaseView", "Updated TimeRange Offset: " + rangeOffset);
        timeNavigatorView.setRangeOffset(offset);

        // Fetch new data
        fetch();
    }

    /**
     * @return the current time range unit used to calculate the
     * {@link #getTimeRangeLimits()}.
     */
    public int getRangeUnit() {
        return rangeUnit;
    }

    /**
     * Set the time range unit used to calculate the {@link #getTimeRangeLimits()}.
     * <p>
     * Valid unit are:
     * <ul>
     *     <li>{@link Calendar#MINUTE}</li>
     *     <li>{@link Calendar#HOUR_OF_DAY}</li>
     *     <li>{@link Calendar#DAY_OF_MONTH}</li>
     *     <li>{@link Calendar#MONTH}</li>
     *     <li>{@link Calendar#YEAR}</li>
     * </ul>
     * <p>
     * When the unit is changed, the chart is automatically updated with a
     * new fetch using the updated {@link #getTimeRangeLimits()}. In this case,
     * the fetch is not direct but it's delegated to the {@link #setRangeQty(int)}
     * method (via {@link #refreshTimeRangeQTY()}). This is because the
     * quantity of the time range depends on the unit.
     *
     * @param unit the time range unit used to calculate the
     *             {@link #getTimeRangeLimits()}.
     */
    public void setRangeUnit(int unit) {
        // Set range unit and updates other range values
        rangeUnit = unit;
        rangeQty = getDefaultQtyByUnit(unit);
        rangeOffset = 0;
        rangePartitions = getDefaultPartitionsByUnitAndQty(rangeUnit, rangeQty);

        // Update date formats
        String newDateFormat = getDefaultUnitPattern(rangeUnit, rangeQty);
        ChartBaseFormatter xFormatter = getAdapter().getXFormatter();
        if (xFormatter instanceof ChartDateTimeFormatter) {
            ChartDateTimeFormatter formatter = (ChartDateTimeFormatter) xFormatter;
            formatter.setDateFormat(newDateFormat);
        }
        timeNavigatorView.setDateTimeFormat(newDateFormat);

        // Update sub views
        Log.v("ChartBaseView", "Updated TimeRange Unit: " + rangeUnit);
        timeSettingsView.setRangeUnit(rangeUnit);
        timeSettingsView.setRangeQty(rangeQty);
        timeNavigatorView.setRangeQty(rangeQty);
        timeNavigatorView.setRangeUnit(rangeUnit);
        timeNavigatorView.setRangeOffset(rangeOffset);

        // Fetch new data
        fetch();
    }

    /**
     * @return the current time range quantity used to calculate the
     * {@link #getTimeRangeLimits()}.
     */
    public int getRangeQty() {
        return rangeQty;
    }

    /**
     * Set the time range quantity used to calculate the {@link #getTimeRangeLimits()}.
     * <p>
     * When the quantity is changed, the chart is automatically updated with a
     * new fetch using the updated {@link #getTimeRangeLimits()}.
     *
     * @param qty the time range quantity used to calculate the
     *            {@link #getTimeRangeLimits()}.
     */
    public void setRangeQty(int qty) {
        // Set range qty and updates other range values
        rangeQty = qty;
        rangeOffset = 0;
        rangePartitions = getDefaultPartitionsByUnitAndQty(rangeUnit, rangeQty);

        // Update date formats
        String newDateFormat = getDefaultUnitPattern(rangeUnit, rangeQty);
        ChartBaseFormatter xFormatter = getAdapter().getXFormatter();
        if (xFormatter instanceof ChartDateTimeFormatter) {
            ChartDateTimeFormatter formatter = (ChartDateTimeFormatter) xFormatter;
            formatter.setDateFormat(newDateFormat);
        }
        timeNavigatorView.setDateTimeFormat(newDateFormat);

        // Update sub views
        Log.v("ChartBaseView", "Updated TimeRange Qty: " + rangeUnit);
        timeSettingsView.setRangeQty(rangeQty);
        timeNavigatorView.setRangeQty(rangeQty);
        timeNavigatorView.setRangeOffset(rangeOffset);

        // Fetch new data
        fetch();
    }

    /**
     * @return the current data set reduction count, if -1 the reduction is
     * disabled.
     */
    public int getRangePartitions() {
        return rangePartitions;
    }

    /**
     * Set the data set partitions count and fetch.
     * <p>
     * Regardless of the items contained into the data set, this view will
     * reduce the data set to this number of items. The reduction is performed
     * by the {@link MPAndroidChartUtils#reduceMiddleValueDataSet(DataSet, int, boolean)}
     * that parts the original data set into a number of chunks equals to the
     * filter count. Then it calculates the middle time of each chunk and
     * creates a new data set with the average value of each chunk.
     * <p>
     * Set it to -1 to disable the dataset reduction.
     * <p>
     * When the reduction count is changed, the chart is automatically updated
     * with a new fetch using the updated {@link #getTimeRangeLimits()}.
     *
     * @param partitions the data set reduction count.
     */
    public void setRangePartitions(int partitions) {
        // Set range partition
        rangePartitions = partitions;

        // Update sub views
        Log.v("ChartBaseView", "Updated TimeRange Partitions count: " + rangeUnit);
        // N/A

        // Fetch new data
        fetch();
    }


    // Abstract methods

    protected abstract void doInit();

    protected abstract void doUpdateTimeRangeOnChart(TimeRangeLimits limits);

    protected abstract DataSet<?> doPrepareDataSet(String dataSetName, DataSet<?> dataSet, TimeRangeLimits limits);

    protected abstract void doAddDataSetFromChart(String dataSetName, DataSet<?> dataSet);

    protected abstract void doRemoveDataSetFromChart(String dataSetName);

    protected abstract void doInvalidateChart(boolean animate);


    // UI Methods (Chart)

    private void updateUI() {
        assert !isInitializing : "Cannot update UI during initialization";

        enableTimeSettingsView(isTimeSettingsViewEnabled);
        enableTimeNavigatorView(isTimeNavigatorViewEnabled);
        enableTimeSettingsBottomSheet(isTimeSettingsBottomSheetEnabled);
        enableExportsBottomSheet(isExportsBottomSheetEnabled);
    }

    private void updateTimeRangeToChart(TimeRangeLimits limits, boolean invalidate) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doUpdateTimeRangeOnChart(limits);
                Log.v("ChartBaseView", String.format("Updated chart time range: %s -> %s",
                        LOG_SDF.format(limits.getFromDate()),
                        LOG_SDF.format(limits.getToDate())));
                if (invalidate) invalidateChart(true);
            }
        });
    }

    private void addDataSetToChart(String dataSetName, DataSet<?> dataSet, boolean invalidate) {
        // style data set
        getAdapter().setupDataSetStyle(dataSetName, dataSet);
        dataSet.setAxisDependency(getAdapter().getDataSetYAxisDep(dataSetName));

        doAddDataSetFromChart(dataSetName, dataSet);
        Log.v("ChartBaseView", String.format("Added data set '%s' to chart", dataSetName));
        if (invalidate) invalidateChart(true);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void removeDataSetFromChart(String dataSetName, boolean invalidate) {
        doRemoveDataSetFromChart(dataSetName);
        Log.v("ChartBaseView", String.format("Removed data set '%s' from chart", dataSetName));
        if (invalidate) invalidateChart(true);
    }

    private void invalidateChart(boolean animate) {
        doInvalidateChart(animate);
    }


    // Data sets fetching

    private void cleanCurrentFetching() {
        for (TimerTask task : fetchTimeouts.values())
            task.cancel();
        fetchTimeouts.clear();
        fetchedDataSet.clear();
        fetchingDataSet.clear();
    }

    private void cleanCurrentFetching(String dataSetName) {
        TimerTask task = fetchTimeouts.remove(dataSetName);
        if (task != null)
            task.cancel();
        fetchedDataSet.clear();
        fetchingDataSet.remove(dataSetName);
    }

    public void fetch() {
        if (isInitializing) return;
        fetch(getAdapter().getDataSetNames());
    }

    @Override
    public void fetch(String dataSetName) {
        fetch(Collections.singletonList(dataSetName));
    }

    private void fetch(List<String> dataSetsToFetch) {
        TimeRangeLimits limits = getTimeRangeLimits();

        if (isInitializing) return;

        for (String dataSetName : dataSetsToFetch) {
            try {
                if (isFetching(dataSetName)) {
                    String msg = String.format("Data set '%s' already fetching, skip it", dataSetName);
                    Log.w("ChartBaseView", msg);
                    cleanCurrentFetching(dataSetName);
                }

                registerNewFetch(dataSetName, limits);

                getAdapter().doFetch(dataSetName, limits);

            } catch (Throwable e) {
                String msg = String.format(String.format("Unknown error fetching the data sets: %s", e.getMessage()));
                Log.w("ChartBaseView", msg, e);
            }
        }
    }

    private void registerNewFetch(String dataSetName, TimeRangeLimits limits) {
        Log.v("ChartBaseView", String.format("DataSet '%s': register fetching data set %s - %s range", dataSetName, LOG_SDF.format(limits.getFromDate()), LOG_SDF.format(limits.getToDate())));

        // Fetching data sets list
        fetchingDataSet.add(dataSetName);

        // Timeout time task
        TimerTask task = startFetchTimer(dataSetName, limits);
        fetchTimeouts.put(dataSetName, task);

        // Remove raw data set
        rawData.remove(dataSetName);
        Runtime.getRuntime().gc();

        showUIFetchingMessage();
    }

    @Override
    public void processFetchedDataSet(String dataSetName, DataSet<?> dataSet, TimeRangeLimits limits) {
        if (!isFetching(dataSetName))
            return; // fetch deleted or timeout

        // Check if data set is empty
        if (dataSet == null || dataSet.getEntryCount() == 0) {
            Log.w("ChartBaseView", String.format("Received empty data for the data set: '%s'", dataSetName));
            registerFetchReceived(dataSetName, limits, null);
            registerFetchCompletion(dataSetName, limits, null);
            return;
        }

        registerFetchReceived(dataSetName, limits, dataSet);

        dataSet = doPrepareDataSet(dataSetName, dataSet, limits);

        registerFetchCompletion(dataSetName, limits, dataSet);
    }

    private void registerFetchReceived(String dataSetName, TimeRangeLimits limits, DataSet<?> dataSet) {
        Log.v("ChartBaseView", String.format("DataSet '%s': register data received %s - %s range (%d)", dataSetName, LOG_SDF.format(limits.getFromDate()), LOG_SDF.format(limits.getToDate()), dataSet != null ? dataSet.getEntryCount() : -1));

        // add raw data set
        rawData.put(dataSetName, dataSet);

        // Timeout time task
        TimerTask task = fetchTimeouts.remove(dataSetName);
        assert task != null : String.format("Fetch timeout for '%s' dataSet not found", dataSetName);
        task.cancel();

        showUIFetchingMessage();
    }

    private void registerFetchCompletion(String dataSetName, TimeRangeLimits limits, DataSet<?> dataSet) {
        Log.v("ChartBaseView", String.format("DataSet '%s': register completed/processed %s - %s range (%d)", dataSetName, LOG_SDF.format(limits.getFromDate()), LOG_SDF.format(limits.getToDate()), dataSet != null ? dataSet.getEntryCount() : 0));

        // Fetching data sets list
        fetchingDataSet.remove(dataSetName);
        if (!isFetching()) hideUIMessage();

        // Update chart
        if (dataSet == null) {
            dataSet = generateZeroDataSet(dataSetName, limits);
            dataSet.setLabel(getAdapter().getDataSetLabel(dataSetName) + " - NO_DATA");
        } else {
            dataSet.setLabel(getAdapter().getDataSetLabel(dataSetName));
        }
        registerFetchDataSet(dataSetName, dataSet, limits);

        if (isFetching()) showUIFetchingMessage();
    }

    protected void registerFetchTimeout(String dataSetName, TimeRangeLimits limits) {
        Log.v("ChartBaseView", String.format("DataSet '%s': register time out %s - %s range", dataSetName, LOG_SDF.format(limits.getFromDate()), LOG_SDF.format(limits.getToDate())));
        displayToastMessage(String.format("DataSet '%s': fetch timeout", dataSetName));

        // Timeout time task
        fetchTimeouts.remove(dataSetName);

        // Fetching data sets list
        fetchingDataSet.remove(dataSetName);
        if (!isFetching()) hideUIMessage();

        // Update chart
        DataSet<?> dataSet = generateZeroDataSet(dataSetName, limits);
        dataSet.setLabel(getAdapter().getDataSetLabel(dataSetName) + " - TIMEOUT");
        registerFetchDataSet(dataSetName, dataSet, limits);
        //invalidateChart(true);
        //updateTimeRangeToChart(limits);
    }

    private void registerFetchDataSet(String dataSetName, DataSet<?> dataSet, TimeRangeLimits limits) {
        Log.v("ChartBaseView", String.format("DataSet '%s': register ready to be shown %s - %s range (%d)", dataSetName, LOG_SDF.format(limits.getFromDate()), LOG_SDF.format(limits.getToDate()), dataSet.getEntryCount()));
        fetchedDataSet.put(dataSetName, dataSet);

        // Sync fetched data sets with chart
        if (!isFetching()) {
            for (Map.Entry<String, DataSet<?>> entry : fetchedDataSet.entrySet()) {
                removeDataSetFromChart(entry.getKey(), false);
                addDataSetToChart(entry.getKey(), entry.getValue(), false);
            }
            chartData = fetchedDataSet;
            fetchedDataSet = new HashMap<>();
            Runtime.getRuntime().gc();
            updateTimeRangeToChart(limits, false);
            invalidateChart(true);
        }
    }

    private <T extends DataSet<?>> T generateZeroDataSet(String dataSetName, TimeRangeLimits limits) {
        List<Entry> dataSetFilteredEntries = new ArrayList<>();
        for (int i = 0; i < getRangePartitions(); i++)
            dataSetFilteredEntries.add(new Entry(i, 0));

        Map<Date, List<Float>> partitions = MPAndroidChartUtils.generatePartitionsMidDate((ChartDateTimeFormatter) adapter.getXFormatter(), limits, getRangePartitions());
        for (List<Float> l : partitions.values())
            l.add(0F);

        // Convert Map<Date,List<Float>> to List<Entry>
        T dataSet = MPAndroidChartUtils.mapPartitionsToDataSet(getChartDataSetClass(), dataSetName + " - NO_DATA", getChartEntryClass(), partitions, (ChartDateTimeFormatter) adapter.getXFormatter());
        dataSet.setLabel(adapter.getDataSetLabel(dataSetName));
        return dataSet;
    }


    // Fetch timer methods

    private TimerTask startFetchTimer(String dataSetName, TimeRangeLimits limits) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!isFetching(dataSetName))
                    return; // already deleted or fetched

                registerFetchTimeout(dataSetName, limits);
            }
        };

        timer.schedule(task, getFetchTimeoutMs());
        return task;
    }


    // UI and Toast messages

    private void showUIFetchingMessage() {
        List<String> wDS = getWaitingDataSets();
        List<String> pDS = getProcessingDataSets();
        List<String> cDS = getCompletedDataSets();
        String message = "";
        if (wDS.size() > 0) {
            message += "Waiting data sets: \n";
            for (String dataSetName : wDS)
                message += "- " + getAdapter().getDataSetLabel(dataSetName) + "\n";
        }
        if (pDS.size() > 0) {
            message += "Processing data sets: \n";
            for (String dataSetName : pDS)
                message += "- " + getAdapter().getDataSetLabel(dataSetName) + "\n";
        }
        if (cDS.size() > 0) {
            message += "Completed data sets: \n";
            for (String dataSetName : cDS)
                message += "- " + getAdapter().getDataSetLabel(dataSetName) + "\n";
        }
        updateUIMessage(true, message);
    }

    private void hideUIMessage() {
        updateUIMessage(false, "");
    }

    private void updateUIMessage(boolean visible, String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup overlayView = getOverlayView();
                TextView overlayText = getOverlayText();

                overlayText.setText(text);
                overlayView.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void displayToastMessage(String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // UI Listeners

    private void showBottomSheetTimeSetting(Context context) {
        TimeSettingsBottomSheet frmTimeSettingsBottomSheet = new TimeSettingsBottomSheet(context);

        // Setup time timeSettingsView
        frmTimeSettingsBottomSheet.setRangePeriod(rangeUnit);
        frmTimeSettingsBottomSheet.setRangeQty(rangeQty);
        frmTimeSettingsBottomSheet.addPeriodListener(new TimeSettingsBottomSheet.PeriodListener() {
            @Override
            public void onPeriodChanged(int newPeriod, int oldPeriod) {
                setRangeUnit(newPeriod);
            }
        });
        frmTimeSettingsBottomSheet.addQtyListener(new TimeSettingsBottomSheet.QtyListener() {
            @Override
            public void onQtyChanged(int newQty, int oldQty) {
                setRangeQty(newQty);
            }
        });

        // Get the fragment manager
        if (!(getContext() instanceof FragmentActivity))
            throw new IllegalStateException("Context must be an Activity");
        FragmentManager fragmentMngr = ((FragmentActivity) getContext()).getSupportFragmentManager();

        // Show the bottom sheet
        frmTimeSettingsBottomSheet.show(fragmentMngr, TimeSettingsBottomSheet.TAG);
    }

    private void showBottomSheetExports(Context context) {
        ExportsBottomSheet frmExportsBottomSheet = new ExportsBottomSheet(context);
        frmExportsBottomSheet.setChart(this);
        frmExportsBottomSheet.setXFormatter(adapter.getXFormatter());

        // Get the fragment manager
        if (!(getContext() instanceof FragmentActivity))
            throw new IllegalStateException("Context must be an Activity");
        FragmentManager fragmentMngr = ((FragmentActivity) getContext()).getSupportFragmentManager();

        // Show the bottom sheet
        frmExportsBottomSheet.show(fragmentMngr, ExportsBottomSheet.TAG);
    }


    // UI enable/disable options

    private void enableTimeSettingsView(boolean enabled) {
        isTimeSettingsViewEnabled = enabled;
        if (isInitializing) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeSettingsView.setVisibility(isTimeSettingsViewEnabled ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void enableTimeNavigatorView(boolean enabled) {
        isTimeNavigatorViewEnabled = enabled;
        if (isInitializing) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeNavigatorView.setVisibility(isTimeNavigatorViewEnabled ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void enableTimeSettingsBottomSheet(boolean enabled) {
        isTimeSettingsBottomSheetEnabled = enabled;
        if (isInitializing) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnTimeSettings.setVisibility(isTimeSettingsBottomSheetEnabled ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void enableExportsBottomSheet(boolean enabled) {
        isExportsBottomSheetEnabled = enabled;
        if (isInitializing) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnExports.setVisibility(isExportsBottomSheetEnabled ? View.VISIBLE : View.GONE);
            }
        });
    }


    // Export methods from ChartExportable

    public Map<String, DataSet<?>> exportChartData() {
        return chartData;
    }

    public Map<String, DataSet<?>> exportRAW() {
        return rawData;
    }

    protected Bitmap loadBitmapFromView(View v) {
        int width = v.getLayoutParams().width > 0 ? v.getLayoutParams().width : v.getWidth();
        int height = v.getLayoutParams().height > 0 ? v.getLayoutParams().height : v.getHeight();
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

}