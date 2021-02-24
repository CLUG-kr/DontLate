package com.example.dontlate_ver1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;


public class PlanCalendar extends AppCompatActivity {

    Database db = new Database();


    //public String[] result1 = {"2021.02.05/인공지능 01분반/0/3/3", "2021.02.23/os/3/4/2", "2021.02.14/pl/3/3/1", "2021.02.23/pl/3/4/2", "2021.03.21/os/2/5/1", "2021.02.14/os/2/1/3", "2021.02.23/pl/3/4/2", "2021.02.23/pl/3/4/2", "2021.02.23/pl/3/4/2", "2021.02.23/pl/3/4/2", "2021.02.23/pl/3/4/2"};
    public ArrayList<String> result = new ArrayList<>();
    public ArrayList<String> dates = new ArrayList<String>();
    public ArrayList<String> plan1 = new ArrayList<String>();
    public ArrayList<String> week1 = new ArrayList<String>();
    public ArrayList<String> numOfVideo1 = new ArrayList<String>();
    public ArrayList<String> comOfVideo1 = new ArrayList<String>();
    public Button refresh_Btn, save_Btn, del_Btn;
    public ListView listView;
    public myAdapter adapter;
    int Year, Month, Day, key;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    ProgressBar progress;

    MaterialCalendarView materialCalendarView;
    WebView mWebView;
    ArrayList<String> path = new ArrayList<String>();
    ArrayList<String> name = new ArrayList<String>();
   // Database db = new Database();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        refresh_Btn = findViewById(R.id.refresh_Btn);
//        save_Btn = findViewById(R.id.save_Btn);
//        del_Btn = findViewById(R.id.del_Btn);
        listView = findViewById(R.id.listView);
        adapter = new myAdapter();


        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2020, 3, 1))
                .setMaximumDate(CalendarDay.from(2021, 12, 30))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator
        );

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Year = date.getYear();
                Month = date.getMonth() + 1;
                Day = date.getDay();
                ArrayList<String> today_plan = new ArrayList<String>();

                for (int i = 0; i < dates.size(); i++) {
                    String[] time = dates.get(i).split("\\.");
                    int year = Integer.parseInt(time[0]);
                    int month = Integer.parseInt(time[1]);
                    int dayy = Integer.parseInt(time[2]);
                    if (year != Year || month != Month || dayy != Day) {
                        ;
                    } else {
                        today_plan.add(result.get(i));
//                        if(i < result.size()){
//                            today_plan.add(result.get(i));
//                        }
                    }
                }

                if(today_plan.size() == 0){
                    listView.setVisibility(View.INVISIBLE);
                }
                else {
                    updateplan(today_plan);
                    listView.setAdapter(adapter);
                    listView.setVisibility(View.VISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            key = position;
                            showPlanAdder();
                        }
                    });
                }
            }
        });

//        save_Btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String tmp = "";
//                tmp = tmp.concat(String.valueOf(Year)).concat(".").concat(String.valueOf(Month)).concat(".").concat(String.valueOf(Day));
//                dates.add(tmp.concat(""));
//                showPlanAdder();
//
//            }
//        });

        refresh_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 progress = (ProgressBar) findViewById(R.id.progress2) ;
                 progress.setProgress(0);

                mWebView = (WebView) findViewById(R.id.webView);//xml 자바코드 연결
                mWebView.setWebChromeClient(new WebChromeClient());//웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
                mWebView.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
                mWebView.addJavascriptInterface(new PlanCalendar.MyJavascriptInterface(), "Android");

                final boolean[] first = {true};
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);");

                    }

                });

                mWebView.loadUrl("");

                Log.d(".msg", "3——————————————————————");
//                updatedate();
//                new ApiSimulator(dates).executeOnExecutor(Executors.newSingleThreadExecutor());
//                db.readData(new Database.dateCallback() {
//                    @Override
//                    public void onCallback(ArrayList<String> value) {
//                        ArrayList<String> data = new ArrayList<>();
//                        data = value;
//
//                        for(int i = 0 ; i < data.size() ; i++) {
//                            System.out.println(i + "번째 DATA : " + data.get(i));
//                        }
//                        result = data;
//                        updatedate(data);
//                        new ApiSimulator(dates).executeOnExecutor(Executors.newSingleThreadExecutor());
//                    }
//                }, "dlwjddms");
            }
        });

    }

    private void updatedate(ArrayList<String> t_date){
        dates = new ArrayList<String>();
            for(int i = 0; i < t_date.size(); i++){
                String tmp ="";
                String[] test = t_date.get(i).split("/");
            dates.add(tmp.concat(test[0]));
        }
    }

    private void updateplan(ArrayList<String> t_plan){
        plan1 = new ArrayList<String>();
        week1 = new ArrayList<String>();
        numOfVideo1 = new ArrayList<String>();
        comOfVideo1 = new ArrayList<String>();
//        if(t_plan.size() != 0) {
//            plan1.add(("").concat("과목"));
//            plan1.add(("").concat("주차시"));
//            plan1.add(("").concat("동영상수"));
//            plan1.add(("").concat("완료"));
//        }
        for(int i = 0; i < t_plan.size(); i++) {
            String tmp = "";
            String[] test = t_plan.get(i).split("/");
            //plan1.add(tmp.concat("과목: ").concat(test[1]).concat(", 주차시: ").concat(test[2]).concat(", 동영상 수: ").concat(test[3]));
            plan1.add(tmp.concat(test[1]));
            week1.add(tmp.concat(test[2]));
            numOfVideo1.add(tmp.concat(test[3]));
            comOfVideo1.add(tmp.concat(test[4]));
            //plan1.add(test[1].concat(test[2]).concat(test[3]).concat(test[4]));
            //plan1.add(tmp.concat(test[2]));
            //plan1.add(tmp.concat(test[3]));
            //plan1.add(tmp.concat(test[4]));
        }
    }

    private void showPlanAdder(){
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout planLayout = (LinearLayout) vi.inflate(R.layout.activity_adder, null);
        final String tmp = "";
        //final EditText plan = (EditText)planLayout.findViewById(R.id.plan);
        final TextView subject = planLayout.findViewById(R.id.subject);
        final TextView week = planLayout.findViewById(R.id.week);
        final TextView numOfVideo = planLayout.findViewById(R.id.numOfVideo);
        final TextView comOfVideo = planLayout.findViewById(R.id.comOfVideo);

        for(int i = 0; i < plan1.size(); i++){
            if(i == key){
                subject.setText(plan1.get(i));
                week.setText(week1.get(i));
                numOfVideo.setText(numOfVideo1.get(i));
                comOfVideo.setText(comOfVideo1.get(i));
            }

        }

        new AlertDialog.Builder(this).setTitle("과목 정보").setView(planLayout).setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //edittext로 받은 string을 받아와서 저장하는 코드가 들어가야함.
//
//                result.add(String.valueOf(Year).concat(".").concat(String.valueOf(Month)).concat(".").concat(String.valueOf(Day).concat("/").concat(tmp[0]).concat("/ / / ")));
//                plan1.add(("").concat(tmp[0]));
//                gridView.setAdapter(adapter);
//                gridView.setVisibility(View.VISIBLE);
//                new ApiSimulator(dates).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
        }).show();
    }

    class myAdapter extends BaseAdapter{

        @Override
        public int getCount() { //데이터 갯수를 알려줌
            return plan1.size();
        }

        @Override
        public Object getItem(int position) { //각각의 아이템 인덱스를 통해 하나씩 받아옴
            return plan1.get(position);
        }

        @Override
        public long getItemId(int position) { //
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) { // 각각의 아이템을 위한 뷰를 지정해주는 역할
            TextView view = new TextView(getApplicationContext());
            view.setText(plan1.get(position));
            view.setTextSize(20.0f);
            view.setTextColor(Color.BLACK);
            return view;
        }
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>>{ //점찍는 거

        String[] Time_Result;

        ApiSimulator(ArrayList<String> Time_Result){
            this.Time_Result = Time_Result.toArray(new String[0]);
        }

        @Override
        protected List<CalendarDay> doInBackground(Void... voids) {
            try{
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            for(int i = 0; i < Time_Result.length; i++){
                String[] time = Time_Result[i].split("\\.");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year, month - 1, dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);

            }

            return dates;
        }


        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays){
            super.onPostExecute(calendarDays);

            if(isFinishing()){
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays, PlanCalendar.this));
        }

    }

    public class MyJavascriptInterface {

        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
//            Log.d(".msg", "——————————————————————");

//            Log.d(".msg",html);
            Elements element = Jsoup.parse(html).getElementsByClass("ic-DashboardCard");
            for (int i = 0; i < element.size(); i++) {
//                    Log.d(".msg", String.valueOf(element.get(i).attr("aria-label"))); // 각 수업 보드 가지고 있다 이제 들어가야
//                    Log.d(".msg", String.valueOf(element.get(i).getElementsByClass("ic-DashboardCard__link").attr("href"))); // 각 수업 보드 가지고 있다 이제 들어가야

                path.add(String.valueOf(element.get(i).getElementsByClass("ic-DashboardCard__link").attr("href")));
                name.add(String.valueOf(element.get(i).attr("aria-label")));



            }


            crawler(name, path, 0);

        }

        public void crawler(ArrayList<String> name,ArrayList<String> path, int idx) {
            if (path.size() <= idx) {
                db.readData(new Database.dateCallback() {
                    @Override
                    public void onCallback(ArrayList<String> value) {
                        ArrayList<String> data = new ArrayList<>();
                        data = value;

                        for(int i = 0 ; i < data.size() ; i++) {
                            System.out.println(i + "번째 DATA : " + data.get(i));
                        }
                        result = data;
                        updatedate(data);
                        new ApiSimulator(dates).executeOnExecutor(Executors.newSingleThreadExecutor());
                    }
                }, "dlwjddms");

                return;
            }
            WebView Wiew;
            Wiew = (WebView) findViewById(R.id.t_webView);//xml 자바코드 연결
            final int tmp = idx;
            final String course = name.get(idx);
            Wiew.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    WebSettings webSettings= Wiew.getSettings();
                    webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                    Wiew.setWebChromeClient(new WebChromeClient());
                    Wiew.getSettings().setJavaScriptEnabled(true);//자바스크립트 허용
                    Wiew.addJavascriptInterface(new PlanCalendar.YourJavascriptInterface(), "ECLASS");
                    Wiew.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);

                            Handler mhandler = new Handler();
                            mhandler.postDelayed(new Runnable() {
                                public void run() {
                                    view.loadUrl("javascript: window.ECLASS.getHtml(document.getElementById('tool_content').contentDocument.documentElement.innerHTML);");
                                }

                            }, 5000);

                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl("javascript: window.ECLASS.getHtml(document.getElementById('tool_content').contentDocument.documentElement.innerHTML);");
                            return true;
                        }
                    });

                    Wiew.loadUrl("https://eclass3.cau.ac.kr" + path.get(tmp) + "/external_tools/2");
//
                }

            });
//            try {
//                Thread.sleep(5000);
////
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    if(path.size()!=0)
                       progress.setProgress(100*((idx+1)/path.size()));
                    crawler(name, path, idx + 1);
                }

            }, 8000); // 0.5초후

        }
    }

    public class YourJavascriptInterface {
        @JavascriptInterface
        public void getHtml( String html) {

            try {
                String tml = Jsoup.parse(html).select("#root").attr("data-course_id");//.get(0);
                if(tml == null)
                    return;
                String course = "";
                ArrayList<String> date = new ArrayList<String>(); // 마감기한 날짜
                String user_id = "dlwjddms";
                String week = ""; // 12-2
                String finish = "";
                String total = "";

                for(int i=0; i<path.size();i++){
                    if(path.get(i).split("/")[2].equals(tml)){

                        course = name.get(i);
                        break;
                    }
                }
                Elements element = Jsoup.parse(html).getElementsByClass("xn-section");

                for (int i = 0; i < element.size(); i++) {
                    week = ""; // 12-2
                    finish = "";
                    total = "";

//                    Log.d(".mmsg", String.valueOf(element.get(i).getElementsByClass("xnslh-section-title").select("span").text()));
                    if(element.get(i).getElementsByClass("xnslh-section-title").select("span")!=null)
                        week =  String.valueOf(element.get(i).getElementsByClass("xnslh-section-title").select("span").text());
//                    Log.d(".mmsg", String.valueOf(element.get(i).getElementsByClass("xnslh-section-opendate-date").select("span").text()));
                    if (element.get(i).getElementsByClass("xnslh-section-opendate-count").first() != null)
                        finish =String.valueOf(element.get(i).getElementsByClass("xnslh-section-opendate-count").first().text());
//                        Log.d(".mmsg", String.valueOf(element.get(i).getElementsByClass("xnslh-section-opendate-count").first().text()));
                    if (element.get(i).getElementsByClass("xnslh-section-opendate-total").first() != null)
                        total =  String.valueOf(element.get(i).getElementsByClass("xnslh-section-opendate-total").first().text());
//                        Log.d(".mmsg", String.valueOf(element.get(i).getElementsByClass("xnslh-section-opendate-total").first().text()));
                    Elements ele = element.get(i).getElementsByClass("xn-subsection-learn");
                    String prev = "";

                    for (int j = 0; j < ele.size(); j++) {
                        String tmp ="";
                        //차시
//                        Log.d(".mmsg", String.valueOf(ele.get(j).getElementsByClass("xnsl-subsection-title").first().text()));
//                        if (ele.get(j).getElementsByClass("xnci-component-title").first() != null)
//                            Log.d(".mmsg", String.valueOf(ele.get(j).getElementsByClass("xnci-component-title").first().text()));

                        if (ele.get(j).getElementsByClass("xnci-component-description-row-right").first() != null){
                            tmp = String.valueOf(ele.get(j).getElementsByClass("xnci-component-description-row-right").text());
                            if(!tmp.equals(prev) && tmp !=null){
                                date.add(tmp);
                                prev = tmp;
//                                Log.d(".mmmsg",prev);
//                                Log.d(".mmmsg",tmp);
                            }else{

                            }
                        }
//                            Log.d(".mmsg", String.valueOf(ele.get(j).getElementsByClass("xnci-component-description-row-right").select("span").text()));
//                        if (ele.get(j).getElementsByClass("xnci-video-duration").first() != null)
//                            Log.d(".mmsg", String.valueOf(ele.get(j).getElementsByClass("xnci-video-duration").first().text()));
                        if (ele.get(j).getElementsByClass("xnci-info.top-value").first() != null){
                            Log.d(".mmsg", String.valueOf(ele.get(j).getElementsByClass("xnci-info.top-value").first().text()));

                        }
//
                    }

                    for(int k=0; k<date.size();k++){
                        db.insertWeek(user_id,course,week,change_finish(date.get(k)),Integer.valueOf(total),Integer.valueOf(finish));
//                        Log.d(".mmmsg",change_finish(date.get(k)));
//                        String tmp = String.valueOf(new Integer(k+1));
//                        Log.d(".mmmsg",week+" - "+tmp);
//                        Log.d(".mmmsg",finish);
//                        Log.d(".mmmsg",total);
//                        Log.d(".mmmsg",course);
//                        Log.d(".mmmsg",user_id);
//                        Log.d(".mmmsg","----------------------------");
                    }
                    date = new ArrayList<String>();
                }

                ArrayList<String> eclass = new ArrayList<String>();

            } catch (Exception e) {
                Log.d(".msg", String.valueOf(e));
            }
//
            return;
        }

        String change_finish(String input){

            String[] token = input.split("일");

            ArrayList<String> dateList = new ArrayList<>();

            // 홀수의 위치에 날짜가 존재함함
            for(int i = 0 ; i < token.length ; i++){
                //System.out.println(token[i]);

                if(i%2==1){
                    String ret ="2020";
                    String[] temp = token[i].split("월");

                    for(int j = 0 ; j < temp.length ; j++){
                        temp[j]= temp[j].trim();
                        if(temp[j].charAt(0)==':'){
                            temp[j] = temp[j].substring(1, temp[j].length());
                        }
                        temp[j]= temp[j].trim();
                        ret = ret+"."+temp[j];
                    }

                    if(!dateList.contains(ret)){
                        dateList.add(ret);
                    }
                }


            }
            return dateList.get(0);
        }
    }

}