package org.rabus.ProjectZero;

import android.os.Bundle;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useWakelock = true;
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        cfg.useGL20 = false;

        initialize(new ProjectZeroGame(), cfg);
    }
}
