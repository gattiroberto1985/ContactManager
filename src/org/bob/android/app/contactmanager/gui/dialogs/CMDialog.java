package org.bob.android.app.contactmanager.gui.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import org.bob.android.app.contactmanager.ApplicationCM;
import org.bob.android.app.contactmanager.R;
import org.bob.android.app.contactmanager.gui.adapters.ACTVAdapter;
import org.bob.android.app.contactmanager.gui.dialogs.interfaces.OnContactAdd;
import org.bob.android.app.contactmanager.gui.dialogs.interfaces.OnContactDelete;
import org.bob.android.app.contactmanager.gui.dialogs.interfaces.OnContactEdit;
import org.bob.android.app.contactmanager.gui.dialogs.interfaces.OnReferenceAdd;
import org.bob.android.app.contactmanager.gui.dialogs.interfaces.OnReferenceDelete;
import org.bob.android.app.contactmanager.gui.fragments.CMDetailFragment;
import org.bob.android.app.contactmanager.gui.fragments.CMListFragment;
import org.bob.android.app.contactmanager.persistence.beans.BaseObject;
import org.bob.android.app.contactmanager.persistence.beans.EmailBean;
import org.bob.android.app.contactmanager.persistence.beans.PhoneBean;
import org.bob.android.app.contactmanager.tasks.ATRetrieveObjects;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;

/**
 * Created by roberto.gatti on 05/01/2015.
 */
public class CMDialog
{

    /**
     * Il metodo mostra un dialog frame per la modifica dei dati di testata
     * del contatto.
     *
     * @lf
     * @return
     */
    public static AlertDialog showEditContactHeaderDialog(CMDetailFragment df)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(df.getActivity());
        builder.setTitle(df.getResources().getString(R.string.DIALOG_TITLE_EDIT_CONTACT_HEADER));
        View dialogView = (df.getActivity().getLayoutInflater()).inflate(R.layout.dialog_new_contact, null);
        EditText name = (EditText) dialogView.findViewById(R.id.dlg_new_contact_name);
        EditText surname = ((EditText) dialogView.findViewById(R.id.dlg_new_contact_surname) );
        EditText birthday = ((EditText) dialogView.findViewById(R.id.dlg_new_contact_birthday) );
        name.setText(df.getSelectedContact().getName());
        surname.setText(df.getSelectedContact().getSurname());
        birthday.setText(Utilities.DATE_FORMATTER.format(new java.util.Date(df.getSelectedContact().getBirthday())));
        builder.setView(dialogView);
        OnContactEdit listener = new OnContactEdit(df, name,surname,birthday);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);
        return builder.create();
    }

    public static AlertDialog showNewContactDialog(CMListFragment lf)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(lf.getActivity());
        builder.setTitle(lf.getResources().getString(R.string.DIALOG_TITLE_NEW_CONTACT));
        View dialogView = (lf.getActivity().getLayoutInflater()).inflate(R.layout.dialog_new_contact, null);
        builder.setView(dialogView);
        OnContactAdd listener = new OnContactAdd(lf);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);
        return builder.create();
    }


    public static AlertDialog showNewReferenceDialog(CMDetailFragment df)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(df.getActivity());
        builder.setTitle(df.getResources().getString(R.string.DIALOG_TITLE_NEW_REFERENCE));
        View dialogView = (df.getActivity().getLayoutInflater()).inflate(R.layout.dialog_new_reference, null);
        Spinner refType = (Spinner) dialogView.findViewById(R.id.dlg_new_reference_type);
        final AutoCompleteTextView refValue = (AutoCompleteTextView) dialogView.findViewById(R.id.dlg_new_reference_value);
        refValue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3)
            {
                BaseObject bo = (BaseObject) adapterView.getItemAtPosition(position);
                ((ACTVAdapter) adapterView.getAdapter()).setSelected(bo);
                refValue.setText(bo.getDescription());
            }
        });
        String[] datas = df.getResources().getStringArray(R.array.DEFAULT_TYPE_VALUES);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(df.getActivity(), android.R.layout.simple_spinner_item, datas);
        refType.setAdapter(spinnerAdapter);
        refType.setOnItemSelectedListener(new OnReferenceTypeSelected(refValue));
        builder.setView(dialogView);
        OnReferenceAdd listener = new OnReferenceAdd(df);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);
        return builder.create();
    }

    public static AlertDialog showDeleteReferenceDialog(CMDetailFragment df)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationCM.getInstance().getApplicationContext());
		builder.setTitle(ApplicationCM.getInstance().getResources().getString(R.string.DIALOG_TITLE_REMOVE_REFERENCE));
        View dialogView = (ApplicationCM.getInstance().getLayoutInflater()).inflate(android.R.layout.simple_dropdown_item_1line, null);
        builder.setView(dialogView);
        OnReferenceDelete listener = new OnReferenceDelete(df);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);
        return builder.create();
//		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//		           public void onClick(DialogInterface dialog, int id)
//		           {
//		        	   /*Logger.v(this.getClass(), "|--> Uri passato per la delete: " + ExpensePMD.CONTENT_URI + "/" + e.getId() );
//		               activity.getContentResolver().delete(Uri.parse(ExpensePMD.CONTENT_URI + "/" + e.getId()), null, null);
//		               //adapter.getCursor().requery();
//		               activity.getLoaderManager().getLoader(ExpenseListFragment.URL_LOADER).forceLoad();
//		               adapter.notifyDataSetChanged();*/
//		           }
//		       });
//		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//		           public void onClick(DialogInterface dialog, int id)
//		           {
//		               /*Log.i(TAG, "Operazione di cancellazione spesa annullata!");
//		               return;*/
//		           }
//		       });
		/*AlertDialog dialog = builder.create();
		dialog.show();*/
    }

    public static AlertDialog showDeleteContactDialog(CMListFragment lf, int id2remove)
    {
        //AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationCM.getInstance().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(lf.getActivity());
        builder.setTitle(ApplicationCM.getInstance().getResources().getString(R.string.DIALOG_TITLE_REMOVE_CONTACT));
        View dialogView = (ApplicationCM.getInstance().getLayoutInflater()).inflate(android.R.layout.simple_dropdown_item_1line, null);
        TextView tv = ((TextView) dialogView.findViewById(android.R.id.text1));
        tv.setSingleLine(false);
        tv.setText(ApplicationCM.getInstance().getResources().getString(R.string.DIALOG_DESCR_REMOVE_CONTACT));
        builder.setView(dialogView);
        OnContactDelete listener = new OnContactDelete(lf, id2remove);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);
        return builder.create();
//		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//		           public void onClick(DialogInterface dialog, int id)
//		           {
//		        	   /*Logger.v(this.getClass(), "|--> Uri passato per la delete: " + ExpensePMD.CONTENT_URI + "/" + e.getId() );
//		               activity.getContentResolver().delete(Uri.parse(ExpensePMD.CONTENT_URI + "/" + e.getId()), null, null);
//		               //adapter.getCursor().requery();
//		               activity.getLoaderManager().getLoader(ExpenseListFragment.URL_LOADER).forceLoad();
//		               adapter.notifyDataSetChanged();*/
//		           }
//		       });
//		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//		           public void onClick(DialogInterface dialog, int id)
//		           {
//		               /*Log.i(TAG, "Operazione di cancellazione spesa annullata!");
//		               return;*/
//		           }
//		       });
		/*AlertDialog dialog = builder.create();
		dialog.show();*/
    }

}

class OnReferenceTypeSelected implements AdapterView.OnItemSelectedListener
{

    private AutoCompleteTextView actv;

    OnReferenceTypeSelected(AutoCompleteTextView actv) { this.actv = actv; }

    private ATRetrieveObjects bgTask;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Toast.makeText(view.getContext(), "Selezionato: " + parent.getItemAtPosition(pos), Toast.LENGTH_LONG).show();
        String selected = parent.getItemAtPosition(pos).toString();
        String[] allValues = ApplicationCM.getInstance().getResources().getStringArray(R.array.DEFAULT_TYPE_VALUES);
        String[] allChars  = ApplicationCM.getInstance().getResources().getStringArray(R.array.DEFAULT_TYPE_CHARS);
        char objType = 'X';
        for ( int i = 0; i < allValues.length; i++ )
        {
            if ( allValues[i].equals(selected) ) objType = allChars[i].charAt(0);
        }
        Logger.v(this.getClass(), "Selezionato " + parent.getItemAtPosition(pos) + ", faccio partire il thread di recupero dati...");
        if ( this.bgTask != null )
        {
            Logger.v(this.getClass(), "|-- ATTENZIONE: interrompo il thread precedente");
            this.bgTask.cancel(true);
            this.actv.setAdapter(null);
            switch ( objType )
            {
                case Constants.EMAIL_TYPE_INDICATOR:   this.actv.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS); break;
                case Constants.PHONE_TYPE_INDICATOR:   this.actv.setInputType(InputType.TYPE_CLASS_PHONE); break;
                default: this.actv.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        }
        this.bgTask = new ATRetrieveObjects(this.actv, objType);
        // Avvia
        this.bgTask.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}


