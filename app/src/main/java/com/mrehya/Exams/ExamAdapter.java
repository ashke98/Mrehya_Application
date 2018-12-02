package com.mrehya.Exams;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mrehya.R;
import com.mrehya.SessionManager;

import java.util.List;

import io.paperdb.Paper;

/**
 * Created by hgjhghgjh on 10/22/2018.
 */

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.MyViewHolder>  {

    private String Language="fa";
    private Context mContext;
    private SessionManager session;
    private List<Exam> albumList;
    private int ExamId=0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public ExamAdapter(Context mContext, List<Exam> albumList) {
        this.mContext = mContext;
        session = new SessionManager(mContext);
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Exam exam = albumList.get(position);
        holder.title.setText(exam.getName());

        Boolean free = true;
        Paper.init(mContext);
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        if (Language.equals("fa")) {
            if(exam.getPrice().equals(" 0 تومان") || exam.getType().equals("1") || exam.getType().equals("رایگان")
            || exam.getPrice().equals("رایگان"))
            {
                if(Language.equals("fa"))
                    holder.count.setText("رایگان-Free");
                else
                    holder.count.setText("رایگان-Free");
            }
            else{
                holder.count.setText(exam.getPrice());
                free= false;
            }

        }else {
            if(exam.getPrice().equals(" 0 تومان") || exam.getType().equals("1") || exam.getType().equals("رایگان")
                    || exam.getType().equals("Free") || exam.getPrice().equals("رایگان"))
            {
                holder.count.setText("رایگان-Free");
            }
            else{
                holder.count.setText(exam.getPrice());
            }
        }


        // loading album cover using Glide library
        Glide.with(mContext).load(exam.getImage()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, exam.getId());
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, int examId) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_exam, popup.getMenu());
        ExamId = examId;

        MenuItem menuItem = popup.getMenu().findItem(R.id.action_show_exam);
        if(session.isLoggedIn())
            menuItem.setVisible(true);
        else
            menuItem.setVisible(false);

        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_play_next:
                    send(ExamId);
                    return true;
                case R.id.action_show_exam:
                    show(ExamId);
                    return true;
                default:
            }
            return false;
        }
    }

    public void send(int pos) {
        Intent intent = new Intent(mContext.getApplicationContext(),Test.class);
        intent.putExtra("examId",pos);
        mContext.startActivity(intent);
    }

    public void show(int pos){
        Intent intent = new Intent(mContext.getApplicationContext(),Result.class);
        intent.putExtra("examId",pos);
        mContext.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
