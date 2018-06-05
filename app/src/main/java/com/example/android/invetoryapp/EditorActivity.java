package com.example.android.invetoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.invetoryapp.data.BookDbHelper;
import com.example.android.invetoryapp.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the book's name*/
    private EditText mBookNameEditText;

    /** EditText field to enter the book's author */
    private EditText mBookAuthorEditText;

    /** EditText field to enter the price of book */
    private EditText mBookPriceEditText;

    /** EditText field to enter the quantity of the book on stock */
    private EditText mBookQuantityEditText;

    /** EditText field to enter the supplier's name */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the supplier's phone */
    private EditText mSupplierPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mBookNameEditText = findViewById(R.id.book_name_ET);
        mBookAuthorEditText = findViewById(R.id.author_ET);
        mBookPriceEditText = findViewById(R.id.price_ET);
        mBookQuantityEditText = findViewById(R.id.quantity_ET);
        mSupplierNameEditText = findViewById(R.id.supplier_name_ET);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone_ET);

    }

    /**
     * Helper method to insert book data into the database.
     */
    private void insertBook() {
        String nameString = mBookNameEditText.getText().toString().trim();
        String authorString = mBookAuthorEditText.getText().toString().trim();
        String priceString = mBookPriceEditText.getText().toString().trim();
        int priceInt = Integer.parseInt(priceString);
        String quantityString = mBookQuantityEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityString);
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Create database helper
        BookDbHelper mDbHelper = new BookDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceInt);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityInt);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        // Insert new row and returning the ID of that new row.
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        // Otherwise, the insertion was successful and we can display a toast with the row ID.
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
