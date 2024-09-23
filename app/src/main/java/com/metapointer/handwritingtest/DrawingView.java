package com.metapointer.handwritingtest;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DrawingView extends View {

    private List<List<Path>> paths;
    private List<Path> currentPagePaths;
    private Path currentPath;
    private Paint paint = new Paint();
    private Paint linePaint = new Paint();
    private List<List<List<float[]>>> allPathPoints;
    private List<List<float[]>> currentPagePathPoints;
    private List<float[]> currentPathPoints;

    private float lineSpacing = 50f;
    private float marginLeft = 100f;

    private int currentPage = 0;

    // initialise the total pages from config api
    public static final int TOTAL_PAGES = 30; //placeHolder Value

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initPages();
    }

    private void initPaint() {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setAntiAlias(true);

        linePaint.setColor(Color.LTGRAY);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1f);
    }

    private void initPages() {
        paths = new ArrayList<>(TOTAL_PAGES);
        allPathPoints = new ArrayList<>(TOTAL_PAGES);
        for (int i = 0; i < TOTAL_PAGES; i++) {
            paths.add(new ArrayList<>());
            allPathPoints.add(new ArrayList<>());
        }
        currentPagePaths = paths.get(currentPage);
        currentPagePathPoints = allPathPoints.get(currentPage);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw ruling lines
        float y = lineSpacing;
        while (y < getHeight()) {
            canvas.drawLine(0, y, getWidth(), y, linePaint);
            y += lineSpacing;
        }

        // Draw margin line
        canvas.drawLine(marginLeft, 0, marginLeft, getHeight(), linePaint);

        // Draw user paths for the current page
        for (Path path : currentPagePaths) {
            canvas.drawPath(path, paint);
        }
        if (currentPath != null) {
            canvas.drawPath(currentPath, paint);
        }
    }

    // will trigger when the user starts interating with page
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                currentPathPoints = new ArrayList<>();
                currentPathPoints.add(new float[]{x, y});
                return true;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                currentPathPoints.add(new float[]{x, y});
                break;
            case MotionEvent.ACTION_UP:
                currentPagePaths.add(currentPath);
                currentPagePathPoints.add(currentPathPoints);
                currentPath = null;
                currentPathPoints = null;
                break;
        }
        invalidate();
        return true;
    }

    public void clearCanvas() {
        currentPagePaths.clear();
        currentPagePathPoints.clear();
        currentPath = null;
        currentPathPoints = null;
        invalidate();
    }

    public void nextPage() {
        if (currentPage < TOTAL_PAGES - 1) {
            currentPage++;
//            currentPage = TOTAL_PAGES-1;
            currentPagePaths = paths.get(currentPage);
            currentPagePathPoints = allPathPoints.get(currentPage);
            invalidate();
        }
    }
    public void setCurrentPage(int page) {
        if (page >= 0 && page < TOTAL_PAGES) {
            currentPage = page;
            currentPagePaths = paths.get(currentPage);
            currentPagePathPoints = allPathPoints.get(currentPage);
            invalidate();
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void previousPage() {
        if (currentPage > 0) {
//            currentPage= 0;
            currentPage--;
            currentPagePaths = paths.get(currentPage);
            currentPagePathPoints = allPathPoints.get(currentPage);
            invalidate();
        }
    }

//    public int getCurrentPage() {
//        return currentPage + 1;
//    }

    public void saveDrawing(Context context) {
        if (!isExternalStorageWritable()) {
            Toast.makeText(context, "External storage is not writable", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(context.getExternalFilesDir(null), "public_drawing.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            StringBuilder builder = new StringBuilder();
            for (int page = 0; page < TOTAL_PAGES; page++) {
                List<List<float[]>> pagePathPoints = allPathPoints.get(page);
                for (List<float[]> pathPoints : pagePathPoints) {
                    for (float[] point : pathPoints) {
                        builder.append(point[0]).append(",").append(point[1]).append(";");
                    }
                    builder.append("|");
                }
                builder.append("||");
            }
            // Need to write the compression process here
            fos.write(builder.toString().getBytes());
            Toast.makeText(context, "Drawing saved to public location!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving drawing", Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreDrawing(Context context) {
        if (!isExternalStorageReadable()) {
            Toast.makeText(context, "External storage is not readable", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(context.getExternalFilesDir(null), "public_drawing.txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            // rewrite this logic for a compressed file scenario
            String line = br.readLine();
            if (line != null) {
                String[] pages = line.split("\\|\\|");
                for (int page = 0; page < Math.min(pages.length, TOTAL_PAGES); page++) {
                    String[] strokes = pages[page].split("\\|");
                    List<Path> pagePaths = paths.get(page);
                    List<List<float[]>> pagePathPoints = allPathPoints.get(page);
                    pagePaths.clear();
                    pagePathPoints.clear();

                    for (String stroke : strokes) {
                        String[] points = stroke.split(";");
                        Path path = new Path();
                        List<float[]> pathPoints = new ArrayList<>();
                        boolean isFirstPoint = true;

                        for (String point : points) {
                            String[] coordinates = point.split(",");
                            if (coordinates.length == 2) {
                                float x = Float.parseFloat(coordinates[0]);
                                float y = Float.parseFloat(coordinates[1]);
                                if (isFirstPoint) {
                                    path.moveTo(x, y);
                                    isFirstPoint = false;
                                } else {
                                    path.lineTo(x, y);
                                }
                                pathPoints.add(new float[]{x, y});
                            }
                        }

                        if (!pathPoints.isEmpty()) {
                            pagePaths.add(path);
                            pagePathPoints.add(pathPoints);
                        }
                    }
                }
            }
            currentPagePaths = paths.get(currentPage);
            currentPagePathPoints = allPathPoints.get(currentPage);
            invalidate();
            Toast.makeText(context, "Drawing restored from public location!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error restoring drawing", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}

