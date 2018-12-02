package com.mrehya.Dashboards;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrehya.Hire.HireCompanies;
import com.mrehya.R;

import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class dashAdapter extends RecyclerView.Adapter<dashAdapter.MyViewHolder> {

    private Context mContext;
    private List<HireCompanies> hireCompaniesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public dashAdapter(Context mContext, List<HireCompanies> hireCompaniesList) {
        this.mContext = mContext;
        this.hireCompaniesList = hireCompaniesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dash_recycler_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        HireCompanies HireCompany = hireCompaniesList.get(position);
        holder.title.setText(HireCompany.getCompanyName());

        // loading album cover using Glide library
        Glide.with(mContext).load(HireCompany.getImage()).into(holder.thumbnail);


    }




    @Override
    public int getItemCount() {
        return hireCompaniesList.size();
    }
}