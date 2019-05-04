package com.example.sqlitesample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    Button bn;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建或打开数据库（此处需要使用绝对路径）
        db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/my.db3",null);
        listView = findViewById(R.id.show);
        bn = findViewById(R.id.ok);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText)findViewById(R.id.title_)).getText().toString();
                String content = ((EditText)findViewById(R.id.content_)).getText().toString();
            try{
                insertData(db,title,content);
                Cursor cursor = db.rawQuery("select * from news_inf",null);
                inflateList(cursor);
            }
            catch (SQLiteException se)
            {
                //执行DDL出创建数据表
                db.execSQL("create table news_inf(_id integer primary key autoincrement,"
                        + "news_title varchar(50),"
                        + "news_content varchar(255))");
                //执行insert语句插入数据
                insertData(db,title,content);
                //执行查询
                Cursor cursor = db.rawQuery("select * from news_inf",
                        null);
                inflateList(cursor);
            }
            }
        });

    }

    private void insertData(SQLiteDatabase db,String title ,String content)
    {
        //执行插入语句
        db.execSQL("insert into news_inf values(null,?,?)",
                new String[]{title,content});
    }

    private void inflateList(Cursor cursor)
    {
        //填充SimpleCursorAdapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this,
                R.layout.line, cursor,
                new String[]{
                        "news_title","news_content"
                },new int[]{R.id.my_title,R.id.my_content},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //显示数据
        listView.setAdapter(adapter);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(db != null && db.isOpen())
        {
            db.close();
        }
    }
}
