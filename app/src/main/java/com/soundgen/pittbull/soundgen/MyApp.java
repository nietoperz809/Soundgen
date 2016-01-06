package com.soundgen.pittbull.soundgen;

import java.util.ArrayList;

import android.app.Application;
import android.widget.Toast;


public class MyApp extends Application
{
    public static MyApp app;
    public static ArrayList<SynthPanel> panels = new ArrayList<SynthPanel>();

    public MyApp()
    {
        super();
        app = this;
    }

    public static void kill()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static MyApp get()
    {
        return app;
    }

    public static void Msg (String msg)
    {
        System.out.println("hello: " + msg);
    }
}

