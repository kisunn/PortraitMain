package com.kmeans;

import java.util.*;

/**
 * @Description TODO
 * @Author wangliqiang
 * @Date 2019/6/6 14:27
 */
public class KMeansRunByUserGroup {

    private int kNum; // 簇的个数
    private int iterNum; //迭代次数

    private int iterMaxTimes = 100000; // 单次迭代最大运行次数
    private int iterRunTimes = 0; // 单次迭代实际运行次数
    private float disDiff = (float) 0.01; // 单次迭代终止条件

    private List<float[]> original_data = null; // 用于存放 原始数据集
    private static List<Point> pointList = null; // 用于存放 原始数据集所构建的点集
    private DistanceCompute disC = new DistanceCompute();
    private int len = 0; // 用于记录每个数据点的维度

    public KMeansRunByUserGroup(int k, List<float[]> original_data) {
        this.kNum = k;
        this.original_data = original_data;
        this.len = original_data.get(0).length - 1;
        // 检查规范
        check();
        // 初始化点集
        init();
    }

    public Set<Cluster> run() {
        Set<Cluster> clusterSet = chooseCenterCluster();
        boolean ifNeedIter = true;
        while (ifNeedIter) {
            cluster(clusterSet);
            ifNeedIter = calculateCenter(clusterSet);
            iterRunTimes++;
        }
        return clusterSet;
    }


    /**
     * 检查规范
     */
    private void check() {
        if (kNum == 0) {
            throw new IllegalArgumentException("k must be the number > 0");
        }
        if (original_data == null) {
            throw new IllegalArgumentException("program can't get real data");
        }
    }

    /**
     * 初始化数据集，把数组转化为Point类型
     */
    private void init() {
        pointList = new ArrayList<Point>();
        for (int i = 0, j = len; i < j; i++) {
            pointList.add(new Point(Integer.valueOf(original_data.get(0)+""), original_data.get(i)));
        }
    }

    /**
     * 随机选取中心点，构建中心类
     *
     * @return
     */
    private Set<Cluster> chooseCenterCluster() {
        Set<Cluster> clusterSet = new HashSet<>();
        Random random = new Random();
        for (int id = 0; id < kNum; ) {
            Point point = pointList.get(random.nextInt(pointList.size()));
            // 用于标记是否已经选择过改数据
            boolean flag = true;
            for (Cluster cluster : clusterSet) {
                if (cluster.getCenter().equals(point)) {
                    flag = false;
                }
            }
            // 如果随机选取的点没有被选中过，则生成一个cluster
            if (flag) {
                Cluster cluster = new Cluster(id, point);
                clusterSet.add(cluster);
                id++;
            }
        }
        return clusterSet;
    }

    /**
     * 为每个点分配一个类
     *
     * @param clusterSet
     */
    public void cluster(Set<Cluster> clusterSet) {
        // 计算每个点到K个中心的距离，并且为每个点标记类别号
        for (Point point : pointList) {
            float min_dis = Integer.MAX_VALUE;
            for (Cluster cluster : clusterSet) {
                float tmp_dis = (float) Math.min(disC.getEuclideanDis(point, cluster.getCenter()), min_dis);
                if (tmp_dis != min_dis) {
                    min_dis = tmp_dis;
                    point.setClusterId(cluster.getId());
                    point.setDist(min_dis);
                }
            }
        }
        // 清楚原理所有的类中成员，把所有的点，分别加入每个类别
        for (Cluster cluster : clusterSet) {
            cluster.getMembers().clear();
            for (Point point : pointList) {
                if (point.getClusterId() == cluster.getId()) {
                    cluster.addPoint(point);
                }
            }
        }
    }

    /**
     * 计算每个类的中心位置
     *
     * @param clusterSet
     * @return
     */
    private boolean calculateCenter(Set<Cluster> clusterSet) {
        boolean ifNeedIter = false;
        for (Cluster cluster : clusterSet) {
            List<Point> point_list = cluster.getMembers();
            float[] sumAll = new float[len];
            // 所有点，对应各个维度进行求和
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < point_list.size(); j++) {
                    sumAll[i] += point_list.get(j).getLocalArray()[i];
                }
            }
            // 计算平均值
            for (int i = 0; i < sumAll.length; i++) {
                sumAll[i] = sumAll[i] / point_list.size();
            }
            // 计算两个新、旧中心的距离，如果任意一个类中心移动的距离大于dis_Diff则继续迭代
            if (disC.getEuclideanDis(cluster.getCenter(), new Point(sumAll)) > disDiff) {
                ifNeedIter = true;
            }
            // 设置新的类中心位置
            cluster.setCenter(new Point(sumAll));
        }
        return ifNeedIter;
    }

    /**
     * 返回实际运行次数
     *
     * @return
     */
    public int getIterTimes() {
        return iterRunTimes;
    }


}
