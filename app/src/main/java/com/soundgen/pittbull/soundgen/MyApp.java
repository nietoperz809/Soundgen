package com.soundgen.pittbull.soundgen;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class MyApp extends Application
{
    public static final String tagStr = "hello: ";
    public static MyApp app;
    public static ArrayList<SynthPanel> panels = new ArrayList<SynthPanel>();

    public MyApp()
    {
        super();
        app = this;
    }

    /**
     * Returns hosting Activity
     * @param v The view that is hosted
     * @return an Activity or null
     */
    public static  Activity getActivity (View v)
    {
        Context context =  v.getContext();
        while (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
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
        System.out.println(tagStr + msg);
    }

    public static void Err (String msg)
    {
        System.err.println(tagStr + msg);
    }

    /**
     * Sets this App to Fullscreen Landscape
     * This Class MUST be derived from Activity or it will CRASH!
     */
    static void setFullScreenLandscape(Activity act)
    {
        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        act.requestWindowFeature(Window.FEATURE_NO_TITLE);
        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public static int getScreenWidth()
    {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight()
    {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}

