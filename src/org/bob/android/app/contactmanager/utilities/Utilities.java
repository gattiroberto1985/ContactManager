package org.bob.android.app.contactmanager.utilities;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.persistence.DBHelper;
import org.bob.android.app.contactmanager.persistence.beans.*;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public static final void parseInputFile()
    {
        File ifile = new File(Environment.getExternalStorageDirectory() + "/dev/" + DBConstants.AUTHORITY + "/rubrica.csv" );
        String separator = "§";
        try
        {
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(ifile)));
            do
            {
                line = reader.readLine();
                if ( line == null ) break;
                // I campi sono
                String[] parts = line.split(separator, 15);

                int birthday = 0;

                String surname = parts[0], name = parts[1], note = parts[2];

                ContactBean cb = new ContactBean(-1, parts[0], parts[1], birthday);
                Logger.i_lfc(Utilities.class, "Inserimento contatto " + cb.toString());
                ContentValues cv = new ContentValues();
                cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, cb.getClass().getName());
                cv.put(DBConstants.CONTACT_NAME_FIELD_NAME, cb.getName());
                cv.put(DBConstants.CONTACT_SURNAME_FIELD_NAME, cb.getSurname());
                cv.put(DBConstants.CONTACT_BIRTHDAY_FIELD_NAME, cb.getBirthday());
                int c_id = Integer.parseInt(ApplicationCM.getInstance().getContentResolver().insert(DBConstants.CONTACT_CONTENT_URI, cv).getLastPathSegment());
                cb.setId(c_id);

                List<PhoneBean> phones = new ArrayList<PhoneBean>(3);
                phones.add(new PhoneBean(-1, parts[3]));
                phones.add(new PhoneBean(-1, parts[5]));
                phones.add(new PhoneBean(-1, parts[7]));
                for ( PhoneBean p1 : phones ) {
                    if (p1.getPhone() != null && !p1.getPhone().equals("")) {
                        Logger.i_lfc(Utilities.class, "Telefono individuato: procedo con l'inserimento");
                        cv = new ContentValues(2);
                        cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, p1.getClass().getName());
                        cv.put(DBConstants.PHONE_PHONE_FIELD_NAME, p1.getPhone());
                        Uri newentry = ApplicationCM.getInstance().getContentResolver().insert(DBConstants.PHONE_CONTENT_URI, cv);
                        int id = Integer.parseInt(newentry.getLastPathSegment());
                        p1.setId(id);
                        Logger.i_lfc(Utilities.class, "Inserisco la relazione tra telefono e contatto ");
                        RelationBean r = new RelationBean(-1, cb, p1);
                        cv = new ContentValues(4);
                        cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, r.getClass().getName());
                        cv.put(DBConstants.RELATION_CONTACT_ID_FIELD_NAME, r.getContactId());
                        cv.put(DBConstants.RELATION_OBJECT_ID_FIELD_NAME, r.getObjectId());
                        cv.put(DBConstants.RELATION_OBJECT_TYPE_FIELD_NAME, String.valueOf(r.getObjectType()));
                        ApplicationCM.getInstance().getContentResolver().insert(DBConstants.RELATION_CONTENT_URI, cv);
                    }
                }
                
                List<EmailBean> emails = new ArrayList<EmailBean>(2);
                emails.add(new EmailBean(-1, parts[9]) );
                emails.add(new EmailBean(-1, parts[11]) );
                for ( EmailBean e : emails ) {
                    if (e.getEMail() != null && !e.getEMail().equals("")) {
                        Logger.i_lfc(Utilities.class, "EMail individuato: procedo con l'inserimento");
                        cv = new ContentValues(2);
                        cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, e.getClass().getName());
                        cv.put(DBConstants.EMAIL_EMAIL_FIELD_NAME, e.getEMail());
                        Uri newentry = ApplicationCM.getInstance().getContentResolver().insert(DBConstants.EMAIL_CONTENT_URI, cv);
                        int id = Integer.parseInt(newentry.getLastPathSegment());
                        e.setId(id);
                        Logger.i_lfc(Utilities.class, "Inserisco la relazione tra email e contatto ");
                        RelationBean r = new RelationBean(-1, cb, e);
                        cv = new ContentValues(4);
                        cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, r.getClass().getName());
                        cv.put(DBConstants.RELATION_CONTACT_ID_FIELD_NAME, r.getContactId());
                        cv.put(DBConstants.RELATION_OBJECT_ID_FIELD_NAME, r.getObjectId());
                        cv.put(DBConstants.RELATION_OBJECT_TYPE_FIELD_NAME, String.valueOf(r.getObjectType()));
                        ApplicationCM.getInstance().getContentResolver().insert(DBConstants.RELATION_CONTENT_URI, cv);
                    }
                }    
                
                List<AddressBean> addresses = new ArrayList<AddressBean>(1);
                addresses.add(new AddressBean(-1, parts[13]));
                for ( AddressBean a : addresses ) {
                    if (a.getAddress() != null && !a.getAddress().equals("")) {
                        Logger.i_lfc(Utilities.class, "Indirizzo individuato: procedo con l'inserimento");
                        cv = new ContentValues(2);
                        cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, a.getClass().getName());
                        cv.put(DBConstants.ADDRESS_ADDRESS_FIELD_NAME, a.getAddress());
                        Uri newentry = ApplicationCM.getInstance().getContentResolver().insert(DBConstants.ADDRESS_CONTENT_URI, cv);
                        int id = Integer.parseInt(newentry.getLastPathSegment());
                        a.setId(id);
                        Logger.i_lfc(Utilities.class, "Inserisco la relazione tra indirizzo e contatto ");
                        RelationBean r = new RelationBean(-1, cb, a);
                        cv = new ContentValues(4);
                        cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, r.getClass().getName());
                        cv.put(DBConstants.RELATION_CONTACT_ID_FIELD_NAME, r.getContactId());
                        cv.put(DBConstants.RELATION_OBJECT_ID_FIELD_NAME, r.getObjectId());
                        cv.put(DBConstants.RELATION_OBJECT_TYPE_FIELD_NAME, String.valueOf(r.getObjectType()));
                        ApplicationCM.getInstance().getContentResolver().insert(DBConstants.RELATION_CONTENT_URI, cv);
                    }
                }
                Logger.i_lfc(Utilities.class, "Parsing eseguito con successo!");

            } while (line != null);
        }
        catch ( IOException e )
        {
            Logger.i_lfc(Utilities.class, "ERRORE: " + e.getMessage());
        }
    }

}
