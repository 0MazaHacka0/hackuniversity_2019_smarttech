package com.smarttech.socialstep.ui.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.GpsStatus;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.MapScreenMarker;
import com.here.android.mpa.mapping.MapTransitLayer;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
import com.here.sdk.analytics.internal.HttpClient;
import com.nokia.maps.restrouting.Response;
import com.smarttech.socialstep.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MapActivity extends AppCompatActivity {

    private Map map = null;

    private boolean paused = false;



    RoutePlan routePlan;

    // Set this to PositioningManager.getInstance() upon Engine Initialization
    private PositioningManager posManager;

    public MapRoute mapRoute;
    public CoreRouter router;

    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**Binary XML file line #10: Error inflating class fragment
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };


    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;

    private PositioningManager.OnPositionChangedListener positionListener = new
            PositioningManager.OnPositionChangedListener() {

                public void onPositionUpdated(PositioningManager.LocationMethod method,
                                              GeoPosition position, boolean isMapMatched) {
                    // set the center only when the app is in the foreground
                    // to reduce CPU consumption
                    if (!paused) {
                        map.setCenter(position.getCoordinate(),
                                Map.Animation.NONE);
                    }
                }

                public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                                 PositioningManager.LocationStatus status) {
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
              //  map.getMapTransitLayer().setMode(MapTransitLayer.Mode.EVERYTHING);
            //showBuses();

                String my_url = "http://socialstep.tech:5000/semen/set/1";// Replace this with your own url
                String my_data = "Hello my First Request Without any library";// Replace this with your data
                new MyHttpRequestTask().execute(my_url,my_data);

                // Select routing options
                map.getMapTransitLayer().setMode(MapTransitLayer.Mode.EVERYTHING);

                RouteOptions routeOptions = new RouteOptions();
                routeOptions.setTransportMode(RouteOptions.TransportMode.PUBLIC_TRANSPORT);
                routeOptions.setRouteType(RouteOptions.Type.FASTEST);
                routePlan.setRouteOptions(routeOptions);

                router.calculateRoute(routePlan, new RouterListener());

            }
        });
        checkPermissions();
    }

    private final class RouterListener implements CoreRouter.Listener {

        // Method defined in Listener
        public void onProgress(int percentage) {
            // Display a message indicating calculation progress
        }

        // Method defined in Listener
        public void onCalculateRouteFinished(List<RouteResult> routeResult, RoutingError error) {
            // If the route was calculated successfully
            if (error == RoutingError.NONE) {
                // Render the route on the map
                mapRoute = new MapRoute(routeResult.get(0).getRoute());
                map.addMapObject(mapRoute);

                // Get the bounding box containing the route and zoom in (no animation)
                GeoBoundingBox gbb = routeResult.get(0).getRoute().getBoundingBox();
                map.zoomTo(gbb, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);
            }
            else {
                // Display a message indicating route calculation failure
            }
        }
    }

    private void initialize() {

        // Search for the map fragment to finish setup by calling init().
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);

        // Set up disk cache path for the map service for this application
        // It is recommended to use a path under your application folder for storing the disk cache
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "here_map_service");

        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        // retrieve a reference of the map from the map fragment
                        map = mapFragment.getMap();

                        PositioningManager.getInstance().addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(positionListener));

                        // Display position indicator
                        mapFragment.getPositionIndicator().setVisible(true);

                        routePlan = new RoutePlan();
                        router = new CoreRouter();

                        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
                            @Override
                            public boolean onLongPressEvent(PointF pointF) {
                                if (map != null) {
                                    map.addMapObject(new MapMarker(map.pixelToGeo(pointF)));
                                    routePlan.addWaypoint(new RouteWaypoint(map.pixelToGeo(pointF)));
                                }
                                return true;
                            }
                        }, 0, false);

                        map.getMapTransitLayer().setMode(MapTransitLayer.Mode.EVERYTHING);
                        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);


                    } else {
                        System.out.println("ERROR: Cannot initialize Map Fragment");
                    }
                }
            });
        }
    }

    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    private void showBuses() {
        List<GeoCoordinate> buses = new ArrayList<GeoCoordinate>();
        buses.add(new GeoCoordinate(59.860233, 30.259548));
        MapMarker marker = new MapMarker(buses.get(0));
        if  (map != null)
            map.addMapObject(marker);
        map.setCenter(buses.get(0), Map.Animation.NONE);
    }

    public void onResume() {
        super.onResume();
        paused = false;
        if (posManager != null) {
            posManager.start(
                    PositioningManager.LocationMethod.GPS_NETWORK);
        }
    }

    // To pause positioning listener
    public void onPause() {
        if (posManager != null) {
            posManager.stop();
        }
        super.onPause();
        paused = true;
    }

    // To remove the positioning listener
    public void onDestroy() {
        if (posManager != null) {
            // Cleanup
            posManager.removeListener(
                    positionListener);
        }
        map = null;
        super.onDestroy();
    }

    private class MyHttpRequestTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            String my_url = params[0];
            String my_data = params[1];
            try {
                java.net.URL url = new URL(my_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                // setting the  Request Method Type
                httpURLConnection.setRequestMethod("GET");
                // adding the headers for request
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                try{
                    //to tell the connection object that we will be wrting some data on the server and then will fetch the output result
                    httpURLConnection.setDoOutput(true);
                    // this is used for just in case we don't know about the data size associated with our request
                    httpURLConnection.setChunkedStreamingMode(0);

                    // to write tha data in our request
                    OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    // this is done so that there are no open connections left when this task is going to complete
                    httpURLConnection.disconnect();
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

}
