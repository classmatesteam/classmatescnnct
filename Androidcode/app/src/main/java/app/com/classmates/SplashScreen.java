package app.com.classmates;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import app.com.classmates.multipleclasses.GlobalConstants;
import app.com.classmates.services.RegistrationIntentService;

public class SplashScreen extends AppCompatActivity {

    ImageView cm_logo_IV;
    Animation animFadein, animslideup, animslidein;
    SharedPreferences spref;
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spref = getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);

        startService(new Intent(SplashScreen.this, RegistrationIntentService.class));

        ShowAnimation();

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupExitWindowAnimations();
                startActivity(new Intent(SplashScreen.this, LoginwithFB.class));
                finish();
            }
        }, 2500);

        global = (Global) getApplicationContext();

    }

    private void setupExitWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setExitTransition(slide);
        }
    }

    private void ShowAnimation() {
        cm_logo_IV = (ImageView) findViewById(R.id.cm_logo_IV);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        animslideup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        animslidein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        cm_logo_IV.startAnimation(animFadein);
    }


}
