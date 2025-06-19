package com.hbisoft.pinlockr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.hbisoft.pinlockr.customfonts.MediumFont;


public class MainActivity extends AppCompatActivity {
    PinlockrView pinLockView;
    IndicatorDots mIndicatorDots;
    MediumFont text_info;
    private static final String PREFS_NAME = "HideITPrefs";
    private static final String KEY_DARK_MODE = "isDarkMode";
    boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle dark mode
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupPinlockView();
    }

    private void initViews() {
        text_info = findViewById(R.id.text_info);
        pinLockView = findViewById(R.id.pin_lock_view);
        mIndicatorDots = findViewById(R.id.indicator_dots);
    }

    private void setupPinlockView() {
        // Change look if dark mode is enabled
        if (isDarkMode){
            mIndicatorDots.setEmptyDotColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
            mIndicatorDots.setFillDotColor(getResources().getColor(android.R.color.white, getTheme()));
            pinLockView.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
            text_info.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        }

        // 1.) Activity - Activity is only used for BiometricManager
        // 2.) PinLockListener - To handle the callbacks
        // 3.) boolean - If this is set to "true", the pin will be saved in SharedPreferences
        //     If you don't set this, or set it to false - You have to handle the storing of the pin yourself
        // 4.) IndicatorDots - If you want to use the IndicatorDots, you have to pass it here, otherwise you have to pass null
        //     You have to setup IndicatorDots before passing it below.
        // 5.) int - Pin length
        pinLockView.setupPinlockrView(this, mPinLockListener, mIndicatorDots, 5);
        pinLockView.disableBiometricLogin(true);

    }

    private enum PinState { SET_PIN, CONFIRM_PIN, LOGIN }
    private PinState currentState = PinState.SET_PIN;
    private int initialPin = 0;
    private final PinlockrListener mPinLockListener = new PinlockrListener() {
        @Override
        public void onComplete(String pin) {
            // The following is a simple example of how the library works
            // Your logic will most probably look different
            // If your application handles sensitive information, its recommended to encrypt the pin and store it in a secure database
            int enteredPin = Integer.parseInt(pin);
            switch (currentState) {
                case SET_PIN:
                    initialPin = enteredPin;
                    text_info.setText("Confirm PIN");
                    currentState = PinState.CONFIRM_PIN;
                    pinLockView.resetPinlockrView(); // Clear for confirmation
                    toastResponse("Please confirm your PIN");
                    break;

                case CONFIRM_PIN:
                    if (enteredPin == initialPin) {
                        text_info.setText("LOGIN");
                        currentState = PinState.LOGIN;
                        savePinInPrefs(enteredPin); // Save after confirmation
                        toastResponse("PIN Confirmed - You can now log in");
                        pinLockView.resetPinlockrView(); // Clear for login
                        pinLockView.disableBiometricLogin(false); // Pin was saved, we can now enable biometric login (if available)
                    } else {
                        text_info.setText("Enter PIN");
                        currentState = PinState.SET_PIN;
                        initialPin = 0; // Reset initial PIN
                        pinLockView.resetPinlockrView(); // Clear and retry
                        toastResponse("PINs do not match, please try again");
                    }
                    break;

                case LOGIN:
                    if (getUserPin() == enteredPin) {
                        text_info.setText("Welcome!");
                        toastResponse("Successfully logged in");
                        pinLockView.setVisibility(View.GONE); // Hide after login
                        mIndicatorDots.setVisibility(View.GONE);
                    } else {
                        text_info.setText("Incorrect PIN");
                        pinLockView.resetPinlockrView(); // Clear for retry
                        toastResponse("Incorrect PIN, please try again");
                    }
                    break;
            }
        }

        @Override
        public void onBiometricSuccess() {
            text_info.setText("Welcome!");
            pinLockView.setVisibility(View.GONE); // Hide after login
            mIndicatorDots.setVisibility(View.GONE);
            toastResponse("Biometric Success");
        }

        @Override
        public void onBiometricFailed(int reason) {
            if (reason == 1) {
                toastResponse("Biometric Failed");
            } else if (reason == 2) {
                toastResponse("Biometric Cancelled");
            } else {
                toastResponse("No Biometric Detected");
            }
        }
    };

    private void toastResponse(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void savePinInPrefs(int pin){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("UserPin", pin);
        editor.apply();
    }

    private int getUserPin(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt("UserPin", 0);
    }

}

