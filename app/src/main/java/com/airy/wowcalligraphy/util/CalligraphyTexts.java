package com.airy.wowcalligraphy.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.List;

public enum CalligraphyTexts {
    LanTing_KuaiRang("快然自足不知老之将至","快然自足，不知老之将至", new int[]{1,8}),
    LanTing_TianLang( "天朗气清惠风和畅","天朗气清，惠风和畅", new int[]{0,1,3}),
    LanTing_Yangguan( "仰观宇宙之大俯察品类之盛","仰观宇宙之大，俯察品类之盛", new int[]{5,7,11});

    private final String plainText;
    private final String text;
    private final int[] target;     // for plainText
    CalligraphyTexts(String plainText, String text, int[] target){
        this.plainText = plainText;
        this.text = text;
        this.target = target;
    }

    public SpannableString getSpannableText() {
        SpannableString spannableString = new SpannableString(text);
        char[] arr = text.toCharArray();
        int commaCnt = 0, targetIdx = 0;
        for (int i = 0; i <text.length(); i++) {
            if(arr[i]==','||arr[i]=='，'){
                commaCnt++;
                continue;
            }
            if(i-commaCnt==target[targetIdx]){
                spannableString.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#A38735")),
                        i,
                        i+1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                if(targetIdx<target.length-1)targetIdx++;
                else break;
            }
        }
        return spannableString;
    }

    public String getText(){return text;}

    public String getPlainText() {
        return plainText;
    }

    public int[] getTarget() {
        return target;
    }
}
