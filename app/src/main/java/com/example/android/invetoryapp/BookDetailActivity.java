package com.example.android.invetoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.invetoryapp.data.BookContract.BookEntry;


public class BookDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    private int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;


    /**
     * Content URI for the existing bool (null if it's a new book)
     */
    private Uri mCurrentBookUri;
    private Spinner mCategorySpinner;
    ImageView imageBookTextView;
    TextView nameTextView;
    TextView authorTextView;
    TextView categoryTextView;
    TextView quantityTextView;
    TextView supplierNameTextView;
    TextView priceTextView;
    TextView supplierPhoneTextView;
    String uploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        // Find fields to populate in inflated template

        imageBookTextView = findViewById(R.id.detail_image);
        nameTextView = findViewById(R.id.detail_book_name);
        authorTextView = findViewById(R.id.detail_author);
        categoryTextView = findViewById(R.id.detail_category);
        quantityTextView = findViewById(R.id.detail_quantity);
        supplierNameTextView = findViewById(R.id.detail_supplier);
        priceTextView = findViewById(R.id.detail_price);
        supplierPhoneTextView = findViewById(R.id.detail_supplier_phone);
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_CATEGORY,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE,
                BookEntry.COLUMN_BOOK_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int categoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_CATEGORY);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_IMAGE);

            int category = cursor.getInt(categoryColumnIndex);
            // Convert the integer values into text for display
            String categoryDisplayed;
            // Category is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            if (category == 1){
                categoryDisplayed = this.getString(R.string.romance_category);
            }
            else if (category == 2){
                categoryDisplayed = this.getString(R.string.crime_category);
            }
            else if (category == 3){
                categoryDisplayed = this.getString(R.string.history_category);
            }
            else if (category == 4){
                categoryDisplayed = this.getString(R.string.education_category);
            }
            else {
                categoryDisplayed = this.getString(R.string.unknown_category);
            }

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            uploadImage = cursor.getString(imageColumnIndex);


            // Update the views on the screen with the values from the database
            nameTextView.setText(name);
            authorTextView.setText("By "+ author);
            categoryTextView.setText("Category: " + categoryDisplayed);
            priceTextView.setText("CZK " + Integer.toString(price));
            quantityTextView.setText("Stock: " + Integer.toString(quantity) + "pcs");
            supplierNameTextView.setText("Name: " + supplierName);
            supplierPhoneTextView.setText("Phone number: " + supplierPhone);
            imageBookTextView.setImageURI(Uri.parse(uploadImage));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
