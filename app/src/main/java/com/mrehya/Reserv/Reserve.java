package com.mrehya.Reserv;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.sbahmani.jalcal.util.JalCal;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.goodiebag.horizontalpicker.HorizontalPicker;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.R;
import com.mrehya.SessionManager;
import com.mrehya.Shopping.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import me.anwarshahriar.calligrapher.Calligrapher;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Reserve extends AppCompatActivity {
    private String Language="fa";
    private Resources resources;
    private Context context;
    private SessionManager session;

    private ProgressDialog addbtnDialog;
    private ProgressDialog firstdayDialog;
    private ProgressDialog reservedayDialog;
    private Dialog dialog;

    private int cuurent_month, cuurent_year, cuurent_day;
    private Persian_Date_Methods dm;
    private PersianCalendar pc;
    private Day day;

    HashMap<Integer, String> reqs;
    //ELEMENTS
    private TableLayout tableButtons;
    private Spinner spinnerRequest;
    private List<HorizontalPicker.PickerItem> day_textItems;
    private HorizontalPicker hpText;
    private HorizontalScrollView hsv;
    private TextView tv_time_message, tv_month;
    private ApiHelpers ah;

    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PolicyGuard.Allow();
        setContentView(R.layout.activity_reserve);
        session = new SessionManager(getApplicationContext());
        updatelanguage();
        ah = new ApiHelpers(this);
        addbtnDialog = new ProgressDialog(this);
        addbtnDialog.setTitle("در حال بارگزاری...");
        firstdayDialog = new ProgressDialog(this);
        firstdayDialog.setTitle("در حال بارگزاری...");
        reservedayDialog = new ProgressDialog(this);
        reservedayDialog.setTitle("بررسی نتیجه رزرو...");

        findViews();
        setViews();
        removeTableViews();
        tv_time_message.setVisibility(View.GONE);
        //new GetbtnsTask().execute(0);
        scrolltoday();
    }
    private void findViews(){
        context = Reserve.this;
        dm = new Persian_Date_Methods();
        pc = new PersianCalendar();

        progressbar = (ProgressBar)findViewById(R.id.spin_kit);
        ThreeBounce doubleBounce = new ThreeBounce();
        progressbar.setIndeterminateDrawable(doubleBounce);

        makedialog("در حال بارگزاری..."  , addbtnDialog);
        makedialog("در حال بارگزاری..."  , firstdayDialog);
        showDialog(addbtnDialog);
        day = new Day(pc.year+"",pc.month+"",pc.day+"");
        Calligrapher calligrapher = new Calligrapher(this);
        calligrapher.setFont(this, "BHoma.ttf", true);
        calligrapher.setFont(getCurrentFocus(), "BHoma.ttf");
        cuurent_year    = pc.year;
        cuurent_month   = pc.month;
        cuurent_day     = pc.day;

        tableButtons = (TableLayout) findViewById(R.id.tableButtons);
        tv_time_message = (TextView) findViewById(R.id.tv_time_message);
        tv_month = (TextView) findViewById(R.id.tv_month);
        spinnerRequest = (Spinner) findViewById(R.id.spinnerRequest);
        reqs = new HashMap<Integer, String>();
    }
    private void setViews(){
        tv_month.setText(day.getYear() + " "+dm.get_month(cuurent_month).toString());
        hpText = (HorizontalPicker) findViewById(R.id.hpicker);
        HorizontalPicker.OnSelectionChangeListener listener = new HorizontalPicker.OnSelectionChangeListener() {
            @Override
            public void onItemSelect(HorizontalPicker picker, int index) {
                day = new Day(day.getYear(), day.getMonth(), index+1+"");
                removeTableViews();
                //showDialog(addbtnDialog);
                tv_time_message.setVisibility(View.GONE);
                new GetbtnsTask().execute(0);
            }
        };
        new GetreqsTask().execute(0);
        //DAY
        int daycount = 29;
        if(cuurent_month<=6)
            daycount=30;
        else if(cuurent_month==12)
            daycount=28;
        day_textItems = dm.day(daycount);
        hpText.setItems(day_textItems,day.getintDay()-1);
        hpText.setChangeListener(listener);
        hsv = (HorizontalScrollView) findViewById(R.id.hsv);
    }
    private void updatelanguage(){
        Paper.init(Reserve.this);
        //Default language is fa
        Language = Paper.book().read("language");
        if(Language==null)
            Paper.book().write("language", "fa");
    }

    class GetreqsTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(2000);
                get_reqs();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {

        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
    private void update_reqs(){
        String[] urlArray = new String[reqs.size()];
        if(reqs.size()>0)
            reqs.values().toArray(urlArray);
        ArrayAdapter<String> reqdapter = new ArrayAdapter<String>(
                this,
                R.layout.z_simple_spinner_item_center,
                urlArray
        );
        reqdapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_center);
        spinnerRequest.setAdapter(reqdapter);
    }
    class GetbtnsTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(2000);
                get_set_btns();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {

        }
        @Override
        protected void onPreExecute() {
            if(Language.equals("fa")){
                //Toast.makeText(Reserve.this, "در حال دریافت ساعات خالی", Toast.LENGTH_SHORT).show();
                showMessage("در حال دریافت ساعات خالی");
            }
            else{
                showMessage("Getting Free Hours");
                //Toast.makeText(Reserve.this, "Getting Free Hours", Toast.LENGTH_SHORT).show();
            }
            progressbar.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
    //APIS
    private void get_reqs(){
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Get_RESERVE_REQUEST+Language, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("get_reqs_reserve", e.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                String dataString = "";
                                try {
                                    dataString = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if(response.isSuccessful()){
                                        JSONObject json = new JSONObject(dataString);
                                        JSONArray data = json.getJSONArray("data");
                                        String success = json.getString("success");
                                        if (success.equalsIgnoreCase("true")) {
                                            for(int i=0;i<data.length();i++){
                                                reqs.put(data.getJSONObject(i).getInt("code"), data.getJSONObject(i).getString("name"));
                                            }
                                            update_reqs();
                                        }
                                    }
                                    else{

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
        );
    }
    private void get_set_btns(){
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Get_FreeHours+day.getdate(), false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("get_set_btns_reserve", e.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                String dataString = "";
                                try {
                                    dataString = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    JSONObject json = new JSONObject(dataString);
                                    JSONObject data = json.getJSONObject("data");

                                    String success = json.getString("success");
                                    if (success.equalsIgnoreCase("true")) {
                                        Day.setDayName(data.getString("dayName"));
                                        Day.setStartTime(data.getString("startTime"));
                                        Day.setEndTime(data.getString("endTime"));
                                        Day.setId(data.getString("id"));

                                        JSONObject hourobj = data.getJSONObject("hours");
                                        Iterator keys =  hourobj.keys();
                                        if(keys.hasNext())
                                        {
                                            removeTableViews();
                                            tv_time_message.setVisibility(View.GONE);
                                            TableRow tr= new TableRow(Reserve.this);
                                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                            int i = 0;
                                            while (keys.hasNext()){
                                                final String time = (String)keys.next();
                                                day.setHour(time);
                                                if((i)%3==0){
                                                    tableButtons.addView(tr);
                                                    tr = new TableRow(Reserve.this);
                                                    tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                                }
                                                Button b = new Button(Reserve.this);
                                                b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 3));
                                                b.setTextSize(20);
                                                b.setText(topersian(time));
                                                b.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        reserve_time(time);
                                                    }
                                                });
                                                tr.addView(b);
                                                i++;
                                                if(!keys.hasNext()){
                                                    tableButtons.addView(tr);
                                                }
                                            }
                                        }
                                        else{
                                            ////////////////////////////////show no hour found
                                        }
                                        hideDialog(addbtnDialog);
                                    } else {
                                        showMessage("وقت خالی برای این روز یافت نشد");
                                        if(Language.equals("fa")){
                                            //Toast.makeText(getApplicationContext(), "وقت خالی در این روز موجود نمی باشد", Toast.LENGTH_SHORT).show();
                                            showMessage("وقت خالی در این روز موجود نمی باشد");
                                        }
                                        else{
                                            showMessage("No free time found!");
                                            //Toast.makeText(getApplicationContext(), "No Free_time found!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    hideDialog(addbtnDialog);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                hideDialog(addbtnDialog);
                            }
                        });
                    }
                }
        );
    }
    private void get_firstday(final int temp_year,final int temp_month,final int temp_day,final int counter){
        if(counter!=7) {
            ah.getCall(AppConfig.URL_Get_FreeHours+jal_to_greg(temp_year, temp_month, temp_day), false).enqueue(
                    new Callback() {
                        Handler mainHandler = new Handler(context.getMainLooper());
                        @Override
                        public void onFailure(Call call, final IOException e) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("get_firstday_reserve", e.getMessage());
                                }
                            });
                        }
                        @Override
                        public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                            mainHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    String dataString = "";
                                    try {
                                        dataString = response.body().string();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        if(response.isSuccessful()){

                                        JSONObject json = new JSONObject(dataString);
                                        String success = json.getString("success");
                                        if (success.equalsIgnoreCase("true")) {

                                            day = new Day(temp_year+"",temp_month+"",temp_day+"");
                                            tv_time_message.setVisibility(View.GONE);
                                            removeTableViews();
                                            //new GetbtnsTask().execute(0);
                                            tv_month.setText(day.getYear() + " "+dm.get_month(day.getintMonth()).toString());
                                            day_textItems.clear();
                                            hpText.removeAllViews();
                                            for(int d=0;d<=dm.day_counts(day.getintMonth(), day.getintYear());d++) {
                                                day_textItems.add(new HorizontalPicker.TextItem(dm.get_day(d)));
                                            }
                                            hpText.setItems(day_textItems,day.getintDay()-1);

                                            if (day.getintDay()>24){
                                                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                                            }
                                            else if(day.getintDay()<=7)
                                                hsv.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                                            else if(day.getintDay()>7 && day.getintDay()<=15)
                                                hsv.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hsv.smoothScrollTo(hsv.getChildAt(0).getWidth()/4, 0);
                                                    }
                                                }, 100L);
                                            else if(day.getintDay()>15 && day.getintDay()<=22)
                                                hsv.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        hsv.smoothScrollTo(hsv.getChildAt(0).getWidth()/3, 0);
                                                    }
                                                }, 100L);
                                            else
                                                hsv.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Double width = hsv.getChildAt(0).getWidth()*0.6;
                                                        hsv.smoothScrollTo(width.intValue() , 0);
                                                    }
                                                }, 100L);

                                            } else {
                                                int newtemp_day=temp_day;
                                                int newtemp_month=temp_month;
                                                int newtemp_year=temp_year;
                                                newtemp_day+=1;
                                                if(newtemp_month<=6){
                                                    if (newtemp_day==31){
                                                        newtemp_day=1;
                                                        newtemp_month+=1;
                                                    }
                                                }
                                                if(newtemp_month<=7 && newtemp_month<12){
                                                    if (newtemp_day==32){
                                                        newtemp_day=1;
                                                        newtemp_month+=1;
                                                    }
                                                }
                                                if(newtemp_month==12){
                                                    if(newtemp_day==30){
                                                        newtemp_day=1;
                                                        newtemp_month=1;
                                                        newtemp_year+=1;
                                                    }
                                                }
                                                get_firstday(newtemp_year, newtemp_month, newtemp_day, counter+1);
                                            }
                                        }
                                        else{
                                            //Log.e("Reserve first time", "error 1 " + e.getMessage());
                                            int newtemp_day=temp_day;
                                            int newtemp_month=temp_month;
                                            int newtemp_year=temp_year;
                                            newtemp_day+=1;
                                            if(newtemp_month<=6){
                                                if (newtemp_day==31){
                                                    newtemp_day=1;
                                                    newtemp_month+=1;
                                                }
                                            }
                                            if(newtemp_month<=7 && newtemp_month<12){
                                                if (newtemp_day==32){
                                                    newtemp_day=1;
                                                    newtemp_month+=1;
                                                }
                                            }
                                            if(newtemp_month==12){
                                                if(newtemp_day==30){
                                                    newtemp_day=1;
                                                    newtemp_month=1;
                                                    newtemp_year+=1;
                                                }
                                            }
                                            get_firstday(newtemp_year, newtemp_month, newtemp_day, counter+1);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //Log.e("Reserve first time", "error 1 " + e.getMessage());
                                        int newtemp_day=temp_day;
                                        int newtemp_month=temp_month;
                                        int newtemp_year=temp_year;
                                        newtemp_day+=1;
                                        if(newtemp_month<=6){
                                            if (newtemp_day==31){
                                                newtemp_day=1;
                                                newtemp_month+=1;
                                            }
                                        }
                                        if(newtemp_month<=7 && newtemp_month<12){
                                            if (newtemp_day==32){
                                                newtemp_day=1;
                                                newtemp_month+=1;
                                            }
                                        }
                                        if(newtemp_month==12){
                                            if(newtemp_day==30){
                                                newtemp_day=1;
                                                newtemp_month=1;
                                                newtemp_year+=1;
                                            }
                                        }
                                        get_firstday(newtemp_year, newtemp_month, newtemp_day, counter+1);
                                    }
                                }
                            });
                        }
                    }
            );
        }
        else{
            //FIRST DAY NOT FOUND
            removeTableViews();
            showMessage("اولین وقت خالی تنظیم نشده است");
            hideDialog(firstdayDialog);
        }
    }

    //methods
    public void prev_month(View view){
        if(reqs.size()==0){
            new GetreqsTask().execute(0);
        }
        day.setMonth((day.getintMonth()-1)+"");

        if(day.getintMonth() < 1)
        {
            day.setMonth(12+"");
            day.setYear((day.getintYear()-1)+"");
        }
        TextView tv_month = (TextView) findViewById(R.id.tv_month);
        tv_month.setText(day.getintYear() + " "+ dm.get_month(day.getintMonth()).toString());

        day_textItems.clear();
        hpText.removeAllViews();
        for(int d=0;d<=dm.day_counts(day.getintMonth(), day.getintYear());d++) {
            day_textItems.add(new HorizontalPicker.TextItem(dm.get_day(d)));
        }
        HorizontalPicker hpText = (HorizontalPicker) findViewById(R.id.hpicker);
//        if(!(is_today())){
//            day.setDay(1+"");
//        }
        hpText.setItems(day_textItems,day.getintDay()-1);
        hsv.postDelayed(new Runnable() {
            @Override
            public void run() {
                hsv.smoothScrollTo(day.getintDay()-1, 0);
                //hsv.fullScroll(HorizontalScrollView.FOCUS);
            }
        }, 100L);
    }
    public void next_month(View view){
        if(reqs.size()==0){
            new GetreqsTask().execute(0);
        }
        day.setMonth(day.getintMonth ()+ 1+"");
        if(day.getintMonth() >12) {
            day.setMonth("1");
            day.setYear(day.getintYear()+1+"");
        }
        TextView tv_month = (TextView) findViewById(R.id.tv_month);
        tv_month.setText(day.getYear() + " "+ dm.get_month(day.getintMonth()).toString());

        day_textItems.clear();
        hpText.removeAllViews();
        for(int d=0;d<=dm.day_counts(day.getintMonth(), day.getintYear());d++) {
            day_textItems.add(new HorizontalPicker.TextItem(dm.get_day(d)));
        }
        HorizontalPicker hpText = (HorizontalPicker) findViewById(R.id.hpicker);

        if(!(is_today())){
            day.setDay("1");
        }
        hpText.setItems(day_textItems,day.getintDay()-1);
        hsv.postDelayed(new Runnable() {
            @Override
            public void run() {
                hsv.smoothScrollTo(day.getintDay()-1, 0);
                //hsv.fullScroll(HorizontalScrollView.Focus);
            }
        }, 100L);
    }
    private boolean is_today() {
        if(day.getintYear() != cuurent_year)
            return false;
        if(day.getintMonth() != cuurent_month)
            return false;
        if(day.getintDay() != cuurent_day)
            return false;
        return true;
    }
    public void go_today(View view){
        day.setYear(cuurent_year+"");
        day.setMonth(cuurent_month+"");
        day.setDay(cuurent_day+"");
        tv_month.setText(day.getYear() + " "+ dm.get_month(day.getintMonth()).toString());
        removeTableViews();
        showDialog(addbtnDialog);
        tv_time_message.setVisibility(View.GONE);
        //new GetbtnsTask().execute(0);
        day_textItems.clear();
        hpText.removeAllViews();
        for(int d=0;d<=dm.day_counts(day.getintMonth(), day.getintYear());d++) {
            day_textItems.add(new HorizontalPicker.TextItem(dm.get_day(d)));
        }
        hpText.setItems(day_textItems,day.getintDay()-1);

        if (day.getintDay()>24){
            hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }
        else if(day.getintDay()<=7)
            hsv.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        else if(day.getintDay()>7 && day.getintDay()<=15)
            hsv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hsv.smoothScrollTo(hsv.getChildAt(0).getWidth()/4, 0);
                }
            }, 100L);
        else if(day.getintDay()>15 && day.getintDay()<=22)
            hsv.postDelayed(new Runnable() {
            @Override
            public void run() {
                hsv.smoothScrollTo(hsv.getChildAt(0).getWidth()/3, 0);
            }
        }, 100L);
        else
            hsv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Double width = hsv.getChildAt(0).getWidth()*0.6;
                    hsv.smoothScrollTo(width.intValue() , 0);
                }
            }, 100L);
    }
    public void go_firsttime(View view){
        int temp_year = cuurent_year;
        int temp_month = cuurent_month;
        int temp_day = cuurent_day;
        showDialog(firstdayDialog);
        tv_time_message.setVisibility(View.GONE);
        removeTableViews();
        get_firstday(temp_year,temp_month,temp_day,0);
    }


    private void removeTableViews(){
            tableButtons.removeAllViews();
    }
    ////////////////////////////////////////////////////////////////////////////MAIN ONE IS HERE/////////////////////////////////
    private void reserve_time(final String time) {
        dialog = new Dialog(Reserve.this);
        dialog.setContentView(R.layout.dialog_reserve);

        final TextView txtExamTitle = dialog.findViewById(R.id.txtExamTitle);
        final TextView txtdate = dialog.findViewById(R.id.txtdate);
        final TextView txthour = dialog.findViewById(R.id.txthour);
        final LinearLayout LinearLayoutday = dialog.findViewById(R.id.LinearLayoutday);
        final LinearLayout LinearLayouthour = dialog.findViewById(R.id.LinearLayouthour);
        final Button btn_doreserve = (Button) dialog.findViewById(R.id.btn_doreserve);
        final Button btn_close = (Button) dialog.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        btn_doreserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(reservedayDialog);
                do_reserve(time, day.getId(), getkey(spinnerRequest.getSelectedItem().toString()));
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //APIS2
    private void do_reserve(final String time, final String dayId, final String request){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", session.getUserDetails().getToken())
                .addFormDataPart("SaveSelectUser[select_date_id]", dayId)
                .addFormDataPart("SaveSelectUser[enterTime]", time)
                .addFormDataPart("SaveSelectUser[mobile]", session.getUserDetails().getMobile())
                .addFormDataPart("SaveSelectUser[fullname]",  session.getUserDetails().getFirstname() + " " + session.getUserDetails().getLastname())
                .addFormDataPart("SaveSelectUser[request]", request)
                .build();

        ah.PosttCall(AppConfig.URL_SEND_RESERVE, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("reserve_time_reserve", e.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                String dataString = "";
                                try {
                                    dataString = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if(response.isSuccessful()){
                                        JSONObject json = new JSONObject(dataString);
                                        JSONObject data = json.getJSONObject("data");
                                        String success = json.getString("success");
                                        if (success.equalsIgnoreCase("true")) {
                                            if(data.getString("message").equals("This time is taken by someone else")){
                                                hideDialog(reservedayDialog);
                                                Toast.makeText(context, "متاسفانه این وقت پر می باشد", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }else{
                                                hideDialog(reservedayDialog);
                                                Toast.makeText(context, "ساعت "+ time + " برای شما رزرو شد", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        } else {
                                            // Error in login. Get the error message
                                            Toast.makeText(getApplicationContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                            hideDialog(reservedayDialog);
                                        }
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                    }
                                    hideDialog(reservedayDialog);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                    hideDialog(reservedayDialog);
                                }
                                hideDialog(reservedayDialog);
                            }
                        });
                    }
                }
        );
    }

    private void showMessage(String message){
        tv_time_message.setVisibility(View.VISIBLE);
        tv_time_message.setText(message);
    }
    private String topersian(String input){
        return input.replace("1","١").replace("2","٢").replace("3","٣")
                .replace("4","۴").replace("5","۵").replace("6","۶")
                .replace("7","۷").replace("8","۸").replace("9","۹")
                .replace("0","۰");
    }
    private void  scrolltoday(){
        day_textItems.clear();
        hpText.removeAllViews();
        for(int d=0;d<=dm.day_counts(day.getintMonth(), day.getintYear());d++) {
            day_textItems.add(new HorizontalPicker.TextItem(dm.get_day(d)));
        }
        hpText.setItems(day_textItems,day.getintDay()-1);

        if (day.getintDay()>24){
            hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
        }
        else if(day.getintDay()<=7)
            hsv.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        else if(day.getintDay()>7 && day.getintDay()<=15)
            hsv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hsv.smoothScrollTo(hsv.getChildAt(0).getWidth()/4, 0);
                }
            }, 100L);
        else if(day.getintDay()>15 && day.getintDay()<=22)
            hsv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hsv.smoothScrollTo(hsv.getChildAt(0).getWidth()/3, 0);
                }
            }, 100L);
        else
            hsv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Double width = hsv.getChildAt(0).getWidth()*0.6;
                    hsv.smoothScrollTo(width.intValue() , 0);
                }
            }, 100L);
    }
    private void makedialog(String text, ProgressDialog dialog){
        dialog = new ProgressDialog(this);
        dialog.setTitle(text);
    }
    private void updatedialog(String text,  ProgressDialog dialog)
    {
        dialog.setTitle(text);
    }
    private String jal_to_greg(int year, int month, int day){
        //E/date: Tue Aug 07 00:00:00 GMT+04:30 2018
        try {
            //get_month_number2(Aug);
            String datetime =JalCal.jalaliToGregorian(year, month, day, 0, 0, 0).toString();
            return  datetime.split(" ")[5]+"-"+dm.get_month_number2(datetime.split(" ")[1])+"-"+datetime.split(" ")[2];
            // Log.e("date", date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private String getkey(String value){
        for (int o:
             reqs.keySet()) {
            if(reqs.get(o).equals(value)){
                return o+"";
            }
        }
        return 1+"";
    }
    private void showDialog( ProgressDialog dialog) {
        progressbar.setVisibility(View.VISIBLE);
//        if (dialog == null) {
//            dialog = new ProgressDialog(this);
//            dialog.setMessage("در حال بارگزاری...");
//            dialog.setIndeterminate(true);
//            dialog.show();
//        }
//        else{
//            if (!dialog.isShowing())
//                dialog.show();
//        }
    }
    private void hideDialog( ProgressDialog dialog) {
        progressbar.setVisibility(View.GONE);
//        if (dialog != null) {
//            if (dialog.isShowing())
//                dialog.dismiss();
//        }
    }
}
