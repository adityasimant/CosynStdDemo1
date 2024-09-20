package com.metapointer.handwritingtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private TextView pageNumberTextView;

    private PageIndicatorView pageIndicatorView;
    RecyclerView recyclerView;
    NumberAdapter numberAdapter;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);

        drawingView = findViewById(R.id.drawing_view);
//        pageIndicatorView = findViewById(R.id.page_indicator);

        MaterialCardView clearButton = findViewById(R.id.clear_button);
        Button saveButton = findViewById(R.id.save_button);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Create list of numbers from 1 to 30
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= DrawingView.TOTAL_PAGES; i++) {
            numbers.add(i);
        }

        // Set adapter
        numberAdapter = new NumberAdapter(numbers);
        recyclerView.setAdapter(numberAdapter);

        clearButton.setOnClickListener(v -> drawingView.clearCanvas());
        saveButton.setOnClickListener(v -> drawingView.saveDrawing(MainActivity.this));

//        pageIndicatorView.setTotalPages(DrawingView.TOTAL_PAGES);
//        pageIndicatorView.setOnPageChangeListener(newPage -> {
//            drawingView.setCurrentPage(newPage - 1);  // DrawingView uses 0-based index
//            updatePageNumber();
//        });

//        updatePageNumber();

        // Restore drawing if the app was restarted
        drawingView.restoreDrawing(this);
        verifyStoragePermissions(this);
    }
//
//    private void updatePageNumber() {
//        int currentPage = drawingView.getCurrentPage() + 1;  // Convert to 1-based index
//        pageIndicatorView.setCurrentPage(currentPage);
//    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}