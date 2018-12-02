package com.mrehya;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rubick on 2/15/2018.
 */

public class ListAdapterTerms extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<>();
    private Context context;
    private Activity activity;
    private ListView listView;


    public ListAdapterTerms(ArrayList<String> list, Context context, Activity activity, ListView listView) {
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
            view = inflater.inflate(R.layout.list_item_terms, null);
        }
        String province = list.get(position);
        //Handle TextView and display string from your list
        final TextView listItemText = (TextView)view.findViewById(R.id.txtTerms);
        listItemText.setText(province);
        //Handle buttons and add onClickListeners
        ImageButton btnDelete =view.findViewById(R.id.btnDeleteTerms);


        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something

                list.remove(position); //or some other task
                notifyDataSetChanged();
                justifyListViewHeightBasedOnChildren(listView);
            }
        });

        return view;
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
}
