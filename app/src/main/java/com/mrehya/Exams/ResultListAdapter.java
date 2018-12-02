package com.mrehya.Exams;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrehya.R;

import java.util.List;

/**
 * Created by hgjhghgjh on 11/20/2018.
 */

public class ResultListAdapter  extends RecyclerView.Adapter<ResultListAdapter.MyViewHolder> {
    private Context context;
    private List<ResultModel> cartList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, point, resultshow, message, isanswer;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            point = view.findViewById(R.id.point);
            resultshow = view.findViewById(R.id.resultshow);
            message = view.findViewById(R.id.message);
            thumbnail = view.findViewById(R.id.thumbnail);
            isanswer = view.findViewById(R.id.isanswer);
        }
    }


    public ResultListAdapter(Context context, List<ResultModel> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resultmodel_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ResultModel recipe = cartList.get(position);
        holder.title.setText(recipe.getTitle());
        holder.point.setText("محدوده امتیاز: " + recipe.getPoint());
        holder.resultshow.setText(recipe.getResultshow());
        holder.message.setText(recipe.getMessage());

        String imagename = "tick2";
        if(recipe.getIsAnswer().equals("no"))
        {
            imagename = "cross2";
            holder.isanswer.setText("");
        }
        else{
            holder.isanswer.setText(recipe.getIsAnswer());
        }
        Glide.with(context)
                .load(getImage(imagename))
                .into(holder.thumbnail);
    }
    // recipe
    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public int getImage(String ImageName){
        int drawableId = context.getResources().getIdentifier(ImageName, "drawable", context.getPackageName());
        return drawableId;
    }
}
