package andreew.vlak;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import static android.content.ContentValues.TAG;
import static java.lang.Math.sqrt;

/**
 * Created by opalo on 15.11.2017.
 */

public class Vlak extends View implements InputManager.InputDeviceListener {
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
    final MediaPlayer zvukcarna = MediaPlayer.create(getContext(), R.raw.zvukcerna);
    final MediaPlayer zvukobjekt = MediaPlayer.create(getContext(), R.raw.zvukobjekt);
    final MediaPlayer konec = MediaPlayer.create(getContext(), R.raw.konec);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Toast.makeText(getContext(), ""+keyCode, Toast.LENGTH_SHORT).show();
        return true;
    }

    public void restart(){
        vlakovaSouprava = new ArrayList<pseudoVagon>();
        vlakovaSouprava.add(new pseudoVagon(7,4, 4, 0));

        firstTouch = false;
        stop=false;
        door = findDoor();
        pocetObjektu = findAllSebratelneObjeky();
    }

    public boolean jeTuVagon(int x, int y){
        for(pseudoVagon vagon : vlakovaSouprava.subList( 1, vlakovaSouprava.size() ) ) {
            if(vagon.X == x && vagon.Y == y){
                return true;
            }
        }
        return false;
    }

    public void playSound(int id){
        final MediaPlayer mp = MediaPlayer.create(getContext(), id);
        mp.start();
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
                        konec.start();
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
                        if (kostka != 1 && kostka != 2 && !jeTuVagon((vlakovaSouprava.get(0).X + x), (vlakovaSouprava.get(0).Y + y))) {
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
                                    zvukobjekt.start();
                                    break;

                                case 5:
                                    vlakovaSouprava.add(new pseudoVagon(vagonX, vagonY, 6, vagonyRotace));
                                    levels[getCurrentLevel()][(vlakovaSouprava.get(0).Y) * lx + (vlakovaSouprava.get(0).X)] = 0;
                                    zvukobjekt.start();
                                    break;

                                default:
                                    zvukcarna.start();
                                    break;
                            }

                            if (findAllSebratelneObjeky() == 0) {
                                levels[getCurrentLevel()][(door.y) * lx + (door.x)] = 9;
                            }

                        }
                        else {
                            vlakovaSouprava.get(0).Typ = 7;
                            stop=true;
                            CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                    restart();
                                }
                            }.start();
                        }
                    }
                }
            }
            public void onFinish() {
            }
        }.start();
    }

    void move(int X, int Y, int Degree){
        x=X;
        y=Y;
        degree=Degree;
    }

    double lineFromPoints(double x1, double x2, double y1, double y2, double myy){
        double A = y2 - y1;
        double B = -(x2 - x1);
        double C = -A * x1 - B * y1;
        double M = sqrt(A * A + B * B);
        double a = A / M;
        double b = B / M;
        double c = C / M;

        return ((b*myy+c)/-a);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            firstTouch=true;

            if(lineFromPoints(0,width*lx, 0, height*ly, event.getY())> event.getX() ) {
                if (lineFromPoints(0, width*lx, height*ly, 0, event.getY()) > event.getX()) {
                    //left
                    move(-1, 0, 180);
                }
                else {
                    //down
                    move(0, +1, 90);
                }
            }
            else{
                if (lineFromPoints(0, width*lx, height*ly, 0, event.getY()) > event.getX()) {
                    //up
                    move(0, -1, 270);
                }
                else {
                    //right
                    move(+1, 0, 0);
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        invalidate();
        return true;
    }

    public Point findDoor(){
        for (int i = 0; i < ly; i++) {
            for (int j = 0; j < lx; j++) {
                if(levels[getCurrentLevel()][(i) * lx + (j)] == 2)
                    return new Point(x = j, y = i);
            }
        }
        return null;
    }

    public int findAllSebratelneObjeky(){
        int pocet = 0;
        for (int i = 0; i < ly; i++) {
            for (int j = 0; j < lx; j++) {
                int objekt = levels[getCurrentLevel()][(i) * lx + (j)];
                if(objekt == 3 || objekt==5)
                    pocet++;
            }
        }
        return pocet;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    public int[] getLevel(){
        return levels[getCurrentLevel()];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w / lx;
        height = h / ly;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < ly; i++) {
            for (int j = 0; j < lx; j++) {
                canvas.drawBitmap(bmp[getLevel()[i*lx + j]], null, new Rect(j*width, i*height,(j+1)*width, (i+1)*height), null);
            }
        }


        for (pseudoVagon vagon : vlakovaSouprava) {
            int j = vagon.X;
            int i = vagon.Y;
            canvas.drawBitmap(
                    vagon.Rotace>90 ?
                        (vagon.Rotace>180 ?
                                flip(rotateBitmap(bmp[vagon.Typ], vagon.Rotace)) :
                                flip(bmp[vagon.Typ])
                        ) : rotateBitmap(bmp[vagon.Typ],vagon.Rotace), null, new Rect(j*width, i*height,(j+1)*width, (i+1)*height), null);
        }
        invalidate();
    }

    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        int width = original.getWidth();
        int height = original.getHeight();

        Bitmap rotatedBitmap = original;
        Matrix mat = new Matrix();
        mat.postRotate(degrees);
        Bitmap bmpRotate = Bitmap.createBitmap(original, 0, 0, width, height, mat, true);

        return bmpRotate;
    }

    public Bitmap flip(Bitmap src)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = src.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    @Override
    public void onInputDeviceAdded(int i) {

    }

    @Override
    public void onInputDeviceRemoved(int i) {

    }

    @Override
    public void onInputDeviceChanged(int i) {

    }
}
