package com.readymadedata.app.api;

import androidx.lifecycle.LiveData;
import com.readymadedata.app.SliderItem;
import com.readymadedata.app.items.AppInfo;
import com.readymadedata.app.items.BusinessCategoryItem;
import com.readymadedata.app.items.BusinessItem;
import com.readymadedata.app.items.CategoryItem;
import com.readymadedata.app.items.CouponItem;
import com.readymadedata.app.items.CustomCategory;
import com.readymadedata.app.items.CustomInModel;
import com.readymadedata.app.items.CustomModel;
import com.readymadedata.app.items.FestivalItem;
import com.readymadedata.app.items.HomeItem;
import com.readymadedata.app.items.LanguageItem;
import com.readymadedata.app.items.NewsItem;
import com.readymadedata.app.items.PersonalItem;
import com.readymadedata.app.items.PoliticalItem;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.items.StoryItem;
import com.readymadedata.app.items.SubjectItem;
import com.readymadedata.app.items.SubsPlanItem;
import com.readymadedata.app.items.User;
import com.readymadedata.app.items.UserFrame;
import com.readymadedata.app.items.UserItem;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @Multipart
    @POST("sdghhgh416546dd5654wst56w4646w46/add-business")
    LiveData<ApiResponse<List<BusinessItem>>> addBusiness(@Part("userId") RequestBody requestBody, @Part("bussinessImage") RequestBody requestBody2, @Part MultipartBody.Part part, @Part("bussinessName") RequestBody requestBody3, @Part("bussinessEmail") RequestBody requestBody4, @Part("bussinessNumber") RequestBody requestBody5, @Part("bussinessWebsite") RequestBody requestBody6, @Part("bussinessAddress") RequestBody requestBody7);

    @Multipart
    @POST("{API_KEY}/add-personal")
    LiveData<ApiResponse<List<PersonalItem>>> addPersonal(@Path("API_KEY") String str, @Part("userId") RequestBody requestBody, @Part MultipartBody.Part part, @Part("name") RequestBody requestBody3, @Part("email") RequestBody requestBody4, @Part("mobile") RequestBody requestBody5, @Part("address") RequestBody requestBody6, @Part("insta") RequestBody requestBody7, @Part("face") RequestBody requestBody8);

//    @Multipart
//    @POST("{API_KEY}/add-personal")
//    Call<ApiResponse<List<PersonalItem>>> addPersonalnew(@Path("API_KEY") String str, @Part("userId") RequestBody requestBody, @Part("logo") RequestBody requestBody2, @Part MultipartBody.Part part, @Part("name") RequestBody requestBody3, @Part("email") RequestBody requestBody4, @Part("mobile") RequestBody requestBody5, @Part("address") RequestBody requestBody6);


    @Multipart
    @POST("{API_KEY}/add-political")
    LiveData<ApiResponse<List<PoliticalItem>>> addPolitical(@Path("API_KEY") String str, @Part("userId") RequestBody requestBody, @Part("logo") RequestBody requestBody2, @Part MultipartBody.Part part, @Part("profile_logo") RequestBody requestBody3, @Part MultipartBody.Part part2, @Part("photo_a") RequestBody requestBody4, @Part MultipartBody.Part part3, @Part("photo_b") RequestBody requestBody5, @Part MultipartBody.Part part4, @Part("photo_c") RequestBody requestBody6, @Part MultipartBody.Part part5, @Part("name") RequestBody requestBody7, @Part("designation") RequestBody requestBody8, @Part("mobile_no") RequestBody requestBody9, @Part("facebook") RequestBody requestBody10, @Part("instagram") RequestBody requestBody11);

    @Multipart
    @POST("{API_KEY}/add-political")
    Call<ApiResponse<List<PoliticalItem>>> addPoliticals(@Path("API_KEY") String api,
                                                         @Part("userId") RequestBody userId,
                                                         @Part("logo") RequestBody logo, @Part MultipartBody.Part part,
                                                         @Part("profile") RequestBody logo_part, @Part MultipartBody.Part logo_partB,
                                                         @Part("photo_a") RequestBody photo_a_part, @Part MultipartBody.Part photo_a,
                                                         @Part("photo_b") RequestBody photo_b_part, @Part MultipartBody.Part photo_b,
                                                         @Part("photo_c") RequestBody photo_c_part, @Part MultipartBody.Part photo_c,
                                                         @Part("name") RequestBody nameRB,
                                                         @Part("designation") RequestBody designationRB,
                                                         @Part("mobile_no") RequestBody mobileRB,
                                                         @Part("facebook") RequestBody facebookRB,
                                                         @Part("instagram") RequestBody instagramRB);


    @FormUrlEncoded
    @POST("{API_KEY}/coupon-code-validation")
    Call<CouponItem> checkCoupon(@Path("API_KEY") String str, @Field("userId") String str2, @Field("code") String str3);

    @FormUrlEncoded
    @POST("{API_KEY}/delete-business")
    Call<ApiStatus> deleteBusiness(@Path("API_KEY") String str, @Field("bussinessId") String str2);

    @Multipart
    @POST("{API_KEY}/profile-update")
    LiveData<ApiResponse<UserItem>> doUploadImage(@Path("API_KEY") String str, @Part("id") RequestBody requestBody, @Part("image") RequestBody requestBody2, @Part MultipartBody.Part part, @Part("name") RequestBody requestBody3, @Part("email") RequestBody requestBody4, @Part("mobile_no") RequestBody requestBody5);

    @GET("{API_KEY}/custom-post")
    LiveData<ApiResponse<CustomModel>> getAllCustom(@Path("API_KEY") String str);

    @GET("{API_KEY}/custom-post")
    Call<List<CustomInModel>> getAllCustom1(@Path("API_KEY") String str);

    @GET("{API_KEY}/app-about")
    LiveData<ApiResponse<AppInfo>> getAppInfo(@Path("API_KEY") String str);

    @GET("{API_KEY}/business")
    LiveData<ApiResponse<List<BusinessItem>>> getBusiness(@Path("API_KEY") String str, @Query("userId") String str2);

    @GET("{API_KEY}/business")
    Call<List<BusinessItem>> getBusinessCall(@Path("API_KEY") String str, @Query("userId") String str2);

    @GET("{API_KEY}/business-category")
    LiveData<ApiResponse<List<BusinessCategoryItem>>> getBusinessCategory(@Path("API_KEY") String str);

    @GET("{API_KEY}/business-frame")
    Call<List<PostItem>> getBusinessList(@Path("API_KEY") String str,@Query("id") String str2);

    @GET("{API_KEY}/business-frame")
    Call<ApiResponse<List<PostItem>>> getBusinessPost(@Path("API_KEY") String str, @Query("id") String str2);

    @GET("{API_KEY}/category")
    LiveData<ApiResponse<List<CategoryItem>>> getCategory(@Path("API_KEY") String str, @Query("page") String str2);

    @GET("{API_KEY}/custom-category")
    LiveData<ApiResponse<List<CustomCategory>>> getCustomCategory(@Path("API_KEY") String str, @Query("page") String str2);

    @GET("{API_KEY}/custom-frame")
    LiveData<ApiResponse<List<PostItem>>> getCustomPost(@Path("API_KEY") String str, @Query("id") String str2);

    @GET("{API_KEY}/festival")
    LiveData<ApiResponse<List<FestivalItem>>> getFestival(@Path("API_KEY") String str, @Query("page") String str2);

    @GET("{API_KEY}/get-home-data")
    LiveData<ApiResponse<HomeItem>> getHomeData(@Path("API_KEY") String str);

    @POST("sdghhgh416546dd5654wst56w4646w46/banners")
    Call<List<SliderItem>> getHomeSliders();

    @GET("{API_KEY}/language")
    LiveData<ApiResponse<List<LanguageItem>>> getLanguages(@Path("API_KEY") String str);

    @GET("{API_KEY}/news")
    LiveData<ApiResponse<List<NewsItem>>> getNews(@Path("API_KEY") String str, @Query("page") String str2);

    @GET("{API_KEY}/personal")
    LiveData<ApiResponse<List<PersonalItem>>> getPersonal(@Path("API_KEY") String str, @Query("userId") String str2);

    @GET("{API_KEY}/personal")
    Call<List<PersonalItem>> getPersonalCall(@Path("API_KEY") String str, @Query("userId") String str2);

    @GET("{API_KEY}/subscription-plan")
    LiveData<ApiResponse<List<SubsPlanItem>>> getPlanData(@Path("API_KEY") String str);

    @GET("{API_KEY}/political")
    LiveData<ApiResponse<List<PersonalItem>>> getPolitical(@Path("API_KEY") String str, @Query("userId") String str2);

    @GET("{API_KEY}/political")
    Call<List<PoliticalItem>> getPoliticalCall(@Path("API_KEY") String str, @Query("userId") String str2);

//    @GET("{API_KEY}/political")
//    Call<List<PoliticalItem>> getPoliticalCall(@Path("API_KEY") String str, @Query("userId") String str2);

    @GET("{API_KEY}/get-post")
    LiveData<ApiResponse<List<PostItem>>> getPost(@Path("API_KEY") String str, @Query("type") String str2, @Query("id") String str3);

    @GET("{API_KEY}/story")
    LiveData<ApiResponse<List<StoryItem>>> getStory(@Path("API_KEY") String str);

    @GET("{API_KEY}/contact-subject")
    LiveData<ApiResponse<List<SubjectItem>>> getSubjectItems(@Path("API_KEY") String str);

    @GET("{API_KEY}/user?")
    LiveData<ApiResponse<UserItem>> getUserById(@Path("API_KEY") String str, @Query("id") String str2);

    @GET("{API_KEY}/user-custom-frame")
    LiveData<ApiResponse<List<UserFrame>>> getUserFrame(@Path("API_KEY") String str, @Query("userId") String str2);

    @GET("{API_KEY}/get-video")
    LiveData<ApiResponse<List<PostItem>>> getVideosById(@Path("API_KEY") String str, @Query("id") String str2, @Query("type") String str3);

    @FormUrlEncoded
    @POST("{API_KEY}/create-payment")
    Call<ApiStatus> loadPayment(@Path("API_KEY") String str, @Field("userId") String str2, @Field("planId") String str3, @Field("paymentId") String str4, @Field("paymentAmount") String str5, @Field("code") String str6);

    @FormUrlEncoded
    @POST("sdghhgh416546dd5654wst56w4646w46/adduser")
    Call<User> postAddUser(@Field("mobile_no") String str);

    @FormUrlEncoded
    @POST("{API_KEY}/forgot-password")
    LiveData<ApiResponse<ApiStatus>> postForgotPassword(@Path("API_KEY") String str, @Field("email") String str2);

    @FormUrlEncoded
    @POST("{API_KEY}/google-registration")
    Call<UserItem> postGoogleUser(@Path("API_KEY") String str, @Field("name") String str2, @Field("email") String str3, @Field("image") String str4);

    @FormUrlEncoded
    @POST("{API_KEY}/change-password")
    LiveData<ApiResponse<ApiStatus>> postPasswordUpdate(@Path("API_KEY") String str, @Field("userId") String str2, @Field("newPassword") String str3);

    @FormUrlEncoded
    @POST("{API_KEY}/registration")
    Call<UserItem> postUser(@Path("API_KEY") String str, @Field("name") String str2, @Field("email") String str3, @Field("password") String str4, @Field("mobile_no") String str5);

    @FormUrlEncoded
    @POST("{API_KEY}/login")
    LiveData<ApiResponse<UserItem>> postUserLogin(@Path("API_KEY") String str, @Field("email") String str2, @Field("password") String str3);

    @FormUrlEncoded
    @POST("{API_KEY}/resend-verify-code")
    Call<ApiStatus> resentCodeAgain(@Path("API_KEY") String str, @Field("userId") String str2);

    @FormUrlEncoded
    @POST("{API_KEY}/contact-massage")
    Call<ApiStatus> sendContact(@Path("API_KEY") String str, @Field("name") String str2, @Field("email") String str3, @Field("mobileNo") String str4, @Field("message") String str5, @Field("subjectId") String str6);

    @FormUrlEncoded
    @POST("{API_KEY}/set-default-business")
    Call<ApiStatus> setDefault(@Path("API_KEY") String str, @Field("userId") String str2, @Field("bussinessId") String str3);

    @Multipart
    @POST("{API_KEY}/update-business")
    LiveData<ApiResponse<List<BusinessItem>>> updateBusiness(@Path("API_KEY") String str, @Part("userId") RequestBody requestBody, @Part("bussinessImage") RequestBody requestBody2, @Part MultipartBody.Part part, @Part("bussinessName") RequestBody requestBody3, @Part("bussinessEmail") RequestBody requestBody4, @Part("bussinessNumber") RequestBody requestBody5, @Part("bussinessWebsite") RequestBody requestBody6, @Part("bussinessAddress") RequestBody requestBody7);

//    @Part("personalImage") RequestBody personalImage_requestBody2, @Part MultipartBody.Part part,
    @Multipart
    @POST("{API_KEY}/update-personal")
    Call<ApiResponse<List<BusinessItem>>> updatePersonal(@Path("API_KEY") String str, @Part("userId") RequestBody userIdrequestBody,  @Part("personalName") RequestBody personalNamerequestBody3, @Part MultipartBody.Part logo,  @Part("personalNumber") RequestBody perosnalNumberrequestBody4, @Part("personalAddress") RequestBody personalAddressrequestBody5, @Part("personalFacebookUser") RequestBody personalFacebookUserrequestBody6, @Part("personalInstaUser") RequestBody personalInstaUserrequestBody7);



    @Multipart
    @POST("{API_KEY}/update-political")
    Call<ApiResponse<List<BusinessItem>>> updateBusiness1(@Path("API_KEY") String str, @Part("id") RequestBody requestBody, @Part("logo") RequestBody requestBody2, @Part MultipartBody.Part part, @Part("profile") RequestBody requestBody3, @Part MultipartBody.Part part2, @Part("name") RequestBody requestBody4, @Part("designation") RequestBody requestBody5, @Part("mobile_no") RequestBody requestBody6, @Part("face_username") RequestBody requestBody7, @Part("insta_username") RequestBody requestBody8);

//    @Multipart
//    @POST("{API_KEY}/update-personal")
//    Call<ApiResponse<List<PersonalItem>>> updatePersonal(@Path("API_KEY") String str, @Part("id") RequestBody requestBody, @Part("logo") RequestBody requestBody2, @Part MultipartBody.Part part, @Part("name") RequestBody requestBody3, @Part("face_username") RequestBody requestBody4, @Part("insta_username") RequestBody requestBody5, @Part("mobile_no") RequestBody requestBody6);

    @Multipart
    @POST("{API_KEY}/update-political")
    Call<ApiResponse<List<PoliticalItem>>> updatePolitical(@Path("API_KEY") String api,
                                                           @Part("userId") RequestBody userId,
                                                           @Part("logo") RequestBody logo, @Part MultipartBody.Part part,
                                                           @Part("profile") RequestBody logo_part, @Part MultipartBody.Part logo_partB,
                                                           @Part("photo_a") RequestBody photo_a_part, @Part MultipartBody.Part photo_a,
                                                           @Part("photo_b") RequestBody photo_b_part, @Part MultipartBody.Part photo_b,
                                                           @Part("photo_c") RequestBody photo_c_part, @Part MultipartBody.Part photo_c,
                                                           @Part("name") RequestBody nameRB,
                                                           @Part("designation") RequestBody designationRB,
                                                           @Part("mobile_no") RequestBody mobileRB,
                                                           @Part("facebook") RequestBody facebookRB,
                                                           @Part("instagram") RequestBody instagramRB);

    @FormUrlEncoded
    @POST("{API_KEY}/verify-account")
    LiveData<ApiResponse<ApiStatus>> verifyEmail(@Path("API_KEY") String str, @Field("userId") String str2, @Field("code") String str3);
}
