package andreew.vlak;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        vlak = (Vlak) findViewById(R.id.map);
        starttime=System.currentTimeMillis();
        CountDownTimer countDownTimer = new CountDownTimer(200000, 100) {
            public void onTick(long millisUntilFinished) {
                setText();
            }
            public void onFinish() {
            }
        }.start();


        vlak.invalidate();
    }
}
