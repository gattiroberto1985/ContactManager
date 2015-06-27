package org.bob.android.app.contactmanager.persistence.beans;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;

/**
 * Created by roberto on 07/01/15.
 */
public class BeanFactory
{
    public static BaseObject createObject(char type, String object)
    {
        BaseObject obj = null;
        switch ( type )
        {
            case Constants.ADDRESS_TYPE_INDICATOR: obj = new AddressBean(-1, object); break;
            case Constants.PHONE_TYPE_INDICATOR:   obj = new PhoneBean(-1, object);   break;
            case Constants.EMAIL_TYPE_INDICATOR:   obj = new EmailBean(-1, object);   break;
            default: return null;
        }
        return BeanFactory.checkObjectExistence(obj);
    }

    public static BaseObject checkObjectExistence(BaseObject obj)
    {
        Logger.v(BeanFactory.class.getClass(), "Controllo esistenza oggetto a db");
        Uri uri = null;
        String where = "";
        if ( obj instanceof AddressBean ) { uri = DBConstants.ADDRESS_CONTENT_URI; where = DBConstants.ADDRESS_ADDRESS_FIELD_NAME; }
        if ( obj instanceof EmailBean   ) { uri = DBConstants.EMAIL_CONTENT_URI;   where = DBConstants.EMAIL_EMAIL_FIELD_NAME;     }
        if ( obj instanceof PhoneBean   ) { uri = DBConstants.PHONE_CONTENT_URI;   where = DBConstants.PHONE_PHONE_FIELD_NAME;     }
        assert uri != null;
        Cursor cursor = ApplicationCM.getInstance().getContentResolver().query(uri, new String[] { DBConstants.DEFAULT_ID_FIELD_NAME }, where, new String[] { obj.getDescription() }, null);
        if ( cursor == null )
        {
            Logger.v(BeanFactory.class.getClass(), "Nessun dato recuperato, ritorno puntatore vuoto");
            Logger.v(BeanFactory.class.getClass(), "Procedo con l'inserimento dell'oggetto");
            Uri newUri = ApplicationCM.getInstance().getContentResolver().insert(uri, obj.getContentValues());
            int newid = Integer.parseInt(newUri.getLastPathSegment().toString());
            obj.setId(newid);
        }
        else {
            cursor.moveToFirst();
            int rows = cursor.getCount();
            if (rows > 1) {
                cursor.close();
                throw new RuntimeException("ERRORE: oggetto doppio sul database: " + obj.getDescription());
            }
            if (rows == 1) {
                Logger.v(BeanFactory.class.getClass(), "Oggetto gia' esistente, ne recupero l'id");
                int id = cursor.getInt(cursor.getColumnIndex(DBConstants.DEFAULT_ID_FIELD_NAME));
                cursor.close();
                obj.setId(id);
            }
            if (rows == 0) {
                Logger.v(BeanFactory.class.getClass(), "Procedo con l'inserimento dell'oggetto");
                cursor.close();
                Uri newUri = ApplicationCM.getInstance().getContentResolver().insert(uri, obj.getContentValues());
                int newid = Integer.parseInt(newUri.getLastPathSegment().toString());
                obj.setId(newid);
            }
        }
        return obj;
    }

    public static int update(BaseObject object)
    {
        Logger.v(BeanFactory.class.getClass(), "Richiesto aggiornamento bean");
        assert object != null;
        assert object.getId() > 0;
        Uri uri = null;
        if ( object instanceof ContactBean ) uri = Uri.parse(DBConstants.CONTACT_CONTENT_URI + "/" + String.valueOf(object.getId()));
        if ( object instanceof AddressBean ) uri = Uri.parse(DBConstants.ADDRESS_CONTENT_URI + "/" + String.valueOf(object.getId()));
        if ( object instanceof EmailBean   ) uri = Uri.parse(DBConstants.EMAIL_CONTENT_URI + "/" + String.valueOf(object.getId()));
        if ( object instanceof PhoneBean   ) uri = Uri.parse(DBConstants.PHONE_CONTENT_URI + "/" + String.valueOf(object.getId()));
        int rowsUpdated = ApplicationCM.getInstance().getContentResolver().update(uri, object.getContentValues(), null, null);
        if (rowsUpdated < 1 )
        {
            Logger.i(BeanFactory.class.getClass(), "ATTENZIONE: cursor a null o nessun dato aggiornato!");
            return -1;
        }
        else return rowsUpdated;
    }

    public static int exists(BaseObject object)
    {
        Logger.v(BeanFactory.class.getClass(), "Controllo esistenza oggetto a db");
        Uri uri = null;
        String where = "";
        String[] values = null;
        if ( object instanceof ContactBean )
        {
            ContactBean c = (ContactBean) object;
            uri = DBConstants.CONTACT_CONTENT_URI;
            where = DBConstants.CONTACT_SURNAME_FIELD_NAME + "@" + DBConstants.CONTACT_NAME_FIELD_NAME;
            values = new String[] { c.getSurname(), c.getName() };
        }
        if ( object instanceof AddressBean ) { uri = DBConstants.ADDRESS_CONTENT_URI; where = DBConstants.ADDRESS_ADDRESS_FIELD_NAME; values = new String[] { object.getDescription()}; }
        if ( object instanceof EmailBean   ) { uri = DBConstants.EMAIL_CONTENT_URI;   where = DBConstants.EMAIL_EMAIL_FIELD_NAME; values = new String[] { object.getDescription()}; }
        if ( object instanceof PhoneBean   ) { uri = DBConstants.PHONE_CONTENT_URI;   where = DBConstants.PHONE_PHONE_FIELD_NAME;     values = new String[] { object.getDescription()}; }
        assert uri != null;
        Cursor cursor = ApplicationCM.getInstance().getContentResolver().query(uri, new String[] { DBConstants.DEFAULT_ID_FIELD_NAME }, where, values, null);
        if ( cursor == null || cursor.getCount() < 1 )
        {
            Logger.i_lfc(BeanFactory.class.getClass(), "Nessun oggetto recuperato! Si puo' procedere!");
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(DBConstants.DEFAULT_ID_FIELD_NAME));
    }
}

