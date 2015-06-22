package org.bob.android.app.contactmanager.gui.dialogs.interfaces;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.ExpandableListAdapter;
import android.widget.Toast;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.gui.adapters.ExpListAdapter;
import org.bob.android.app.contactmanager.gui.fragments.CMListFragment;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;

/**
 * Created by roberto.gatti on 05/01/2015.
 */
public class OnContactDelete implements DialogInterface.OnClickListener
{

    private CMListFragment lf;
    private int id2remove;

    public OnContactDelete(CMListFragment lf, int id2remove)
    {
        this.lf = lf;
        this.id2remove = id2remove;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int buttonPressed)
    {
        switch (buttonPressed)
        {
            case DialogInterface.BUTTON_POSITIVE:
            {
                this.deleteContact((AlertDialog) dialogInterface);
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                //Log.e(TAG, "Scelta non valida!");
        }
        return;
    }

    private void deleteContact(AlertDialog dialog)
    {
        Logger.i(this.getClass(), "Procedo con la rimozione del contatto con id " + id2remove);
        int removed = ApplicationCM.getInstance().getContentResolver().delete(Uri.parse(DBConstants.RELATION_CONTACT_ID_URI + "/" + id2remove), null, null);
        Logger.i(this.getClass(), "Rimozione eseguita: righe rimosse: " + removed);
        Logger.i(this.getClass(), "Eseguo la rimozione delle relazioni del contatto");
        removed = ApplicationCM.getInstance().getContentResolver().delete(Uri.parse(DBConstants.CONTACT_CONTENT_URI + "/" + id2remove), null, null);
        Logger.i(this.getClass(), "Rimozione eseguita: relazioni rimosse: " + removed);
        ( (ExpListAdapter)this.lf.getListAdapter()).removeChild(id2remove);
        Toast.makeText(ApplicationCM.getInstance().getApplicationContext(), "Contatto rimosso con successo!", Toast.LENGTH_LONG).show();
    }

}
