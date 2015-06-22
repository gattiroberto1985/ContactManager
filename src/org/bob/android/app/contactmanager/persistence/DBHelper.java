package org.bob.android.app.contactmanager.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bob.android.app.contactmanager.persistence.beans.*;
import org.bob.android.app.contactmanager.utilities.Constants;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.test.contactmanager.persistence.TestPersistence;
import java.sql.SQLException;

//import org.bob.android.test.contactmanager.persistence.TestPersistence;

/**
 * Created by roberto.gatti on 12/12/2014.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper
{
    // name of the database file for your application -- change to something appropriate for your app
    public static final String DATABASE_NAME = "contactmanager.db";

    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    /**
     * Oggetto context.
     */
    private Context mContext;

    /**
     * Stringa di creazione SQL della view con i dettagli del contatto.
     */
    private static final String CONTACT_DETAILS_VIEW_SQL =
            "SELECT r." + DBConstants.DEFAULT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_R_ID_FIELD_NAME +
                    ", r." + DBConstants.RELATION_CONTACT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_C_ID_FIELD_NAME +
                    ", '" + Constants.ADDRESS_TYPE_INDICATOR + "' AS " + DBConstants.CONTACT_DETAILS_OBJECT_TYPE_FIELD_NAME +
                    ", r." + DBConstants.RELATION_OBJECT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_O_ID_FIELD_NAME +
                    ", a." + DBConstants.ADDRESS_ADDRESS_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_OBJ_FIELD_NAME +
                    " FROM " + DBConstants.RELATION_TABLE_NAME + " r, " + DBConstants.ADDRESS_TABLE_NAME + " a " +
                    " WHERE r.object_type = '" + Constants.ADDRESS_TYPE_INDICATOR + "' " +
                    "AND r." + DBConstants.RELATION_OBJECT_ID_FIELD_NAME + " = a." + DBConstants.DEFAULT_ID_FIELD_NAME + " UNION ALL " +
                    "SELECT r." + DBConstants.DEFAULT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_R_ID_FIELD_NAME +
                    ", r." + DBConstants.RELATION_CONTACT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_C_ID_FIELD_NAME +
                    ", '" + Constants.EMAIL_TYPE_INDICATOR + "' AS " + DBConstants.CONTACT_DETAILS_OBJECT_TYPE_FIELD_NAME +
                    ", r." + DBConstants.RELATION_OBJECT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_O_ID_FIELD_NAME +
                    ", e." + DBConstants.EMAIL_EMAIL_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_OBJ_FIELD_NAME +
                    " FROM " + DBConstants.RELATION_TABLE_NAME + " r, " + DBConstants.EMAIL_TABLE_NAME + " e " +
                    "WHERE r.object_type = '" + Constants.EMAIL_TYPE_INDICATOR + "' " +
                    "AND r." + DBConstants.RELATION_OBJECT_ID_FIELD_NAME + " = e." + DBConstants.DEFAULT_ID_FIELD_NAME + " UNION ALL " +
                    "SELECT r." + DBConstants.DEFAULT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_R_ID_FIELD_NAME +
                    ", r." + DBConstants.RELATION_CONTACT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_C_ID_FIELD_NAME +
                    ", '" + Constants.PHONE_TYPE_INDICATOR + "' AS " + DBConstants.CONTACT_DETAILS_OBJECT_TYPE_FIELD_NAME +
                    ", r." + DBConstants.RELATION_OBJECT_ID_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_O_ID_FIELD_NAME +
                    ", p." + DBConstants.PHONE_PHONE_FIELD_NAME + " AS " + DBConstants.CONTACT_DETAILS_OBJ_FIELD_NAME +
                    " FROM " + DBConstants.RELATION_TABLE_NAME + " r, " + DBConstants.PHONE_TABLE_NAME + " p " +
                    "WHERE r.object_type = '" + Constants.PHONE_TYPE_INDICATOR +"'" +
                    "AND r." + DBConstants.RELATION_OBJECT_ID_FIELD_NAME + " = p." + DBConstants.DEFAULT_ID_FIELD_NAME ;

    // the DAO object we use to access the SimpleData table
    /*private Dao<SimpleData, Integer> simpleDao = null;
    private RuntimeExceptionDao<SimpleData, Integer> simpleRuntimeDao = null;*/

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // R.raw.ormlite_config);
        this.mContext = context;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {
        try
        {
            Logger.i(this.getClass(), "onCreate");
            Logger.v(this.getClass(), "Rimuovo le tabelle...");
            TableUtils.dropTable(connectionSource, ContactBean.class, true);
            TableUtils.dropTable(connectionSource, PhoneBean.class, true);
            TableUtils.dropTable(connectionSource, AddressBean.class, true);
            TableUtils.dropTable(connectionSource, EmailBean.class, true);
            TableUtils.dropTable(connectionSource, RelationBean.class, true);
            db.execSQL("DROP VIEW IF EXISTS " + DBConstants.CONTACT_DETAILS_VIEW_NAME);

            Logger.v(this.getClass(), "Creo le tabelle");
            TableUtils.createTable(connectionSource, ContactBean.class);
            TableUtils.createTable(connectionSource, EmailBean.class);
            TableUtils.createTable(connectionSource, PhoneBean.class);
            TableUtils.createTable(connectionSource, AddressBean.class);
            TableUtils.createTable(connectionSource, RelationBean.class);
            db.execSQL("CREATE VIEW " + DBConstants.CONTACT_DETAILS_VIEW_NAME + " AS " + this.CONTACT_DETAILS_VIEW_SQL);

            Logger.v(this.getClass(), "Inserisco i dati");
            TestPersistence.createTestContact(this.mContext.getContentResolver());
        }
        catch (SQLException e)
        {
            Logger.e(this.getClass(), "Can't create database: " + e.getMessage());
            throw new RuntimeException(e);
        }

        /*// here we try inserting data in the on-create as a test
        RuntimeExceptionDao<SimpleData, Integer> dao = getSimpleDataDao();
        long millis = System.currentTimeMillis();
        // create some entries in the onCreate
        SimpleData simple = new SimpleData(millis);
        dao.create(simple);
        simple = new SimpleData(millis + 1);
        dao.create(simple);
        Logger.i(this.getClass(), "created new entries in onCreate: " + millis);*/
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        /*try
        {*/
            Logger.i(this.getClass(), "onUpgrade");
            /*TableUtils.dropTable(connectionSource, ContactBean.class, true);
            TableUtils.dropTable(connectionSource, PhoneBean.class, true);
            TableUtils.dropTable(connectionSource, AddressBean.class, true);
            TableUtils.dropTable(connectionSource, EmailBean.class, true);
            TableUtils.dropTable(connectionSource, RelationBean.class, true);
            db.execSQL("DROP VIEW IF EXISTS " + DBConstants.CONTACT_DETAILS_VIEW_NAME);*/
            // after we drop the old databases, we create the new ones
            this.onCreate(db, connectionSource);
        /*}
        catch (SQLException e)
        {
            Logger.e(this.getClass(), "Can't drop databases: " + e.getMessage());
            throw new RuntimeException(e);
        }*/
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    /*public Dao<SimpleData, Integer> getDao() throws SQLException {
        if (simpleDao == null) {
            simpleDao = getDao(SimpleData.class);
        }
        return simpleDao;
    }*/

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    /*public RuntimeExceptionDao<SimpleData, Integer> getSimpleDataDao() {
        if (simpleRuntimeDao == null) {
            simpleRuntimeDao = getRuntimeExceptionDao(SimpleData.class);
        }
        return simpleRuntimeDao;
    }*/

    /**
     * Close the database connections and clear any cached DAOs.
     */
    /*@Override
    public void close() {
        super.close();
        simpleDao = null;
        simpleRuntimeDao = null;
    }*/
}
