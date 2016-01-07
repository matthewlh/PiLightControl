package matthew.pilightcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // private vars
    private TextView brightnessTextView;
    private SeekBar brightnessSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init variables
        brightnessTextView = (TextView) findViewById(R.id.brightnessTextView);
        brightnessSeekBar = (SeekBar) findViewById(R.id.brightnessSeekBar);

        updateBrightnessTextView();

        // create ChangeListener for brightnessSeekBar
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBrightnessTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

    }

    // update the brightnessTextView based on brightnessSeekBar's value
    protected void updateBrightnessTextView()
    {
        brightnessTextView.setText(String.format("Brightness: %3d%%", brightnessSeekBar.getProgress()));
    }
}
