package com.app.simpleimagemapdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.app.simpleimagemapdemo.R;
import com.app.simpleimagemapdemo.controls.DrawCanvasWithOnTouch;
import com.app.simpleimagemapdemo.controls.ZoomLayoutViewInteractiveMap;
import com.app.simpleimagemapdemo.interfaces.InteractiveMapHideShowInterface;
import com.app.simpleimagemapdemo.model.MapLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import htk.lib.areasimage.ClickableArea;

public class MainActivity extends AppCompatActivity implements InteractiveMapHideShowInterface {

    private Activity activity;
    private Context context;

    private String mapLink = ""; //if you provide link image will be loaded from link
    private Drawable localResource;

    //To draw a canvas on ImageView with a touch listner
    private boolean showAllLocation = false;
    private boolean blinkImage = false;
    private int blinkTimes = 3;  //How many time to blink and stop
    private int blinkTimesCounter = 0; //How many times blinking has been completed it will change runtime
    private DrawCanvasWithOnTouch drawCanvasWithOnTouch;
    private DrawCanvasWithOnTouch DrawCanvasWithOnTouchToBlink;
    private InteractiveMapHideShowInterface interactiveMapHideShowInterface;

    //setring map location that include map coordinates
    private ArrayList<MapLocation> mapLocations = new ArrayList<>();

    //handler to blink image
    private android.os.Handler customHandler;

    //controls
    private ZoomLayoutViewInteractiveMap ivZoomVIew;
    private ImageView topImage, thirdImageFromLocation, backImage;
    private Switch switchBlink;
    private ProgressBar pgrsBar;
    private AppCompatSpinner spnrLocationsToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        context = getApplicationContext();
        interactiveMapHideShowInterface = this;

        getSupportActionBar().setTitle("Click on image");

        //i have not loaded image from link so i am using drawable image
        localResource = getResources().getDrawable(R.drawable.map);

        //i have a testing dummy map image so on base of i am using dummy locations
        setDummyMapLocation();

        //initialize all controlls
        initializeControls();

        loadMapImage(mapLink, localResource);  //you pass a link to image and load with URL

        ivZoomVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBlinking();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (blinkImage) {
                            startBlinking(true);
                        } else {
                            if (interactiveMapHideShowInterface != null) {
                                interactiveMapHideShowInterface.interactiveMapHideShow();
                            }
                        }
                    }
                }, 500);
            }
        });
    }

    public void initializeControls() {

        ivZoomVIew = findViewById(R.id.ivZoomVIew);
        topImage = findViewById(R.id.topImage);
        thirdImageFromLocation = findViewById(R.id.thirdImageFromLocation);
        backImage = findViewById(R.id.backImage);
        switchBlink = findViewById(R.id.switchBlink);
        pgrsBar = findViewById(R.id.pgrsBar);
        spnrLocationsToShow = findViewById(R.id.spnrLocationsToShow);

        switchBlink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    blinkImage = isChecked;
                    if (!blinkImage) {
                        stopBlinking();
                    }
                }
            }
        });

        spnrLocationsToShow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {  //show only single location
                    showAllLocation = false;
                } else {  //show multiple location
                    showAllLocation = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        customHandler = new android.os.Handler();
    }

    public void setDummyMapLocation() {
        //Coordinates are px values they are not in dp so they can be used in all device screen and in website also
        MapLocation mapLocation1 = new MapLocation();
        mapLocation1.setId(1);
        mapLocation1.setCoordinates("83,118,230,271");  //X1, Y1, X2, Y2
        mapLocation1.setColor("#4d0000ff");
        mapLocation1.setName("Location 1");

        MapLocation mapLocation2 = new MapLocation();
        mapLocation2.setId(2);
        mapLocation2.setCoordinates("469,341,611,469"); //X1, Y1, X2, Y2
        mapLocation2.setColor("#4ded10ed");
        mapLocation2.setName("Location 2");

        MapLocation mapLocation3 = new MapLocation();
        mapLocation3.setId(1);
        mapLocation3.setCoordinates("155,316,412,506"); //X1, Y1, X2, Y2
        mapLocation3.setColor("#4dFFff00");
        mapLocation3.setName("Location 3");

        MapLocation mapLocation4 = new MapLocation();
        mapLocation4.setId(1);
        mapLocation4.setCoordinates("197,560,741,734"); //X1, Y1, X2, Y2
        mapLocation4.setColor("#4dFF0000");
        mapLocation4.setName("Location 4");

        MapLocation mapLocation5 = new MapLocation();
        mapLocation5.setId(1);
        mapLocation5.setCoordinates("267,82,626,274"); //X1, Y1, X2, Y2
        mapLocation5.setColor("#4d004000");
        mapLocation5.setName("Location 5");

        mapLocations.add(mapLocation1);
        mapLocations.add(mapLocation2);
        mapLocations.add(mapLocation3);
        mapLocations.add(mapLocation4);
        mapLocations.add(mapLocation5);
    }

    public void loadMapImage(final String imageLink, final Drawable localResource) {
        if (!imageLink.isEmpty()) {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(mapLink)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            if (mapLocations != null) {
                                backImage.setImageBitmap(resource);
                                topImage.setImageDrawable(null);
                                topImage.setImageBitmap(null);
                                thirdImageFromLocation.setImageDrawable(null);
                                thirdImageFromLocation.setImageBitmap(null);
                                drawSecondImageCanvas(localResource, resource);
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }
                    });
        } else {
            if (mapLocations != null) {
                backImage.setImageDrawable(localResource);
                topImage.setImageDrawable(null);
                topImage.setImageBitmap(null);
                thirdImageFromLocation.setImageDrawable(null);
                thirdImageFromLocation.setImageBitmap(null);
                drawSecondImageCanvas(localResource, null);
            }
        }
    }

    //topImage and thirdImageFromLocation are fake images which contains boxes of color on particular coordinates
    //this boxes are drawn and image and that image is high lighted original image is backImage
    private void drawSecondImageCanvas(Drawable localResource, Bitmap resource) {
        if (resource != null) {
            if (activity != null && !activity.isFinishing()) {
                if (mapLocations != null) {
                    topImage.setImageBitmap(resource);
                    thirdImageFromLocation.setImageBitmap(resource);

                    ArrayList<MapLocation> singleMapLocations = new ArrayList<>();
                    singleMapLocations.add(mapLocations.get(0));

                    //here have set up clicks for locations according to coordinates
                    drawCanvasWithOnTouch = new DrawCanvasWithOnTouch(activity, context, topImage, singleMapLocations);
                    drawCanvasWithOnTouch.drawCanvas();

                    // Create your image
                    htk.lib.areasimage.ClickableAreasImage clickableAreasImage = new htk.lib.areasimage.ClickableAreasImage(topImage, new htk.lib.areasimage.OnClickableAreaClickedListener() {
                        @Override
                        public void onClickableAreaTouched(htk.lib.areasimage.ClickableArea.State state, float v, float v1, float v2, float v3) {
                            if (state instanceof StateObj) {
                                MapLocation mapLocation = ((StateObj) state).getMapLocation();
                                Toast.makeText(getApplicationContext(), "Location name = " + mapLocation.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // set enable zoom by two finger or double tap
                    clickableAreasImage.setEnableScalePicture(false);

                    // Define your clickable area (pixel values: x coordinate, y coordinate, width, height) and assign an object to it
                    List<ClickableArea> clickableAreas = getClickableAreas(mapLocations);
                    clickableAreasImage.setClickableAreas(clickableAreas);

                    if (mapLocations != null) {
                        MapLocation mapLocation = null;
                        for (int i = 0; i < mapLocations.size(); i++) {
                            mapLocation = mapLocations.get(i);

                            ArrayList<MapLocation> mapLocationsThird = new ArrayList<>();
                            mapLocationsThird.add(mapLocation);
                            DrawCanvasWithOnTouchToBlink = new DrawCanvasWithOnTouch(activity, context, thirdImageFromLocation, mapLocationsThird);
                            DrawCanvasWithOnTouchToBlink.drawCanvas();
                        }
                    }

                    //display image after loaded
                    ivZoomVIew.setVisibility(View.VISIBLE);
                    pgrsBar.setVisibility(View.GONE);
                }
            }
        } else {
            if (mapLocations != null && localResource != null) {
                topImage.setImageDrawable(localResource);
                thirdImageFromLocation.setImageDrawable(localResource);

                ArrayList<MapLocation> singleMapLocations = new ArrayList<>();
                singleMapLocations.add(mapLocations.get(0));
                drawCanvasWithOnTouch = new DrawCanvasWithOnTouch(activity, context, topImage, singleMapLocations);
                drawCanvasWithOnTouch.drawCanvas();

                // Create your image
                htk.lib.areasimage.ClickableAreasImage clickableAreasImage = new htk.lib.areasimage.ClickableAreasImage(topImage, new htk.lib.areasimage.OnClickableAreaClickedListener() {
                    @Override
                    public void onClickableAreaTouched(htk.lib.areasimage.ClickableArea.State state, float v, float v1, float v2, float v3) {
                        if (state instanceof StateObj) {
                            MapLocation mapLocation = ((StateObj) state).getMapLocation();
                            Toast.makeText(getApplicationContext(), "Location Name = " + mapLocation.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // set enable zoom by two finger or double tap
                clickableAreasImage.setEnableScalePicture(false);

                // Define your clickable area (pixel values: x coordinate, y coordinate, width, height) and assign an object to it
                List<ClickableArea> clickableAreas = getClickableAreas(mapLocations);
                clickableAreasImage.setClickableAreas(clickableAreas);

                if (mapLocations != null) {
                    MapLocation mapLocation = null;
                    for (int i = 0; i < mapLocations.size(); i++) {
                        mapLocation = mapLocations.get(i);

                        ArrayList<MapLocation> mapLocationsThird = new ArrayList<>();
                        mapLocationsThird.add(mapLocation);
                        DrawCanvasWithOnTouchToBlink = new DrawCanvasWithOnTouch(activity, context, thirdImageFromLocation, mapLocationsThird);
                        DrawCanvasWithOnTouchToBlink.drawCanvas();
                    }
                }

                //display image after loaded
                ivZoomVIew.setVisibility(View.VISIBLE);
                pgrsBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void interactiveMapHideShow() {
        if (blinkImage) {
            //when blinking
            if (showAllLocation) {
                show(thirdImageFromLocation);
            } else {
                show(topImage);
            }
        } else {
            if (showAllLocation) {
                show(thirdImageFromLocation);
            } else {
                show(topImage);
            }
        }
    }

    //this class will the object of loaction which has been clicked or touch
    public class StateObj extends htk.lib.areasimage.ClickableArea.State {

        private MapLocation mapLocation;

        public StateObj(MapLocation mapLocation) {
            this.mapLocation = mapLocation;
        }

        public MapLocation getMapLocation() {
            return mapLocation;
        }
    }

    @NonNull
    private List<htk.lib.areasimage.ClickableArea> getClickableAreas(ArrayList<MapLocation> mapLocations) {

        List<htk.lib.areasimage.ClickableArea> clickableAreas = new ArrayList<>();

        for (int i = 0; i < mapLocations.size(); i++) {
            if (mapLocations.get(i).getCoordinates() != null && !mapLocations.get(i).getCoordinates().isEmpty()) {

                String splitCoordinates[] = mapLocations.get(i).getCoordinates().split(",");
                int pointx1 = Integer.valueOf(splitCoordinates[0]);
                int pointy1 = Integer.valueOf(splitCoordinates[1]);
                int pointx2 = Integer.valueOf(splitCoordinates[2]);
                int pointy2 = Integer.valueOf(splitCoordinates[3]);

                clickableAreas.add(new htk.lib.areasimage.ClickableArea(Math.round(convertPixelsToDp(activity, pointx1)), Math.round(convertPixelsToDp(activity, pointy1)),
                        Math.round(convertPixelsToDp(activity, calculateWidth(pointx1, pointx2))), Math.round(convertPixelsToDp(activity, calculateheight(pointy1, pointy2))),
                        new StateObj(mapLocations.get(i))));
            }
        }

        return clickableAreas;
    }

    public float convertPixelsToDp(Activity activity, float px) { //while the clickable area convert pixels to dp
        return px / ((float) activity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private int calculateWidth(int x1, int x2) { //calculating the width of clickable box
        return Math.abs(x2 - x1);
    }

    private int calculateheight(int y1, int y2) { //calculating the height of clickable box
        return Math.abs(y2 - y1);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            //write here whatever you want to repeat
            if (interactiveMapHideShowInterface != null) {
                interactiveMapHideShowInterface.interactiveMapHideShow();
            }
            if (blinkTimesCounter == blinkTimes) {
                stopBlinking();
            } else {
                startBlinking(false);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        interactiveMapHideShowInterface = null;
        stopBlinking();
    }

    private void show(final View v) {
        v.animate().alpha(1f).setDuration(200);
        v.setAlpha(1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hide(v);
            }
        }, 400);
    }

    private void hide(View v) {
        v.setAlpha(0f);
        v.animate().alpha(0f).setDuration(200);
    }

    private void startBlinking(boolean startImmediately) {
        if (customHandler != null && updateTimerThread != null) {
            blinkTimesCounter += 1;
            int timeIntervalToBlink = 500;
            if (startImmediately) {
                timeIntervalToBlink = 0;
            }
            customHandler.postDelayed(updateTimerThread, timeIntervalToBlink);

        }
    }

    private void stopBlinking() {
        if (customHandler != null && updateTimerThread != null) {
            customHandler.removeCallbacks(updateTimerThread);
        }
        blinkTimesCounter = 0;
    }
}