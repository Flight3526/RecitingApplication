package com.cky.reciting;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int time = 0;
    int[] num_arr;
    Random ran = new Random();
    JSONObject jsonObject;
    JSONArray jsonArray;
    String[] value;
    TextView order, word, pronun, meaning;
    Button prior, next, notice, translate;
    Boolean notice_flag = false;
    ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);                     // 取消默认标题栏
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {});
        try {
            InputStream inputStream = getAssets().open("word.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            jsonObject = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "读取失败", Toast.LENGTH_SHORT).show();
        }

        //获得随机数数组
        int r,t;
	    num_arr = new int[jsonObject.length() + 10];
        for(int i = 0; i< jsonObject.length(); i++)
            num_arr[i]=i+1;
        for(int k=0; k<5; k++)                        //打乱数组
            for(int i = 0; i< jsonObject.length(); i++) {
                r=ran.nextInt(jsonObject.length());
                t=num_arr[i];
                num_arr[i]=num_arr[r];
                num_arr[r]=t;
            }

        order = findViewById(R.id.order);
        word = findViewById(R.id.word);
        pronun = findViewById(R.id.pronun);
        meaning = findViewById(R.id.meaning);
        prior = findViewById(R.id.button_prior);
        next = findViewById(R.id.button_next);
        notice = findViewById(R.id.button_notice);
        translate = findViewById(R.id.button_translate);

        prior.setOnClickListener(v -> {
            if (time > 1) {
                time -= 2;
                value = WordBank.jsonAnalysis(jsonObject, "" + num_arr[time++]);
                order.setText("" + time);
                word.setText(value[0]);
                pronun.setText(value[1]);
                meaning.setText("");
                translate.setText("翻译");
            }
        });
        next.setOnClickListener(v -> {
            if(time < jsonObject.length()) {
                value = WordBank.jsonAnalysis(jsonObject, "" + num_arr[time++]);
                order.setText("" + time);
                word.setText(value[0]);
                pronun.setText(value[1]);
                meaning.setText("");
                translate.setText("翻译");
            }
        });
        translate.setOnClickListener(v -> {
            if(time < 1) return;
            if(translate.getText().toString().equals("翻译")) {
                meaning.setText(value[2]);
                translate.setText("隐藏");
            }
            else{
                meaning.setText("");
                translate.setText("翻译");
            }
        });
        notice.setOnClickListener(v -> {
            if(time < 1) return;
            JSONObject newJsonObject = WordBank.loadWord(getApplicationContext());
            boolean addFlag = true;                                             // 防止重复添加
            for (int index = 1; index <= newJsonObject.length(); ++index) {     // 检测是否有相同单词
                String[] newValue = WordBank.jsonAnalysis(newJsonObject, "" + index);
                if (newValue[0].equals(value[0])) { addFlag = false; break; }
            }
            if (addFlag) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("添加生词").setMessage("确定要添加生词吗?").setPositiveButton("确定", (dialogInterface, i) -> {
                    JSONObject temp = WordBank.jsonAppend(newJsonObject, String.valueOf(newJsonObject.length() + 1), new String[]{value[0], value[1], value[2]});
                    WordBank.saveWord(getApplicationContext(), temp);
                }).setNegativeButton("取消", (dialogInterface, i) -> {}).create().show();
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {             // 控制标题栏内容显示
        getMenuInflater().inflate(R.menu.menu_new_word, menu);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar);
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {         // 标题栏组件点击调用
        int id = item.getItemId();
        if (id == R.id.action_new_word) {
            Intent intent = new Intent(this, NewWordActivity.class);
            launcher.launch(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


//    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//        // 请求权限
//        Toast.makeText(this, "请授予存储权限", Toast.LENGTH_SHORT).show();
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                222);
//    } else {
//    }