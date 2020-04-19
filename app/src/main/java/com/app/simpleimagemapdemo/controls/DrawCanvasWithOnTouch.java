package com.app.simpleimagemapdemo.controls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.app.simpleimagemapdemo.model.MapLocation;

import java.util.ArrayList;
import java.util.List;

public class DrawCanvasWithOnTouch implements View.OnTouchListener {

    private ImageView imageView;
    private Context context;
    private Activity activity;

    private Canvas canvas;
    private List<Rect> rectList = new ArrayList<>();
    private Paint paint;
    private Bitmap bitmap;
    private Bitmap mutableBitmap;
    private ArrayList<MapLocation> mapLocations = new ArrayList<>();
    private Path path;
    private List<Path> paths = new ArrayList<>();

    //private boolean changeColor = false;
    private boolean changeColor = true;
    private boolean clickedOnce = false;

    public DrawCanvasWithOnTouch(Activity activity, Context context, ImageView imageView, final ArrayList<MapLocation> mapLocations) {
        this.context = context;
        this.imageView = imageView;
        this.activity = activity;
        this.mapLocations = mapLocations;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void drawCanvas() {
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        canvas = new Canvas(mutableBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        drawBitmapCanvas(mutableBitmap, activity, imageView, mapLocations, Color.TRANSPARENT);
        changeColor = true;

    }

    private void drawBitmapCanvas(final Bitmap mutableBitmap, final Activity activity,
                                  final ImageView imageView, final ArrayList<MapLocation> mapLocations, int color) {

        paths = new ArrayList<>();
        for (int i = 0; i < mapLocations.size(); i++) {

            String colors = "#FF0000";  //Defualt red color
            if (mapLocations.get(i) != null && mapLocations.get(i).getColor() != null && !mapLocations.get(i).getColor().isEmpty()) {
                colors = mapLocations.get(i).getColor();
            }

            paint.setColor(Color.parseColor(colors));

            path = new Path();
            RectF rect = new RectF();

            if (mapLocations.get(i).getCoordinates() != null && !mapLocations.get(i).getCoordinates().isEmpty()) {

                String splitCoordinates[] = mapLocations.get(i).getCoordinates().split(",");

                int pointx1 = Math.round(Integer.valueOf(splitCoordinates[0]));
                int pointy1 = Math.round(Integer.valueOf(splitCoordinates[1]));
                int pointx2 = Math.round(Integer.valueOf(splitCoordinates[2]));
                int pointy2 = Math.round(Integer.valueOf(splitCoordinates[3]));

                System.out.println("Something with canvas value - " + pointx1 + " " + pointy1 + " " +
                        pointx2 + " " + pointy2);

                rect.set(pointx1, pointy1, pointx2, pointy2);
                paths.add(path);
                canvas.drawRect(rect, paint);
            }
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageDrawable(new BitmapDrawable(activity.getResources(), mutableBitmap));
            }
        });
    }


}