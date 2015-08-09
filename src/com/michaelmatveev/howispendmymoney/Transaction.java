package com.michaelmatveev.howispendmymoney;

public class Transaction {
	
	public static final String TABLE = "Transactions";
    // Columns names
    public static final String KEY_ID = "_id";
    public static final String COLUMN_TITLE = "Title";    
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_AMOUNT = "Amount";

    public static final String[] COLUMNS = { KEY_ID, COLUMN_TITLE, COLUMN_DATE };

}
