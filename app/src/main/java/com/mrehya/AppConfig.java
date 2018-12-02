package com.mrehya;

/**
 * Created by arash on 8/6/2017.
 */

public class AppConfig {

    public static String AUTH_USERNAME = "auth_key";
    public static String AUTH_PASS = "";

    public static String URL_SIGNUP = "https://api.mrehya.com/v1/user/signup";

    public static String URL_SIGNIN = "https://api.mrehya.com/v1/user/signin";

    public static String URL_VIEW_Profile = "https://api.mrehya.com/v1/user/view";

    public static String URL_EDIT_Profile = "https://api.mrehya.com/v1/user/edit";

    public static String URL_Provinces = "https://api.mrehya.com/v1/province/index";

    public static String URL_JobCats = "https://api.mrehya.com/v1/category/index/?lang=";

    public static String URL_Corporations = "https://api.mrehya.com/v1/lookup/index?type=Contract&lang=";

    public static String URL_Hires = "https://api.mrehya.com//v1/ads/index?lang=";

    public static String URL_DashExam = "https://api.mrehya.com/v1/quizzes/index?type=";

    public static String URL_Products = "https://api.mrehya.com//v1/products/index";

    public static String URL_MAKE_RESUME = "https://api.mrehya.com/v1/user/builder";
    public static String URL_SEND_RESUME = "https://api.mrehya.com/v1/ads/send/";
    public static String URL_UPDATE_RESUME = "https://api.mrehya.com/v1/user/update";
    public static String URL_GETAPI_RESUME = "https://api.mrehya.com/v1/resume/view/";


    public static String URL_ResumeLanguages = "https://api.mrehya.com/v1/user/language?id=";
    public static String URL_ResumeExperiences = "https://api.mrehya.com/v1/user/experience?id=";
    public static String URL_ResumeAcademics = "https://api.mrehya.com/v1/user/academic?id=";

    public static String URL_SEND_RESERVE = "https://api.mrehya.com/v1/user/reserve";
    public static String URL_Get_FreeHours = "https://api.mrehya.com/v1/queue/index?date=";
    public static String URL_Get_RESERVE_REQUEST = "https://api.mrehya.com/v1/lookup/index?type=RequestFor&lang=";

    public static String URL_SEND_ANSWER = "https://api.mrehya.com/v1/user/answer";
    public static String URL_Get_Exam = "https://api.mrehya.com/v1/quizzes/view/";
    public static String URL_Get_Exam_CODE = "https://api.mrehya.com/v1/user/end/";
    public static String URL_Get_Exam_RESULT = "https://api.mrehya.com/v1/user/result/";
}
