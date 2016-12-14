package com.itcast.zbc.mediaplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取城市Until
 *
 * @author dyyy
 */
public class CityUtil {

    private static List<String> proset;
    private static List<String> prosetId;
    private static List<String> typeName;
    private static List<String> cityset;
    private static List<String> cityId;
    private static List<String> district;

    /**
     * 获取数据库存储的路径
     *
     * @param context
     * @return 返回路径成功，返回
     */
    public static String setDB(Context context) {

        return write(context);

    }

    private static String write(Context context) {
        String path = Util.getDownloadSDCardPath(context) + "/yirenbangProvinceDatabase.text";
        if (new File(path).exists()) {
//        	  new File(path).delete();
            return path;
        }

        InputStream inputStream;
        try {
//			inputStream = context.getResources().getAssets().open("db_weather.db");
//			inputStream = context.getResources().getAssets().open("yirenbang.db");
            inputStream = context.getResources().getAssets().open("yirenbangProvinceDatabase.db");
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            byte[] buffer = new byte[1024 * 10];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();

            return path;
        } catch (IOException e) {
            LogUtil.log("错误提示：" + e.toString());
            e.printStackTrace();
        }
        return "1";
    }

    /**
     * 查找省份
     *
     * @param file
     * @return
     */
    public static List<String> getProSet(String file) {
        proset = new ArrayList<String>();
        //打开数据库
        SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);
//        Cursor cursor=db1.query("fch_province", null, null, null, null, null, null);  
        Cursor cursor = db1.rawQuery("select * from quan_prov_city_area where topno=?", new String[]{"0"});
        while (cursor.moveToNext()) {
//        	String pro=cursor.getString(cursor.getColumnIndexOrThrow("provinceName")); 
            String pro = cursor.getString(cursor.getColumnIndexOrThrow("areaname"));
            proset.add(pro);
        }
        cursor.close();
        db1.close();
        return proset;
    }

    /**
     * 获取所有的省份对应 provinceId
     *
     * @param file
     * @return
     */
    public static List<String> getProId(String file) {
        prosetId = new ArrayList<String>();
        //打开数据库
        SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);
//        Cursor cursor=db1.query("fch_province", null, null, null, null, null, null);  
        Cursor cursor = db1.rawQuery("select * from quan_prov_city_area where topno=?", new String[]{"0"});
        while (cursor.moveToNext()) {
//        	String prId=cursor.getString(cursor.getColumnIndexOrThrow("provinceId")); 
            String prId = cursor.getString(cursor.getColumnIndexOrThrow("no"));
            prosetId.add(prId);
        }
        cursor.close();
        db1.close();

        return prosetId;
    }

    /**
     * 获取所有省或者直辖市的标示
     *
     * @param file 数据库保存的路径
     * @return
     */
    public static List<String> getTypeName(String file) {
        typeName = new ArrayList<String>();
        SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);
        Cursor cursor = db1.rawQuery("select * from quan_prov_city_area where topno=?", new String[]{"0"});
        while (cursor.moveToNext()) {
            String prId = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            typeName.add(prId);
        }
        cursor.close();
        db1.close();

        return typeName;
    }

    /**
     * 获取指定是否为直辖市
     *
     * @param
     * @return
     */
    public static String getTypeName(String file, String provinceId) {

        SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);
        Cursor cursor = db1.rawQuery("select * from quan_prov_city_area where no=?", new String[]{provinceId});
        while (cursor.moveToNext()) {
            String prId = cursor.getString(cursor.getColumnIndexOrThrow("typename"));
            return prId;
        }
        cursor.close();
        db1.close();

        return null;
    }


    /**
     * 查找城市
     *
     * @param file
     * @param pro_id 省份id
     * @return
     */
    public static List<String> getCitySet(String file, String pro_id) {
        cityset = new ArrayList<String>();
        //打开数据库
        SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);
//        Cursor cursor = db1.rawQuery("select * from fch_city where cityUpId=?", new String[]{pro_id});
        Cursor cursor = db1.rawQuery("select * from quan_prov_city_area where topno=?", new String[]{pro_id});
        while (cursor.moveToNext()) {
//    	   String cityName = cursor.getString(cursor.getColumnIndexOrThrow("cityName"));
            String cityName = cursor.getString(cursor.getColumnIndexOrThrow("areaname"));
            cityset.add(cityName);

        }
        cursor.close();
        db1.close();
        return cityset;
    }

    /**
     * 获取城市的cityId
     *
     * @return
     */
    public static List<String> getCityId(String file, String pro_id) {
        cityId = new ArrayList<String>();
        //打开数据库
        SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);
//		Cursor cursor = db1.rawQuery("select * from fch_city where cityUpId=?", new String[]{pro_id});
        Cursor cursor = db1.rawQuery("select * from quan_prov_city_area where topno=?", new String[]{pro_id});
        while (cursor.moveToNext()) {
//			String citId = cursor.getString(cursor.getColumnIndexOrThrow("cityId"));
            String citId = cursor.getString(cursor.getColumnIndexOrThrow("no"));
            cityId.add(citId);
        }
        cursor.close();
        db1.close();

        return cityId;

    }

    /**
     * 获取区或者县
     *
     * @param file
     * @param
     * @return
     */
    public static List<String> getDistrict(String file, String city_Id) {
        district = new ArrayList<String>();
        //打开数据库
        SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(file, null);
//		Cursor cursor = db1.rawQuery("select * from fch_district where districtUpId=?", new String[]{city_Id});
        Cursor cursor = db1.rawQuery("select * from quan_prov_city_area where topno=?", new String[]{city_Id});
        while (cursor.moveToNext()) {
//			String city=cursor.getString(cursor.getColumnIndexOrThrow("districtName"));
            String city = cursor.getString(cursor.getColumnIndexOrThrow("areaname"));
            LogUtil.log("区----" + city);
            district.add(city);
        }
        cursor.close();
        db1.close();
        return district;
    }


}
