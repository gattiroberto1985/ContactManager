package org.bob.android.app.contactmanager.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import org.bob.android.app.contactmanager.persistence.beans.*;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * ContentProvider dell'applicazione.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class CMContentProvider extends ContentProvider
{
    /**
     * Database helper dell'applicazione.
     */
    private DBHelper dbh;

    /**
     * Uri Matcher per il controllo degli URI.
     */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Blocco statico di inizializzazione risorse generali della classe.
     */
    static
    {
        // Uri relativo alle tabelle
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.CONTACT_TABLE_NAME, DBConstants.CONTACT_COLLECTION_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.ADDRESS_TABLE_NAME, DBConstants.ADDRESS_COLLECTION_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.PHONE_TABLE_NAME, DBConstants.PHONE_COLLECTION_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.EMAIL_TABLE_NAME, DBConstants.EMAIL_COLLECTION_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.RELATION_TABLE_NAME, DBConstants.RELATION_COLLECTION_URI_INDICATOR);

        // Aggiungo gli uri relativi alle singole righe delle tabelle
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.CONTACT_TABLE_NAME + "/#", DBConstants.CONTACT_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.ADDRESS_TABLE_NAME + "/#", DBConstants.ADDRESS_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.PHONE_TABLE_NAME + "/#", DBConstants.PHONE_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.EMAIL_TABLE_NAME + "/#", DBConstants.EMAIL_URI_INDICATOR);
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.RELATION_TABLE_NAME + "/#", DBConstants.RELATION_URI_INDICATOR);

        // Aggiungo gli uri relativi ad altre tabelle e campi
        sURIMatcher.addURI(DBConstants.AUTHORITY, DBConstants.RELATION_TABLE_NAME + ".CID/#", DBConstants.RELATION_CONTACT_ID_URI_INDICATOR);

    }

    public CMContentProvider()
    {
        Logger.v(this.getClass(), "Costruttore content provider");
    }

    @Override
    public boolean onCreate()
    {
        Logger.v(this.getClass(),  "onCreate content provider");
        if ( this.dbh == null ) this.dbh = new DBHelper(this.getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String whereClauses, String[] whereValues, String sortOrder)
    {
        Logger.v(this.getClass(),  "Content Provider: query");

        // CEROTTONE DA CHILO!!
        if ( sURIMatcher.match(uri) == DBConstants.RELATION_CONTACT_ID_URI_INDICATOR )
        {
            int id = Integer.parseInt(uri.getLastPathSegment());
            Logger.v(this.getClass(),  "Richiesto recupero dettagli contatto per il contatto ");
            return this.getContactDetails(id);
        }

        // altrimenti procedo con le query standard...
        Class clazz = this.getClassFromURI(uri);
        Cursor cursor = null;
        try
        {
            Dao<Object, Integer> red = this.dbh.getDao(clazz);
            QueryBuilder<Object, Integer> qb = red.queryBuilder();

            if ( whereClauses != null )
            {
                String[] wc = whereClauses.split("@");
                if ( wc.length == 2 )
                    qb.where().eq(wc[0], whereValues[0]).and().eq(wc[1], whereValues[1]);
                else qb.where().eq(wc[0], whereValues[0]);
            }

            if ( sortOrder != null )
                qb.orderBy(sortOrder, true);

            //RuntimeExceptionDao<Class, String> red = this.dbh.getRuntimeExceptionDao(clazz);
            //QueryBuilder<Class, String> qb = red.queryBuilder();
            CloseableIterator<Object> iterator = null;
            iterator = red.iterator(qb.prepare());
            AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
            cursor = results.getRawCursor();
            cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        /*RuntimeExceptionDao<AddressBean,Integer> simpleDao = helper.getRuntimeExceptionDao(AddressBean.class);
            Logger.v(this.getClass(),  "Inserico indirizzo: " + address.toString());
            simpleDao.create(address);*/
        Logger.v(this.getClass(),  "Content Provider: insert");
        Class clazz = this.getClassFromURI(uri);
        int id = -1;
        try
        {
            Dao<Object, Integer> dao = this.dbh.getDao(clazz);
            Object obj = this.backToObjectFromContentValues(contentValues);
            dao.create(obj);
            Logger.v(this.getClass(),  "Dao creato, procedo con il recupero dell'id...");
            id = dao.extractId(obj);
        }
        catch ( SQLException ex )
        {
            Logger.e(this.getClass(),  "ERRORE: impossibile inserire l'oggetto a db!");
            Logger.e(this.getClass(),  "        " + ex.getMessage());
            ex.printStackTrace();
        }
        Logger.v(this.getClass(),  "Ritorno l'uri: ' " + Uri.withAppendedPath(uri, String.valueOf(id)) + "'");
        this.getContext().getContentResolver().notifyChange(Uri.withAppendedPath(uri, String.valueOf(id)), null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    @Override
    public int delete(Uri uri, String s, String[] strings)
    {
        Logger.v(this.getClass(),  "Content Provider: delete");
        Class clazz = this.getClassFromURI(uri);
        int output = -1;
        int id = Integer.parseInt(uri.getLastPathSegment().toString());
        try
        {
            Dao<Object, Integer> dao = this.dbh.getDao(clazz);
            if ( sURIMatcher.match(uri) == DBConstants.RELATION_CONTACT_ID_URI_INDICATOR )
            {
                DeleteBuilder<Object, Integer> db = dao.deleteBuilder();
                Where where = db.where();
                where.eq(DBConstants.RELATION_CONTACT_ID_FIELD_NAME, id);
                db.setWhere(where);
                int removed = db.delete();
                Logger.v(this.getClass(), "Rimosse " + removed + " relazioni!");
                return removed;
            }
            else
            {
                output = dao.deleteById(id);
                Logger.v(this.getClass(), "Dao rimosso!");
            }
        }
        catch ( SQLException ex )
        {
            Logger.e(this.getClass(),  "ERRORE: impossibile rimuovere l'oggetto a db!");
            Logger.e(this.getClass(),  "        " + ex.getMessage());
            ex.printStackTrace();
        }
        catch ( NumberFormatException ex )
        {
            Logger.e(this.getClass(),  "ERRORE: impossibile rimuovere l'oggetto a db!");
            Logger.e(this.getClass(),  "        " + ex.getMessage());
            output = -1;
        }
        return output;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings)
    {
        Logger.v(this.getClass(),  "Content Provider: update");
        Class clazz = this.getClassFromURI(uri);
        int output = -1;
        try
        {
            contentValues.put(DBConstants.DEFAULT_ID_FIELD_NAME, Integer.parseInt(uri.getLastPathSegment()));
            Dao<Object, Integer> dao = this.dbh.getDao(clazz);
            Object obj = this.backToObjectFromContentValues(contentValues);
            output = dao.update(obj);
            Logger.v(this.getClass(),  "Dao aggiornato!");
        }
        catch ( SQLException ex )
        {
            Logger.e(this.getClass(),  "ERRORE: impossibile aggiornare l'oggetto a db!");
            Logger.e(this.getClass(),  "        " + ex.getMessage());
            ex.printStackTrace();
        }
        this.getContext().getContentResolver().notifyChange(uri, null);
        return output;
    }


    /**
     * Metodo di decodifica dell'URI.
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri)
    {
        Logger.i(this.getClass(),  "Decoding URI: '" + uri.toString() + "'");
        int match = sURIMatcher.match(uri);
        switch ( match )
        {
            case DBConstants.CONTACT_COLLECTION_URI_INDICATOR: return DBConstants.CONTACT_CONTENT_TYPE;
            case DBConstants.CONTACT_URI_INDICATOR: return DBConstants.CONTACT_CONTENT_ITEM_TYPE;

            case DBConstants.ADDRESS_COLLECTION_URI_INDICATOR: return DBConstants.ADDRESS_CONTENT_TYPE;
            case DBConstants.ADDRESS_URI_INDICATOR: return DBConstants.ADDRESS_CONTENT_ITEM_TYPE;

            case DBConstants.PHONE_COLLECTION_URI_INDICATOR: return DBConstants.PHONE_CONTENT_TYPE;
            case DBConstants.PHONE_URI_INDICATOR: return DBConstants.PHONE_CONTENT_ITEM_TYPE;

            case DBConstants.EMAIL_COLLECTION_URI_INDICATOR: return DBConstants.EMAIL_CONTENT_TYPE;
            case DBConstants.EMAIL_URI_INDICATOR: return DBConstants.EMAIL_CONTENT_ITEM_TYPE;

            case DBConstants.RELATION_COLLECTION_URI_INDICATOR: return DBConstants.RELATION_CONTENT_TYPE;
            case DBConstants.RELATION_URI_INDICATOR:
            case DBConstants.RELATION_CONTACT_ID_URI_INDICATOR:
                return DBConstants.RELATION_CONTENT_ITEM_TYPE;

            default: throw new IllegalArgumentException("Uri invalido: '" + uri.toString() + "' -- match: " + match + "!");
        }
    }

    /**
     * Il metodo, a partire dall'uri, fornisce in output la classe corretta da passare
     * ad ORMLite.
     * @param uri
     * @return
     */
    private Class getClassFromURI(Uri uri)
    {
        Logger.i(this.getClass(), "Decoding URI to get the right Class: '" + uri.toString() + "'");
        int match = sURIMatcher.match(uri);
        switch ( match )
        {
            case DBConstants.CONTACT_COLLECTION_URI_INDICATOR:
            case DBConstants.CONTACT_URI_INDICATOR: return ContactBean.class;

            case DBConstants.ADDRESS_COLLECTION_URI_INDICATOR:
            case DBConstants.ADDRESS_URI_INDICATOR: return AddressBean.class;

            case DBConstants.PHONE_COLLECTION_URI_INDICATOR:
            case DBConstants.PHONE_URI_INDICATOR: return PhoneBean.class;

            case DBConstants.EMAIL_COLLECTION_URI_INDICATOR:
            case DBConstants.EMAIL_URI_INDICATOR: return EmailBean.class;

            case DBConstants.RELATION_COLLECTION_URI_INDICATOR:
            case DBConstants.RELATION_CONTACT_ID_URI_INDICATOR:
            case DBConstants.RELATION_URI_INDICATOR: return RelationBean.class;

            default: throw new IllegalArgumentException("Uri invalido: '" + uri.toString() + "' -- match: " + match + "!");
        }
    }

    private Object backToObjectFromContentValues(ContentValues cv)
    {
        Logger.d(this.getClass(),  "Recupero l'oggetto dal contentvalues");
        try
        {
            Class clazz = Class.forName(cv.get(DBConstants.OBJECT_CLASS_TYPE_KEY).toString());
            Object[] constrParams = new Object[] { cv } ;
            return clazz.getConstructor(ContentValues.class).newInstance(constrParams); // newInstance(cv);

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        catch ( NoSuchMethodException ex )
        {
            ex.printStackTrace();
            return null;
        }
        catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Il metodo recupera i dettagli del contatto con id passato come parametro.
     * @param c_id
     * @return
     */
    public Cursor getContactDetails(int c_id)
    {
        Logger.d(this.getClass(),   "Metodo di recupero dei dettagli del singolo contatto");
        Logger.d(this.getClass(),   "|--> Istanzio il query builder...");
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Logger.d(this.getClass(),   "|--> Recupero i dati della query");
        queryBuilder.setTables(DBConstants.CONTACT_DETAILS_VIEW_NAME);
        SQLiteDatabase db = this.dbh.getReadableDatabase();
        // Inizializzo le variabili per il cursor
        String[] projection = null;
        String groupBy = null, having = null;
        String whereClause = DBConstants.CONTACT_DETAILS_C_ID_FIELD_NAME + " = ?" ;
        String sortOrder = DBConstants.CONTACT_DETAILS_OBJECT_TYPE_FIELD_NAME + " ASC";
        String[] whereValues = new String[] { String.valueOf(c_id) };
        // eseguo la query
        Cursor cursor = queryBuilder.query(db, projection, whereClause, whereValues, groupBy, having, sortOrder);
        // make sure that potential listeners are getting notified
        // cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Logger.d(this.getClass(),   "|--> Esco dal metodo ritornando il cursore trovato...");
        return cursor;
    }

}
