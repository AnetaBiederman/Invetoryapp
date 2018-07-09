package com.example.android.invetoryapp;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.support.v7.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.example.android.invetoryapp.data.BookContract;
import com.example.android.invetoryapp.data.BookDbHelper;
import com.example.android.invetoryapp.data.MyApplication;

public class SearchableActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        handleIntent(getIntent());
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
        BookDbHelper bookDbHelper = ((MyApplication)getApplication()).getDbHelper();

        Cursor cursor = bookDbHelper.getReadableDatabase().rawQuery("SELECT " + BookContract.BookEntry.COLUMN_BOOK_NAME + ", " +
                BookContract.BookEntry.COLUMN_BOOK_AUTHOR + " FROM " + BookContract.BookEntry.TABLE_NAME +
                " WHERE upper(" + BookContract.BookEntry.COLUMN_BOOK_NAME + ") like '%" + query.toUpperCase() + "%'", null);
        setListAdapter(new SimpleCursorAdapter(this, R.layout.list_item, cursor,
                new String[] {BookContract.BookEntry.COLUMN_BOOK_NAME }, new int[]{R.id.name}));
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