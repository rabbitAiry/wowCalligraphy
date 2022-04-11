package com.airy.wowcalligraphy.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.List;

public enum CalligraphyTexts {
    LanTing_KuaiRang(10, "快然自足不知老之将至","快然自足，不知老之将至", new int[]{1,8}),
    LanTing_TianLang(8, "天朗气清惠风和畅","天朗气清，惠风和畅", new int[]{0,1,3}),
    LanTing_Yangguan(12, "仰观宇宙之大俯察品类之盛","仰观宇宙之大，俯察品类之盛", new int[]{5,7,11});

    private final int size;
    private final String plainText;
    private final String text;
    private final int[] target;
    private CalligraphyTexts(int size, String plainText, String text, int[] target){
        this.size = size;
        this.plainText = plainText;
        this.text = text;
        this.target = target;
    }

    public int getSize() {
        return size;
    }

    public SpannableString getText() {
        SpannableString spannableString = new SpannableString(text);
        for (int val : target) {
            spannableString.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#A38735")),
                    val,
                    val+1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return spannableString;
    }

    public String getPlainText() {
        return plainText;
    }

    public int[] getTarget() {
        return target;
    }

    public static CalligraphyTexts[] getAllCalligraphy(){
        return new CalligraphyTexts[]{LanTing_KuaiRang, LanTing_TianLang, LanTing_Yangguan};
    }

    public static CalligraphyTexts getCalligraphy(String id){
        CalligraphyTexts[] arr = getAllCalligraphy();
        for (CalligraphyTexts calligraphyTexts : arr) {
            if (calligraphyTexts.name().equals(id)) return calligraphyTexts;
        }
        return null;
    }
}
