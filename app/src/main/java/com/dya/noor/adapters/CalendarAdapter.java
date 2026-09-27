package com.dya.noor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.DayModel;

import org.jspecify.annotations.NonNull;

import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    private List<DayModel> list;
    private Context context;

    public CalendarAdapter(Context context, List<DayModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {

        DayModel model = list.get(position);

        if (model.day == 0) {
            holder.tvDay.setText("");
            holder.tvHijri.setText("");
            return;
        }
        holder.itemView.setAlpha(model.isCurrentMonth ? 1f : 0.5f);
        holder.tvDay.setText(String.valueOf(model.day));

        // Hijri conversion
        holder.tvHijri.setText(getHijriDate(model));

        // Today highlight
        if (model.isToday) {
            holder.itemView.setBackgroundResource(R.drawable.bg_today);
        }

        // Friday highlight
        Calendar cal = Calendar.getInstance();
        cal.set(model.year, model.month, model.day);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.FRIDAY) {
            holder.tvDay.setTextColor(Color.parseColor("#E53935"));
        }

        if (!model.isCurrentMonth) {
            holder.tvDay.setAlpha(0.3f);
            holder.tvHijri.setAlpha(0.3f);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvHijri;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvHijri = itemView.findViewById(R.id.tvHijri);
        }
    }

    // Simple Hijri conversion (basic)
    private String getHijriDate(DayModel model) {

        try {
            java.time.LocalDate gregorianDate =
                    java.time.LocalDate.of(model.year, model.month + 1, model.day);

            java.time.chrono.HijrahDate hijri =
                    java.time.chrono.HijrahDate.from(gregorianDate);

            int day = hijri.get(java.time.temporal.ChronoField.DAY_OF_MONTH);
            int month = hijri.get(java.time.temporal.ChronoField.MONTH_OF_YEAR);

            String[] hijriMonths = {
                    "محرم", "صفر", "ربيع١", "ربيع٢",
                    "جمادى١", "جمادى٢",
                    "رجب", "شعبان", "رمضان",
                    "شوال", "ذو القعدة", "ذو الحجة"
            };

            return day + " " + hijriMonths[month - 1];

        } catch (Exception e) {
            return "";
        }
    }
}