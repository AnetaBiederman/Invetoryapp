package com.example.android.invetoryapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.invetoryapp.data.BookContract.BookEntry;

import com.example.android.invetoryapp.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Spinner field to enter the item's category
     */
    private Spinner mCategorySpinner;

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        Log.d("Position " + cursor.getPosition() + ":", " bindView() has been called.");

        // Find fields to populate in inflated template
        ImageView imageBookTextView = view.findViewById(R.id.product_image);
        TextView nameTextView = view.findViewById(R.id.name);
        TextView authorTextView = view.findViewById(R.id.author);
        TextView categoryTextView = view.findViewById(R.id.category);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        ImageView bookSaleImageView = view.findViewById(R.id.sale_icon);

        // Find the columns of book attributes that we're interested in
        final int columnIdIndex = cursor.getColumnIndex(BookEntry._ID);
        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int bookAuthorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int bookCategoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_CATEGORY);
        int bookQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int bookPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int bookImageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_IMAGE);

        int category = cursor.getInt(bookCategoryColumnIndex);
        // Convert the integer values into text for display
        String categoryDisplayed;
        // Category is a dropdown spinner, so map the constant value from the database
        // into one of the dropdown options.
        // Then call setSelection() so that option is displayed on screen as the current selection.
        if (category == 1){
            categoryDisplayed = context.getString(R.string.romance_category);
        }
        else if (category == 2){
            categoryDisplayed = context.getString(R.string.crime_category);
        }
        else if (category == 3){
            categoryDisplayed = context.getString(R.string.history_category);
        }
        else if (category == 4){
            categoryDisplayed = context.getString(R.string.education_category);
        }
        else {
            categoryDisplayed = context.getString(R.string.unknown_category);
        }


        // Read the book attributes from the Cursor for the current book
        final String productID = cursor.getString(columnIdIndex);
        String bookImage = cursor.getString(bookImageColumnIndex);
        String bookName = cursor.getString(bookNameColumnIndex);
        String bookAuthor = cursor.getString(bookAuthorColumnIndex);
        final String bookQuantity = cursor.getString(bookQuantityColumnIndex);
        final String bookPrice = cursor.getString(bookPriceColumnIndex);

        // set sale_icon clickable to decrease quantity
        bookSaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CatalogActivity Activity = (CatalogActivity) context;
                Activity.saleBook(Integer.valueOf(productID), Integer.valueOf(bookQuantity));
            }
        });

        // Update the TextViews with the attributes for the current book
        imageBookTextView.setImageURI(Uri.parse(bookImage));
        nameTextView.setText(bookName);
        authorTextView.setText(bookAuthor);
        categoryTextView.setText(categoryDisplayed);
        quantityTextView.setText(context.getString(R.string.pcs_tv) + " " + bookQuantity);
        priceTextView.setText(context.getString(R.string.czk) + " " + bookPrice);

        ImageView productEdit = view.findViewById(R.id.edit_icon);
        productEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, Long.parseLong(productID));
                intent.setData(currentBookUri);
                context.startActivity(intent);
            }
        });

    }
}
