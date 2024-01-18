package com.robypomper.josp.jsl.android.components.charts.formatters;

import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPStatusHistory;

/**
 * e necessaria, o la conversione da JOSPHistoryStatus si puo fare all'occorrenza (p.e. in LineChartView.ChartViewAdapterJSL)
 * TODO Check and reformat current class
 *
 * @noinspection unused
 */
public class JSLUnitFormatter extends ChartUnitFormatter {

    // Constructors

    public JSLUnitFormatter(float scale) {
        super(scale);
    }


    // SVBaseFormatter re-implementation over SVUnitFormatter

    @Override
    public float from(Object obj) {
        // If JSLRangeState, replace obj with JSLRangeState's newState
        if (obj instanceof JOSPStatusHistory) {
            JOSPStatusHistory history = (JOSPStatusHistory) obj;
            try {
                obj = (float) payloadToValue(history.getPayload());
            } catch (Throwable e) {
                throw new IllegalArgumentException(String.format("JOSPStatusHistory badly formatted, got '%s' payload", history.getPayload()), e);
            }
        }

        return super.from(obj);
    }

    public static String valueToPayload(double newValue, double oldValue) {
        String payload = "";
        payload += "newState=" + newValue + ",";
        payload += "oldState=" + oldValue;
        return payload;
    }

    public static double payloadToValue(String historyStatusPayload) {
        JSLRangeState.JOSPRange range = new JSLRangeState.JOSPRange(historyStatusPayload);
        return range.newState;
    }

    public static double tryPayloadToValue(String historyStatusPayload, double defaultValue) {
        try {
            return payloadToValue(historyStatusPayload);
        } catch (Throwable ignore) {
        }
        return defaultValue;
    }

}
