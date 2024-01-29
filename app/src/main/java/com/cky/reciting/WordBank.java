package com.cky.reciting;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordBank {
    private static String filename = "NewWord.json";
    public static JSONObject loadWord(Context context){
        String folderPath = context.getExternalFilesDir("").toString();
        File folder = new File(folderPath);
        if (!folder.exists())
            if (folder.mkdirs()) Toast.makeText(context, "文件夹创建成功", Toast.LENGTH_SHORT).show();
            else {Toast.makeText(context, "文件夹创建失败", Toast.LENGTH_SHORT).show();}

        File file = new File(folderPath, filename);
        if(!file.exists())
            try {
                if (file.createNewFile()) Toast.makeText(context, "文件创建成功", Toast.LENGTH_SHORT).show();
                else {Toast.makeText(context, "文件创建失败", Toast.LENGTH_SHORT).show();}
            } catch (IOException e) {
                Toast.makeText(context, "Exception", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        JSONObject jo = new JSONObject();
        try{
            InputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            jo = new JSONObject(json);
            Log.d("Word", "Data loaded successfully");
        } catch(IOException | JSONException e){
//            Toast.makeText(context, "读取文件失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace(); Log.d("Word", "Data loaded failed");
        }
        return jo;
    }

    public static void saveWord(Context context, JSONObject jsonObject){
        String folderPath = context.getExternalFilesDir("").toString();
        File file = new File(folderPath, filename);
        try{
            FileOutputStream fileOut = new FileOutputStream(file);
            String jsonString = jsonObject.toString();
            fileOut.write(jsonString.getBytes());
            fileOut.close();
            Log.d("Word", "Data saved successfully");
        } catch (IOException e) {
            Toast.makeText(context, "写入文件失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace(); Log.d("Word", "Data saved failed");
        }
    }

    public static String[] jsonAnalysis(JSONObject jo, String key){
        String[] content = new String[3];
        try{
            JSONArray jsonArray = jo.getJSONArray(key);
            content = new String[]{(String)jsonArray.get(0), (String)jsonArray.get(1), (String)jsonArray.get(2)};
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return content;
    }

    public static JSONObject jsonAppend(JSONObject jo, String key, String[] value){
        JSONObject jsonObject = jo;
        try{
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(value[0]);
            jsonArray.put(value[1]);
            jsonArray.put(value[2]);
            jsonObject.put(key, jsonArray);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject jsonRemove(JSONObject jo, String key){
        JSONObject jsonObject = new JSONObject();
        String k;
        JSONArray v;
        Iterator<String> keys;
        int order = 1;                    // 重新排列键的序号
        try{
            keys = jo.keys();
            while(keys.hasNext()){        // 去除键值对，并深拷贝
                k = keys.next();
                v = jo.getJSONArray(k);
                if(!k.equals(key)) jsonObject.put(String.valueOf(order++), v);
                keys.remove();            // 不能用jo.remove(k)，否则迭代器会出错
            }
            keys = jsonObject.keys();
            while(keys.hasNext()){        // 拷贝回原来的引用，防止adapter的JSONObject引用出现差异
                k = keys.next();
                v = jsonObject.getJSONArray(k);
                jo.put(k, v);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return jo;
    }
}
