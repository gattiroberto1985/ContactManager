package org.bob.android.app.contactmanager.gui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.activities.DetailActivity;
import org.bob.android.app.contactmanager.gui.adapters.LVAdapter;
import org.bob.android.app.contactmanager.gui.dialogs.CMDialog;
import org.bob.android.app.contactmanager.persistence.beans.*;
import org.bob.android.app.contactmanager.tasks.ATRetrieveContactDetails;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che implementa il fragment contenente i dettagli del contatto
 * selezionato.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class CMDetailFragment extends Fragment
{

    private ProgressDialog pd = null;

    private ATRetrieveContactDetails task = null;

    private DetailActivity mActivity;

    private ContactBean selectedContact = null;

    private TabHost th = null;

    private ListView lv_adrs = null;

    private ListView lv_phns = null;

    private ListView lv_emls = null;

    private ListView curLV = null;

    private LVAdapter lvad = null;

    private LVAdapter lvem = null;

    private LVAdapter lvph = null;

    /* ********************************************************************* */
    /*                              CLASS METHODS                            */
    /* ********************************************************************* */

    public void setSelectedContact(ContactBean cb)
    {
        Logger.i(this.getClass(), "Fragment di dettaglio: carico i dati del contatto");
        this.selectedContact = cb;
        ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_id)     ).setText(String.valueOf(this.selectedContact.getId()));
        ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_name)   ).setText(this.selectedContact.getName());
        ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_surname)).setText(this.selectedContact.getSurname());
        ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_birthday)).setText(Utilities.DATE_FORMATTER.format(new java.util.Date(this.selectedContact.getBirthday())));
        this.pd = ProgressDialog.show(this.getActivity(), "Loading contact details", "Please wait while loading contact details...");
        this.task = new ATRetrieveContactDetails(this.selectedContact, this);
        task.execute();
    }

    public ContactBean getSelectedContact() { return this.selectedContact; }

    public void refreshUI(ContactBean cb)
    {
        this.pd.dismiss();
        if ( this.lvad != null ) this.lvad.clear();
        if ( this.lvem != null ) this.lvem.clear();
        if ( this.lvph != null ) this.lvph.clear();
        // Recupero le tre listview
        ArrayList<BaseObject> adrs = new ArrayList<BaseObject>();
        ArrayList<BaseObject> emls = new ArrayList<BaseObject>();
        ArrayList<BaseObject> phns = new ArrayList<BaseObject>();

        for (RelationBean r : cb.getRelations() )
        {
            switch (r.getObjectType() )
            {
                case Constants.ADDRESS_TYPE_INDICATOR: adrs.add(r.getObject()); break;
                case Constants.EMAIL_TYPE_INDICATOR:   emls.add(r.getObject()); break;
                case Constants.PHONE_TYPE_INDICATOR:   phns.add(r.getObject()); break;
                default:
                    throw new RuntimeException("ERRORE: dato invalido in refreshUI: '" + r.getObjectType() + "'");
            }
        }

        this.lvad = new LVAdapter(this.getActivity(), adrs);
        this.lvph = new LVAdapter(this.getActivity(), phns);
        this.lvem = new LVAdapter(this.getActivity(), emls);

        lv_adrs.setAdapter(lvad);
        lv_phns.setAdapter(lvph);
        lv_emls.setAdapter(lvem);

        //this.lv_adrs.setOnItemClickListener(new OnReferenceClick());
        //this.lv_phns.setOnItemClickListener(new OnReferenceClick());
        //this.lv_emls.setOnItemClickListener(new OnReferenceClick());

        lvad.notifyDataSetChanged();
        lvph.notifyDataSetChanged();
        lvem.notifyDataSetChanged();
    }

    /* ********************************************************************* */
    /*                     FRAGMENT LIFECYCLE OVERRIDE                       */
    /* ********************************************************************* */

    @Override
    public void onAttach(Activity activity)
    {
        Logger.v_lfc(this.getClass(), "DetailFragment: onAttach");
        if ( activity instanceof DetailActivity )
            this.mActivity = (DetailActivity) activity;
        super.onAttach(activity);
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
        Logger.v_lfc(this.getClass(), "DetailFragment: onCreateView");
        View v = inflater.inflate(R.layout.fragment_detail, container);


        this.th =(TabHost) v.findViewById(R.id.tab_host);
        // Before adding tabs, it is imperative to call the method setup()
        this.th.setup();

        // Adding tabs
        this.th.addTab(this.th.newTabSpec("addresses").setIndicator(getResources().getString(R.string.HDR_ADDRESSES_TAB_LABEL), getResources().getDrawable(android.R.drawable.ic_menu_add)).setContent(R.id.addresses_lyt));
        this.th.addTab(this.th.newTabSpec("phones")   .setIndicator(getResources().getString(R.string.HDR_PHONES_TAB_LABEL)   , getResources().getDrawable(android.R.drawable.ic_menu_add)).setContent(R.id.phones_lyt));
        this.th.addTab(this.th.newTabSpec("emails")   .setIndicator(getResources().getString(R.string.HDR_EMAILS_TAB_LABEL)   , getResources().getDrawable(android.R.drawable.ic_menu_add)).setContent(R.id.emails_lyt));

        this.th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
               /*if (tabId.equals("tag2")) {
                    View view = this.th.getChildAt(this.th.getCurrentTab());
                    NewActivity.this.curLV = (ListView) view.findViewById(R.id.listFavourites);
                    Toast.makeText(getApplicationContext(), favouritesList.toString(), Toast.LENGTH_LONG).show();
                }*/
            }
        });
        this.lv_adrs =(ListView) v.findViewById(R.id.lyt_adr_list);
        this.lv_phns =(ListView) v.findViewById(R.id.lyt_phn_list);
        this.lv_emls =(ListView) v.findViewById(R.id.lyt_eml_list);
        this.curLV = this.lv_adrs;
        /*ListView lv=(ListView) findViewById(R.id.listView1);
        MyListAdapter adapter =new MyListAdapter(this);
        lv.setAdapter(adapter);*/

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
        Logger.v_lfc(this.getClass(), "DetailFragment: onStart");
        super.onStart();
        // verifico che il contatto sia stato valorizzato
        if ( this.selectedContact == null )
        {
            this.selectedContact = (ContactBean) this.getActivity().getIntent().getSerializableExtra(Constants.SELECTED_CONTACT_KEY);
            if ( this.selectedContact == null )
            {
                Logger.v_lfc(this.getClass(), "Nessun contatto selezionato!");
                return;
            }
            Logger.v(this.getClass(), "Contatto selezionato: " + this.selectedContact.toString() );
            ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_id)      ).setText(String.valueOf(this.selectedContact.getId()));
            ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_name)    ).setText(this.selectedContact.getName());
            ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_surname) ).setText(this.selectedContact.getSurname());
            ( (TextView) this.getActivity().findViewById(R.id.dtl_hdr_birthday)).setText(Utilities.DATE_FORMATTER.format(new java.util.Date(this.selectedContact.getBirthday())));
            this.getActivity().setTitle(this.selectedContact.getDescription());
        }
        // se il contatto ha gia' le relazioni caricate, non procedo con un nuovo task
        if ( !(this.selectedContact.getRelations() != null && this.selectedContact.getRelations().size() > 0 ))
        {
            // Marco il fragment come "retained fragment"
            //this.setRetainInstance(true);
            Logger.v(this.getClass(), "Starting asynctask to retrieve contact details...");
            this.pd = ProgressDialog.show(this.getActivity(), "Loading", this.getActivity().getString(R.string.LOADING_CONTACT_DETAILS), true);
            this.task = new ATRetrieveContactDetails(this.selectedContact, this);
            task.execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if ( this.pd != null )
        {
            this.pd.dismiss();
            this.pd = null;
        }
        if ( this.task != null ) this.task.cancel(Constants.CANCEL_TASK_IF_RUNNING);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Logger.v(this.getClass(), "OnOptionsItemSelected listfragment");
        // Handle item selection
        AlertDialog dialog;
        switch (item.getItemId()) {
            case R.id.menu_new_reference:
                dialog = CMDialog.showNewReferenceDialog(this);
                dialog.show();
                return true;
            case R.id.menu_contact_detail_edit:
                dialog = CMDialog.showEditContactHeaderDialog(this);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

/*class OnReferenceClick implements AdapterView.OnItemClickListener
{
    @Override
    public void onItemClick(AdapterView<?> listview, View childview, int i, long l)
    {
        LVAdapter ad = (LVAdapter) ( (ListView) listview).getAdapter();
        BaseObject selected = ad.getItem(i);
        this.showOps(selected);
    }

    private void showOps(BaseObject selected)
    {
        if ( selected instanceof AddressBean )
        {

        }
        else if ( selected instanceof PhoneBean )
        {

        }
        else if ( selected instanceof EmailBean )
        {

        }
    }
}*/