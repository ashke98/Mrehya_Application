package com.mrehya.Dashboards;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mrehya.DashboardPackage.DashboardLists;
import com.mrehya.Exams.Exam;
import com.mrehya.Exams.ExamAdapter;
import com.mrehya.Exams.Test;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Helper.PolicyGuard;
import com.mrehya.R;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by hgjhghgjh on 10/22/2018.
 */

public class DashboardFragment2 extends Fragment {

    private String Language="fa";
    private Context context;

    private RecyclerView recyclerView;
    private ExamAdapter adapter;
    private List<Exam> List;
    private JSONObject ExamList;
    private View view;
    private TextView txtEmptyExams;
    //https://www.androidhive.info/category/material-design/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PolicyGuard.Allow();
        view = inflater.inflate(R.layout.fragment_dashboard2, container, false);
        context =view.getContext();
        Paper.init(context);
        Language = (String) Paper.book().read("language");
        DashboardLists dl = new DashboardLists(context, false);
        ExamList = DashboardLists.Testsdata;
        txtEmptyExams = (TextView) view.findViewById(R.id.txtEmptyExams);
        setView();

        updateView();
        return view;
    }
     private void setView(){
         Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
         ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
         //setSupportActionBar(toolbar);

         initCollapsingToolbar();

         recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


         List = new ArrayList<>();
         adapter = new ExamAdapter(context, List);
         ////

         RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
         recyclerView.setLayoutManager(mLayoutManager);
         recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
         recyclerView.setItemAnimator(new DefaultItemAnimator());
         recyclerView.setAdapter(adapter);

         prepareAlbums();

         try {
             Glide.with(this).load(R.drawable.test).into((ImageView) view.findViewById(R.id.backdrop));
         } catch (Exception e) {
             e.printStackTrace();
         }
     }


    //EXAM APIS
    private void prepareAlbums() {
        if(ExamList.length()>0){
            txtEmptyExams.setVisibility(View.GONE);
            //Show_Hide_Exams(true);
            List<String> Allowed_Exams = Arrays.asList( "54", "53", "49","45","30","2","1","55","56");
            for (int i = 0; i < ExamList.length(); i++) {
                try {
                    final JSONObject c = ExamList.getJSONObject(i+"");
                    if(Allowed_Exams.contains(c.getInt("id") +""))
                    {
                        if(Language.equals("fa"))
                        {
                            Exam a = new Exam(c.getInt("id"), c.getString("title"),  c.getString("content"), c.getString("price"),c.getJSONObject("image").getString("thumb"),c.getString("paymentType"));
                            List.add(a);
                        }
                        else
                        {
                            Exam a = new Exam(c.getInt("id"), c.getString("title_en"),  c.getString("content"),  c.getString("price"),c.getJSONObject("image").getString("thumb"),c.getString("paymentType"));
                            List.add(a);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        else{
            txtEmptyExams.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    //methods
    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout)view.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
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

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    private void updateView() {
        Context context = LocaleHelper.setLocale(getActivity(), Language);
        Resources resources = context.getResources();
        txtEmptyExams.setText(resources.getText(R.string.examnotfound));
    }
}
