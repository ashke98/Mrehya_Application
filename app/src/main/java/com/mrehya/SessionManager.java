package com.mrehya;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.mrehya.Resume.Resume;
import com.mrehya.UserAccount.User;

import org.json.JSONException;

import co.ronash.pushe.Pushe;

//import co.ronash.pushe.Pushe;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref, prefResume;

    Editor editor, editorResume;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "mrehya";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_IS_LOADED = "isLoaded";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        prefResume = _context.getSharedPreferences(PREF_NAME+"Resume", PRIVATE_MODE);
        editor = pref.edit();
        editorResume = prefResume.edit();
    }
    public void setNotifStatus(boolean isNotifOn, Context context) {

        editor.putBoolean("isNotifOn", isNotifOn);
        if(isNotifOn){
            try{
                Pushe.setNotificationOff(context);
            }catch (Exception e){

            }
        }
        else{
            try{
                Pushe.setNotificationOff(context);
            }catch (Exception e){

            }
        }
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }
    public boolean isNotifOn(){
        return pref.getBoolean("isNotifOn", true);
    }
    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();

        if(!isLoggedIn){
            editor.putString("resume",null);
            editor.putString("exams","");
            editor.putString("token","");
            editor.commit();
            editorResume.putString("id",null);
            editorResume.putBoolean("loaded", false);
            editorResume.commit();
        }
        Log.d(TAG, "User login session modified!");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public boolean isLoaded(){
        return prefResume.getBoolean(KEY_IS_LOADED, false);
    }
    public void setLoaded(boolean Loaded) {
        editorResume.putBoolean("loaded", Loaded);
        editorResume.commit();
    }


    public void setUserDetails(int id ,  String firstname,String lastname ,String email, String phone, String token,String image, String mobile, String address, String zip
            ,String password , String resume){

        editor.putInt("user_id" , id);

        editor.putString("firstname",firstname);
        editor.commit();
        editor.putString("lastname",lastname);
        editor.commit();
        editor.putString("email",email);
        editor.commit();
        editor.putString("image",image);
        editor.commit();
        editor.putString("token",token);
        editor.commit();
        editor.putString("phone",phone);
        editor.commit();
        editor.putString("mobile",mobile);
        editor.commit();
        editor.putString("address",address);
        editor.commit();
        editor.putString("zip",zip);
        editor.commit();
        editor.putString("password",password);
        editor.commit();

        editor.putString("resume",resume);
        editor.commit();


    }
    public User getUserDetails(){

        int id = pref.getInt("user_id", 0);
        int password_change = pref.getInt("password_change", 0);
        String firstname = pref.getString("firstname" , null);
        String lastname = pref.getString("lastname" , null);
        String email = pref.getString("email" , null);
        String image = pref.getString("image" , null);
        String token = pref.getString("token" , null);
        String address = pref.getString("address" , null);
        String phone = pref.getString("phone" , null);
        String mobile = pref.getString("mobile" , null);
        String zip = pref.getString("zip" , null);
        String password = pref.getString("password" , null);
        String resume = pref.getString("resume" , null);
        String exams = pref.getString("exams" , "");

        User user = new User(id,firstname,lastname,email,resume);
        user.setEmail(email);
        user.setExams(exams);
        user.setAddress(address);user.setPhone(phone);user.setMobile(mobile);user.setImage(image);user.setZip(zip);
        user.setToken(token);user.setPassword(password);user.setResume(resume);
        return user;
    }
    public void setUserEmail(String email){
        editor.putString("email",email);
        editor.commit();
    }
    public void setChangePassword(int changePassword){
        editor.putInt("password_change" , changePassword);
        editor.commit();
    }

    public void setResumeId(String resume) {
        editor.putString("resume",resume);
        editor.commit();
        editorResume.putString("id",resume);
        editorResume.commit();
    }

    public void setExams(String exams){
        editor.putString("exams",exams);
        editor.commit();
    }


    public  String set_Resume(String id, String email, String job_title, //String address,
                              String job_status, String certificate,
                           String province, String marital, //String militarystatus,
                           String about_me, String image, String fullname,
                           String link, String sex, String levels, String salary,
                           String categories, String benefits, String provinces,
                           String contracts, String skills, String experiences, String academics,
                           String languages, String mobile, String year_birth, String gender){
        editorResume.putString("id",id).apply();
        editorResume.putString("email", email).apply();
        editorResume.putString("job_title", job_title).apply();
        //editorResume.putString("slug", slug).apply();
        editorResume.putString("address" ,null).apply();
        editorResume.putString("job_status", job_status).apply();
        editorResume.putString("certificate", certificate).apply();
        editorResume.putString("province", province).apply();
        editorResume.putString("marital",  marital).apply();
        editorResume.putString("militarystatus", null).apply();
        editorResume.putString("about_me", about_me).apply();
        editorResume.putString("image", image).apply();
        editorResume.putString("fullname", fullname).apply();
        editorResume.putString("link", link).apply();
        editorResume.putString("sex", sex).apply();
        editorResume.putString("levels", levels).apply();
        editorResume.putString("salary", salary).apply();
        editorResume.putString("categories", categories).apply();
        editorResume.putString("benefits", benefits).apply();
        editorResume.putString("provinces", provinces).apply();
        editorResume.putString("contracts",  contracts).apply();
        editorResume.putString("skills", skills).apply();
        editorResume.putString("experiences",  experiences).apply();
        editorResume.putString("academics" , academics).apply();
        editorResume.putString("languages", languages).apply();
        editorResume.putString("mobile",  mobile).apply();
        editorResume.putString("year_birth", year_birth).apply();
        editorResume.putString("sex", gender).apply();
        return  "ok";
    }

    public String set_Resume_UserInfo(String job_title,
                                      String year_birth,
                                      String job_status,
                                      String province,
                                      String marital,
                                      String gender,
                                      String image){
        editorResume.putString("job_title", job_title).apply();
        editorResume.putString("year_birth", year_birth).apply();
        editorResume.putString("job_status", job_status).apply();
        editorResume.putString("province", province).apply();
        editorResume.putString("marital",  marital).apply();
        editorResume.putString("sex", gender).apply();
        editorResume.putString("image", image).apply();
        return  "ok";
    }

    public  String set_Resume_Acts(String skills,String provinces,
                                   String contracts, String categories, String levels){
        editorResume.putString("levels", levels).apply();
        editorResume.putString("categories", categories).apply();
        editorResume.putString("provinces", provinces).apply();
        editorResume.putString("contracts",  contracts).apply();
        editorResume.putString("skills", skills).apply();
        return  "ok";
    }

    public  String set_Resume_Benefits(String benefits){
        editorResume.putString("benefits", benefits).apply();
        return  "ok";
    }

    public  String set_Resume_Aboutme(String aboutme){
        editorResume.putString("about_me", aboutme).apply();
        return  "ok";
    }

    public Resume get_Resume() throws JSONException {
        String  id = prefResume.getString("id", null);
        String  email  = prefResume.getString("email", null);
        String  job_title  = prefResume.getString("job_title", null);
        String  slug = prefResume.getString("slug", null);
        String  address  = prefResume.getString("address" ,null);
        String  job_status  = prefResume.getString("job_status", null);
        String  certificate  = prefResume.getString("certificate", null);
        String  province  = prefResume.getString("province", null);
        String  marital = prefResume.getString("marital",  null);
        String  militarystatus  = prefResume.getString("militarystatus", null);
        String  about_me  = prefResume.getString("about_me", null);
        String  image  = prefResume.getString("image",null );
        String  fullname  = prefResume.getString("fullname", null);
        String  link  = prefResume.getString("link", null);
        String  sex  = prefResume.getString("sex", null);
        String  levels  = prefResume.getString("levels",null );
        String  salary  = prefResume.getString("salary", null);
        String  categories  = prefResume.getString("categories",null );
        String  benefits  = prefResume.getString("benefits",null );
        String  provinces  = prefResume.getString("provinces", null);
        String  contracts  = prefResume.getString("contracts", null );
        String year_birth = prefResume.getString("year_birth", null );
        String skills =  prefResume.getString("skills", null);

        String  experiences  = prefResume.getString("experiences", null );
        String  academics  = prefResume.getString("academics" , null);
        String  languages  = prefResume.getString("languages",null );
        String  mobile = prefResume.getString("mobile",  null);



        Resume resume = new Resume( id,  email,  job_title,  slug,  address,  job_status,  certificate,
                province,  marital,  militarystatus,  about_me,  image,  fullname,  link,  sex,
                 levels,  salary,  categories,  benefits,  provinces,  contracts,  skills,  experiences,
                 academics,  languages,  mobile, year_birth);
        return  resume;
    }
}