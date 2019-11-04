package com.example.administrator.smart_watch;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.example.administrator.smart_watch.MainActivity.DRAW_SET;
import static com.example.administrator.smart_watch.MainActivity.WEATHER_VIEW;
import static com.example.administrator.smart_watch.MainActivity.mBluetoothService;
import static com.example.administrator.smart_watch.R.id.textView;

public class WeatherViewActivity extends AppCompatActivity {

    TextView tv_nowWeather;
    TextView tv_nowTemp;
    Document doc = null;

    char mCharDelimiter = '\0';

    String temp;
    String weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_view);

        tv_nowWeather = (TextView)findViewById(R.id.nowWeather);
        tv_nowTemp = (TextView)findViewById(R.id.nowTemp);

        GetXMLTask task = new GetXMLTask();
        task.execute("http://www.kma.go.kr/wid/queryDFS.jsp?zone=2817760000");
    }

    public class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;

            try{
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱
                doc.getDocumentElement().normalize();
            } catch (Exception e) {
                Toast.makeText(WeatherViewActivity.this, "데이터를 불러오는 중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }

            return doc;
        }

        @Override
        protected void onPostExecute(Document document) {

            String s = "";
            // data 태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = document.getElementsByTagName("data");
            // data 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환
            Node node = nodeList.item(0); //data 엘리먼트 노드
            Element fstElmnt = (Element)node;
            NodeList nameList = fstElmnt.getElementsByTagName("temp");
            Element nameElement = (Element)nameList.item(0);
            nameList = nameElement.getChildNodes();

            temp = ((Node)nameList.item(0)).getNodeValue();
            tv_nowTemp.setText(temp +"도");

            NodeList websiteList = fstElmnt.getElementsByTagName("wfKor");
            weather = websiteList.item(0).getChildNodes().item(0).getNodeValue();
            tv_nowWeather.setText(weather);

            if(weather.equals("맑음")){
                Toast.makeText(WeatherViewActivity.this, "맑음", Toast.LENGTH_SHORT).show();
                int temp1 = WEATHER_VIEW | 0x00;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);
            }

            if(weather.equals("구름 조금")){
                Toast.makeText(WeatherViewActivity.this, "구름조금", Toast.LENGTH_SHORT).show();
                int temp1 = WEATHER_VIEW | 0x01;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);
            }

            if(weather.equals("흐림")){
                Toast.makeText(WeatherViewActivity.this, "흐림", Toast.LENGTH_SHORT).show();
                int temp1 = WEATHER_VIEW | 0x02;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);
            }

            if(weather.equals("구름 많음")){
                Toast.makeText(WeatherViewActivity.this, "구름많음", Toast.LENGTH_SHORT).show();
                int temp1 = WEATHER_VIEW | 0x03;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);
            }

            if(weather.equals("비")){
                Toast.makeText(WeatherViewActivity.this, "비", Toast.LENGTH_SHORT).show();
                int temp1 = WEATHER_VIEW | 0x04;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);
            }

            if(weather.equals("눈")){
                Toast.makeText(WeatherViewActivity.this, "눈", Toast.LENGTH_SHORT).show();
                int temp1 = WEATHER_VIEW | 0x05;
                byte[] commandByte = new byte[2];
                commandByte[0] = (byte) temp1;
                commandByte[1] = (byte) mCharDelimiter;
                mBluetoothService.write(commandByte);
            }

            super.onPostExecute(document);
        }
    }
}
