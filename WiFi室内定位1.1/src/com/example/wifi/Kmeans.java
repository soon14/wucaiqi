package com.example.wifi;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

/**
 * K均值聚类算法
 */
public class Kmeans {
	private int k;// 分成多少簇
	private int m;// 迭代次数
	private int dataSetLength;// 数据集元素个数，即数据集的长度
	private ArrayList<double[]> dataSet;// 数据集链表
	private ArrayList<double[]> center;// 中心链表
	private ArrayList<ArrayList<double[]>> cluster; // 簇
	private ArrayList<Double> jc;// 误差平方和，k越接近dataSetLength，误差越小
	private Random random;

	/**
	 * 设置需分组的原始数据集
	 * 
	 * @param dataSet
	 */

	public void setDataSet(ArrayList<double[]> dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * 获取结果分组
	 * 
	 * @return 结果集
	 */

	public ArrayList<ArrayList<double[]>> getCluster() {
		return cluster;
	}
	
	/**
	 * 获取结果
	 * 
	 * @return 簇中心集
	 */

	public ArrayList<double[]> getCenter() {
		return center;
	}
	
	/**
	 * 构造函数，传入需要分成的簇数量
	 * 
	 * @param k
	 *            簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
	 */
	public Kmeans(int k) {
		if (k <= 0) {
			k = 1;
		}
		this.k = k;
	}

	/**
	 * 初始化
	 */
	private void init() {
		m = 0;
		random = new Random();
		if (dataSet == null || dataSet.size() == 0) {
			initDataSet();
		}
		dataSetLength = dataSet.size();
		if (k > dataSetLength) {
			k = dataSetLength;
		}
		center = initCenters();
		cluster = initCluster();
		jc = new ArrayList<Double>();
	}

	/**
	 * 如果调用者未初始化数据集，则采用内部测试数据集
	 */
	private void initDataSet() {
		dataSet = new ArrayList<double[]>();
		// 其中{6,3}是一样的，所以长度为15的数据集分成14簇和15簇的误差都为0

		double[][] dataSetArray = new double[][] { { 8, 2 }, { 3, 4 },
				{ 2, 5 }, { 4, 2 }, { 7, 3 }, { 6, 2 }, { 4, 7 }, { 6, 3 },
				{ 5, 3 }, { 6, 3 }, { 6, 9 }, { 1, 6 }, { 3, 9 }, { 4, 1 },
				{ 8, 6 } };

		for (int i = 0; i < dataSetArray.length; i++) {
			dataSet.add(dataSetArray[i]);
		}
	}

	/**
	 * 初始化中心数据链表，分成多少簇就有多少个中心点
	 * 
	 * @return 中心点集
	 */
	private ArrayList<double[]> initCenters() {
		ArrayList<double[]> center = new ArrayList<double[]>();
		int[] randoms = new int[k];
		boolean flag;
		int temp = random.nextInt(dataSetLength);
		randoms[0] = temp;
		for (int i = 1; i < k; i++) {
			flag = true;
			while (flag) {
				temp = random.nextInt(dataSetLength);
				int j = 0;
				// 不清楚for循环导致j无法加1
				// for(j=0;j<i;++j)
				// {
				// if(temp==randoms[j]);
				// {
				// break;
				// }
				// }
				while (j < i) {
					if (temp == randoms[j]) {
						break;
					}
					j++;
				}
				if (j == i) {
					flag = false;
				}
			}
			randoms[i] = temp;
		}

		// 测试随机数生成情况
		// for(int i=0;i<k;i++)
		// {
		// System.out.println("test1:randoms["+i+"]="+randoms[i]);
		// }

		// System.out.println();
		for (int i = 0; i < k; i++) {
			center.add(dataSet.get(randoms[i]));// 生成初始化中心链表
		}
		return center;
	}

	/**
	 * 初始化簇集合
	 * 
	 * @return 一个分为k簇的空数据的簇集合
	 */
	private ArrayList<ArrayList<double[]>> initCluster() {
		ArrayList<ArrayList<double[]>> cluster = new ArrayList<ArrayList<double[]>>();
		for (int i = 0; i < k; i++) {
			cluster.add(new ArrayList<double[]>());
		}

		return cluster;
	}

	/**
	 * 计算两个点之间的距离
	 * 
	 * @param ds
	 *            点1
	 * @param center
	 *            点2
	 * @return 距离
	 */
	private double distance(double[] ds, double[] center) {
		double distance = 0.0f;
		double d1 = ds[0] - center[0];
		double d2 = ds[1] - center[1];
		double d3 = ds[2] - center[2];
		double d4 = ds[3] - center[3];
		double d5 = ds[4] - center[4];
		double d = d1 * d1 + d2 * d2 + d3 * d3 + d4 * d4 + d5 * d5;
		distance = (double) Math.sqrt(d);

		return distance;
	}

	/**
	 * 获取距离集合中最小距离的位置
	 * 
	 * @param distance
	 *            距离数组
	 * @return 最小距离在距离数组中的位置
	 */
	private int minDistance(double[] distance) {
		double minDistance = distance[0];
		int minLocation = 0;
		for (int i = 1; i < distance.length; i++) {
			if (distance[i] < minDistance) {
				minDistance = distance[i];
				minLocation = i;
			} else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
			{
				if (random.nextInt(10) < 5) {
					minLocation = i;
				}
			}
		}

		return minLocation;
	}

	/**
	 * 核心，将当前元素放到最小距离中心相关的簇中
	 */
	private void clusterSet() {
		double[] distance = new double[k];
		for (int i = 0; i < dataSetLength; i++) {
			for (int j = 0; j < k; j++) {
				distance[j] = distance(dataSet.get(i), center.get(j));
				// System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);

			}
			int minLocation = minDistance(distance);
			// System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
			// System.out.println();

			cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

		}
	}

	/**
	 * 求两点误差平方的方法
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 误差平方
	 */
	private double errorSquare(double[] element, double[] center) {
		// double x = element[0] - center[0];
		// double y = element[1] - center[1];

		double d1 = element[0] - center[0];
		double d2 = element[1] - center[1];
		double d3 = element[2] - center[2];
		double d4 = element[3] - center[3];
		double d5 = element[4] - center[4];
		double errSquare = d1 * d1 + d2 * d2 + d3 * d3 + d4 * d4 + d5 * d5;
		return errSquare;
	}

	/**
	 * 计算误差平方和准则函数方法
	 */
	private void countRule() {
		double jcF = (double) 0;
		for (int i = 0; i < cluster.size(); i++) {
			for (int j = 0; j < cluster.get(i).size(); j++) {
				jcF = jcF + errorSquare(cluster.get(i).get(j), center.get(i));

			}
		}
		jc.add(jcF);
	}

	/**
	 * 设置新的簇中心方法
	 */
	private void setNewCenter() {
		for (int i = 0; i < k; i++) {
			int n = cluster.get(i).size();
			if (n != 0) {
				double[] newCenter = { 0, 0, 0, 0, 0 };
				for (int j = 0; j < n; j++) {
					newCenter[0] += cluster.get(i).get(j)[0];
					newCenter[1] += cluster.get(i).get(j)[1];
					newCenter[2] += cluster.get(i).get(j)[2];
					newCenter[3] += cluster.get(i).get(j)[3];
					newCenter[4] += cluster.get(i).get(j)[4];
				}
				// 设置一个平均值
				newCenter[0] = newCenter[0] / n;
				newCenter[1] = newCenter[1] / n;
				newCenter[2] = newCenter[2] / n;
				newCenter[3] = newCenter[3] / n;
				newCenter[4] = newCenter[4] / n;
				center.set(i, newCenter);
			}
		}
	}

	/**
	 * 打印数据，测试用
	 * 
	 * @param dataArray
	 *            数据集
	 * @param dataArrayName
	 *            数据集名称
	 */
	public void printDataArray(ArrayList<double[]> dataArray,
			String dataArrayName) {
		for (int i = 0; i < dataArray.size(); i++) {
			Log.e("print:", dataArrayName + "[" + i + "]={"
					+ dataArray.get(i)[0] + "," + dataArray.get(i)[1] + ","
					+ dataArray.get(i)[2] + "," + dataArray.get(i)[3] + ","
					+ dataArray.get(i)[4] + "}");
			// System.out.println("print:" + dataArrayName + "[" + i + "]={"
			// + dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}");
		}
		System.out.println("===================================");
	}

	/**
	 * 打印数据，测试用
	 * 
	 * @param dataArray
	 *            数据集
	 * @param dataArrayName
	 *            数据集名称
	 */
	public void printCenterData(ArrayList<double[]> center) {
		for (int i = 0; i < center.size(); i++) {
			Log.e("print:", "center: " + (i + 1) + "={"
					+ center.get(i)[0] + "," + center.get(i)[1] + ","
					+ center.get(i)[2] + "," + center.get(i)[3] + ","
					+ center.get(i)[4] + "}");
			// System.out.println("print:" + dataArrayName + "[" + i + "]={"
			// + dataArray.get(i)[0] + "," + dataArray.get(i)[1] + "}");
		}
		System.out.println("===================================");
	}
	
	/**
	 * Kmeans算法核心过程方法
	 */
	private void kmeans() {
		init();
		// printDataArray(dataSet,"initDataSet");
		// printDataArray(center,"initCenter");

		// 循环分组，直到误差不变为止
		while (true) {
			clusterSet();
			// for(int i=0;i<cluster.size();i++)
			// {
			// printDataArray(cluster.get(i),"cluster["+i+"]");
			// }

			countRule();

			// System.out.println("count:"+"jc["+m+"]="+jc.get(m));

			// System.out.println();
			// 误差不变了，分组完成
			if (m != 0) {
				if (jc.get(m) - jc.get(m - 1) == 0) {
					break;
				}
			}

			setNewCenter();
			// printDataArray(center,"newCenter");
			m++;
			cluster.clear();
			cluster = initCluster();
		}

		// System.out.println("note:the times of repeat:m="+m);//输出迭代次数
	}

	/**
	 * 执行算法
	 */
	public void execute() {
		long startTime = System.currentTimeMillis();
		System.out.println("kmeans begins");
		kmeans();
		long endTime = System.currentTimeMillis();
		System.out.println("kmeans running time=" + (endTime - startTime)
				+ "ms");
		System.out.println("kmeans ends");
		System.out.println();
	}
}