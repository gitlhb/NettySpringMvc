package com.netty.nioserver;

/**
 * 冒泡排序
 * Created by liuhongbing on 2018/4/23.
 */

public class SortDemo {
    public static void main(String[] args) {
        int[] arr = {1, 9, 22, 12, 34, 999, 64, 100, -1, 87, 33};
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                int temp = 0;
                if (arr[j] > arr[j + 1]) {
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        for (int a : arr
                ) {
            System.out.println(a);
        }
    }

    private SortDemo() {

    }

    private static SortDemo sortDemo = null;

    /**
     * 获取一个单例
     *
     * @return SortDemo的单例
     */
    public static SortDemo getSortDemo() {
        if (sortDemo == null) {
            synchronized (SortDemo.class) {
                if (sortDemo == null) {
                    sortDemo = new SortDemo();
                }
            }
        }
        return sortDemo;
    }
}
