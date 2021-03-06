package org.bob.android.app.contactmanager.persistence.beans;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by roberto.gatti on 11/12/2014.
 */
@DatabaseTable(tableName = DBConstants.CONTACT_TABLE_NAME)
public class ContactBean extends BaseObject
{

    public static final String[] PROJECTION = new String[4] ;

    // id is generated by the database and set on the object automagically
    @DatabaseField(generatedId = true, columnName = DBConstants.DEFAULT_ID_FIELD_NAME)
    private int id;

    @DatabaseField(index = true, columnName = DBConstants.CONTACT_SURNAME_FIELD_NAME, dataType = DataType.STRING)
    private String surname;

    @DatabaseField(columnName = DBConstants.CONTACT_NAME_FIELD_NAME, dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = DBConstants.CONTACT_BIRTHDAY_FIELD_NAME, dataType = DataType.LONG)
    private long birthday;

    @DatabaseField(columnName = DBConstants.DEFAULT_TMST_UPD_FIELD_NAME, dataType = DataType.LONG)
    private long tmst_upd;

    @DatabaseField(columnName = DBConstants.DEFAULT_TMST_INS_FIELD_NAME, dataType = DataType.LONG)
    private long tmst_ins;

    private List<RelationBean> relations = new ArrayList<RelationBean>();

    static
    {
        PROJECTION[0] = DBConstants.DEFAULT_ID_FIELD_NAME;
        PROJECTION[1] = DBConstants.CONTACT_NAME_FIELD_NAME;
        PROJECTION[2] = DBConstants.CONTACT_SURNAME_FIELD_NAME;
        PROJECTION[3] = DBConstants.CONTACT_BIRTHDAY_FIELD_NAME;
    }

    /* ********************************************************************* */
    /*                              CONSTRUCTOR                              */
    /* ********************************************************************* */

    /**
     * Costruttore di default (per ormlite)
     */
    public ContactBean() { }

    /**
     * Costruttore col solo id.
     * @param id
     */
    public ContactBean(int id)
    {
        this.setId(id);
    }

    /**
     * Costruttore full.
     * @param id
     * @param surname
     * @param name
     * @param birthday
     */
    public ContactBean(int id, String surname, String name, long birthday)
    {
        this.setId(id);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
    }

    /**
     * Costruttore per il recupero dell'oggetto da db.
     * @param cursor
     */
    public ContactBean(Cursor cursor)
    {
        if ( cursor != null )
        {
            this.setId(cursor.getInt(cursor.getColumnIndex(DBConstants.DEFAULT_ID_FIELD_NAME)));
            this.setSurname(cursor.getString(cursor.getColumnIndex(DBConstants.CONTACT_SURNAME_FIELD_NAME)));
            this.setName(cursor.getString(cursor.getColumnIndex(DBConstants.CONTACT_NAME_FIELD_NAME)));
            this.setBirthday(cursor.getLong(cursor.getColumnIndex(DBConstants.CONTACT_BIRTHDAY_FIELD_NAME)));
        }
    }

    /**
     * Costruttore porcata legato all'integrazione ORMLite - ContentProvider...
     * @param cv
     */
    public ContactBean(ContentValues cv)
    {
        this.setBirthday((Long) cv.get(DBConstants.CONTACT_BIRTHDAY_FIELD_NAME));
        this.setName(cv.get(DBConstants.CONTACT_NAME_FIELD_NAME).toString());
        this.setSurname(cv.get(DBConstants.CONTACT_SURNAME_FIELD_NAME).toString());
        if ( cv.get(DBConstants.DEFAULT_ID_FIELD_NAME) != null )
            this.setId(Integer.parseInt(cv.get(DBConstants.DEFAULT_ID_FIELD_NAME).toString()));
    }

    /* ********************************************************************* */
    /*                             CLASS METHODS                             */
    /* ********************************************************************* */

    public void addRelation(RelationBean line)
    {
        this.relations.add(line);
    }

    /* ********************************************************************* */
    /*                           SETTER AND GETTER                           */
    /* ********************************************************************* */

    @Override
    public void setId(int id) { this.id = id;  super.set_Id(this.id); }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setBirthday(long date) { this.birthday = date; }
    public void setRelations(ArrayList<RelationBean> rels) { this.relations = rels; }

    @Override
    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public String getSurname() { return this.surname; }
    public long getBirthday() { return this.birthday; }
    public List<RelationBean> getRelations() { return this.relations; }
    

    
    /* ********************************************************************* */
    /*                          OVERRIDDEN METHODS                           */
    /* ********************************************************************* */

    /*       Object class     */

    @Override
    public int hashCode()
    {
        int hashcode = 1;
        // hashcode += ( Math.abs(this.getId()) * 11 );
        hashcode += (this.getName().toUpperCase().hashCode() * Utilities.STRING_MULTIPLIER);
        hashcode += (this.getSurname().toUpperCase().hashCode() * Utilities.STRING_MULTIPLIER);
        hashcode += (this.getBirthday() * Utilities.LONG_MULTIPLIER);
        return hashcode;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( o instanceof ContactBean )
        {
            ContactBean obj = (ContactBean) o;
            if ( obj == null )
            {
                Logger.v(this.getClass(),"ContactBean.equals: parametro a null!");
                return false;
            }
            // Se hanno l'id valorizzato controllo semplicemente l'hashcode
            if ( this.getId() > 0 && obj.getId() > 0)
            {
                Logger.v(this.getClass(),"|-- Id degli oggetti sono uguali? --> " + String.valueOf(this.getId() == obj.getId()));
                return this.getId() == obj.getId() ? true : false;
            }
            // ... altrimenti controllo l'hashcode
            Logger.v(this.getClass(),"|-- hashcode dei due oggetti: this: " + this.hashCode() + " -- param :" + obj.hashCode() );
            return this.hashCode() == obj.hashCode();
        }
        else
        {
            Logger.v(this.getClass(), "ContactBean.equals: parametro non di tipo ContactBean");
            return false;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return new ContactBean(this.getId(), this.getSurname(), this.getName(), this.getBirthday() );
    }

    @Override
    public String toString()
    {
        String output = this.getClass().getName() +
                ": [ id: " + this.getId() +
                " @@ surname: "  + this.getSurname() +
                " @@ name: "     + this.getName() +
                " @@ birthday: " + this.getBirthday() +" ]";
        return output;
    }

    @Override
    public String getDescription()
    {
        return this.getSurname() + ", " + this.getName();
    }

    @Override
    public ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, this.getClass().getName());
        cv.put(DBConstants.CONTACT_NAME_FIELD_NAME, this.getName());
        cv.put(DBConstants.CONTACT_SURNAME_FIELD_NAME, this.getSurname());
        cv.put(DBConstants.CONTACT_BIRTHDAY_FIELD_NAME, this.getBirthday());
        return cv;
    }

    /**
     * Il metodo genera la mappa dei parametri dell'oggetto.
     */
    /*@Override
    public void mapParameters() {

    }*/

    /*public static String[] getProjection()
    {
        return new String[] {
          DBConstants.DEFAULT_ID_FIELD_NAME,
          DBConstants.CONTACT_NAME_FIELD_NAME,
          DBConstants.CONTACT_SURNAME_FIELD_NAME,
          DBConstants.CONTACT_BIRTHDAY_FIELD_NAME
        };
    }*/
}
