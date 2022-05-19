package com.example.myapplication;

import androidx.annotation.VisibleForTesting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.json.JSONArray;

import com.alibaba.fastjson.JSONObject;
import org.json.JSONException;

public class CheckGPSList {
    //声明交点的GPS坐标：
    public String x;
    public String y;

    //添加三个公有的成员变量：
    public String diag_String;
    public String comp_String;
    public String result1;
    public String result2;
    public boolean result;
    // 添加日期信息：
    public String date;
    //时间与GPS信息：
    public String starttime;
    public String endtime;
    public String gps;
    //flag判断对应的为跟患者比还是B类比较：
    public int WhatType;
    public String pos;
    //compare成员信息：
    public int id;
    public String name;
    public String card;
    public String tele;
    //添加构造方法：flag作为判断是comp还是diag,date则是传参中将日期传进来：
    public CheckGPSList(String string1,String string2,String date,int WhatType) {
        this.date = date;
        this.comp_String = string1;
        this.diag_String = string2;
        this.WhatType = WhatType;
    }

    //main函数非static，仅仅作为函数操作入口：
    public void main(int id,String name,String card,String tele) throws IOException {
        //compare信息：成员变量赋值：
        this.id = id;
        this.name = name;
        this.card = card;
        //集合信息用于存取对应的确诊和比对
        ArrayList<Line> diagnose = new ArrayList<>();
        ArrayList<Line> compare = new ArrayList<>();

        if (!(diag_String == null || "".equals(diag_String))) {
            //读取文件：存储两个集合
            getInformationFromString(diagnose, 0);
            getInformationFromString(compare, 1);
            //word用于存放最后需要输出的内容：
            String word = "";
            //System.out.println(diag_String);
            //System.out.println(comp_String);
            //创建两个下标，用于表示diagnose和compare的下标：
            int i = 0;
            int j = 0;
            System.out.println(diagnose.size());
            System.out.println(compare.size());
            if (diagnose.size() == 0) {
                this.result = false;
            } else {
                String[] id_dia = {};   //String数组用于记录比较的id号，默认提供数据在3个患者
                int index = 0;          //index作为访问数组的索引
                for (; i < diagnose.size(); i++) {
                    Line diagnose_line = diagnose.get(i);
                    String id_diagnose = diagnose_line.getId();     //记录当前的患者编号
                    if (id_dia[index] == null || id_dia[index].equals(id_diagnose)) {
                        id_dia[index] = id_diagnose;
                        index = index + 1;
                    }
                    if (id_dia.length >= 3) {
                        break;                  //默认记录三个患者的信息
                    }
                    int fmin1 = diagnose_line.getMinute1();
                    int fmin2 = diagnose_line.getMinute2();
                    String t1 = diagnose_line.getTime1();
                    String t2 = diagnose_line.getTime2();
                    word = word + "Daignose from" + t1 + "-" + t2 + ":\n";
                    for (j = 0; j < compare.size(); j++) {
                        Line compare_line = compare.get(j);
                        int smin1 = compare_line.getMinute1();
                        int smin2 = compare_line.getMinute2();

                        System.out.println(i + " " + j);
                        //判断时间是否差为10min或时间相等：
                        if ((fmin1 == smin1 && fmin2 == smin2) || ((smin1 - fmin1) >= 0 && Math.abs(smin2 - fmin2) <= 10)) {
                            System.out.println(i + " " + j);
                            //读取结束后类中有两个类：diagnose类和compare类:
                            int flag = JudgeCross(diagnose_line, compare_line);
                            String time1 = compare_line.getTime1();
                            String time2 = compare_line.getTime2();
                            if (flag == 1 || flag == 2 || flag == 3) {
                                this.result1 = "B";
                                this.result2 = "C";
                                //表示当天有密接
                            } else if (flag == 4) {
                                //时空伴随都是C
                                this.result1 = "C";
                                this.result2 = "Not";
                            } else {
                                this.result1 = "Not";
                                this.result2 = "Not";
                                //表示当天未密接
                            }
                        }
                        else
                        {
                            this.result1 = "Not";
                            this.result2 = "Not";
                        }
                        //变灯判断与文件上传：
                        judge_and_submit();
                    }
                }
            }
        }
    }

    //方法1：导入String
    public void getInformationFromString(ArrayList<Line> Line_prepare, int flag) throws IOException
    {
        //x1,x2,y1,y2,time1,time2用于存储对应的信息：
        double x1 = 0.0;
        double x2 = 0.0;
        double y1 = 0.0;
        double y2 = 0.0;
        String time1 = " ";
        String time2 = " ";
        //首先判断是comp还是diag：flag依旧为1表示comp，为0表示diag
        if(flag == 1) {
            String[] info = this.comp_String.split("\n");
            int i = 0;

            for(;i<info.length-1;i++) {
                String tt1[] = info[i].split(" ");
                x1 = Double.parseDouble(tt1[2]);
                y1 = Double.parseDouble(tt1[3]);
                time1 = tt1[1];

                String tt2[] = info[i+1].split(" ");
                x2 = Double.parseDouble(tt2[2]);
                y2 = Double.parseDouble(tt2[3]);
                time2 = tt2[1];

                //省市位置信息
                this.pos = tt2[4];

                Line line = new Line(x1,x2,y1,y2,time1,time2);
                Line_prepare.add(line);
            }
        }
        else if(flag == 0) {
            //String id用于记录患者id
            String id = " ";
            String[] info = this.diag_String.split("\n");
            if (info.length != 1) {
                //首先记录第一行id：
                String[] info_split = info[1].split(":");
                id = info_split[info_split.length - 1];
                int i = 2;
                //直接从第三行开始读

                for (; i < info.length - 1; i++) {
                    //如果是id的形式我们将跳行：
                    if (info[i + 1].charAt(0) == 'i') {
                        String[] info_split1 = info[i + 1].split(":");
                        id = info_split1[info_split1.length - 1];       //记录id
                        i = i + 2;  //直接跳行
                        break;
                    }
                    String tt1[] = info[i].split(" ");
                    x1 = Double.parseDouble(tt1[2]);
                    y1 = Double.parseDouble(tt1[3]);
                    time1 = tt1[1];

                    String tt2[] = info[i + 1].split(" ");
                    x2 = Double.parseDouble(tt2[2]);
                    y2 = Double.parseDouble(tt2[3]);
                    time2 = tt2[1];

                    //省市位置信息
                    this.pos = tt2[4];

                    Line line = new Line(x1, x2, y1, y2, time1, time2);
                    line.setId(id);   //将患者id set进去
                    Line_prepare.add(line);
                }
            }
        }
    }


    public int JudgeCross(Line Line1, Line Line2) { //传入两个Line类的变量
        //第一条直线两点
        double fx1 = Line1.getX1();
        double fy1 = Line1.getY1();
        double fx2 = Line1.getX2();
        double fy2 = Line1.getY2();
        //第二条直线两点
        double sx1 = Line2.getX1();
        double sy1 = Line2.getY1();
        double sx2 = Line2.getX2();
        double sy2 = Line2.getY2();
        //记录两条直线的交点的经纬度
        double x_temp;
        double y_temp;
        //两个时间点：
        int fminute1 = Line1.getMinute1();
        int fminute2 = Line1.getMinute2();
        int sminute1 = Line2.getMinute1();
        int sminute2 = Line2.getMinute2();

        double k1;
        double b1;
        double b2;
        double k2;

        //首先进行预判断进行相关剪枝：
        //预判断距离±3km（经纬度±0.015）
        double xmin = min(fx1,fx2)-0.015;
        double xmax = max(fx1,fx2)+0.015;
        double ymin = min(fy1,fy2)-0.015;
        double ymax = max(fy1,fy2)+0.015;
        if((sx1 < xmin && sx2 < xmin) || (sy1 < ymin && sy2 < ymin) || (sx1 > xmax && sx2 > xmax) || (sy1 > ymax && sy2 > ymax))
        {
            return 0;
        }

        //其次是精判断：
        //首先是同时空判断：
        //两个人的坐标都是点（这段时间内都没动）：
        if(fminute1 == sminute1 && fminute2 == sminute2) {
            if((fx1 == fx2 && fy1 == fy2)&&(sx1 == sx2 && sy1 == sy2))
            {
                double d = Math.sqrt(Math.pow((fx1-sx1),2)+Math.pow((fy1-sy2), 2));
                if(d <= 0.0005)
                {
                    String x_string = String.valueOf(sx1);
                    String y_string = String.valueOf(sy1);
                    this.x = x_string;
                    this.y = y_string;
                    return 1;
                }

                else return 0;
            }
            //diagnose是点，compare是一条线且斜率不存在
            if((fx1 == fx2) && (fy1 == fy2) && (sx1 == sx2))
            {
                //d为点到直线的距离
                double d = Math.abs(fx1-sx1);
                //d1为diagnose点到compare第一个点的距离，d2是diagnose点到compare第二个点的距离
                double d1 = Math.sqrt(Math.pow((fx1-sx1),2)+Math.pow((fy1-sy1), 2));
                double d2 = Math.sqrt(Math.pow((fx1-sx2),2)+Math.pow((fy1-sy2), 2));
                if((d <= 0.0005 && (fy1 >= min(sy1,sy2) && (fy1 <= max(sy1,sy2)))) || d1<=0.0005 || d2<=0.0005 )
                {
                    String x_string = String.valueOf(sx2);
                    String y_string;
                    if(d1 <= d2)
                    {
                        y_string = String.valueOf(sy1);
                    }
                    else
                        y_string = String.valueOf(sy2);
                    x = x_string;
                    y = y_string;
                    return 1;
                }
                else return 0;
            }

            //diagnose是点，compare是一条线且斜率存在
            if((fx1 == fx2) && (fy1 == fy2) && (sx1 != sx2))
            {
                k2 = Line2.makeK();
                b2 = Line2.makeB();
                //d为点到直线的距离
                double d = Math.abs(k2*fx1-fy1+b2)/Math.sqrt(Math.pow(k2,2)+1);
                //d1为diagnose点到compare第一个点的距离，d2是diagnose点到compare第二个点的距离
                double d1 = Math.sqrt(Math.pow((fx1-sx1),2)+Math.pow((fy1-sy1), 2));
                double d2 = Math.sqrt(Math.pow((fx1-sx2),2)+Math.pow((fy1-sy2), 2));
                if((d < 0.0005 && (fy1 >= min(sy1,sy2) && (fy1 <= max(sy1,sy2)))) || d1<0.0005 || d2<0.0005 )
                {
                    String x_string = String.valueOf(sx2);
                    String y_string;
                    if(d1 <= d2)
                    {
                        y_string = String.valueOf(sy1);
                    }
                    else
                        y_string = String.valueOf(sy2);
                    x = x_string;
                    y = y_string;
                    return 1;
                }
                else return 0;
            }

            //diagnose是一条线且斜率不存在，compare是点（原地不动）
            if((fx1 == fx2) && (sy1 == sy2) && (sx1 == sx2))
            {
                //d为点到直线的距离
                double d = Math.abs(fx1-sx1);
                //d1为compare点到diagnose第一个点的距离，d2是compare点到diagnose第二个点的距离
                double d1 = Math.sqrt(Math.pow((fx1-sx1),2)+Math.pow((fy1-sy1), 2));
                double d2 = Math.sqrt(Math.pow((fx2-sx1),2)+Math.pow((fy2-sy1), 2));
                if((d < 0.0005 && (sy1 >= min(fy1,fy2) && (sy1 <= max(fy1,fy2)))) || d1<0.0005 || d2<0.0005 )
                {
                    String x_string = String.valueOf(sx1);
                    String y_string;
                    y_string = String.valueOf(sy1);
                    x = x_string;
                    y = y_string;
                    return 1;
                }
                else return 0;
            }

            //diagnose是一条线且斜率存在，compare是点（原地不动）
            if((fx1 != fx2) && (sy1 == sy2) && (sx1 == sx2))
            {
                k1 = Line1.makeK();
                b1 = Line1.makeB();
                //d为点到直线的距离
                double d = Math.abs(k1*sx1-sy1+b1)/Math.sqrt(Math.pow(k1,2)+1);
                //d1为compare点到diagnose第一个点的距离，d2是compare点到diagnose第二个点的距离
                double d1 = Math.sqrt(Math.pow((fx1-sx1),2)+Math.pow((fy1-sy1), 2));
                double d2 = Math.sqrt(Math.pow((fx2-sx1),2)+Math.pow((fy2-sy1), 2));
                if((d < 0.0005 && (sy1 >= min(fy1,fy2) && (sy1 <= max(fy1,fy2)))) || d1<0.0005 || d2<0.0005 )
                {
                    String x_string = String.valueOf(sx1);
                    String y_string;
                    y_string = String.valueOf(sy1);
                    x = x_string;
                    y = y_string;
                    return 1;
                }
                else return 0;
            }

            //diagnose是直线且斜率不存在，compare是直线斜率存在
            if((fx1 == fx2) && (sx1 != sx2))
            {
                k2 = Line2.makeK();
                b2 = Line2.makeB();
                y_temp = k2*fx1 + b2;
                x_temp = fx1;
                if((y_temp >= fy1) && (y_temp <= fy2) && (y_temp >= sy1) && (y_temp <= fy2))
                {
                    String x_string = String.valueOf(x_temp);
                    String y_string = String.valueOf(y_temp);
                    x = x_string;
                    y = y_string;
                    return 1;
                }
                else return 0;
            }


            //第一条直线斜率存在，第二条直线斜率不存在
            if((fx1 != fx2) && (sx1 == sx2))
            {
                k1 = Line1.makeK();
                b1 = Line1.makeB();
                y_temp = k1*sx1 + b1;
                x_temp = sx1;
                if((y_temp >= fy1) && (y_temp <= fy2) && (y_temp >= sy1) && (y_temp <= fy2))
                {
                    String x_string = String.valueOf(x_temp);
                    String y_string = String.valueOf(y_temp);
                    x = x_string;
                    y = y_string;
                    return 1;
                }
                else return 0;
            }

            //两条直线斜率都不存在
            if((fx1 == fx2) && (sx1 == sx2))
            {
                if((fx1 == sx1) && (max(min(fy1,fy2),min(sy1,sy2)) <= min(max(fy1,fy2),max(sy1,sy2))) )
                {
                    String x_string = String.valueOf(fx1);
                    String y1 = String.valueOf(max(min(fy1,fy2),min(sy1,sy2)));
                    String y2 = String.valueOf(min(max(fy1,fy2),max(sy1,sy2)));
                    String y_string = y1+"-"+y2;
                    x = x_string;
                    y = y_string;
                    return 2;
                }
                else return 0;
            }

            //两条直线斜率都存在
            k1 = Line1.makeK();
            b1 = Line1.makeB();
            k2 = Line2.makeK();
            b2 = Line2.makeB();
            if(k1 != k2)
            {
                x_temp = (b1-b2)/(k2-k1);
                if ((x_temp <= Line1.getX2() && x_temp >= Line1.getX1())&&(x_temp <= Line2.getX2() && x_temp >= Line2.getX1())) {
                    //x在上四个点中两点之间说明有相交，计算交点x，y
                    y_temp = k1*x_temp + b1;
                    String x_string = String.valueOf(x_temp);
                    String y_string = String.valueOf(y_temp);
                    x = x_string;
                    y = y_string;
                    return 1;
                }
                else return 0;
            }else if((fy1 == sy1) && (max(min(fx1,fx2),min(sx1,sx2)) <= min(max(fx1,fx2),max(sx1,sx2))))
            {
                String x1 = String.valueOf(max(min(fx1,fx2),min(sx1,sx2)));
                String x2 = String.valueOf(min(max(fx1,fx2),max(sx1,sx2)));
                String x_string = x1+"-"+x2;
                String y_string = String.valueOf(fy1);
                x = x_string;
                y = y_string;
                return 3;
            }
        }

        //第二类判断：时空伴随
        else if((sminute1-fminute1) >= 0 && Math.abs(sminute2-fminute2)<=10 ){
            //不算中间的，只管起始点和终止点
            //先计算对应的边界: 在0.8*0.8km的网格中：换算至经纬度为：±0.004
            double x1min = fx1 - 0.004;
            double x1max = fx1 + 0.004;
            double y1min = fy1 - 0.004;
            double y1max = fy1 + 0.004;

            double x2min = fx2 - 0.004;
            double x2max = fx2 + 0.004;
            double y2min = fy2 - 0.004;
            double y2max = fy2 + 0.004;

            //判断条件：处于起始点和终止点两个时空网格中：
            if((sx1>=x1min && sx1<=x1max && sy1>=y1min && sy1<=y1max)||(sx2>=x2min && sx2<=x2max && sy2>=y2min && sy2<=y2max)) {
                return 4;
            }
        }
        return 0;
    }

    //计算两个数的最小值
    private static double min(double a, double b) {
        double min;
        if(a<=b)
        {
            min = a;
        }
        else
            min =b;
        return min;
    }

    private static double max(double a, double b) {
        double max;
        if(a>=b)
        {
            max = a;
        }
        else
            max =b;
        return max;
    }

    public void judge_and_submit() {
        if (this.result1.equals("B")) {
            this.result = true;
            //TODO：submit the trace
            //upload_file_to_db1("D",id,name,tele,card);
            //upload_file_to_db2(id,gps,pos,card,stime,etime);
        } else if (result1.equals("C")) {
            this.result = true;
            //上传数据：但是需要区分B,C上传的数据位置
            //TODO: submit the trace: Ctype
            //upload_file_to_db1("E",id,name,tele,card);
            //upload_file_to_db2(id,gps,pos,card,stime,etime);
        } else {
            //跟B类密接比:
            if (result2.equals("C")) {
                //上传数据：但是需要区分B,C上传的数据位置
                this.result = true;
                //upload_file_to_db1("E",id,name,tele,card);
                //upload_file_to_db2(id,gps,pos,card,stime,etime);
            } else {
                this.result = false;
            }
        }
    }

    public void upload_file_to_db1(String type, int id, String name, String tele, String card) throws JSONException{
        String url1 = "http://127.0.0.1:8000/insertuser/";
        //上传文件涉及的参数
        //首先制作json文件：即需要上传的json
        //jsonOBJ1上传表2：column：type，user_id，name，phone，id_card，date，state
        try{
            if(type.equals("D")) {
                JSONObject jsonOBJ1 = new JSONObject(new LinkedHashMap());    //D类
                jsonOBJ1.put("type", WhatType);
                jsonOBJ1.put("user_id", id);  //id此后留一个传参接口
                jsonOBJ1.put("name", name);    //name也留
                jsonOBJ1.put("phone", tele);    //phone也留
                jsonOBJ1.put("id_card", card); //card也留
                jsonOBJ1.put("date", this.date);
                jsonOBJ1.put("state", null); //留接口

                //Submit JsON
                Submit submit = new Submit(jsonOBJ1,url1);
                submit.submit();
            }
            else if(type.equals("E")) {
                JSONObject jsonOBJ2 = new JSONObject(new LinkedHashMap());    //E类
                jsonOBJ2.put("type", WhatType);
                jsonOBJ2.put("user_id", id);  //id此后留一个传参接口
                jsonOBJ2.put("name", name);    //name也留
                jsonOBJ2.put("phone", tele);    //phone也留
                jsonOBJ2.put("id_card", card); //card也留
                jsonOBJ2.put("date", this.date);
                jsonOBJ2.put("state", null); //留接口

                //Submit JSON
                Submit submit = new Submit(jsonOBJ2, url1);
                submit.submit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upload_file_to_db2(int id, String gps, String pos, String card, String stime, String etime) throws JSONException{
        //jsonOBJ2上传表3：column：tracetype，user_id，date，gps，pos，time
        try{
            JSONObject jsonOBJ3 = new JSONObject(new LinkedHashMap());
            jsonOBJ3.put("tracetype", "trace");  //类型为时间段类型
            jsonOBJ3.put("user_id", id);  //id此后留一个传参接口
            jsonOBJ3.put("date", this.date);
            jsonOBJ3.put("gps", gps);    //保留接口
            jsonOBJ3.put("pos", pos);   //位置也留
            jsonOBJ3.put("start_time", stime);    //time可以传参实现
            jsonOBJ3.put("end_time", etime);
            String url2 = "http://127.0.0.1:8000/inserttrace/";

            //Submit
            Submit submit = new Submit(jsonOBJ3,url2);
            submit.submit();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

