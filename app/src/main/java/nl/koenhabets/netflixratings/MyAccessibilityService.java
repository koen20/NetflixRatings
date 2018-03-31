package nl.koenhabets.netflixratings;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MyAccessibilityService extends android.accessibilityservice.AccessibilityService {
    LinearLayout linearLayout;
    Timer timer;

    public MyAccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        final AccessibilityNodeInfo source = accessibilityEvent.getSource();
        try {
            Log.i("text", source.getText().toString());
            Log.i("tada", source.toString());
        } catch (NullPointerException ignored){}

        try {
            if (source.getClassName().equals("android.widget.TextView") && source.getParent().getClassName().equals("android.widget.FrameLayout") && source.getParent().getParent().getClassName().equals("android.support.v7.widget.RecyclerView")) {
                String split[] = source.toString().split(";");
                if (Objects.equals(split[1], " boundsInParent: Rect(0, 0 - 0, 98)")) {
                    Log.i("jaaaaaaaaaaaaaaaaa", source.getText().toString());
                    if (linearLayout != null) {
                        try {
                            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(linearLayout);

                        } catch (IllegalArgumentException ignored) {
                        }
                        linearLayout = null;
                    }
                    RequestQueue requestQueue = Volley.newRequestQueue(this);

                    ImdbRatingRequest request = new ImdbRatingRequest(source.getText().toString(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("response", response);
                            linearLayout = new LinearLayout(getApplicationContext());
                            ImageView imageView = new ImageView(getApplicationContext());
                            TextView textView = new TextView(getApplicationContext());
                            double imdbRating = 0;
                            String error = "";
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                error = jsonObject.getString("Error");

                            } catch (JSONException ignored) {
                            }
                            if (Objects.equals(error, "")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    imdbRating = jsonObject.getDouble("imdbRating");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                textView.setText(imdbRating + "/10");
                            } else {
                                textView.setText(error);
                            }
                            textView.setBackgroundColor(Color.GRAY);
                            imageView.setImageResource(R.drawable.ic_star_black_24dp);
                            linearLayout.addView(imageView);
                            linearLayout.addView(textView);
                            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                                    PixelFormat.TRANSLUCENT);
                            params.gravity = Gravity.RIGHT | Gravity.TOP;
                            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                            wm.addView(linearLayout, params);
                            if (timer != null) {
                                timer.cancel();
                            }
                            timer = new Timer();

                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(linearLayout);
                                }
                            }, 1000 * 20);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    requestQueue.add(request);
                }
            }
        } catch (NullPointerException ignored) {
        }
        /*if (source.getPackageName().equals("com.netflix.mediaclient")) {
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (source.getText() != null && source.getClassName().equals("android.widget.TextView") && source.getParent() != null && source.getParent().getClassName().equals("android.widget.RelativeLayout") && source.getParent().getParent() != null && source.getParent().getParent().getClassName().equals("android.widget.ScrollView") && source.getParent().getParent().getParent() != null && source.getParent().getParent().getParent().getClassName().equals("android.support.v4.view.ViewPager") && source.getParent().getParent().getParent().getParent() != null && source.getParent().getParent().getParent().getParent().getClassName().equals("android.widget.FrameLayout") && source.getParent().getParent().getParent().getParent().getParent() == null) {

                }
            }
        }*/
    }

    @Override
    public void onInterrupt() {

    }
}
