package com.readymadedata.app.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.readymadedata.app.R;
import com.readymadedata.app.adapters.DetailAdapter;
import com.readymadedata.app.adapters.PostAdapter;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.databinding.ActivitySearchBinding;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
   private ActivitySearchBinding binding;
   private List<PostItem> originalItems;
    DetailAdapter adapter;
    int position = 0;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefManager = new PrefManager(this);



        if(getIntent().getStringExtra("viewAll")!=null)
        {
            binding.lvlA.setVisibility(View.VISIBLE);
            binding.svSearchData.setVisibility(View.GONE);
        }

        searchData();
        binding.rvPost.setLayoutManager(new GridLayoutManager(this, 3));

        originalItems = new ArrayList<>();

        ApiClient.getApiService().getBusinessList("sdghhgh416546dd5654wst56w4646w46",prefManager.getString(Constant.USER_LANGUAGE)).enqueue(new Callback<List<PostItem>>() {
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                if (response.isSuccessful()) {

                    originalItems = response.body();
//        detailAdapter.setData(data);
//        binding.rvPost.setVisibility(View.VISIBLE);
//        binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.rvPost.setAdapter(adapter);
                    Log.e("data--->",  String.valueOf(response.body().size()));
                    setData(response.body());

                }
            }
            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                Log.e("ErrorPOst--->", t.getMessage());
            }
        });
        adapter = new DetailAdapter(getApplicationContext(), data -> {
            if (originalItems != null) {
                position = data;
                startActivity(new Intent(this, EditorActivity.class).putExtra("type", Constant.BUSINESS).putExtra(Constant.INTENT_POST_LIST, (Serializable) originalItems).putExtra("data", data).putExtra(Constant.INTENT_POS, data).putExtra(Constant.INTENT_TYPE, "custom").putExtra("img_url", originalItems.get(data.intValue()).image_url));

            }
        }, 3, getResources().getDimension(com.intuit.ssp.R.dimen._2ssp));

        binding.rvPost.setAdapter(adapter);

    }



    private void searchData() {

        binding.svSearchData.setFocusable(true);

        binding.svSearchData.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);

                return false;
            }
        });
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        List<PostItem> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (PostItem item : originalItems) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.title.toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    public void setData(List<PostItem> data) {
        adapter.setData(data);
        binding.rvPost.setVisibility(View.VISIBLE);
        binding.rvPost.setAdapter(this.adapter);
    }


}