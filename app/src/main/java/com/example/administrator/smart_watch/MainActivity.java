package com.example.administrator.smart_watch;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.data;
import static com.example.administrator.smart_watch.ClockViewActivity.hour;
import static com.example.administrator.smart_watch.ClockViewActivity.minute;

public class MainActivity extends AppCompatActivity {

    ImageView bt_clock, bt_draw, bt_aralm, bt_weather;
    TextView tv_connectInfo;
    Button bt_connect;

    char mCharDelimiter = '\0';

    public final int REQUEST_ENABLE_BT = 1;          //Bluetooth 인에이블 요청 코드
    public final int REQUEST_CONNECT_DEVICE = 2;    //디바이스 연결 요청 코드

    public static final int TIME_VIEW = 0x04;   // 시간 보기 명령
    public static final int TIME_SET = 0x07;    // 시간 설정 명령
    public static final int ALARM_SET = 0x0f;   // 알람 설정 명령
    public static final int HOUR_SET = 0x80;     // "시" 설정
    public static final int MINUTE_SET = 0xc0;   // "분" 설정
    public static final int DRAW_SET = 0x01;           // 그림 그리기 명령
    public static final int DRAW_REMOVE = 0x03;        // 그림 지우기 명령
    public static final int DRAW_X = 0x40;            // 그림  X 좌표 입력
    public static final int DRAW_Y = 0x60;            // 그림 Y 좌표 입력
    public static final int WEATHER_VIEW = 0x10;       // 날씨 보기 입력
    public static final int ALARM_RESET = 0x3f;     // 알람 해제

    public boolean BLUETOOTH_CONNECT = false;   // 블루투스가 연결되었는지 아닌지를 나타내는 변수

    public BluetoothAdapter mBTAdapter; // 블루투스 어댑터 (핸드폰에 있는)
    BluetoothDevice mBTDevice;  //블루투스 디바이스(ATmega128에 달린 블루투스 모듈 HC-06)

    public static BluetoothService mBluetoothService; // 블루투스 서비스를 담당하는 객체 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 전체화면 (상태바 지우기)

        // 객체 ID 인식
        bt_clock = (ImageView)findViewById(R.id.clock);
        bt_draw = (ImageView)findViewById(R.id.draw);
        bt_aralm = (ImageView)findViewById(R.id.aralm);
        bt_weather = (ImageView)findViewById(R.id.weather);
        tv_connectInfo = (TextView)findViewById(R.id.connectInfo);
        bt_connect = (Button)findViewById(R.id.connectBtn);

        // 브로드캐스트 리시버의 필터를 설정
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(Intent.ACTION_TIME_TICK);

        // 브로드캐스트 리시버가 응답할 수 있도록 필터 설정
        registerReceiver(mBroadcastReceiver, filter);

        // 블루투스 퍼미션 체크
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            //  권한이 없는 경우
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1); // 블루투스 권한을 줌
        }

        // 블루투스 정보 불러오기
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        // 블루투스를 지원하지 않는 기기일 경우
        if(mBTAdapter == null) Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();

        // "블루투스 연결하기" 버튼을 클릭한 경우
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 블루투스가 활성화 되어있는지 확인
                if(!mBTAdapter.isEnabled()){
                    // 활성화 되어있지 않다면
                    Intent requestBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 블루투스를 활성화하는 인텐트
                    startActivityForResult(requestBT, REQUEST_ENABLE_BT); // 리퀘스트코드와 함께 인텐트를 실행시킴
                }
                else{
                    // 활성화 되어있다면
                    Intent serverIntent = new Intent(MainActivity.this, DeviceConnectActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
            }
        });

        // "시계 보기" 버튼을 클릭한 경우
        bt_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BLUETOOTH_CONNECT) {
                    Intent intent = new Intent(getApplicationContext(), ClockViewActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "시계와 연결되지 않아 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // "그림 그리기" 버튼을 클릭할 경우
        bt_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BLUETOOTH_CONNECT){
                    Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "시계와 연결되지 않아 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // "날씨 보기" 버튼을 클릭할 경우
        bt_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BLUETOOTH_CONNECT){
                    Intent intent = new Intent(getApplicationContext(), WeatherViewActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "시계와 연결되지 않아 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // "알람 설정" 버튼을 클릭할 경우
        bt_aralm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BLUETOOTH_CONNECT){
                    Intent intent = new Intent(getApplicationContext(), AralmActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "시계와 연결되지 않아 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){    // 리퀘스트코드로 분기
            case REQUEST_ENABLE_BT: // 블루투스 활성화 요청을 했을 때
                if(resultCode == RESULT_OK){ // OK를 눌렀을 때
                    // 블루투스가 활성 상태로 변경됨
                    Toast.makeText(this, "블루투스가 활성화 되었습니다. 버튼을 다시 눌러주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(resultCode == RESULT_CANCELED){ // NO를 눌렀을 때
                    // 블루투스 활성화를 취소함
                    Toast.makeText(this, "블루투스 활성화가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CONNECT_DEVICE: // 디바이스 연결 요청을 했을 때
                if(resultCode == RESULT_OK){ //OK를 눌렀을 때
                    String address = data.getExtras().getString(DeviceConnectActivity.EXTRA_DEVICE_ADDRESS); // 연결할 디바이스를 선택했던 액티비티에서 넘어온 주소 정보를 변수에 담음
                    mBTDevice = mBTAdapter.getRemoteDevice(address);
                    if(mBluetoothService == null) mBluetoothService = new BluetoothService();
                    mBluetoothService.connect(mBTDevice);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothService != null) mBluetoothService.stop();

        // 브로드캐스트 리시버 활성화
        this.unregisterReceiver(mBroadcastReceiver);
    }

   private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           String action = intent.getAction();

           switch(action){
               case BluetoothDevice.ACTION_PAIRING_REQUEST:
                   Toast.makeText(context, "장치와 페어링을 시도합니다.", Toast.LENGTH_SHORT).show();
                   break;

               case BluetoothDevice.ACTION_ACL_CONNECTED:
                   Toast.makeText(context, "" + mBTDevice.getName() + " 장치와 연결하는데 성공했습니다.", Toast.LENGTH_SHORT).show();
                   tv_connectInfo.setText("시계와 연결되었습니다.");
                   bt_connect.setVisibility(View.GONE);
                   BLUETOOTH_CONNECT = true;
                   break;

               case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                   Toast.makeText(context, "" + " 장치와 연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                   tv_connectInfo.setText("시계와 연결이 해제되었습니다.");
                   bt_connect.setVisibility(View.VISIBLE);
                   BLUETOOTH_CONNECT = false;
                   break;
           }

           // 시간이 흘러갈 때
           if(Intent.ACTION_TIME_TICK.equals(action)){
               SimpleDateFormat mHourSimpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
               SimpleDateFormat mMinuteSimpleDateFormat = new SimpleDateFormat("mm", Locale.KOREA);
               Date hourDate = new Date();
               Date minuteDate = new Date();
               String mHourTime = mHourSimpleDateFormat.format(hourDate);
               String mMinuteTime = mMinuteSimpleDateFormat.format(minuteDate);

               hour = (Integer.parseInt(mHourTime.toString())) % 12;
               minute = Integer.parseInt(mMinuteTime.toString());

               int temp1 = HOUR_SET | hour;
               byte[] hourByte = new byte[2];
               hourByte[0] = (byte)temp1;
               hourByte[1] = (byte)mCharDelimiter;
               mBluetoothService.write(hourByte);

               int temp2 = MINUTE_SET | minute;
               byte[] minuteByte = new byte[2];
               minuteByte[0] = (byte)temp2;
               minuteByte[1] = (byte)mCharDelimiter;
               mBluetoothService.write(minuteByte);
           }
       }
   };
}
