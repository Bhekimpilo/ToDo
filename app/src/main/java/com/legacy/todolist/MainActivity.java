package com.legacy.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabaseHelper db;
    ProgressBar progressbar;
    CheckBox checkBox;
    TextView txtPercentage;
    ArrayList<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        progressbar = findViewById(R.id.progressBar);
        checkBox = findViewById(R.id.checkBox);
        txtPercentage = findViewById(R.id.txtPercentage);

        setSupportActionBar(toolbar);

        db = new SQLiteDatabaseHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent modify = new Intent(MainActivity.this, ModifyList.class);
            startActivity(modify);
            }
        });

        tasks = new ArrayList<>();

        SQLiteDatabase database = db.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM Tasks", null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Task task = new Task();
                task.setTitle(cursor.getString(cursor.getColumnIndex(db.COLUMN_TITLE)));
                task.setDetail(cursor.getString(cursor.getColumnIndex(db.COLUMN_DETAILS)));
                task.setStatus(cursor.getInt(cursor.getColumnIndex(db.COLUMN_STATUS)));
                task.setId(cursor.getInt(cursor.getColumnIndex(db.COLUMN_ID)));

                tasks.add(task);
            }

        }

        getProgress(database);

        cursor.close();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Adapter adapter = new Adapter(tasks, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    public void getProgress(SQLiteDatabase database) {
        Cursor checked = database.rawQuery("SELECT * FROM Tasks WHERE Status = 1", null);

        int done = tasks.size() == 0 ? 0 : checked.getCount() * 100 / tasks.size();

        progressbar.setProgress(done);
        txtPercentage.setText(done + "%");

        checked.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProgress(db.getReadableDatabase());

    }

    @Override
    protected void onStart() {
        super.onStart();
        getProgress(db.getReadableDatabase());
    }

    public void refreshProgress(int completed) {
        progressbar = findViewById(R.id.progressBar);
        progressbar.setProgress(completed);
        txtPercentage.setText(completed + "%");
    }
}
