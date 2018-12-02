package com.mrehya.Resume.ResumeListAdapters;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Job;
import com.mrehya.R;
import com.mrehya.Reserv.Persian_Date_Methods;

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by Rubick on 2/15/2018.
 */

public class ListAdapterSimpleJobs extends BaseAdapter implements ListAdapter {
    private ArrayList<Job> list = new ArrayList<Job>();
    private Context context;
    private Activity activity;
    private ListView listView;
    private String language="fa";
    ArrayList<String> years, month;
    private Resources resources;
    //new
    private LinearLayout LinearLayoutitemjobs1;
    private TextView txtJobTitle;

    public ListAdapterSimpleJobs(ArrayList<Job> list, Context context, Activity activity, ListView listView) {
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
            view = inflater.inflate(R.layout.list_item_jobs, null);
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
        final Job job = list.get(position);
        TextView txtJobTitle = (TextView)view.findViewById(R.id.txtJobTitle);
        TextView txtJobCompany = (TextView)view.findViewById(R.id.txtJobCompany);
        TextView txtJobFrom = (TextView)view.findViewById(R.id.txtJobFrom);
        TextView txtJobTo = (TextView)view.findViewById(R.id.txtJobTo);
        TextView txtaz = (TextView)view.findViewById(R.id.txtaz);
        TextView txtta = (TextView)view.findViewById(R.id.txtta);

        txtJobTitle.setText(job.getJobtitle());
        txtJobCompany.setText(job.getCompany());
        txtJobFrom.setText(job.getFrom());
        txtJobTo.setText(job.getTo());

        txtJobTitle.setTextSize(17);
        txtJobCompany.setTextSize(15);
        txtJobFrom.setTextSize(15);
        txtJobTo.setTextSize(15);
        txtaz.setTextSize(15);
        txtta.setTextSize(15);

        //Handle buttons and add onClickListeners
        ImageButton btnDelete =view.findViewById(R.id.btnDeleteJob);
        ImageButton btnEdit = view.findViewById(R.id.btnEditJob);
        btnDelete.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        //new
        LinearLayoutitemjobs1=view.findViewById(R.id.LinearLayoutitemjobs1);
        this.txtJobTitle=view.findViewById(R.id.txtJobTitle);
        //updateView(updateLanguage());

        return view;
    }
    private void showDialog(final int id, String title, String jobRole, String jobCompany, String jobFrom, String jobTo, final int index){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.list_dialog_jobs);
        final EditText txtEditRole = dialog.findViewById(R.id.txtEditDialogJobDesc);
        final EditText txtEditCompany = dialog.findViewById(R.id.txtEditDialogJobCompany);
        final Spinner txtEditFrom = dialog.findViewById(R.id.spinnerFromJob);
        final Spinner txtEditTo = dialog.findViewById(R.id.spinnerToJob);
        final CheckBox chkStillWorking = dialog.findViewById(R.id.chkStillWorking);

        final Spinner txtEditFrommonth = dialog.findViewById(R.id.spinnerFromJobmonth);
        final Spinner txtEditTomonth = dialog.findViewById(R.id.spinnerToJobmonth);

        final EditText txtEditJobtitle = dialog.findViewById(R.id.txtEditDialogJobtitle);
        final TextView txtjobtitle= dialog.findViewById(R.id.txtjobtitle);
        final TextView txtpost = dialog.findViewById(R.id.txtdesc);
        final TextView txtcompany = dialog.findViewById(R.id.txtcompany);
        final TextView txtFrom = dialog.findViewById(R.id.txtFrom);
        final TextView txtuntil = dialog.findViewById(R.id.txtuntil);


        txtEditFrommonth.setVisibility(View.GONE);
        txtEditTomonth.setVisibility(View.GONE);
        chkStillWorking.setVisibility(View.GONE);
        txtpost.setVisibility(View.GONE);
        txtEditRole.setVisibility(View.GONE);

        txtEditCompany.setText(jobCompany);
        txtEditJobtitle.setText(title);
        if(language.equals("fa")){
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

//            ArrayAdapter<String> Frommonth = new ArrayAdapter<String>(
//                    context,
//                    R.layout.z_simple_spinner_item_rtl,
//                    month
//            );
//            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
//            txtEditFrommonth.setAdapter(Frommonth);
//
//            ArrayAdapter<String> Tomonth = new ArrayAdapter<String>(
//                    context,
//                    R.layout.z_simple_spinner_item_rtl,
//                    month
//            );
//            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_rtl);
//            txtEditTomonth.setAdapter(Tomonth);
        }
        else{
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




//            ArrayAdapter<String> Frommonth = new ArrayAdapter<String>(
//                    context,
//                    R.layout.z_simple_spinner_item_ltr,
//                    month
//            );
//            From.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
//            txtEditFrommonth.setAdapter(Frommonth);
//
//            ArrayAdapter<String> Tomonth = new ArrayAdapter<String>(
//                    context,
//                    R.layout.z_simple_spinner_item_ltr,
//                    month
//            );
//            To.setDropDownViewResource(R.layout.z_simple_spinner_dropdown_item_ltr);
//            txtEditTomonth.setAdapter(Tomonth);
        }

        for (int i=0;i<txtEditFrom.getCount();i++){
            if (txtEditFrom.getItemAtPosition(i).toString().equalsIgnoreCase(jobFrom)){
                txtEditFrom.setSelection(i,true);
                break;
            }
        }
        for (int i=0;i<txtEditTo.getCount();i++){
            if (txtEditTo.getItemAtPosition(i).toString().equalsIgnoreCase(jobTo)){
                txtEditTo.setSelection(i,true);
                break;
            }
        }
        ImageButton btnEdit = dialog.findViewById(R.id.btnEditDialogJob);
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        context = LocaleHelper.setLocale(context, language);
        resources = context.getResources();
        txtjobtitle.setText(resources.getString(R.string.JobTitle2));
        txtpost.setText(resources.getString(R.string.post));
        txtcompany.setText(resources.getString(R.string.Company));
        txtFrom.setText(resources.getString(R.string.From));
        txtuntil.setText(resources.getString(R.string.until));

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Job job = new Job(txtEditJobtitle.getText().toString(),txtEditFrom.getSelectedItem().toString(),txtEditTo.getSelectedItem().toString(),"",txtEditCompany.getText().toString(),
                        "","","" );

//                Job job = new Job(txtEditJobtitle.getText().toString(),txtEditFrom.getSelectedItem().toString(),txtEditTo.getSelectedItem().toString(),txtEditRole.getText().toString(),txtEditCompany.getText().toString(),
//                       "",txtEditFrommonth.getSelectedItem().toString(),txtEditTomonth.getSelectedItem().toString() );
                job.setId(id);
                list.set(index,job);
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
            txtJobTitle.setGravity(Gravity.RIGHT|Gravity.CENTER);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitemjobs1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitemjobs1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        else{
            txtJobTitle.setGravity(Gravity.LEFT|Gravity.CENTER);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutitemjobs1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutitemjobs1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }
    }
    private void setLists(){
        Persian_Date_Methods pd = new Persian_Date_Methods(language);
        if(language.equals("fa")){
            years = pd.get_persian_years();
            month = pd.get_persian_month();
        }
        else{
            years = pd.get_gregorian_years();
            month = pd.get_gregorian_months();
        }

    }

}
