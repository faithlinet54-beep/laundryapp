package com.example.laundryapp;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class KeyboardHandler {

    public interface OnActionListener {
        void onAction();
    }

    public static void setOnActionDoneListener(EditText editText, final OnActionListener listener) {
        if (editText == null) return;
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    
                    EventLogger.logEvent("Keyboard Action triggered on: " + v.getResources().getResourceEntryName(v.getId()));
                    if (listener != null) {
                        listener.onAction();
                    }
                    return true;
                }
                return false;
            }
        });
    }
}
