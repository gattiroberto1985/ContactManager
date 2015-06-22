package org.bob.android.app.contactmanager.utilities;

import android.util.Log;

/**
 * Wrapper per la classe statica android.util.Log. Eseg
 * Created by roberto.gatti on 11/12/2014.
 */
public class Logger
{

    /**
     * Flag che indica se eseguire il logging dei messaggi
     * relativi allo stage di lifecycle.
     */
    public static final boolean LOG_LFC_MESSAGES = true;

    /**
     * Flag che indica se eseguire il logging dei messaggi
     * relativi agli asynctask.
     */
    public static final boolean LOG_AST_MESSAGES = true;

    /**
     * Flag che indica se eseguire il logging dei messaggi
     * di log generici.
     */
    public static final boolean LOG_APP_MESSAGES = true;

    /**
     * Flag che indica se eseguire il logging dei messaggi
     * di log relativo ai test.
     */
    public static final boolean LOG_TST_MESSAGES = true;


    /**
     * Stringa di logging dei messaggi di lifecycle.
     */
    public static String STAGE_LFC = "[LFC]";

    /**
     * Stringa di logging dei messaggi asincroni (asyncronous task, ...)
     */
    public static String STAGE_AST = "[AST]";

    /**
     * Stringa di logging dei messaggi generici dell'applicazione.
     */
    public static String STAGE_APP = "[APP]";

    /**
     * Stringa di logging dei messaggi generici dell'applicazione.
     */
    public static String STAGE_TST = "[TST]";

    /**
     * Wrapper del metodo i della classe Log.
     * @param clazz
     * @param message
     */
    public static void i(Class clazz, String message)
    {
        Log.i(clazz.getName(), STAGE_APP + " " + message);
    }

    /**
     * Ese
     * @param clazz
     * @param stage
     * @param message
     */
    public static void i(Class clazz, String stage, String message)
    {
        Log.i(clazz.getName(), stage + " " + message);
    }

    /**
     * Wrapper del metodo i della classe Log.
     * @param clazz
     * @param message
     */
    public static void v(Class clazz, String message)
    {
        Log.v(clazz.getName(), STAGE_APP + " " + message);
    }

    /**
     * Ese
     * @param clazz
     * @param stage
     * @param message
     */
    public static void v(Class clazz, String stage, String message)
    {
        Log.v(clazz.getName(), stage + " " + message);
    }

    /**
     * Wrapper del metodo i della classe Log.
     * @param clazz
     * @param message
     */
    public static void d(Class clazz, String message)
    {
        Log.d(clazz.getName(), STAGE_APP + " " + message);
    }

    /**
     * Ese
     * @param clazz
     * @param stage
     * @param message
     */
    public static void d(Class clazz, String stage, String message)
    {
        Log.d(clazz.getName(), stage + " " + message);
    }

    /**
     * Wrapper del metodo i della classe Log.
     * @param clazz
     * @param message
     */
    public static void e(Class clazz, String message)
    {
        Log.e(clazz.getName(), STAGE_APP + " " + message);
    }

    /**
     * Ese
     * @param clazz
     * @param stage
     * @param message
     */
    public static void e(Class clazz, String stage, String message)
    {
        Log.e(clazz.getName(), stage + " " + message);
    }

    /**
     * Metodo per il logging info relativo al lifecycle
     * @param clazz
     * @param message
     */
    public static void i_lfc(Class clazz, String message)
    {
        if (  LOG_LFC_MESSAGES )
            Log.i(clazz.getName(), STAGE_LFC + " " + message);
    }

    public static void v_lfc(Class clazz, String message)
    {
        if ( LOG_LFC_MESSAGES )
            Log.v(clazz.getName(), STAGE_LFC + " " + message);
    }

    /**
     * Metodo per il logging infor relativo ai Test
     * @param clazz
     * @param message
     */
    public static void i_tst(Class clazz, String message)
    {
        if (  LOG_TST_MESSAGES )
            Log.i(clazz.getName(), STAGE_TST + " " + message);
    }

    /**
     * Metodo per il logging verbose relativo ai Test
     * @param clazz
     * @param message
     */
    public static void v_tst(Class clazz, String message)
    {
        if ( LOG_TST_MESSAGES )
            Log.v(clazz.getName(), STAGE_TST + " " + message);
    }
}
