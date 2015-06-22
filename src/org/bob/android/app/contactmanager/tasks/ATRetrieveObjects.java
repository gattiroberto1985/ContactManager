package org.bob.android.app.contactmanager.tasks;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.AutoCompleteTextView;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.gui.adapters.ACTVAdapter;
import org.bob.android.app.contactmanager.persistence.beans.AddressBean;
import org.bob.android.app.contactmanager.persistence.beans.BaseObject;
import org.bob.android.app.contactmanager.persistence.beans.EmailBean;
import org.bob.android.app.contactmanager.persistence.beans.PhoneBean;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by roberto on 06/01/15.
 */
public class ATRetrieveObjects extends AsyncTask<Void, Void, Void>
{

    private AutoCompleteTextView actv;
    private char objType;
    private ContentResolver cr;
    private Cursor cursor;
    private ArrayList<BaseObject> list = new ArrayList<BaseObject>();

    public ATRetrieveObjects(AutoCompleteTextView actv, char objType)
    {
        this.actv = actv;
        this.objType = objType;
        this.cr = ApplicationCM.getInstance().getContentResolver();
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        ArrayList<BaseObject> objs = new ArrayList<BaseObject>();
        Uri tosearch;
        String fieldName;
        switch ( this.objType )
        {
            case Constants.ADDRESS_TYPE_INDICATOR: tosearch = DBConstants.ADDRESS_CONTENT_URI; fieldName = DBConstants.ADDRESS_ADDRESS_FIELD_NAME; break;
            case Constants.EMAIL_TYPE_INDICATOR: tosearch = DBConstants.EMAIL_CONTENT_URI; fieldName = DBConstants.EMAIL_EMAIL_FIELD_NAME; break;
            case Constants.PHONE_TYPE_INDICATOR: tosearch = DBConstants.PHONE_CONTENT_URI; fieldName = DBConstants.PHONE_PHONE_FIELD_NAME; break;
            default: throw new RuntimeException("ERRORE: tipo non valido: " + this.objType);
        }
        this.cursor = this.cr.query(tosearch, new String[] { fieldName }, null, null, fieldName );
        if ( this.cursor == null || this.cursor.getCount() < 1 )
        {
            Logger.v(this.getClass(), "Nessun dato presente nel cursore!");
            return null;
        }
        if ( ! this.cursor.moveToFirst() )
        {
            Logger.v(this.getClass(), "MoveToFirst sul cursor ha restituito false: nessun dato presente!");
        }

        this.list = new ArrayList<BaseObject>();
        do
        {
            BaseObject obj = null;
            if ( this.objType == Constants.ADDRESS_TYPE_INDICATOR ) obj = new AddressBean(cursor);
            if ( this.objType == Constants.EMAIL_TYPE_INDICATOR ) obj = new EmailBean(cursor);
            if ( this.objType == Constants.PHONE_TYPE_INDICATOR ) obj = new PhoneBean(cursor);
            assert obj != null;
            this.list.add(obj);
        } while ( cursor.moveToNext() );
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        ACTVAdapter aa = new ACTVAdapter(ApplicationCM.getInstance().getApplicationContext(), this.list);

        actv.setAdapter(aa);
        actv.setThreshold(3);
        aa.notifyDataSetChanged();
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onCancelled() {
        if ( this.cursor != null )
            this.cursor.close();
    }

    @Override
    protected void onCancelled(Void aVoid)
    {
        this.onCancelled();
    }
}
