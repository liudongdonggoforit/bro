package com.sh.browser.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.sh.browser.R;
import com.sh.browser.fragment.Fragment_settings;
import com.sh.browser.utils.HelperUnit;
import com.sh.browser.utils.IntentUnit;

public class Settings_Activity extends AppCompatActivity {
    private Fragment_settings fragment;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        HelperUnit.setTheme(this);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = new Fragment_settings();
        getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getInt("restart_changed", 0) == 1) {
            sp.edit().putInt("restart_changed", 0).apply();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                IntentUnit.setDBChange(fragment.isDBChange());
                IntentUnit.setSPChange(fragment.isSPChange());
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            IntentUnit.setDBChange(fragment.isDBChange());
            IntentUnit.setSPChange(fragment.isSPChange());
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentUnit.REQUEST_CLEAR) {
            if (resultCode == Activity.RESULT_OK && data != null && data.hasExtra(Settings_ClearActivity.DB_CHANGE)) {
                fragment.setDBChange(data.getBooleanExtra(Settings_ClearActivity.DB_CHANGE, false));
            }
        }
    }
}
