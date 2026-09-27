package com.dya.noor.adapters;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R; // Make sure this import is correct
import com.dya.noor.customImageView.ZoomableImageView;

import java.io.File;
import java.io.IOException;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfPageViewHolder> {

    private PdfRenderer pdfRenderer;
    private final Context context;
    private final int screenWidth;

    public PdfAdapter(Context context, File pdfFile) {
        this.context = context;

        // Get the screen width to render high-quality bitmaps
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = displayMetrics.widthPixels;

        try {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            this.pdfRenderer = new PdfRenderer(fileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public PdfPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We will create our custom ZoomableImageView in the next step
        // For now, this will assume you have a layout file with it.
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_pdf_page, parent, false);
        return new PdfPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfPageViewHolder holder, int position) {
        if (pdfRenderer == null) {
            return;
        }

        PdfRenderer.Page page = pdfRenderer.openPage(position);

        // --- HIGH QUALITY RENDERING ---
        // Calculate the target bitmap size to match screen width while maintaining aspect ratio
        float aspectRatio = (float) page.getHeight() / (float) page.getWidth();
        int targetHeight = (int) (screenWidth * aspectRatio);

        // Create a high-quality bitmap
        Bitmap bitmap = Bitmap.createBitmap(screenWidth, targetHeight, Bitmap.Config.ARGB_8888);

        // Fill the bitmap's background with white
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);

        // Render the page onto the high-quality bitmap
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        // Set the final bitmap to our custom view
        holder.pageImageView.setImageBitmap(bitmap);
        page.close();
    }

    @Override
    public int getItemCount() {
        return (pdfRenderer != null) ? pdfRenderer.getPageCount() : 0;
    }

    public void close() {
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }

    // We will change this to ZoomableImageView in the next step
    public static class PdfPageViewHolder extends RecyclerView.ViewHolder {
        ZoomableImageView pageImageView; // Changed from ImageView

        public PdfPageViewHolder(@NonNull View itemView) {
            super(itemView);
            pageImageView = itemView.findViewById(R.id.pageImageView);
        }
    }
}