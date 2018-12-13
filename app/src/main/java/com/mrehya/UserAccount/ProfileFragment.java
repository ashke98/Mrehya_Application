package com.mrehya.UserAccount;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mrehya.ApiHelpers.ApiHelpers;
import com.mrehya.AppConfig;
import com.mrehya.AppController;
import com.mrehya.DashboardPackage.HttpsTrustManager;
import com.mrehya.Exams.RetriveExamsHistory;
import com.mrehya.Helper.LocaleHelper;
import com.mrehya.Hire.ShowHireStatus;
import com.mrehya.Language;
import com.mrehya.MainActivity;
import com.mrehya.MyTextView;
import com.mrehya.R;
import com.mrehya.SessionManager;
import com.mrehya.Shopping.ShowPurchase;
import com.mrehya.UserAccount.LoginOrSignup;
import com.mrehya.UserAccount.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

import android.Manifest;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private Context context;
    private String Language, newtoken="empty";
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    //ELEMENTS
    private Button btnSignInOrUp, showPurchases, hireRequestStatus, btnEditProfile,
             BtnInstagram ,btnTelegram, ReserveRequestStatus, btn_save_editprof,ExamsHistory;
    private AlertDialog ad ;
    private MyTextView profileType, mytextUserInfo, mytextUserInfo2, profname;
    private LinearLayout LinearLayoutprofile1,LinearLayoutprofile2,LinearLayoutprofile3,LinearLayoutprofile4
            ,LinearLayoutprofile5,LinearLayoutprofile6, LinearLayoutprofileReserve;
    private TextView txtEditName,txtEditLastName,txtEditEmail,txtEditMobile,txtEditPhone,txtEditZip,txtEditAddress;
    private TextView txtEditProfile,txtName,txtLastname,txtEmail,txtPhoneNumber,txtTelephone,txtPostcode,txtAddress;
    private ImageView imageViewProfile;



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return findViews(inflater, container);
    }
    private View findViews(final LayoutInflater inflater, ViewGroup container){
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignInOrUp = view.findViewById(R.id.btnSignInOrUp);
        showPurchases = view.findViewById(R.id.showPurchases);
        hireRequestStatus = view.findViewById(R.id.hireRequestStatus);
        ReserveRequestStatus = view.findViewById(R.id.ReserveRequestStatus);
        ExamsHistory = view.findViewById(R.id.ExamsHistory);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        context =view.getContext();
        sessionManager = new SessionManager(context);
        //new
        profileType = view.findViewById(R.id.profileType);
        mytextUserInfo = view.findViewById(R.id.mytextUserInfo);
        mytextUserInfo2 = view.findViewById(R.id.mytextUserInfo2);
        //layouts
        LinearLayoutprofile1 = (LinearLayout) view.findViewById(R.id.LinearLayoutprofile1);
        LinearLayoutprofile2 = (LinearLayout) view.findViewById(R.id.LinearLayoutprofile2);
        LinearLayoutprofile3 = (LinearLayout) view.findViewById(R.id.LinearLayoutprofile3);
        LinearLayoutprofile4 = (LinearLayout) view.findViewById(R.id.LinearLayoutprofile4);
        LinearLayoutprofile5 = (LinearLayout) view.findViewById(R.id.LinearLayoutprofile5);
        LinearLayoutprofile6 = (LinearLayout) view.findViewById(R.id.LinearLayoutprofile6);
        LinearLayoutprofileReserve = (LinearLayout) view.findViewById(R.id.LinearLayoutprofileReserve);
        imageViewProfile = (ImageView) view.findViewById(R.id.profileImage);
        btnTelegram = view.findViewById(R.id.btnTelegram);
        BtnInstagram = view.findViewById(R.id.BtnInstagram);
        profname = view.findViewById(R.id.profileName);

        setViews();
        loadImageFromStorage("/data/user/0/com.mrehya/app_EHYA");
        setOnclicks();
        return view;
    }
    private void setViews(){
        //Remaining apis
        LinearLayoutprofile4.setVisibility(View.GONE);
        LinearLayoutprofileReserve.setVisibility(View.GONE);
        LinearLayoutprofile3.setVisibility(View.GONE);

        profname.setText(sessionManager.getUserDetails().getFirstname() + " " + sessionManager.getUserDetails().getLastname());
        Language = updateLanguage();
        updateView((String) Paper.book().read("language"));
    }
    private void setOnclicks(){

        //choose image dialog
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_profile_image);

                    final TextView title = (TextView) dialog.findViewById(R.id.txtImagedialog);
                    final Button btn_gallery = (Button) dialog.findViewById(R.id.btn_gallery);
                    final Button btn_take = (Button) dialog.findViewById(R.id.btn_take);
                    final Button btn_remove = (Button) dialog.findViewById(R.id.btn_remove);
                    final Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                    if (Language.equals("fa")) {
                        title.setText("تصویر پروفایل");
                        btn_gallery.setText("انتخاب از گالری");
                        btn_take.setText("گرفتن عکس");
                        btn_remove.setText("حذف");
                        btn_cancel.setText("انصراف");
                    } else {
                        title.setText("Profile picture");
                        btn_gallery.setText("Choose from gallery");
                        btn_take.setText("Take new one");
                        btn_remove.setText("Remove");
                        btn_cancel.setText("Cancel");
                    }

                    btn_gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, 1);
                            dialog.dismiss();
                        }
                    });
                    ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                    File directory = cw.getDir("EHYA", Context.MODE_PRIVATE);
                    loadImageFromStorage(directory.getPath());

                    Log.e("path: ",directory.getPath());


                    btn_take.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (checkSelfPermission(context, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA},
                                        MY_CAMERA_PERMISSION_CODE);
                            } else {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                dialog.dismiss();
                            }
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btn_remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            imageViewProfile.setImageDrawable(getResources().getDrawable(R.drawable.profile_thumb));
                            File myFile = new File("/data/user/0/com.mrehya/app_EHYA/profile.jpg");
                            if(myFile.exists())
                                myFile.delete();
                        }
                    });

                    DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    dialog.getWindow().setLayout((12 * width) / 13, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });

        btnTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://t.me/memoryinstitute";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        btnSignInOrUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()){
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("آیا واقعا میخواهید خارج شوید؟");

                    alertDialogBuilder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sessionManager.setLogin(false);
                            Intent intent = new Intent(getActivity() , com.mrehya.Language.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ad.dismiss();
                        }
                    });
                    ad = alertDialogBuilder.create();
                    ad.show();
                }else{
                    Intent intent = new Intent(getActivity(),LoginOrSignup.class);
                    startActivity(intent);
                }

            }
        });

        ExamsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), RetriveExamsHistory.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginOrSignup.class);
                    startActivity(intent);
                }
            }
        });
        hireRequestStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), ShowHireStatus.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginOrSignup.class);
                    startActivity(intent);
                }
            }
        });
        ReserveRequestStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), ShowHireStatus.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginOrSignup.class);
                    startActivity(intent);
                }
            }
        });
        showPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), ShowPurchase.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginOrSignup.class);
                    startActivity(intent);
                }
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isLoggedIn()) {
                    if (Language.equals("fa")) {
                        Toast.makeText(getContext(), "در حال ارتباط با سرور...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Connecting to server...", Toast.LENGTH_SHORT).show();
                    }

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_edit_profile);
                    get_credentials(sessionManager.getUserDetails().getToken(),sessionManager.getUserDetails().getEmail()
                            ,sessionManager.getUserDetails().getPassword(), null);
                    //txts
                    txtEditProfile = dialog.findViewById(R.id.txtEditProfile);
                    txtName = dialog.findViewById(R.id.txtName);
                    txtLastname = dialog.findViewById(R.id.txtLastname);
                    txtEmail = dialog.findViewById(R.id.txtEmail);
                    txtPhoneNumber = dialog.findViewById(R.id.txtPhoneNumber);
                    txtTelephone = dialog.findViewById(R.id.txtTelephone);

                    txtPostcode = dialog.findViewById(R.id.txtPostcode);
                    txtEditZip = dialog.findViewById(R.id.txtEditZip);
                    txtPostcode.setVisibility(View.GONE);
                    txtEditZip.setVisibility(View.GONE);

                    txtAddress = dialog.findViewById(R.id.txtAddress);
                    txtEditAddress = dialog.findViewById(R.id.txtEditAddress);
                    txtAddress.setVisibility(View.GONE);
                    txtEditAddress.setVisibility(View.GONE);

                    btn_save_editprof = dialog.findViewById(R.id.btn_save_editprof);

                    txtEditName = dialog.findViewById(R.id.txtEditName);
                    txtEditLastName = dialog.findViewById(R.id.txtEditLastName);
                    txtEditEmail = dialog.findViewById(R.id.txtEditEmail);
                    txtEditMobile = dialog.findViewById(R.id.txtEditMobile);
                    txtEditPhone = dialog.findViewById(R.id.txtEditPhone);
                    txtEditZip = dialog.findViewById(R.id.txtEditZip);
                    txtEditAddress = dialog.findViewById(R.id.txtEditAddress);



                    //user credentials
                    User user = sessionManager.getUserDetails();
                    if(user.getFirstname() == null) txtEditName.setText(""); else txtEditName.setText(user.getFirstname());

                    if(sessionManager.getUserDetails().getLastname().equals("null")) txtEditLastName.setText("");
                    else txtEditLastName.setText(sessionManager.getUserDetails().getLastname());

                    if(sessionManager.getUserDetails().getEmail().equals("null")) txtEditEmail.setText("");
                    else txtEditEmail.setText(sessionManager.getUserDetails().getEmail());

                    if(sessionManager.getUserDetails().getMobile().equals("null")) txtEditMobile.setText("");
                    else txtEditMobile.setText(sessionManager.getUserDetails().getMobile());

                    if(sessionManager.getUserDetails().getPhone().equals("null")) txtEditPhone.setText("");
                    else txtEditPhone.setText(sessionManager.getUserDetails().getPhone());

                    if(sessionManager.getUserDetails().getZip().equals("null")) txtEditZip.setText("");
                    else txtEditZip.setText(sessionManager.getUserDetails().getZip());

                    if(sessionManager.getUserDetails().getAddress().equals("null")) txtEditAddress.setText("");
                    else txtEditAddress.setText(sessionManager.getUserDetails().getAddress());

                    updateView_dialog(Language);

                    DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    //int height = metrics.heightPixels;
                    btn_save_editprof.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Language.equals("fa")) {
                                Toast.makeText(getContext(), "در حال ارسال اطلاعات...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Sending information...", Toast.LENGTH_SHORT).show();
                            }
                            update_token(sessionManager.getUserDetails().getEmail(), sessionManager.getUserDetails().getPassword(), dialog);
                        }
                    });
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    dialog.getWindow().setLayout((12 * width)/13, ViewGroup.LayoutParams.WRAP_CONTENT);


                }else{
                    if (Language.equals("fa")) {
                        Toast.makeText(getContext(), "شما باید لاگین شوید", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //َAPIs
    private void update_token(final String email , final String password, final Dialog dialog){
        ApiHelpers ah = new ApiHelpers(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .addFormDataPart("type", "1")
                .build();
        ah.PosttCall(AppConfig.URL_SIGNIN, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(Language.equals("fa")){
                                    Toast.makeText(getContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                }
                                Log.e("update_token", e.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Boolean message = false;
                                if (response.isSuccessful()) {
                                    try {
                                        if (response.code() == 200) {
                                            String dataString = response.body().string();
                                            Log.e("update_token", dataString);
                                            JSONObject data = new JSONObject(dataString).getJSONObject("data");
                                                newtoken = data.getString("token");
                                                if(dialog != null)
                                                    get_credentials(newtoken, email, password, dialog);
                                            }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    if(Language.equals("fa")){
                                        Toast.makeText(getContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                    }

                                    else{
                                        Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });

    }
    private void get_credentials(final String token, final String email, final String passwordd, final Dialog dialog){
        ApiHelpers ah = new ApiHelpers(context);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", token)
                .build();
        ah.PosttCall(AppConfig.URL_VIEW_Profile, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Log.e("get_credntials", e.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {

                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {

                        if (response.isSuccessful()) {
                            try {
                                if (response.code()==200) {
                                    String dataString = response.body().string();
                                    //Log.e("get_credntials", dataString);
                                    JSONObject data = new JSONObject(dataString).getJSONObject("data");

                                    String firstname=" ",lastname=" " , email=" ", phone= " ", token=" ",
                                            image =" ",mobile=" ",address=" ", zip=" ",password=passwordd,resume=" ";

                                    int id =        data.getInt("id");
                                    firstname = data.getString("first_name");
                                    lastname =  data.getString("last_name");
                                    email =     data.getString("email");
                                    token =     data.getString("auth_key");
                                    phone =     data.getString("tel_number");
                                    image =     data.getString("avatar");
                                    mobile =    data.getString("mobile");
                                    address =   data.getString("address");
                                    zip =       data.getString("postal_code");
                                    if(data.has("resumeId"))
                                        resume = data.getInt("resumeId")+"";
                                    else
                                        resume =null;
                                    sessionManager.setLogin(true);
                                    sessionManager.setUserDetails(id,firstname,lastname,email,phone,token, image,mobile,address,zip,password,resume);
                                    if(dialog != null)
                                    {
                                        if (Language.equals("fa")) {
                                            Toast.makeText(getContext(), "منتظر بمانید...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Please Wait...", Toast.LENGTH_SHORT).show();
                                        }
                                        update_user(token, dialog);
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                            }
                        });
                    }
                });
    }
    private void update_user(final String token, final Dialog dialog){
        ApiHelpers ah = new ApiHelpers(context);
        if(txtEditAddress.getText().toString().equals(null))
            txtEditAddress.setText("empty");
        if(txtEditZip.getText().toString().equals(null))
            txtEditZip.setText(0);
        if(txtEditPhone.getText().toString().equals(null))
            txtEditPhone.setText(0);
        if(txtEditMobile.getText().toString().equals(null))
            txtEditMobile.setText(0);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("auth", token)
                .addFormDataPart("first_name", txtEditName.getText().toString())
                .addFormDataPart("last_name", txtEditEmail.getText().toString())
                .addFormDataPart("address", txtEditAddress.getText().toString())
                .addFormDataPart("postal_code", txtEditZip.getText().toString())
                .addFormDataPart("tel_number", txtEditPhone.getText().toString())
                .addFormDataPart("mobile", txtEditMobile.getText().toString())
                .build();

        ah.PosttCall(AppConfig.URL_EDIT_Profile, requestBody, false).enqueue(
                new Callback() {
                    Handler mainHandler = new Handler(context.getMainLooper());

                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (Language.equals("fa")) {
                                    Toast.makeText(context, "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                }
                                //Log.e("update_user", e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final okhttp3.Response response) throws IOException {
                        mainHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    try {
                                        if (response.code() == 200) {
                                            String dataString = response.body().string();
                                            //Log.e("URL_EDIT_Profile", dataString);
                                            JSONObject data = new JSONObject(dataString).getJSONObject("data");
                                            sessionManager.setUserDetails(
                                                    data.getInt("id"),
                                                    data.getString("first_name"),
                                                    data.getString("last_name"),
                                                    data.getString("email"),
                                                    data.getString("tel_number"), newtoken, sessionManager.getUserDetails().getImage(),
                                                    data.getString("mobile"),
                                                    data.getString("address"),
                                                    data.getString("postal_code"), sessionManager.getUserDetails().getPassword(), "0");


                                            if (Language.equals("fa")) {
                                                Toast.makeText(getContext(), "تغییرات اعمال شد", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Your request is successfully done", Toast.LENGTH_SHORT).show();
                                            }
                                            if (dialog.isShowing())
                                                dialog.dismiss();
                                        } else {
                                            if (Language.equals("fa")) {
                                                Toast.makeText(getContext(), "مشکلی در اتصال با سرور پیش آمده است", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Network Connection or Server failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
    }

    //methods
    //IMAGE Gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                saveToInternalStorage(bitmapImage);
                imageViewProfile.setImageBitmap(bitmapImage);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null){


                Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
                saveToInternalStorage(bitmapImage);
                imageViewProfile.setImageBitmap(bitmapImage);

        }
    }
    private void loadImageFromStorage(String path) {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageViewProfile.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("EHYA", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    private void getContentResolver() {

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Image TAKE
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }else if (requestCode == 2){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_profile_image);

                final TextView title = (TextView) dialog.findViewById(R.id.txtImagedialog);
                final Button btn_gallery = (Button) dialog.findViewById(R.id.btn_gallery);
                final Button btn_take = (Button) dialog.findViewById(R.id.btn_take);
                final Button btn_remove = (Button) dialog.findViewById(R.id.btn_remove);
                final Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                if (Language.equals("fa")) {
                    title.setText("تصویر پروفایل");
                    btn_gallery.setText("انتخاب از گالری");
                    btn_take.setText("گرفتن عکس");
                    btn_remove.setText("حذف");
                    btn_cancel.setText("انصراف");
                } else {
                    title.setText("Profile picture");
                    btn_gallery.setText("Choose from gallery");
                    btn_take.setText("Take new one");
                    btn_remove.setText("Remove");
                    btn_cancel.setText("Cancel");
                }

                btn_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, 1);
                    }
                });
                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                File directory = cw.getDir("EHYA", Context.MODE_PRIVATE);
                loadImageFromStorage(directory.getPath());


                btn_take.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (checkSelfPermission(context, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    MY_CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                dialog.getWindow().setLayout((12 * width) / 13, ViewGroup.LayoutParams.WRAP_CONTENT);

            }else{
                Toast.makeText(getContext(),"اجازه دسترسی به فظای دیسک داده نشده است!",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode ==3){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_profile_image);

                final TextView title = (TextView) dialog.findViewById(R.id.txtImagedialog);
                final Button btn_gallery = (Button) dialog.findViewById(R.id.btn_gallery);
                final Button btn_take = (Button) dialog.findViewById(R.id.btn_take);
                final Button btn_remove = (Button) dialog.findViewById(R.id.btn_remove);
                final Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                if (Language.equals("fa")) {
                    title.setText("تصویر پروفایل");
                    btn_gallery.setText("انتخاب از گالری");
                    btn_take.setText("گرفتن عکس");
                    btn_remove.setText("حذف");
                    btn_cancel.setText("انصراف");
                } else {
                    title.setText("Profile picture");
                    btn_gallery.setText("Choose from gallery");
                    btn_take.setText("Take new one");
                    btn_remove.setText("Remove");
                    btn_cancel.setText("Cancel");
                }

                btn_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, 1);
                    }
                });
                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                File directory = cw.getDir("EHYA", Context.MODE_PRIVATE);
                loadImageFromStorage(directory.getPath());


                btn_take.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (checkSelfPermission(context, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    MY_CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                dialog.getWindow().setLayout((12 * width) / 13, ViewGroup.LayoutParams.WRAP_CONTENT);
            }else{
                Toast.makeText(getContext(),"اجازه دسترسی به فظای دیسک داده نشده است!",Toast.LENGTH_SHORT).show();            }
        }
    }

    private String updateLanguage(){
        String language = Paper.book().read("language");
        if(language==null)
            Paper.book().write("language", "fa");
        return language;
    }
    private void updateView(String language) {
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();

        //Linear Layouts
        if(language.equals("fa")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutprofile1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile3.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile4.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile5.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile6.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofileReserve.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                mytextUserInfo.setGravity(Gravity.RIGHT);
                mytextUserInfo2.setGravity(Gravity.RIGHT);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutprofile1.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile3.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile4.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile5.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofile6.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                LinearLayoutprofileReserve.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                mytextUserInfo.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                mytextUserInfo2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                mytextUserInfo.setGravity(Gravity.RIGHT);
                mytextUserInfo2.setGravity(Gravity.RIGHT);

            }

            btnSignInOrUp.setGravity(Gravity.RIGHT|Gravity.CENTER);
            showPurchases.setGravity(Gravity.RIGHT|Gravity.CENTER);
            hireRequestStatus.setGravity(Gravity.RIGHT|Gravity.CENTER);
            ReserveRequestStatus.setGravity(Gravity.RIGHT|Gravity.CENTER);
            btnEditProfile.setGravity(Gravity.RIGHT|Gravity.CENTER);
            //BtnInstagram.setGravity(Gravity.RIGHT|Gravity.CENTER);
            //btnTelegram.setGravity(Gravity.RIGHT|Gravity.CENTER);
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                LinearLayoutprofile1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile3.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile4.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile5.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile6.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofileReserve.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                mytextUserInfo.setGravity(Gravity.LEFT);
                mytextUserInfo2.setGravity(Gravity.LEFT);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LinearLayoutprofile1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile3.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile4.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile5.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofile6.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                LinearLayoutprofileReserve.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                mytextUserInfo.setGravity(Gravity.LEFT);
                mytextUserInfo2.setGravity(Gravity.LEFT);
            }

            btnSignInOrUp.setGravity(Gravity.LEFT|Gravity.CENTER);
            showPurchases.setGravity(Gravity.LEFT|Gravity.CENTER);
            hireRequestStatus.setGravity(Gravity.LEFT|Gravity.CENTER);
            ReserveRequestStatus.setGravity(Gravity.LEFT|Gravity.CENTER);
            btnEditProfile.setGravity(Gravity.LEFT|Gravity.CENTER);
            //BtnInstagram.setGravity(Gravity.LEFT|Gravity.CENTER);
            //btnTelegram.setGravity(Gravity.LEFT|Gravity.CENTER);
        }
        btnSignInOrUp.setText(resources.getString(R.string.SignInOrUp));
        if (sessionManager.isLoggedIn()){
            btnSignInOrUp.setText(resources.getString(R.string.LogOut));
        }
        showPurchases.setText(resources.getString(R.string.showPurchases));
        hireRequestStatus.setText(resources.getString(R.string.hireRequestStatus));
        ReserveRequestStatus.setText(resources.getString(R.string.ReserveRequestStatus));
        btnEditProfile.setText(resources.getString(R.string.EditProfile));
        BtnInstagram.setText(resources.getString(R.string.Instagram));
        btnTelegram.setText(resources.getString(R.string.Telegram));
        profileType.setText(resources.getString(R.string.profileType));
        mytextUserInfo.setText(resources.getString(R.string.UserInfo));
        mytextUserInfo2.setText(resources.getString(R.string.UserInfo2));


    }
    public void updateView_dialog(String language){
        Context context = LocaleHelper.setLocale(getActivity(), language);
        Resources resources = context.getResources();
        txtEditProfile.setText(resources.getString(R.string.EditProfile));
        txtName.setText(resources.getString(R.string.Name));
        txtLastname.setText(resources.getString(R.string.Lastname));
        txtEmail.setText(resources.getString(R.string.Email));
        txtPhoneNumber.setText(resources.getString(R.string.PhoneNumber));
        txtTelephone.setText(resources.getString(R.string.Telephone));
        txtPostcode.setText(resources.getString(R.string.Postcode));
        txtAddress.setText(resources.getString(R.string.Address));
    }


    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted1");
                return true;
            } else {

                Log.v("TAG","Permission is revoked1");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted1");
            return true;
        }
    }
    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted2");
                return true;
            } else {

                Log.v("TAG","Permission is revoked2");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted2");
            return true;
        }
    }
}
