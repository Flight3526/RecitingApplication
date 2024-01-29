package com.cky.reciting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class NewWordActivity extends AppCompatActivity {
    JSONObject newWordJsonObject;
    RecyclerView recycler_view_word;
    WordAdapter wordAdapter;
    WordAdapter wordAdapterHide;
    JSONObject jo;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);
        recycler_view_word = findViewById(R.id.new_word_view);
        recycler_view_word.setLayoutManager(new LinearLayoutManager(this));
        context = getApplicationContext();
        newWordJsonObject = WordBank.loadWord(context);
        wordAdapter = new WordAdapter(newWordJsonObject, false);
        wordAdapterHide = new WordAdapter(newWordJsonObject, true);
        recycler_view_word.setAdapter(wordAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycler_view_word.addItemDecoration(divider);

        Toolbar toolbar = (Toolbar)findViewById(R.id.word_toolbar);
        toolbar.setTitle("生词");
        setSupportActionBar(toolbar);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {         // 标题栏组件点击调用
        int id = item.getItemId();
        if (id == R.id.action_bulb) {
            if(recycler_view_word.getAdapter() == wordAdapter)
                recycler_view_word.swapAdapter(wordAdapterHide, false);
            else
                recycler_view_word.swapAdapter(wordAdapter, false);
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onContextItemSelected(MenuItem item){
        switch(item.getItemId()){
            case 0:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String key = String.valueOf(item.getOrder() + 1);
                String[] value = WordBank.jsonAnalysis(newWordJsonObject, key);
                builder.setTitle("Delete Word").setMessage("你确定要删除\"" + value[0] + "\"吗？");
                builder.setPositiveButton("是的", (dialogInterface, i) -> {
                    int pos = item.getOrder();
                    newWordJsonObject = WordBank.jsonRemove(newWordJsonObject, key);
                    WordBank.saveWord(context, newWordJsonObject);
                    wordAdapter.notifyItemRemoved(pos);
                    wordAdapter.notifyItemRangeChanged(pos, newWordJsonObject.length() - pos);
                    wordAdapterHide.notifyItemRemoved(pos);
                    wordAdapterHide.notifyItemRangeChanged(pos, newWordJsonObject.length() - pos);
                    Toast.makeText(this, "删除 "+value[0], Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("取消", (dialogInterface, i) -> {});
                builder.create().show();
                break;
        }
        return true;
    }
}