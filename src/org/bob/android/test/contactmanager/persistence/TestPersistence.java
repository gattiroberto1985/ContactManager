package org.bob.android.test.contactmanager.persistence;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import org.bob.android.app.contactmanager.persistence.beans.*;
import org.bob.android.app.contactmanager.utilities.DBConstants;
import org.bob.android.app.contactmanager.utilities.Logger;
import org.bob.android.app.contactmanager.utilities.Utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by roberto.gatti on 12/12/2014.
 */
public class TestPersistence
{
    public static final void createTestContact(ContentResolver cr)
    {
        List<ContactBean> cblist = new ArrayList<ContactBean>(10);
        ArrayList<AddressBean> addresses = new ArrayList<AddressBean>(10);
        ArrayList<EmailBean> emails      = new ArrayList<EmailBean>(10);
        ArrayList<PhoneBean> phones      = new ArrayList<PhoneBean>(10);
        ArrayList<RelationBean> rels     = new ArrayList<RelationBean>(50);
        Random rand = new Random();


        int i = 0;
        cblist.add(new ContactBean(++i, "Gatti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Aatti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Aatti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Batti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Batti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Catti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Fatti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Hatti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Ratti", "Roberto", (new Date()).getTime()));
        cblist.add(new ContactBean(++i, "Zatti", "Roberto", (new Date()).getTime()));

        for ( i = 0; i < 10; i++ )
        {
            int randomNum = 100000 * rand.nextInt((10 - 0) + 1) + 0;
            AddressBean address = new AddressBean(i+1, "Nome via " + randomNum + " civico " + (randomNum+4) );
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, address.getClass().getName());
            cv.put(DBConstants.ADDRESS_ADDRESS_FIELD_NAME, address.getAddress());
            addresses.add(address);
            Uri newentry = cr.insert(DBConstants.ADDRESS_CONTENT_URI, cv);
            Logger.v_tst(TestPersistence.class.getClass(), "Inserito uri: '" + newentry.toString() + "'");

            /*RuntimeExceptionDao<AddressBean,Integer> simpleDao = helper.getRuntimeExceptionDao(AddressBean.class);
            Logger.v_tst(this.getClass(), "Inserico indirizzo: " + address.toString());
            simpleDao.create(address);*/
        }

        for ( i = 0; i < 10; i++ )
        {
            int randomNum = 100000 * rand.nextInt((10 - 0) + 1) + 0;
            EmailBean email = new EmailBean(i+1, "nome_utente_" + randomNum + "@dominio.it");
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, email.getClass().getName());
            cv.put(DBConstants.EMAIL_EMAIL_FIELD_NAME, email.getEMail());
            emails.add(email);
            Uri newentry = cr.insert(DBConstants.EMAIL_CONTENT_URI, cv);
            Logger.v_tst(TestPersistence.class.getClass(), "Inserito uri: '" + newentry.toString() + "'");

            /*RuntimeExceptionDao<EmailBean,Integer> simpleDao = helper.getRuntimeExceptionDao(EmailBean.class);
            Logger.v_tst(this.getClass(), "Inserico email: " + address.toString());
            simpleDao.create(address);*/
        }

        for ( i = 0; i < 10; i++ )
        {
            int randomNum = 100000 * rand.nextInt((10 - 0) + 1) + 0;
            PhoneBean phone = new PhoneBean(i+1, "045/123456" + randomNum );
            ContentValues cv = new ContentValues(2);
            cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, phone.getClass().getName());
            cv.put(DBConstants.PHONE_PHONE_FIELD_NAME, phone.getPhone());
            phones.add(phone);
            Uri newentry = cr.insert(DBConstants.PHONE_CONTENT_URI, cv);
            Logger.v_tst(TestPersistence.class.getClass(), "Inserito uri: '" + newentry.toString() + "'");

            /*RuntimeExceptionDao<PhoneBean,Integer> simpleDao = helper.getRuntimeExceptionDao(PhoneBean.class);
            Logger.v_tst(this.getClass(), "Inserico telefono: " + address.toString());
            simpleDao.create(address);*/
        }



        for ( i = 0; i < 10; i++)
        {
            ContactBean contact = cblist.get(i);
            Logger.v_tst(TestPersistence.class.getClass(), "Testing insert of " + contact.getDescription());
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, contact.getClass().getName());
            cv.put(DBConstants.CONTACT_NAME_FIELD_NAME, contact.getName());
            cv.put(DBConstants.CONTACT_SURNAME_FIELD_NAME, contact.getSurname());
            cv.put(DBConstants.CONTACT_BIRTHDAY_FIELD_NAME, contact.getBirthday());
            Uri newEntry = cr.insert(DBConstants.CONTACT_CONTENT_URI, cv);
            Logger.i(TestPersistence.class.getClass(), "[TST]", "Inserito uri: '" + newEntry.toString() + "'");
        }

        for ( i = 0; i < 30; i++ )
        {
            BaseObject obj = null;
            if ( i < 10 ) obj = addresses.get(Utilities.randInt(0, 9));
            else if ( i < 20 ) obj = phones.get(Utilities.randInt(0,9));
            else obj = emails.get(Utilities.randInt(0,9));
            RelationBean r = new RelationBean(i+1, cblist.get(Utilities.randInt(0,9)), obj);
            ContentValues cv = new ContentValues(4);
            cv.put(DBConstants.OBJECT_CLASS_TYPE_KEY, r.getClass().getName());
            cv.put(DBConstants.RELATION_CONTACT_ID_FIELD_NAME, r.getContactId());
            cv.put(DBConstants.RELATION_OBJECT_ID_FIELD_NAME, r.getObjectId());
            cv.put(DBConstants.RELATION_OBJECT_TYPE_FIELD_NAME, String.valueOf(r.getObjectType()));

            Uri newentry = cr.insert(DBConstants.RELATION_CONTENT_URI, cv);
            Logger.v_tst(TestPersistence.class.getClass(), "Inserito uri: '" + newentry.toString() + "'");

            /*RuntimeExceptionDao<Phone,Integer> simpleDao = helper.getRuntimeExceptionDao(Phone.class);
            Log.v(TAG, "Inserico telefono: " + address.toString());
            simpleDao.create(address);*/
        }


    }
}
