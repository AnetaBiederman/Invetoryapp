package com.example.android.invetoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.invetoryapp.data.BookDbHelper;
import com.example.android.invetoryapp.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    private int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;


    /**
     * Content URI for the existing bool (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    private Spinner mCategorySpinner;
    private EditText bookNameEditText;
    private EditText bookAuthorEditText;
    private EditText bookPriceEditText;
    private EditText bookQuantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;
    private ImageButton decreaseQuantity;
    private ImageButton increaseQuantity;
    private ImageButton call;
    String uploadImage;
    private ImageView bookImage;
    private Boolean image_status;
    private ImageView bookPreview;

    /**
     * Category of the item. The possible valid values are in BookContract.java file:
     * {@link BookEntry#CATEGORY_UNKNOWN}, {@link BookEntry#CATEGORY_HISTORY}, or
     * {@link BookEntry#CATEGORY_ROMANCE} or {@link BookEntry#CATEGORY_EDUCATION} or
     * {@link BookEntry#CATEGORY_CRIME}.
     */
    private int mCategory = BookEntry.CATEGORY_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        bookPreview = findViewById(R.id.book_image);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_book));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();

            image_status = false;

        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.editor_activity_title_edit_book));
            image_status = true;
            call = findViewById(R.id.call);
            call.setVisibility(View.VISIBLE);

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mCategorySpinner = findViewById(R.id.category_spinner);
        bookImage = findViewById(R.id.book_image);
        bookNameEditText = findViewById(R.id.book_name_ET);
        bookAuthorEditText = findViewById(R.id.author_ET);
        bookPriceEditText = findViewById(R.id.price_ET);
        bookQuantityEditText = findViewById(R.id.quantity_ET);
        supplierNameEditText = findViewById(R.id.supplier_name_ET);
        supplierPhoneEditText = findViewById(R.id.supplier_phone_ET);
        decreaseQuantity = findViewById(R.id.imageButton_minus);
        increaseQuantity = findViewById(R.id.imageButton_plus);
        call = findViewById(R.id.call);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mCategorySpinner.setOnTouchListener((mTouchListener));
        bookNameEditText.setOnTouchListener(mTouchListener);
        bookAuthorEditText.setOnTouchListener(mTouchListener);
        bookPriceEditText.setOnTouchListener(mTouchListener);
        bookQuantityEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);
        bookImage.setOnTouchListener(mTouchListener);

        setupSpinner();

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusOneToQuantity();
                mBookHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusOneToQuantity();
                mBookHasChanged = true;
            }
        });

        ImageButton uploadImage = findViewById(R.id.upload_image);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryToOpenImageSelector();
                mBookHasChanged = true;
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBooks();
            }
        });

    }

    /**
     * Setup the dropdown spinner that allows the user to select the category of the items.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter itemSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_item_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        itemSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(itemSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.romance_category))) {
                        mCategory = BookEntry.CATEGORY_ROMANCE;
                    } else if (selection.equals(getString(R.string.crime_category))) {
                        mCategory = BookEntry.CATEGORY_CRIME;
                    } else if (selection.equals(getString(R.string.history_category))) {
                        mCategory = BookEntry.CATEGORY_HISTORY;
                    } else if (selection.equals(getString(R.string.education_category))) {
                        mCategory = BookEntry.CATEGORY_EDUCATION;
                    } else {
                        mCategory = BookEntry.CATEGORY_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCategory = BookEntry.CATEGORY_UNKNOWN;
            }
        });
    }

    private void minusOneToQuantity() {
        String quantityValue = bookQuantityEditText.getText().toString();
        int quantityValueInt;
        if (quantityValue.isEmpty()) {
            return;
        } else if (quantityValue.equals("0")) {
            Toast.makeText(EditorActivity.this, R.string.zero_quantity, Toast.LENGTH_SHORT).show();

        } else {
            quantityValueInt = Integer.parseInt(quantityValue);
            bookQuantityEditText.setText(String.valueOf(quantityValueInt - 1));
        }
    }

    private void plusOneToQuantity() {
        String quantityValue = bookQuantityEditText.getText().toString();
        int quantityValueInt;
        if (quantityValue.isEmpty()) {
            return;
        } else {
            quantityValueInt = Integer.parseInt(quantityValue);
            bookQuantityEditText.setText(String.valueOf(quantityValueInt + 1));
        }
    }


    /**
     * Get user input from editor and save book into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = bookNameEditText.getText().toString().trim();
        String authorString = bookAuthorEditText.getText().toString().trim();
        String priceString = bookPriceEditText.getText().toString().trim();
        String quantityString = bookQuantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        if (uploadImage != null) {
            uploadImage = uploadImage.toString();
        }

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                mCategory == BookEntry.CATEGORY_UNKNOWN || TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(authorString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(supplierPhoneString)|| image_status !=null) {

            Toast.makeText(this, R.string.data_not_filled,Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, mCategory);
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        values.put(BookEntry.COLUMN_BOOK_IMAGE, uploadImage);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent intent) {
        if (reqCode == PICK_IMAGE_REQUEST && resCode == RESULT_OK) {
            uploadImage = intent.getData().toString();
            bookPreview.setImageURI(Uri.parse(String.valueOf(uploadImage)));
        }
        super.onActivityResult(reqCode, resCode, intent);
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
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.

        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case R.id.contact_supplier:
                orderBooks();
                // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

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

            // Extract out the value from the Cursor for the given column index
            int category = cursor.getInt(categoryColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            uploadImage = cursor.getString(imageColumnIndex);


            // Update the views on the screen with the values from the database
            bookNameEditText.setText(name);
            bookAuthorEditText.setText(author);
            bookPriceEditText.setText(Integer.toString(price));
            bookQuantityEditText.setText(Integer.toString(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
            bookImage.setImageURI(Uri.parse(uploadImage));

            // Category is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (category) {
                case BookEntry.CATEGORY_ROMANCE:
                    mCategorySpinner.setSelection(1);
                    break;
                case BookEntry.CATEGORY_CRIME:
                    mCategorySpinner.setSelection(2);
                    break;
                case BookEntry.CATEGORY_HISTORY:
                    mCategorySpinner.setSelection(3);
                case BookEntry.CATEGORY_EDUCATION:
                    mCategorySpinner.setSelection(4);
                default:
                    mCategorySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mCategorySpinner.setSelection(0);
        bookNameEditText.setText("");
        bookAuthorEditText.setText("");
        bookPriceEditText.setText("");
        bookQuantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // intent to order more book via phone
    private void orderBooks() {
        // intent to phone
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + supplierPhoneEditText.getText().toString().trim()));
        startActivity(intent);
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}