package com.mrehya.Shopping;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.MyTextView;
import com.mrehya.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Cart extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private ProgressDialog pDialog;
    MyTextView txtEmptyCart ;
    private ListView listView;
    private ListAdapterCart adapter;
    private ArrayList<Product> cartList;
    ArrayList<Integer> cartListPref;
    ArrayList<Integer> cartListCountPref;

    String Language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        PolicyGuard.Allow();
        txtEmptyCart = (MyTextView) findViewById(R.id.txtEmptyCart);
        txtEmptyCart.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.listViewCart);
        prefs = getApplicationContext().getSharedPreferences("ehya", 0);
        editor = prefs.edit();
        pDialog = new ProgressDialog(this);
        Language = updatelanguage(this);
        cartListPref = loadArray("cart_list",getApplicationContext());
        cartListCountPref = loadArray("cart_list_count",getApplicationContext());
        cartList = new ArrayList<>();
        adapter = new ListAdapterCart(cartList, getApplicationContext(),this,cartListCountPref,cartListPref);
        listView.setAdapter(adapter);
        Product_Api(Language);
    }
    private boolean isInCart(int id){
        return cartListPref.contains(id);
    }
    public boolean saveArray(ArrayList<Integer> array, String arrayName, Context mContext) {
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putInt(arrayName + "_" + i, array.get(i));
        editor.commit();
        return true;
    }
    public ArrayList<Integer> loadArray(String arrayName, Context mContext) {
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<Integer> array = new ArrayList<>();
        for(int i=0;i<size;i++)
            array.add(prefs.getInt(arrayName + "_" + i, 0));
        return array;
    }
    private String updatelanguage(Context context){
        Paper.init(context);
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");

        return language;
    }
    private void startDialog(){
        pDialog.setCancelable(false);
        if(Language.equals("fa"))
            pDialog.setMessage("گرفتن لیست آیتم\u200Cهای محصولات...");
        else
            pDialog.setMessage("Loading Products...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showDialog();
    }
    private void Product_Api(final String Language){
        startDialog();
        String tag_string_req = "req_product";
        HttpsTrustManager.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_Products, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "product Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONObject data = jObj.getJSONObject("data");
                    if(data.length()>0){
                        txtEmptyCart.setVisibility(View.GONE);
                        for (int i = 0; i < data.length()-1; i++) {
                            JSONObject c = data.getJSONObject(i+"");
                            JSONObject image = c.getJSONObject("image");
                            Log.d("TAG", c.toString());


                            if (isInCart(c.getInt("id"))){
                                Product a = new Product(c.getInt("id"),c.getString("price"),c.getString("title"),
                                        image.getString("thumb"),image.getString("preview"),c.getString("short_description")
                                        ,c.getInt("stock"));
                                cartList.add(a);
                            }


                        }

                        adapter.notifyDataSetChanged();
                    }
                    else{
                        if(Language.equals("fa"))
                            txtEmptyCart.setText("سبد خرید\u200C خالی است!\"");
                        else
                            txtEmptyCart.setText("Empty products!");
                        txtEmptyCart.setVisibility(View.VISIBLE);
                    }
                    Log.d("TAG", "No Object recieved!");
                    hideDialog();
                } catch (JSONException e) {
                    // JSON error
                    Log.d("TAG", "error 1 " + e.getMessage());
                    //e.printStackTrace();
                    if(Language.equals("fa")){
                        txtEmptyCart.setText("سبد خرید\u200C خالی است!\"");
                        Toast.makeText(getApplicationContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                    }

                    else{
                        txtEmptyCart.setText("Empty products!");
                        Toast.makeText(getApplicationContext(), "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                    }
                    txtEmptyCart.setVisibility(View.VISIBLE);

                    hideDialog();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG",  "error2");
                if(Language.equals("fa")){
                    txtEmptyCart.setText("لیست محصولات\u200Cها خالی است");
                    Toast.makeText(getApplicationContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                }

                else{
                    txtEmptyCart.setText("Empty products!");
                    Toast.makeText(getApplicationContext(), "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                }
                txtEmptyCart.setVisibility(View.VISIBLE);
                hideDialog();
            }
        }) {
            //basic auth
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String, String>();
                // add headers <key,value>
                String credentials = AppConfig.AUTH_USERNAME+":"+AppConfig.AUTH_PASS;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.URL_SAFE|Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
