package com.airy.wowcalligraphy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
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
    private int baseCharacterWidth = 0, baseCharacterHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalligraphyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();
        getCurrCalligraphySet(getIntent());
        updateCharacterView();

        binding.menuFinish.setEnabled(false);
        binding.calligraphyText.setText(currCalligraphyText.getText());
    }

    private void setToolBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)actionBar.hide();
        binding.menuNext.setOnClickListener(v->nextCharacter());
        binding.menuFinish.setOnClickListener(v->finishCalligraphyWork());
    }

    private void updateCharacterView() {
        binding.calligraphyCurrCharacter.setText(String.valueOf(currCalligraphyText.getPlainText().charAt(characterIdx)));
        loadCalligraphy();
    }

    private void loadCalligraphy() {
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

    @SuppressLint("SetTextI18n")
    private void updateEvaluateRate(float filledRate, float overFilledRate) {
        binding.calligraphyFilledRateText.setText("准确率：" + (filledRate < 0 ? "N/A" : filledRate * 100 + "%"));
        binding.calligraphyOverfilledRateText.setText("溢出率：" + (overFilledRate < 0 ? "N/A" : overFilledRate * 100 + "%"));
    }

    private void getCurrCalligraphySet(Intent intent) {
        currCalligraphyText = CalligraphyTexts.getCalligraphy(intent.getStringExtra(CALLIGRAPHY_ID));
        int[] target = currCalligraphyText.getTarget();
        characterIdx = target[0];
        new Thread(() -> {
            try {
                list = CalligraphyUtils.getCalligraphyBitmapSet(currCalligraphyText, CalligraphyActivity.this);
                baseCharacterWidth = list.get(0).getWidth();
                baseCharacterHeight = list.get(0).getHeight();
                for (int i = target.length-1; i >= 0; i--) {
                    list.remove(target[i]);
                }
            } catch (IOException e) {
                Toast.makeText(CalligraphyActivity.this, "找不到图片", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void nextCharacter() {
        list.add(characterIdx, binding.calligraphyCurrCharacterDrawView.getDrawBitmap(baseCharacterWidth, baseCharacterHeight));
        sumFilledRate += currFilledRate;
        sumOverFilledRate += currOverFilledRate;
        int[] target = currCalligraphyText.getTarget();
        targetId++;
        characterIdx = target[targetId];
        updateCharacterView();
        if(targetId==target.length-1){
            binding.menuNext.setEnabled(false);
            binding.menuFinish.setEnabled(true);
            Toast.makeText(this, "就差最后一个字了", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    public void setFinishDialog(Bitmap resultBitmap){
        sumFilledRate += currFilledRate;
        sumOverFilledRate += currOverFilledRate;
        int characterCnt = currCalligraphyText.getTarget().length;
        float averFilledPercentage = sumFilledRate/characterCnt*100, averOverFilledPercentage = sumOverFilledRate/characterCnt*100;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_finish_work, null);
        TextView title = view.findViewById(R.id.dialog_title);
        TextView score = view.findViewById(R.id.dialog_score);
        TextView message = view.findViewById(R.id.dialog_message);
        ImageView image = view.findViewById(R.id.dialog_image);
        float finalScore = averFilledPercentage*1.2f-averOverFilledPercentage*0.5f;
        DoneComments comments;
        if(finalScore>80)comments = DoneComments.PERFECT;
        else if(finalScore>60)comments = DoneComments.GOOD;
        else if(finalScore>40)comments = DoneComments.OK;
        else comments = DoneComments.BAD;
        title.setText(comments.getTitle());
        score.setText(finalScore+"分");
        message.setText(comments.getMessage());
        image.setImageBitmap(resultBitmap);
        dialog.setView(view).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CalligraphyActivity.this.finish();
            }
        }).show();
    }

    public void finishCalligraphyWork(){
        list.add(characterIdx, binding.calligraphyCurrCharacterDrawView.getDrawBitmap(baseCharacterWidth, baseCharacterHeight));
        binding.menuFinish.setEnabled(false);
        new Thread(() -> {
            Bitmap result = concatCalligraphyBitmapSet();
            runOnUiThread(() -> setFinishDialog(result));
        }).start();
    }

    private Bitmap concatCalligraphyBitmapSet() {
        if(list.size()==0)return null;
        int resWidth = 0;
        for (int i = 0; i < list.size(); i++) {
            resWidth += list.get(i).getWidth();
        }
        Bitmap result = Bitmap.createBitmap(resWidth, baseCharacterHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        resWidth = 0;
        for (int i = 0; i < list.size(); i++) {
            Bitmap curr = list.get(i);
            canvas.drawBitmap(curr, (float)resWidth, 0f, null);
            resWidth += curr.getWidth();
        }
        canvas.save();
        canvas.restore();
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.calligraphyCurrCharacterDrawView.releaseAll();
    }
}