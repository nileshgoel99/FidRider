/*
 * Copyright (c) 2015 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uber.sdk.android.rides;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowActivity;

import java.io.IOException;

import static com.uber.sdk.android.rides.TestUtils.readUriResourceWithUserAgentParam;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/**
 * Tests {@link RequestDeeplink}
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RequestDeeplinkTest {

    private static final String UBER_PACKAGE_NAME = "com.ubercab";
    private static final String CLIENT_ID = "clientId";
    private static final String PRODUCT_ID = "productId";
    private static final float PICKUP_LAT = 32.1234f;
    private static final float PICKUP_LONG = -122.3456f;
    private static final String PICKUP_NICK = "pickupNick";
    private static final String PICKUP_ADDR = "Pickup Address";
    private static final float DROPOFF_LAT = 32.5678f;
    private static final float DROPOFF_LONG = -122.6789f;
    private static final String DROPOFF_NICK = "pickupNick";
    private static final String DROPOFF_ADDR = "Dropoff Address";
    private static final String USER_AGENT_DEEPLINK = "rides-deeplink-v0.1.0";

    @Rule public ExpectedException exception = ExpectedException.none();

    @Test
    public void onBuildDeeplink_whenClientIdAndDefaultRideParamsProvided_shouldHaveDefaults() throws IOException {
        String expectedUri = readUriResourceWithUserAgentParam("src/test/resources/deeplinkuris/just_client_provided",
                USER_AGENT_DEEPLINK);

        RideParameters rideParameters = new RideParameters.Builder().build();
        RequestDeeplink deeplink = new RequestDeeplink.Builder()
                .setRideParameters(rideParameters)
                .setClientId(CLIENT_ID)
                .build();

        assertEquals("URI does not match.", expectedUri, deeplink.getUri().toString());
    }

    @Test
    public void onBuildDeeplink_whenFullRideParamsProvided_shouldCompleteUri() throws IOException {
        String expectedUri = readUriResourceWithUserAgentParam("src/test/resources/deeplinkuris/full_details_uri",
                USER_AGENT_DEEPLINK);

        RideParameters rideParameters = new RideParameters.Builder()
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG, PICKUP_NICK, PICKUP_ADDR)
                .setDropoffLocation(DROPOFF_LAT, DROPOFF_LONG, DROPOFF_NICK, DROPOFF_ADDR)
                .setProductId(PRODUCT_ID)
                .build();
        RequestDeeplink deeplink = new RequestDeeplink.Builder()
                .setRideParameters(rideParameters)
                .setClientId(CLIENT_ID)
                .build();

        assertEquals("URI does not match.", expectedUri, deeplink.getUri().toString());
    }

    @Test
    public void onBuildDeeplink_whenPickupAndClientIdProvided_shouldNotHaveDropoffOrProduct()
            throws IOException {
        String path = "src/test/resources/deeplinkuris/pickup_and_client_provided";
        String expectedUri = readUriResourceWithUserAgentParam(path, USER_AGENT_DEEPLINK);

        RideParameters rideParameters = new RideParameters.Builder()
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG, PICKUP_NICK, PICKUP_ADDR)
                .build();
        RequestDeeplink deeplink = new RequestDeeplink.Builder()
                .setRideParameters(rideParameters)
                .setClientId(CLIENT_ID)
                .build();

        assertEquals("URI does not match.", expectedUri, deeplink.getUri().toString());
    }

    @Test
    public void onBuildDeeplink_whenDropoffClientIdAndProductIdProvided_shouldHaveDefaultPickupAndFullDropoff()
            throws IOException {
        String path = "src/test/resources/deeplinkuris/dropoff_client_and_product_provided";
        String expectedUri = readUriResourceWithUserAgentParam(path, USER_AGENT_DEEPLINK);

        RideParameters rideParameters = new RideParameters.Builder()
                .setProductId(PRODUCT_ID)
                .setDropoffLocation(DROPOFF_LAT, DROPOFF_LONG, DROPOFF_NICK, DROPOFF_ADDR)
                .build();
        RequestDeeplink deeplink = new RequestDeeplink.Builder()
                .setRideParameters(rideParameters)
                .setClientId(CLIENT_ID)
                .build();

        assertEquals("URI does not match.", expectedUri, deeplink.getUri().toString());
    }

    @Test
    public void onBuildDeeplink_whenNoNicknameOrAddressProvided_shouldNotHaveNicknameAndAddress()
            throws IOException {
        String expectedUri = readUriResourceWithUserAgentParam("src/test/resources/deeplinkuris/no_nickname_or_address",
                USER_AGENT_DEEPLINK);

        RideParameters rideParameters = new RideParameters.Builder()
                .setProductId(PRODUCT_ID)
                .setPickupLocation(PICKUP_LAT, PICKUP_LONG, null, null)
                .setDropoffLocation(DROPOFF_LAT, DROPOFF_LONG, null, null)
                .build();
        RequestDeeplink deeplink = new RequestDeeplink.Builder()
                .setRideParameters(rideParameters)
                .setClientId(CLIENT_ID)
                .build();

        assertEquals("URI does not match.", expectedUri, deeplink.getUri().toString());
    }

    @Test
    public void onBuildDeeplink_whenNoClientId_shouldNotBuild() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Must supply a client ID.");

        RideParameters rideParameters = new RideParameters.Builder().build();
        new RequestDeeplink.Builder().setRideParameters(rideParameters).build();
    }

    @Test
    public void onBuildDeeplink_whenNoRideParams_shouldNotBuild() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Must supply ride parameters.");

        new RequestDeeplink.Builder().setClientId(CLIENT_ID).build();
    }

    @Test
    public void execute_whenNoUberApp_shouldPointToMobileSite() throws IOException {
        String expectedUri = readUriResourceWithUserAgentParam("src/test/resources/deeplinkuris/no_app_installed",
                USER_AGENT_DEEPLINK);

        Activity activity = Robolectric.setupActivity(Activity.class);
        ShadowActivity shadowActivity = shadowOf(activity);

        RideParameters rideParameters = new RideParameters.Builder().build();

        RequestDeeplink requestDeeplink = new RequestDeeplink.Builder()
                .setClientId(CLIENT_ID)
                .setRideParameters(rideParameters)
                .build();
        requestDeeplink.execute(activity);

        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(expectedUri, startedIntent.getData().toString());
    }

    @Test
    public void execute_whenUberAppInsalled_shouldPointToUberApp() throws IOException {
        String expectedUri = readUriResourceWithUserAgentParam("src/test/resources/deeplinkuris/just_client_provided",
                USER_AGENT_DEEPLINK);

        Activity activity = Robolectric.setupActivity(Activity.class);
        ShadowActivity shadowActivity = shadowOf(activity);

        RobolectricPackageManager packageManager = (RobolectricPackageManager) shadowActivity.getPackageManager();

        PackageInfo uberPackage = new PackageInfo();
        uberPackage.packageName = UBER_PACKAGE_NAME;
        packageManager.addPackage(uberPackage);

        RideParameters rideParameters = new RideParameters.Builder().build();

        RequestDeeplink requestDeeplink = new RequestDeeplink.Builder()
                .setClientId(CLIENT_ID)
                .setRideParameters(rideParameters)
                .build();

        requestDeeplink.execute(activity);

        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertEquals(expectedUri, startedIntent.getData().toString());
    }
}
