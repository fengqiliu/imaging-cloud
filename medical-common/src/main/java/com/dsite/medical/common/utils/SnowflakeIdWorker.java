package com.dsite.medical.common.utils;

/**
 * 雪花算法ID生成器
 */
public class SnowflakeIdWorker {

    /** 起始时间戳 (2024-01-01 00:00:00) */
    private static final long EPOCH = 1704038400000L;

    /** 数据中心ID位数 */
    private static final long DATA_CENTER_ID_BITS = 5L;

    /** 工作机器ID位数 */
    private static final long WORKER_ID_BITS = 5L;

    /** 序列号位数 */
    private static final long SEQUENCE_BITS = 12L;

    /** 工作机器ID最大值 */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /** 数据中心ID最大值 */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /** 工作机器ID左移位数 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /** 数据中心ID左移位数 */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /** 时间戳左移位数 */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /** 序列号掩码 */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /** 工作机器ID */
    private static long workerId = 1L;

    /** 数据中心ID */
    private static long dataCenterId = 1L;

    /** 序列号 */
    private static long sequence = 0L;

    /** 上次时间戳 */
    private static long lastTimestamp = -1L;

    private static final SnowflakeIdWorker INSTANCE = new SnowflakeIdWorker();

    private SnowflakeIdWorker() {
    }

    public static SnowflakeIdWorker getInstance() {
        return INSTANCE;
    }

    /**
     * 获取下一个ID
     */
    public static synchronized long getNextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上次时间戳，说明时钟回拨
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        // 如果是同一时间生成的，则进行序列号递增
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 序列号溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同时间戳，重置序列号
            sequence = 0L;
        }

        // 更新上次时间戳
        lastTimestamp = timestamp;

        // 生成ID
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(getNextId());
        }
    }
}
