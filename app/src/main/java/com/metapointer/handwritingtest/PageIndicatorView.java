package com.metapointer.handwritingtest;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PageIndicatorView extends HorizontalScrollView {
    private static final int PAGE_NUMBER_PADDING = 10;
    private int totalPages = 30;
    private int currentPage = 1;
    private LinearLayout container;
    private TextView prevButton, nextButton;
    private OnPageChangeListener pageChangeListener;
    private int availableWidth;
    private int pageNumberWidth;

    public PageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setHorizontalScrollBarEnabled(false);
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        addView(container);

        prevButton = createButton(context, "<");
        nextButton = createButton(context, ">");

        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                setCurrentPage(currentPage - 1);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                setCurrentPage(currentPage + 1);
            }
        });

        // Estimate page number width
        TextView tempView = new TextView(context);
        tempView.setTextSize(16);
        tempView.setPadding(PAGE_NUMBER_PADDING, 5, PAGE_NUMBER_PADDING, 5);
        tempView.setText("00");
        tempView.measure(0, 0);
        pageNumberWidth = tempView.getMeasuredWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        availableWidth = w - prevButton.getMeasuredWidth() - nextButton.getMeasuredWidth();
        updatePageNumbers();
    }

    private TextView createButton(Context context, String text) {
        TextView button = new TextView(context);
        button.setText(text);
        button.setTextSize(16);
        button.setPadding(20, 5, 20, 5);
        button.setTextColor(Color.BLUE);
        return button;
    }

    private void updatePageNumbers() {
        container.removeAllViews();
        container.addView(prevButton);

        int maxVisiblePages = availableWidth / pageNumberWidth;
        int halfVisible = maxVisiblePages / 2;

        addPageNumber(1);

        if (currentPage > halfVisible + 2) {
            addEllipsis();
        }

        int start = Math.max(2, currentPage - halfVisible);
        int end = Math.min(totalPages - 1, currentPage + halfVisible);

        for (int i = start; i <= end; i++) {
            addPageNumber(i);
        }

        if (currentPage < totalPages - halfVisible - 1) {
            addEllipsis();
        }

        if (totalPages > 1) {
            addPageNumber(totalPages);
        }

        container.addView(nextButton);

        post(() -> scrollToPage(currentPage));
    }

    private void addPageNumber(int pageNum) {
        TextView pageView = new TextView(getContext());
        pageView.setText(String.valueOf(pageNum));
        pageView.setPadding(PAGE_NUMBER_PADDING, 5, PAGE_NUMBER_PADDING, 5);
        pageView.setTextSize(16);
        pageView.setTextColor(pageNum == currentPage ? Color.BLUE : Color.BLACK);

        pageView.setOnClickListener(v -> setCurrentPage(pageNum));

        container.addView(pageView);
    }

    private void addEllipsis() {
        TextView ellipsis = new TextView(getContext());
        ellipsis.setText("...");
        ellipsis.setPadding(PAGE_NUMBER_PADDING, 5, PAGE_NUMBER_PADDING, 5);
        ellipsis.setTextSize(16);
        container.addView(ellipsis);
    }

    private void scrollToPage(int page) {
        int scrollX = 0;
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                if (textView.getText().toString().equals(String.valueOf(page))) {
                    break;
                }
                scrollX += child.getWidth();
            }
        }
        smoothScrollTo(scrollX - availableWidth / 2 + pageNumberWidth / 2, 0);
    }

    public void setCurrentPage(int page) {
        if (page >= 1 && page <= totalPages && page != currentPage) {
            currentPage = page;
            updatePageNumbers();
            if (pageChangeListener != null) {
                pageChangeListener.onPageChanged(currentPage);
            }
        }
    }

    public void setTotalPages(int total) {
        totalPages = total;
        updatePageNumbers();
    }

    public interface OnPageChangeListener {
        void onPageChanged(int newPage);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.pageChangeListener = listener;
    }
}