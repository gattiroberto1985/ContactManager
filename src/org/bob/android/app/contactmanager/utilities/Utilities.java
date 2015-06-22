package org.bob.android.app.contactmanager.utilities;

import android.os.Environment;
import android.widget.Toast;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.persistence.DBHelper;
import org.bob.android.app.contactmanager.persistence.beans.AddressBean;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.persistence.beans.EmailBean;
import org.bob.android.app.contactmanager.persistence.beans.PhoneBean;

import java.io.*;
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
        String currentDBPath = "/data/" + DBConstants.AUTHORITY + "/databases/" + DBHelper.DATABASE_NAME;
        String backupDBPath = "dev/" + DBConstants.AUTHORITY + "/" + DBHelper.DATABASE_NAME + (new Date()).getTime();
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

    public static final void parseInputFile(String file)
    {
        File ifile = new File(Environment.getExternalStorageDirectory() + "/dev/" + DBConstants.AUTHORITY + "/inputfile.csv" );
        String separator = "§";
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ifile));
            do
            {
                line = reader.readLine();
                // I campi sono
                String[] parts = line.split(separator);

                int birthday = 0;

                String surname = parts[0], name = parts[1], note = parts[2];

                ContactBean cb = new ContactBean(-1, parts[0], parts[1], birthday);

                PhoneBean p1 = new PhoneBean(-1, parts[3]);
                PhoneBean p2 = new PhoneBean(-1, parts[5]);
                PhoneBean p3 = new PhoneBean(-1, parts[7]);

                EmailBean e1 = new EmailBean(-1, parts[9]);
                EmailBean e2 = new EmailBean(-1, parts[11]);

                AddressBean a1 = new AddressBean(-1, parts[13]);


            } while (line != null);
        }
    }

}
