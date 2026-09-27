package com.dya.noor.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dya.noor.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeSliderAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PRAYER = 0;
    private static final int TYPE_SLIDER = 1;

    private final Context context;

    private JSONArray slides;

    /*
     * Create the prayer view immediately.
     *
     * This is important because MainActivity needs to access
     * date2, btnThirty, mCity, katymawa, etc. during onCreate().
     */
    private final View prayerView;

    public HomeSliderAdapter(Context context, JSONArray slides) {
        this.context = context;
        if (slides != null) {
            this.slides = slides;
        } else {
            this.slides = new JSONArray();
        }

        // Create prayer card immediately
        prayerView = LayoutInflater.from(context).inflate(R.layout.item_prayer_card, null, false
        );

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        prayerView.setLayoutParams(params);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_PRAYER;
        }

        return TYPE_SLIDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( @NonNull ViewGroup parent,int viewType) {

        if (viewType == TYPE_PRAYER) {

            return new PrayerViewHolder(
                    prayerView
            );
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_ad_slider, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {

        // ---------------------------------------------------------
        // PRAYER CARD
        // ---------------------------------------------------------

        if (holder instanceof PrayerViewHolder) {
            return;
        }

        // ---------------------------------------------------------
        // API SLIDER
        // ---------------------------------------------------------

        if (holder instanceof SliderViewHolder) {

            SliderViewHolder sliderHolder =
                    (SliderViewHolder) holder;

            int slideIndex = position - 1;

            try {

                JSONObject slide =
                        slides.getJSONObject(slideIndex);

                String image =
                        slide.optString(
                                "image",
                                ""
                        );

                String title =
                        slide.optString(
                                "title",
                                ""
                        );

                String description =
                        slide.optString(
                                "description",
                                ""
                        );

                String link =
                        slide.optString(
                                "link",
                                ""
                        );

                // Text
                sliderHolder.title.setText(title);

                sliderHolder.description.setText(
                        description
                );

                // Image
                Glide.with(context)
                        .load(image)
                        .centerCrop()
                        .into(sliderHolder.image);

                // Click
                if (!link.isEmpty()) {

                    sliderHolder.itemView.setOnClickListener(
                            v -> {

                                try {

                                    Intent intent =
                                            new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(link)
                                            );

                                    context.startActivity(intent);

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                    );

                } else {

                    sliderHolder.itemView.setOnClickListener(
                            null
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {

        // Position 0 = prayer card
        // Remaining positions = API slides

        return slides.length() + 1;
    }

    // =============================================================
    // UPDATE API SLIDES
    // =============================================================

    public void setSlides(JSONArray newSlides) {

        if (newSlides != null) {

            slides = newSlides;

        } else {

            slides = new JSONArray();
        }

        notifyDataSetChanged();
    }

    // =============================================================
    // GET PRAYER VIEW
    // =============================================================

    public View getPrayerView() {

        return prayerView;
    }

    // =============================================================
    // PRAYER VIEW HOLDER
    // =============================================================

    static class PrayerViewHolder
            extends RecyclerView.ViewHolder {

        PrayerViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);
        }
    }

    // =============================================================
    // SLIDER VIEW HOLDER
    // =============================================================

    static class SliderViewHolder
            extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView description;

        SliderViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            image = itemView.findViewById(
                    R.id.sliderImage
            );

            title = itemView.findViewById(
                    R.id.sliderTitle
            );

            description = itemView.findViewById(
                    R.id.sliderDescription
            );
        }
    }
}