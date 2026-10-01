package com.dya.noor.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Spotlight / Coach Mark guided tours — ported from noor-main Flutter AppCoachTour.
 */
public final class AppCoachTour {

    private static final String PREFS = "coach_tour";
    public static final String KEY_HOME = "coach_tour_home_v2";
    public static final String KEY_SETTINGS = "coach_tour_settings_v2";
    public static final String KEY_QURAN = "coach_tour_quran_v2";
    public static final String KEY_KHATM = "coach_tour_khatm_v2";
    public static final String KEY_LISTEN = "coach_tour_listen_v2";
    private static final String KEY_REPLAY_PENDING = "coach_tour_replay_pending";

    private static OverlayHost activeHost;

    private AppCoachTour() {
    }

    public static class Step {
        @Nullable
        public final View target;
        public final String title;
        public final String body;

        public Step(@Nullable View target, String title, String body) {
            this.target = target;
            this.title = title;
            this.body = body;
        }
    }

    public static boolean isDone(Context context, String key) {
        return prefs(context).getBoolean(key, false);
    }

    public static void markDone(Context context, String key) {
        prefs(context).edit().putBoolean(key, true).apply();
    }

    public static void resetAll(Context context) {
        SharedPreferences.Editor editor = prefs(context).edit();
        for (String k : new String[]{
                KEY_HOME, KEY_SETTINGS, KEY_QURAN, KEY_KHATM, KEY_LISTEN,
                "coach_tour_home_v1", "coach_tour_settings_v1",
                "coach_tour_quran_v1", "coach_tour_khatm_v1", "coach_tour_listen_v1"
        }) {
            editor.remove(k);
        }
        editor.apply();
    }

    public static void replayFromSettings(Activity activity) {
        resetAll(activity);
        prefs(activity).edit().putBoolean(KEY_REPLAY_PENDING, true).apply();
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void consumeReplayIfNeeded(Activity activity, Runnable showHome) {
        SharedPreferences p = prefs(activity);
        if (p.getBoolean(KEY_REPLAY_PENDING, false)) {
            p.edit().remove(KEY_REPLAY_PENDING).apply();
            if (showHome != null) {
                showHome.run();
            }
        }
    }

    @Nullable
    public static View recyclerItem(RecyclerView recyclerView, int position) {
        if (recyclerView == null) return null;
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
        return holder != null ? holder.itemView : null;
    }

    public static void maybeShow(Activity activity, String storageKey, List<Step> steps, boolean force) {
        if (activity == null || activity.isFinishing()) return;
        if (!force && isDone(activity, storageKey)) return;

        List<Step> usable = new ArrayList<>();
        for (Step step : steps) {
            if (step != null && step.target != null
                    && step.target.getVisibility() != View.GONE) {
                usable.add(step);
            }
        }
        if (usable.isEmpty()) return;

        dismissActive();
        OverlayHost host = new OverlayHost(activity, storageKey, usable);
        activeHost = host;
        host.attach();
    }

    public static void dismissActive() {
        if (activeHost != null) {
            activeHost.detach(false);
            activeHost = null;
        }
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static final class OverlayHost {
        private final Activity activity;
        private final String storageKey;
        private final List<Step> steps;
        private final FrameLayout root;
        private final SpotlightView spotlight;
        private final TextView titleView;
        private final TextView bodyView;
        private final TextView nextBtn;
        private int index;

        OverlayHost(Activity activity, String storageKey, List<Step> steps) {
            this.activity = activity;
            this.storageKey = storageKey;
            this.steps = steps;

            root = new FrameLayout(activity);
            root.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            root.setClickable(true);
            root.setFocusable(true);

            spotlight = new SpotlightView(activity);
            root.addView(spotlight, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            View tip = LayoutInflater.from(activity).inflate(R.layout.coach_tip_card, root, false);
            titleView = tip.findViewById(R.id.coachTitle);
            bodyView = tip.findViewById(R.id.coachBody);
            TextView skipBtn = tip.findViewById(R.id.coachSkip);
            nextBtn = tip.findViewById(R.id.coachNext);
            FrameLayout.LayoutParams tipLp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tipLp.gravity = android.view.Gravity.BOTTOM;
            root.addView(tip, tipLp);

            skipBtn.setOnClickListener(v -> finish(true));
            nextBtn.setOnClickListener(v -> goNext());
            spotlight.setOnClickListener(v -> goNext());
        }

        void attach() {
            ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
            decor.addView(root);
            showStep(0);
        }

        void detach(boolean mark) {
            if (mark) {
                markDone(activity, storageKey);
            }
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null) {
                parent.removeView(root);
            }
            if (activeHost == this) {
                activeHost = null;
            }
        }

        private void goNext() {
            if (index >= steps.size() - 1) {
                finish(true);
            } else {
                showStep(index + 1);
            }
        }

        private void finish(boolean mark) {
            detach(mark);
        }

        private void showStep(int i) {
            index = i;
            Step step = steps.get(i);
            titleView.setText(step.title);
            bodyView.setText(step.body);
            nextBtn.setText(i >= steps.size() - 1 ? "تەواو" : "دواتر");

            ensureVisible(step.target);
            step.target.postDelayed(() -> {
                if (activeHost != this) return;
                spotlight.setTarget(step.target);
            }, 80);
        }

        private void ensureVisible(@NonNull View target) {
            target.requestRectangleOnScreen(new android.graphics.Rect(
                    0, 0, target.getWidth(), target.getHeight()), false);
        }
    }

    private static final class SpotlightView extends View {
        private final Paint dimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF hole = new RectF();
        private final float radius;

        SpotlightView(Context context) {
            super(context);
            setLayerType(LAYER_TYPE_HARDWARE, null);
            dimPaint.setColor(0xC72C2420);
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            radius = dp(14);
        }

        void setTarget(@Nullable View target) {
            if (target == null) {
                hole.setEmpty();
                invalidate();
                return;
            }
            int[] loc = new int[2];
            target.getLocationInWindow(loc);
            int[] self = new int[2];
            getLocationInWindow(self);
            float pad = dp(10);
            hole.set(
                    loc[0] - self[0] - pad,
                    loc[1] - self[1] - pad,
                    loc[0] - self[0] + target.getWidth() + pad,
                    loc[1] - self[1] + target.getHeight() + pad
            );
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
            canvas.drawRect(0, 0, getWidth(), getHeight(), dimPaint);
            if (!hole.isEmpty()) {
                canvas.drawRoundRect(hole, radius, radius, clearPaint);
            }
            canvas.restoreToCount(sc);
        }

        private float dp(float v) {
            return v * getResources().getDisplayMetrics().density;
        }
    }
}
