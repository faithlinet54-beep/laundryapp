package com.example.laundryapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class to handle touch gestures on a RecyclerView.
 */
public class GestureHandler {

    public interface OnGestureListener {
        void onSingleTap(int position);
        void onDoubleTap(int position);
        void onLongPress(int position);
        void onSwipeLeft(int position);
        void onSwipeRight(int position);
    }

    public static void attachToRecyclerView(Context context, final RecyclerView recyclerView, final OnGestureListener listener) {
        if (recyclerView == null || context == null || listener == null) return;

        final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int pos = recyclerView.getChildAdapterPosition(child);
                    EventLogger.logEvent("Gesture: Single Tap at position " + pos);
                    listener.onSingleTap(pos);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int pos = recyclerView.getChildAdapterPosition(child);
                    EventLogger.logEvent("Gesture: Double Tap at position " + pos);
                    listener.onDoubleTap(pos);
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int pos = recyclerView.getChildAdapterPosition(child);
                    EventLogger.logEvent("Gesture: Long Press at position " + pos);
                    listener.onLongPress(pos);
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        // Swipe Handling using ItemTouchHelper
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    EventLogger.logEvent("Gesture: Swipe Left at position " + position);
                    listener.onSwipeLeft(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    EventLogger.logEvent("Gesture: Swipe Right at position " + position);
                    listener.onSwipeRight(position);
                }
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }
}
