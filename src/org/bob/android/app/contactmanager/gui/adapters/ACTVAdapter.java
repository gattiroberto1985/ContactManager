package org.bob.android.app.contactmanager.gui.adapters;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.persistence.beans.BaseObject;
import org.bob.android.app.contactmanager.utilities.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roberto on 06/01/15.
 */
public class ACTVAdapter extends ArrayAdapter<BaseObject> implements Filterable
{

    private BaseObject selectedObj;

    private List<BaseObject> allObjects;

    private List<BaseObject> filtered;

    /**
     * Local reference al layoutinflater.
     */
    private LayoutInflater inflater;

    private ArrayFilter filter;

    public static class ViewHolderList
    {
        public TextView tv_ref_id;
        public TextView tv_ref_desc;
    }

    public ACTVAdapter(Context context, List<BaseObject> objects)
    {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.inflater = (LayoutInflater) ApplicationCM.getInstance().getApplicationContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        this.allObjects = objects;
    }


    @Override
    public void add(BaseObject object)
    {
        super.add(object);
        /*if ( this.objects == null)
        {
            Logger.e(this.getClass(), "ERRORE: WTF??? Are you trying to add object to null list???? Shame on you!!");
            throw new RuntimeException("Are you trying to add object to null list???? Shame on you!!");
        }
        this.objects.add(object);*/
    }

    @Override
    public int getCount()
    {
        return this.allObjects.size();
    }

    @Override
    public BaseObject getItem(int position)
    {
        return this.allObjects.get(position);
    }


    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    @Override
    public int getPosition(BaseObject item)
    {
        return super.getPosition(item);
        /*if ( this.objects == null )
            return -1;
        if ( item == null )
            return -2;
        for ( int i = 0; i < this.objects.size(); i++ )
        {
            if ( this.objects.get(i).equals(item) ) return i;
        }
        return -3;*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Logger.v(this.getClass(), "getView su ACTVAdapter");
        BaseObject object = (BaseObject) this.getItem(position);
        ViewHolderList vh;
        if ( convertView == null )
        {
            vh = new ViewHolderList();
            convertView = this.inflater.inflate(R.layout.view_actv_suggest_row, parent, false);
            vh.tv_ref_id   = (TextView) convertView.findViewById(R.id.dlg_actv_ref_id);
            vh.tv_ref_desc = (TextView) convertView.findViewById(R.id.dlg_actv_ref_value);
            convertView.setTag(vh);
        }
        vh = (ViewHolderList) convertView.getTag();
        vh.tv_ref_id.setText(String.valueOf(object.get_Id()));
        vh.tv_ref_desc.setText(object.getDescription());
        /*viewHolder.contact.setText(contact.getSurname() + ", " + contact.getName());
        viewHolder.contactId.setText(String.valueOf(contact.getId()));
        viewHolder.contactImage.setImageDrawable(this.context.getResources().getDrawable(R.drawable.contact_default_image));*/
        return convertView;
    }

    @Override
    public void clear()
    {
        super.clear();
        //this.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (this.filter == null){
            this.filter = new ArrayFilter();
        }
        return this.filter;
    }

    public void setSelected(BaseObject ob) { this.selectedObj = ob; }

    public BaseObject getSelected() { return this.selectedObj; }

    class ArrayFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();

            if ( ACTVAdapter.this.filtered == null) 
            {
                ACTVAdapter.this.filtered = new ArrayList<BaseObject>(ACTVAdapter.this.allObjects);
            }

            if (constraint != null && constraint.length() != 0)
            {
                ArrayList<BaseObject> resultsSuggestions = new ArrayList<BaseObject>();
                for (int i = 0; i < ACTVAdapter.this.filtered.size(); i++)
                {
                    if(ACTVAdapter.this.filtered.get(i).getDescription().toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        resultsSuggestions.add(ACTVAdapter.this.filtered.get(i));
                    }
                }

                results.values = resultsSuggestions;
                results.count = resultsSuggestions.size();

            }
            else {
                ArrayList <BaseObject> list = new ArrayList <BaseObject>(ACTVAdapter.this.filtered);
                results.values = list;
                results.count = list.size();
            }
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            clear();
            ArrayList<BaseObject> newValues = (ArrayList<BaseObject>) results.values;
            if(newValues !=null)
            {
                for (int i = 0; i < newValues.size(); i++)
                {
                    add(newValues.get(i));
                }
                if(results.count>0)
                {
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }

        }

    }


}

