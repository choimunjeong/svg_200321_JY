package com.example.svg_200321_jy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String readStr = "";  //txt에서 가져온 문자를 담는다.
    LinearLayout addview;  //동적으로 text 추가할 레이아웃
    EditText et_Date;
    TextView ticket1;
    Calendar myCalendar = Calendar.getInstance();
    String time;  //달력날짜 받기
    SimpleDateFormat sdf;
    private List<String> list;  //데이터를 넣을 리스트 변수
    TextView c1,c2;
    Handler handler =new Handler();
    Button startStation_Btn;

    TextView[] text = new TextView[6];
    String startTxt, middleTxt, endTxt;
    Float xx, yy;
    int width, height;  // 해상도 값

    int i = 0;
    int j = 1;
    int stringtext[] = {R.id.text1,R.id.text2,R.id.text3,R.id.text4,R.id.text5,R.id.text6};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //검색 리스트 구현 부분
        list = new ArrayList<String>();            //리스트를 생성
        settingList();                             //리스트에 검색될 단어를 추가한다

        //객체 연결
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.startStation);

        //아답터에 연결
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list));

        //맵 버튼을 누르면->page678로 이동
//        Button go_page678 = (Button)findViewById(R.id.buttonOk);
//        go_page678.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Page678.class);
//                startActivity(intent);
//            }
//        });

        //editText를 선택하면 Date피커가 나온다.
        et_Date = (EditText) findViewById(R.id.Date);
        et_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, myDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //이용권 선택하면 팝업창 뜨는 부분
        ticket1 = (TextView) findViewById(R.id.ticket1);
        ticket1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTicket1();
            }
        });


        //-----------------------------------------------------웹뷰 구현 부분
        final WebView web = (WebView)findViewById(R.id.web);
        for( i=0; i<6; i++){
            text[i] = (TextView)findViewById(stringtext[i]);
        }

        //웹뷰 자바스크립트 사용가능하도록 선언
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setDisplayZoomControls(false);  //웹뷰 돋보기 없앰

        //웹뷰 줌기능
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);
        web.setWebViewClient(new WebViewClient());
        web.setInitialScale(230);                          //웹뷰 화면 비율 맞추기

        //웹뷰를 로드함
        web.loadUrl("file:///android_asset/index.html");

        //웹뷰에서 터치된 역의 화면좌표를 받는다
        web.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                xx = event.getX();
                yy = event.getY();
                Toast.makeText(getApplicationContext(), xx+"+"+yy, Toast.LENGTH_LONG).show();
                return false;
            }
        });

        //화면 해상도 구하기
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;

        //자바스크립트에서 메시지를 받을 수 있게 함 + 글자 비교해서 이미지 나오도록
        web.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void send(final String msg){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //int xx = (int)Double.parseDouble(x);
                        //int yy = (int)Double.parseDouble(y);
                        //web.scrollTo(xx-300, yy-300);
                        //Toast.makeText(getApplicationContext(), xx+"------"+yy, Toast.LENGTH_LONG).show();

                        //터치된 역이 화면 가장가리 가까이에 있으면 화면 안으로 밀어줌
                        if ( xx < 50 && yy < 50){ web.scrollBy(-150, -150); }
                        else if (  xx < 100 && yy < 50) { web.scrollBy(-100, -150); }
                        else if (  xx < 50 && yy < 100) { web.scrollBy(-150, -100); }
                        else if ( xx < 50) { web.scrollBy(-150, 0); }
                        else if ( yy < 50) { web.scrollBy(0, -150); }
                        else if ( (xx > 50 && xx < 100) && (yy > 50 && yy < 100)){ web.scrollBy(-100, -100); }
                        else if ( xx > 50 && xx < 100) { web.scrollBy(-100, 0); }
                        else if ( yy > 50 && yy < 100) { web.scrollBy(0, -100); }

                        else if ( xx > width-50  && yy > height-50){ web.scrollBy(150, 150); }
                        else if ( xx > width-50) { web.scrollBy(150, 0); }
                        else if ( yy > height-50) { web.scrollBy(0, 150); }
                        else if ( (xx < width-50 && xx > width-100) && (yy < height-50 && yy > height-100)){ web.scrollBy(100, 100); }
                        else if (xx < width-50 && xx > width-100) { web.scrollBy(100, 0); }
                        else if (yy < height-50 && yy > height-100) { web.scrollBy(0, 100); }

                        else { web.scrollBy(0,0); }

                        text[0].setText(msg);

                    }});
            }
        }, "android");



        //자동입력 버튼 누르면 웹뷰 지도에서 해당역 표시
        startStation_Btn = (Button)findViewById(R.id.startStation_Btn);
        startStation_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoCompleteTextView.getText().toString() != null) {
//                    Toast.makeText(getApplicationContext(), autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                    web.loadUrl("javascript:setMessage('"+autoCompleteTextView.getText().toString()+"')");
                }

                //키보드 내림
                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(startStation_Btn.getWindowToken(), 0);
            }
        });
    }


    //날짜 값을 받아온다.
    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    //선택된 날짜를 edittext에 적용시킨다.
    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText et_date = (EditText) findViewById(R.id.Date);
        time = sdf.format(myCalendar.getTime());

        et_date.setText(time);
    }


    //리스트에 검색될 단어를 추가한다. txt파일을 for문으로 쪼개서 넣었다.
    private void settingList(){
        AssetManager am = getResources().getAssets() ;
        InputStream is = null;
        try{
            is = am.open("InfoTrainStation.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = null;
            while(((str = reader.readLine()) != null)){ readStr += str +"\n"; }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] arr = readStr.split("\n");  //한 줄씩 자른다.
        for(int i=0; i<arr.length; i++){
            list.add(arr[i]);
        }
    }


    //이용권 다이얼로그 부분
    private void setTicket1(){
        final String[] oItems = {"3일권", "5일권", "7일권"};

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
        alt_bld.setItems(oItems, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ticket1.setText(oItems[which]);
            }
        })
                .setCancelable(false);

        AlertDialog alert = alt_bld.create();

        // 대화창 제목 설정
        alert.setTitle("구매한 티켓을 선택하세요");

        // 대화창 배경 색  설정
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(89,78,59)));

        alert.show();
    }
}