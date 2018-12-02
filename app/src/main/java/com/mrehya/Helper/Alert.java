package com.mrehya.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mrehya.R;

import org.w3c.dom.Text;

/**
 * Created by hgjhghgjh on 10/18/2018.
 */

public class Alert {

    private View view;
    private Context context;
    private String Language="fa";
    private Activity activity;
    public Alert(View v, Context context, String Language){
        view = v;
        this.context=context;
        this.Language = Language;
    }

    public Alert(View v, Context context){
        view = v;
        this.context=context;
    }

    public Alert(View v){
        view = v;
    }

    public void show(String Header, String message){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View alertview = inflater.inflate( R.layout.alert, null );

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(alertview);
        builder.setTitle(Header);
        builder.setIcon(R.mipmap.ic_launcher);

        TextView txtmessage = (TextView)  alertview.findViewById(R.id.txtMessage);
        txtmessage.setText(message);

        if(Language.equals("fa")){
            builder.setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        else{
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
        builder.show();
    }

}
