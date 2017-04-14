package com.yoscholar.deliveryboy.couchDB;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Created by agrim on 2/11/16.
 */

public class CouchBaseSingleton {

    private static CouchBaseSingleton couchBaseInstance;
    private Manager manager;
    private Database database;

    public static final String DB_NAME = "yo_scholar_delivery";

    private CouchBaseSingleton() {
    }

    public static CouchBaseSingleton getInstance() {
        //if no instance is initialized yet then create new instance
        //else return stored instance
        if (couchBaseInstance == null) {
            couchBaseInstance = new CouchBaseSingleton();
        }
        return couchBaseInstance;
    }


    public Database getDatabaseInstance() throws CouchbaseLiteException {
        if ((this.database == null) & (this.manager != null)) {
            this.database = manager.getDatabase(DB_NAME);
        }
        return database;
    }

    public Manager getManagerInstance(Context context) throws IOException {
        if (manager == null) {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        }
        return manager;
    }
}
