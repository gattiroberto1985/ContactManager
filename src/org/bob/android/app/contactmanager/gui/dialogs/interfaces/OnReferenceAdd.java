package org.bob.android.app.contactmanager.gui.dialogs.interfaces;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.adapters.ACTVAdapter;
import org.bob.android.app.contactmanager.gui.fragments.CMDetailFragment;
import org.bob.android.app.contactmanager.persistence.beans.BaseObject;
import org.bob.android.app.contactmanager.persistence.beans.BeanFactory;
import org.bob.android.app.contactmanager.persistence.beans.ContactBean;
import org.bob.android.app.contactmanager.persistence.beans.RelationBean;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;

/**
 * Created by roberto.gatti on 05/01/2015.
 */
public class OnReferenceAdd implements DialogInterface.OnClickListener 
{
    private CMDetailFragment df;

    public OnReferenceAdd(CMDetailFragment df)
    {
        this.df = df;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int buttonPressed) {
        switch (buttonPressed) {
            case DialogInterface.BUTTON_POSITIVE: {
                this.insertReference((AlertDialog) dialogInterface);
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                //Log.e(TAG, "Scelta non valida!");
        }
        return;
    }

    private void insertReference(AlertDialog dialog)
    {
        //EditText et_name = (EditText) dialog.findViewById(R.id.dlg_new_contact_name);
        AutoCompleteTextView actv = (AutoCompleteTextView) dialog.findViewById(R.id.dlg_new_reference_value);
        BaseObject selected = ( (ACTVAdapter) actv.getAdapter() ).getSelected();
        if ( selected == null )
        {
            Logger.v(this.getClass(), "Nessun oggetto selezionato precedentemente. Procedo con il recupero o il suo censimento a db");
            // rilevo il tipo
            Spinner spinner = (Spinner) dialog.findViewById(R.id.dlg_new_reference_type);
            char type = ' ';
            try
            {
                String typeSelected = spinner.getSelectedItem().toString();
                String value = actv.getText().toString();
                for ( int i = 0; i < ApplicationCM.OBJECTS_TYPE_NAME.length ; i++ )
                {
                    if ( typeSelected.equals(ApplicationCM.OBJECTS_TYPE_NAME[i]) )
                    {
                        type = ApplicationCM.OBJECTS_TYPE_LABEL[i].charAt(0);
                        break;
                    }
                }
                assert type != ' ';
                selected = BeanFactory.createObject(type, value);

            }
            catch (NullPointerException ex)
            {

            }
        }
        Logger.v(this.getClass(), "Oggetto selezionato: " + selected.getDescription() + " -- " + selected.toString());
        //Toast.makeText(ApplicationCM.getInstance().getApplicationContext(), "Oggetto selezionato: " + selected.getDescription() + " -- " + selected.toString(), Toast.LENGTH_LONG).show();
        ContactBean contact = this.df.getSelectedContact();
        // Creo la relazione e la aggiungo al contatto
        RelationBean newRel = new RelationBean(-1, contact, selected);
        // procedo all'inserimento
        ApplicationCM.getInstance().getContentResolver().insert(DBConstants.RELATION_CONTENT_URI, newRel.getContentValues());
        this.df.refreshUI(contact);
    }
}
