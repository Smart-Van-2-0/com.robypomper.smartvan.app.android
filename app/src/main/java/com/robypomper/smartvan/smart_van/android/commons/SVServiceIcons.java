package com.robypomper.smartvan.smart_van.android.commons;

import androidx.annotation.DrawableRes;

import com.robypomper.smartvan.smart_van.android.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Utility class for SV Services' icons.
 * <p>
 * This class provides methods to convert icons from string to drawable resource
 * and vice versa.
 * @noinspection unused
 */
public class SVServiceIcons {

    // Constants

    public static final String DEF_ICON_TXT = "Light";
    public static final int DEF_ICON_RES = R.drawable.ic_srv_light;


    // Static vars

    private static final Map<String, Integer> icons = new HashMap<>();

    static {
        // Names must be unique!!
        icons.put("Light", R.drawable.ic_srv_light);

        icons.put("Lamp", R.drawable.ic_srv_lamp);
        icons.put("Lamp Floor", R.drawable.ic_srv_lamp_floor);
        icons.put("Lamp Multiple", R.drawable.ic_srv_lamp_multi);
        icons.put("Lamp Table", R.drawable.ic_srv_lamp_table);
        icons.put("Lamp Wall", R.drawable.ic_srv_lamp_wall);
        icons.put("Fan", R.drawable.ic_srv_fan);
        icons.put("Shower", R.drawable.ic_srv_shower);
        icons.put("Heating", R.drawable.ic_srv_heating);
        icons.put("water Pump", R.drawable.ic_srv_water_pump);
        icons.put("Faucet", R.drawable.ic_srv_faucet);
        icons.put("Shutter", R.drawable.ic_srv_shutter);
        icons.put("Blinds", R.drawable.ic_srv_blinds);

        icons.put("Area Kitchen", R.drawable.ic_srv_area_kitchen2);
        icons.put("Area Bed", R.drawable.ic_srv_area_bed);
        icons.put("Area Read", R.drawable.ic_srv_area_read);

        icons.put("Scene Day", R.drawable.ic_srv_scene_day);
        icons.put("Scene Sunlight", R.drawable.ic_srv_scene_sunlight);
        icons.put("Scene Night", R.drawable.ic_srv_scene_night);
    }


    // Getters

    public static Map<String, Integer> getIcons() {
        return icons;
    }

    public static Set<String> getIconNames() {
        return icons.keySet();
    }

    public static Collection<Integer> getIconRes() {
        return icons.values();
    }


    // Conversion methods

    public static String iconRes2String(@DrawableRes int icon) {
        for (Map.Entry<String, Integer> entry : icons.entrySet())
            if (entry.getValue() == icon)
                return entry.getKey();
        return DEF_ICON_TXT;
    }

    public static @DrawableRes int iconString2Res(String iconTxt) {
        Integer icon = icons.get(iconTxt);
        return icon != null ? icon : DEF_ICON_RES;
    }

}
