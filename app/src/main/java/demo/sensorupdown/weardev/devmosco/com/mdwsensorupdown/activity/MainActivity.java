package demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.activity;



import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.R;
import demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.adapter.PagerAdapter;
import demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.fragments.CounterFragment;
import demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.fragments.SettingsFragment;
import demo.sensorupdown.weardev.devmosco.com.mdwsensorupdown.utils.Utils;

/**
 * Created by joseberna on 24/11/15.
 */
public class MainActivity extends Activity  implements SensorEventListener {

    private static final String TAG = "JJMainActivity";

    /** Tiempo de detección de actividad en la pantalla (Milisegundos) **/
    private static final long SCREEN_ON_TIMEOUT_MS = 20000;

    /** Defdine el tiempo de detección de movimientos Arriba/Abajo (nanosegundos = 2 seg) **/
    private static final long TIME_THRESHOLD_NS = 2000000000;

    /** Variable que define nuestro punto de gravedad **/
    private static final float GRAVITY_THRESHOLD = 7.0f;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long mLastTime = 0;
    private boolean mUp = false;
    private int mJumpCounter = 0;
    private ViewPager mPager;
    private CounterFragment mCounterPage;
    private SettingsFragment mSettingPage;
    private ImageView mSecondIndicator;
    private ImageView mFirstIndicator;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jj_layout);
        setupViews();
        mHandler = new Handler();
        mJumpCounter = Utils.getCounterFromPreference(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        renewTimer();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    private void setupViews() {
        mPager = (ViewPager) findViewById(R.id.pager);
        mFirstIndicator = (ImageView) findViewById(R.id.indicator_0);
        mSecondIndicator = (ImageView) findViewById(R.id.indicator_1);
        final PagerAdapter adapter = new PagerAdapter(getFragmentManager());
        mCounterPage = new CounterFragment();
        mSettingPage = new SettingsFragment();
        adapter.addFragment(mCounterPage);
        adapter.addFragment(mSettingPage);
        setIndicator(0);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                setIndicator(i);
                renewTimer();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL)) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Successfully registered for the sensor updates");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Unregistered for sensor events");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        detectJump(event.values[0], event.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /** Algortimo que define cúando hay un movimiento Arriba/Abajo, dependiendo de nuestro puento de gravedad **/
    private void detectJump(float xValue, long timestamp) {
        if ((Math.abs(xValue) > GRAVITY_THRESHOLD)) {
            if(timestamp - mLastTime < TIME_THRESHOLD_NS && mUp != (xValue > 0)) {
                onJumpDetected(!mUp);
            }
            mUp = xValue > 0;
            mLastTime = timestamp;
        }
    }

    /** Método encargado de detectar cuando hay un ejercicio físico, Arriba/Abajo **/
    private void onJumpDetected(boolean up) {
        // we only count a pair of up and down as one successful movement
        if (up) {
            return;
        }
        mJumpCounter++;
        setCounter(mJumpCounter);
        renewTimer();
    }


    private void setCounter(int i) {
        mCounterPage.setCounter(i);
        Utils.saveCounterToPreference(this, i);
        if (i > 0 && i % 10 == 0) {
            Utils.vibrate(this, 0);
        }
    }

    public void resetCounter() {
        setCounter(0);
        renewTimer();
    }


    private void renewTimer() {
        if (null != mTimer) {
            mTimer.cancel();
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG,
                            "Removing the FLAG_KEEP_SCREEN_ON flag to allow going to background");
                }
                resetFlag();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, SCREEN_ON_TIMEOUT_MS);
    }


    private void resetFlag() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Resetting FLAG_KEEP_SCREEN_ON flag to allow going to background");
                }
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                finish();
            }
        });
    }


    private void setIndicator(int i) {
        switch (i) {
            case 0:
                mFirstIndicator.setImageResource(R.drawable.full_10);
                mSecondIndicator.setImageResource(R.drawable.empty_10);
                break;
            case 1:
                mFirstIndicator.setImageResource(R.drawable.empty_10);
                mSecondIndicator.setImageResource(R.drawable.full_10);
                break;
        }
    }


}
