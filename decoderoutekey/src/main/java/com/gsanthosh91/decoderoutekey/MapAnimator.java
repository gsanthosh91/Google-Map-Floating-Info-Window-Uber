package com.gsanthosh91.decoderoutekey;

/**
 * Created by Admin on 25-10-2017.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapAnimator {

    static final int GREY = Color.parseColor("#FFA7A6A6");
    public static MapAnimator mapAnimator;
    private Polyline backgroundPolyline;
    private Polyline foregroundPolyline;
    private PolylineOptions optionsForeground;
    private AnimatorSet firstRunAnimSet;
    private AnimatorSet secondLoopRunAnimSet;
    Context mContext;
    List<LatLng> routePointList = null;
    GoogleMap googleMap = null;

    private MapAnimator() {

    }

    public static MapAnimator getInstance() {
        if (mapAnimator == null) mapAnimator = new MapAnimator();
        return mapAnimator;
    }

    public void stopRouteAnim() {
        try {
            if (firstRunAnimSet != null) {
                firstRunAnimSet.removeAllListeners();
                firstRunAnimSet.end();
                firstRunAnimSet.cancel();
            }
            if (secondLoopRunAnimSet != null) {
                secondLoopRunAnimSet.removeAllListeners();
                secondLoopRunAnimSet.end();
                secondLoopRunAnimSet.cancel();
            }
            if (backgroundPolyline != null) {
                backgroundPolyline.remove();
                backgroundPolyline = null;
            }
            if (foregroundPolyline != null) {
                foregroundPolyline.remove();
                foregroundPolyline = null;
            }

            if (routePointList != null) {
                routePointList.clear();
            }
            if (googleMap != null) {
                googleMap = null;
            }

            if (optionsForeground != null) {
                optionsForeground = null;
            }
            MapAnimator.mapAnimator = null;
//            optionsForeground = new PolylineOptions().color(Color.BLACK).width(Utils.dipToPixels(mContext, 5));
//            optionsForeground.addAll(routePointList);
//            foregroundPolyline = googleMap.addPolyline(optionsForeground);
        } catch (Exception e) {

        }
    }

    public void animateRoute(GoogleMap googleMap, List<LatLng> routePointList, Context mContext) {
        this.mContext = mContext;
        this.routePointList = routePointList;
        this.googleMap = googleMap;
        if (firstRunAnimSet == null) {
            firstRunAnimSet = new AnimatorSet();
        } else {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();

            firstRunAnimSet = new AnimatorSet();
        }
        if (secondLoopRunAnimSet == null) {
            secondLoopRunAnimSet = new AnimatorSet();
        } else {
            secondLoopRunAnimSet.removeAllListeners();
            secondLoopRunAnimSet.end();
            secondLoopRunAnimSet.cancel();

            secondLoopRunAnimSet = new AnimatorSet();
        }
        //Reset the polylines
        if (foregroundPolyline != null) foregroundPolyline.remove();
        if (backgroundPolyline != null) backgroundPolyline.remove();


        PolylineOptions optionsBackground = new PolylineOptions().add(routePointList.get(0)).color(GREY).width(dipToPixels(mContext, 5));
        backgroundPolyline = googleMap.addPolyline(optionsBackground);

        optionsForeground = new PolylineOptions().add(routePointList.get(0)).color(Color.BLACK).width(dipToPixels(mContext, 5));
        foregroundPolyline = googleMap.addPolyline(optionsForeground);

        final ValueAnimator percentageCompletion = ValueAnimator.ofInt(0, 100);
        percentageCompletion.setDuration(1800);
        percentageCompletion.setInterpolator(new DecelerateInterpolator());
        percentageCompletion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (backgroundPolyline == null || foregroundPolyline == null) {
                 //   percentageCompletion.end();
                  //  percentageCompletion.cancel();
                    return;
                }
                List<LatLng> foregroundPoints = backgroundPolyline.getPoints();

                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount = foregroundPoints.size();
                int countTobeRemoved = (int) (pointcount * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
                subListTobeRemoved.clear();

                foregroundPolyline.setPoints(foregroundPoints);
            }
        });
        percentageCompletion.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (foregroundPolyline == null) {

                    return;
                }
                foregroundPolyline.setColor(GREY);
                foregroundPolyline.setPoints(backgroundPolyline.getPoints());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), GREY, Color.BLACK);
        colorAnimation.setInterpolator(new AccelerateInterpolator());
        colorAnimation.setDuration(15); // milliseconds

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                if (foregroundPolyline == null) {
                    return;
                }
                foregroundPolyline.setColor((int) animator.getAnimatedValue());
            }

        });

        if (routePointList == null || routePointList.size() == 0) {
            return;
        }

        ObjectAnimator foregroundRouteAnimator = ObjectAnimator.ofObject(this, "routeIncreaseForward", new RouteEvaluator(), routePointList.toArray());
        foregroundRouteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        foregroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (backgroundPolyline == null) {
                    return;
                }
                backgroundPolyline.setPoints(foregroundPolyline.getPoints());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        foregroundRouteAnimator.setDuration(2500);
//        foregroundRouteAnimator.start();

        firstRunAnimSet.playSequentially(foregroundRouteAnimator,
                percentageCompletion);
        firstRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        secondLoopRunAnimSet.playSequentially(colorAnimation,
                percentageCompletion);
        secondLoopRunAnimSet.setStartDelay(10);

        secondLoopRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        firstRunAnimSet.start();

    }

    /**
     * This will be invoked by the ObjectAnimator multiple times. Mostly every 16ms.
     **/
    public void setRouteIncreaseForward(LatLng endLatLng) {
        List<LatLng> foregroundPoints = foregroundPolyline.getPoints();
        foregroundPoints.add(endLatLng);
        foregroundPolyline.setPoints(foregroundPoints);
    }


    public class RouteEvaluator implements TypeEvaluator<LatLng> {
        @Override
        public LatLng evaluate(float t, LatLng startPoint, LatLng endPoint) {
            double lat = startPoint.latitude + t * (endPoint.latitude - startPoint.latitude);
            double lng = startPoint.longitude + t * (endPoint.longitude - startPoint.longitude);
            return new LatLng(lat, lng);
        }
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics));
    }
}
