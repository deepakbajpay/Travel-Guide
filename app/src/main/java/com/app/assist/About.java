package com.app.assist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class About extends AppCompatActivity {
    ImageView i1;
    TextView t1;
    TextView t2;
    TextView t3;
    TextView t4;
    TextView t5;
    String versionName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        try {
            this.versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        this.t1 = (TextView) findViewById(R.id.textView1);
        this.t2 = (TextView) findViewById(R.id.textView2);
        this.t3 = (TextView) findViewById(R.id.textView3);
        this.t4 = (TextView) findViewById(R.id.textView4);
        this.t5 = (TextView) findViewById(R.id.textView5);
        this.t5.setText("Version " + this.versionName);
        this.i1 = (ImageView) findViewById(R.id.imageView1);


        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("About");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
