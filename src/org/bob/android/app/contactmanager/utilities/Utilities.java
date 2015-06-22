package org.bob.android.app.contactmanager.utilities;

import android.os.Environment;
import android.widget.Toast;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.persistence.DBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *
 * Classe che contiene metodi di utilità varia per l'applicazione.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class Utilities
{

    /**
     * Moltiplicatore per l'hashcode per il tipo string.
     */
    public static final int STRING_MULTIPLIER = 3;

    /**
     * Moltiplicatore per l'hashcode per il tipo long.
     */
    public static final int LONG_MULTIPLIER = 5;

    private static Random rand = new Random();





    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max)
    {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = Utilities.rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Inserito metodo di esportazione database.
     */
    public static final void handleDB()
    {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/org.bob.android.app.contactmanager/databases/" + DBHelper.DATABASE_NAME;
        String backupDBPath = "dev/org.bob.android.app.contactmanager/" + DBHelper.DATABASE_NAME + (new Date()).getTime();
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(ApplicationCM.getInstance().getApplicationContext(), "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
