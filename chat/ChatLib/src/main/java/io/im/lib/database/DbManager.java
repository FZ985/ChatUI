package io.im.lib.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.im.lib.database.dao.MessageDao;
import io.im.lib.database.dao.SessionDao;
import io.im.lib.database.dao.UserDao;
import io.im.lib.database.db.MessageDB;
import io.im.lib.database.db.UserDB;
import io.im.lib.utils.ChatExecutorHelper;


/**
 * author : JFZ
 * date : 2023/12/21 10:36
 * description :
 */
public class DbManager extends AndroidViewModel {

    public DbManager(@NonNull Application application) {
        super(application);
    }

    public MessageDao messageDao() {
        return MessageDB.getInstance(getApplication()).dao();
    }

    public UserDao userDao() {
        return UserDB.getInstance(getApplication()).dao();
    }

    public SessionDao sessionDao() {
        return MessageDB.getInstance(getApplication()).sessionDao();
    }

    public void clearAllDB() {
        ChatExecutorHelper.getInstance().diskIO().execute(() -> {
            userDao().clearAll();
            sessionDao().clearAll();
        });
    }
}
