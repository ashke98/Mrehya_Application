package com.mrehya.Resume.ResumeListAdapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mrehya.Helper.LocaleHelper;
import com.mrehya.R;

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by Rubick on 2/15/2018.
 */

public class ListAdapterSimpleProes extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private Activity activity;
    private ListView listView;

    //new
    private LinearLayout LinearLayoutitempros1;
    TextView txtPro;


    public ListAdapterSimpleProes(ArrayList<String> list, Context context, Activity activity, ListView listView) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        if(list!=null)
            return list.size();
        else
            return 0;
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
            view = inflater.inflate(R.layout.list_item_proes, null);
        }

        //Handle TextView and display string from your list
        final TextView listItemText = (TextView)view.findViewById(R.id.txtPro);
        listItemText.setText(list.get(position));
        listItemText.setTextSize(15);
        //Handle buttons and add onClickListeners
        ImageButton btnDelete =view.findViewById(R.id.btnDeletePro);
        ImageButton btnEdit = view.findViewById(R.id.btnEditPro);
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);

        //new
        Paper.init(context);
        LinearLayoutitempros1=view.findViewById(R.id.LinearLayoutitempros1);
        txtPro = view.findViewById(R.id.txtPro);
        //updateView(updateLanguage());

        return view;
    }
    private void showDialog(String oldItem, final int index){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.list_dialog_proes);
        final EditText txtEdit = dialog.findViewById(R.id.txtEditDialogPro);
        txtEdit.setText(oldItem);
        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogPro);
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.set(index,txtEdit.getText().toString());
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
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this.context  , language);

        if(language.equals("fa")){
            txtPro.setGravity(Gravity.RIGHT|Gravity.CENTER);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitempros1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitempros1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else{
            txtPro.setGravity(Gravity.LEFT|Gravity.CENTER);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitempros1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitempros1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
    }
}
