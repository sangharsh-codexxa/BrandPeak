package com.readymadedata.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.readymadedata.app.R;
import com.readymadedata.app.binding.GlideBinding;
import com.readymadedata.app.listener.ClickListener;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.MyViewHolder> {

    Context context;
    int[] stickers;
    ClickListener<Integer> listener;

    public StickerAdapter(Context context, int[] stickers, ClickListener<Integer> listener) {
        this.context = context;
        this.stickers = stickers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GlideBinding.bindImage(holder.mImageView, "file:///android_asset/" + "stickers" + "/" + stickers[position] + ".png");
        holder.itemView.setOnClickListener(v -> {
            listener.onClick(stickers[position]);
        });
    }

    @Override
    public int getItemCount() {
        return stickers.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgSticker);
        }
    }
}
