package io.im.core.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.im.core.model.ReMessage;


/**
 * author : JFZ
 * date : 2023/12/21 10:00
 * description :
 */
@Dao
public interface RevokeMessageDao {


    @Query("SELECT * FROM revoke_message WHERE `fromId`=:from or `toId`=:to")
    List<ReMessage> allMessageListWith(String from, String to);


    @Query("SELECT * FROM revoke_message WHERE `fromId`=:from or `toId`=:to")
    LiveData<List<ReMessage>> allMessageWith(String from, String to);

    @Insert
    long insertMessage(ReMessage message);

    @Query("DELETE FROM revoke_message WHERE `fromId`=:from or `toId`=:to")
    void deleteWith(String from, String to);

    @Delete
    void deleteMessages(ReMessage messages);

    @Delete
    void deleteMessagesWithList(List<ReMessage> messages);
}
