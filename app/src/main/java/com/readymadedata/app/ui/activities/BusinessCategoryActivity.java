package com.readymadedata.app.ui.activities;

import static com.readymadedata.app.utils.Constant.BUSINESS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.readymadedata.app.R;
import com.readymadedata.app.adapters.BusinessCategoryAdapter;
import com.readymadedata.app.databinding.ActivityCustomCategoryBinding;
import com.readymadedata.app.items.BusinessCategoryItem;
import com.readymadedata.app.listener.ClickListener;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.viewmodel.CategoryViewModel;

public class BusinessCategoryActivity extends AppCompatActivity implements ClickListener<BusinessCategoryItem> {

    ActivityCustomCategoryBinding binding;
    BusinessCategoryAdapter adapter;
    CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpUi();
        initViewModel();
    }

    private void setUpUi() {
        binding.toolbar.toolbarIvMenu.setBackground(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.toolName.setText(getResources().getString(R.string.business_category));

        binding.toolbar.toolbarIvMenu.setOnClickListener(v -> {
            onBackPressed();
        });

        adapter = new BusinessCategoryAdapter(this, this, false);
        binding.rvCustomCategory.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            categoryViewModel.setBusinessCategoryObj("Category");
        });
    }

    private void initViewModel() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.setBusinessCategoryObj("Category");
        categoryViewModel.getBusinessCategories().observe(this, result->{
            if (result.data!=null){
                binding.swipeRefresh.setRefreshing(false);
                if (result.data.size()>0){
                    binding.animationView.setVisibility(View.GONE);
                    adapter.setCategories(result.data);
                }else{
                    binding.animationView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(BusinessCategoryItem data) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constant.INTENT_TYPE, BUSINESS);
        intent.putExtra(Constant.INTENT_FEST_ID, data.businessCategoryId);
        intent.putExtra(Constant.INTENT_FEST_NAME, data.businessCategoryName);
        intent.putExtra(Constant.INTENT_POST_IMAGE, "");
        intent.putExtra(Constant.INTENT_VIDEO, data.video);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}