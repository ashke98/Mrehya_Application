package com.mrehya.Shopping;

import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mrehya.R;

import java.util.List;

/**
 * Created by sdfsdfasf on 2/22/2018.
 */




    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

        private Context mContext;
        private List<Product> productList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, count;
            public ImageView thumbnail, overflow;

            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                count = (TextView) view.findViewById(R.id.count);
                thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            }
        }


        public ProductAdapter(Context mContext, List<Product> albumList) {
            this.mContext = mContext;
            this.productList = albumList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_item, parent, false);

            return new MyViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final Product album = productList.get(position);
            holder.title.setText(album.getTitle());
            holder.count.setText(album.getPrice());

            SimpleTarget target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    holder.thumbnail.setImageBitmap(resource);
                }
            };

            // loading album cover using Glide library
            Glide.with(mContext)
                    .load(album.getImage())
                    .asBitmap()
                    .placeholder(R.drawable.logo_eng)
                    .error(R.drawable.logo_eng)
                    .into(target);


//            Glide.with(mContext).load(album.getId()).into(holder.thumbnail);

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent =new Intent(mContext,ShowProduct.class);
                    intent.putExtra("Product", album);
                    mContext.startActivity(intent);
                }
            });

        }


        @Override
        public int getItemCount() {
            return productList.size();
        }
    }

