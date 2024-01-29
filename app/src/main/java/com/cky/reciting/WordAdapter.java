package com.cky.reciting;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder>{
    private JSONObject newWordJsonObject;
    Boolean hideFlag;
    int order = 1;
    public WordAdapter(JSONObject newWordJsonObject, Boolean hideFlag){
        this.newWordJsonObject = newWordJsonObject;
        this.hideFlag = hideFlag;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        String key = String.valueOf(position + 1);
        String[] value = WordBank.jsonAnalysis(newWordJsonObject, key);
        holder.word.setText(value[0]);
        holder.pronun.setText(value[1]);
        if(!hideFlag) holder.meaning.setText(value[2]);
        else holder.meaning.setText("");
        holder.order.setText(key);
    }
    public int getItemCount(){
        return newWordJsonObject.length();
    }
    public static class WordViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView word;
        TextView pronun;
        TextView meaning;
        TextView order;
        public WordViewHolder(View itemView){
            super(itemView);
            word = itemView.findViewById(R.id.word1);
            pronun = itemView.findViewById(R.id.pronun1);
            meaning = itemView.findViewById(R.id.meaning1);
            order = itemView.findViewById(R.id.order1);
            itemView.setOnCreateContextMenuListener(this);
        }
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            menu.setHeaderTitle("操作");
            menu.add(0, 0, this.getAdapterPosition(), "删除");
        }
    }
}
