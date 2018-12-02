package com.mrehya;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Reserv.PersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;

import io.paperdb.Paper;

/**
 * Created by Rubick on 2/15/2018.
 */

public class ListAdapterEducation extends BaseAdapter implements ListAdapter {
    private ArrayList<Education> list = new ArrayList<Education>();
    private Context context;
    private Activity activity;
    private ListView listView;
    private String language="fa";
    ArrayList<String> years;
    private int thisYear;
    private Resources resources;
    //new
    LinearLayout LinearLayoutitemeducation1;
    TextView txtEducationTitle;


    public ListAdapterEducation(ArrayList<Education> list, Context context, Activity activity, ListView listView) {
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
            view = inflater.inflate(R.layout.list_item_education, null);
        }
        updateLanguage();
        setLists();
        LinearLayout LinearLayouttop=(LinearLayout)view.findViewById(R.id.LinearLayouttop);
        LinearLayout LinearLayoutbot=(LinearLayout)view.findViewById(R.id.LinearLayoutbot);

        resources = context.getResources();
        if(language.equals("fa")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayouttop.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutbot.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            LinearLayouttop.setGravity(Gravity.RIGHT);
            LinearLayoutbot.setGravity(Gravity.RIGHT);
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LinearLayouttop.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutbot.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            LinearLayouttop.setGravity(Gravity.LEFT);
            LinearLayoutbot.setGravity(Gravity.LEFT);
        }



        //Handle TextView and display string from your list
        Education edu = list.get(position);
        TextView txtEducationTitle = (TextView)view.findViewById(R.id.txtEducationTitle);
        TextView txtEducationCompany = (TextView)view.findViewById(R.id.txtEducationCompany);
        TextView txtEducationFrom = (TextView)view.findViewById(R.id.txtEducationFrom);
        TextView txtEducationTo = (TextView)view.findViewById(R.id.txtEducationTo);

        txtEducationTitle.setText(edu.getField());
        txtEducationCompany.setText(edu.getPlace());
        txtEducationFrom.setText(edu.getFrom());
        txtEducationTo.setText(edu.getTo());

        //Handle buttons and add onClickListeners
        ImageButton btnDelete =view.findViewById(R.id.btnDeleteEducation);
        ImageButton btnEdit = view.findViewById(R.id.btnEditEducation);

        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                list.remove(position); //or some other task
                notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listView);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                Education edu2 = list.get(position);
                showDialog(edu2.getId(), edu2.getField(),edu2.getPlace(),edu2.getFrom(),edu2.getTo(),position);
                notifyDataSetChanged();
            }
        });

         //new
        LinearLayoutitemeducation1=view.findViewById(R.id.LinearLayoutitemeducation1);
        txtEducationTitle=view.findViewById(R.id.txtEducationTitle);
        //updateView(updateLanguage());

        return view;
    }
    private void showDialog(final int id, String educationField, String educationPlace, String educationFrom, String educationTo, final int index){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.list_dialog_education);
        final EditText txtEditField = dialog.findViewById(R.id.txtEditDialogEducationField);
        final EditText txtEditPlace = dialog.findViewById(R.id.txtEditDialogEducationPlace);
        final Spinner txtEditFrom = dialog.findViewById(R.id.spinnerFromEducation);
        final Spinner txtEditTo = dialog.findViewById(R.id.spinnerToEducation);
        final Spinner txtEditgrade = dialog.findViewById(R.id.spinnerGradeEducation);

        final TextView txtmajor= dialog.findViewById(R.id.txtmajor);
        final TextView txteducationplace = dialog.findViewById(R.id.txteducationplace);
        final TextView txteducationgrade = dialog.findViewById(R.id.txteducationgrade);
        final TextView txtFrom = dialog.findViewById(R.id.txtFrom);
        final TextView txtuntil = dialog.findViewById(R.id.txtuntil2);

        context = LocaleHelper.setLocale(context, language);
        resources = context.getResources();

        txtEditgrade.setVisibility(View.GONE);

        txtmajor.setText(resources.getString(R.string.major));
        txteducationplace.setText(resources.getString(R.string.educationplace));
        txteducationgrade.setText(resources.getString(R.string.Educationgrade));
        txtFrom.setText(resources.getString(R.string.From));
        txtuntil.setText(resources.getString(R.string.until));

        if(language.equals("fa"))
        {
            ArrayAdapter<String> From = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    years
            );
            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditFrom.setAdapter(From);

            ArrayAdapter<String> To = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    years
            );
            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditTo.setAdapter(To);

            ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_rtl,
                    resources.getStringArray(R.array.certificate_arrays)
            );
            gradeAdapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
            txtEditgrade.setAdapter(gradeAdapter);
        }

        else
        {
            ArrayAdapter<String> From = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    years
            );
            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditFrom.setAdapter(From);

            ArrayAdapter<String> To = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    years
            );
            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditTo.setAdapter(To);

            ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(
                    context,
                    R.layout.z_simple_spinner_item_ltr,
                    resources.getStringArray(R.array.certificate_arrays)
            );
            gradeAdapter.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
            txtEditgrade.setAdapter(gradeAdapter);
        }

        txtEditField.setText(educationField);
        txtEditPlace.setText(educationPlace);
        for (int i=0;i<txtEditFrom.getCount();i++){
            if (txtEditFrom.getItemAtPosition(i).toString().equalsIgnoreCase(educationFrom)){
                txtEditFrom.setSelection(i,true);
                break;
            }
        }
        for (int i=0;i<txtEditTo.getCount();i++){
            if (txtEditTo.getItemAtPosition(i).toString().equalsIgnoreCase(educationTo)){
                txtEditTo.setSelection(i,true);
                break;
            }
        }
        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogEducation);
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Education edu = new Education("", txtEditField.getText().toString(),txtEditPlace.getText().toString(),txtEditFrom.getSelectedItem().toString(),txtEditTo.getSelectedItem().toString());
                edu.setId(id);
                list.set(index,edu);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout((12 * width)/13, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            txtEducationTitle.setGravity(Gravity.RIGHT|Gravity.CENTER);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitemeducation1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitemeducation1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else{
            txtEducationTitle.setGravity(Gravity.LEFT|Gravity.CENTER);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitemeducation1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitemeducation1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
    }
    private void setLists(){
        if(language.equals("fa")){
            years = new ArrayList<String>();
            PersianCalendar pc = new PersianCalendar();
            thisYear = pc.getYear();
            for (int i = thisYear; i >= 1350; i--) {
                years.add(topersian_number(i+""));
            }
        }
        else{
            years = new ArrayList<String>();
            thisYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = thisYear; i >= 1990; i--) {
                years.add(i+"");
            }
        }

    }
    private String topersian_number(String number){
        String result="";
        for(int i=0;i<number.length();i++){
            result+=en_num_to_p(number.charAt(i));
        }
        return result;
    }
    private char en_num_to_p(char number){

        switch (number){
            case '1':return '۱';
            case '2':return '۲';
            case '3':return '۳';
            case '4':return '۴';
            case '5':return '۵';
            case '6':return '۶';
            case '7':return '۷';
            case '8':return '۸';
            case '9':return '۹';
            default: return '۰';
        }
    }
}
