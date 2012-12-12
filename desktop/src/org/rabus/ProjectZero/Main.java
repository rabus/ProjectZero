package org.rabus.ProjectZero;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "ProjectZero";
        cfg.useGL20 = false;
        cfg.vSyncEnabled = true;
        cfg.width = 800;
        cfg.height = 480;

        new LwjglApplication(new ProjectZeroGame(), cfg);
    }
}