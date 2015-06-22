package org.bob.android.app.contactmanager.gui.dialogs.interfaces;

import android.app.AlertDialog;
import android.content.DialogInterface;
import org.bob.android.app.contactmanager.gui.fragments.CMDetailFragment;
import org.bob.android.app.contactmanager.gui.fragments.CMListFragment;

/**
 * Created by roberto.gatti on 05/01/2015.
 */
public class OnReferenceDelete implements DialogInterface.OnClickListener
{

    private CMDetailFragment df;

    public OnReferenceDelete(CMDetailFragment df)
    {
        this.df = df;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int buttonPressed)
    {
        switch (buttonPressed)
        {
            case DialogInterface.BUTTON_POSITIVE:
            {
                this.deleteReferenceFromContact((AlertDialog) dialogInterface);
                break;
            }
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                //Log.e(TAG, "Scelta non valida!");
        }
        return;
    }

    private void deleteReferenceFromContact(AlertDialog dialog)
    {

    }

}
