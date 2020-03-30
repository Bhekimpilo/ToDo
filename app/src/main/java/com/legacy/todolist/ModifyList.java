package com.legacy.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyList extends AppCompatActivity {

    EditText title, detail;
    Button add;
    SQLiteDatabaseHelper db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_list);

        title = findViewById(R.id.editTitle);
        detail = findViewById(R.id.editDetail);
        add = findViewById(R.id.btnAdd);
        db = new SQLiteDatabaseHelper(this);
        context = this;

        title.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        if (getIntent().getStringExtra("title") != null) {

            final int idIntent = getIntent().getIntExtra("id", 0);
            title.setText(getIntent().getStringExtra("title"));
            detail.setText(getIntent().getStringExtra("detail"));
            add.setText("Update");

            title.setSelection(getIntent().getStringExtra("title").length());
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtTitle = title.getText().toString().trim();
                String txtDetail = detail.getText().toString().trim();

                if (txtTitle.equals("") || txtDetail.equals("")) {
                    Toast.makeText(ModifyList.this, "One or more fields is empty", Toast.LENGTH_SHORT).show();
                } else {

                    if (getIntent().getStringExtra("title") != null) {

                        if (db.edit(getIntent().getIntExtra("id", 0), title.getText().toString(), detail.getText().toString()))
                            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();

                    } else if (db.createTask(txtTitle, txtDetail))
                        Toast.makeText(ModifyList.this, "Task Added successfully", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ModifyList.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(detail.getWindowToken(), 0);

                    Intent intent = new Intent(ModifyList.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}
