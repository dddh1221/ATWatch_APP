package com.example.administrator.smart_watch;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2018-11-13.
 */

public class BluetoothService {
    // UUID
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // 서버 소켓을 만들 때 이름
    private static final String NAME = "BluetoothChat;";

    // 변수
    private final BluetoothAdapter mAdapter;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // 상태
    public static final int STATE_NONE = 0; // 아무것도 하지 않을 때
    public static final int STATE_LISTEN = 1; // 연결이 들어오는지 아닌지 대기할 때
    public static final int STATE_CONNECTING = 2; // 연결 중일 때
    public static final int STATE_CONNECTED = 3; // 연결이 완료됬을 때

    // 생성자
    public BluetoothService() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    // 상태 설정
    private synchronized void setState(int state){
        mState = state;
    }

    // 상태 불러오기
    private synchronized int getState(){
        return mState;
    }

    // 연결 실패했을 때
    private void connectionFailed(){
        setState(STATE_LISTEN);
    }

    // 연결을 잃었을 때
    private void connectionLost(){
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    public synchronized void start(){

        if(mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(mAcceptThread == null){
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);

    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(STATE_NONE);
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.write(out);
    }
    class AcceptThread extends Thread {
        // 서버소켓
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            super.run();
            BluetoothSocket socket = null;

            // 연결이 될 때 까지 요청
            while(mState != STATE_CONNECTED){
                try{
                    socket = mmServerSocket.accept();
                }catch(IOException e){
                    break;
                }

                // 연결을 수락했을 때
                if(socket != null){
                    synchronized (BluetoothService.this){
                        switch (mState){
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                //연결 시도 중이면, 연결 스레드를 시작
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // 준비되지 않았거나 이미 연결이 되었을 때 소켓을 닫는다
                                try{
                                    socket.close();
                                }catch (IOException e) { }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel(){
            try{
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

    class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            mmDevice = device;
            BluetoothSocket tmp = null;

            try{
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e) { }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            super.run();

            // 속도가 느려질 수 있으니 장치 검색을 취소
            mAdapter.cancelDiscovery();

            try{
                mmSocket.connect();
            } catch (IOException e){
                connectionFailed();
                // 소켓 닫기
                try {
                    mmSocket.close();
                }catch (IOException e2){ }

                // 연결 대기모드 다시 시작
                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this){
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){ }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e){ }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = mmInStream.read(buffer);
                } catch (IOException e){
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer){
            try{
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
