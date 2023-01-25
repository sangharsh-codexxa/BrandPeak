package com.readymadedata.app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.readymadedata.app.R;
import com.readymadedata.app.MyApplication;
import com.readymadedata.app.binding.GlideBinding;
import com.readymadedata.app.databinding.ItemFrameBinding;
import com.readymadedata.app.items.FrameItem;
import com.readymadedata.app.listener.ClickListener;
import java.util.List;

public class PoliticalFrameAdapter extends RecyclerView.Adapter<PoliticalFrameAdapter.MyViewHolder> {
    int column;
    public Context context;
    public List<FrameItem> frameItemList;
    private int itemWidth = 0;
    public ClickListener<Integer> listener;
    private int selectedPos = 0;
    float width;

    public PoliticalFrameAdapter(Context context2, ClickListener<Integer> listener2, int column2, float width2) {
        this.context = context2;
        this.listener = listener2;
        this.itemWidth = MyApplication.getColumnWidth(column2, width2);
    }

    public void setFrameItemList(List<FrameItem> frameItemList2) {
        this.frameItemList = frameItemList2;
        notifyDataSetChanged();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemFrameBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.setFrameData(this.frameItemList.get(position));
        if (this.frameItemList.get(position).is_from_url) {
            GlideBinding.bindImage(holder.binding.ivPost, this.frameItemList.get(position).imageUrl);
        } else {
            holder.binding.flPost.removeAllViews();
            holder.binding.ivPost.setImageResource(this.frameItemList.get(position).previewImage);
        }
        if (this.selectedPos == holder.getAdapterPosition()) {
            holder.binding.cvBase.setCardBackgroundColor(ColorStateList.valueOf(this.context.getResources().getColor(R.color.active_color)));
        } else {
            holder.binding.cvBase.setCardBackgroundColor(ColorStateList.valueOf(this.context.getResources().getColor(R.color.transparent_color)));
        }
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               clickListener(position);
           }
       });
    }

    public void clickListener(int position) {
         listener.onClick(Integer.valueOf(position));
    }

    public void setSelected(int pos) {
        int oldPos = this.selectedPos;
        this.selectedPos = pos;
        notifyItemChanged(oldPos);
        notifyItemChanged(pos);
    }

    public int getSelectedPos() {
        return this.selectedPos;
    }

    public int getItemCount() {
        List<FrameItem> list = this.frameItemList;
        if (list == null || list.size() <= 0) {
            return 0;
        }
        return this.frameItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemFrameBinding binding;

        public MyViewHolder(ItemFrameBinding binding2) {
            super(binding2.getRoot());
            this.binding = binding2;
        }
    }
}
