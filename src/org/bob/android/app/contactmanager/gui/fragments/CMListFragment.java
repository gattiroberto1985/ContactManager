package org.bob.android.app.contactmanager.gui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.activities.MainActivity;
import org.bob.android.app.contactmanager.gui.adapters.ExpListAdapter;
import org.bob.android.app.contactmanager.gui.dialogs.CMDialog;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.tasks.ATRetrieveContactDetails;
import org.bob.android.app.contactmanager.tasks.ATRetrieveContactHeaders;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;

/**
 * Classe che implementa il fragment contenente la lista dei contatti
 * censiti nell'applicazione.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class CMListFragment extends Fragment
{

    /**
     * Activity parent del fragment.
     */
    private Activity mActivity = this.getActivity();

    /**
     * ListAdapter per la expandableListView
     */
    private ExpListAdapter elvAdapter;

    /**
     * Reference per la expandable list view.
     */
    private ExpandableListView elv;

    /**
     * Progress Dialog di caricamento dati.
     */
    private ProgressDialog pd;

    /**
     * Flag che indica se gli header dei contatti sono stati caricati o meno.
     */
    public boolean flagLoadedContacts = false;

    /**
     * Interfaccia di gestione selezione contatto sulla expandablelistview.
     */
    public interface OnContactSelectedListener
    {
        public void onContactSelected(int position);
    }

    /**
     * Listener per la selezione del contatto sulla expListView.
     */
    private OnContactSelectedListener listener = null;

    /**
     * Reference al task di recupero dati testata contatto.
     */
    private ATRetrieveContactHeaders task = null;

    /**
     * View per il filtro dei contatti.
     */
    private SearchView contactFilter = null;

    /* ********************************************************************* */
    /*                       SETTER AND GETTER METHODS                       */
    /* ********************************************************************* */

    private void setExpandableListView(View v)
    {
        this.elv = (ExpandableListView) v.findViewById(R.id.frg_list_contact_exp_list);
        this.elvAdapter = new ExpListAdapter(this.getActivity());
        this.elv.setAdapter(this.elvAdapter);
        // Chiude tutti i gruppi non selezionati
        this.elv.setOnGroupExpandListener(new OnGroupExpandListener(this.elv));
        this.elv.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
            {
                ContactBean cb = (ContactBean) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                ((MainActivity) CMListFragment.this.getActivity()).onContactSelected(cb);
                return true;
            }
        });
        // Aggancio il gestore di long click sul contatto
        this.elv.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> elv, View view, int id, long l)
                    {
                        // se la view ha un tag allora e' un child (sarebbe la viewholder), se e' a null allora e'
                        // un header e trascuro l'evento
                        Object viewTag = view.getTag();
                        if ( viewTag == null )
                        {
                            return false;
                        }
                        ExpListAdapter.ContactViewHolder vh = (ExpListAdapter.ContactViewHolder) viewTag;
                        int id2remove = Integer.parseInt(vh.contact_id_tv.getText().toString());
                        Logger.v(this.getClass(), "Apro il dialog di rimozione contatto");
                        CMDialog.showDeleteContactDialog(CMListFragment.this, id2remove).show();
                        return true;
                    }
                }
        );
    }

    public ExpListAdapter getListAdapter() { return this.elvAdapter; }

    public void setFlagLoadedContacts()
    {
        this.flagLoadedContacts = true;
        if ( this.pd != null )
        {
            this.pd.dismiss();
            this.pd = null;
        }
        this.task = null;
    }

    /* ********************************************************************* */
    /*                     FRAGMENT LIFECYCLE OVERRIDE                       */
    /* ********************************************************************* */

    @Override
    public void onAttach(Activity activity) 
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onAttach");
        this.mActivity = activity;
        super.onAttach(activity);
        /*if ( ! ( activity instanceof OnContactSelectedListener ) )
        {
            Logger.e(this.getClass(), "ERRORE: impossibile eseguire il casting in OnContactSelectedListener!");
            throw new RuntimeException("L'activity deve implementare l'interfaccia " + OnContactSelectedListener.class.getCanonicalName() + "!!");
        }
        this.listener = (OnContactSelectedListener) activity;*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onCreateView");
        View v = inflater.inflate(R.layout.fragment_list, container);
        this.setExpandableListView(v);
        this.contactFilter = (SearchView) v.findViewById(R.id.frg_list_filter_contact);
        this.contactFilter.setOnQueryTextListener(this.elvAdapter);
        this.contactFilter.setOnCloseListener(this.elvAdapter);
        this.flagLoadedContacts = false;
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onStart");
        super.onStart();
        // Marco il fragment come "retained fragment"
        this.setRetainInstance(true);
        if ( ! this.flagLoadedContacts )
        {
            Logger.v(this.getClass(), "Starting asynctask to retrieve datas from db....");
            this.pd = ProgressDialog.show(mActivity, "Loading", "Please wait...", true);
            this.task = new ATRetrieveContactHeaders(this, this.getActivity());
            task.execute();
        }
    }

    @Override
    public void onResume()
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onResume");
        if ( this.getActivity() instanceof OnContactSelectedListener )
        {
            Logger.i(this.getClass(), "attivo il listener");
            this.listener = (OnContactSelectedListener) this.mActivity;
            //this.lvContacts.setOnItemClickListener((AdapterView.OnItemClickListener) this.getActivity());
        }
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onPause");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onSaveInstanceState");
        if ( this.pd != null )
        {
            this.pd.dismiss();
            this.pd = null;
        }
        if ( this.task != null ) this.task.cancel(Constants.CANCEL_TASK_IF_RUNNING);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop()
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Logger.v_lfc(this.getClass(), "ListFragment: onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Logger.v_lfc(this.getClass(), "ListFragment: ondestroy");
        this.flagLoadedContacts = false;
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        this.listener = null;
        super.onDetach();
    }

    /*
     * Metodi per gestione menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.v(this.getClass(), "OnOptionsItemSelected listfragment");
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_new_contact:
                AlertDialog dialog = CMDialog.showNewContactDialog(this);
                dialog.show();
                return true;
            case R.id.menu_export_db:
                Utilities.handleDB();
                return true;
            case R.id.menu_parse_file:
                Utilities.parseInputFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

/**
 * Interfaccia di gestione dell'evento di long click su un item della listview.
 * Esegue, se confermata, la rimozione della spesa dal database.
 *
 * @author roberto.gatti
 *
 */
/*class OnContactLongClickListener implements AdapterView.OnItemLongClickListener
{


    private Activity activity;


    private ExpListAdapter adapter;

    public OnContactLongClickListener(Activity a, ExpListAdapter la)
    {
        this.activity = a;
        this.adapter = la;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        String cId_str  =  ((TextView) view.findViewById(R.id.view_exp_list_tv_contact_id)).getText().toString();
        CMDialog.showDeleteContactDialog();
        return false;
    }

}*/

class OnGroupExpandListener implements ExpandableListView.OnGroupExpandListener
{

    int previousItem = -1;

    ExpandableListView elv;

    public OnGroupExpandListener(ExpandableListView elv)
    {
        this.elv = elv;
    }

    @Override
    public void onGroupExpand(int groupPosition)
    {
        if(groupPosition != previousItem )
            this.elv.collapseGroup(previousItem );
        previousItem = groupPosition;
    }
}