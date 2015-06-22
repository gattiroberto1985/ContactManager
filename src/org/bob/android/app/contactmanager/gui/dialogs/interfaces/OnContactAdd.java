package org.bob.android.app.contactmanager.gui.dialogs.interfaces;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.activities.MainActivity;
import org.bob.android.app.contactmanager.gui.fragments.CMListFragment;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by roberto.gatti on 05/01/2015.
 */
public class OnContactAdd implements DialogInterface.OnClickListener
{

    private CMListFragment lf;

    public OnContactAdd(CMListFragment lf)
    {
        this.lf = lf;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int buttonPressed) {
        switch (buttonPressed) {
            case DialogInterface.BUTTON_POSITIVE: {
                this.insertContact((AlertDialog) dialogInterface);
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                //Log.e(TAG, "Scelta non valida!");
        }
        return;
    }

    private void insertContact(AlertDialog dialog)
    {
        EditText et_name = (EditText) dialog.findViewById(R.id.dlg_new_contact_name);
        EditText et_surname = (EditText) dialog.findViewById(R.id.dlg_new_contact_surname);
        EditText et_birthday = (EditText) dialog.findViewById(R.id.dlg_new_contact_birthday);
        String name = "", surname = "";
        Date birthday = new Date();
        try
        {
            name = et_name.getText().toString();
            surname = et_surname.getText().toString();
            birthday = Utilities.DATE_FORMATTER.parse(et_birthday.getText().toString());
        }
        /*catch (NullPointerException ex )
        {
            Toast.makeText(this.lf.getActivity(), lf.getString(R.string.DIALOG_ERROR_EMPTY_FIELD), Toast.LENGTH_LONG).show();
            dialog.dismiss();
            return;
        }*/
        catch (ParseException ex)
        {
            if ( et_birthday.getText().toString().length() == 0 )
            {
                Toast.makeText(this.lf.getActivity(), lf.getString(R.string.DIALOG_WARNING_NULL_DATE), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this.lf.getActivity(), lf.getString(R.string.DIALOG_ERROR_UNPARSABLE_DATE), Toast.LENGTH_LONG).show();
                dialog.dismiss();
                return;
            }
        }
        ContactBean cb = new ContactBean(-1, surname, name, birthday.getTime());
        Logger.i(this.getClass(), "Contatto creato, tento l'inserimento a db");
        Uri newins = this.lf.getActivity().getContentResolver().insert(DBConstants.CONTACT_CONTENT_URI, cb.getContentValues());
        int newid = Integer.parseInt(newins.getLastPathSegment().toString());

        if ( newid > 0 )
        {
            // Apro il contatto
            Logger.v(this.getClass(), "Apro l'activity di dettaglio");
            this.lf.getListAdapter().addElement(/*cb.getSurname().substring(0,1).toUpperCase(),*/ cb);
            this.lf.getListAdapter().notifyDataSetChanged();
            cb.setId(newid);
            ( (MainActivity) this.lf.getActivity()).onContactSelected(cb);
        }
    }
}
