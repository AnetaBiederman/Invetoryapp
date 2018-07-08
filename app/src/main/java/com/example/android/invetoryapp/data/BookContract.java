package com.example.android.invetoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    private BookContract (){}

    public static final String CONTENT_AUTHORITY = "com.example.android.invetoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.invetoryapp/books/ is a valid path for
     * looking at book data. content://com.example.android.invetoryapp/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";

        public static abstract class BookEntry implements BaseColumns {

            /** The content URI to access the book data in the provider */
            public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

            public static final String TABLE_NAME = "books";

            public static final String _ID = BaseColumns._ID;
            public static final String COLUMN_BOOK_CATEGORY = "category";
            public static final String COLUMN_BOOK_NAME = "book_name";
            public static final String COLUMN_BOOK_AUTHOR = "book_author";
            public static final String COLUMN_BOOK_PRICE = "price";
            public static final String COLUMN_BOOK_QUANTITY = "quantity";
            public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
            public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
            public static final String COLUMN_BOOK_IMAGE = "book_image";

            /**
             * Possible values for category.
             */
            public static final int CATEGORY_UNKNOWN = 0;
            public static final int CATEGORY_ROMANCE = 1;
            public static final int CATEGORY_CRIME = 2;
            public static final int CATEGORY_HISTORY = 3;
            public static final int CATEGORY_EDUCATION = 4;

            /**
             * Returns whether or not the given gender is {@link #CATEGORY_UNKNOWN}, {@link #CATEGORY_ROMANCE}, {@link #CATEGORY_CRIME}, {@link #CATEGORY_HISTORY}
             * or {@link #CATEGORY_EDUCATION}.
             */
            public static boolean isValidCategory(int category) {
                if (category == CATEGORY_UNKNOWN || category == CATEGORY_ROMANCE ||
                        category == CATEGORY_CRIME || category == CATEGORY_HISTORY ||
                        category == CATEGORY_EDUCATION ) {
                    return true;
                }
                return false;
            }


            /**
             * The MIME type of the {@link #CONTENT_URI} for a list of books.
             */
            public static final String CONTENT_LIST_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

            /**
             * The MIME type of the {@link #CONTENT_URI} for a single book.
             */
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        }
}
