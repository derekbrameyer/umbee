package com.doomonafireball.umbee.activity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doomonafireball.umbee.MainApp;
import com.doomonafireball.umbee.R;
import com.doomonafireball.umbee.query.WeatherQuery;
import com.doomonafireball.umbee.receiver.NotificationReceiver;
import com.doomonafireball.umbee.util.Refs;
import com.doomonafireball.umbee.util.SharedPrefsManager;
import com.doomonafireball.umbee.util.UmbeeTextUtils;
import com.doomonafireball.umbee.util.UmbeeTimeUtils;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import oak.CancelEditText;
import roboguice.inject.InjectView;

public class StartupActivity extends RoboSherlockFragmentActivity {

    @InjectView(R.id.CB_enable_umbee) CheckBox enableUmbeeCB;
    @InjectView(R.id.CB_enable_smart_location) CheckBox enableLocationUpdatesCB;
    @InjectView(R.id.CB_custom_threshold) CheckBox customThresholdCB;
    @InjectView(R.id.ET_location) CancelEditText locationET;
    @InjectView(R.id.BTN_advanced_options) Button advancedOptionsBTN;
    @InjectView(R.id.BTN_update_time) Button updateTimeBTN;
    @InjectView(R.id.BTN_alert_type) Button alertTypeBTN;
    @InjectView(R.id.BTN_test_notif) Button testNotificationBTN;
    //@InjectView(R.id.TV_alert_example_big) TextView alertExampleBigTV;
    //@InjectView(R.id.TV_alert_example_small) TextView alertExampleSmallTV;
    //@InjectView(R.id.TV_alert_example_small2) TextView alertExampleSmall2TV;
    @InjectView(R.id.TV_single_threshold) TextView singleThresholdTV;
    @InjectView(R.id.TV_triple_threshold) TextView tripleThresholdTV;
    @InjectView(R.id.TV_triple_threshold2) TextView tripleThreshold2TV;
    @InjectView(R.id.TV_triple_threshold3) TextView tripleThreshold3TV;
    @InjectView(R.id.SB_single_threshold) SeekBar singleThresholdSB;
    @InjectView(R.id.SB_triple_threshold_1) SeekBar tripleThreshold1SB;
    @InjectView(R.id.SB_triple_threshold_2) SeekBar tripleThreshold2SB;
    @InjectView(R.id.SB_triple_threshold_3) SeekBar tripleThreshold3SB;
    @InjectView(R.id.LL_single_threshold_container) LinearLayout singleThresholdLL;
    @InjectView(R.id.LL_triple_threshold_container) LinearLayout tripleThresholdLL;
    @InjectView(R.id.LL_advanced_options_container) LinearLayout advancedOptionsContainerLL;
    @InjectView(R.id.RL_threshold_container) RelativeLayout thresholdCheckRL;
    @InjectView(R.id.FL_todays_precip_container) FrameLayout todaysPrecipContainerFL;

    SharedPrefsManager mSharedPrefs;
    Context mContext;
    ArrayAdapter<CharSequence> weatherApiAdapter;
    ArrayAdapter<CharSequence> alertTypeAdapter;
    String[] weatherApiOptions;
    String[] alertTypeOptions;

    private int bar1Progress;
    private int bar2Progress;
    private int bar3Progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(MainApp.TAG, "onCreate");
        setContentView(R.layout.startup);

        mContext = this;
        mSharedPrefs.initialize(this);
        mSharedPrefs = SharedPrefsManager.getInstance();

        alertTypeAdapter = ArrayAdapter.createFromResource(this, R.array.alert_type_array,
                android.R.layout.simple_dropdown_item_1line);
        alertTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        alertTypeOptions = getResources().getStringArray(R.array.alert_type_array);

        enableUmbeeCB.setOnCheckedChangeListener(enableCCL);
        customThresholdCB.setOnCheckedChangeListener(customThresholdCCL);
        enableLocationUpdatesCB.setOnCheckedChangeListener(enableLocationUpdatesCCL);
        advancedOptionsBTN.setOnClickListener(advancedOptionsCL);
        updateTimeBTN.setOnClickListener(updateTimeCL);
        alertTypeBTN.setOnClickListener(alertTypeCL);
        testNotificationBTN.setOnClickListener(testNotificationCL);
        singleThresholdSB.setOnSeekBarChangeListener(singleSBCL);
        tripleThreshold1SB.setOnSeekBarChangeListener(triple1SBCL);
        tripleThreshold2SB.setOnSeekBarChangeListener(triple2SBCL);
        tripleThreshold3SB.setOnSeekBarChangeListener(triple3SBCL);
        locationET.setOnEditorActionListener(locationETListener);

        boolean b = mSharedPrefs.getAdvancedOptions();
        if (b) {
            advancedOptionsContainerLL.setVisibility(View.VISIBLE);
        } else {
            advancedOptionsContainerLL.setVisibility(View.GONE);
        }

        setUpPrecipViews();
        setUpPreferenceViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.startup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                // TODO Launch AboutActivity
                return false;
            case R.id.menu_refresh:
                // TODO Refresh current stuff
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    SeekBar.OnSeekBarChangeListener singleSBCL = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            String s = String
                    .format(getResources().getString(R.string.dynamic_single_threshold), i);
            singleThresholdTV.setText(s);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mSharedPrefs.setSingleThreshold(seekBar.getProgress());
        }
    };

    SeekBar.OnSeekBarChangeListener triple1SBCL = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            String s;
            if (i > bar2Progress) {
                seekBar.setProgress(bar2Progress);
                s = String.format(getResources().getString(R.string.dynamic_triple_threshold1),
                        bar2Progress, bar2Progress);
            } else {
                s = String.format(getResources().getString(R.string.dynamic_triple_threshold1), i,
                        bar2Progress);
            }
            tripleThreshold2SB.setSecondaryProgress(seekBar.getProgress());
            tripleThresholdTV.setText(s);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            bar2Progress = mSharedPrefs.getTripleThreshold2();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mSharedPrefs.setTripleThreshold1(seekBar.getProgress());
        }
    };

    SeekBar.OnSeekBarChangeListener triple2SBCL = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            String s;
            String s2;
            if (i > bar3Progress) {
                seekBar.setProgress(bar3Progress);
                s = String.format(getResources().getString(R.string.dynamic_triple_threshold1),
                        bar1Progress, bar3Progress);
                s2 = String.format(getResources().getString(R.string.dynamic_triple_threshold2),
                        bar3Progress, bar3Progress);
            } else if (i < bar1Progress) {
                seekBar.setProgress(bar1Progress);
                s = String.format(getResources().getString(R.string.dynamic_triple_threshold1),
                        bar1Progress, bar1Progress);
                s2 = String.format(getResources().getString(R.string.dynamic_triple_threshold2),
                        bar1Progress, bar3Progress);
            } else {
                s = String.format(getResources().getString(R.string.dynamic_triple_threshold1),
                        bar1Progress, i);
                s2 = String.format(getResources().getString(R.string.dynamic_triple_threshold2), i,
                        bar3Progress);
            }
            tripleThreshold3SB.setSecondaryProgress(seekBar.getProgress());
            tripleThresholdTV.setText(s);
            tripleThreshold2TV.setText(s2);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            bar1Progress = mSharedPrefs.getTripleThreshold1();
            bar3Progress = mSharedPrefs.getTripleThreshold3();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mSharedPrefs.setTripleThreshold2(seekBar.getProgress());
        }
    };

    SeekBar.OnSeekBarChangeListener triple3SBCL = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            String s;
            String s2;
            if (i < bar2Progress) {
                seekBar.setProgress(bar2Progress);
                s = String.format(getResources().getString(R.string.dynamic_triple_threshold2),
                        bar2Progress, bar2Progress);
                s2 = String.format(getResources().getString(R.string.dynamic_triple_threshold3),
                        bar2Progress);
            } else {
                s = String.format(getResources().getString(R.string.dynamic_triple_threshold2),
                        bar2Progress, i);
                s2 = String.format(getResources().getString(R.string.dynamic_triple_threshold3), i);
            }
            tripleThreshold2TV.setText(s);
            tripleThreshold3TV.setText(s2);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            bar2Progress = mSharedPrefs.getTripleThreshold2();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mSharedPrefs.setTripleThreshold3(seekBar.getProgress());
        }
    };

    CompoundButton.OnCheckedChangeListener enableCCL
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mSharedPrefs.setEnabled(b);
            if (b) {
                startUmbeeService();
            } else {
                cancelUmbeeService();
            }
            setUpPreferenceViews();
        }
    };

    CompoundButton.OnCheckedChangeListener customThresholdCCL
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mSharedPrefs.setCustomThreshold(b);
            setUpThresholdViews();
        }
    };

    CompoundButton.OnCheckedChangeListener enableLocationUpdatesCCL
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mSharedPrefs.setEnableLocationUpdates(b);
            setUpThresholdViews();
        }
    };

    TextView.OnEditorActionListener locationETListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String loc = locationET.getText().toString();
                if (UmbeeTextUtils.isValidZipCode(loc)) {
                    mSharedPrefs.setLocation(loc);
                    InputMethodManager mgr = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(locationET.getWindowToken(), 0);
                    Toast.makeText(mContext, "Zip code saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Not a valid zip code.", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    };

    View.OnClickListener testNotificationCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WeatherQuery testNotifQuery = new WeatherQuery(mContext, true, true, new Handler());
            testNotifQuery.execute();
        }
    };

    View.OnClickListener advancedOptionsCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean b = mSharedPrefs.getAdvancedOptions();
            mSharedPrefs.setAdvancedOptions(!b);
            if (b) {
                advancedOptionsContainerLL.setVisibility(View.GONE);
            } else {
                advancedOptionsContainerLL.setVisibility(View.VISIBLE);
            }
        }
    };

    View.OnClickListener updateTimeCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TimePickerDialog dialog = new TimePickerDialog(mContext,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            mSharedPrefs.setUpdateTime(
                                    UmbeeTimeUtils.timeOfDayFromTimePicker(hourOfDay, minute));
                            updateTimeBTN.setText(
                                    UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));
                            startUmbeeService();
                        }
                    },
                    UmbeeTimeUtils.hourOfDayFromInt(mSharedPrefs.getUpdateTime()),
                    UmbeeTimeUtils.minuteFromInt(mSharedPrefs.getUpdateTime()),
                    false);
            dialog.show();
        }
    };

    View.OnClickListener alertTypeCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setSingleChoiceItems(alertTypeAdapter, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int selectedAlertType = i + Refs.ALERT_BASE;
                            mSharedPrefs.setAlertType(selectedAlertType);
                            alertTypeBTN.setText(alertTypeOptions[i]);
                            dialogInterface.dismiss();
                            setUpPreferenceViews();
                            setUpThresholdViews();
                        }
                    });
            builder.setTitle("Alert Type");
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private void cancelUmbeeService() {
        Intent notif = new Intent(mContext, NotificationReceiver.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, notif,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        Log.d(MainApp.TAG, "Cancelled AlarmManager alarm.");
    }

    private void startUmbeeService() {
        String s = "";
        Calendar currCal = new GregorianCalendar();
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY,
                UmbeeTimeUtils.hourOfDayFromInt(mSharedPrefs.getUpdateTime()));
        cal.set(Calendar.MINUTE, UmbeeTimeUtils.minuteFromInt(mSharedPrefs.getUpdateTime()));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (currCal.after(cal)) {
            // Increment the day
            cal.add(Calendar.DAY_OF_MONTH, 1);
            s = String.format(getResources().getString(R.string.dynamic_umbee_is_set_for),
                    "tomorrow",
                    UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));
        } else {
            s = String.format(getResources().getString(R.string.dynamic_umbee_is_set_for), "today",
                    UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));
        }

        Intent notif = new Intent(mContext, NotificationReceiver.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, notif,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                pi);
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        Log.d(MainApp.TAG, "Set alarmManager.setRepeating to: " + cal.getTime().toLocaleString());
    }

    private void setUpPrecipViews() {
        if ((mSharedPrefs.getNoaaEveningPrecip() == -1)
                || (mSharedPrefs.getNoaaMorningPrecip() == -1)) {
            // We haven't gotten any info yet.
            WeatherQuery getWeatherQuery = new WeatherQuery(mContext, true, true, new Handler() {
                public void HandleMessage(Message msg) {
                    setUpPrecipViews();
                }
            });
            getWeatherQuery.execute();
        } else {
            LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View precipNoaaVW = li.inflate(R.layout.precip_noaa, null);
            TextView morningPrecipTV = (TextView) precipNoaaVW.findViewById(R.id.TV_morning_precip);
            TextView eveningPrecipTV = (TextView) precipNoaaVW.findViewById(R.id.TV_evening_precip);
            morningPrecipTV.setText(String.format(getResources().getString(R.string.dynamic_int_percentage), mSharedPrefs.getNoaaMorningPrecip()));
            eveningPrecipTV.setText(String.format(getResources().getString(R.string.dynamic_int_percentage), mSharedPrefs.getNoaaEveningPrecip()));
            todaysPrecipContainerFL.removeAllViews();
            todaysPrecipContainerFL.addView(precipNoaaVW);
        }
    }

    private void setUpPreferenceViews() {
        boolean isCustomThreshold = mSharedPrefs.getCustomThreshold();
        customThresholdCB.setChecked(isCustomThreshold);

        boolean isUmbeeEnabled = mSharedPrefs.getEnabled();
        enableUmbeeCB.setChecked(isUmbeeEnabled);
        locationET.setEnabled(isUmbeeEnabled);
        updateTimeBTN.setEnabled(isUmbeeEnabled);
        alertTypeBTN.setEnabled(isUmbeeEnabled);
        customThresholdCB.setEnabled(isUmbeeEnabled);
        customThresholdCB.setClickable(isUmbeeEnabled);
        singleThresholdSB.setEnabled(isUmbeeEnabled);
        tripleThreshold1SB.setEnabled(isUmbeeEnabled);
        tripleThreshold2SB.setEnabled(isUmbeeEnabled);
        tripleThreshold3SB.setEnabled(isUmbeeEnabled);
        enableLocationUpdatesCB.setEnabled(isUmbeeEnabled);
        enableLocationUpdatesCB.setClickable(isUmbeeEnabled);
        enableLocationUpdatesCB.setChecked(mSharedPrefs.getEnableLocationUpdates());

        if (locationET.getText().toString().length() == 0) {
            locationET.append(mSharedPrefs.getLocation());
        }

        updateTimeBTN.setText(UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));

        alertTypeBTN.setText(alertTypeOptions[mSharedPrefs.getAlertType() - Refs.ALERT_BASE]);

        switch (mSharedPrefs.getAlertType()) {
            case Refs.ALERT_SIMPLE:
                /*alertExampleBigTV.setText(mContext.getString(R.string.alert_example_simple_big));
                alertExampleSmallTV.setText(mContext.getString(R.string.alert_example_simple_small));
                alertExampleSmall2TV.setText(mContext.getString(R.string.alert_example_simple_small2));*/
                thresholdCheckRL.setVisibility(View.VISIBLE);
                break;
            case Refs.ALERT_COMPLEX:
                /*alertExampleBigTV.setText(mContext.getString(R.string.alert_example_four_big));
                alertExampleSmallTV.setText(mContext.getString(R.string.alert_example_four_small));
                alertExampleSmall2TV.setText(mContext.getString(R.string.alert_example_four_small2));*/
                thresholdCheckRL.setVisibility(View.VISIBLE);
                break;
            case Refs.ALERT_PERCENT:
                /* Not in use
                alertExampleBigTV.setText(mContext.getString(R.string.alert_example_percent_big));
                alertExampleSmallTV.setText(mContext.getString(R.string.alert_example_percent_small));
                alertExampleSmall2TV.setText(mContext.getString(R.string.alert_example_percent_small2));
                thresholdCheckRL.setVisibility(View.GONE);*/
                break;
            default:
                /*alertExampleBigTV.setText(mContext.getString(R.string.alert_example_simple_big));
                alertExampleSmallTV.setText(mContext.getString(R.string.alert_example_simple_small));
                alertExampleSmall2TV.setText(mContext.getString(R.string.alert_example_simple_small2));*/
                thresholdCheckRL.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setUpThresholdViews() {
        if (customThresholdCB.isChecked()) {
            switch (mSharedPrefs.getAlertType()) {
                case Refs.ALERT_SIMPLE:
                    singleThresholdLL.setVisibility(View.VISIBLE);
                    tripleThresholdLL.setVisibility(View.GONE);
                    thresholdCheckRL.setVisibility(View.VISIBLE);
                    break;
                case Refs.ALERT_COMPLEX:
                    singleThresholdLL.setVisibility(View.GONE);
                    tripleThresholdLL.setVisibility(View.VISIBLE);
                    thresholdCheckRL.setVisibility(View.VISIBLE);
                    break;
                case Refs.ALERT_PERCENT:
                    singleThresholdLL.setVisibility(View.GONE);
                    tripleThresholdLL.setVisibility(View.GONE);
                    thresholdCheckRL.setVisibility(View.GONE);
                    break;
                default:
                    singleThresholdLL.setVisibility(View.VISIBLE);
                    tripleThresholdLL.setVisibility(View.GONE);
                    thresholdCheckRL.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            singleThresholdLL.setVisibility(View.GONE);
            tripleThresholdLL.setVisibility(View.GONE);
        }

        singleThresholdSB.setProgress(mSharedPrefs.getSingleThreshold());
        bar1Progress = mSharedPrefs.getTripleThreshold1();
        bar2Progress = mSharedPrefs.getTripleThreshold2();
        bar3Progress = mSharedPrefs.getTripleThreshold3();
        tripleThreshold1SB.setProgress(bar1Progress);
        tripleThreshold2SB.setProgress(bar2Progress);
        tripleThreshold2SB.setSecondaryProgress(bar1Progress);
        tripleThreshold3SB.setProgress(bar3Progress);
        tripleThreshold3SB.setSecondaryProgress(bar2Progress);
    }
}

