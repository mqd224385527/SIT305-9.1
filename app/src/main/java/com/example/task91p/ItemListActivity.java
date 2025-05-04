package com.example.task91p;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemListActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {

    private RecyclerView itemsRecyclerView;
    private TextView emptyTextView;
    private ItemAdapter itemAdapter;
    private DatabaseHelper databaseHelper;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI elements
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);

        // Set up RecyclerView
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh items when returning to this activity
        loadItems();
    }

    private void loadItems() {
        // Get all items from database
        itemList = databaseHelper.getAllItems();

        if (itemList.isEmpty()) {
            itemsRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            itemsRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            // Create and set adapter
            if (itemAdapter == null) {
                itemAdapter = new ItemAdapter(itemList, this);
                itemsRecyclerView.setAdapter(itemAdapter);
            } else {
                itemAdapter.updateData(itemList);
            }
        }
    }

    @Override
    public void onRemoveClick(int position) {
        // Get the item to remove
        Item itemToRemove = itemList.get(position);

        // Delete from database
        databaseHelper.deleteItem(itemToRemove.getId());

        // Remove from list and update adapter
        itemList.remove(position);
        itemAdapter.notifyItemRemoved(position);

        // Show message
        Toast.makeText(this, "Item removed successfully", Toast.LENGTH_SHORT).show();

        // Check if list is now empty
        if (itemList.isEmpty()) {
            itemsRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }
} 