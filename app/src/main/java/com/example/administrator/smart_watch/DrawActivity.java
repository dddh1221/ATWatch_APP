package com.example.administrator.smart_watch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import static android.R.attr.action;
import static com.example.administrator.smart_watch.MainActivity.DRAW_REMOVE;
import static com.example.administrator.smart_watch.MainActivity.DRAW_SET;
import static com.example.administrator.smart_watch.MainActivity.DRAW_X;
import static com.example.administrator.smart_watch.MainActivity.DRAW_Y;
import static com.example.administrator.smart_watch.MainActivity.mBluetoothService;

public class DrawActivity extends AppCompatActivity {

    GridView drawView;
    ImageAdapter imageAdapter;

    ImageView im_drawReset;

    char mCharDelimiter = '\0';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 전체화면 (상태바 지우기)

        drawView = (GridView)findViewById(R.id.draw);
        imageAdapter = new ImageAdapter(this);
        drawView.setAdapter(imageAdapter);

        im_drawReset = (ImageView)findViewById(R.id.drawReset);

        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                float currentX = motionEvent.getX();
                float currentY = motionEvent.getY();
                int position = drawView.pointToPosition((int)currentX, (int)currentY);

                if(action == MotionEvent.ACTION_DOWN) {

                }

                if(action == MotionEvent.ACTION_MOVE){
                    if(position >= 0) {
                        imageAdapter.onChange(position);
                        imageAdapter.notifyDataSetChanged();

                        int x = (position % 8) + 1;
                        int y = (position / 8) + 1;

                        int temp1 = DRAW_SET;
                        byte[] commandByte = new byte[2];
                        commandByte[0] = (byte) temp1;
                        commandByte[1] = (byte) mCharDelimiter;
                        mBluetoothService.write(commandByte);

                        int temp2 = DRAW_X | x;
                        byte[] xByte = new byte[2];
                        xByte[0] = (byte) temp2;
                        xByte[1] = (byte) mCharDelimiter;
                        mBluetoothService.write(xByte);

                        int temp3 = DRAW_Y | y;
                        byte[] yByte = new byte[2];
                        yByte[0] = (byte) temp3;
                        yByte[1] = (byte) mCharDelimiter;
                        mBluetoothService.write(yByte);
                    }
                }

                if(action == MotionEvent.ACTION_UP){

                }
                return false;
            }
        });

        im_drawReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<128;i++){
                    imageAdapter.offChange(i);
                }
                imageAdapter.notifyDataSetChanged();

                int temp1 = DRAW_REMOVE;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);
            }
        });
    }
}
