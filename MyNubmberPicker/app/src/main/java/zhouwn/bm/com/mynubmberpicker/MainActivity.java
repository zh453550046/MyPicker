package zhouwn.bm.com.mynubmberpicker;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Arrays;


public class MainActivity extends Activity {

    private static final String STR[] = {
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
    };

    private MyPicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picker = (MyPicker) findViewById(R.id.picker);
        picker.setResouce(this, Arrays.asList(STR));
        picker.setTextCorlor(getResources().getColor(android.R.color.holo_orange_light));
        picker.setOnScrollListenner(new MyPicker.OnScrollListenner() {
            @Override
            public void onScrollFinish() {
               Toast.makeText(MainActivity.this,""+ picker.getCurrentResource(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
