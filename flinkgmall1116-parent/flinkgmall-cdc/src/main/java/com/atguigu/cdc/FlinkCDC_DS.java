package com.atguigu.cdc;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.alibaba.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlinkCDC_DS {
    public static void main(String[] args) throws Exception {
        //TODO 1.准备流处理环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);


        /*//TODO 2.开启检查点   Flink-CDC将读取binlog的位置信息以状态的方式保存在CK,如果想要做到断点续传,
        // 需要从Checkpoint或者Savepoint启动程序
        //2.1 开启Checkpoint,每隔5秒钟做一次CK  ,并指定CK的一致性语义
        env.enableCheckpointing(5000L, CheckpointingMode.EXACTLY_ONCE);
        //2.2 设置超时时间为1分钟
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        //2.3 指定从CK自动重启策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,5000L));
        //2.4 设置任务关闭的时候保留最后一次CK数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        //2.5 设置状态后端
        env.setStateBackend(new FsStateBackend("hdfs://hadoop102:8020/flinkgmall/cdc"));
        //2.6 设置访问HDFS的用户名
        System.setProperty("HADOOP_USER_NAME", "atguigu");*/


        DebeziumSourceFunction<String> sourceFunction =
                MySQLSource.<String>builder()
                        .hostname("hadoop102")
                        .port(3306)
                        .username("root")
                        .password("000000")
                        .databaseList("flinkgmall-realtime")
                        .tableList("flinkgmall-realtime.table_process")
                        .startupOptions(StartupOptions.initial())
                        .deserializer(new StringDebeziumDeserializationSchema())
                        .build();

        DataStreamSource<String> mySqlDS = env.addSource(sourceFunction);

        mySqlDS.print(">>>>");
        env.execute();
    }
}
