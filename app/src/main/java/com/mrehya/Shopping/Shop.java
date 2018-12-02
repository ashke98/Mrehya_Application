package com.mrehya.Shopping;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.DashboardLists;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Exams.ChooseExam;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Job;
import com.mrehya.MainActivity;
import com.mrehya.MyTextView;
import com.mrehya.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;

public class Shop extends AppCompatActivity {
    private String Language="fa";
    private ProgressDialog pDialog;
    private Context context;
    private DashboardLists dl;
    //ELEMENTS
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> shopList;
    public  TextView txtEmptyProductRequest;
    private MyTextView mytextShop;
    private View view;
    private Button btnToCart;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        PolicyGuard.Allow();
        dl = new DashboardLists(this,"Products");
        context =this;
        pDialog = new ProgressDialog(this);
        Paper.init(this);
        Language = updatelanguage(this);
        findViews();
        setLists();
        startDialog();
        new ProductTask().execute(0);
        updateView(Language);
    }
    private void findViews(){
        btnBack = (Button) findViewById(R.id.btnBack);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btnToCart = (Button) findViewById(R.id.btnToCart);
        txtEmptyProductRequest = (TextView) findViewById(R.id.txtEmptyProductRequest);
        mytextShop = (MyTextView) findViewById(R.id.mytextShop);
    }
    private void setLists(){
        shopList = new ArrayList<>();
        adapter = new ProductAdapter(getApplicationContext(), shopList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        btnToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shop.this,Cart.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.putExtra("frgToLoad", "0");
                startActivity(intent);
            }
        });
    }
    class ProductTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(2000);
                // some long running task will run here. We are using sleep as a dummy to delay execution
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        try {
                            Product_Api();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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

    //APIS
    private void Product_Api() throws JSONException {

                if (dl.Productsdata.length() > 0) {
                    JSONObject data = new JSONObject(dl.Productsdata.toString());
                    if (data.has("totalPage") && data.getString("totalPage").equals("0")) {
                        txtEmptyProductRequest.setVisibility(View.VISIBLE);
                        hideDialog();
                    }
                    else{
                        Log.e("Products 1", data.toString());
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject data = dl.Productsdata;
                                    txtEmptyProductRequest.setVisibility(View.GONE);
                                    for (int i = 0; i < data.length() - 1; i++) {
                                        JSONObject c = data.getJSONObject(i + "");
                                        JSONObject image = c.getJSONObject("image");
                                        //Log.d("TAG", c.toString());
                                        Product a = new Product(c.getInt("id"), c.getString("price"), c.getString("title"),
                                                image.getString("thumb"), image.getString("preview"), c.getString("short_description")
                                                , c.getInt("stock"));
                                        shopList.add(a);
                                    }
                                    adapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    txtEmptyProductRequest.setVisibility(View.VISIBLE);
                                }
                                hideDialog();
                            }
                        });
                        hideDialog();
                    }
                }
                else {
                    Product_Api_Caller();
                }
    }
    private void  Product_Api_Caller() throws JSONException {
        ApiHelpers ah = new ApiHelpers(context);
        ah.getCall(AppConfig.URL_Products, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (Language.equals("fa")) {
                                    txtEmptyProductRequest.setText("لیست محصولات\u200Cها خالی است");
                                    Toast.makeText(context, "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                } else {
                                    txtEmptyProductRequest.setText("Empty products!");
                                    Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                }
                                txtEmptyProductRequest.setVisibility(View.VISIBLE);
                                hideDialog();
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
                                Log.e("Products ok", dataString);
                                JSONObject Jobject = null;
                                try {
                                    Jobject = new JSONObject(dataString);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (Jobject.has("totalPage") && Jobject.getString("totalPage").equals("0")) {
                                        txtEmptyProductRequest.setVisibility(View.VISIBLE);
                                    } else {
                                    if (response.isSuccessful()) {
                                        try {
                                            JSONObject data = Jobject.getJSONObject("data");
                                            if (data.length() > 0) {
                                                dl.setProductsdata(data);
                                                txtEmptyProductRequest.setVisibility(View.GONE);
                                                for (int i = 0; i < data.length() - 1; i++) {
                                                    JSONObject c = data.getJSONObject(i + "");
                                                    JSONObject image = c.getJSONObject("image");
                                                    //Log.d("TAG", c.toString());
                                                    Product a = new Product(c.getInt("id"), c.getString("price"), c.getString("title"),
                                                            image.getString("thumb"), image.getString("preview"), c.getString("short_description")
                                                            , c.getInt("stock"));
                                                    shopList.add(a);
                                                }

                                                adapter.notifyDataSetChanged();
                                            } else {
                                                if (Language.equals("fa"))
                                                    txtEmptyProductRequest.setText("لیست محصولات\u200Cها خالی است!\"");
                                                else
                                                    txtEmptyProductRequest.setText("Empty products!");
                                                txtEmptyProductRequest.setVisibility(View.VISIBLE);
                                            }
                                            //Log.d("TAG", "No Object recieved!");
                                            hideDialog();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            if (Language.equals("fa")) {
                                                txtEmptyProductRequest.setText("لیست محصولات\u200Cها خالی است");
                                                Toast.makeText(context, "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                            } else {
                                                txtEmptyProductRequest.setText("Empty products!");
                                                Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                            }
                                            txtEmptyProductRequest.setVisibility(View.VISIBLE);
                                            hideDialog();
                                        }
                                    } else {
                                        if (Language.equals("fa")) {
                                            txtEmptyProductRequest.setText("لیست محصولات\u200Cها خالی است");
                                            Toast.makeText(context, "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                        } else {
                                            txtEmptyProductRequest.setText("Empty products!");
                                            Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                        }
                                        txtEmptyProductRequest.setVisibility(View.VISIBLE);
                                        hideDialog();
                                    }
                                }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                hideDialog();
                            }
                        });
                    }
                }
        );
    }


    //methods
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    private String updatelanguage(Context context){
        Paper.init(context);
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");

        return language;
    }
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();
        mytextShop.setText(resources.getString(R.string.Shop));
        btnToCart.setText(resources.getString(R.string.ToCart));
    }
    private void startDialog(){
        pDialog.setCancelable(false);
        if(Language.equals("fa"))
            pDialog.setMessage("گرفتن لیست آیتم\u200Cهای محصولات...");
        else
            pDialog.setMessage("Loading Hiring Advertisements...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showDialog();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /////CALL BACKS
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        if(Language.equals("fa")){
            Toast.makeText(this, "برای خروج دکمه بازگشت را دو بار کلیک کنید", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }


        mHandler.postDelayed(mRunnable, 2000);
    }


}