package com.airy.wowcalligraphy.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.List;
import java.util.Random;

public enum CalligraphyTexts {
    LanTing_KuaiRang("快然自足不知老之将至","快然自足，不知老之将至","兰亭集序","王羲之"),
    LanTing_TianLang( "天朗气清惠风和畅","天朗气清，惠风和畅","兰亭集序","王羲之"),
    LanTing_Yangguan( "仰观宇宙之大俯察品类之盛","仰观宇宙之大，俯察品类之盛","兰亭集序","王羲之"),
    LiSao_JiTi("既替余以蕙纕兮又申之以揽茝","既替余以蕙纕兮，又申之以揽茝","离骚经","米芾"),
    LiSao_YiYv("亦余心之所善兮虽九死其犹未悔","亦余心之所善兮，虽九死其犹未悔","离骚经","米芾"),
    ChiBi_ShiZao("是造物者之无尽藏也而吾与子之所共适","是造物者之无尽藏也，而吾与子之所共适","赤壁赋","苏轼"),
    ChiBi_ErDe("耳得之而为声目遇之而成色","耳得之而为声，目遇之而成色","赤壁赋","苏轼"),
    Chibi_QingFeng("清风徐来水波不兴","清风徐来，水波不兴","赤壁赋","苏轼");


    private final String plainText;
    private final String text;
    private final String title;
    private final String writer;
    CalligraphyTexts(String plainText, String text, String title, String writer){
        this.plainText = plainText;
        this.text = text;
        this.title = title;
        this.writer = writer;
    }

    public SpannableString getSpannableText(int[] target) {
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

    public String getTitle() {
        return title;
    }

    public String getWriter() {
        return writer;
    }

    /**
     * 随机获取3个不一样的数字并按照从小到大的顺序排列
     * @return 这3个数字的数组
     */
    public int[] getTarget() {
        int len = plainText.length();
        int size = Math.min(3, len);
        int[] target = new int[size];
        Random random = new Random();
        int last = 0;
        for(int i = 0; i<size; i++){
            target[i] = last + random.nextInt(len-last-size+i+1);
            last = target[i]+1;
        }
        return target;
    }
}
