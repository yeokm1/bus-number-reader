package com.example.mkms_android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	Button start;
	Button stop;
	Button voice;
	TextView feed;
	TextToSpeech ttobj; //Sathish: Global Variable for TTS
	BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	
	List<String> bus = new ArrayList<String>();
	
	protected static final int REQUEST_OK = 1;
	private final static int MAX_RSSI = -20;
	private final static int MIN_RSSI = -100;
	private final static String TAG = "mainActivity";
	private final static int REQUEST_ENABLE_BT = 1;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // init button     
        start = (Button)findViewById(R.id.start_btn);
        stop = (Button)findViewById(R.id.stop_btn);
        voice = (Button)findViewById(R.id.voice);
        feed = (TextView)findViewById(R.id.bluetooth_feed);
        
        //Sathish: Initialize TTS in oncreate
        ttobj=new TextToSpeech(getApplicationContext(), 
      	      new TextToSpeech.OnInitListener() {
      	      @Override
      	      public void onInit(int status) {
      	         if(status != TextToSpeech.ERROR){
      	             ttobj.setLanguage(Locale.UK);
      	            }				
      	         }
      	      });       
        feed.setMovementMethod(new ScrollingMovementMethod());
        setupBluetooth();
    }
	
	protected void onStart(){
		super.onStart();
		Log.i(TAG,"started");
		
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
	}
	
	public void voiceRecognition(View v){
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
       	 try {
            startActivityForResult(i, REQUEST_OK);
        } catch (Exception e) {
       	 	Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
	    		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	    		processSpeech(thingsYouSaid.get(0));
	    }
	 }
	
	private void processSpeech(String speech){
		if(speech.contains("start"))
			if(mScanning)
				ttobj.speak("Scanning Already", TextToSpeech.QUEUE_FLUSH, null);
			else
				start.performClick();
		else if(speech.contains("stop"))
			stop.performClick();
		else {
			ttobj.speak("Can't recognize command. Try Again.", TextToSpeech.QUEUE_FLUSH, null);
			voice.performClick();
		}
	}

	private void scanLeDevice(final boolean enable) {
        if (enable) {
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
	}
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
	    @Override
	    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
	    	if(!bus.contains(device.getAddress())) {
	    		bus.add(device.getAddress());
	    		appendFeed(device.getName(),device.getAddress(),rssi);
	    	}
	   }
	};
	
	private void appendFeed(final String device_name, final String address, final int rssi){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if( device_name != null && device_name.startsWith("MKMS")) {
					if(rssi <= MAX_RSSI && rssi >= MIN_RSSI){
						speakText(device_name.substring(5));
						feed.append("Bus no: "+ device_name +", RSSI: " + Integer.toString(rssi) + "\n");
					}
				}
			}
		});
	}
	
	// start button
	public void startBluetooth(View v){
		ttobj.speak("Searching for buses", TextToSpeech.QUEUE_FLUSH, null);
		if(!mScanning)
			scanLeDevice(mBluetoothAdapter.isEnabled());
	}
	// stop button
	public void stopBluetooth(View v){
		Log.i(TAG, "Stop bluetooth was pressed");
		mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        ttobj.speak("Closing connection. Have a safe journey", TextToSpeech.QUEUE_FLUSH, null);
        bus.clear();
	}
	
	// Initializes Bluetooth adapter.
	public void setupBluetooth(){
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}
	
	
	
	public void clearFeed(View v){
		feed.setText("");
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //Sathish: Call this function for android TTS to say the bus number out.
    public void speakText(String busNo){
        String toSpeak = busNo + " is arriving " + busNo;
        ttobj.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
     }
}
