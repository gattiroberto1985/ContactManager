package org.bob.android.app.contactmanager.gui.dialogs.interfaces;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.EditText;
import android.widget.Toast;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.activities.MainActivity;
import org.bob.android.app.contactmanager.gui.fragments.CMDetailFragment;
import org.bob.android.app.contactmanager.persistence.beans.BeanFactory;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by roberto.gatti on 05/01/2015.
 */
public class OnContactEdit implements DialogInterface.OnClickListener
{

    private CMDetailFragment df;

    /**
     * References per le edittext.
     */
    private EditText et_name, et_surname, et_birthday;

    public OnContactEdit(CMDetailFragment df, EditText name, EditText surname, EditText birthday)
    {
        this.df = df;
        this.et_name = name;
        this.et_surname = surname;
        this.et_birthday = birthday;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int buttonPressed) {
        switch (buttonPressed) {
            case DialogInterface.BUTTON_POSITIVE: {
                this.editContact((AlertDialog) dialogInterface);
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                //Log.e(TAG, "Scelta non valida!");
        }
        return;
    }

    /**
     *
     * @param dialog
     */
    private void editContact(AlertDialog dialog)
    {
        Logger.i_lfc("Tentativo modifica dati di testata del contatto");
        ContactBean oldCnt = df.getSelectedContact();
        ContactBean newCnt = null;
        String name = "", surname = "";
        long birthday = -1L;
        try
        {
            name = et_name.getText().toString();
            surname = et_surname.getText().toString();
            birthday = Utilities.DATE_FORMATTER.parse(et_birthday.getText().toString());
            newCnt = new ContactBean(oldCnt.getId(), surname, name, birthday);
            int id = BeanFactory.exists(newCnt);
            if ( id > 1 )
            {
                Toast.makeText(this.df.getActivity(), df.getString(R.string.DIALOG_WARNING_CONTACT_EXISTS) + String.valueOf(id), Toast.LENGTH_LONG).show();
            }
            else
            {
                Logger.i(this.getClass(), "Procedo con l'aggiornamento dei dati...");
                BeanFactory.update(newCnt);
            }
        }
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



    }
}
