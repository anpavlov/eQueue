package com.sudo.equeue.content.contracts;

import android.provider.BaseColumns;

public final class SearchResultContract {

    public SearchResultContract() {}

    public static abstract class SearchResultEntry implements BaseColumns {

        public static final String TABLE_NAME = "search_result";
        public static final String COLUMN_NAME_VACANCY_ID = "vacancy_id";
        public static final String COLUMN_NAME_NAME = "vacancy_name";
        public static final String COLUMN_NAME_EMPLOYER_NAME = "employer_name";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";
    public static final String SQL_CREATE =
            "CREATE TABLE " + SearchResultEntry.TABLE_NAME + " (" +
                    SearchResultEntry._ID + INT_TYPE + " PRIMARY KEY, " +
                    SearchResultEntry.COLUMN_NAME_VACANCY_ID + INT_TYPE + COMMA_SEP +
                    SearchResultEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    SearchResultEntry.COLUMN_NAME_EMPLOYER_NAME + TEXT_TYPE +
                    ")";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + SearchResultEntry.TABLE_NAME;
}
