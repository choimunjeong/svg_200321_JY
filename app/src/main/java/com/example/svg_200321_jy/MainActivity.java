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
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
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


        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.startStation);    //객체 연결
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list));   //아답터에 연결



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
//        web.setInitialScale(230);                          //웹뷰 화면 비율 맞추기

        //웹뷰를 로드함
        web.loadUrl("file:///android_asset/index2.html");

        //자바스크립트에서 메시지를 받을 수 있게 함 + 글자 비교해서 이미지 나오도록
        web.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void send(final String msg){
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //넣을 text가 비어있는지 검사 후 넣기
                        //또, text배열에 같은 값이 있으면 안돼.
                        //또, text 자리가 꽉 차있으면 안내해줘야해.
                        for(i=0;i < 6; i++){
                            if(msg.equals(text[i].getText())){
                                dialog_Show();
                                break;
                            }
                            else if("야호".equals(text[i].getText()) ){
                                text[i].setText(msg);
                                break;
                            }
                            else if(i==5){
                                no_Show();
                                break;
                            }
                        }
                    }});
            }
        }, "android");

        //자바스크립트에서 메시지를 받을 수 있게 함 + 글자 비교해서 텍스트뷰에서 삭제하도록
        web.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void delete(final String msg){
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //지우려는 역을 text에서 찾고, 배열을 한칸씩 앞으로 당겨준다.
                        for(i=0;i < 6; i++){

                            //찾은 역이 있는 text번호(i)를 찾으면
                            if(msg.equals(text[i].getText())){

                                //한칸씩 앞으로 값을 보낸다.
                                for(int j=i; j < text.length; j++){
                                    if(j != text.length-1){
                                        text[j].setText(text[j+1].getText());
                                        //Toast.makeText(getApplicationContext(), "찾았다"+msg, Toast.LENGTH_SHORT).show();

                                        //다음 text에 값이 없으면 for문을 끝낸다.
                                        if(j < text.length && text[j+1].getText().equals("야호")){
                                            break;
                                        }
                                    }
                                    else{
                                        text[j].setText("야호");
                                        //Toast.makeText(getApplicationContext(), "끝났다"+msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            }
                                //Toast.makeText(getApplicationContext(), i+"다르대~"+msg, Toast.LENGTH_SHORT).show();
                        }
                    }});
            }
        }, "android2");

        web.setWebChromeClient(new WebChromeClient());


        //자동입력에서 항목을 터치했을 때, 키보드가 바로 내려감 + 웹뷰에서 해당역에 출경도 버튼 띄워짐-!!!!!!!!!!!!!!!!!!!!!!
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(autoCompleteTextView.getText().toString() != null) {
//                    Toast.makeText(getApplicationContext(), autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
                    web.loadUrl("javascript:setMessage('"+autoCompleteTextView.getText().toString()+"')");
                }

                //키보드 내림
                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
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


    //경유 추가하는데 이미 추가가 되어있다면 다이얼로그 띄움
    void dialog_Show(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("이미 추가한 경유역입니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }


    //경유 추가하는데 자리가 없으면 없다고 다이얼로그 띄움
    void no_Show(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("추가할 수 있는 개수를 초과했습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

}


