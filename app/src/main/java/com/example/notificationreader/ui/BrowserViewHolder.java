package com.example.notificationreader.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationreader.R;

public class BrowserViewHolder extends RecyclerView.ViewHolder {

    public ConstraintLayout item;
    public ImageView icon;
    public TextView name;
    public TextView preview;
    public TextView text;
    public TextView date;
    public TextView itemTime;
    public TextView itemDate;

    public BrowserViewHolder(@NonNull View view) {
        super(view);
        item = view.findViewById(R.id.item);
        icon = view.findViewById(R.id.icon);
        name = view.findViewById(R.id.name);
        preview = view.findViewById(R.id.preview);
        text = view.findViewById(R.id.text);
        date = view.findViewById(R.id.date);
        itemTime = view.findViewById(R.id.item_time);
        itemDate = view.findViewById(R.id.item_date);

    }
}
