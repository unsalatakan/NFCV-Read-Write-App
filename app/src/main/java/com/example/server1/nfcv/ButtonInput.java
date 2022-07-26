package com.example.server1.nfcv;

import android.app.Application;

public class ButtonInput extends Application {

    private String input = "";

    public String getButtonInput() {
        return input;
    }

    public void setButtonInput(String someVariable) {
        this.input = someVariable;
    }
}
