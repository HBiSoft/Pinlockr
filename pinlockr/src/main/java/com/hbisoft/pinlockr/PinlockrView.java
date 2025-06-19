package com.hbisoft.pinlockr;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


public class PinlockrView extends RecyclerView {
    private static final int DEFAULT_PIN_LENGTH = 4;
    private static final int[] DEFAULT_KEY_SET = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
    private String mPin = "";
    private int mPinLength;
    private int mHorizontalSpacing, mVerticalSpacing;
    private int mTextColor, mDeleteButtonPressedColor;
    private int mTextSize, mButtonSize, mDeleteButtonSize;
    private Drawable mButtonBackgroundDrawable;
    private Drawable mDeleteButtonDrawable;
    private boolean mShowDeleteButton;
    private IndicatorDots mIndicatorDots;
    private PinlockrAdapter mAdapter;
    private PinlockrListener mPinlockrListener;
    private CustomizationOptionsBundle mCustomizationOptionsBundle;
    private int[] mCustomKeySet;
    private Activity mActivity;
    private String mBiometricTitle = "Biometric Login";
    private String mBiometricSubTitle = "Log in using your fingerprint";
    private String mBiometricButtonText = "Cancel";

    private PinlockrAdapter.OnNumberClickListener mOnNumberClickListener
            = new PinlockrAdapter.OnNumberClickListener() {
        @Override
        public void onNumberClicked(int keyValue) {
            if (mPin.length() < getPinLength()) {
                mPin = mPin.concat(String.valueOf(keyValue));

                if (isIndicatorDotsAttached()) {
                    mIndicatorDots.updateDot(mPin.length());
                }

                if (mPin.length() == 1) {
                    mAdapter.setPinLength(mPin.length());
                    mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                }

                if (mPinlockrListener != null) {
                    if (mPin.length() == mPinLength) {
                        mPinlockrListener.onComplete(mPin);
                    }
                }
            } else {
                if (!isShowDeleteButton()) {
                    resetPinlockrView();
                    mPin = mPin.concat(String.valueOf(keyValue));

                    if (isIndicatorDotsAttached()) {
                        mIndicatorDots.updateDot(mPin.length());
                    }

                } else {
                    if (mPinlockrListener != null) {
                        mPinlockrListener.onComplete(mPin);
                    }
                }
            }
        }
    };

    private PinlockrAdapter.OnDeleteClickListener mOnDeleteClickListener = new PinlockrAdapter.OnDeleteClickListener() {
        @Override
        public void onDeleteClicked() {
            if (mPin.length() > 0) {
                mPin = mPin.substring(0, mPin.length() - 1);
                if (isIndicatorDotsAttached()) {
                    mIndicatorDots.updateDot(mPin.length());
                }
                if (mPin.length() == 0) {
                    mAdapter.setPinLength(mPin.length());
                    mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
                }
            }
        }
    };


    private PinlockrAdapter.OnBiometricClickListener mOnBiometricClickListener = new PinlockrAdapter.OnBiometricClickListener() {
        @Override
        public void onBiometricClicked() {
            if (isBiometricAvailable()) {
                showBiometricPrompt();
            }else{
                mPinlockrListener.onBiometricFailed(3);
            }
        }
    };

    // Check if the device has a biometric sensor
    private boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(getContext());
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }


    public PinlockrView(Context context) {
        super(context);
        init(null);
    }

    public PinlockrView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.PinlockrView);
        try {
            mPinLength = typedArray.getInt(R.styleable.PinlockrView_pinLength, DEFAULT_PIN_LENGTH);
            mHorizontalSpacing = (int) typedArray.getDimension(R.styleable.PinlockrView_keypadHorizontalSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_horizontal_spacing));
            mVerticalSpacing = (int) typedArray.getDimension(R.styleable.PinlockrView_keypadVerticalSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_vertical_spacing));
            mTextColor = typedArray.getColor(R.styleable.PinlockrView_keypadTextColor, ResourceUtils.getColor(getContext(), R.color.white));
            mTextSize = (int) typedArray.getDimension(R.styleable.PinlockrView_keypadTextSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_text_size));
            mButtonSize = (int) typedArray.getDimension(R.styleable.PinlockrView_keypadButtonSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_button_size));
            mDeleteButtonSize = (int) typedArray.getDimension(R.styleable.PinlockrView_keypadDeleteButtonSize, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_delete_button_size));
            mButtonBackgroundDrawable = typedArray.getDrawable(R.styleable.PinlockrView_keypadButtonBackgroundDrawable);
            mDeleteButtonDrawable = typedArray.getDrawable(R.styleable.PinlockrView_keypadDeleteButtonDrawable);
            mShowDeleteButton = typedArray.getBoolean(R.styleable.PinlockrView_keypadShowDeleteButton, true);
            mDeleteButtonPressedColor = typedArray.getColor(R.styleable.PinlockrView_keypadDeleteButtonPressedColor, ResourceUtils.getColor(getContext(), R.color.greyish));
        } finally {
            typedArray.recycle();
        }

        mCustomizationOptionsBundle = new CustomizationOptionsBundle();
        mCustomizationOptionsBundle.setTextColor(mTextColor);
        mCustomizationOptionsBundle.setTextSize(mTextSize);
        mCustomizationOptionsBundle.setButtonSize(mButtonSize);
        mCustomizationOptionsBundle.setButtonBackgroundDrawable(mButtonBackgroundDrawable);
        mCustomizationOptionsBundle.setDeleteButtonDrawable(mDeleteButtonDrawable);
        mCustomizationOptionsBundle.setDeleteButtonSize(mDeleteButtonSize);
        mCustomizationOptionsBundle.setShowDeleteButton(mShowDeleteButton);
        mCustomizationOptionsBundle.setDeleteButtonPressesColor(mDeleteButtonPressedColor);

        initView();
    }

    private void initView() {
        setLayoutManager(new LTRGridLayoutManager(getContext(), 3));

        mAdapter = new PinlockrAdapter(getContext());
        mAdapter.setOnItemClickListener(mOnNumberClickListener);
        mAdapter.setOnDeleteClickListener(mOnDeleteClickListener);
        mAdapter.setOnBiometricClickListener(mOnBiometricClickListener);
        mAdapter.setCustomizationOptions(mCustomizationOptionsBundle);
        setAdapter(mAdapter);

        addItemDecoration(new ItemSpaceDecoration(mHorizontalSpacing, mVerticalSpacing, 3, false));
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private void showBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(getContext());
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(mBiometricTitle)
                    .setSubtitle(mBiometricSubTitle)
                    .setNegativeButtonText(mBiometricButtonText)
                    .build();


            BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) mActivity,
                    ContextCompat.getMainExecutor(getContext()),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(
                                @NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            mPinlockrListener.onBiometricSuccess();
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            /*Toast.makeText(BiometricActivity.this, errString, Toast.LENGTH_SHORT).show();
                            if (!errString.equals("Cancel")) {
                                finish();
                            }*/
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            /*Toast.makeText(BiometricActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();*/
                        }
                    });


            biometricPrompt.authenticate(promptInfo);
        } else {
            /*Toast.makeText(this, "Biometric not available", Toast.LENGTH_SHORT).show();
            finish();*/
        }
    }

    public void disableBiometricLogin(boolean shouldDisable){
        mAdapter.disableBiometric(shouldDisable);
    }

    public void customizeBiometricDialog(String titleText, String subTitleText, String buttonTitle){
        mBiometricTitle = titleText;
        mBiometricSubTitle = subTitleText;
        mBiometricButtonText = buttonTitle;
    }

    public int getPinLength() {
        return mPinLength;
    }

    public void setupPinlockrView(Activity activity, PinlockrListener pinlockrListener, IndicatorDots indicatorDots, int pinLength){
        mActivity = activity;
        this.mPinlockrListener = pinlockrListener;
        this.mIndicatorDots = indicatorDots;
        this.mPinLength = pinLength;

        if (isIndicatorDotsAttached()) {
            mIndicatorDots.setPinLength(pinLength);
        }

    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        mCustomizationOptionsBundle.setTextColor(textColor);
        mAdapter.notifyDataSetChanged();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mCustomizationOptionsBundle.setTextSize(textSize);
        mAdapter.notifyDataSetChanged();
    }

    public int getButtonSize() {
        return mButtonSize;
    }

    public void setButtonSize(int buttonSize) {
        this.mButtonSize = buttonSize;
        mCustomizationOptionsBundle.setButtonSize(buttonSize);
        mAdapter.notifyDataSetChanged();
    }

    public Drawable getButtonBackgroundDrawable() {
        return mButtonBackgroundDrawable;
    }

    public void setButtonBackgroundDrawable(Drawable buttonBackgroundDrawable) {
        this.mButtonBackgroundDrawable = buttonBackgroundDrawable;
        mCustomizationOptionsBundle.setButtonBackgroundDrawable(buttonBackgroundDrawable);
        mAdapter.notifyDataSetChanged();
    }

    public Drawable getDeleteButtonDrawable() {
        return mDeleteButtonDrawable;
    }

    public void setDeleteButtonDrawable(Drawable deleteBackgroundDrawable) {
        this.mDeleteButtonDrawable = deleteBackgroundDrawable;
        mCustomizationOptionsBundle.setDeleteButtonDrawable(deleteBackgroundDrawable);
        mAdapter.notifyDataSetChanged();
    }

    public int getDeleteButtonSize() {
        return mDeleteButtonSize;
    }

    public void setDeleteButtonSize(int deleteButtonSize) {
        this.mDeleteButtonSize = deleteButtonSize;
        mCustomizationOptionsBundle.setDeleteButtonSize(deleteButtonSize);
        mAdapter.notifyDataSetChanged();
    }

    public boolean isShowDeleteButton() {
        return mShowDeleteButton;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.mShowDeleteButton = showDeleteButton;
        mCustomizationOptionsBundle.setShowDeleteButton(showDeleteButton);
        mAdapter.notifyDataSetChanged();
    }

    public int getDeleteButtonPressedColor() {
        return mDeleteButtonPressedColor;
    }

    public void setDeleteButtonPressedColor(int deleteButtonPressedColor) {
        this.mDeleteButtonPressedColor = deleteButtonPressedColor;
        mCustomizationOptionsBundle.setDeleteButtonPressesColor(deleteButtonPressedColor);
        mAdapter.notifyDataSetChanged();
    }

    public void resetPinlockrView() {
        mPin = "";
        mAdapter.setPinLength(mPin.length());
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);

        if (mIndicatorDots != null) {
            mIndicatorDots.updateDot(mPin.length());
        }
    }

    public boolean isIndicatorDotsAttached() {
        return mIndicatorDots != null;
    }

}
