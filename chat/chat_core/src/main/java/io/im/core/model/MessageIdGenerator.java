package io.im.core.model;


import io.im.core.utils.ServeTime;

/**
 * by DAD FZ
 * 2026/5/22
 * desc：消息id生成
 **/
public class MessageIdGenerator {

    private static long lastTimestamp = 0L;

    private static int sequence = 0;

    public synchronized static long nextId() {
        long timestamp = ServeTime.currentTimeMillis();
        if (timestamp == lastTimestamp) {
            sequence++;
            if (sequence > 999) {
                while (timestamp <= lastTimestamp) {
                    timestamp = ServeTime.currentTimeMillis();
                }
                sequence = 0;
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return timestamp * 1000 + sequence;
    }
}
