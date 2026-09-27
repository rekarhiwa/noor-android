package com.dya.noor.adapters;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dya.noor.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private Context context;
    private List<String> imageList;
    ViewPager2 viewPager2;

    public SliderAdapter(Context context, List<String> imageList, ViewPager2 viewPager2) {
        this.context = context;
        this.imageList = imageList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String fileName = imageList.get(position);
        AssetManager assetManager = context.getAssets();
        try {
            InputStream is = assetManager.open("image/" + fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.imageSlider.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageSlider;

        SliderViewHolder(View itemView) {
            super(itemView);
            imageSlider = itemView.findViewById(R.id.imageSlider);
        }
    }
}

