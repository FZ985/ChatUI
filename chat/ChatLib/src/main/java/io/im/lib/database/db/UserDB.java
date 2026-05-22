package io.im.lib.database.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.im.lib.database.dao.UserDao;
import io.im.lib.model.UserInfo;


/**
 * author : JFZ
 * date : 2023/12/21 10:25
 * description :
 */
@Database(entities = {UserInfo.class}, version = 1)
public abstract class UserDB extends RoomDatabase {
    private static UserDB rsDataBase = null;

    public static UserDB getInstance(Context context) {
        if (rsDataBase == null) {
            synchronized (UserDB.class) {
                if (rsDataBase == null) {
                    rsDataBase = Room.databaseBuilder(context, UserDB.class, "user.db")
                            .fallbackToDestructiveMigration(true)
                            .build();
                }
            }
        }
        return rsDataBase;
    }

    public abstract UserDao dao();
}
