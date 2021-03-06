package io.androidblog.simpletodo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.androidblog.simpletodo.R;
import io.androidblog.simpletodo.SimpleTodoApplication;
import io.androidblog.simpletodo.adapters.CategoriesRecyclerAdapter;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    private final int REQUEST_CODE = 20;
    @Bind(R.id.recycler_view_items)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SimpleTodoApplication app = (SimpleTodoApplication) getApplicationContext();

        databaseReference = app.getCategoriesReference();
        adapter = new CategoriesRecyclerAdapter(R.layout.category, databaseReference);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String newValue = data.getExtras().getString("value");
            String position = data.getExtras().getString("position");
            int intPosition = Integer.parseInt(position);
            updateItem(intPosition, newValue);
        }
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                        items.remove(position);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                });

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                        String value = (String) av.getItemAtPosition(pos);
                        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                        i.putExtra("position", String.valueOf(pos));
                        i.putExtra("value", value);
                        startActivityForResult(i, REQUEST_CODE);
                    }
                });

    }

    private void updateItem(int index, String value) {
        items.set(index, value);
        itemsAdapter.notifyDataSetChanged();
        writeItems();
    }


    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");

        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fab)
    public void onClick() {
        Intent i = new Intent(MainActivity.this, AddItemActivity.class);
        startActivity(i);
    }
}
