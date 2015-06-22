package org.bob.android.app.contactmanager.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.exceptions.CMBaseException;
import org.bob.android.app.contactmanager.gui.fragments.CMDetailFragment;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.persistence.beans.RelationBean;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;

import java.util.ArrayList;

/**
 * AsyncTask che, specificato un id di contatto, ne recupera i dati.
 * Created by roberto.gatti on 11/12/2014.
 */
public class ATRetrieveContactDetails extends AsyncTask<Void, Void, Void>
{

    private ContactBean cb;

    private CMDetailFragment df;

    public ATRetrieveContactDetails(ContactBean cb, CMDetailFragment df)
    {
        this.cb = cb;
        this.df = df;
    }

    @Override
    protected Void doInBackground(Void... someVoids)
    {
        ContentResolver cr = ApplicationCM.getInstance().getContentResolver();
        String whereClause = DBConstants.RELATION_CONTACT_ID_FIELD_NAME;
        Cursor cursor = cr.query(Uri.parse(DBConstants.RELATION_CONTACT_ID_URI + "/" + this.cb.getId()), RelationBean.PROJECTION, whereClause, new String[] { String.valueOf(this.cb.getId()) }, DBConstants.RELATION_OBJECT_TYPE_FIELD_NAME );
        if ( cursor == null  || cursor.getCount() < 1 )
        {
            Logger.v(this.getClass(), "Nessuna relazione censita per il contatto!");
            return null;
        }
        if ( cursor.moveToFirst() )
        {
            Logger.v(this.getClass(), "MoveToFirst sul cursor ha restituito false: nessun dato presente");
        }
        do
        {
            RelationBean rel = new RelationBean(cursor);
            this.cb.addRelation(rel);
        }
        while ( cursor.moveToNext() );
        cursor.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        this.df.refreshUI(this.cb);
    }
}
