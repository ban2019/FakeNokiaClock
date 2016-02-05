package io.github.xialincn.fakenokiaclockdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.github.xialincn.fakenokiaclock.views.NokiaClockView;
import io.github.xialincn.fakenokiaclock.views.OnTimeUpdateListener;


/**
 * Created by lin on 2016/1/18.
 */
public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    private NokiaClockView mClockView;
    private int mHour, mMinute;

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClockView = (NokiaClockView) findViewById(R.id.nokia_clock);

        mClockView.setOnHourUpdateListener(new OnTimeUpdateListener() {
            @Override
            public void onTimeUpdate(int value) {
                mHour = value;
                updateTimeDisplay();
            }
        });
        mClockView.setOnMinuteUpdateListener(new OnTimeUpdateListener() {
            @Override
            public void onTimeUpdate(int value) {
                mMinute = value;
                updateTimeDisplay();
            }
        });

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = 500;
                Snackbar.make(findViewById(R.id.coordinate_layout), R.string.snack_bar_info, duration)
                        .show();
            }
        });
    }

    private void updateTimeDisplay() {
        ((TextView) findViewById(R.id.time)).setText(mHour + ":" + mMinute + mClockView.getPeriod());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
