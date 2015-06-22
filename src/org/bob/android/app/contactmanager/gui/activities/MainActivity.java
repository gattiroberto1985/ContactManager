package org.bob.android.app.contactmanager.gui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.fragments.CMDetailFragment;
import org.bob.android.app.contactmanager.gui.fragments.CMListFragment;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;

/**
 * MainActivity dell'applicazione.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class MainActivity extends Activity implements CMListFragment.OnContactSelectedListener
{

    /**
     * FragmentManager di classe.
     */
    private FragmentManager fm = null;

    /**
     * Content resolver di classe.
     */
    private ContentResolver cr = null;

    /**
     * Istanza del fragment di lista contatti.
     */
    private CMListFragment lf = null;

    /**
     * Istanza del fragment di dettaglio singolo contatto.
     */
    private CMDetailFragment df = null;


    /* ********************************************************************* */
    /*                    INTERFACE LIFECYCLE OVERRIDE                       */
    /* ********************************************************************* */
    @Override
    public void onContactSelected(int position)
    {
        Logger.v(this.getClass(), "Cliccato item in posizione " + position);
    }

    public void onContactSelected(ContactBean cb)
    {
        Logger.v(this.getClass(), "Selezionato contatto: '" + cb.toString() + "'");
        Logger.v(this.getClass(), "Verifico la presenza del fragment di dettaglio");
        this.df = (CMDetailFragment) this.fm.findFragmentById(R.id.fragment_contact_detail);
        if ( this.df == null || ! this.df.isVisible() )
        {
            Logger.v(this.getClass(), "Orientamento verticale, fragment di dettaglio non caricato");
            Logger.v(this.getClass(), "Carico la relativa activity...");
            Intent intent = new Intent(this.getApplicationContext(), DetailActivity.class);
            intent.putExtra(Constants.SELECTED_CONTACT_KEY, cb);
            Logger.v(this.getClass(), " |-- Intent creati e oggetto contact registrato. Eseguo lo start dell'activity di dettaglio...");
            startActivity(intent);
        }
        else
        {
            Logger.v(this.getClass(), "Orientamento orizzontale, fragment di dettaglio gia' presenti");
            Logger.v(this.getClass(), "Carico la relativa activity...");
            this.df.setSelectedContact(cb);
        }
        /*
        if ( edf != null && edf.isInLayout() )
	    {
	    	Log.i(TAG, " |-- Fragment '" + edf.getTag() + "' gia' caricato e in layout, setto l'adapter");
	    	//edf.setExpenseArticleAdapter(uri);
	    }
	    else
	    {
	    	Log.i(TAG, " |-- Fragment di dettaglio spese non caricato! Passo la spesa selezionata all'activity nuova");
	    	Intent intent = new Intent(this.getApplicationContext(), SuperMarketExpenseDetailActivity.class);
	    	intent.putExtra(Utilities.INTENT_SELECTED_EXPENSE_NAME, eb);
	    	Log.i(TAG, " |-- Intent creati e oggetto expense registrato. Eseguo lo start dell'activity di dettaglio...");
	        startActivity(intent);
	    }
         */
    }


    /* ********************************************************************* */
    /*                     ACTIVITY LIFECYCLE OVERRIDE                       */
    /* ********************************************************************* */

    /**
     * TODO: gestione event handler menu.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * TODO: gestione recupero bundle di salvataggio per recupero dati
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        Logger.v_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onStart");
        this.cr = this.getContentResolver();
        this.fm = this.getFragmentManager();
        this.lf = (CMListFragment) this.fm.findFragmentById(R.id.fragment_contact_list);
        this.df = (CMDetailFragment) this.fm.findFragmentById(R.id.fragment_contact_detail);
        super.onStart();
    }

    @Override
    protected void onRestart()
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onResume");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause()
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onPause");
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onMenuItemSelected");
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onCreateContextMenu");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onContextItemSelected");
        return super.onContextItemSelected(item);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Logger.i_lfc(this.getClass(), this.getClass().getSimpleName() + " -- onCreate");
        super.onCreate(savedInstanceState);
        // ActionBar
        ActionBar actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        setContentView(R.layout.activity_list);
    }

    /* ********************************************************************* */
    /*                                                                       */
    /* ********************************************************************* */

    public void setFlagLoadedContacts()
    {
        if ( this.lf != null )
            this.lf.setFlagLoadedContacts();
    }
}
