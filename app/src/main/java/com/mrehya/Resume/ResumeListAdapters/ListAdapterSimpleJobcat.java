package com.mrehya.Resume.ResumeListAdapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mrehya.R;

import java.util.ArrayList;
import java.util.Arrays;

import io.paperdb.Paper;

/**
 * Created by Rubick on 2/15/2018.
 */

public class ListAdapterSimpleJobcat extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private ListView listView;
    private String language="fa";


    public ListAdapterSimpleJobcat(ArrayList<String> list, Context context, Activity activity, ListView listView) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_jobcat, null);
        }
        String jobcat = list.get(position);
        //Handle TextView and display string from your list
        final TextView listItemText = (TextView)view.findViewById(R.id.txtJobcat);
        listItemText.setTextSize(15);
        listItemText.setText(jobcat);
        //Handle buttons and add onClickListeners
        ImageButton btnDelete =view.findViewById(R.id.btnDeleteJobcat);
        ImageButton btnEdit = view.findViewById(R.id.btnEditJobcat);
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);

        return view;
    }
    private void showDialog(String oldItem, final int index){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.list_dialog_lang);
       // final EditText txtEdit = dialog.findViewById(R.id.txtEditDialogLang);
        final Spinner spinnerLang = dialog.findViewById(R.id.spinnerLang);
        //txtEdit.setText(oldItem);
        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogLang);
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        Resources resources = context.getResources();
        ArrayAdapter<String> Jobcatadapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(resources.getStringArray(R.array.job_status_arrays)));
        updateLanguage();
        if(language.equals("fa")) {
            Jobcatadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
        }
        else{
            Jobcatadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
        }
        spinnerLang.setAdapter(Jobcatadapter);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //
                //list.set(index,txtEdit.getText().toString());
                list.set(spinnerLang.getSelectedItemPosition(),spinnerLang.getSelectedItem().toString());
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    private String updateLanguage(){
        //Default language is fa
        language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
}
