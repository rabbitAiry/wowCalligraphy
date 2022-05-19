package com.airy.wowcalligraphy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airy.wowcalligraphy.databinding.ActivityCalligraphyBinding;
import com.airy.wowcalligraphy.util.CalligraphyUtils;
import com.airy.wowcalligraphy.util.CalligraphyTexts;
import com.airy.wowcalligraphy.util.DoneComments;

import java.io.IOException;
import java.util.List;

public class CalligraphyActivity extends AppCompatActivity {
    public static final String CALLIGRAPHY_ID = "CALLIGRAPHY_ID";
    private ActivityCalligraphyBinding binding;
    private CalligraphyTexts currCalligraphyText;
    private List<Bitmap> list;
    private int characterIdx = 0, targetId = 0;
    private float sumFilledRate = 0, sumOverFilledRate = 0;
    private float currFilledRate = 0, currOverFilledRate = 0;
    private int averCharacterWidth = 0, averCharacterHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalligraphyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        getCurrCalligraphySet(getIntent());
        updateCharacterView();

        binding.menuFinish.setEnabled(false);
        binding.calligraphyText.setText(currCalligraphyText.getSpannableText());
    }

    /**
     * 为Toolbar内容进行初始化，包括点击事件
     */
    private void setToolBar() {
        WindowInsetsControllerCompat compat = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if(compat!=null)compat.setAppearanceLightStatusBars(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();
        binding.menuNext.setOnClickListener(v -> nextCharacter());
        binding.menuFinish.setOnClickListener(v -> finishCalligraphyWork());
    }

    /**
     * 获取文字图片，并初始化图片列表
     * @param intent 来自MainActivity
     */
    private void getCurrCalligraphySet(Intent intent) {
        currCalligraphyText = CalligraphyTexts.valueOf(intent.getStringExtra(CALLIGRAPHY_ID));
        int[] target = currCalligraphyText.getTarget();
        characterIdx = target[0];
        new Thread(() -> {
            try {
                list = CalligraphyUtils.getCalligraphyBitmapSet(currCalligraphyText, CalligraphyActivity.this);
                getAverCharacterSize();
                for (int i = target.length - 1; i >= 0; i--) {
                    list.remove(target[i]);
                }
            } catch (IOException e) {
                Toast.makeText(CalligraphyActivity.this, "找不到图片", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).start();
    }

    /**
     * 获取每个字的平均长宽
     */
    private void getAverCharacterSize() {
        int width = 0, height = 0;
        for (int i = 0; i < list.size(); i++) {
            Bitmap curr = list.get(i);
            width += curr.getWidth();
            height += curr.getHeight();
        }
        averCharacterWidth = width/ list.size();
        averCharacterHeight = height/ list.size();
    }

    /**
     * UI上展示下一个字
     */
    private void updateCharacterView() {
        binding.calligraphyCurrCharacter.setText(String.valueOf(currCalligraphyText.getPlainText().charAt(characterIdx)));
        loadCharacter();
    }

    /**
     * 在CharacterDrawView上展示文字
     * 这是该Activity中配置CharacterDrawView的唯一入口
     */
    private void loadCharacter() {
        binding.calligraphyCurrCharacterDrawView.setEvaluateListener((filledRate, overFilledRate) -> {
            updateEvaluateRate(filledRate, overFilledRate);
            currFilledRate = filledRate;
            currOverFilledRate = overFilledRate;
        });
        binding.calligraphyCurrCharacterDrawView.post(() -> {
            try {
                binding.calligraphyCurrCharacterDrawView.setBitMapAsBackground(
                        CalligraphyUtils.getCalligraphyBitmap(currCalligraphyText.name(), characterIdx, CalligraphyActivity.this)
                );
            } catch (IOException e) {
                Toast.makeText(CalligraphyActivity.this, "找不到图片", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更新准确率和溢出率的值
     * @param filledRate    准确率
     * @param overFilledRate    溢出率
     */
    @SuppressLint("SetTextI18n")
    private void updateEvaluateRate(float filledRate, float overFilledRate) {
        binding.calligraphyFilledRateText.setText("准确率：" + (filledRate < 0 ? "N/A" : filledRate * 100 + "%"));
        binding.calligraphyOverfilledRateText.setText("溢出率：" + (overFilledRate < 0 ? "N/A" : overFilledRate * 100 + "%"));
    }

    /**
     * 点击按钮“下一个”时触发
     * 在CharacterDrawView上显示下一个字，
     * 并将当前写好的字加入到列表中，
     * 以及统计当前字的准确率和溢出率。
     * 如果准备写的是当前语句的最后一个字了，则发出Toast进行提醒
     */
    private void nextCharacter() {
        list.add(characterIdx, binding.calligraphyCurrCharacterDrawView.getDrawBitmap(averCharacterWidth, averCharacterHeight));
        sumFilledRate += currFilledRate;
        sumOverFilledRate += currOverFilledRate;
        int[] target = currCalligraphyText.getTarget();
        targetId++;
        characterIdx = target[targetId];
        updateCharacterView();
        if (targetId == target.length - 1) {
            binding.menuNext.setEnabled(false);
            binding.menuFinish.setEnabled(true);
            Toast.makeText(this, "就差最后一个字了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击按钮“大功告成！”时触发
     * 将最后写的字加入到列表中，然后将所有的字合并为一个bitmap，
     * 最后使用Dialog展示结果
     */
    public void finishCalligraphyWork() {
        list.add(characterIdx, binding.calligraphyCurrCharacterDrawView.getDrawBitmap(averCharacterWidth, averCharacterHeight));
        binding.menuFinish.setEnabled(false);
        new Thread(() -> {
            Bitmap result = concatCalligraphyBitmapSet();
            runOnUiThread(() -> setFinishDialog(result));
        }).start();
    }

    /**
     * 展示最终的bitmap以及评价
     * @param resultBitmap 合并后的bitmap
     */
    @SuppressLint("SetTextI18n")
    public void setFinishDialog(Bitmap resultBitmap) {
        sumFilledRate += currFilledRate;
        sumOverFilledRate += currOverFilledRate;
        int characterCnt = currCalligraphyText.getTarget().length;
        float averFilledPercentage = sumFilledRate / characterCnt * 100, averOverFilledPercentage = sumOverFilledRate / characterCnt * 100;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_finish_work, null);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView score = view.findViewById(R.id.dialog_score);
        TextView message = view.findViewById(R.id.dialog_message);
        ImageView image = view.findViewById(R.id.dialog_image);
        float finalScore = averFilledPercentage * 1.2f - averOverFilledPercentage * 0.5f;
        DoneComments comments;
        if (finalScore > 80) comments = DoneComments.PERFECT;
        else if (finalScore > 60) comments = DoneComments.GOOD;
        else if (finalScore > 40) comments = DoneComments.OK;
        else comments = DoneComments.BAD;
        title.setText(comments.getTitle());
        score.setText(finalScore + "分");
        message.setText(comments.getMessage());
        image.setImageBitmap(resultBitmap);
        dialog.setView(view).setPositiveButton("确认", (dialog1, which) ->
                CalligraphyActivity.this.finish()
        ).show();
    }

    /**
     * 合并所有字为一个bitmap
     *
     * @return 合并结果
     */
    private Bitmap concatCalligraphyBitmapSet() {
        if (list.size() == 0) return null;
        int resWidth = 0, maxHeight = 0;
        for (int i = 0; i < list.size(); i++) {
            Bitmap curr = list.get(i);
            resWidth += curr.getWidth();
            maxHeight = Math.max(curr.getHeight(), maxHeight);
        }
        Bitmap result = Bitmap.createBitmap(resWidth, maxHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        resWidth = 0;
        for (int i = 0; i < list.size(); i++) {
            Bitmap curr = list.get(i);
            canvas.drawBitmap(curr, (float) resWidth, (maxHeight - curr.getHeight()) >> 1, null);
            resWidth += curr.getWidth();
        }
        canvas.save();
        canvas.restore();
        return result;
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.calligraphyCurrCharacterDrawView.releaseAll();
        for(Bitmap curr: list){
            curr.recycle();
        }
    }
}