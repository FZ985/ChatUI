package io.im.core.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.im.core.model.Message;


/**
 * author : JFZ
 * date : 2023/12/21 10:00
 * description :
 */
@Dao
public interface MessageDao {

    //查询单聊指定会话的最新消息
    @Query("SELECT * FROM message WHERE ((from_id = :fromId AND to_id = :toId) OR (from_id = :toId AND to_id = :fromId)) ORDER BY messageId DESC LIMIT 1")
    Message getLatestP2PMessage(String fromId, String toId);

    //单聊首页查询
    @Query("SELECT * FROM message WHERE ((from_id = :fromId AND to_id = :toId) OR (from_id = :toId AND to_id = :fromId)) ORDER BY messageId DESC LIMIT :pageSize")
    List<Message> getP2PFirstPageMessageByFromTo(String fromId, String toId, int pageSize);


    //群聊首页查询
    @Query("SELECT * FROM message WHERE (to_id = :groupId ) AND chatType = :conversationType ORDER BY messageId DESC LIMIT :pageSize")
    List<Message> getTeamFirstPageMessageById(String groupId, int conversationType, int pageSize);


    //单聊分页查询
    @Query("SELECT * FROM message WHERE messageId < :lastMessageId AND ((from_id = :fromId AND to_id = :toId) OR (from_id = :toId AND to_id = :fromId)) ORDER BY messageId DESC LIMIT :pageSize")
    List<Message> getP2PNextPageMessageByFromTo(String fromId, String toId, long lastMessageId, int pageSize);

    //群聊分页查询
    @Query("SELECT * FROM message WHERE messageId < :lastMessageId AND  to_id = :groupId AND chatType =:conversationType  ORDER BY messageId DESC LIMIT :pageSize")
    List<Message> getTeamNextPageMessageById(String groupId, int conversationType, long lastMessageId, int pageSize);

    //插入消息
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMessage(Message message);

    //更新消息
    @Update
    void updateMessage(Message message);

    //删除单聊指定消息
    @Query("DELETE FROM message WHERE messageId IN (:messageIds) AND ((from_id = :fromId AND to_id = :toId) OR (from_id = :toId AND to_id = :fromId))")
    int deleteP2PMessages(String fromId, String toId, List<Long> messageIds);

    //清空单聊会话消息
    @Query("DELETE FROM message WHERE ((from_id = :fromId AND to_id = :toId) OR (from_id = :toId AND to_id = :fromId))")
    int clearP2PMessages(String fromId, String toId);

    //清空群聊会话消息
    @Query("DELETE FROM message WHERE to_id = :groupId AND chatType = :conversationType")
    int clearTeamMessages(String groupId, int conversationType);


//    @Query("SELECT * FROM message " +
//            "GROUP BY `to` " +
//            "ORDER BY sendTime DESC")
//    LiveData<List<Message>> getUniqueMessages();

}
