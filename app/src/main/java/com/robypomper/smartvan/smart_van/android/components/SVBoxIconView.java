package com.robypomper.smartvan.smart_van.android.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.remote.ObjInfo;
import com.robypomper.smartvan.smart_van.android.R;

import androidx.appcompat.widget.AppCompatTextView;

public class SVBoxIconView extends AppCompatTextView {

    public final char DEF_LETTER = '#';
    public final int DEF_BG_COLOR = getResources().getColor(R.color.smartvan_origin);

    private JSLRemoteObject remObj;
    private final ObjInfo.RemoteObjectInfoListener remObjInfoListener = new ObjInfo.RemoteObjectInfoListener() {
        @Override
        public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
            setCapitalLetter(newName.charAt(0));
        }

        @Override
        public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {

        }

        @Override
        public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {

        }

        @Override
        public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {

        }

        @Override
        public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {

        }

        @Override
        public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {

        }
    };


    public SVBoxIconView(Context context) {
        super(context);

        setGravity(Gravity.CENTER_HORIZONTAL);

        setCapitalLetter(DEF_LETTER);
        setIconBackgroundColor(DEF_BG_COLOR);
    }

    public SVBoxIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs); // used by the editor and inflater

        setGravity(Gravity.CENTER_HORIZONTAL);

        setCapitalLetter(DEF_LETTER);
        setIconBackgroundColor(DEF_BG_COLOR);
    }

    public SVBoxIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.CENTER_HORIZONTAL);

        setCapitalLetter(DEF_LETTER);
        setIconBackgroundColor(DEF_BG_COLOR);
    }


    public void setSVBox(JSLRemoteObject remObj) {
        if (this.remObj != null)
            this.remObj.getInfo().removeListener(remObjInfoListener);

        this.remObj = remObj;

        setCapitalLetter(' ');
        if (remObj != null) {
            setCapitalLetter(remObj.getName().charAt(0));
            remObj.getInfo().addListener(remObjInfoListener);
        }
    }

    private void setCapitalLetter(char letter) {
        setText(String.valueOf(letter));
    }

    public void setIconBackgroundColor(@ColorInt int color) {
        setBackground(drawCircle(color));
        setTextColor(getContrastColor(color));
    }



    public static int getContrastColor(int color) {
        double y = (double) (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    public static GradientDrawable drawCircle(int backgroundColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shape.setColor(backgroundColor);
        return shape;
    }

}
