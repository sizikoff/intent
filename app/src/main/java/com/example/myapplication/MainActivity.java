package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String LOG_TAG="myLogs";
    Button btnAdd,btnRead,btnClear;
    EditText etName,etEmail;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        btnRead = findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);

        //создаем объект для создания и управоения версиями бд
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View v) {
        //создвем объект для данных
        ContentValues cv = new ContentValues();
        //получаем данные из полей ввода
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();

        //подключаемся к бд
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (v.getId()){
            case R.id.btnAdd:
                Log.d(LOG_TAG,"<---------INSERT in mytable>");
                //подготовим данные для вставки в виде пар столбец-значение
                cv.put("name",name);
                cv.put("email",email);
                //вставляем запись и получаем ее id
                long rowId = db.insert("mytable",null,cv);
                Log.d(LOG_TAG,"добавлено запись " + rowId);
                break;
            case R.id.btnRead:
               //делаем запрос всех данных из таблицы mytable получаем Cursor
                Cursor c = db.query("mytable",null,null,null,null,null,null);
                //ставим курсор на первое место если строк нет-вернет false
                if (c.moveToFirst()) {
                    //опреляем имена столбцов по имени в выборке
                    int idColIndex = c.getColumnIndex("id");
                    int nameColIndex = c.getColumnIndex("name");
                    int emailColIndex = c.getColumnIndex("email");
                    do {
                        Log.d(LOG_TAG,"id= " + c.getInt(idColIndex)+
                                ",  name = " + c.getString(nameColIndex)+
                                ",  email= " + c.getString(emailColIndex));
                        //переход на след строку,если нет следующей то false-выходим из цикла
                    }while(c.moveToNext());
                }else
                    Log.d(LOG_TAG,"0 rows");
                c.close();
                break;
            case R.id.btnClear:
                Log.d(LOG_TAG,"<---------CLEAR mytable>");
                //удаляем все
                int clearCount = db.delete("mytable",null,null);
                Log.d(LOG_TAG,"удаленные число столбцов " + clearCount);
                break;
        }
        dbHelper.close();
    }
}