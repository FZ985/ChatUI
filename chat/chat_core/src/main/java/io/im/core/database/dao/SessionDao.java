package io.im.core.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

/**
 * author : JFZ
 * date : 2023/12/21 10:00
 * description :
 */
@Dao
public interface SessionDao {

//    @Query("SELECT * FROM session")
//    LiveData<List<Session>> allSessionLiveData();

//    @Query("SELECT * FROM session")
//    List<Session> allSession();

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long insertSession(Session user);

//    @Query("SELECT * FROM session WHERE friendName LIKE :text")
//    List<Session> getSessionBySearch(String text);

    //批量插入
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertSessionList(List<Session> list);

//    @Update
//    void update(Session user);

//    @Query("SELECT * FROM session WHERE friendId = :friendIdStr")
//    Session getSessionByFriendId(String friendIdStr);

//    @Query("SELECT * FROM session WHERE shopId = :shopIdStr")
//    Session getSessionByShopId(String shopIdStr);

//    @Query("UPDATE session SET noReadNumber = :readCount WHERE friendId = :friendIdStr")
//    void updateReadCount(String friendIdStr, int readCount);

//    @Query("SELECT * FROM session WHERE topNum =(SELECT MAX(topNum)FROM session)")
//    Session getMaxTopNum();

//    @Query("DELETE FROM session WHERE id=:idStr")
//    int deleteById(String idStr);

    // 删除所有数据
    @Query("DELETE FROM session")
    int clearAll();
}
