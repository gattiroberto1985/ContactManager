package org.bob.android.app.contactmanager.persistence.beans;

import android.content.ContentValues;
import org.bob.android.app.contactmanager.utilities.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Oggetto base dell'applicazione. Tutti i bean generati dall'app devono
 * estendere questa classe.
 * Fornisce metodi di raggruppamento dei parametri e di utilita' generica
 * sui bean usati in fase di runtime.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public abstract class BaseObject implements Serializable
{

    private int _id;

    /**
     * HashMap con i dati del bean. Contiene il nome del campo e
     * il suo valore, registrato come Object.
     */
    private HashMap<String, Object> obj_fields = new HashMap<String, Object>();

    /**
     * Il metodo ritorna un array di stringhe con la full projection del bean.
     *
     * @return
     */
    public final String[] getFullProjection()
    {
        String[] projection = new String[this.obj_fields.size()];
        int i = 0;
        for (Map.Entry entry : this.obj_fields.entrySet() )
        {
            Logger.i(this.getClass(), "getFullProjection: adding column '" + entry.getKey().toString() + "'");
            projection[i++] = entry.getKey().toString();
        }
        return projection;
        /*try
        {
            // System.out.println(this.getClass().getDeclaredMethod(
            // "getBonusMultiplier").toString());
            try
            {
                return (String[]) this.getClass().getDeclaredMethod("getFullProjection").invoke(this);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        return null;*/
    }

    public final int get_Id()
    {
        return this._id;
    }

    protected final void set_Id(int _id)
    {
        this._id = _id;
    }

    public abstract void setId(int id);

    public abstract int getId();

    /**
     * Ritorna una descrizione del bean, nella forma
     * [ nome_campo: valore_campo, ... ]
     * @return
     */
    public abstract String getDescription();

    public abstract ContentValues getContentValues();

}
