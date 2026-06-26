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

    //删除指定消息
    @Query("DELETE FROM message WHERE messageId IN (:messageIds)")
    int deleteMessages(List<Long> messageIds);

    //清空单聊会话消息
    @Query("DELETE FROM message WHERE ((from_id = :fromId AND to_id = :toId) OR (from_id = :toId AND to_id = :fromId))")
    int clearP2PMessages(String fromId, String toId);

    //清空群聊会话消息
    @Query("DELETE FROM message WHERE to_id = :groupId AND chatType = :conversationType")
    int clearTeamMessages(String groupId, int conversationType);


    //更新消息中的用户信息
    @Query("UPDATE message SET from_name = CASE WHEN from_id = :userId THEN :name ELSE from_name END,from_avatar = CASE WHEN from_id = :userId THEN :avatar ELSE from_avatar END,to_name = CASE WHEN to_id = :userId THEN :name ELSE to_name END,to_avatar = CASE WHEN to_id = :userId THEN :avatar ELSE to_avatar END WHERE from_id = :userId OR to_id = :userId")
    void updateUserInfo(String userId, String name, String avatar);


    //更新用户的备注信息，单聊+群聊
    @Query("UPDATE message " +
            "SET " +
            "from_remark = CASE WHEN from_id = :userId THEN :remark ELSE from_remark END, " +
            "to_remark = CASE WHEN to_id = :userId THEN :remark ELSE to_remark END " +
            "WHERE from_id = :userId " +
            "OR to_id = :userId")
    void updateUserRemarkWithAll(String userId, String remark);


    //更新用户的备注信息，指定单聊还是群聊
    @Query("UPDATE message " +
            "SET " +
            "from_remark = CASE WHEN from_id = :userId THEN :remark ELSE from_remark END, " +
            "to_remark = CASE WHEN to_id = :userId THEN :remark ELSE to_remark END " +
            "WHERE chatType = :conversationType " +
            "AND (from_id = :userId OR to_id = :userId)")
    void updateUserRemarkByType(String userId, String remark, int conversationType);

}
