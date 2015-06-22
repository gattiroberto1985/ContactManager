package org.bob.android.app.contactmanager.gui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.activities.MainActivity;
import org.bob.android.app.contactmanager.persistence.beans.BaseObject;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.Logger;

import java.io.Serializable;
import java.util.*;

/**
 *
 * Adapter per la expandablelistview con la lista dei contatti censiti
 * nell'applicazione.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class ExpListAdapter extends BaseExpandableListAdapter 
                            implements    SearchView.OnQueryTextListener
                                        , SearchView.OnCloseListener
{
    /**
     * Reference al context relativo all'adapter.
     */
    private Context context;

    /**
     * Lista con gli oggetti da mostrare nella explv
     */
    private ArrayList<ExtendedHeader> datas = new ArrayList<ExtendedHeader>(0);

    /**
     * Lista con gli oggetti da mostrare nella explv
     */
    private ArrayList<ExtendedHeader> backup = new ArrayList<ExtendedHeader>(0);

    /**
     * Per il controllo sulla dimensione della stringa da filtrare.
     */
    private int SIZE = 0;

    /**
     * Holder per la view relativa al singolo item.
     */
    public static class ContactViewHolder
    {
        public TextView contact_tv;
        public TextView contact_id_tv;
    };

    /* ********************************************************************* */
    /*                               CONSTRUCTORS                            */
    /* ********************************************************************* */

    /**
     * Costruttore dell'adapter.
     * @param c
     */
    public ExpListAdapter(Context c)
    {
        this.context = c;
    }

    public void addElement(Serializable element)
    {
        String newHeader = ((ContactBean) element).getSurname().substring(0,1).toUpperCase();
        ExtendedHeader eh = new ExtendedHeader(newHeader);
        int indexOfHeader =  this.datas.indexOf(eh);
        if ( indexOfHeader < 0 )
        {
            Logger.i(this.getClass(), "|-- Aggiungo l'header '" + newHeader + "' alla explistview...");
            this.datas.add(eh);
            this.backup.add(new ExtendedHeader(newHeader)); // altrimenti mi condividono l'istanza e si spacca tutto quando filtro!!
            indexOfHeader = this.datas.size() == 0 ? 0 : this.datas.size() - 1;
        }
        this.datas.get(indexOfHeader).addContactBean((ContactBean)element);
        this.backup.get(indexOfHeader).addContactBean((ContactBean)element);
    }

    public void removeChild(int id2remove)
    {
        boolean removed = false;
        int j = 0;
        for ( ExtendedHeader eh : this.datas )
        {
            for ( ContactBean cb : eh.getList() )
            {
                int i = 0;
                if ( cb.getId() == id2remove )
                {
                    eh.getList().remove(i);
                    this.backup.get(j).getList().remove(i);
                    removed = true;
                    break;
                }
                else i++;
            }
            j++;
            if ( removed ) break;
        }
        this.notifyDataSetChanged();
    }

    /* ********************************************************************* */
    /*                BASEEXPANDABLELISTADAPTER IMPLEMENTS                   */
    /* ********************************************************************* */

    /**
     * Ritorna il numero di headers della expandablelistview.
     * @return
     */
    @Override
    public int getGroupCount()
    {
        return this.datas.size();
    }

    /**
     * Ritorna il numero di oggetti nel gruppo specificato come parametro.
     * @param i
     * @return
     */
    @Override
    public int getChildrenCount(int i)
    {
        return this.datas.get(i).getList().size();
    }

    /**
     * Ritorna un'oggetto (in questo caso una stringa) relativa al gruppo
     * specificato come parametro.
     * @param i
     * @return
     */
    @Override
    public Object getGroup(int i)
    {
        return this.datas.get(i);
    }

    /**
     * Ritorna l'elemento childPos-esimo della lista groupPos-esima.
     * @param groupPos
     * @param childPos
     * @return
     */
    @Override
    public Object getChild(int groupPos, int childPos)
    {
        return this.datas.get(groupPos).getList().get(childPos);
    }


    /**
     * Ritorna la posizione del gruppo groupPos.
     * @param groupPos
     * @return
     */
    @Override
    public long getGroupId(int groupPos)
    {
        return groupPos;
    }


    /**
     * Ritorna la posizione dell'elemnto figlio childPos-esimo.
     * @param groupPos
     * @param childPos
     * @return
     */
    @Override
    public long getChildId(int groupPos, int childPos)
    {
        return childPos;
    }


    /**
     * Boh...
     * @return
     */
    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    /**
     *
     * @param groupPos
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent)
    {
        ExtendedHeader headerTitle = (ExtendedHeader) this.getGroup(groupPos);
        if (convertView == null)
        {
            convertView = ApplicationCM.getLayoutInflater().inflate(R.layout.view_explist_parent, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.exp_list_tv_parent);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getHeader());

        return convertView;
    }

    /**
     * Ritorna la view relativa al childPos-esimo oggetto.
     * @param groupPos
     * @param childPos
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView, ViewGroup parent)
    {
        //final Serializable obj = (Serializable) this.getChild(groupPos, childPos);
        final ContactBean cnt = (ContactBean) this.getChild(groupPos, childPos);
        ContactViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = ApplicationCM.getLayoutInflater().inflate(R.layout.view_explist_child, null);
            viewHolder = new ContactViewHolder();
            viewHolder.contact_tv = (TextView) convertView.findViewById(R.id.view_exp_list_tv_contact);
            viewHolder.contact_id_tv = (TextView) convertView.findViewById(R.id.view_exp_list_tv_contact_id);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ContactViewHolder) convertView.getTag();
        viewHolder.contact_tv.setText(cnt.getSurname() + ", " + cnt.getName());
        viewHolder.contact_id_tv.setText(String.valueOf(cnt.getId()));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPos, int childPos)
    {
        return true;
    }

    @Override
    public void notifyDataSetChanged()
    {
        ((MainActivity)this.context).setFlagLoadedContacts();
        super.notifyDataSetChanged();
    }

    
	/* ********************************************************************* */
	/*                   OVERRIDING SearchView methods                       */        
	/* ********************************************************************* */


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        this.filterData(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        this.filterData(newText);
        return true;
    }

    @Override
    public boolean onClose()
    {
        this.filterData("");
        return true;
    }    
    
    /* ********************************************************************* */
    /*                            CLASS METHODS                              */
    /* ********************************************************************* */

    public void filterData(String query)
    {
        ArrayList<ExtendedHeader> newlist = new ArrayList<ExtendedHeader>();
        if ( query == null || query.equals("") )
        {
            Logger.v(this.getClass(),  "Resetto la vista...");
            this.datas.clear();
            this.datas.addAll(this.backup);
            this.notifyDataSetChanged();
            return;
        }

        String valueToCheck = query.toString().toUpperCase();

        // controllo la lunghezza della stringa: se minore riparto dall'inizio
        if ( this.SIZE > query.length() )
        {
            this.datas.clear();
            this.datas.addAll(this.backup);
        }
        this.SIZE = query.length();

        /*this.datas = new HashMap<String, List<Serializable>>();
        this.headers = new ArrayList<String>();*/

        for (ExtendedHeader eh : this.datas )
        {
            int j = 0;
            int indexOfEH = newlist.indexOf(eh);
            for ( ContactBean cb : eh.getList() )
            {
                String fullfield = cb.getSurname() + " " + cb.getName() + " " + cb.getSurname();
                if (fullfield.toUpperCase().contains(valueToCheck) )
                {
                    if ( indexOfEH == -1 )
                    {
                        newlist.add(new ExtendedHeader(eh.getHeader()));
                        indexOfEH = newlist.size() - 1;
                    }
                    newlist.get(indexOfEH).getList().add(cb);
                }
            }
        }

        Logger.v(this.getClass(),  "|--> Match ok su " + newlist.size() + " contatti!");
        this.datas = newlist;
        this.notifyDataSetChanged();
    }    
}

class ExtendedHeader
{

    private String header = "";
    List<ContactBean> beans = new ArrayList<ContactBean>(0);

    public ExtendedHeader(String header) { this.setHeader(header); }

    public void addContactBean(ContactBean cb)
    {
        this.beans.add(cb);
    }

    public void setHeader(String header) { this.header = header; }
    public String getHeader() { return this.header; }

    public List<ContactBean> getList() { return this.beans; }

    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof ExtendedHeader )
        {
            ExtendedHeader eh = (ExtendedHeader) obj;
            if ( eh.getHeader().equals(this.getHeader()) )
                return true;
        }
        if ( obj instanceof String )
        {
            if ( this.getHeader().equals(obj.toString()))
                return true;
        }
        return false;
    }
}