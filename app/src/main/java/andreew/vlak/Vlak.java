package andreew.vlak;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by opalo on 21.11.2017.
 */

public class Vlak extends View{
    Bitmap[] bmp;

    private int currentLevel = 0;
    int width;
    int height;
    int lx = 15;
    int ly = 10;
    int x = 1;
    int y = 0;
    int degree;
    int starttime=0;
    boolean start = true;
    boolean firstTouch = false;
    Point door;
    boolean stop=false;
    int pocetObjektu;

    List<pseudoVagon> vlakovaSouprava = new ArrayList<pseudoVagon>();

    private int[][] levels = {
            {
                    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
                    1,0,0,3,3,0,1,0,0,0,0,0,0,0,1,
                    1,0,1,3,0,3,0,0,0,0,0,0,0,0,1,
                    1,0,0,3,3,0,0,0,0,0,0,0,0,0,1,
                    1,0,1,3,0,3,0,0,0,0,0,0,0,0,1,
                    1,0,0,3,3,0,1,0,0,0,0,0,0,0,1,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
                    1,1,1,1,1,1,1,2,1,1,1,1,1,1,1
            },
            {
                    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
                    1,3,3,3,0,0,3,0,0,0,3,0,0,0,1,
                    1,0,3,0,0,0,3,3,0,3,3,0,0,0,1,
                    1,0,3,0,0,0,3,0,3,0,3,0,0,0,1,
                    1,0,3,0,5,0,3,0,0,0,3,5,5,5,1,
                    1,0,3,5,0,5,3,0,0,0,3,0,0,5,1,
                    1,0,0,5,5,5,3,0,0,0,3,0,5,0,1,
                    1,0,0,5,0,5,0,0,0,0,0,5,0,0,1,
                    1,0,0,5,0,5,0,0,0,0,0,5,5,5,1,
                    1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,
            },
            {
                    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
                    1,0,0,3,3,0,1,0,0,0,0,0,0,0,1,
                    1,0,1,3,0,3,0,0,0,0,0,0,0,0,1,
                    1,0,0,3,3,3,0,0,0,0,0,0,0,0,1,
                    1,0,1,3,0,3,0,0,0,0,0,0,0,5,1,
                    1,0,0,3,3,0,1,0,0,0,0,0,0,5,1,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,5,1,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
                    1,1,1,1,1,1,1,1,1,1,1,2,1,1,1
            }
    };


    public Vlak(Context context) {
        super(context);
        init(context);
    }
    public Vlak(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Vlak(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void restart(){
        vlakovaSouprava = new ArrayList<pseudoVagon>();
        vlakovaSouprava.add(new pseudoVagon(7,4, 4, 0));

        firstTouch = false;
        stop=false;
    }

    void init(final Context context) {
        bmp = new Bitmap[10];
        bmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.cerna);
        bmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.stena);
        bmp[2] = BitmapFactory.decodeResource(getResources(), R.drawable.dvere);
        bmp[3] = BitmapFactory.decodeResource(getResources(), R.drawable.diamant);
        bmp[4] = BitmapFactory.decodeResource(getResources(), R.drawable.lokomotiva);
        bmp[5] = BitmapFactory.decodeResource(getResources(), R.drawable.koruna);
        bmp[6] = BitmapFactory.decodeResource(getResources(), R.drawable.vagon1);
        bmp[7] = BitmapFactory.decodeResource(getResources(), R.drawable.mrtvalokomotiva);
        bmp[8] = BitmapFactory.decodeResource(getResources(), R.drawable.vagon2);
        bmp[9] = BitmapFactory.decodeResource(getResources(), R.drawable.otevrenedvere);

        restart();

        CountDownTimer countDownTimer = new CountDownTimer(1000000, 500) {
            public void onTick(long millisUntilFinished) {

                long millis = System.currentTimeMillis() - starttime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;
                //TextView text = findViewById(R.id.textView2);
                //text.setText("ahoj");

                if(!stop)
                    if(firstTouch) {
                        if(levels[getCurrentLevel()][(vlakovaSouprava.get(0).Y) * lx + (vlakovaSouprava.get(0).X)] == 9) {
                            stop=true;

                            CountDownTimer countDownTimer = new CountDownTimer(2000, 2000) {
                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                    currentLevel++;
                                    if(currentLevel>2){

                                    }
                                    else{
                                        restart();
                                    }
                                }
                            }.start();
                        }
                        else{
                            int kostka = levels[getCurrentLevel()][(vlakovaSouprava.get(0).Y + y) * lx + (vlakovaSouprava.get(0).X + x)];
                            if (kostka != 1 && kostka != 2 ) {
                                int vagonX = vlakovaSouprava.get(0).X + x;
                                int vagonY = vlakovaSouprava.get(0).Y + y;
                                int vagonyRotace = degree;

                                int myPosX;
                                int myPosY;
                                int myRotace;

                                for (pseudoVagon psVa : vlakovaSouprava) {
                                    myPosX = psVa.X;
                                    myPosY = psVa.Y;
                                    myRotace = psVa.Rotace;

                                    psVa.X = vagonX;
                                    psVa.Y = vagonY;
                                    psVa.Rotace = vagonyRotace;

                                    vagonX = myPosX;
                                    vagonY = myPosY;
                                    vagonyRotace = myRotace;
                                }

                                switch (levels[getCurrentLevel()][(vlakovaSouprava.get(0).Y) * lx + (vlakovaSouprava.get(0).X)]) {
                                    case 3:
                                        vlakovaSouprava.add(new pseudoVagon(vagonX, vagonY, 8, vagonyRotace));
                                        levels[getCurrentLevel()][(vlakovaSouprava.get(0).Y) * lx + (vlakovaSouprava.get(0).X)] = 0;
                                        break;

                                    case 5:
                                        vlakovaSouprava.add(new pseudoVagon(vagonX, vagonY, 6, vagonyRotace));
                                        levels[getCurrentLevel()][(vlakovaSouprava.get(0).Y) * lx + (vlakovaSouprava.get(0).X)] = 0;
                                        break;                                }
                            }
                            else {
                                vlakovaSouprava.get(0).Typ = 7;
                                stop=true;

                            }
                        }
                    }
            }
            public void onFinish() {
            }
        }.start();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }



}
