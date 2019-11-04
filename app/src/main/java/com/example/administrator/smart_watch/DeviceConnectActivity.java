package com.example.administrator.smart_watch;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by Administrator on 2018-11-13.
 */

public class DeviceConnectActivity extends AppCompatActivity {
    ListView newDeviceView, pairedDeviceView;
    Button btnSearch;

    // 변수
    private BluetoothAdapter mBtAdapter; // 블루투스 어댑터 객체(로컬 블루투스)
    private ArrayAdapter<String> mPairedDevicesArrayAdapter; // 페어링된 장치 배열
    private ArrayAdapter<String> mNewDevicesArrayAdapter; // 새로운 장치 배열

    // 연결 스레드 변수
    BluetoothService bluetoothService;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // 타이틀바에 원형 프로그레스바 띄우기
        setContentView(R.layout.activity_device_connect);

        // 리스트뷰 어댑터 배열 선언
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        newDeviceView = (ListView)findViewById(R.id.new_devices); // 새로운 장치를 보여줄 리스트뷰
        newDeviceView.setAdapter(mNewDevicesArrayAdapter); // 새로운 장치를 보여줄 리스트뷰의 어댑터 설정
        newDeviceView.setOnItemClickListener(mDeviceClickListener); // 아이템을 클릭했을 때의 리스너 설정

        pairedDeviceView = (ListView)findViewById(R.id.paired_devices); // 페어링된 장치를 보여줄 리스트뷰
        pairedDeviceView.setAdapter(mPairedDevicesArrayAdapter); // 페어링된 장치를 보여줄 리스트뷰의 어댑터 설정
        pairedDeviceView.setOnItemClickListener(mDeviceClickListener); // 아이템을 클릭했을 때의 리스너 설정

        // 장치를 검색했을 때 브로드캐스트 리시버가 응답할 수 있도록 필터 설정
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);

        // 장치 검색을 종료했을 때 브로드캐스트 리시버가 응답할 수 있도록 필터 설정
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        // 블루투스 어댑터 불러오기
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // 페어링된 장치 가져오기
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // 페어링된 장치를 성공적으로 불러왔을 때, 리스트뷰 어댑터에 추가해주는 작업
        if(pairedDevices.size() > 0){
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE); // 텍스트뷰 보이게 설정하기
            for(BluetoothDevice device : pairedDevices){ // 페어링된 디바이스 수 만큼 반복
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress()); // 디바이스 이름과 주소 정보를 리스트뷰 어댑터에 추가
            }
        } else {
            // 페어링된 장치가 없을 경우
            mPairedDevicesArrayAdapter.add("장치 없음");
        }

        btnSearch = (Button)findViewById(R.id.btnSearch); // 장치 검색 버튼
        // 버튼을 클릭했을 때
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery(); // 장치 검색 시작
                v.setVisibility(View.GONE); // 버튼 안보이게
            }
        });
    }

    // 어플을 종료하려 했을 때
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 장치 검색을 중단
        if(mBtAdapter != null){
            mBtAdapter.cancelDiscovery();
        }

        // 브로드캐스트 리시버 비활성화
        this.unregisterReceiver(mReceiver);
    }

    // 장치 검색
    private void doDiscovery(){
        setProgressBarIndeterminateVisibility(true); // 원형 프로그레스바 보이게 활성화
        setTitle("장치 검색중..."); // 타이틀 바꾸기

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE); // 새로운 디바이스 리스드뷰 보이게 활성화

        // 이미 장치를 검색중일 때 장치 검색 비활성화
        if(mBtAdapter.isDiscovering()){
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery(); // 장치 검색 시작
    }

    // 리스트뷰에 있는 디바이스를 클릭했을 때
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBtAdapter.cancelDiscovery(); // 장치 검색 비활성화

            // MAC 주소 가져오기
            String info = ((TextView)view).getText().toString(); // 뷰에 있는 텍스트 정보 가져오기
            String address = info.substring(info.length() - 17); // 가져온 텍스트 정보에서 마지막 17문자 자르기(MAC주소만 자르기 위해서)

            // 새로운 디바이스의 정보를 담은 인텐트
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // OK 정보와 인텐트 정보를 넘기고 종료
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 새로운 디바이스를 찾았을 때
            if(BluetoothDevice.ACTION_CLASS_CHANGED.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                // 새로운 블루투스 디바이스 객체 정보를 인텐트에 추가
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // 만약 페어링되지 않은 디바이스일 경우
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress()); // 새로운 장치 리스트뷰에 정보룰 추가
                }
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){ // 장치 검색이 종료되었을 경우
                setProgressBarIndeterminateVisibility(false); // 원형 프로그레스바 없애기
                setTitle("장치를 선택하세요."); // 타이틀바 텍스트 변경

                if(mNewDevicesArrayAdapter.getCount() == 0){ // 검색된 새로운 장치가 없을 경우
                    mNewDevicesArrayAdapter.add("장치 없음");
                }
            }
        }
    };
}
