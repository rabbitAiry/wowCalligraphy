package com.airy.wowcalligraphy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CalligraphyUtils {
    private CalligraphyUtils() {
    }

    public static Bitmap getCalligraphyBitmap(String id, int idx, Context context) throws IOException {
        InputStream inputStream = context.getResources().getAssets().open(id+"_"+idx+".jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    public static List<Bitmap> getCalligraphyBitmapSet(CalligraphyTexts calligraphyText, Context context) throws IOException {
        List<Bitmap> list = new ArrayList<>();
        String id = calligraphyText.name();
        String text = calligraphyText.getPlainText();
        for (int i = 0; i < text.length(); i++) {
            InputStream inputStream;
            inputStream = context.getResources().getAssets().open(id+"_"+i+".jpg");
            list.add(BitmapFactory.decodeStream(inputStream));
            inputStream.close();
        }
        return list;
    }
}
