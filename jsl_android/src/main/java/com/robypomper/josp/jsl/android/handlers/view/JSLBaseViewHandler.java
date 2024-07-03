package com.robypomper.josp.jsl.android.handlers.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;


/**
 * Base class for all ViewHandlers: classes that keep synchronized a given View
 * and a specific aspect of the JSL library.
 * <p>
 * Like the JSL's Handlers, the ViewHandlers are used to handle a specific
 * aspect of the JSL library, but they are used to show the aspect into the UI.
 * <p>
 * As a base class, the JSLBaseViewHandler, provides all fields commons to all
 * ViewHandlers, like the main ViewGroup that hosts the views to update, the
 * main handler associated, but also string formats like the never, time and
 * date formats.<br/>
 * Then it set the base methods to update the UI.
 * <p>
 * Default values and formats:
 * <ul>
 *     <li>{@link #DEF_INIT_NEVER}: default date's string used when the given date is null</li>
 *     <li>{@link #DEF_FORMAT_TIME}: default time format to use for date (only time) to string conversion</li>
 *     <li>{@link #DEF_FORMAT_DATE}: default time format to use for date (only date) to string conversion</li>
 * </ul>
 * <p>
 * It also provides some utility methods to update the UI. This methods can be
 * used by subclasses to updates Views' properties like visibility, text, etc.
 * All those methods are prefixed with `try` to indicate that they do nothing
 * if the view is not found.
 * <p>
 * In order to implement a new ViewHandler, it's necessary to extend this class
 * and to implement the specific logic in the new class.<br/>
 * Generally, the new class will have a
 * <ul>
 *
 * DEFAULT VALUES
 *   - UI
 *   - DEF_INIT
 *   - DEF_...
 *   - DEF_FORMAT
 * Handler
 *   - depends on JSL aspect
 * Observed instances                           IS IT NECESSARY?
 *   - get/set methods wraps the handler's methods
 * Initial states, Default values, String Formats, Action names
 *   - by default from the DEFAULT VALUES::DEF_INIT
 *   - settable by client classes (if necessary they can update the UI)
 *
 * Subclasses capabilities:
 * TODO document how subclassed can do their stuff
 * - init UI via
 * - initHandlers
 * <p>
 * ViewHandler's Constants' conventions:
 * - UI_XY: ids for the component's view
 * - DEF_INIT_XY: values used when no component is set
 * - DEF_FORMAT_XY: default formats to use for conversion to string
 * - DEF_VAL_XY: default strings to use when the state/value is unknown
 * - DEF_CMD_XY: default strings used as command labels (only for actions)
 * - DEF_{OTHER}_: like DEF_ACTION_ as strings to describe action states
 * <p>
 * TODO add error handling method that can be used also by subclasses
 */
public class JSLBaseViewHandler {

    // Constants

    /**
     * Default date string used in {@link #convertDate(Date)} when the given date is null
     */
    public final static String DEF_INIT_NEVER = "Never";
    /**
     * Default time format to use for date (only time) to string conversion.
     * <p>
     * `null` means android.text.format.DateFormat.getTimeFormat(ctx);
     */
    public final static DateFormat DEF_FORMAT_TIME = null;
    /**
     * Default time format to use for date (only date) to string conversion.
     * <p>
     * `null` means android.text.format.DateFormat.getMediumDateFormat(ctx);
     */
    public final static DateFormat DEF_FORMAT_DATE = null;


    // Internal vars

    /**
     * The ViewGroup to use to look for handler's Views.
     */
    private final ViewGroup mainView;
    /**
     * String to use as date when the date is null.
     */
    private final String initNever;
    /**
     * Time format to use for date (only time) to string conversion.
     * <p>
     * This field can not be set after constructor because his update requires
     * a notification to all his subclasses in order to refresh the dates into
     * the UI.
     * <p>
     * By default it's null, that means, the constructors sets it to
     * {@link android.text.format.DateFormat#getTimeFormat(Context)}.
     */
    private final DateFormat formatTime;
    /**
     * Time format to use for date (only date) to string conversion.
     * <p>
     * This field can not be set after constructor because his update requires
     * a notification to all his subclasses in order to refresh the dates into
     * the UI.
     * <p>
     * By default it's null, that means, the constructors sets it to
     * {@link android.text.format.DateFormat#getMediumDateFormat(Context)}.
     */
    private final DateFormat formatDate;


    // Constructors

    /**
     * Create a new JSLBaseViewHandlerInternal based on given ViewGroup.
     * <p>
     * This constructor uses the default time and date formats.
     *
     * @param ctx       the context
     * @param mainView    the ViewGroup to use to look for component's Views
     */
    public JSLBaseViewHandler(Context ctx, ViewGroup mainView) {
        this(ctx, mainView, DEF_FORMAT_TIME, DEF_FORMAT_DATE);
    }

    /**
     * Create a new JSLBaseViewHandlerInternal based on given ViewGroup.
     * <p>
     * This constructor uses the default time and date formats.
     *
     * @param ctx       the context
     * @param mainView    the ViewGroup to use to look for component's Views
     * @param formatTime the time format to use for date (only time) to string conversion
     * @param formatDate the time format to use for date (only date) to string conversion
     */
    public JSLBaseViewHandler(Context ctx, ViewGroup mainView, DateFormat formatTime, DateFormat formatDate) {
        this(ctx, mainView, DEF_INIT_NEVER, formatTime, formatDate);
    }

    /**
     * Create a new JSLBaseViewHandlerInternal.
     *
     * @param ctx        the context
     * @param mainView     the ViewGroup to use to look for component's Views
     * @param formatTime the time format to use for date (only time) to string conversion
     * @param formatDate the time format to use for date (only date) to string conversion
     */
    public JSLBaseViewHandler(Context ctx, ViewGroup mainView, String initNever, DateFormat formatTime, DateFormat formatDate) {
        this.mainView = mainView;
        this.initNever = initNever != null ? initNever : DEF_INIT_NEVER;
        this.formatTime = formatTime != null ? formatTime : android.text.format.DateFormat.getTimeFormat(ctx);
        this.formatDate = formatDate != null ? formatDate : android.text.format.DateFormat.getMediumDateFormat(ctx);
    }


    // Getters and Setters

    /**
     * Get the main View of the handled View, to use to look for component's Views.
     *
     * @return the main View
     */
    protected ViewGroup getMainView() {
        return mainView;
    }


    // UI Methods

    /**
     * Update all handled Views into the UI.
     * <p>
     * This method is called by the setComponent method, so you don't need to
     * call it manually. Although, you can call it manually to force the UI
     * update.
     */
    public void updateUI() {
        // nothing to do
    }


    // UI Utils

    /**
     * Try to set the visibility of a View into a ViewGroup.
     * <p>
     * This method is used to set the visibility of a View into a ViewGroup. If
     * the view is not found, then nothing happens.
     *
     * @param mainView     the ViewGroup to look for the View
     * @param id         the id of the View
     * @param visibility the visibility to set
     */
    public static void trySetVisibility(ViewGroup mainView, int id, int visibility) {
        View v = mainView.findViewById(id);
        if (v != null)
            if (v.getVisibility() != visibility)
                v.setVisibility(visibility);
    }

    /**
     * Try to set the enabled state of a View into a ViewGroup.
     * <p>
     * This method is used to set the enabled state of a View into a ViewGroup.
     * If the view is not found, then nothing happens.
     *
     * @param mainView  the ViewGroup to look for the View
     * @param id      the id of the View
     * @param enabled the enabled state to set
     */
    public static void trySetEnabled(ViewGroup mainView, int id, boolean enabled) {
        View v = mainView.findViewById(id);
        if (v != null)
            if (v.isEnabled() != enabled)
                v.setEnabled(enabled);
    }

    /**
     * Try to set the text of a TextView into a ViewGroup.
     * <p>
     * This method is used to set the text of a TextView into a ViewGroup. If
     * the view is not found, or if the view is not a TextView, then nothing
     * happens.
     *
     * @param mainView the ViewGroup to look for the TextView
     * @param id     the id of the TextView
     * @param text   the text to set
     */
    public static void trySetText(ViewGroup mainView, int id, String text) {
        View v = mainView.findViewById(id);
        if (v != null)
            if (v instanceof TextView)
                if (((TextView) v).getText().toString().compareTo(text) != 0)
                    ((TextView) v).setText(text);
    }

    /**
     * Try to set the checked state of a Checkable into a ViewGroup.
     * <p>
     * This method is used to set the checked state of a Checkable into a
     * ViewGroup. If the view is not found, or if the view is not a Checkable,
     * then nothing happens.
     *
     * @param mainView  the ViewGroup to look for the Checkable
     * @param id      the id of the Checkable
     * @param checked the checked state to set
     */
    public static void trySetChecked(ViewGroup mainView, int id, boolean checked) {
        View v = mainView.findViewById(id);
        if (v != null)
            if (v instanceof Checkable)
                if (((Checkable) v).isChecked() != checked)
                    ((Checkable) v).setChecked(checked);
    }

    /**
     * Try to set the image of an ImageView into a ViewGroup.
     * <p>
     * This method is used to set the image of an ImageView into a ViewGroup. If
     * the view is not found, or if the view is not an ImageView, then nothing
     * happens.
     *
     * @param mainView the ViewGroup to look for the ImageView
     * @param id     the id of the ImageView
     * @param drawable the image to set
     */
    public static void trySetImage(ViewGroup mainView, int id, Drawable drawable) {
        View v = mainView.findViewById(id);
        if (v != null)
            if (v instanceof ImageView)
                if (((ImageView) v).getDrawable() != drawable)
                    ((ImageView) v).setImageDrawable(drawable);

    }

    /**
     * Try to set the progress of a ProgressBar into a ViewGroup.
     * <p>
     * This method is used to set the progress of a ProgressBar into a ViewGroup.
     * If the view is not found, or if the view is not a ProgressBar, then
     * nothing happens.
     *
     * @param mainView  the ViewGroup to look for the ProgressBar
     * @param id      the id of the ProgressBar
     * @param progress the progress to set
     */
    public static void trySetProgress(ViewGroup mainView, int id, int progress) {
        View v = mainView.findViewById(id);
        if (v != null)
            if (v instanceof ProgressBar)
                if (((ProgressBar) v).getProgress() != progress)
                    ((ProgressBar) v).setProgress(progress);
    }

    /**
     * Try to set the onClickListener of a View into a ViewGroup.
     * <p>
     * This method is used to set the onClickListener of a View into a ViewGroup.
     * If the view is not found, then nothing happens.
     *
     * @param mainView   the ViewGroup to look for the View
     * @param id       the id of the View
     * @param listener the onClickListener to set
     */
    public static void trySetOnClickListener(ViewGroup mainView, int id, View.OnClickListener listener) {
        View v = mainView.findViewById(id);
        if (v != null)
            v.setOnClickListener(listener);
    }

    /**
     * Try to set the onSeekBarChangeListener of a SeekBar into a ViewGroup.
     * <p>
     * This method is used to set the onSeekBarChangeListener of a SeekBar into
     * a ViewGroup. If the view is not found, then nothing happens.
     *
     * @param mainView   the ViewGroup to look for the SeekBar
     * @param id       the id of the SeekBar
     * @param listener the onSeekBarChangeListener to set
     */
    public static void trySetOnSeekBarChangeListener(ViewGroup mainView, int id, SeekBar.OnSeekBarChangeListener listener) {
        View v = mainView.findViewById(id);
        if (v != null)
            if (v instanceof SeekBar)
                ((SeekBar) v).setOnSeekBarChangeListener(listener);
    }

    /**
     * Convert a date to a string using the default time and date formats.
     *
     * @param date the date to convert
     * @return the date as string
     */
    protected String convertDate(Date date) {
        if (date == null) return initNever;

        if (DateUtils.isToday(date.getTime())) return formatTime.format(date);
        return formatTime.format(date) + " " + formatDate.format(date);
    }

}
