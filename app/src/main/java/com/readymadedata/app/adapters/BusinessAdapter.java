package com.readymadedata.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.readymadedata.app.databinding.ItemBusinessBinding;
import com.readymadedata.app.items.BusinessItem;
import com.readymadedata.app.listener.ClickListener;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.MyViewHolder> {

    Context content;
    ClickListener<BusinessItem> listener;
    OnClick clickListener;
    List<BusinessItem> businessItemList;

    public BusinessAdapter(Context content, ClickListener<BusinessItem> listener, OnClick clickListener) {
        this.content = content;
        this.listener = listener;
        this.clickListener = clickListener;
    }

    public void setBusinessItemList(List<BusinessItem> businessItemList) {
        this.businessItemList = businessItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBusinessBinding binding = ItemBusinessBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setBusiness(businessItemList.get(position));
        holder.itemView.setOnClickListener(v -> {
            listener.onClick(businessItemList.get(position));
        });
        holder.binding.cvBusinessEdit.setOnClickListener(v -> {
            clickListener.OnEdit(businessItemList.get(position));
        });
        holder.binding.cvBusinessDelete.setOnClickListener(v -> {
            clickListener.OnDelete(businessItemList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        if (businessItemList != null && businessItemList.size() > 0) {
            return businessItemList.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemBusinessBinding binding;

        public MyViewHolder(@NonNull ItemBusinessBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClick {
        void OnEdit(BusinessItem businessItem);

        void OnDelete(BusinessItem businessItem);
    }
}
