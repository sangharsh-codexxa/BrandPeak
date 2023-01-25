package com.readymadedata.app.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readymadedata.app.items.BusinessCategoryItem;
import com.readymadedata.app.items.CategoryItem;
import com.readymadedata.app.items.CustomCategory;
import com.readymadedata.app.items.CustomInModel;
import com.readymadedata.app.items.FeatureItem;
import com.readymadedata.app.items.FestivalItem;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.items.StoryItem;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class DataConverters implements Serializable {

    @TypeConverter // note this annotation
    public String fromOptionValuesList(List<String> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<String> toOptionValuesList(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }


    @TypeConverter // note this annotation
    public String fromStory(List<StoryItem> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<StoryItem>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<StoryItem> toStory(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<StoryItem>>() {
        }.getType();
        List<StoryItem> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }



    @TypeConverter // note this annotation
    public String fromFestival(List<FestivalItem> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<FestivalItem>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<FestivalItem> toFestival(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<FestivalItem>>() {
        }.getType();
        List<FestivalItem> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }



    @TypeConverter // note this annotation
    public String fromBusinessCategory(List<BusinessCategoryItem> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<BusinessCategoryItem>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<BusinessCategoryItem> toBusinessCategory(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<BusinessCategoryItem>>() {
        }.getType();
        List<BusinessCategoryItem> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }


    @TypeConverter // note this annotation
    public String fromCategory(List<CategoryItem> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CategoryItem>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<CategoryItem> toCategory(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CategoryItem>>() {
        }.getType();
        List<CategoryItem> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }





    @TypeConverter // note this annotation
    public String fromPost(List<PostItem> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PostItem>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<PostItem> toPost(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<PostItem>>() {
        }.getType();
        List<PostItem> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }


    @TypeConverter // note this annotation
    public String fromFeature(List<FeatureItem> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<FeatureItem>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<FeatureItem> toFeature(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<FeatureItem>>() {
        }.getType();
        List<FeatureItem> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }

    @TypeConverter // note this annotation
    public String fromCustomModel(List<CustomCategory> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CustomCategory>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<CustomCategory> toCustomModel(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CustomCategory>>() {
        }.getType();
        List<CustomCategory> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }

    @TypeConverter // note this annotation
    public String fromCustomInModel(List<CustomInModel> optionValues) {
        if (optionValues == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CustomInModel>>() {
        }.getType();
        String json = gson.toJson(optionValues, type);
        return json;
    }

    @TypeConverter // note this annotation
    public List<CustomInModel> toCustomInModel(String optionValuesString) {
        if (optionValuesString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CustomInModel>>() {
        }.getType();
        List<CustomInModel> productCategoriesList = gson.fromJson(optionValuesString, type);
        return productCategoriesList;
    }

}
