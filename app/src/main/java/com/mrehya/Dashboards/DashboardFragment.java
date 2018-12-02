package com.mrehya.Dashboards;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mrehya.DashboardPackage.DashboardLists;
import com.mrehya.Exams.Test;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.Hire.HireCompanies;
import com.mrehya.MyTextView;
import com.mrehya.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.paperdb.Paper;


public class DashboardFragment extends Fragment {

    //new
    private MyTextView mytextPsycoTest,mytextHireBestCompanies,
            mytextPsycoTests1,mytextPsycoTests2,mytextPsycoTests3,mytextPsycoTests4,mytextPsycoTests5,mytextPsycoTests6;
    private String Language="fa";

    private ImageView imgViewE1,imgViewE2,imgViewE3,imgViewE4,imgViewE5,imgViewE6;
    private Boolean show = true;

    private TextView txtEmptyExams;

    private RecyclerView recyclerView;
    private dashAdapter adapter;
    private List<HireCompanies> companyList;
    Context context;
    RelativeLayout test1,test2,test3,test4,test5,test6 ;

    private ProgressDialog pDialog;
    private TextView txtEmptyCompanies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PolicyGuard.Allow();
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        context =view.getContext();
        Paper.init(context);
        Language = (String) Paper.book().read("language");
        //context = LocaleHelper.setLocale(context, Language);
        DashboardLists dl = new DashboardLists(context, false);
        test1 = (RelativeLayout) view.findViewById(R.id.test1);
        test2 = (RelativeLayout) view.findViewById(R.id.test2);
        test3 = (RelativeLayout) view.findViewById(R.id.test3);
        test4 = (RelativeLayout) view.findViewById(R.id.test4);
        test5 = (RelativeLayout) view.findViewById(R.id.test5);
        test6 = (RelativeLayout) view.findViewById(R.id.test6);

        pDialog = new ProgressDialog(context);
        setToSquare(test1);
        setToSquare(test2);
        setToSquare(test3);
        setToSquare(test4);
        setToSquare(test5);
        setToSquare(test6);
        txtEmptyExams = (TextView) view.findViewById(R.id.txtEmptyExams);
        companyList = new ArrayList<>();
        adapter = new dashAdapter(getContext(), companyList);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager mLayoutManager;
        if(Language.equals("fa")) {
            mLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }

        }
        else{
            mLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //new
        mytextPsycoTest =  view.findViewById(R.id.mytextPsycoTest);
        mytextHireBestCompanies =  view.findViewById(R.id.mytextHireBestCompanies);
        mytextPsycoTests1 =  view.findViewById(R.id.mytextPsycoTests1);
        mytextPsycoTests2 =  view.findViewById(R.id.mytextPsycoTests2);
        mytextPsycoTests3 =  view.findViewById(R.id.mytextPsycoTests3);
        mytextPsycoTests4 =  view.findViewById(R.id.mytextPsycoTests4);
        mytextPsycoTests5 =  view.findViewById(R.id.mytextPsycoTests5);
        mytextPsycoTests6 =  view.findViewById(R.id.mytextPsycoTests6);

        txtEmptyCompanies =  view.findViewById(R.id.txtEmptyCompanies);

        imgViewE1 = view.findViewById(R.id.imgViewE1);
        imgViewE2 = view.findViewById(R.id.imgViewE2);
        imgViewE3 = view.findViewById(R.id.imgViewE3);
        imgViewE4 = view.findViewById(R.id.imgViewE4);
        imgViewE5 = view.findViewById(R.id.imgViewE5);
        imgViewE6 = view.findViewById(R.id.imgViewE6);

        //new
        updateView();

        startDialog();
        new ApiTask(context).execute(0);
        return view;
    }
    class ApiTask extends AsyncTask<Integer, Integer, String> {
        private Context context2;
        public ApiTask(Context context) {
            this.context2=context;
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(300);
                // some long running task will run here. We are using sleep as a dummy to delay execution
                set_Content(context2);
                set_Content_Company(context2);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            hideDialog();
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
    //EXAM
    public void set_Content(final Context context) throws JSONException {
         getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {
                try {
                    JSONObject data = DashboardLists.Testsdata;
        if(data.length()>0){
            txtEmptyExams.setVisibility(View.GONE);
            Show_Hide_Exams(true);
            Random rand = new Random();
            int length = data.length()-1;
            if(length>6){
                length=6;
            }
            List<Integer> Seen_examsList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                int number = 0;
                do {
                    number=rand.nextInt(data.length()-1);
                }
                while(Seen_examsList.contains(number));
                final int take = number;
                Seen_examsList.add(take);
                final JSONObject c = data.getJSONObject(take+"");
                //Log.d("Fragment part String: ", c.toString());


                switch (i){
                    case 0:
                        if(Language.equals("fa"))
                            mytextPsycoTests1.setText(c.getString("title"));
                        else
                            mytextPsycoTests1.setText(c.getString("title_en"));
                        if(c.getJSONObject("image").getString("thumb").length()>1) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(context)
                                                .load(c.getJSONObject("image").getString("thumb"))
                                                .asBitmap()
                                                .placeholder(R.drawable.logo_eng)
                                                .error(R.drawable.logo_eng)
                                                .into(new BitmapImageViewTarget(imgViewE1) {
                                                          @Override
                                                          protected void setResource(Bitmap resource) {
                                                              super.setResource(resource);
                                                          }
                                                      }

                                                );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                            imgViewE1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });

                            mytextPsycoTests1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });

                        break;
                    case 1:
                        if(Language.equals("fa"))
                            mytextPsycoTests2.setText(c.getString("title"));
                        else
                            mytextPsycoTests2.setText(c.getString("title_en"));
                        if(c.getJSONObject("image").getString("thumb").length()>1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(context)
                                                .load(c.getJSONObject("image").getString("thumb"))
                                                .asBitmap()
                                                .placeholder(R.drawable.logo_eng)
                                                .error(R.drawable.logo_eng)
                                                .into(new BitmapImageViewTarget(imgViewE2) {
                                                          @Override
                                                          protected void setResource(Bitmap resource) {
                                                              super.setResource(resource);
                                                          }
                                                      }
                                                );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                            imgViewE2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });
                            mytextPsycoTests2.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });

                        break;
                    case 2:
                        if(Language.equals("fa"))
                            mytextPsycoTests3.setText(c.getString("title"));
                        else
                            mytextPsycoTests3.setText(c.getString("title_en"));
                        if(c.getJSONObject("image").getString("thumb").length()>1) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(context)
                                                .load(c.getJSONObject("image").getString("thumb"))
                                                .asBitmap()
                                                .placeholder(R.drawable.logo_eng)
                                                .error(R.drawable.logo_eng)
                                                .into(new BitmapImageViewTarget(imgViewE3) {
                                                          @Override
                                                          protected void setResource(Bitmap resource) {
                                                              super.setResource(resource);
                                                          }
                                                      }
                                                );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                            imgViewE3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });
                            mytextPsycoTests3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });

                        break;
                    case 3:
                        if(Language.equals("fa"))
                            mytextPsycoTests4.setText(c.getString("title"));
                        else
                            mytextPsycoTests4.setText(c.getString("title_en"));
                        if(c.getJSONObject("image").getString("thumb").length()>1) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(context)
                                                .load(c.getJSONObject("image").getString("thumb"))
                                                .asBitmap()
                                                .placeholder(R.drawable.logo_eng)
                                                .error(R.drawable.logo_eng)
                                                .into(new BitmapImageViewTarget(imgViewE4) {
                                                          @Override
                                                          protected void setResource(Bitmap resource) {
                                                              super.setResource(resource);
                                                          }
                                                      }
                                                );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                            imgViewE4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });
                            mytextPsycoTests4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });

                        break;
                    case 4:
                        if(Language.equals("fa"))
                            mytextPsycoTests5.setText(c.getString("title"));
                        else
                            mytextPsycoTests5.setText(c.getString("title_en"));
                        if(c.getJSONObject("image").getString("thumb").length()>1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(context)
                                                .load(c.getJSONObject("image").getString("thumb"))
                                                .asBitmap()
                                                .placeholder(R.drawable.logo_eng)
                                                .error(R.drawable.logo_eng)
                                                .into(new BitmapImageViewTarget(imgViewE5) {
                                                          @Override
                                                          protected void setResource(Bitmap resource) {
                                                              super.setResource(resource);
                                                          }
                                                      }
                                                );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                            imgViewE5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });
                            mytextPsycoTests5.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });

                        break;
                    case 5:
                        if(Language.equals("fa"))
                            mytextPsycoTests6.setText(c.getString("title"));
                        else
                            mytextPsycoTests6.setText(c.getString("title_en"));
                        if(c.getJSONObject("image").getString("thumb").length()>1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(context)
                                                .load(c.getJSONObject("image").getString("thumb"))
                                                .asBitmap()
                                                .placeholder(R.drawable.logo_eng)
                                                .error(R.drawable.logo_eng)
                                                .into(new BitmapImageViewTarget(imgViewE6) {
                                                          @Override
                                                          protected void setResource(Bitmap resource) {
                                                              super.setResource(resource);
                                                          }
                                                      }
                                                );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                            imgViewE6.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });
                            mytextPsycoTests6.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    send(take);
                                }
                            });

                        break;
                    default:
                        Log.d("no more", c.toString());
                }

            }
        }
        else{
            Show_Hide_Exams(false);
            if(Language.equals("fa"))
                txtEmptyExams.setText("لیست آزمون ها خالی است");
            else
                txtEmptyExams.setText("Empty Hire Advertisements!");
            txtEmptyExams.setVisibility(View.VISIBLE);
        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }});
    }
    public void Show_Hide_Exams(boolean show){
        if(show){
            test1.setVisibility(View.VISIBLE);
            test2.setVisibility(View.VISIBLE);
            test3.setVisibility(View.VISIBLE);
            test4.setVisibility(View.VISIBLE);
            test5.setVisibility(View.VISIBLE);
            test6.setVisibility(View.VISIBLE);
        }
        else{
            test1.setVisibility(View.GONE);
            test2.setVisibility(View.GONE);
            test3.setVisibility(View.GONE);
            test4.setVisibility(View.GONE);
            test5.setVisibility(View.GONE);
            test6.setVisibility(View.GONE);
        }
    }
    public void send(int pos) {
        Intent intent = new Intent(this.getActivity(),Test.class);
        intent.putExtra("examId",pos);
        context.startActivity(intent);
    }
    //COMPANY
    public void set_Content_Company(Context context) throws JSONException {
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {
                try {
                       JSONObject data = DashboardLists.Companiesdata;
                        if(data.length()>0){
                            txtEmptyCompanies.setVisibility(View.GONE);
                            //check language to add item to list
                            if(Language.equals("fa")) {
                                for (int i = 0; i < data.length() - 1; i++) {
                                    JSONObject c = data.getJSONObject(i + "");
                                    HireCompanies a = new HireCompanies(c.getString("brand_name"), c.getString("image"));
                                    companyList.add(a);
                                }
                            }
                            else{
                                for (int i = 0; i < data.length() - 1; i++) {
                                    JSONObject c = data.getJSONObject(i + "");
                                    HireCompanies a = new HireCompanies(c.getString("brand_name"), c.getString("image"));
                                    companyList.add(a);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            if(Language.equals("fa"))
                                txtEmptyCompanies.setText("لیست شرکت\u200Cها خالی است");
                            else
                                txtEmptyCompanies.setText("Empty Companies List!");
                            txtEmptyCompanies.setVisibility(View.VISIBLE);
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }});
    }



    //methods
    private void setToSquare(View mLayout){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;
        float dpWidth = displayMetrics.widthPixels ;
        final LinearLayout.LayoutParams layoutparams = (LinearLayout.LayoutParams) mLayout.getLayoutParams();
        double mlayouWidth = dpWidth *0.25 ;

        layoutparams.width = (int) mlayouWidth+10;
        layoutparams.height = (int) mlayouWidth;

        mLayout.setLayoutParams(layoutparams);
    }
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
    private void updateView() {
        Context context = LocaleHelper.setLocale(getActivity(), Language);
        Resources resources = context.getResources();

        mytextPsycoTest.setText(resources.getString(R.string.PsycoTest));
        mytextHireBestCompanies.setText(resources.getString(R.string.HireBestCompanies));

    }
    private void startDialog(){
        pDialog.setCancelable(true);
        if(Language.equals("fa"))
            pDialog.setMessage("در حال بارگزاری...");
        else
            pDialog.setMessage("Loading Companies...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        showDialog();
    }
    private void showDialog() {
        //if (!pDialog.isShowing())
            //pDialog.show();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
