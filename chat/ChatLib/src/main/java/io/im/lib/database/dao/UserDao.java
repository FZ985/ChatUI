package io.im.lib.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

/**
 * author : JFZ
 * date : 2023/12/28 15:15
 * description :
 */
@Dao
public interface UserDao {

//    @Query("SELECT * FROM userInfo")
//    LiveData<List<UserInfo>> allUser();

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    long insertUser(UserInfo user);

//    @Query("SELECT * FROM userInfo WHERE userName LIKE :text")
//    List<UserInfo> getUsersBySearch(String text);


    //批量插入
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertUserList(List<UserInfo> list);

//    @Update
//    void update(UserInfo user);

//    @Query("SELECT * FROM userInfo WHERE userId = :userIdStr")
//    UserInfo getUserForUserId(String userIdStr);


    //删除所有数据
    @Query("DELETE FROM userInfo")
    int clearAll();
}
