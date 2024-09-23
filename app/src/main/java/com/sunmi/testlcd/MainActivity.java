package com.sunmi.testlcd;

import android.app.Presentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import woyou.aidlservice.jiuiv5.IWoyouService;

public class MainActivity extends AppCompatActivity {

    private IWoyouService woyouService;
    private ServiceConnection connService = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind your service
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        bindService(intent, connService, Context.BIND_AUTO_CREATE);

        // Check for displays and set the appropriate layout
        Display[] displays = getPresentationDisplays();
        if (displays.length > 0) {
            // Set layout for the primary display
            setContentView(R.layout.activity_main);
            handleSecondaryDisplay(displays);
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    public Display[] getPresentationDisplays() {
        DisplayManager mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        return mDisplayManager.getDisplays();
    }

    private void handleSecondaryDisplay(Display[] displays) {
        if (displays.length > 1) {
            // Get the second display
            Display secondDisplay = displays[1];
            // Inflate the second layout for the secondary display
            View secondLayout = LayoutInflater.from(this).inflate(R.layout.activity_second_screen, null);

            // Use the WindowManager to show the layout on the second display
            // You'll need to create a presentation or a new activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Use a Presentation class if available
                Presentation presentation = new Presentation(this, secondDisplay);
                presentation.setContentView(secondLayout);
                presentation.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connService);
    }

    // Button click methods remain unchanged
    public void button1(View view) {
        if (woyouService == null) {
            Toast.makeText(this, "Service not ready", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            woyouService.sendLCDCommand(1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // Other button methods...
}
