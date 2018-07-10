package com.example.android.invetoryapp;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.support.v7.widget.SearchView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.android.invetoryapp.data.BookDbHelper;
import com.example.android.invetoryapp.data.MyApplication;
import com.example.android.invetoryapp.data.BookContract.BookEntry;


public class SearchableActivity extends ListActivity {

    private BookDbHelper mDbHelper;
    protected SQLiteDatabase db;
    protected Cursor cursor;
    protected ListAdapter adapter;
    protected ListView searchLV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        handleIntent(getIntent());

        searchLV = findViewById (android.R.id.list);

        db = (new BookDbHelper(this)).getReadableDatabase();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {

        cursor = db.rawQuery("SELECT _id, category, book_name, book_author, price, quantity, book_image FROM books WHERE book_name LIKE ? order by book_name",
                new String[]{"%" + query + "%"});
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item,
                cursor,
                new String[] {"category", "book_name", "book_author", "price", "quantity", "book_image"},
                new int[] {R.id.category, R.id.name, R.id.author, R.id.price, R.id.quantity, R.id.product_image});

        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        String bookName = cursor.getString(bookNameColumnIndex);

        searchLV.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}