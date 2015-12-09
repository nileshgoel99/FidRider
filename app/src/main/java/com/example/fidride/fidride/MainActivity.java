package com.example.fidride.fidride;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.uber.sdk.android.rides.RequestButton;
import com.uber.sdk.android.rides.RideParameters;


public class MainActivity extends Activity {

    private static final String DROPOFF_ADDR = "One Embarcadero Center, San Francisco";
    private static final float DROPOFF_LAT = 37.795079f;
    private static final float DROPOFF_LONG = -122.397805f;
    private static final String DROPOFF_NICK = "Boston";
    private static final String PICKUP_ADDR = "1455 Market Street, San Francisco";
    private static final float PICKUP_LAT = 37.775304f;
    private static final float PICKUP_LONG = -122.417522f;
    private static final String PICKUP_NICK = "Fidelity Investor Center";
    private static final String UBERX_PRODUCT_ID = "a1111c8c-c720-46c3-8534-2fcdd730040d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String clientId = getString(R.string.client_id);
        if (clientId.equals("insert_your_client_id_here")) {
            throw new IllegalArgumentException("Please enter your client ID in client_id in res/values/strings.xml");
        }

        RequestButton uberButtonBlack = (RequestButton) findViewById(R.id.uber_button_black);
        RequestButton uberButtonWhite = (RequestButton) findViewById(R.id.uber_button_white);

        RideParameters rideParameters = new RideParameters.Builder()
                .setProductId(UBERX_PRODUCT_ID)
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG, PICKUP_NICK, PICKUP_ADDR)
                .setDropoffLocation(DROPOFF_LAT, DROPOFF_LONG, DROPOFF_NICK, DROPOFF_ADDR)
                .build();

        uberButtonBlack.setRideParameters(rideParameters);
        uberButtonWhite.setRideParameters(rideParameters);
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
