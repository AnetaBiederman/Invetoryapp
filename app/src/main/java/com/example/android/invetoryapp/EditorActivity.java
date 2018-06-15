package com.example.android.invetoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.invetoryapp.data.BookDbHelper;
import com.example.android.invetoryapp.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the book's name
     */
    private EditText bookNameEditText;

    /**
     * EditText field to enter the book's author
     */
    private EditText bookAuthorEditText;

    /**
     * EditText field to enter the price of book
     */
    private EditText bookPriceEditText;

    /**
     * EditText field to enter the quantity of the book on stock
     */
    private EditText bookQuantityEditText;

    /**
     * EditText field to enter the supplier's name
     */
    private EditText supplierNameEditText;

    /**
     * EditText field to enter the supplier's phone
     */
    private EditText supplierPhoneEditText;

    /**
     * Menu so we can use it in TextWatcher.
     */

    Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        bookNameEditText = findViewById(R.id.book_name_ET);
        bookAuthorEditText = findViewById(R.id.author_ET);
        bookPriceEditText = findViewById(R.id.price_ET);
        bookQuantityEditText = findViewById(R.id.quantity_ET);
        supplierNameEditText = findViewById(R.id.supplier_name_ET);
        supplierPhoneEditText = findViewById(R.id.supplier_phone_ET);

// Add textChangeListener for each editText
        bookNameEditText.addTextChangedListener(editorScreenTextWatcher);
        bookAuthorEditText.addTextChangedListener(editorScreenTextWatcher);
        bookPriceEditText.addTextChangedListener(editorScreenTextWatcher);
        bookQuantityEditText.addTextChangedListener(editorScreenTextWatcher);
        supplierNameEditText.addTextChangedListener(editorScreenTextWatcher);
        supplierPhoneEditText.addTextChangedListener(editorScreenTextWatcher);

    }

    // TextWatcher to create save button enable if all text fields are filled and set error message if not.
    private TextWatcher editorScreenTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        // Check if fields are not empty. If it is true enable save menu button.
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nameString = bookNameEditText.getText().toString().trim();
            String authorString = bookAuthorEditText.getText().toString().trim();
            String priceString = bookPriceEditText.getText().toString().trim();
            String quantityString = bookQuantityEditText.getText().toString().trim();
            String supplierNameString = supplierNameEditText.getText().toString().trim();
            String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

            myMenu.findItem(R.id.action_save).setEnabled(
                    !nameString.isEmpty() &&
                            !authorString.isEmpty() &&
                            !priceString.isEmpty() &&
                            !quantityString.isEmpty() &&
                            !supplierNameString.isEmpty() &&
                            !supplierPhoneString.isEmpty());
        }

        // check if text fields are filled, if not display error message.
        @Override
        public void afterTextChanged(Editable s) {
            if (bookNameEditText.getText().toString().equalsIgnoreCase("")) {
                bookNameEditText.setError(getString(R.string.error_book_name));
            } else {
                bookNameEditText.setError(null);
            }
            if (bookAuthorEditText.getText().toString().equalsIgnoreCase("")) {
                bookAuthorEditText.setError(getString(R.string.error_book_author));
            } else {
                bookAuthorEditText.setError(null);
            }
            if (bookQuantityEditText.getText().toString().equalsIgnoreCase("")) {
                bookQuantityEditText.setError(getString(R.string.error_qunatity));
            } else {
                bookQuantityEditText.setError(null);
            }
            if (bookPriceEditText.getText().toString().equalsIgnoreCase("")) {
                bookPriceEditText.setError(getString(R.string.error_price));
            } else {
                bookPriceEditText.setError(null);
            }
            if (supplierNameEditText.getText().toString().equalsIgnoreCase("")) {
                supplierNameEditText.setError(getString(R.string.error_supplier_name));
            } else {
                supplierNameEditText.setError(null);
            }
            if (supplierPhoneEditText.getText().toString().equalsIgnoreCase("")) {
                supplierPhoneEditText.setError(getString(R.string.error_supplier_phone));
            } else {
                supplierPhoneEditText.setError(null);
            }

        }
    };

    /**
     * Helper method to insert book data into the database.
     */
    private void insertBook() {
        String nameString = bookNameEditText.getText().toString().trim();
        String authorString = bookAuthorEditText.getText().toString().trim();
        String priceString = bookPriceEditText.getText().toString().trim();
        int priceInt = Integer.parseInt(priceString);
        String quantityString = bookQuantityEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityString);
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Save reference to the menu
        myMenu = menu;

        // Disable Send Button
        myMenu.findItem(R.id.action_save).setEnabled(false);

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