**<p align="center">Creating and maintaining a library like this requires a significant amount of time and effort.</p>**

**<p align="center">If you’d like to show your appreciation, you can do so below:</p>**

<p align="center"><a href="https://www.buymeacoffee.com/HBiSoft" target="_blank" ><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 164px !important;box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" ></a></p>

---
</br>

# Pinlockr
[![](https://jitpack.io/v/HBiSoft/Pinlockr.svg)](https://jitpack.io/#HBiSoft/Pinlockr)


<p align="center">Lightweight screen lock Android library </br></br><b>Requires API level 24></b></p>

<p align="center"><img src="https://github.com/user-attachments/assets/39dfd598-1225-4289-b106-82440e259c95"></p>


</br>

<h2 align="center"><b>Demo:</b></h2>

<p align="center">Download the demo app  <a href="https://github.com/HBiSoft/HBRecorder/releases/download/0.0.5/PinlockrDemo.apk"><nobr>here</nobr></a></p>

<p align="center"><img src="https://github.com/user-attachments/assets/f78959a8-a641-4254-84ec-e39e156f262d" height="550" </p>
</br></br>

**Adding the library to your project:**
---
Add the following in your root build.gradle at the end of repositories:

```java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }	    
    }
}
```
    
Implement library in your app level build.gradle:

```java
dependencies {
    implementation 'com.github.HBiSoft:Pinlockr:0.0.5'
}
```
    

**Implementing the library:**
--- 
```java
public class MainActivity extends AppCompatActivity {
    //Declare PinlockrView
    PinlockrView pinLockrView;

    //Declare IndicatorDots (Optional - It's the dots that show the pin progress)
    IndicatorDots mIndicatorDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PinlockrView
        pinLockrView = findViewById(R.id.pinLockrView);

        // Initialize IndicatorDots
        mIndicatorDots = findViewById(R.id.mIndicatorDots);

        // Setup
        // 1.) Activity - Activity is only used for BiometricManager
        // 2.) PinLockListener - To handle the callbacks
        // 3.) boolean - If this is set to "true", the pin will be saved in SharedPreferences
        //     If you don't set this, or set it to false - You have to handle the storing of the pin yourself
        // 4.) IndicatorDots - If you want to use the IndicatorDots, you have to pass it here, otherwise you have to pass null
        //     You have to setup IndicatorDots before passing it below.
        // 5.) int - Pin length
        pinLockrView.setupPinlockrView(this, mPinLockListener, mIndicatorDots, 5);
    }

    // PinlockrListener
    private final PinlockrListener mPinLockListener = new PinlockrListener() {
        @Override
        public void onComplete(String pin) {
        // Pin will be returned here
        // See the demo application 
    });
    
}
```
***Add PinlockrView to layout***
---
```xml
<com.hbisoft.pinlockr.PinlockrView
    android:id="@+id/pin_lock_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_below="@id/indicator_dots"
    android:layout_centerHorizontal="true"
    android:layout_marginTop="16dp"/>

```
AND YOU ARE DONE!

---
Customisation:
---
This can be done in `xml` or in `java`.

If you set this in java and in xml, then java will be favoured

Java:
```java
// Disable biometric
pinLockView.disableBiometricLogin(boolean);
// Customize the biometric texts
pinLockView.customizeBiometricDialog(titleText, subTitleText, buttonTitle);
// Set the text color of the numbers
pinLockView.setTextColor(int);
// Set the text size of the numbers
pinLockView.setTextSize(int);
// Set the size of the buttons the numbers are in
pinLockView.setButtonSize(int);
// Change the button background drawable
pinLockView.setButtonBackgroundDrawable(Drawable);
// Change the delete button drawable
pinLockView.setDeleteButtonDrawable(Drawable);
// Change the delete button size
pinLockView.setDeleteButtonSize(int);
// Show/Hide delete button
pinLockView.setShowDeleteButton(boolean);
// Set delete button pressed color
pinLockView.setDeleteButtonPressedColor(int);


// OTHER PUBLIC METHODS:

// Resets the view - returns boolean
pinLockView.resetPinlockrView();
// Check if the indicator dots are attached - returns boolean
pinLockView.isIndicatorDotsAttached();

```
xml:
```xml

  app:pinLength="6"                                       // Change the pin length
  app:keypadTextColor="#E6E6E6"                           // Change the color of the keypad text
  app:keypadTextSize="16dp"                               // Change the text size in the keypad
  app:keypadButtonSize="72dp"                             // Change the size of individual keys/buttons
  app:keypadVerticalSpacing="24dp"                        // Alters the vertical spacing between the keypad buttons
  app:keypadHorizontalSpacing="36dp"                      // Alters the horizontal spacing between the keypad buttons
  app:keypadButtonBackgroundDrawable="@drawable/bg"       // Set a custom background drawable for the buttons
  app:keypadDeleteButtonDrawable="@drawable/ic_back"      // Set a custom drawable for the delete button
  app:keypadDeleteButtonSize="16dp"                       // Change the size of the delete button icon in the keypad
  app:keypadShowDeleteButton="false"                      // Should show the delete button, default is true
  app:keypadDeleteButtonPressedColor="#C8C8C8"            // Change the pressed/focused state color of the delete button
  
  app:dotEmptyBackground="@drawable/empty"                // Customize the empty state of the dots
  app:dotFilledBackground"@drawable/filled"               // Customize the filled state of the dots
  app:dotDiameter="12dp"                                  // Change the diameter of the dots
  app:dotSpacing="16dp"                                   // Change the spacing between individual dots
  app:indicatorType="fillWithAnimation"                   // Choose between "fixed", "fill" and "fillWithAnimation"

```

Credit:
---

This library was built upon [this](https://github.com/aritraroy/PinLockView) one, which is no longer maintained

