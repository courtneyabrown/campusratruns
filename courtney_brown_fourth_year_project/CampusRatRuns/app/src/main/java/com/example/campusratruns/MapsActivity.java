package com.example.campusratruns;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static GeoApiContext mGeoApiContext;
    private static PlacesClient placesClient;
    private static AlertDialog errorAlert;
    private static AlertDialog routeAlert;
    private static AlertDialog imageAlert;
    private static LatLng fromBuilding;
    private static LatLng toBuilding;
    private static String fromLevel;
    private static String toLevel;
    private static ArrayList<String> rooms = new ArrayList<>();
    private static Duration durations[] = new Duration[3];
    private static Distance distances[] = new Distance[3];
    private static String directions[] = new String[3];
    private static HashMap<String, Bitmap> waypointImages = new HashMap<>();
    private static int counter = 0;

    /**
     * On Create method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Create error AlertDialog for later use
        errorAlert = new AlertDialog.Builder(this).create();
        errorAlert.setTitle("Error");
        errorAlert.setMessage("Failure to obtain route. Please try again.");
        errorAlert.setButton(errorAlert.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Create route AlertDialog for later use
        routeAlert = new AlertDialog.Builder(this).create();
        routeAlert.setTitle("Route information");
        routeAlert.setMessage("There is no route information to show.");
        routeAlert.setButton(routeAlert.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Create image AlertDialog for later use
        imageAlert = new AlertDialog.Builder(this).create();
        imageAlert.setTitle("Way point image");
        imageAlert.setMessage("Way point images not available.");
        imageAlert.setButton(imageAlert.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Load InputStreams containing room information
        try {
            InputStream alexanderTurnbull = this.getAssets().open("AlexanderTurnbull.txt");
            readFile(alexanderTurnbull);
            InputStream hamnett = this.getAssets().open("Hamnett.txt");
            readFile(hamnett);
            InputStream robertson = this.getAssets().open("Robertson.txt");
            readFile(robertson);
            InputStream barony = this.getAssets().open("Barony.txt");
            readFile(barony);
            InputStream collins = this.getAssets().open("Collins.txt");
            readFile(collins);
            InputStream curran = this.getAssets().open("Curran.txt");
            readFile(curran);
            InputStream grahamHills = this.getAssets().open("GrahamHills.txt");
            readFile(grahamHills);
            InputStream henryDyer = this.getAssets().open("HenryDyer.txt");
            readFile(henryDyer);
            InputStream jamesWeir = this.getAssets().open("JamesWeir.txt");
            readFile(jamesWeir);
            InputStream johnAnderson = this.getAssets().open("JohnAnderson.txt");
            readFile(johnAnderson);
            InputStream livingstone = this.getAssets().open("Livingstone.txt");
            readFile(livingstone);
            InputStream lordHope = this.getAssets().open("LordHope.txt");
            readFile(lordHope);
            InputStream lordTodd = this.getAssets().open("LordTodd.txt");
            readFile(lordTodd);
            InputStream mccance = this.getAssets().open("McCance.txt");
            readFile(mccance);
            InputStream ramshorn = this.getAssets().open("Ramshorn.txt");
            readFile(ramshorn);
            InputStream royalCollege = this.getAssets().open("RoyalCollege.txt");
            readFile(royalCollege);
            InputStream sirWilliamDuncan = this.getAssets().open("SirWilliamDuncan.txt");
            readFile(sirWilliamDuncan);
            InputStream thomasGraham = this.getAssets().open("ThomasGraham.txt");
            readFile(thomasGraham);
            InputStream universityCentre = this.getAssets().open("UniversityCentre.txt");
            readFile(universityCentre);
            InputStream wolfson = this.getAssets().open("Wolfson.txt");
            readFile(wolfson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        rooms.add("Andersonian Library");
        rooms.add("Strathclyde Business School");
        rooms.add("Estates Services");
        rooms.add("Learning and Teaching Building");
        rooms.add("St Pauls Building");
        rooms.add("Strathclyde Sport");
        rooms.add("Strathclyde Students' Union");
        rooms.add("Technology and Innovation Centre");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Create the instance of ArrayAdapter containing list of locations
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown, rooms);

        // Get the instances of AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView1 = findViewById(R.id.fromAutoCompleteTextView);
        autoCompleteTextView1.setThreshold(1);
        autoCompleteTextView1.setAdapter(adapter);
        AutoCompleteTextView autoCompleteTextView2 = findViewById(R.id.toAutoCompleteTextView);
        autoCompleteTextView2.setThreshold(1);
        autoCompleteTextView2.setAdapter(adapter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(/* Insert API Key here */);
                    .build();
        }

        // Initialize the Places SDK
        Places.initialize(getApplicationContext(), /* Insert API Key here */);

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng university = new LatLng(55.8621, -4.2424);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 17));
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int n) {
                // Displays the appropriate room level of the departure building
                IndoorBuilding building = mMap.getFocusedBuilding();
                DecimalFormat df = new DecimalFormat("0.###");
                Double buildingLat = mMap.getCameraPosition().target.latitude;
                Double buildingLon = mMap.getCameraPosition().target.longitude;
                if (df.format(buildingLat).equals(df.format(fromBuilding.latitude)) && df.format(buildingLon).equals(df.format(fromBuilding.longitude))) {
                    if (building != null) {
                        int level = 0;
                        List<IndoorLevel> levels = building.getLevels();
                        for (int i = 0; i < levels.size(); i++) {
                            if (levels.get(i).getName().equals(fromLevel)) {
                                level = i;
                            }
                        }
                        if (level != -1) {
                            levels.get(level).activate();
                        }
                    }
                }
            }
        });
    }

    /**
     * Handles Go button click.
     */
    public void onButtonClick(View view) {
        mMap.clear();
        AutoCompleteTextView autoCompleteTextView1 = findViewById(R.id.fromAutoCompleteTextView);
        String from = null;
        // Check to ensure that from input matches an actual room/building within the university
        if (rooms.contains(autoCompleteTextView1.getText().toString().trim()) || rooms.contains(autoCompleteTextView1.getText().toString().trim().toUpperCase())) {
            // Distinguish from location based on prefix
            if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("AT")) {
                from = "Alexander Turnbull Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("HW")) {
                from = "Arbuthnott (Hamnett Wing)";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("AB")) {
                from = "Arbuthnott (Robertson Wing)";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("BH")) {
                from = "Barony Hall";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("CL")) {
                from = "Collins Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("CU")) {
                from = "Curran Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("GH")) {
                from = "Graham Hills Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("HD")) {
                from = "Henry Dyer Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("JW")) {
                from = "James Weir Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("JA")) {
                from = "John Anderson Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("LT")) {
                from = "Livingstone Tower";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("LH")) {
                from = "Lord Hope Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("AQ")) {
                from = "Lord Todd Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("MC")) {
                from = "McCance Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("AX")) {
                from = "Ramshorn Theatre";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("R")) {
                from = "Royal College Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("DW")) {
                from = "Sir William Duncan Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("TG")) {
                from = "Thomas Graham Building";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("UC")) {
                from = "University Centre";
            } else if (autoCompleteTextView1.getText().toString().trim().toUpperCase().startsWith("WC")) {
                from = "Wolfson Centre";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("ANDERSONIAN LIBRARY")) {
                from = "Andersonian Library";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("STRATHCLYDE BUSINESS SCHOOL")) {
                from = "Strathclyde Business School";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("ESTATES SERVICES")) {
                from = "Estates Services";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("LEARNING AND TEACHING BUILDING")) {
                from = "Learning and Teaching Building";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("ST PAULS BUILDING")) {
                from = "St Pauls Building";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("STRATHCLYDE SPORT")) {
                from = "Strathclyde Sport";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("STRATHCLYDE STUDENTS' UNION")) {
                from = "Strathclyde Students' Union";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("TECHNOLOGY AND INNOVATION CENTRE")) {
                from = "Technology and Innovation Centre";
            }
        }
        // Handle irregular (but still valid) from building input, i.e. "strathclyde sport"
        else {
            if (autoCompleteTextView1.getText().toString().toUpperCase().equals("ANDERSONIAN LIBRARY")) {
                from = "Andersonian Library";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("STRATHCLYDE BUSINESS SCHOOL")) {
                from = "Strathclyde Business School";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("ESTATES SERVICES")) {
                from = "Estates Services";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("LEARNING AND TEACHING BUILDING")) {
                from = "Learning and Teaching Building";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("ST PAULS BUILDING")) {
                from = "St Pauls Building";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("STRATHCLYDE SPORT")) {
                from = "Strathclyde Sport";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("STRATHCLYDE STUDENTS' UNION")) {
                from = "Strathclyde Students' Union";
            } else if (autoCompleteTextView1.getText().toString().toUpperCase().equals("TECHNOLOGY AND INNOVATION CENTRE")) {
                from = "Technology and Innovation Centre";
            }
        }

        AutoCompleteTextView autoCompleteTextView2 = findViewById(R.id.toAutoCompleteTextView);
        String to = null;
        // Check to ensure that to input matches an actual room/building within the university
        if (rooms.contains(autoCompleteTextView2.getText().toString().trim()) || rooms.contains(autoCompleteTextView2.getText().toString().trim().toUpperCase())) {
            // Distinguish to location based on prefix
            if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("AT")) {
                to = "Alexander Turnbull Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("HW")) {
                to = "Arbuthnott (Hamnett Wing)";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("AB")) {
                to = "Arbuthnott (Robertson Wing)";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("BH")) {
                to = "Barony Hall";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("CL")) {
                to = "Collins Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("CU")) {
                to = "Curran Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("GH")) {
                to = "Graham Hills Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("HD")) {
                to = "Henry Dyer Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("JW")) {
                to = "James Weir Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("JA")) {
                to = "John Anderson Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("LT")) {
                to = "Livingstone Tower";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("LH")) {
                to = "Lord Hope Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("AQ")) {
                to = "Lord Todd Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("MC")) {
                to = "McCance Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("AX")) {
                to = "Ramshorn Theatre";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("R")) {
                to = "Royal College Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("DW")) {
                to = "Sir William Duncan Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("TG")) {
                to = "Thomas Graham Building";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("UC")) {
                to = "University Centre";
            } else if (autoCompleteTextView2.getText().toString().trim().toUpperCase().startsWith("WC")) {
                to = "Wolfson Centre";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("ANDERSONIAN LIBRARY")) {
                to = "Andersonian Library";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("STRATHCLYDE BUSINESS SCHOOL")) {
                to = "Strathclyde Business School";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("ESTATES SERVICES")) {
                to = "Estates Services";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("LEARNING AND TEACHING BUILDING")) {
                to = "Learning and Teaching Building";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("ST PAULS BUILDING")) {
                to = "St Pauls Building";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("STRATHCLYDE SPORT")) {
                to = "Strathclyde Sport";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("STRATHCLYDE STUDENTS' UNION")) {
                to = "Strathclyde Students' Union";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("TECHNOLOGY AND INNOVATION CENTRE")) {
                to = "Technology and Innovation Centre";
            }
        }
        // Handle irregular (but still valid) to building input, i.e. "strathclyde sport"
        else {
            if (autoCompleteTextView2.getText().toString().toUpperCase().equals("ANDERSONIAN LIBRARY")) {
                to = "Andersonian Library";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("STRATHCLYDE BUSINESS SCHOOL")) {
                to = "Strathclyde Business School";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("ESTATES SERVICES")) {
                to = "Estates Services";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("LEARNING AND TEACHING BUILDING")) {
                to = "Learning and Teaching Building";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("ST PAULS BUILDING")) {
                to = "St Pauls Building";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("STRATHCLYDE SPORT")) {
                to = "Strathclyde Sport";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("STRATHCLYDE STUDENTS' UNION")) {
                to = "Strathclyde Students' Union";
            } else if (autoCompleteTextView2.getText().toString().toUpperCase().equals("TECHNOLOGY AND INNOVATION CENTRE")) {
                to = "Technology and Innovation Centre";
            }
        }

        if (from != null && to != null) {
            // Handle special cases of some buildings for from location
            fromLevel = String.valueOf(autoCompleteTextView1.getText().toString().trim().charAt(2));
            if (from.equals("Royal College Building")) {
                fromLevel = String.valueOf(autoCompleteTextView1.getText().toString().trim().charAt(1));
            } else if (autoCompleteTextView1.getText().toString().trim().charAt(2) == 'B') {
                fromLevel = "B1";
            } else if (from.equals("Livingstone Tower")) {
                if (autoCompleteTextView1.getText().toString().length() >= 6) {
                    if (Character.isDigit(autoCompleteTextView1.getText().toString().trim().charAt(4)) && Character.isDigit(autoCompleteTextView1.getText().toString().trim().charAt(5))) {
                        fromLevel = String.valueOf(autoCompleteTextView1.getText().toString().trim().charAt(2)).concat(String.valueOf(autoCompleteTextView1.getText().toString().trim().charAt(3)));
                        if (!fromLevel.equals("10") && !fromLevel.equals("11") && !fromLevel.equals("12") && !fromLevel.equals("13") && !fromLevel.equals("14") && !fromLevel.equals("15")) {
                            toLevel = String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(2));
                        }
                    } else {
                        toLevel = String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(2));
                    }
                }
            } else if (from.equals("Sir William Duncan Building")) {
                fromLevel = String.valueOf(Integer.parseInt(String.valueOf(autoCompleteTextView1.getText().toString().trim().charAt(2))) - 1);
            } else if (autoCompleteTextView1.getText().toString().trim().length() > 10) {
                fromLevel = "1";
            }

            // Handle special cases of some buildings for to location
            toLevel = String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(2));
            if (to.equals("Royal College Building")) {
                toLevel = String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(1));
            } else if (autoCompleteTextView2.getText().toString().trim().charAt(2) == 'B') {
                toLevel = "B1";
            } else if (to.equals("Livingstone Tower")) {
                if (autoCompleteTextView2.getText().toString().length() >= 6) {
                    if (Character.isDigit(autoCompleteTextView2.getText().toString().trim().charAt(4)) && Character.isDigit(autoCompleteTextView2.getText().toString().trim().charAt(5))) {
                        toLevel = String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(2)).concat(String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(3)));
                        if (!toLevel.equals("10") && !toLevel.equals("11") && !toLevel.equals("12") && !toLevel.equals("13") && !toLevel.equals("14") && !toLevel.equals("15")) {
                            toLevel = String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(2));
                        }
                    } else {
                        toLevel = String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(2));
                    }
                }
            } else if (to.equals("Sir William Duncan Building")) {
                toLevel = String.valueOf(Integer.parseInt(String.valueOf(autoCompleteTextView2.getText().toString().trim().charAt(2))) - 1);
            } else if (autoCompleteTextView2.getText().toString().trim().length() > 10) {
                toLevel = "1";
            }

            // Get from and to location coordinates
            double fromLat = getCoords(from).get(0);
            double fromLon = getCoords(from).get(1);
            double toLat = getCoords(to).get(0);
            double toLon = getCoords(to).get(1);
            if (fromLat != 0.0 && fromLon != 0.0 && toLat != 0.0 && toLon != 0.0) {
                // Perform directions calculation
                calculateDirections(fromLat, fromLon, toLat, toLon);
                fromBuilding = new LatLng(fromLat, fromLon);
                toBuilding = new LatLng(toLat, toLon);
                // Update camera display to destination building
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toBuilding, (float) 18.5));
            }
        }

        // Handle invalid from input
        else if (from == null && to != null) {
            if (autoCompleteTextView1.getText().length() > 0) {
                errorAlert.setMessage("From input: '" + autoCompleteTextView1.getText() + "' is not valid. Please try again.");
            } else {
                errorAlert.setMessage("From input is not valid. Please try again.");
            }
            waypointImages.clear();
            Arrays.fill(durations, null);
            Arrays.fill(distances, null);
            Arrays.fill(directions, null);
            errorAlert.show();
        }

        // Handle invalid to input
        else if (from != null && to == null) {
            if (autoCompleteTextView2.getText().length() > 0) {
                errorAlert.setMessage("To input: '" + autoCompleteTextView2.getText() + "' is not valid. Please try again.");
            } else {
                errorAlert.setMessage("To input is not valid. Please try again.");
            }
            waypointImages.clear();
            Arrays.fill(durations, null);
            Arrays.fill(distances, null);
            Arrays.fill(directions, null);
            errorAlert.show();
        }

        // Handle invalid from and to input
        else {
            if (autoCompleteTextView1.getText().length() > 0 && autoCompleteTextView2.getText().length() > 0) {
                errorAlert.setMessage("From input: '" + autoCompleteTextView1.getText() + "' and To input: '" + autoCompleteTextView2.getText() + "' is not valid. Please try again.");
            } else {
                errorAlert.setMessage("From input and To input is not valid. Please try again.");
            }
            waypointImages.clear();
            Arrays.fill(durations, null);
            Arrays.fill(distances, null);
            Arrays.fill(directions, null);
            errorAlert.show();
        }
    }

    /**
     * Sets location coordinates.
     *
     * @return List of Lat and Lon values
     */
    public static List<Double> getCoords(String location) {
        double lat = 0.0;
        double lon = 0.0;
        List<Double> coords = new ArrayList<>();
        switch (location) {
            case "Alexander Turnbull Building":
                lat = 55.8608;
                lon = -4.2444;
                break;
            case "Andersonian Library":
                lat = 55.8636;
                lon = -4.2411;
                break;
            case "Arbuthnott (Hamnett Wing)":
                lat = 55.8629;
                lon = -4.2412;
                break;
            case "Arbuthnott (Robertson Wing)":
                lat = 55.8626;
                lon = -4.2411;
                break;
            case "Barony Hall":
                lat = 55.8616;
                lon = -4.2372;
                break;
            case "Collins Building":
                lat = 55.86124;
                lon = -4.24399;
                break;
            case "Curran Building":
                lat = 55.8636;
                lon = -4.2411;
                break;
            case "Estates Services":
                lat = 55.8640;
                lon = -4.2426;
                break;
            case "Graham Hills Building":
                lat = 55.8608;
                lon = -4.2426;
                break;
            case "Henry Dyer Building":
                lat = 55.8625;
                lon = -4.2447;
                break;
            case "James Weir Building":
                lat = 55.8624;
                lon = -4.2456;
                break;
            case "John Anderson Building":
                lat = 55.8614;
                lon = -4.2417;
                break;
            case "Learning and Teaching Building":
                lat = 55.8614;
                lon = -4.2424;
                break;
            case "Livingstone Tower":
                lat = 55.8611;
                lon = -4.2435;
                break;
            case "Lord Hope Building":
                lat = 55.8639;
                lon = -4.2419;
                break;
            case "Lord Todd Building":
                lat = 55.8622;
                lon = -4.2392;
                break;
            case "McCance Building":
                lat = 55.8612;
                lon = -4.2447;
                break;
            case "Ramshorn Theatre":
                lat = 55.8597;
                lon = -4.2450;
                break;
            case "Royal College Building":
                lat = 55.8617;
                lon = -4.2463;
                break;
            case "Sir William Duncan Building":
                lat = 55.8623;
                lon = -4.2432;
                break;
            case "St Pauls Building":
                lat = 55.8624;
                lon = -4.2473;
                break;
            case "Strathclyde Business School":
                lat = 55.8628;
                lon = -4.2430;
                break;
            case "Strathclyde Sport":
                lat = 55.8633;
                lon = -4.2422;
                break;
            case "Strathclyde Students' Union":
                lat = 55.8620;
                lon = -4.2467;
                break;
            case "Technology and Innovation Centre":
                lat = 55.8605;
                lon = -4.2429;
                break;
            case "Thomas Graham Building":
                lat = 55.8627;
                lon = -4.2465;
                break;
            case "University Centre":
                lat = 55.8626;
                lon = -4.2480;
                break;
            case "Wolfson Centre":
                lat = 55.8621;
                lon = -4.2411;
                break;
        }
        coords.add(lat);
        coords.add(lon);
        return coords;
    }

    /**
     * Calculates the route directions.
     */
    /***************************************************
     * Title: Google-Maps-2018 source code
     * Author: Tabian, M.
     * Date: 2018
     * Availability: https://github.com/mitchtabian/Google-Maps-2018/blob/adding-polylines-to-a-google-map-end/app/src/main/java/com/codingwithmitch/googlemaps2018/ui/UserListFragment.java
     ****************************************************/
    private void calculateDirections(double fromLat, double fromLon, double toLat, double toLon) {
        waypointImages.clear();
        Arrays.fill(durations, null);
        Arrays.fill(distances, null);
        Arrays.fill(directions, null);
        counter = 0;
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                toLat, toLon
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(true);
        directions.mode(TravelMode.WALKING);
        directions.origin(
                new com.google.maps.model.LatLng(
                        fromLat,
                        fromLon
                )
        );
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                /***************************************************
                 * Title: Place Photos
                 * Author: Google Developers
                 * Date: 2020
                 * Availability: https://developers.google.com/maps/documentation/places/android-sdk/photos
                 ****************************************************/
                // Get geo-coded way point images, if available
                for (int i = 0; i < result.geocodedWaypoints.length; i++) {
                    // Define Place ID
                    final String placeId = result.geocodedWaypoints[i].placeId;

                    // Specify Place fields
                    final List<Place.Field> placePhotoMetadata = Collections.singletonList(Place.Field.PHOTO_METADATAS);
                    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                    // Create FetchPlaceRequest
                    final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, placePhotoMetadata);
                    // Get Place object
                    placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                        final Place place = response.getPlace();

                        // Get the PhotoMetadata
                        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                        // If metadata is null/empty, return
                        if (metadata == null || metadata.isEmpty()) {
                            return;
                        } else if (metadata.get(0) != null) {
                            final PhotoMetadata photoMetadata = metadata.get(0);

                            // Create FetchPhotoRequest
                            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                    .setMaxWidth(500)
                                    .setMaxHeight(300)
                                    .build();
                            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                // Create FetchPlaceRequest
                                final FetchPlaceRequest placeRequest1 = FetchPlaceRequest.newInstance(placeId, placeFields);
                                placesClient.fetchPlace(placeRequest1).addOnSuccessListener((response1) -> {
                                    waypointImages.put(response1.getPlace().getName(), fetchPhotoResponse.getBitmap());
                                });
                            });
                        }
                    });
                    // Add Polyline directions to map
                    addPolylinesToMap(result);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                errorAlert.show();
            }
        });
    }


    /**
     * Adds route to the map as a Polyline.
     */
    /***************************************************
     * Title: Google-Maps-2018 source code
     * Author: Tabian, M.
     * Date: 2018
     * Availability: https://github.com/mitchtabian/Google-Maps-2018/blob/adding-polylines-to-a-google-map-end/app/src/main/java/com/codingwithmitch/googlemaps2018/ui/UserListFragment.java
     ****************************************************/
    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                int counter = 0;
                for (DirectionsRoute route : result.routes) {
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();
                    // Get current route's duration
                    durations[counter] = route.legs[0].duration;
                    // Get current route's distance
                    distances[counter] = route.legs[0].distance;
                    // Get current route's summary (streets used)
                    directions[counter] = route.summary;
                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    // Add each Polyline route to the map, if applicable
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    if (counter == 0) {
                        polyline.setColor(Color.RED);
                    } else if (counter == 1) {
                        polyline.setColor(Color.GREEN);
                    } else if (counter == 2) {
                        polyline.setColor(Color.BLUE);
                    } else {
                        return;
                    }
                    polyline.setClickable(true);
                    counter++;
                }

                // Displays the appropriate room level of the destination building
                IndoorBuilding building = mMap.getFocusedBuilding();
                mMap.getUiSettings().setZoomControlsEnabled(true);
                if (building != null) {
                    int level = 0;
                    List<IndoorLevel> levels = building.getLevels();
                    for (int i = 0; i < levels.size(); i++) {
                        if (levels.get(i).getName().equals(toLevel)) {
                            level = i;
                        }
                    }
                    if (level != -1) {
                        levels.get(level).activate();
                    }
                }
            }
        });
    }

    /**
     * Reads files containing room information.
     *
     * @return ArrayList of String room names
     */
    public static ArrayList<String> readFile(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter(", ");
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] room = data.split(", ");
            // Add each teaching room to rooms ArrayList
            for (int i = 0; i < room.length; i++) {
                rooms.add(room[i]);
            }
        }
        return rooms;
    }

    /**
     * Gets route information, if available.
     */
    public void displayRouteInformation(View view) {
        // Add each route's information to the AlertDialog, if available
        if (durations[0] != null && durations[1] != null && durations[2] != null) {
            routeAlert.setMessage("Red route: " + durations[0] + ", " + distances[0] + ", via: " + directions[0] + "\n\nGreen route: " + durations[1] + ", " + distances[1] + ", via: " + directions[1] + "\n\nBlue route: " + durations[2] + ", " + distances[2] + ", via: " + directions[2]);
        } else if (durations[0] != null && durations[1] != null && durations[2] == null) {
            routeAlert.setMessage("Red route: " + durations[0] + ", " + distances[0] + ", via: " + directions[0] + "\n\nGreen route: " + durations[1] + ", " + distances[1] + ", via: " + directions[1]);
        } else if (durations[0] != null && durations[1] == null && durations[2] == null) {
            if (!directions[0].isEmpty()) {
                routeAlert.setMessage("Red route: " + durations[0] + ", " + distances[0] + ", via: " + directions[0]);
            } else {
                routeAlert.setMessage("Red route: " + durations[0] + ", " + distances[0]);
            }
        }
        // Revert AlertDialog to original settings if no route information is available
        else {
            routeAlert.setMessage("There is no route information to show.");
        }
        routeAlert.show();
    }

    /**
     * Displays way point image, if available.
     */
    public void displayImage(View view) {
        // Add way point image to the AlertDialog, if available
        if (waypointImages.size() > 0) {
            if (waypointImages.values().toArray()[counter] != null) {
                Drawable d = new BitmapDrawable(getResources(), (Bitmap) waypointImages.values().toArray()[counter]);
                imageAlert.setTitle((CharSequence) waypointImages.keySet().toArray()[counter]);
                imageAlert.setMessage("Way point images available for this route: " + waypointImages.size() + ".");
                imageAlert.setIcon(d);
                if (counter == waypointImages.size() - 1) {
                    counter = 0;
                } else {
                    counter++;
                }
            }
        }
        // Revert AlertDialog to original settings if no way point images available
        else {
            imageAlert.setTitle("Way point image");
            imageAlert.setMessage("Way point images not available.");
            imageAlert.setIcon(null);
        }
        imageAlert.show();
    }
}