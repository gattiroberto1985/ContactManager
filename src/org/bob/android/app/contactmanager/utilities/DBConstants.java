package org.bob.android.app.contactmanager.utilities;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * La classe contiene una serie di costanti relative al database utilizzato
 * dall'applicazione.
 *
 * Created by roberto.gatti on 11/12/2014.
 */
public class DBConstants 
{

    /**
     * Stringa relativa alle authority dell'applicazione.
     */
    public static final String AUTHORITY = "org.bob.android.app.contactmanager";

    /* TABLES NAME */
    public static final String CONTACT_TABLE_NAME = "contacts";
    public static final String ADDRESS_TABLE_NAME = "addresses";
    public static final String PHONE_TABLE_NAME = "phones";
    public static final String EMAIL_TABLE_NAME = "emails";
    public static final String RELATION_TABLE_NAME = "relations";

    public static final String CONTACT_DETAILS_VIEW_NAME = "contact_details";

    /* URI */

    public static final Uri CONTACT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTACT_TABLE_NAME);
    public static final Uri ADDRESS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ADDRESS_TABLE_NAME);
    public static final Uri PHONE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PHONE_TABLE_NAME);
    public static final Uri EMAIL_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + EMAIL_TABLE_NAME);
    public static final Uri RELATION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + RELATION_TABLE_NAME);

    public static final Uri RELATION_CONTACT_ID_URI = Uri.parse("content://" + AUTHORITY + "/" + RELATION_TABLE_NAME + ".CID");

    /* URI INDICATOR */

    public static final int CONTACT_URI_INDICATOR = 10;
    public static final int CONTACT_COLLECTION_URI_INDICATOR = 11;

    public static final int ADDRESS_URI_INDICATOR = 20;
    public static final int ADDRESS_COLLECTION_URI_INDICATOR = 21;

    public static final int PHONE_URI_INDICATOR = 30;
    public static final int PHONE_COLLECTION_URI_INDICATOR = 31;

    public static final int EMAIL_URI_INDICATOR = 40;
    public static final int EMAIL_COLLECTION_URI_INDICATOR = 41;

    public static final int RELATION_URI_INDICATOR = 50;
    public static final int RELATION_COLLECTION_URI_INDICATOR = 51;
    public static final int RELATION_CONTACT_ID_URI_INDICATOR = 52;
    public static final int RELATION_OBJECT_ID_URI_INDICATOR = 53;


    /* CONTENT TYPES */

    public static final String CONTACT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + CONTACT_TABLE_NAME;
    public static final String CONTACT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + CONTACT_TABLE_NAME + ".element";
    public static final String ADDRESS_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + ADDRESS_TABLE_NAME;
    public static final String ADDRESS_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + ADDRESS_TABLE_NAME + ".element";
    public static final String PHONE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + PHONE_TABLE_NAME;
    public static final String PHONE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + PHONE_TABLE_NAME + ".element";
    public static final String EMAIL_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + EMAIL_TABLE_NAME;
    public static final String EMAIL_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + EMAIL_TABLE_NAME + ".element";
    public static final String RELATION_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + RELATION_TABLE_NAME;
    public static final String RELATION_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + RELATION_TABLE_NAME + ".element";


    /* COLUMN NAMES */

    public static final String DEFAULT_ID_FIELD_NAME = "_id";
    public static final String DEFAULT_TMST_INS_FIELD_NAME = "tmst_ins";
    public static final String DEFAULT_TMST_UPD_FIELD_NAME = "tmst_upd";

    public static final String CONTACT_NAME_FIELD_NAME = "name";
    public static final String CONTACT_SURNAME_FIELD_NAME = "surname";
    public static final String CONTACT_BIRTHDAY_FIELD_NAME = "birthday";

    public static final String ADDRESS_ADDRESS_FIELD_NAME = "address";

    public static final String PHONE_PHONE_FIELD_NAME = "phone";

    public static final String EMAIL_EMAIL_FIELD_NAME = "email";

    public static final String RELATION_CONTACT_ID_FIELD_NAME = "contact_id";
    public static final String RELATION_OBJECT_ID_FIELD_NAME = "object_id";
    public static final String RELATION_OBJECT_TYPE_FIELD_NAME = "object_type";

    public static final String CONTACT_DETAILS_R_ID_FIELD_NAME = "R_ID";
    public static final String CONTACT_DETAILS_C_ID_FIELD_NAME = "C_ID";
    public static final String CONTACT_DETAILS_O_ID_FIELD_NAME = "O_ID";
    public static final String CONTACT_DETAILS_OBJECT_TYPE_FIELD_NAME = "OBJECT_TYPE";
    public static final String CONTACT_DETAILS_OBJ_FIELD_NAME = "OBJECT";

    /* OTHER CONSTANTS */
    public static final String OBJECT_CLASS_TYPE_KEY = "class";

}
