package com.liweile.wigetsdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liweile.wigetsdemo.ruler.RulerActivity;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private String[] strings = {"刻度尺"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.list_view);

        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return strings.length;
            }

            @Override
            public String getItem(int position) {
                return strings[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list, parent, false);
                TextView tvTitle = inflate.findViewById(R.id.tv_title);
                tvTitle.setText(getItem(position));
                return inflate;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this,RulerActivity.class));
                        break;
                }
            }
        });




    }
}
