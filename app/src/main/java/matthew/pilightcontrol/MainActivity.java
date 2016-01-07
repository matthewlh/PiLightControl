package matthew.pilightcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // private vars
    private TextView brightnessTextView;
    private SeekBar brightnessSeekBar;

    private EditText addressEditText;
    private EditText portEditText;
    private Button connectButton;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init variables
        brightnessTextView  = (TextView) findViewById(R.id.brightnessTextView);
        brightnessSeekBar   = (SeekBar)  findViewById(R.id.brightnessSeekBar);
        addressEditText     = (EditText) findViewById(R.id.addressEditText);
        portEditText        = (EditText) findViewById(R.id.portEditText);

        connectButton = (Button) findViewById(R.id.connectButton);
        connected = false;

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

    // respond to click event on connectButton
    public void connectButtonOnClick(View view)
    {
        // respond to button press
        connected = !connected;

        // update button text and address fields
        if(connected) {
            connectButton.setText(R.string.Disconnect);
            addressEditText.setEnabled(false);
            portEditText.setEnabled(false);
        }
        else {
            connectButton.setText(R.string.Connect);
            addressEditText.setEnabled(true);
            portEditText.setEnabled(true);
        }
    }

    // update the brightnessTextView based on brightnessSeekBar's value
    protected void updateBrightnessTextView()
    {
        brightnessTextView.setText(String.format("Brightness: %3d%%", brightnessSeekBar.getProgress()));
    }
}
