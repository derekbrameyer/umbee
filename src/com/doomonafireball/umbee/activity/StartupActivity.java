package com.doomonafireball.umbee.activity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.doomonafireball.umbee.MainApp;
import com.doomonafireball.umbee.R;
import com.doomonafireball.umbee.adapter.NoaaPagerAdapter;
import com.doomonafireball.umbee.dialog.AboutDialog;
import com.doomonafireball.umbee.dialog.AlertTypeDialog;
import com.doomonafireball.umbee.model.NoaaByDay;
import com.doomonafireball.umbee.query.WeatherQuery;
import com.doomonafireball.umbee.query.ZipCodeQuery;
import com.doomonafireball.umbee.receiver.NotificationReceiver;
import com.doomonafireball.umbee.util.JsonParser;
import com.doomonafireball.umbee.util.Refs;
import com.doomonafireball.umbee.util.SharedPrefsManager;
import com.doomonafireball.umbee.util.UmbeeNotifUtils;
import com.doomonafireball.umbee.util.UmbeeTextUtils;
import com.doomonafireball.umbee.util.UmbeeTimeUtils;
import com.doomonafireball.umbee.util.UmbeeWidgetUtils;
import com.doomonafireball.umbee.widget.FixedScrollView;
import com.flurry.android.FlurryAgent;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import oak.widget.CancelEditText;
import roboguice.inject.InjectView;

public class StartupActivity extends RoboSherlockFragmentActivity {

    @InjectView(R.id.CB_enable_umbee) CheckBox enableUmbeeCB;
    @InjectView(R.id.CB_enable_smart_location) CheckBox enableLocationUpdatesCB;
    @InjectView(R.id.CB_custom_threshold) CheckBox customThresholdCB;
    @InjectView(R.id.CB_notify_tomorrow) CheckBox notifyTomorrowCB;
    @InjectView(R.id.ET_location) CancelEditText locationET;
    @InjectView(R.id.TV_single_threshold) TextView singleThresholdTV;
    @InjectView(R.id.TV_triple_threshold) TextView tripleThresholdTV;
    @InjectView(R.id.TV_triple_threshold2) TextView tripleThreshold2TV;
    @InjectView(R.id.TV_triple_threshold3) TextView tripleThreshold3TV;
    @InjectView(R.id.TV_update_time) TextView updateTimeTV;
    @InjectView(R.id.TV_alert_type) TextView alertTypeTV;
    @InjectView(R.id.SB_single_threshold) SeekBar singleThresholdSB;
    @InjectView(R.id.SB_triple_threshold_1) SeekBar tripleThreshold1SB;
    @InjectView(R.id.SB_triple_threshold_2) SeekBar tripleThreshold2SB;
    @InjectView(R.id.SB_triple_threshold_3) SeekBar tripleThreshold3SB;
    @InjectView(R.id.RL_advanced_options_container) RelativeLayout advancedOptionsRL;
    @InjectView(R.id.LL_single_threshold_container) LinearLayout singleThresholdLL;
    @InjectView(R.id.LL_triple_threshold_container) LinearLayout tripleThresholdLL;
    @InjectView(R.id.LL_advanced_options_container) LinearLayout advancedOptionsContainerLL;
    @InjectView(R.id.RL_alert_type_container) RelativeLayout alertTypeContainerRL;
    @InjectView(R.id.LL_update_time) LinearLayout updateTimeLL;
    @InjectView(R.id.RL_test_notif) RelativeLayout testNotifRL;
    @InjectView(R.id.RL_smart_location_container) RelativeLayout smartLocationContainerRL;
    @InjectView(R.id.RL_enable_container) RelativeLayout enableContainerRL;
    @InjectView(R.id.RL_threshold_container) RelativeLayout thresholdCheckRL;
    @InjectView(R.id.RL_notify_tomorrow_container) RelativeLayout notifyTomorrowRL;
    @InjectView(R.id.IV_advanced_options_icon) ImageView advancedOptionsIconIV;
    @InjectView(R.id.VP_todays_precip) ViewPager todaysPrecipVP;
    @InjectView(R.id.CPI_todays_precip_ind) CirclePageIndicator todaysPrecipCPI;
    @InjectView(R.id.FSV_parent) FixedScrollView parentFSV;
    @InjectView(R.id.TV_update_time_title) TextView titleUpdateTimeTV;
    @InjectView(R.id.TV_location_title) TextView locationTitleTV;
    @InjectView(R.id.TV_advanced_title) TextView advancedTitleTV;
    @InjectView(R.id.TV_alert_type_title) TextView alertTypeTitleTV;
    @InjectView(R.id.TV_notify_tomorrow_title) TextView notifyTomorrowTitleTV;
    @InjectView(R.id.TV_test_notif_title) TextView testNotifTitleTV;
    @InjectView(R.id.TV_enable_location_updates) TextView enableLocationUpdatesTitleTV;
    @InjectView(R.id.TV_enable_location_updates_sub) TextView enableLocationsSubtitleTV;
    @InjectView(R.id.TV_custom_threshold_title) TextView customThresholdTV;
    @InjectView(R.id.TV_triple_threshold_title) TextView tripleThresholdTitleTV;

    SharedPrefsManager mSharedPrefs;
    Context mContext;
    ArrayAdapter<CharSequence> weatherApiAdapter;
    String[] weatherApiOptions;
    String[] alertTypeOptions;

    private Animation advancedOptionsFadeInAnim;
    private Animation advancedOptionsFadeOutAnim;
    private Animation advancedOptionsRotateCwAnim;
    private Animation advancedOptionsRotateCcwAnim;
    private int bar1Progress;
    private int bar2Progress;
    private int bar3Progress;
    private NoaaPagerAdapter mPagerAdapter;
    private ZipCodeQuery mZcq;
    private NoaaByDay mNbd;
    private Drawable currBgDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.startup);
        mContext = this;

        SharedPrefsManager.initialize(this);
        mSharedPrefs = SharedPrefsManager.getInstance();

        alertTypeOptions = getResources().getStringArray(R.array.alert_type_array);

        enableUmbeeCB.setOnCheckedChangeListener(enableCCL);
        customThresholdCB.setOnCheckedChangeListener(customThresholdCCL);
        enableLocationUpdatesCB.setOnCheckedChangeListener(enableLocationUpdatesCCL);
        notifyTomorrowCB.setOnCheckedChangeListener(notifyTomorrowCCL);
        advancedOptionsRL.setOnClickListener(advancedOptionsCL);
        updateTimeLL.setOnClickListener(updateTimeCL);
        alertTypeContainerRL.setOnClickListener(alertTypeCL);
        testNotifRL.setOnClickListener(testNotificationCL);
        singleThresholdSB.setOnSeekBarChangeListener(singleSBCL);
        tripleThreshold1SB.setOnSeekBarChangeListener(triple1SBCL);
        tripleThreshold2SB.setOnSeekBarChangeListener(triple2SBCL);
        tripleThreshold3SB.setOnSeekBarChangeListener(triple3SBCL);
        locationET.setOnEditorActionListener(locationETListener);

        enableContainerRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableUmbeeCB.setChecked(!enableUmbeeCB.isChecked());
            }
        });

        smartLocationContainerRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLocationUpdatesCB.setChecked(!enableLocationUpdatesCB.isChecked());
            }
        });

        thresholdCheckRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customThresholdCB.setChecked(!customThresholdCB.isChecked());
            }
        });

        notifyTomorrowRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyTomorrowCB.setChecked(!notifyTomorrowCB.isChecked());
            }
        });

        advancedOptionsFadeInAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade_in);
        advancedOptionsFadeOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade_out);
        advancedOptionsRotateCwAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate_cw_180);
        advancedOptionsRotateCcwAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate_ccw_180);
        advancedOptionsRotateCcwAnim.setFillEnabled(true);
        advancedOptionsRotateCcwAnim.setFillAfter(true);

        advancedOptionsFadeInAnim.setAnimationListener(advancedOptionsFadeInAL);
        advancedOptionsFadeOutAnim.setAnimationListener(advancedOptionsFadeOutAL);

        String nbdString = mSharedPrefs.getNoaaByDayString();
        if (nbdString.equals("")) {
            mNbd = new NoaaByDay();
        } else {
            try {
                mNbd = JsonParser.parseNoaaByDay(mSharedPrefs.getNoaaByDayString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mPagerAdapter = new NoaaPagerAdapter(mContext, mNbd);
        todaysPrecipVP.setAdapter(mPagerAdapter);

        todaysPrecipCPI.setViewPager(todaysPrecipVP);
        todaysPrecipCPI.setOnPageChangeListener(todaysPrecipOPCL);

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{getResources().getColor(R.color.cornflower_blue), getResources().getColor(R.color.white)});
        gd.setCornerRadius(0f);
        currBgDrawable = gd;

        setAdvancedOptionsVisibility();
        setWindowBackgroundGradient(todaysPrecipVP.getCurrentItem());
        setActionBarBackground();
        setUpPrecipViews();
        setUpPreferenceViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        FlurryAgent.logEvent("startup_activity_on_resume");
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(mContext, getResources().getString(R.string.flurry_key));
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(mContext);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AlertTypeDialog.START_ALERT_TYPE_ACTIVITY_FOR_RESULT:
                    int selectedAlert = data.getIntExtra(AlertTypeDialog.SELECTED_ALERT, 0);
                    String selectedAlertText = data.getStringExtra(AlertTypeDialog.SELECTED_ALERT_TEXT);
                    int selectedAlertType = selectedAlert + Refs.ALERT_BASE;
                    mSharedPrefs.setAlertType(selectedAlertType);
                    alertTypeTV.setText(selectedAlertText);
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("alert_type", "" + selectedAlertType);
                    parameters.put("alert_text", selectedAlertText);
                    FlurryAgent.logEvent("alert_type_changed", parameters);
                    setUpPreferenceViews();
                    setUpThresholdViews();
                    break;
            }
        }
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
                AboutDialog d = new AboutDialog(mContext);
                d.show();
                return false;
            case R.id.menu_refresh:
                setLocation();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ViewPager.OnPageChangeListener todaysPrecipOPCL = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setWindowBackgroundGradient(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    Animation.AnimationListener advancedOptionsFadeInAL = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            advancedOptionsContainerLL.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    Animation.AnimationListener advancedOptionsFadeOutAL = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            advancedOptionsContainerLL.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

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
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("threshold", "" + seekBar.getProgress());
            FlurryAgent.logEvent("single_seek_bar_changed", parameters);
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
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("threshold", "" + seekBar.getProgress());
            FlurryAgent.logEvent("triple_1_seek_bar_changed", parameters);

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
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("threshold", "" + seekBar.getProgress());
            FlurryAgent.logEvent("triple_2_seek_bar_changed", parameters);
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
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("threshold", "" + seekBar.getProgress());
            FlurryAgent.logEvent("triple_3_seek_bar_changed", parameters);
        }
    };

    CompoundButton.OnCheckedChangeListener enableCCL = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mSharedPrefs.setEnabled(b);
            if (b) {
                startUmbeeService();
            } else {
                cancelUmbeeService();
            }
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("umbee_enabled", "" + b);
            FlurryAgent.logEvent("umbee_enabled_checked", parameters);
            setUpPreferenceViews();
        }
    };

    CompoundButton.OnCheckedChangeListener customThresholdCCL
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mSharedPrefs.setCustomThreshold(b);
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("custom_threshold", "" + b);
            FlurryAgent.logEvent("custom_threshold_checked", parameters);
            setUpThresholdViews();
        }
    };

    CompoundButton.OnCheckedChangeListener enableLocationUpdatesCCL
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mSharedPrefs.setEnableLocationUpdates(b);
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("enable_location_updates", "" + b);
            FlurryAgent.logEvent("enable_location_updates_checked", parameters);
        }
    };

    CompoundButton.OnCheckedChangeListener notifyTomorrowCCL = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mSharedPrefs.setNotifyTomorrow(b);
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("notify_tomorrow", "" + b);
            FlurryAgent.logEvent("notify_tomorrow_checked", parameters);
        }
    };

    private void setLocation() {
        String loc = locationET.getText().toString();
        if (UmbeeTextUtils.isValidZipCode(loc)) {
            mSharedPrefs.setLocation(loc);
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("custom_location", loc);
            FlurryAgent.logEvent("custom_location_set", parameters);
            InputMethodManager mgr = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(locationET.getWindowToken(), 0);
            Toast.makeText(mContext, getResources().getString(R.string.zip_code_saved), Toast.LENGTH_SHORT)
                    .show();
            WeatherQuery getWeatherQuery = new WeatherQuery(mContext, true, false, refreshCompleteHandler);
            getWeatherQuery.execute();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.not_valid_zip), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    TextView.OnEditorActionListener locationETListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                    || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_NEXT
                    || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {
                setLocation();
            }
            return true;
        }
    };

    View.OnClickListener testNotificationCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FlurryAgent.logEvent("test_notification_clicked");
            UmbeeNotifUtils.createNotification(mContext, mNbd);
        }
    };

    View.OnClickListener advancedOptionsCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean b = mSharedPrefs.getAdvancedOptions();
            mSharedPrefs.setAdvancedOptions(!b);
            setAdvancedOptionsVisibility();
        }
    };

    View.OnClickListener updateTimeCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (updateTimeLL.isEnabled()) {
                TimePickerDialog dialog = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                                mSharedPrefs.setUpdateTime(
                                        UmbeeTimeUtils.timeOfDayFromTimePicker(hourOfDay, minute));
                                updateTimeTV.setText(
                                        UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));
                                HashMap<String, String> parameters = new HashMap<String, String>();
                                parameters.put("update_time_hour", "" + hourOfDay);
                                parameters.put("update_time_minute", "" + minute);
                                parameters.put("update_time_text",
                                        UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));
                                FlurryAgent.logEvent("update_time_set", parameters);
                                startUmbeeService();
                            }
                        },
                        UmbeeTimeUtils.hourOfDayFromInt(mSharedPrefs.getUpdateTime()),
                        UmbeeTimeUtils.minuteFromInt(mSharedPrefs.getUpdateTime()),
                        false);
                dialog.show();
            }
        }
    };

    View.OnClickListener alertTypeCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle alertBundle = new Bundle();
            alertBundle.putStringArray(AlertTypeDialog.ALERT_TYPE_OPTIONS, alertTypeOptions);

            AlertTypeDialog d = new AlertTypeDialog(mContext, alertBundle, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Bundle msgData = msg.getData();
                    if (msgData != null) {
                        int selectedAlert = msgData.getInt(AlertTypeDialog.SELECTED_ALERT, 0);
                        String selectedAlertText = msgData.getString(AlertTypeDialog.SELECTED_ALERT_TEXT);
                        int selectedAlertType = selectedAlert + Refs.ALERT_BASE;
                        mSharedPrefs.setAlertType(selectedAlertType);
                        HashMap<String, String> parameters = new HashMap<String, String>();
                        parameters.put("alert_type", "" + selectedAlertType);
                        parameters.put("alert_type_text", "" + selectedAlertText);
                        FlurryAgent.logEvent("alert_type_set", parameters);
                        alertTypeTV.setText(selectedAlertText);
                        setUpPreferenceViews();
                        setUpThresholdViews();
                    }
                }
            });
            d.show();
        }
    };

    private Handler refreshCompleteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                mNbd = JsonParser.parseNoaaByDay(mSharedPrefs.getNoaaByDayString());
                if (mNbd.mPop.probabilities.size() > 0) {
                    setUpPrecipViews();
                    setWindowBackgroundGradient(todaysPrecipVP.getCurrentItem());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    getResources().getString(R.string.tomorrow),
                    UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));
        } else {
            s = String.format(getResources().getString(R.string.dynamic_umbee_is_set_for),
                    getResources().getString(R.string.today),
                    UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));
        }

        Intent notif = new Intent(mContext, NotificationReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, notif,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                pi);
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        Log.d(MainApp.TAG, "Set alarmManager.setRepeating to: " + cal.getTime().toLocaleString());
    }

    private void setAdvancedOptionsVisibility() {
        boolean b = mSharedPrefs.getAdvancedOptions();
        if (b) {
            advancedOptionsContainerLL.startAnimation(advancedOptionsFadeInAnim);
            advancedOptionsIconIV.startAnimation(advancedOptionsRotateCcwAnim);
        } else {
            advancedOptionsContainerLL.startAnimation(advancedOptionsFadeOutAnim);
            advancedOptionsIconIV.startAnimation(advancedOptionsRotateCwAnim);
        }
    }

    private void setWindowBackgroundGradient(int position) {
        if (mNbd.mPop.probabilities.size() > 0) {
            int evePrecip = mNbd.mPop.probabilities.get(position).first;
            int morPrecip = mNbd.mPop.probabilities.get(position).second;
            float evePercent = ((((float) evePrecip) / 100.0f));
            float morPercent = ((((float) morPrecip) / 100.0f));
            float percent = Math.max(evePercent, morPercent);
            int color = UmbeeWidgetUtils.getBetweenColorByPercent(percent,
                    getResources().getColor(R.color.cornflower_blue),
                    getResources().getColor(R.color.midnight_blue));
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{color, getResources().getColor(R.color.window_bg_gradient_bottom)});
            gd.setCornerRadius(0f);
            Drawable[] layers = new Drawable[2];
            layers[0] = currBgDrawable;
            layers[1] = gd;
            TransitionDrawable td = new TransitionDrawable(layers);
            this.getWindow().setBackgroundDrawable(td);
            td.startTransition(250);
            currBgDrawable = gd;
        }
    }

    private void setActionBarBackground() {
        ActionBar ab = getSupportActionBar();
        Drawable d = UmbeeWidgetUtils.getActionBarDrawable(mContext);
        ab.setBackgroundDrawable(d);
        ab.hide();
        ab.show();
    }

    private void setUpPrecipViews() {
        if ((mNbd.mPop.probabilities.size() == 0) && mSharedPrefs.getLocation().equals("")) {
            // We haven't gotten any info yet, and we don't have a zip code, so we can't do anything.
        } else if ((mNbd.mPop.probabilities.size() == 0)) {
            // We haven't gotten any info yet.
            WeatherQuery getWeatherQuery = new WeatherQuery(mContext, true, false, refreshCompleteHandler);
            getWeatherQuery.execute();
        } else {
            todaysPrecipVP.setVisibility(View.VISIBLE);
            todaysPrecipCPI.setVisibility(View.VISIBLE);
            int currPos = todaysPrecipVP.getCurrentItem();
            mPagerAdapter = new NoaaPagerAdapter(mContext, mNbd);
            todaysPrecipVP.setAdapter(mPagerAdapter);
            todaysPrecipCPI.setCurrentItem(currPos);
        }
    }

    private void setUpPreferenceViews() {
        boolean isUmbeeEnabled = mSharedPrefs.getEnabled();

        enableUmbeeCB.setChecked(isUmbeeEnabled);
        customThresholdCB.setChecked(mSharedPrefs.getCustomThreshold());
        enableLocationUpdatesCB.setChecked(mSharedPrefs.getEnableLocationUpdates());
        notifyTomorrowCB.setChecked(mSharedPrefs.getNotifyTomorrow());

        locationET.setEnabled(isUmbeeEnabled);
        updateTimeLL.setEnabled(isUmbeeEnabled);
        alertTypeContainerRL.setEnabled(isUmbeeEnabled);
        customThresholdCB.setEnabled(isUmbeeEnabled);
        customThresholdCB.setClickable(isUmbeeEnabled);
        singleThresholdSB.setEnabled(isUmbeeEnabled);
        tripleThreshold1SB.setEnabled(isUmbeeEnabled);
        tripleThreshold2SB.setEnabled(isUmbeeEnabled);
        tripleThreshold3SB.setEnabled(isUmbeeEnabled);
        enableLocationUpdatesCB.setEnabled(isUmbeeEnabled);
        enableLocationUpdatesCB.setClickable(isUmbeeEnabled);
        notifyTomorrowCB.setEnabled(isUmbeeEnabled);
        notifyTomorrowCB.setClickable(isUmbeeEnabled);
        notifyTomorrowRL.setEnabled(isUmbeeEnabled);
        testNotifRL.setEnabled(isUmbeeEnabled);
        smartLocationContainerRL.setEnabled(isUmbeeEnabled);
        thresholdCheckRL.setEnabled(isUmbeeEnabled);
        advancedOptionsRL.setEnabled(isUmbeeEnabled);
        // Disable TextViews
        singleThresholdTV.setEnabled(isUmbeeEnabled);
        tripleThresholdTV.setEnabled(isUmbeeEnabled);
        tripleThreshold2TV.setEnabled(isUmbeeEnabled);
        tripleThreshold3TV.setEnabled(isUmbeeEnabled);
        updateTimeTV.setEnabled(isUmbeeEnabled);
        alertTypeTV.setEnabled(isUmbeeEnabled);
        // Disable title TextViews
        titleUpdateTimeTV.setEnabled(isUmbeeEnabled);
        locationTitleTV.setEnabled(isUmbeeEnabled);
        advancedTitleTV.setEnabled(isUmbeeEnabled);
        alertTypeTitleTV.setEnabled(isUmbeeEnabled);
        notifyTomorrowTitleTV.setEnabled(isUmbeeEnabled);
        testNotifTitleTV.setEnabled(isUmbeeEnabled);
        enableLocationUpdatesTitleTV.setEnabled(isUmbeeEnabled);
        enableLocationsSubtitleTV.setEnabled(isUmbeeEnabled);
        customThresholdTV.setEnabled(isUmbeeEnabled);
        tripleThresholdTitleTV.setEnabled(isUmbeeEnabled);

        if (locationET.getText().toString().length() == 0) {
            locationET.append(mSharedPrefs.getLocation());
        }

        if (mSharedPrefs.getLocation().equals("") && mZcq == null) {
            // Check for latest existing location
            LocationInfo latestInfo = new LocationInfo(mContext);
            mZcq = new ZipCodeQuery(mContext, latestInfo.lastLat, latestInfo.lastLong, true, true, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    locationET.append(mSharedPrefs.getLocation());
                    if (mSharedPrefs.getLocation().equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.please_set_location),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        WeatherQuery getWeatherQuery = new WeatherQuery(mContext, true, false, refreshCompleteHandler);
                        getWeatherQuery.execute();
                    }
                }
            });
            mZcq.execute();
        }

        updateTimeTV.setText(UmbeeTimeUtils.timeOfDayFromInt(mSharedPrefs.getUpdateTime()));

        alertTypeTV.setText(alertTypeOptions[mSharedPrefs.getAlertType() - Refs.ALERT_BASE]);

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

