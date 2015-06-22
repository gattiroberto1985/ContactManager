package org.bob.android.app.contactmanager.gui.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.persistence.beans.AddressBean;
import org.bob.android.app.contactmanager.persistence.beans.BaseObject;
import org.bob.android.app.contactmanager.persistence.beans.EmailBean;
import org.bob.android.app.contactmanager.persistence.beans.PhoneBean;
import org.bob.android.app.contactmanager.utilities.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roberto.gatti on 15/12/2014.
 */
public class LVAdapter extends ArrayAdapter<BaseObject>
{
    
    List<BaseObject> objs = new ArrayList<BaseObject>();

    /**
     * Oggetto context per il recupero di oggetti di sistema.
     */
    private Activity context;

    /**
     * Local reference al layoutinflater.
     */
    private LayoutInflater inflater;

    public static class ViewHolderList
    {
        public TextView tv_obj_id;
        public TextView tv_obj_desc;
        public ImageButton btn_one;
        public ImageButton btn_two;
    }

    public LVAdapter(Context context, List<BaseObject> objects)
    {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.inflater = ((Activity) context).getLayoutInflater();
        this.objs = objects;
    }


    @Override
    public void add(BaseObject object)
    {
        if ( this.objs == null)
        {
            Logger.e(this.getClass(), "ERRORE: WTF??? Are you trying to add object to null list???? Shame on you!!");
            throw new RuntimeException("Are you trying to add object to null list???? Shame on you!!");
        }
        this.objs.add(object);
    }

    @Override
    public int getCount()
    {
        return this.objs.size();
    }

    @Override
    public BaseObject getItem(int position)
    {
        return this.objs.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    @Override
    public int getPosition(BaseObject item)
    {
        if ( this.objs == null )
            return -1;
        if ( item == null )
            return -2;
        for ( int i = 0; i < this.objs.size(); i++ )
        {
            if ( this.objs.get(i).equals(item) ) return i;
        }
        return -3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Logger.v(this.getClass(), "getView su ListAdapterNoCursor");
        BaseObject object = this.getItem(position);
        ViewHolderList vh;
        if ( convertView == null )
        {
            vh = new ViewHolderList();
            convertView = this.inflater.inflate(R.layout.layout_simple_itemslist, parent, false);
            vh.tv_obj_id   = (TextView)    convertView.findViewById(R.id.view_simple_list_item_obj_id);
            vh.tv_obj_desc = (TextView)    convertView.findViewById(R.id.view_simple_list_item_obj_desc);
            vh.btn_one     = (ImageButton) convertView.findViewById(R.id.view_simple_list_item_obj_btn_one);
            vh.btn_two     = (ImageButton) convertView.findViewById(R.id.view_simple_list_item_obj_btn_two);
            vh.btn_one.setOnClickListener(new OnButtonReferenceClick());
            vh.btn_two.setOnClickListener(new OnButtonReferenceClick());
            this.decodeButtons(vh, object);
            convertView.setTag(vh);
        }
        vh = (ViewHolderList) convertView.getTag();
        vh.tv_obj_id.setText(String.valueOf(object.get_Id()));
        vh.tv_obj_desc.setText(object.getDescription());
        /*viewHolder.contact.setText(contact.getSurname() + ", " + contact.getName());
        viewHolder.contactId.setText(String.valueOf(contact.getId()));
        viewHolder.contactImage.setImageDrawable(this.context.getResources().getDrawable(R.drawable.contact_default_image));*/
        return convertView;
    }

    @Override
    public void clear()
    {
        this.objs.clear();
        this.notifyDataSetChanged();
    }

    /* ********************************************************************* */
    /*                               CLASS METHODS                           */
    /* ********************************************************************* */

    private void decodeButtons(ViewHolderList vh, BaseObject object)
    {
        if ( object instanceof AddressBean )
        {
            vh.btn_one.setImageDrawable(ApplicationCM.getInstance().getResources().getDrawable(R.drawable.drive_to));
            vh.btn_two.setVisibility(View.INVISIBLE);
        }
        else if ( object instanceof PhoneBean )
        {
            vh.btn_one.setImageDrawable(ApplicationCM.getInstance().getResources().getDrawable(R.drawable.call));
            vh.btn_two.setVisibility(View.VISIBLE);
            vh.btn_two.setImageDrawable(ApplicationCM.getInstance().getResources().getDrawable(R.drawable.sms));
            vh.btn_two.setTag(object);
        }
        else if ( object instanceof EmailBean )
        {
            vh.btn_one.setImageDrawable(ApplicationCM.getInstance().getResources().getDrawable(R.drawable.email));
            vh.btn_two.setVisibility(View.INVISIBLE);
        }
        vh.btn_one.setTag(object);
    }
}

class OnButtonReferenceClick implements View.OnClickListener
{
    @Override
    public void onClick(View buttonClicked)
    {
        BaseObject obj = (BaseObject) buttonClicked.getTag();
        int id = buttonClicked.getId();
        switch ( id )
        {
            case R.id.view_simple_list_item_obj_btn_one: this.firstButton(obj); break;
            case R.id.view_simple_list_item_obj_btn_two: this.secondButton(obj); break;
            default:
                return;
        }
    }

    private void firstButton(BaseObject obj)
    {
        if ( obj instanceof AddressBean )
        {
            Logger.i( this.getClass(), "First button su indirizzo: " + obj.getDescription() + " -- guido!");
            this.driveTo(obj.getDescription());
        }
        else if ( obj instanceof PhoneBean )
        {
            Logger.i( this.getClass(), "First button su telefono: " + obj.getDescription() + " -- chiamo");
            this.call(obj.getDescription());
        }
        else if ( obj instanceof EmailBean )
        {
            Logger.i( this.getClass(), "First button su mail: " + obj.getDescription() + " -- scrivo ");
            this.sendEmailTo(obj.getDescription());
        }

    }

    private void secondButton(BaseObject obj)
    {
        Logger.i( this.getClass(),  "Second button su telefono: " + obj.getDescription() + " -- scrivo sms");
        this.sendSMS(obj.getDescription());

    }

    /**
     * Il metodo esegue la chiamata al numero selezionato, se l'activity e'
     * esistente.
     * @param phoneNumber
     */
    public void call(String phoneNumber)
    {
        try
        {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            ApplicationCM.getInstance().startActivity(callIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Logger.e(this.getClass(), "ERRORE: impossibile avviare la chiamata! Activity not found: " + e.getMessage());
        }
    }

    /**
     * Metodo di invio di un sms al numero selezionato. Esegue lo start dell'activity di default
     * per la gestione degli sms.
     * @param phoneNumber
     */
    public void sendSMS(String phoneNumber)
    {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body",ApplicationCM.getInstance().getResources().getString(R.string.DEFAULT_SMS_CONTENT));
        ApplicationCM.getInstance().startActivity(smsIntent);
    }


    public void driveTo(String address)
    {
        String driveTo = address.replace(" ", "+");
        driveTo = "https://www.google.it/maps/place/" + driveTo;
        Intent driveIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(driveTo));
        //driveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationCM.getInstance().startActivity(Intent.createChooser(driveIntent, "See on maps").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void sendEmailTo(String mailTo)
    {
        Intent email_intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",mailTo, null));
        //email_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ApplicationCM.getInstance().startActivity(Intent.createChooser(email_intent, "Send email...").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}