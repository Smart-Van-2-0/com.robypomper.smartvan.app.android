package com.robypomper.josp.jsl.android.components.charts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StyleRes;
import androidx.core.content.FileProvider;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.robypomper.josp.jsl.android.charts.R;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartBaseFormatter;
import com.robypomper.josp.jsl.android.components.charts.formatters.ChartDateTimeFormatter;
import com.robypomper.josp.jsl.android.components.charts.utils.ChartExportable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * Fragment that shows a bottom sheet dialog with the options to export a chart.
 * <p>
 * The chart must implement the {@link ChartExportable} interface.
 * <p>
 * The bottom sheet has three toggle groups:
 * <ul>
 *   <li>Export type: Image, CSV, JSON</li>
 *   <li>Export source: Chart's DataSets, Raw Data</li>
 *   <li>Export destination: Share, File</li>
 * </ul>
 *
 * @noinspection unused, SameParameterValue
 */
public class ExportsBottomSheet extends BottomSheetDialogFragment {

    // Constants

    public static final String TAG = ExportsBottomSheet.class.getSimpleName();
    private static final int LAYOUT = R.layout.bottom_sheet_exports_large;
    private static final Bitmap.CompressFormat IMG_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final String JSON_FORMAT = "{\n\t\"timestamp\":\"%s\",\n\t\"dataset\": \"%s\",\n\t\"value\": \"%.2f\"\n},\n";
    private static final String CSV_TITLE = "Timestamp,DataSet,Value\n";
    private static final String CSV_FORMAT = "%s,%s,%.2f\n";
    private static final String DEF_FILE_PROVIDER_EXTENSION = "chart.share";


    // Internal vars

    private final Context currentContext;
    private ChartExportable chart = null;
    private ChartBaseFormatter xFormatter;
    private static int lastTypeUsed = R.id.btnTypeImage;
    private static int lastSourceUsed = R.id.btnSourceChart;
    private static int lastDestUsed = R.id.btnDestShare;


    // View args

    private String exportsDirName = "ChartExported";
    private String formatFilenameImg = "jsl_chart_%s.%s";
    private String formatFilenameData = "jsl_chart_%s.%s";
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatFilenameDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private String titleShareImg = "Share JSL Chart Image";
    private String titleShareData = "Share JSL Chart Data";
    private final String idFileProvider;
    private int textAppearanceTitle = 0;   //  com.google.android.material.R.attr.textAppearanceHeadline6
    private int textAppearanceTxt = 0;   //  com.google.android.material.R.attr.textAppearanceSubtitle1
    private int textAppearanceTgl = 0;   //  N/A (style: ?attr/materialButtonOutlinedStyle)
    private int textAppearanceBtn = 0;   //  N/A (style: ?android:attr/buttonBarButtonStyle)

    private TextView txtTypeTitle;
    private TextView txtTypeDescription;
    private TextView txtSourceTitle;
    private TextView txtSourceDescription;
    private TextView txtDestTitle;
    private TextView txtDestDescription;
    private MaterialButtonToggleGroup tglType;
    private MaterialButtonToggleGroup tglSource;
    private MaterialButtonToggleGroup tglDest;
    private Button btnCancel;
    private Button btnExport;


    // Constructors

    public ExportsBottomSheet(Context context) {
        this(context, null, 0);
    }

    public ExportsBottomSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("SimpleDateFormat")
    public ExportsBottomSheet(Context context, AttributeSet attrs, int defStyle) {
        super();
        currentContext = context;

        // Parse attributes
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExportsBottomSheet, defStyle, 0);
        textAppearanceTitle = a.getResourceId(R.styleable.ExportsBottomSheet_exports_bottom_text_appearance_title, textAppearanceTitle);
        textAppearanceTxt = a.getResourceId(R.styleable.ExportsBottomSheet_exports_bottom_text_appearance_txt, textAppearanceTxt);
        textAppearanceTgl = a.getResourceId(R.styleable.ExportsBottomSheet_exports_bottom_text_appearance_tgl, textAppearanceTgl);
        textAppearanceBtn = a.getResourceId(R.styleable.ExportsBottomSheet_exports_bottom_text_appearance_btn, textAppearanceBtn);
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_exports_dir_name))
            exportsDirName = a.getString(R.styleable.ExportsBottomSheet_exports_bottom_exports_dir_name);
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_format_filename_img))
            formatFilenameImg = a.getString(R.styleable.ExportsBottomSheet_exports_bottom_format_filename_img);
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_format_filename_data))
            formatFilenameData = a.getString(R.styleable.ExportsBottomSheet_exports_bottom_format_filename_data);
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_format_filename_date))
            formatFilenameDate = new SimpleDateFormat(a.getString(R.styleable.ExportsBottomSheet_exports_bottom_format_filename_date));
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_format_date))
            formatDate = new SimpleDateFormat(a.getString(R.styleable.ExportsBottomSheet_exports_bottom_format_date));
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_title_share_img))
            titleShareImg = a.getString(R.styleable.ExportsBottomSheet_exports_bottom_title_share_img);
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_title_share_data))
            titleShareData = a.getString(R.styleable.ExportsBottomSheet_exports_bottom_title_share_data);
        if (a.hasValue(R.styleable.ExportsBottomSheet_exports_bottom_id_file_provider))
            idFileProvider = a.getString(R.styleable.ExportsBottomSheet_exports_bottom_id_file_provider);
        else
            idFileProvider = context.getPackageName() + "." + DEF_FILE_PROVIDER_EXTENSION;
        a.recycle();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(LAYOUT, container, false);

        // Get UI components
        tglType = v.findViewById(R.id.tglType);
        tglSource = v.findViewById(R.id.tglSource);
        tglDest = v.findViewById(R.id.tglDest);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnExport = v.findViewById(R.id.btnExport);
        txtTypeTitle = v.findViewById(R.id.txtTypeTitle);
        txtTypeDescription = v.findViewById(R.id.txtTypeDescription);
        txtSourceTitle = v.findViewById(R.id.txtSourceTitle);
        txtSourceDescription = v.findViewById(R.id.txtSourceDescription);
        txtDestTitle = v.findViewById(R.id.txtDestTitle);
        txtDestDescription = v.findViewById(R.id.txtDestDescription);

        // setup type buttons
        tglType.check(getLastTypeUsed());
        tglType.addOnButtonCheckedListener(onTglTypeCheckedListener);

        // setup source buttons
        tglSource.check(getLastSourceUsed());
        updateSourceButtonsList(tglType, tglSource, getContext());

        // setup dest buttons
        tglDest.check(getLastDestUsed());

        // setup Apply|Cancel buttons
        btnCancel.setOnClickListener(onBtnCancelClickListener);
        btnExport.setOnClickListener(onBtnExportClickListener);

        updateTitleTextAppearance();
        updateTxtTextAppearance();
        updateTglTextAppearance();
        updateBtnTextAppearance();

        return v;
    }


    // Getters/Setters

    public void setChart(ChartExportable chartView) {
        chart = chartView;
    }

    private ChartExportable getChart() {
        assert chart != null : "Chart not set";
        return chart;
    }

    public void setXFormatter(ChartBaseFormatter xFormatter) {
        this.xFormatter = xFormatter;
    }

    private ChartBaseFormatter getXFormatter() {
        assert xFormatter != null : "XFormatter not set";
        return xFormatter;
    }

    public static int getLastTypeUsed() {
        return lastTypeUsed;
    }

    public static void setLastTypeUsed(int lastTypeUsed) {
        ExportsBottomSheet.lastTypeUsed = lastTypeUsed;
    }

    public static int getLastSourceUsed() {
        return lastSourceUsed;
    }

    public static void setLastSourceUsed(int lastSourceUsed) {
        ExportsBottomSheet.lastSourceUsed = lastSourceUsed;
    }

    public static int getLastDestUsed() {
        return lastDestUsed;
    }

    public static void setLastDestUsed(int lastDestUsed) {
        ExportsBottomSheet.lastDestUsed = lastDestUsed;
    }

    public void setTitleTextAppearance(@StyleRes int textAppearanceTitle) {
        this.textAppearanceTitle = textAppearanceTitle;
        updateTitleTextAppearance();
    }

    public void setTxtTextAppearance(@StyleRes int textAppearanceTxt) {
        this.textAppearanceTxt = textAppearanceTxt;
        updateTxtTextAppearance();
    }

    public void setToggleTextAppearance(@StyleRes int textAppearanceTgl) {
        this.textAppearanceTgl = textAppearanceTgl;
        updateTglTextAppearance();
    }

    public void setBtnTextAppearance(@StyleRes int textAppearanceBtn) {
        this.textAppearanceBtn = textAppearanceBtn;
        updateBtnTextAppearance();
    }


    // UI

    private static void updateSourceButtonsList(MaterialButtonToggleGroup tglType, MaterialButtonToggleGroup tglSource, Context ctx) {
        boolean disable = getSelectedType(tglType) == R.id.btnTypeImage;
        tglSource.setEnabled(!disable);
        if (disable)
            tglSource.check(R.id.btnSourceChart);
        else
            tglSource.check(getLastSourceUsed());
    }

    private static @IdRes int getSelectedType(MaterialButtonToggleGroup tglType) {
        if (tglType.getCheckedButtonIds().isEmpty())
            return 0;
        return tglType.getCheckedButtonIds().get(0);    // only one button can be selected
    }

    private static @IdRes int getSelectedSource(MaterialButtonToggleGroup tglSource) {
        if (tglSource.getCheckedButtonIds().isEmpty())
            return 0;
        return tglSource.getCheckedButtonIds().get(0);    // only one button can be selected
    }

    private static @IdRes int getSelectedDest(MaterialButtonToggleGroup tglDest) {
        if (tglDest.getCheckedButtonIds().isEmpty())
            return 0;
        return tglDest.getCheckedButtonIds().get(0);    // only one button can be selected
    }

    private void updateTitleTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtTypeTitle.setTextAppearance(textAppearanceTitle);
            txtSourceTitle.setTextAppearance(textAppearanceTitle);
            txtDestTitle.setTextAppearance(textAppearanceTitle);
        }
    }

    private void updateTxtTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtTypeDescription.setTextAppearance(textAppearanceTxt);
            txtSourceDescription.setTextAppearance(textAppearanceTxt);
            txtDestDescription.setTextAppearance(textAppearanceTxt);
        }
    }

    private void updateTglTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < tglType.getChildCount(); i++)
                ((MaterialButton) tglType.getChildAt(i)).setTextAppearance(textAppearanceTgl);
            for (int i = 0; i < tglSource.getChildCount(); i++)
                ((MaterialButton) tglSource.getChildAt(i)).setTextAppearance(textAppearanceTgl);
        }
    }

    private void updateBtnTextAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnCancel.setTextAppearance(textAppearanceBtn);
            btnExport.setTextAppearance(textAppearanceBtn);
        }
    }

    private final MaterialButtonToggleGroup.OnButtonCheckedListener onTglTypeCheckedListener = new MaterialButtonToggleGroup.OnButtonCheckedListener() {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            updateSourceButtonsList(tglType, tglSource, getContext());
        }
    };

    private final View.OnClickListener onBtnCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private final View.OnClickListener onBtnExportClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selectedType = getSelectedType(tglType);
            lastTypeUsed = selectedType;
            int selectedSource = getSelectedSource(tglSource);
            lastSourceUsed = selectedSource;
            int selectedDest = getSelectedDest(tglDest);
            lastDestUsed = selectedDest;

            try {
                if (selectedType == R.id.btnTypeImage) {
                    // Get image from chart
                    Bitmap image = getChart().exportImg();
                    // Save or share image file
                    File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (selectedDest == R.id.btnDestFile) {
                        File savedFile = saveImgToFile(image, picturesDir, generateImgFileName("png"));
                        notifyFileReady(savedFile);
                    } else {
                        shareImg(currentContext, idFileProvider, image, picturesDir, generateImgFileName("png"), "png", titleShareImg);
                    }
                } else {
                    assert selectedType == R.id.btnTypeCSV || selectedType == R.id.btnTypeJSON : "Unknown type";

                    // Get data from chart
                    Map<String, DataSet<?>> data;
                    if (selectedSource == R.id.btnSourceChart) {
                        data = getChart().exportChartData();
                    } else {
                        assert selectedSource == R.id.btnSourceRAW : "Unknown source";
                        data = getChart().exportRAW();
                    }
                    // Convert data to desired file format
                    String extension;
                    String dataString;
                    if (selectedType == R.id.btnTypeCSV) {
                        extension = "csv";
                        dataString = toCSVFile(data, getXFormatter(), formatDate);
                    } else {
                        assert selectedType == R.id.btnTypeJSON : "Unknown type";
                        extension = "json";
                        dataString = toJSONFile(data, getXFormatter(), formatDate);
                    }
                    // Save or share data file
                    File documentsDir = getDocumentsDir(getContext());
                    if (selectedDest == R.id.btnDestFile) {
                        File savedFile = saveDataToFile(dataString, documentsDir, generateDataFileName(extension));
                        notifyFileReady(savedFile);
                    } else {
                        assert selectedDest == R.id.btnDestShare : "Unknown dest";
                        shareData(currentContext, idFileProvider, dataString, documentsDir, generateDataFileName(extension), extension, titleShareData);
                    }
                }
            } catch (IOException e) {
                Log.e("ExportsBottomSheet", String.format("Error exporting the file: %s", e.getMessage()), e);
                // TODO show an error dialog
            }

            dismiss();
        }
    };


    // File names generators

    private String generateImgFileName(String extension) {
        return String.format(formatFilenameImg, formatFilenameDate.format(new Date()), extension);
    }

    private String generateDataFileName(String extension) {
        return String.format(formatFilenameData, formatFilenameDate.format(new Date()), extension);
    }

    private File getDocumentsDir(Context context) {
        File documentsDir;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        else
            documentsDir = new File(Environment.getExternalStorageDirectory(), "Documents");

        documentsDir = new File(documentsDir, exportsDirName);

        if (!documentsDir.exists())
            if (!documentsDir.mkdirs()) {
                Log.e("ExportsBottomSheet", String.format("Error creating the directory '%s'", documentsDir));
                return null;
            }

        return documentsDir;
    }


    // File operations

    private static void shareImg(Context ctx, String idFileProvider, Bitmap image, File filePath, String fileName, String extension, String titleShare) throws IOException {
        File savedFile = saveImgToFile(image, filePath, fileName);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/" + extension);
        share.putExtra(Intent.EXTRA_STREAM, toUri(ctx, idFileProvider, savedFile));
        ctx.startActivity(Intent.createChooser(share, titleShare));
    }

    private static File saveImgToFile(Bitmap image, File filePath, String fileName) throws IOException {
        File file = new File(filePath, fileName);
        //noinspection IOStreamConstructor
        OutputStream fOut = new FileOutputStream(file);
        image.compress(IMG_COMPRESS_FORMAT, 100, fOut);
        fOut.flush();
        fOut.close();

        return file;
    }

    private static void shareData(Context ctx, String idFileProvider, String dataString, File filePath, String fileName, String extension, String titleShare) throws IOException {
        File savedFile = saveDataToFile(dataString, filePath, fileName);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("application/" + extension);
        share.putExtra(Intent.EXTRA_STREAM, toUri(ctx, idFileProvider, savedFile));
        ctx.startActivity(Intent.createChooser(share, titleShare));
    }

    private static File saveDataToFile(String dataString, File filePath, String fileName) throws IOException {
        File file = new File(filePath, fileName);
        //noinspection IOStreamConstructor
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(dataString.getBytes());
        outputStream.close();
        return file;
    }

    private static void notifyFileReady(File file) {
        // TODO show a notification to the user with the file path
    }

    private static Uri toUri(Context ctx, String idFileProvider, File savedFile) {
        return FileProvider.getUriForFile(ctx, idFileProvider, savedFile);
    }


    // File formats converters

    @SuppressLint("DefaultLocale")
    private static String toJSONFile(Map<String, DataSet<?>> data, ChartBaseFormatter xFormatter, SimpleDateFormat formatDate) {
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (Map.Entry<String, DataSet<?>> dataSetEntry : data.entrySet()) {
            String dataSetLabel = dataSetEntry.getKey();
            DataSet<?> dataSet = dataSetEntry.getValue();
            for (int i = 0; i < dataSet.getEntryCount(); i++) {
                Entry entry = dataSet.getEntryForIndex(i);
                Date date = ((ChartDateTimeFormatter)xFormatter).toDate(entry.getX());
                String dateStr = formatDate.format(date);
                float value = entry.getY();
                jsonBuilder.append(String.format(JSON_FORMAT, dateStr, dataSetLabel, value));
            }
        }
        String json = jsonBuilder.toString();
        if (json.endsWith(",\n"))
            json = json.substring(0, json.length() - 2);
        json += "]";
        return json;
    }

    @SuppressLint("DefaultLocale")
    private static String toCSVFile(Map<String, DataSet<?>> data, ChartBaseFormatter xFormatter, SimpleDateFormat formatDate) {
        StringBuilder csvBuilder = new StringBuilder(CSV_TITLE);
        for (Map.Entry<String, DataSet<?>> dataSetEntry : data.entrySet()) {
            String dataSetLabel = dataSetEntry.getKey();
            DataSet<?> dataSet = dataSetEntry.getValue();
            for (int i = 0; i < dataSet.getEntryCount(); i++) {
                Entry entry = dataSet.getEntryForIndex(i);
                Date date = ((ChartDateTimeFormatter)xFormatter).toDate(entry.getX());
                String dateStr = formatDate.format(date);
                float value = entry.getY();
                csvBuilder.append(String.format(CSV_FORMAT, dateStr, dataSetLabel, value));
            }
        }
        return csvBuilder.toString();
    }


}
