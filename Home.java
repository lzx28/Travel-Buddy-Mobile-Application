package translateapp.jason.com;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.ads.InterstitialAd;

import android.util.Log;
import android.widget.Toast;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class Home extends AppCompatActivity {
    //Initialize variable
    private static final String TAG = Home.class.getSimpleName();
    HuaweiIdAuthParams authParams;
    AccountAuthService service;
    MainActivity main;
    DrawerLayout drawerLayout;
    RelativeLayout relativeLayoutImage, relativeLayoutText, relativeLayoutVoice;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Assign variable
        drawerLayout = findViewById(R.id.drawer_layout);
        relativeLayoutImage = findViewById(R.id.relativeLayoutImage);
        relativeLayoutText = findViewById(R.id.relativeLayoutText);
        relativeLayoutVoice = findViewById(R.id.relativeLayoutVoice);

        relativeLayoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRelativeLayoutImage();
            }
        });

        relativeLayoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRelativeLayoutText();
            }
        });

        relativeLayoutVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRelativeLayoutVoice();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadInterstitialAd();
            }
        },50);

    }

    public void clickRelativeLayoutImage(){
        Intent intent = new Intent(Home.this, ImageTranslation.class);
        startActivity(intent);
    }

    public void clickRelativeLayoutText(){
        Intent intent = new Intent(Home.this, RealTimeLanguageTranslation.class);
        startActivity(intent);
    }

    public void clickRelativeLayoutVoice(){
        Intent intent = new Intent(Home.this, VoiceTranslation.class);
        startActivity(intent);
    }

    public void ClickMenu(View view){
        //Open Drawer
        NavigationBar.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        //Close drawer
        NavigationBar.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        //Redirect activity to home
        NavigationBar.redirectActivity(this, Home.class);
    }

    public void ClickDashboard(View view){
        //Redirect activity to dashboard
        NavigationBar.redirectActivity(this, History.class);
    }


    public void ClickLogout(View view){
        //Close app
        //finishAffinity();
        //System.exit(0);
        //NavigationBar.logout(this);
        confirmDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        NavigationBar.closeDrawer(drawerLayout);
    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            //Toast.makeText(Home.this, "Ad loaded", Toast.LENGTH_SHORT).show();
            // Display an interstitial ad.
            showInterstitial();
        }

        @Override
        public void onAdFailed(int errorCode) {
            Toast.makeText(Home.this, "Ad load failed with error code: " + errorCode,
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Ad load failed with error code: " + errorCode);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "onAdClicked");
            super.onAdClicked();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "onAdOpened");
            super.onAdOpened();
        }
    };

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdId(getAdId());
        interstitialAd.setAdListener(adListener);

        AdParam adParam = new AdParam.Builder().build();
        interstitialAd.loadAd(adParam);
    }

    private String getAdId() {

        return "testb4znbuh3n2";

    }

    private void showInterstitial() {
        // Display an interstitial ad.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }



    public void confirmDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wish to logout")
                .setTitle("Logout")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //finishAffinity();
                        //System.exit(0);
                        MainActivity.getmInstanceActivity().signOut();
                        MainActivity.getmInstanceActivity().cancelAuthorization();
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}