package com.example.laundryapp;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardHandler {

    public static void setOnActionDoneListener(EditText editText, Runnable onDoneAction) {
        if (editText == null) return;
        
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(editText);
                if (onDoneAction != null) {
                    onDoneAction.run();
                }
                return true;
            }
            return false;
        });
    }

    public static void hideKeyboard(EditText editText) {
        if (editText == null) return;
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
