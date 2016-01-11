package matthew.pilightcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    // private vars
    private TextView brightnessTextView;
    private SeekBar brightnessSeekBar;

    private EditText addressEditText;
    private EditText portEditText;
    private Button connectButton;
    private Switch autoReconnectSwitch;

    TCPClient mTcpClient;
    private boolean connected;
    private boolean auto_reconnect;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT).show();

        //init variables
        initVariables();

        // create ChangeListener for brightnessSeekBar
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBrightnessTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // get editor
        SharedPreferences.Editor editor = sharedPref.edit();

        // save server info
        editor.putInt(getString(R.string.saved_brightness_key), brightnessSeekBar.getProgress());

        editor.putString(getString(R.string.saved_address_key), addressEditText.getText().toString());
        editor.putString(getString(R.string.saved_port_key), portEditText.getText().toString());

        editor.putBoolean(getString(R.string.saved_connected_key), connected);
        editor.putBoolean(getString(R.string.saved_auto_reconnect_key), autoReconnectSwitch.isChecked());

        // close editor
        editor.commit();
        editor.clear();
    }

    protected void initVariables()
    {
        // retrieve preferences
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        // retrieve GUI handles
        brightnessTextView  = (TextView)    findViewById(R.id.brightnessTextView);
        brightnessSeekBar   = (SeekBar)     findViewById(R.id.brightnessSeekBar);
        addressEditText     = (EditText)    findViewById(R.id.addressEditText);
        portEditText        = (EditText)    findViewById(R.id.portEditText);
        connectButton       = (Button)      findViewById(R.id.connectButton);
        autoReconnectSwitch = (Switch)      findViewById(R.id.autoReconnectSwitch);


        // retrieve stored preferences
        SharedPreferences.Editor editor = sharedPref.edit();

        Integer defaultBrightness = getResources().getInteger(R.integer.saved_brightness_default);
        Integer savedBrightness = sharedPref.getInt(getString(R.string.saved_brightness_key), defaultBrightness);
        brightnessSeekBar.setProgress(savedBrightness);

        String defaultAddress = getResources().getString(R.string.saved_address_default);
        String savedAddress = sharedPref.getString(getString(R.string.saved_address_key), defaultAddress);
        addressEditText.setText(savedAddress);

        String defaultPort = getResources().getString(R.string.saved_port_default);
        String savedPort = sharedPref.getString(getString(R.string.saved_port_key), defaultPort);
        portEditText.setText(savedPort);

        boolean defaultConnected = getResources().getBoolean(R.bool.saved_connected_default);
        boolean savedConnected = sharedPref.getBoolean(getString(R.string.saved_connected_key), defaultConnected);

        boolean defaultAutoReconnect = getResources().getBoolean(R.bool.saved_auto_reconnect_default);
        boolean savedAutoReconnect = sharedPref.getBoolean(getString(R.string.saved_auto_reconnect_key), defaultAutoReconnect);
        autoReconnectSwitch.setChecked(savedAutoReconnect);

        // try to reconnect if needed
        if(savedConnected && savedAutoReconnect)
        {
            tryConnect();
        }

        // update dependent GUI elements
        updateBrightnessTextView();
        updateConnectButton();
    }

    // respond to click event on connectButton
    public void connectButtonOnClick(View view)
    {
        // respond to button press
        if(connected)
        {
            disconnect();
        }
        else
        {
            tryConnect();
        }

        // update button text and address fields
        updateConnectButton();
    }

    // update button text and address fields
    protected void updateConnectButton()
    {
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
        if( mTcpClient != null) {
            mTcpClient.sendMessage(String.format("Brightness: %3d%%", brightnessSeekBar.getProgress()));
        }

        brightnessTextView.setText(String.format("Brightness: %3d%%", brightnessSeekBar.getProgress()));
    }

    private void tryConnect() {

        String port = portEditText.getText().toString();

        try {
            // check if the port is parsable, although we will still pass it along as a string
            Integer.parseInt(port);

            new connectTask().execute(addressEditText.getText().toString(), port);

            //TODO: check if we actually got connected
            connected = true;
        }
        catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Invalid Port Number!", Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnect() {
        connected = false;

        mTcpClient.stopClient();
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();

    }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... args) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, args[0], Integer.parseInt(args[1]));

            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            //Toast the message from the server
            Toast.makeText(getApplicationContext(), "Server: " + values[0], Toast.LENGTH_SHORT).show();
        }
    }
}
