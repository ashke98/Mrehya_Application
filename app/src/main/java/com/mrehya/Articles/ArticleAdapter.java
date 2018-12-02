package com.mrehya.Articles;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.R;

import java.util.List;

import io.paperdb.Paper;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder> {

    private Context mContext;
    private List<Article> articles;

    //new
    TextView text1;
    TextView title, name;
    ImageView thumbnail;
    LinearLayout LinearLayoutarticlerecycleitem;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, name;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            name = (TextView) view.findViewById(R.id.city);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public ArticleAdapter(Context mContext, List<Article> articles) {
        this.mContext = mContext;
        this.articles = articles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_recycler_item, parent, false);

        //new
        Paper.init(mContext);
        text1 = itemView.findViewById(R.id.text1);
        title = (TextView) itemView.findViewById(R.id.title);
        name = (TextView) itemView.findViewById(R.id.city);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        LinearLayoutarticlerecycleitem = itemView.findViewById(R.id.LinearLayoutarticlerecycleitem);

        updateView(updateLanguage());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.getName());

        // loading album cover using Glide library
        Glide.with(mContext).load(article.getImage()).into(holder.thumbnail);


    }




    @Override
    public int getItemCount() {
        return articles.size();
    }


    private String updateLanguage(){
        //Default language is fa
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }

    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(this.mContext, language);
        Resources resources = context.getResources();

        RelativeLayout.LayoutParams text1params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams thumbnailparams = new RelativeLayout.LayoutParams(80, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams titleparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams LinearLayoutarticlerecycleitemparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);



        //LANGUAGE FA
        if(language.equals("fa")){

            text1.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
            text1params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            text1params.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
            text1.setLayoutParams(text1params);

            thumbnailparams.addRule(RelativeLayout.RIGHT_OF, R.id.text1);
            thumbnailparams.width = 160;
            thumbnailparams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            thumbnail.setLayoutParams(thumbnailparams);

            titleparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            titleparams.setMargins(0,0,15,0);
            title.setLayoutParams(titleparams);

            LinearLayoutarticlerecycleitemparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            LinearLayoutarticlerecycleitemparams.addRule(RelativeLayout.BELOW, R.id.title);
            LinearLayoutarticlerecycleitemparams.setMargins(0,10,15,0);
            LinearLayoutarticlerecycleitem.setLayoutParams(LinearLayoutarticlerecycleitemparams);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutarticlerecycleitem.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            //makeResume.setGravity(Gravity.RIGHT|Gravity.CENTER);
        }
        //LANGUAGE ENGLISH
        else {
            text1.setGravity(Gravity.BOTTOM|Gravity.LEFT);
            text1params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            text1params.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
            text1.setLayoutParams(text1params);

            thumbnailparams.addRule(RelativeLayout.LEFT_OF, R.id.text1);
            thumbnailparams.width = 160;
            thumbnailparams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            thumbnail.setLayoutParams(thumbnailparams);

            titleparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            titleparams.setMargins(15,0,0,0);
            title.setLayoutParams(titleparams);

            LinearLayoutarticlerecycleitemparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            LinearLayoutarticlerecycleitemparams.addRule(RelativeLayout.BELOW, R.id.title);
            LinearLayoutarticlerecycleitemparams.setMargins(15,10,0,0);
            LinearLayoutarticlerecycleitem.setLayoutParams(LinearLayoutarticlerecycleitemparams);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutarticlerecycleitem.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        }

        text1.setText(resources.getString(R.string.article));

    }
}