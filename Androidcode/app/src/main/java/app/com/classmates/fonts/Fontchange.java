package app.com.classmates.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by brightroots on 16/3/16.
 */
@SuppressWarnings("deprecation")
public class Fontchange {

    public static void overrideFonts(Context context, View v) {

        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {

                if (((TextView) v).getTypeface() != null && ((TextView) v).getTypeface().isBold()) {
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Ubuntu-C.ttf"));
                } else {
                    ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "Ubuntu-L.ttf"));
                }
            }
        } catch (Exception e) {
        }

    }

}
