package org.bob.android.app.contactmanager;

import android.app.Application;
import android.app.Service;
import android.view.LayoutInflater;

/**
 * Created by roberto on 03/01/15.
 */
public class ApplicationCM extends Application
{
    private static ApplicationCM singleton;

    public static ApplicationCM getInstance()
    {
        return singleton;
    }

    public static String[] OBJECTS_TYPE_NAME;

    public static String[] OBJECTS_TYPE_LABEL;

    @Override
    public void onCreate()
    {
        super.onCreate();
        ApplicationCM.singleton = this;
        ApplicationCM.OBJECTS_TYPE_NAME = getInstance().getResources().getStringArray(R.array.DEFAULT_TYPE_VALUES);
        ApplicationCM.OBJECTS_TYPE_LABEL = getInstance().getResources().getStringArray(R.array.DEFAULT_TYPE_CHARS);
    }

    public static LayoutInflater getLayoutInflater()
    {
        return (LayoutInflater) ApplicationCM.getInstance().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }
}
