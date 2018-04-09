package ru.yugsys.vvvresearch.lconfig.model.DataBaseClasses;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import ru.yugsys.vvvresearch.lconfig.model.DataEntity.MDevice;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MDevicesTable".
*/
public class MDeviceDao extends AbstractDao<MDevice, Long> {

    public static final String TABLENAME = "MDevicesTable";

    /**
     * Properties of entity MDevice.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, String.class, "type", false, "TYPE");
        public final static Property IsOTTA = new Property(2, boolean.class, "isOTTA", false, "IS_OTTA");
        public final static Property Eui = new Property(3, String.class, "eui", false, "EUI");
        public final static Property Appeui = new Property(4, String.class, "appeui", false, "APPEUI");
        public final static Property Appkey = new Property(5, String.class, "appkey", false, "APPKEY");
        public final static Property Nwkid = new Property(6, String.class, "nwkid", false, "NWKID");
        public final static Property Devadr = new Property(7, String.class, "devadr", false, "DEVADR");
        public final static Property Nwkskey = new Property(8, String.class, "nwkskey", false, "NWKSKEY");
        public final static Property Appskey = new Property(9, String.class, "appskey", false, "APPSKEY");
        public final static Property Latitude = new Property(10, double.class, "Latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(11, double.class, "Longitude", false, "LONGITUDE");
        public final static Property OutType = new Property(12, String.class, "outType", false, "OUT_TYPE");
        public final static Property KV = new Property(13, String.class, "kV", false, "K_V");
        public final static Property KI = new Property(14, String.class, "kI", false, "K_I");
    }

    private DaoSession daoSession;


    public MDeviceDao(DaoConfig config) {
        super(config);
    }
    
    public MDeviceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MDevicesTable\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TYPE\" TEXT NOT NULL ," + // 1: type
                "\"IS_OTTA\" INTEGER NOT NULL ," + // 2: isOTTA
                "\"EUI\" TEXT UNIQUE ," + // 3: eui
                "\"APPEUI\" TEXT NOT NULL ," + // 4: appeui
                "\"APPKEY\" TEXT NOT NULL ," + // 5: appkey
                "\"NWKID\" TEXT NOT NULL ," + // 6: nwkid
                "\"DEVADR\" TEXT NOT NULL ," + // 7: devadr
                "\"NWKSKEY\" TEXT NOT NULL ," + // 8: nwkskey
                "\"APPSKEY\" TEXT NOT NULL ," + // 9: appskey
                "\"LATITUDE\" REAL NOT NULL ," + // 10: Latitude
                "\"LONGITUDE\" REAL NOT NULL ," + // 11: Longitude
                "\"OUT_TYPE\" TEXT NOT NULL ," + // 12: outType
                "\"K_V\" TEXT NOT NULL ," + // 13: kV
                "\"K_I\" TEXT NOT NULL );"); // 14: kI
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MDevicesTable\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MDevice entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getType());
        stmt.bindLong(3, entity.getIsOTTA() ? 1L: 0L);
 
        String eui = entity.getEui();
        if (eui != null) {
            stmt.bindString(4, eui);
        }
        stmt.bindString(5, entity.getAppeui());
        stmt.bindString(6, entity.getAppkey());
        stmt.bindString(7, entity.getNwkid());
        stmt.bindString(8, entity.getDevadr());
        stmt.bindString(9, entity.getNwkskey());
        stmt.bindString(10, entity.getAppskey());
        stmt.bindDouble(11, entity.getLatitude());
        stmt.bindDouble(12, entity.getLongitude());
        stmt.bindString(13, entity.getOutType());
        stmt.bindString(14, entity.getKV());
        stmt.bindString(15, entity.getKI());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MDevice entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getType());
        stmt.bindLong(3, entity.getIsOTTA() ? 1L: 0L);
 
        String eui = entity.getEui();
        if (eui != null) {
            stmt.bindString(4, eui);
        }
        stmt.bindString(5, entity.getAppeui());
        stmt.bindString(6, entity.getAppkey());
        stmt.bindString(7, entity.getNwkid());
        stmt.bindString(8, entity.getDevadr());
        stmt.bindString(9, entity.getNwkskey());
        stmt.bindString(10, entity.getAppskey());
        stmt.bindDouble(11, entity.getLatitude());
        stmt.bindDouble(12, entity.getLongitude());
        stmt.bindString(13, entity.getOutType());
        stmt.bindString(14, entity.getKV());
        stmt.bindString(15, entity.getKI());
    }

    @Override
    protected final void attachEntity(MDevice entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MDevice readEntity(Cursor cursor, int offset) {
        MDevice entity = new MDevice( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // type
            cursor.getShort(offset + 2) != 0, // isOTTA
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // eui
            cursor.getString(offset + 4), // appeui
            cursor.getString(offset + 5), // appkey
            cursor.getString(offset + 6), // nwkid
            cursor.getString(offset + 7), // devadr
            cursor.getString(offset + 8), // nwkskey
            cursor.getString(offset + 9), // appskey
            cursor.getDouble(offset + 10), // Latitude
            cursor.getDouble(offset + 11), // Longitude
            cursor.getString(offset + 12), // outType
            cursor.getString(offset + 13), // kV
            cursor.getString(offset + 14) // kI
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MDevice entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.getString(offset + 1));
        entity.setIsOTTA(cursor.getShort(offset + 2) != 0);
        entity.setEui(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAppeui(cursor.getString(offset + 4));
        entity.setAppkey(cursor.getString(offset + 5));
        entity.setNwkid(cursor.getString(offset + 6));
        entity.setDevadr(cursor.getString(offset + 7));
        entity.setNwkskey(cursor.getString(offset + 8));
        entity.setAppskey(cursor.getString(offset + 9));
        entity.setLatitude(cursor.getDouble(offset + 10));
        entity.setLongitude(cursor.getDouble(offset + 11));
        entity.setOutType(cursor.getString(offset + 12));
        entity.setKV(cursor.getString(offset + 13));
        entity.setKI(cursor.getString(offset + 14));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MDevice entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MDevice entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MDevice entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
