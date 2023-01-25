package com.readymadedata.app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.readymadedata.app.items.AppInfo;
import com.readymadedata.app.items.AppVersion;
import com.readymadedata.app.items.BusinessCategoryItem;
import com.readymadedata.app.items.BusinessItem;
import com.readymadedata.app.items.CategoryItem;
import com.readymadedata.app.items.CustomCategory;
import com.readymadedata.app.items.CustomModel;
import com.readymadedata.app.items.FestivalItem;
import com.readymadedata.app.items.HomeItem;
import com.readymadedata.app.items.LanguageItem;
import com.readymadedata.app.items.NewsItem;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.items.StoryItem;
import com.readymadedata.app.items.SubjectItem;
import com.readymadedata.app.items.SubsPlanItem;
import com.readymadedata.app.items.UserFrame;
import com.readymadedata.app.items.UserItem;
import com.readymadedata.app.items.UserLogin;

@Database(entities = {StoryItem.class, FestivalItem.class, CategoryItem.class, PostItem.class,
        LanguageItem.class, UserItem.class,
        UserLogin.class, BusinessItem.class, SubsPlanItem.class,
        SubjectItem.class, NewsItem.class, AppVersion.class, AppInfo.class, CustomCategory.class, HomeItem.class,
        BusinessCategoryItem.class, CustomModel.class, UserFrame.class}, version = 17, exportSchema = false)
@TypeConverters({DataConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "festival_database";

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }

    public abstract StoryDao getStoryDao();

    public abstract FestivalDao getFestivalDao();

    public abstract CategoryDao getCategoryDao();

    public abstract PostDao getPostDao();

    public abstract LanguageDao getLanguageDao();

    public abstract UserDao getUserDao();

    public abstract BusinessDao getBusinessDao();

    public abstract SubsPlanDao getSubsPlanDao();

    public abstract NewsDao getNewsDao();

    public abstract UserLoginDao getUserLoginDao();

    public abstract CustomCategoryDao getCustomCategoryDao();

    public abstract HomeDao getHomeDao();
}

