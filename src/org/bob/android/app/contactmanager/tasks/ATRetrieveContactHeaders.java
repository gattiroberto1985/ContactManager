package org.bob.android.app.contactmanager.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.gui.adapters.ExpListAdapter;
import org.bob.android.app.contactmanager.gui.fragments.CMListFragment;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;

/**
 * AsyncTask che recupera i dati di testata dei contatti.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class ATRetrieveContactHeaders extends AsyncTask<Void, ContactBean, Void>
{


    /**
     * Contesto dell'applicazione.
     */
    private Context context;

    /**
     * ListAdapter della listview con i contatti
     */
    private ExpListAdapter contactListAdapter;

    /**
     * Costruttore
     * @param listFragment
     */
    public ATRetrieveContactHeaders(CMListFragment listFragment, Context context)
    {
        this.contactListAdapter = listFragment.getListAdapter();
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        Cursor cursor = this.context.getContentResolver().query(DBConstants.CONTACT_CONTENT_URI, ContactBean.PROJECTION, null, null, DBConstants.CONTACT_SURNAME_FIELD_NAME );
        if ( cursor == null  || cursor.getCount() < 1 )
        {
            Logger.v(this.getClass(), "Nessun contatto censito!");
            return null;
        }
        if ( cursor.moveToFirst() )
        {
            Logger.v(this.getClass(), "MoveToFirst sul cursor ha restituito false: nessun dato presente");
        }
        do
        {
            ContactBean c = new ContactBean(cursor);
            //String expListHeader = c.getSurname().substring(0,1).toUpperCase();
            this.contactListAdapter.addElement(/*expListHeader,*/ c);
        } while ( cursor.moveToNext() );
        cursor.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(ContactBean... values)
    {
        /*Logger.i(this.getClass(), "onProgressUpdate: " + (values[0] == null ? " NULL CONTACT " : values[0].toString())) ;
        this.contactListAdapter.notifyDataSetChanged();*/
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        Logger.i(this.getClass(), "Updating listview...");
        this.contactListAdapter.notifyDataSetChanged();
        super.onPostExecute(aVoid);
    }
}
