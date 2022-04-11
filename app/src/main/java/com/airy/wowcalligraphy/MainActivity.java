package com.airy.wowcalligraphy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.airy.wowcalligraphy.adapter.CalligraphyAdapter;
import com.airy.wowcalligraphy.databinding.ActivityMainBinding;
import com.airy.wowcalligraphy.util.CalligraphyTexts;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CalligraphyAdapter adapter = new CalligraphyAdapter(id -> {
            Intent intent = new Intent(MainActivity.this, CalligraphyActivity.class);
            intent.putExtra(CalligraphyActivity.CALLIGRAPHY_ID, id);
            startActivity(intent);
        }, CalligraphyTexts.getAllCalligraphy());
        binding.mainTextList.setAdapter(adapter);
        binding.mainTextList.setLayoutManager(new LinearLayoutManager(this));
    }
}