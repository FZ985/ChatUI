package io.im.core.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.im.core.model.Session;

/**
 * author : JFZ
 * date : 2023/12/21 10:00
 * description :
 */
@Dao
public interface SessionDao {

    @Query("SELECT * FROM session")
    LiveData<List<Session>> allSessionLiveData();

    @Query("SELECT * FROM session")
    List<Session> allSession();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSession(Session conversation);

    //批量插入
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSessionList(List<Session> list);

    @Update
    void update(Session conversation);

    @Query("SELECT * FROM session WHERE sid = :sidStr")
    Session getSessionBySid(String sidStr);

    @Query("DELETE FROM session WHERE sid=:idStr")
    int deleteById(String idStr);

    // 删除所有数据
    @Query("DELETE FROM session")
    void clearAll();

//
//    @Query("SELECT * FROM session WHERE shopId = :shopIdStr")
//    Session getSessionByShopId(String shopIdStr);

//    @Query("UPDATE session SET noReadNumber = :readCount WHERE friendId = :friendIdStr")
//    void updateReadCount(String friendIdStr, int readCount);

//    @Query("SELECT * FROM session WHERE topNum =(SELECT MAX(topNum)FROM session)")
//    Session getMaxTopNum();


//    @Query("SELECT * FROM session WHERE friendName LIKE :text")
//    List<Session> getSessionBySearch(String text);


}
