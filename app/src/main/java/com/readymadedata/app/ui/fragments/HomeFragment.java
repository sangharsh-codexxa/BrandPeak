package com.readymadedata.app.ui.fragments;

import static android.widget.LinearLayout.HORIZONTAL;



import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.readymadedata.app.R;
import com.readymadedata.app.HomeSliderAdapter;
import com.readymadedata.app.SliderItem;
import com.readymadedata.app.adapters.DetailAdapter;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.databinding.FragmentHomeBinding;
import com.readymadedata.app.items.HomeItem;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.items.UserItem;
import com.readymadedata.app.ui.activities.AddBusinessActivity;
import com.readymadedata.app.ui.activities.EditorActivity;
import com.readymadedata.app.ui.activities.SearchActivity;
import com.readymadedata.app.ui.activities.SubsPlanActivity;
import com.readymadedata.app.ui.dialog.DialogMsg;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;
import com.readymadedata.app.viewmodel.HomeViewModel;
import com.readymadedata.app.viewmodel.PostViewModel;
import com.readymadedata.app.viewmodel.UserViewModel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* renamed from: com.readymadedata.app.ui.fragments.HomeFragment */
public class HomeFragment extends Fragment {
    DetailAdapter adapter;
    List<SliderItem> bannerList;
    FragmentHomeBinding binding;
    HomeViewModel homeViewModel;
    int position = 0;
    List<PostItem> postItemList;
    PostViewModel postViewModel;
    UserItem userItem;
    PrefManager prefManager;
    Runnable runnable;
    Handler handler;

    public static final String LOGTAG = "slider";
    /* access modifiers changed from: private */
    public Handler sliderHandler = new Handler();
    /* access modifiers changed from: private */
    public Runnable sliderRunnable = new Runnable() {
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 2);
        }
    };
    DialogMsg dialogMsg;

    UserViewModel userViewModel;
    /* access modifiers changed from: private */
    public ViewPager2 viewPager2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentHomeBinding inflate = FragmentHomeBinding.inflate(getLayoutInflater());
        binding = inflate;

        dialogMsg = new DialogMsg(getActivity(), false);

        Util.fadeIn(inflate.getRoot(), getContext());
        this.prefManager = new PrefManager(getActivity());
        bannerList = new ArrayList();
        initViewModel();
        setData();

        return binding.getRoot();
    }

    private void initViewModel() {
        this.viewPager2 = binding.viewpager;
        this.postViewModel = (PostViewModel) new ViewModelProvider(this).get(PostViewModel.class);
        this.userViewModel = (UserViewModel) new ViewModelProvider(this).get(UserViewModel.class);
        binding.swipeRefresh.setRefreshing(false);
        binding.shimmerViewContainer.stopShimmer();
        binding.shimmerViewContainer.setVisibility(View.GONE);
        binding.mainContainer.setVisibility(View.VISIBLE);
    }

    private void setData() {

        ApiClient.getApiService().getHomeSliders().enqueue(new Callback<List<SliderItem>>() {
            public void onResponse(Call<List<SliderItem>> call, Response<List<SliderItem>> response) {
                Log.e("Errorr--->", response.toString());
                if (response.isSuccessful()) {
                    bannerList = response.body();
                    viewPager2.setAdapter(new HomeSliderAdapter(bannerList, viewPager2));
                    viewPager2.setClipToPadding(false);
                    viewPager2.setClipChildren(false);
                    viewPager2.setOffscreenPageLimit(3);
                    viewPager2.getChildAt(0).setOverScrollMode(2);
                    CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                    compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                    compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                        public void transformPage(View page, float position) {
                            page.setScaleY((0.15f * (1.0f - Math.abs(position))) + 0.85f);
                        }
                    });
                    viewPager2.setPageTransformer(compositePageTransformer);
                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            sliderHandler.removeCallbacks(sliderRunnable);
                            sliderHandler.postDelayed(sliderRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        }
                    });
                }
            }

            public void onFailure(Call<List<SliderItem>> call, Throwable t) {
                Log.e("Errorr--->", t.getMessage());
            }
        });
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getDbUserData(prefManager.getString(Constant.USER_ID)).observe(this.getActivity(), result -> {
            if (result != null) {
                userItem = result.user;
            }
        });
        binding.btnUpgrade.setOnClickListener(e -> {
            if (userItem.isSubscribed) {
                showWarningDialogue();

            } else {
                startActivity(new Intent(e.getContext(), SubsPlanActivity.class));

            }
        });
        binding.viewpager.setClipToPadding(false);
        binding.viewpager.setClipChildren(false);
        binding.viewpager.setOffscreenPageLimit(3);
        binding.viewpager.getChildAt(0).setOverScrollMode(2);
        new CompositePageTransformer().addTransformer(new MarginPageTransformer(40));
        binding.rvPost.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        postItemList = new ArrayList();


        ApiClient.getApiService().getBusinessList("sdghhgh416546dd5654wst56w4646w46", prefManager.getString(Constant.USER_LANGUAGE)).enqueue(new Callback<List<PostItem>>() {
            public void onResponse(Call<List<PostItem>> call, Response<List<PostItem>> response) {
                Log.e("Error--->", response.toString());
                if (response.isSuccessful()) {
                    postItemList = response.body();
                    setData(response.body());
                }
            }

            public void onFailure(Call<List<PostItem>> call, Throwable t) {
                Log.e("ErrorPOst--->", t.getMessage());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(binding.getRoot().getContext()) {
                    private static final float SPEED = 4000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvTrendingPosts.setLayoutManager(layoutManager);

        binding.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class).putExtra("viewAll", "view"));
            }
        });


        adapter = new DetailAdapter(getActivity().getApplicationContext(), data -> {
            if (postItemList != null) {
                position = data;
                startActivity(new Intent(getActivity(), EditorActivity.class).putExtra("type", Constant.BUSINESS).putExtra(Constant.INTENT_POST_LIST, (Serializable) this.postItemList).putExtra("data", data).putExtra(Constant.INTENT_POS, data).putExtra(Constant.INTENT_TYPE, "custom").putExtra("img_url", this.postItemList.get(data.intValue()).image_url));
            }
        }, 3, getResources().getDimension(com.intuit.ssp.R.dimen._2ssp));
        binding.rvPost.setAdapter(adapter);
        binding.rvTrendingPosts.setAdapter(adapter);


        LinearLayoutManager layoutManagerSl = new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvTrendingPosts.setLayoutManager(layoutManagerSl);
        AutoScrollHelper autoScrollHelper = new AutoScrollHelper(binding.rvTrendingPosts, layoutManagerSl);
        binding.rvTrendingPosts.addOnScrollListener(autoScrollHelper);
        autoScrollHelper.startAutoScroll();

    }


    public class AutoScrollHelper extends RecyclerView.OnScrollListener {

        private static final int AUTO_SCROLL_DELAY = 3000; // delay in milliseconds
        private static final int AUTO_SCROLL_SPEED = 1000; // speed in milliseconds

        private final Handler handler = new Handler();
        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (recyclerView != null && recyclerView.getAdapter() != null) {
                    int nextItem = layoutManager.findLastCompletelyVisibleItemPosition() + 1;
                    if (nextItem == recyclerView.getAdapter().getItemCount()) {
                        nextItem = 0;
                    }
                    recyclerView.smoothScrollToPosition(nextItem);
                    handler.postDelayed(this, AUTO_SCROLL_DELAY);
                }
            }
        };
        private RecyclerView recyclerView;
        private LinearLayoutManager layoutManager;

        public AutoScrollHelper(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
            this.recyclerView = recyclerView;
            this.layoutManager = layoutManager;
        }

        public void startAutoScroll() {
            handler.postDelayed(runnable, AUTO_SCROLL_DELAY);
        }

        public void stopAutoScroll() {
            handler.removeCallbacks(runnable);
        }




}

    public void setData(Integer data1) {
        if (this.postItemList != null) {
            this.position = data1.intValue();
            if (!this.prefManager.getString(Constant.USER_ID).equals("")) {
                try {
                    startActivity(new Intent(getActivity(), EditorActivity.class).putExtra("type", Constant.BUSINESS).putExtra(Constant.INTENT_POST_LIST, (Serializable) this.postItemList).putExtra("data", data1).putExtra(Constant.INTENT_POS, data1).putExtra(Constant.INTENT_TYPE, "custom").putExtra("img_url", this.postItemList.get(data1.intValue()).image_url));
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                startActivity(new Intent(getActivity(), AddBusinessActivity.class));
            }
        }
    }

    public void onPause() {
        super.onPause();
        this.sliderHandler.removeCallbacks(this.sliderRunnable);
    }

    public void onResume() {
        super.onResume();
        this.sliderHandler.postDelayed(this.sliderRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    private void setHomeData(HomeItem data) {
        this.homeViewModel.setLoadingState(false);
    }


    public void setData(List<PostItem> data) {
        adapter.setData(data);
        binding.rvPost.setVisibility(View.VISIBLE);
        binding.shimmerViewContainer.setVisibility(View.GONE);
        binding.rvPost.setAdapter(this.adapter);
    }

    private void showWarningDialogue() {
        dialogMsg.showConfirmDialog("Already Subscribed!", "Are you want to upgrade your plan?", "Upgrade Now", getString(R.string.cancel));
        dialogMsg.show();
        dialogMsg.okBtn.setOnClickListener(v -> {
            dialogMsg.cancel();
            startActivity(new Intent(this.getActivity(),SubsPlanActivity.class));
        });
    }






}