package com.metapointer.handwritingtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private TextView pageNumberTextView;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawing_view);
        pageNumberTextView = findViewById(R.id.page_number_text);

        MaterialCardView clearButton = findViewById(R.id.clear_button);
        Button saveButton = findViewById(R.id.save_button);
        ImageView prevPageButton = findViewById(R.id.prev_page_button);
        ImageView nextPageButton = findViewById(R.id.next_page_button);

        clearButton.setOnClickListener(v -> drawingView.clearCanvas());
        saveButton.setOnClickListener(v -> drawingView.saveDrawing(MainActivity.this));

        prevPageButton.setOnClickListener(v -> {
            drawingView.previousPage();
            updatePageNumber();
        });

        nextPageButton.setOnClickListener(v -> {
            drawingView.nextPage();
            updatePageNumber();
        });

        updatePageNumber();

        // Restore drawing if the app was restarted
        drawingView.restoreDrawing(this);
        verifyStoragePermissions(this);
    }

    private void updatePageNumber() {
        pageNumberTextView.setText("Page " + drawingView.getCurrentPage());
    }

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