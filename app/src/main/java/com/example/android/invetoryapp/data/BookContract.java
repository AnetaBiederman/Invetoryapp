package com.example.android.invetoryapp.data;

import android.provider.BaseColumns;

public final class BookContract {

    private BookContract (){}

        public static abstract class BookEntry implements BaseColumns {

            public static final String TABLE_NAME = "books";

            public static final String _ID = BaseColumns._ID;
            public static final String COLUMN_BOOK_NAME = "book_name";
            public static final String COLUMN_BOOK_AUTHOR = "book_author";
            public static final String COLUMN_BOOK_PRICE = "price";
            public static final String COLUMN_BOOK_QUANTITY = "quantity";
            public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
            public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        }}
