package com.example.administrator.smart_watch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.administrator.smart_watch.MainActivity.ALARM_RESET;
import static com.example.administrator.smart_watch.MainActivity.ALARM_SET;
import static com.example.administrator.smart_watch.MainActivity.DRAW_SET;
import static com.example.administrator.smart_watch.MainActivity.HOUR_SET;
import static com.example.administrator.smart_watch.MainActivity.MINUTE_SET;
import static com.example.administrator.smart_watch.MainActivity.mBluetoothService;

public class AralmActivity extends AppCompatActivity {

    Spinner hourSpinner, minuteSpinner;
    Button bt_setAlarm;
    int alarmHour, alarmMinute;
    TextView tv_alarmHour, tv_alarmMinute;
    ImageView im_alarmOff;

    char mCharDelimiter = '\0';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aralm);

        hourSpinner = (Spinner)findViewById(R.id.hourSpinner);
        minuteSpinner = (Spinner)findViewById(R.id.minuteSpinner);
        bt_setAlarm = (Button)findViewById(R.id.setAralm);

        tv_alarmHour = (TextView)findViewById(R.id.alarmHour);
        tv_alarmMinute = (TextView)findViewById(R.id.alarmMinute);

        im_alarmOff = (ImageView)findViewById(R.id.aralmOff);

        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                alarmHour = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        minuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                alarmMinute = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bt_setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AralmActivity.this, "알림이 " + alarmHour + "시 " + alarmMinute + "분으로 설정되었습니다." , Toast.LENGTH_SHORT).show();

                int temp2 = HOUR_SET | alarmHour;
                byte[] hourByte = new byte[2];
                hourByte[0] = (byte) temp2;
                hourByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(hourByte);

                int temp3 = MINUTE_SET | alarmMinute;
                byte[] minuteByte = new byte[2];
                minuteByte[0] = (byte) temp3;
                minuteByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(minuteByte);


                int temp1 = ALARM_SET;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);

                tv_alarmHour.setText("" + alarmHour);
                tv_alarmMinute.setText("" + alarmMinute);
            }
        });

        im_alarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmHour = 0;
                alarmMinute = 0;
                tv_alarmHour.setText("" + alarmHour);
                tv_alarmMinute.setText("" + alarmMinute);

                int temp1 = ALARM_RESET;
                byte[] resetByte = new byte[2];
                resetByte[0] = (byte) temp1;
                resetByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(resetByte);

                Toast.makeText(AralmActivity.this, "알람이 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
