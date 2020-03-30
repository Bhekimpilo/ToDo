package com.legacy.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ToDoViewHolder> {

    ArrayList<Task> tasks;
    Context context;
    SQLiteDatabaseHelper db;

    public Adapter(ArrayList<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.todo_item, viewGroup,false);

        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ToDoViewHolder toDoViewHolder, final int i) {

        toDoViewHolder.edTitle.setText(tasks.get(i).Title);
        toDoViewHolder.edDetail.setText(tasks.get(i).Detail);
        toDoViewHolder.parentLayout.setId(tasks.get(i).Id);
        toDoViewHolder.checkBox.setChecked(tasks.get(i).Status == 0 ? false : true);
        toDoViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               SQLiteDatabaseHelper helper = new SQLiteDatabaseHelper(context);

               helper.UpdateStatus(i + 1, b);
            }
        });

    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView edTitle, edDetail;
        CheckBox checkBox;
        ConstraintLayout parentLayout;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);

            edTitle = itemView.findViewById(R.id.titleText);
            edDetail = itemView.findViewById(R.id.detailText);
            checkBox = itemView.findViewById(R.id.checkBox);
            parentLayout = itemView.findViewById(R.id.parentLayout);

            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem edit = menu.add(Menu.NONE, 1,1,"Edit");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");
            edit.setOnMenuItemClickListener(contextMenu);
            delete.setOnMenuItemClickListener(contextMenu);
        }

        private final MenuItem.OnMenuItemClickListener contextMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                db = new SQLiteDatabaseHelper(context);

                switch (item.getItemId()){
                    case 1:
                        Intent intent = new Intent(context, ModifyList.class);
                        intent.putExtra("title", edTitle.getText().toString());
                        intent.putExtra("detail", edDetail.getText().toString());
                        intent.putExtra("id", parentLayout.getId());
                        context.startActivity(intent);
                        break;

                    case 2:

                        if(db.delete(parentLayout.getId())){
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            tasks.remove(getAdapterPosition());

                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(),tasks.size());

                            Cursor c = db.getReadableDatabase().rawQuery("SELECT * FROM Tasks WHERE Status = 1", null);

                            ((MainActivity)context).refreshProgress(c.getCount() * 100 / tasks.size());

                            //notifyDataSetChanged();

                            c.close();
                        }
                        else
                            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();

                        break;

                    default:

                        break;
                }

                return true;
            }
        };
    }
}


