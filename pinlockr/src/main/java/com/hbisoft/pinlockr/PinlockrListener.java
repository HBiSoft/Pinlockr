package com.hbisoft.pinlockr;


public interface PinlockrListener {

    void onComplete(String pin);

    void onBiometricSuccess();

    void onBiometricFailed(int reason);

}
