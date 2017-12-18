package andreew.vlak;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends Activity {

    long starttime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void start(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void score(View view){
        Intent intent = new Intent(this, Main4Activity.class);
        startActivity(intent);
    }

    public void setText() {
        long millis = System.currentTimeMillis() - starttime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        TextView text = findViewById(R.id.textView2);
        text.setText(minutes + ":" + seconds);
    }
}
