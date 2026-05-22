package io.im.lib.database.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.im.lib.database.dao.MessageDao;
import io.im.lib.database.dao.SessionDao;
import io.im.lib.model.Message;
import io.im.lib.model.Session;


/**
 * author : JFZ
 * date : 2023/12/21 10:25
 * description :
 */
@Database(entities = {Message.class, Session.class}, version = 5)
public abstract class MessageDB extends RoomDatabase {
    private static MessageDB rsDataBase = null;

    public static MessageDB getInstance(Context context) {
        if (rsDataBase == null) {
            synchronized (MessageDB.class) {
                if (rsDataBase == null) {
                    rsDataBase = Room.databaseBuilder(context, MessageDB.class, "message.db")
                            .fallbackToDestructiveMigration(true)
                            .build();
                }
            }
        }
        return rsDataBase;
    }

    public abstract MessageDao dao();

    public abstract SessionDao sessionDao();
}
