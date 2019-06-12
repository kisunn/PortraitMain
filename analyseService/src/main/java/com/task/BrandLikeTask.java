package com.task;

import com.entity.BrandLike;
import com.kafka.CustomWatermarkExtractor;
import com.kafka.KafkaEvent;
import com.kafka.KafkaEventSchema;
import com.map.BrandLikeMap;
import com.reduce.BrandLikeReduce;
import com.sink.BrandLikeSink;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

/**
 * @Description 品牌偏好
 * @Author wangliqiang
 * @Date 2019/6/5 8:57
 */
public class BrandLikeTask {
    public static void main(String[] args) throws Exception {
        args = new String[]{"-input-topic", "scanProductLog", "--bootstrap-server", "flink01:9092", "-zookeeper.connect", "flink01:2821", "--group.id", "userportrait"};

        final ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableSysoutLogging();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        env.enableCheckpointing(5000);
        env.getConfig().setGlobalJobParameters(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<KafkaEvent> input = env.addSource(
                new FlinkKafkaConsumer010<>(
                        parameterTool.getRequired("input-topic"),
                        new KafkaEventSchema(),
                        parameterTool.getProperties())
                        .assignTimestampsAndWatermarks(new CustomWatermarkExtractor()));
        DataStream<BrandLike> brandLikeMap = input.flatMap(new BrandLikeMap());

        DataStream<BrandLike> brandLikeReduce = brandLikeMap.keyBy("groupfield").timeWindow(Time.seconds(2)).reduce(new BrandLikeReduce());

        brandLikeReduce.addSink(new BrandLikeSink());

        env.execute("BrandLikeTask execute");

    }
}
