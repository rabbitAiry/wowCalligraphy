package com.airy.wowcalligraphy;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import com.airy.wowcalligraphy.adapter.CalligraphyAdapter;
import com.airy.wowcalligraphy.databinding.ActivityMainBinding;
import com.airy.wowcalligraphy.util.CalligraphyTexts;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setToolBar();

        CalligraphyAdapter adapter = new CalligraphyAdapter(id -> {
            Intent intent = new Intent(MainActivity.this, CalligraphyActivity.class);
            intent.putExtra(CalligraphyActivity.CALLIGRAPHY_ID, id);
            startActivity(intent);
        }, CalligraphyTexts.values());
        binding.mainTextList.setAdapter(adapter);
        binding.mainTextList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setToolBar() {
        WindowInsetsControllerCompat compat = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if(compat!=null)compat.setAppearanceLightStatusBars(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>", FROM_HTML_MODE_LEGACY));
    }
}