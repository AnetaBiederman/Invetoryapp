package com.example.android.invetoryapp;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.SearchManager;
import android.support.v7.widget.SearchView;

import com.example.android.invetoryapp.data.BookContract.BookEntry;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the book data
        ListView bookListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);


        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, BookDetailActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    // Helper method for sale_icon to decrease quantity, when the book was sold.
    public void saleBook(int productID, int bookQuantity) {
        bookQuantity = bookQuantity - 1;
        if (bookQuantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
            Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, productID);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, R.string.book_sold, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to delete all books in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from book database");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.action_delete_all_books:
                deleteAllBooks();
                return true;
            case R.id.insert_dummy_data:
                dummyBook();
                dummyBook1();
                dummyBook2();
                dummyBook3();
                dummyBook4();
                dummyBook5();
                dummyBook6();
                dummyBook7();
                dummyBook8();
                dummyBook9();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_CATEGORY,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_IMAGE
        };


        return new CursorLoader(this,
                BookEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row,
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void dummyBook() {

        // Create a ContentValues object where column names are the keys,
        // and book attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_ROMANCE);
        values.put(BookEntry.COLUMN_BOOK_NAME, "Fifty Shades of Grey");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "E. L. James");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 269);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Ikar");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420778582645");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.fifty_shades_of_grey)
                + '/' + getResources().getResourceTypeName(R.drawable.fifty_shades_of_grey)
                + '/' + getResources().getResourceEntryName(R.drawable.fifty_shades_of_grey) );
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }
    private void dummyBook1() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_CRIME);
        values.put(BookEntry.COLUMN_BOOK_NAME, "The Girl on the Train");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Paula Hawkins");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 349);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 10);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Albatros");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420776411036");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.the_girl_on_train)
                + '/' + getResources().getResourceTypeName(R.drawable.the_girl_on_train)
                + '/' + getResources().getResourceEntryName(R.drawable.the_girl_on_train));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook2() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_CRIME);
        values.put(BookEntry.COLUMN_BOOK_NAME, "The Washington Decree");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Jussi Adler-Olsen");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 229);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 8);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Albatros");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420776411036");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.the_washington_decree)
                + '/' + getResources().getResourceTypeName(R.drawable.the_washington_decree)
                + '/' + getResources().getResourceEntryName(R.drawable.the_washington_decree));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook3() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_EDUCATION);
        values.put(BookEntry.COLUMN_BOOK_NAME, "Unlimited Memory");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Kevin Horsley");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 389);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Ikar");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420778582645");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.unlimeted_memory)
                + '/' + getResources().getResourceTypeName(R.drawable.unlimeted_memory)
                + '/' + getResources().getResourceEntryName(R.drawable.unlimeted_memory));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook4() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_HISTORY);
        values.put(BookEntry.COLUMN_BOOK_NAME, "Astrophysics for People in a Hurry");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Neil deGrasse Tyson");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 289);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 4);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Ikar");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420778582645");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.asprophysics_for_people)
                + '/' + getResources().getResourceTypeName(R.drawable.asprophysics_for_people)
                + '/' + getResources().getResourceEntryName(R.drawable.asprophysics_for_people));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook5() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_HISTORY);
        values.put(BookEntry.COLUMN_BOOK_NAME, "The World as It Is");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Ben Rhodes");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 489);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Ikar");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420778582645");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.the_world_is_as_it_is)
                + '/' + getResources().getResourceTypeName(R.drawable.the_world_is_as_it_is)
                + '/' + getResources().getResourceEntryName(R.drawable.the_world_is_as_it_is));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook6() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_ROMANCE);
        values.put(BookEntry.COLUMN_BOOK_NAME, "Memoirs Of A Geisha");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Arthur Golden");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 259);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 7);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Ikar");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420778582645");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.memories_of_a_geisha)
                + '/' + getResources().getResourceTypeName(R.drawable.memories_of_a_geisha)
                + '/' + getResources().getResourceEntryName(R.drawable.memories_of_a_geisha));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook7() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_CRIME);
        values.put(BookEntry.COLUMN_BOOK_NAME, "Whisper Me This");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Kerry Anne King");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 159);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Albatros");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420775611036");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.whisper_me_this)
                + '/' + getResources().getResourceTypeName(R.drawable.whisper_me_this)
                + '/' + getResources().getResourceEntryName(R.drawable.whisper_me_this));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook8() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_CRIME);
        values.put(BookEntry.COLUMN_BOOK_NAME, "The Plant Paradox Cookbook");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Steven R Gundry M.D.");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 189);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 3);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Albatros");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420775611036");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.the_plain_cook_book)
                + '/' + getResources().getResourceTypeName(R.drawable.the_plain_cook_book)
                + '/' + getResources().getResourceEntryName(R.drawable.the_plain_cook_book));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void dummyBook9() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, BookEntry.CATEGORY_CRIME);
        values.put(BookEntry.COLUMN_BOOK_NAME, "Then She Was Gone");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Lisa Jewell");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 289);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 6);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Albatros");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+420775611036");
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.then_she_was_gone)
                + '/' + getResources().getResourceTypeName(R.drawable.then_she_was_gone)
                + '/' + getResources().getResourceEntryName(R.drawable.then_she_was_gone));
        values.put(BookEntry.COLUMN_BOOK_IMAGE, imageUri.toString());
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }


}
