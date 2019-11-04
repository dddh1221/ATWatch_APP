package com.example.administrator.smart_watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.administrator.smart_watch.MainActivity.HOUR_SET;
import static com.example.administrator.smart_watch.MainActivity.MINUTE_SET;
import static com.example.administrator.smart_watch.MainActivity.TIME_SET;
import static com.example.administrator.smart_watch.MainActivity.TIME_VIEW;
import static com.example.administrator.smart_watch.MainActivity.mBluetoothService;

public class ClockViewActivity extends AppCompatActivity {

    char mCharDelimiter = '\0';

    static public int hour;
    static public int minute;

    TextView tv_nowTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 전체화면 (상태바 지우기)

        tv_nowTime = (TextView)findViewById(R.id.nowTime);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, filter);

        SimpleDateFormat mHourSimpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
        SimpleDateFormat mMinuteSimpleDateFormat = new SimpleDateFormat("mm", Locale.KOREA);
        Date hourDate = new Date();
        Date minuteDate = new Date();
        String mHourTime = mHourSimpleDateFormat.format(hourDate);
        String mMinuteTime = mMinuteSimpleDateFormat.format(minuteDate);

        hour = Integer.parseInt(mHourTime.toString());
        minute = Integer.parseInt(mMinuteTime.toString());

        tv_nowTime.setText("" + hour + " : " + minute);

        int temp2 = HOUR_SET | (hour % 12);
        byte[] hourByte = new byte[2];
        hourByte[0] = (byte)temp2;
        hourByte[1] = (byte)mCharDelimiter;
        mBluetoothService.write(hourByte);

        int temp3 = MINUTE_SET | minute;
        byte[] minuteByte = new byte[2];
        minuteByte[0] = (byte)temp3;
        minuteByte[1] = (byte)mCharDelimiter;
        mBluetoothService.write(minuteByte);

        int temp1 = TIME_SET;
        byte[] timeSetByte = new byte[2];
        timeSetByte[0] = (byte)temp1;
        timeSetByte[1] = (byte)mCharDelimiter;
        mBluetoothService.write(timeSetByte);

        int temp4 = TIME_VIEW;
        byte[] timeViewByte = new byte[2];
        timeViewByte[0] = (byte)temp4;
        timeViewByte[1] = (byte)mCharDelimiter;
        mBluetoothService.write(timeViewByte);

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 시간이 흘러갈 때
            if(Intent.ACTION_TIME_TICK.equals(action)){
                SimpleDateFormat mHourSimpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                SimpleDateFormat mMinuteSimpleDateFormat = new SimpleDateFormat("mm", Locale.KOREA);
                Date hourDate = new Date();
                Date minuteDate = new Date();
                String mHourTime = mHourSimpleDateFormat.format(hourDate);
                String mMinuteTime = mMinuteSimpleDateFormat.format(minuteDate);

                hour = Integer.parseInt(mHourTime.toString());
                minute = Integer.parseInt(mMinuteTime.toString());

                tv_nowTime.setText("" + hour + " : " + minute);

                int temp2 = HOUR_SET | (hour % 12);
                byte[] hourByte = new byte[2];
                hourByte[0] = (byte)temp2;
                hourByte[1] = (byte)mCharDelimiter;
                mBluetoothService.write(hourByte);

                int temp3 = MINUTE_SET | minute;
                byte[] minuteByte = new byte[2];
                minuteByte[0] = (byte)temp3;
                minuteByte[1] = (byte)mCharDelimiter;
                mBluetoothService.write(minuteByte);

                int temp1 = TIME_SET;
                byte[] timeSetByte = new byte[2];
                timeSetByte[0] = (byte)temp1;
                timeSetByte[1] = (byte)mCharDelimiter;
                mBluetoothService.write(timeSetByte);

                int temp4 = TIME_VIEW;
                byte[] timeViewByte = new byte[2];
                timeViewByte[0] = (byte)temp4;
                timeViewByte[1] = (byte)mCharDelimiter;
                mBluetoothService.write(timeViewByte);
            }
        }
    };
}
