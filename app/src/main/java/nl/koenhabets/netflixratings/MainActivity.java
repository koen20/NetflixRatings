package nl.koenhabets.netflixratings;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView  = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        updateText();
    }

    public void accesss(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }

    public void overlay(View view) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }

    private void updateText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView2.setText("Overlay:" + Settings.canDrawOverlays(this));
        }

        AccessibilityManager am = (AccessibilityManager) this
                .getSystemService(Context.ACCESSIBILITY_SERVICE);
        textView.setText("Accessibillity: " + am.isEnabled());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateText();
    }
}
