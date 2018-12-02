package com.mrehya.Resume.ResumeListAdapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Lang;
import com.mrehya.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by Rubick on 2/15/2018.
 */

public class ListAdapterSimpleLang extends BaseAdapter implements ListAdapter {
    private ArrayList<Lang> list = new ArrayList<>();
    private Context context;
    private Resources resources;
    private Activity activity;
    private ListView listView;
    private String language="fa";
    private List<String> levels, names;
    //new
    LinearLayout LinearLayoutitemlang1;

    public ListAdapterSimpleLang(ArrayList<Lang> list, Context context, Activity activity, ListView listView) {
        this.list = list;
        this.context = context;
        this.activity = activity;
        this.listView = listView;


        updateLanguage();
        context = LocaleHelper.setLocale(this.context  , language);
        resources = context.getResources();
        levels = Arrays.asList(resources.getStringArray(R.array.langLevels));
        names =   Arrays.asList(resources.getStringArray(R.array.langs));
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
            view = inflater.inflate(R.layout.list_item_lang, null);
        }
        Lang lang = list.get(position);
        //Handle TextView and display string from your list
        final TextView txtLang = (TextView)view.findViewById(R.id.txtLang);
        final TextView txtLangLevel = (TextView)view.findViewById(R.id.txtLangLevel);
        txtLang.setTextSize(15);
        txtLangLevel.setTextSize(15);
        if(Integer.parseInt(lang.getName())==0){
            txtLang.setText(names.get(0));
        }
        else{
            txtLang.setText(names.get(Integer.parseInt(lang.getName())-1));
        }
        if(Integer.parseInt(lang.getLevel())==0) {
            txtLangLevel.setText("0");
        }
        else{
            txtLangLevel.setText(levels.get(Integer.parseInt(lang.getLevel())-1));
        }
        //Handle buttons and add onClickListeners
        ImageButton btnDelete =view.findViewById(R.id.btnDeleteLang);
        ImageButton btnEdit = view.findViewById(R.id.btnEditLang);
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);

        //new
        LinearLayoutitemlang1=view.findViewById(R.id.LinearLayoutitemlang1);
        //updateView(updateLanguage());
        return view;
    }
    private void showDialog(final int Id, String oldItem, String oldItemLevel, final int index){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.list_dialog_lang);
        //final EditText txtEdit = dialog.findViewById(R.id.txtEditDialogLang);
        final Spinner spinner = dialog.findViewById(R.id.spinnerLangLevel);
        //txtEdit.setText(oldItem);
        final Spinner spinnerLang = dialog.findViewById(R.id.spinnerLang);
        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogLang);

        btnEdit.setVisibility(View.GONE);
        if(language.equals("fa")){
            ArrayAdapter<String> Leveladapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(R.array.langLevels)
            );
            Leveladapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            spinner.setAdapter(Leveladapter);

            ArrayAdapter<String> Langadapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    resources.getStringArray(R.array.langs)
            );
            Langadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            spinnerLang.setAdapter(Langadapter);
        }
        else
        {
            ArrayAdapter<String> Leveladapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    resources.getStringArray(R.array.langLevels)
            );
            Leveladapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            spinner.setAdapter(Leveladapter);

            ArrayAdapter<String> Langadapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    resources.getStringArray(R.array.langs)
            );
            Langadapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            spinnerLang.setAdapter(Langadapter);
        }


        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(oldItemLevel)){
                spinner.setSelection(i,true);
                break;
            }
        }
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lang lang = new Lang(txtEdit.getText().toString(),spinner.getSelectedItem().toString());
                Lang lang = new Lang((names.indexOf( spinnerLang.getSelectedItem().toString())+1)+""
                        ,(levels.indexOf(spinner.getSelectedItem().toString())+1)+"");
                lang.setId(Id);
                list.set(index,lang);
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
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this.context  , language);

        if(language.equals("fa")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitemlang1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitemlang1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitemlang1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitemlang1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
    }
}
