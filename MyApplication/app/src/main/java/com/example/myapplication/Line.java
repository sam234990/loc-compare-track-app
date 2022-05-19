package com.example.myapplication;

public class Line {
    private double k;
    private double b; //直线的斜率和截距
    public double x1; //第一个时间点的x坐标（经度)
    public double y1; //第一个时间点的y坐标（纬度）
    public double x2; //第二个时间点的x坐标（经度)
    public double y2; //第二个时间点的y坐标（纬度)

    public String time1;	//第一个点对应的时间点
    public String time2;  	//第二个点对应的时间点

    private int minute1;		//存放第一个时间点对应的分钟
    private int minute2;		//存放第二个时间点对应的分钟

    public String id;   //记录diagnose的患者编号

    //get和set方法：
    public double getX1() {
        return x1;
    }
    public void setX1(double x1) {
        this.x1 = x1;
    }
    public double getY1() {
        return y1;
    }
    public void setY1(double y1) {
        this.y1 = y1;
    }
    public double getX2() {
        return x2;
    }
    public void setX2(double x2) {
        this.x2 = x2;
    }
    public double getY2() {
        return y2;
    }
    public void setY2(double y2) {
        this.y2 = y2;
    }
    public double getK() {
        return k;
    }
    public void setK(double k) {
        this.k = k;
    }
    public double getB() {
        return b;
    }
    public void setB(double b) {
        this.b = b;
    }
    public int getMinute1() {
        return minute1;
    }
    public void setMinute1(int minute1) {
        this.minute1 = minute1;
    }
    public int getMinute2() {
        return minute2;
    }
    public void setMinute2(int minute2) {
        this.minute2 = minute2;
    }
    public String getTime1() {
        return time1;
    }
    public void setTime1(String time1) {
        this.time1 = time1;
    }
    public String getTime2() {
        return time2;
    }
    public void setTime2(String time2) {
        this.time2 = time2;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    //构造方法
    //空参构造
    public Line() {
        super();
    }

    //带输入两个坐标点的构造方法：
    public Line(double d, double e, double f, double g, String time1, String time2) {
        super();
        this.x1 = d;
        this.x2 = e;
        this.y1 = f;
        this.y2 = g;
        String s1[] = time1.split(":");
        this.minute1 = Integer.parseInt(s1[0])*60 + Integer.parseInt(s1[1]);
        this.time1 = time1;
        String s2[] = time2.split(":");
        this.minute1 = Integer.parseInt(s2[0])*60 + Integer.parseInt(s2[1]);
        this.time2 = time2;
    }

    //方法：创造直线：获取两个k，b
    public double makeK() {
        this.k = (this.y1-this.y2)/(this.x1-this.x2);
        return this.k;
    }

    public double makeB() {
        this.b = this.y1 - this.k * this.x1;
        return this.b;
    }
}
