package com.michaelmatveev.howispendmymoney;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.text.format.DateFormat;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	private static final String TAG = "SQLiteHelper";
	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "HowISpendMyMoneyDB";
	
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
       
        String CREATE_TAG_TABLE = 
        		"CREATE TABLE " + Tag.TABLE + " ( " +
                Tag.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                Tag.COLUMN_NAME + " TEXT, " +
                Tag.COLUMN_COLOR + " INTEGER)";
 
        Log.d(TAG + ".onCreate", CREATE_TAG_TABLE);
        db.execSQL(CREATE_TAG_TABLE);    
        
        String CREATE_TRANSACTION_TABLE =
        		"CREATE TABLE " + Transaction.TABLE + "(" +
        		Transaction.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        		Transaction.COLUMN_TITLE + " TEXT, " +
        		Transaction.COLUMN_AMOUNT + " DECIMAL, " +
        		Transaction.COLUMN_DATE + " TimeStamp NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        Log.d(TAG + ".onCreate", CREATE_TRANSACTION_TABLE);
        db.execSQL(CREATE_TRANSACTION_TABLE);
        
        String CREATE_ENTRIES_TABLE =
        		"CREATE TABLE Entries (" +
        		"TransactionId INTEGER, " +
        		"TagId INTEGER, " +
        		"FOREIGN KEY (TransactionId) REFERENCES " + Transaction.TABLE + "(_id) ON DELETE CASCADE, " +
        		"FOREIGN KEY (TagId) REFERENCES " + Tag.TABLE + "(_id) ON DELETE CASCADE)";
        Log.d(TAG + ".onCreate", CREATE_ENTRIES_TABLE);
        db.execSQL(CREATE_ENTRIES_TABLE);
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Tags");
		
		this.onCreate(db);
	}
	
	public long addTag(Tag newTag) {
		Log.d(TAG + ".addTag", newTag.toString());
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Tag.COLUMN_NAME, newTag.getName());
		values.put(Tag.COLUMN_COLOR, newTag.getColor());
		
		long id = db.insert(Tag.TABLE, null, values);
		db.close();
		return id;
	}
	
	public long addTransaction(String title, float amount, Timestamp date, int[] tagsIds) {
		Log.d(TAG + ".addTransaction", String.valueOf(amount) + " " + String.valueOf(title));
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Transaction.COLUMN_AMOUNT, amount);
		values.put(Transaction.COLUMN_TITLE, title);
		values.put("Date", date.toString());
		
		long id = db.insert("Transactions", null, values);
		
		for(int i: tagsIds) {
			values.clear();
			values.put("TransactionId", id);
			values.put("TagId", i);
			db.insert("Entries", null, values);
		}
		
		db.close();
		return id;
	}
	
	public boolean isTagExist(String name) {
		
		String query = "SELECT * FROM " + Tag.TABLE + " WHERE LOWER(Name) = LOWER('" + name + "')";
		
		Log.d(TAG + ".isTagExist", name);
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		boolean result = cursor.moveToFirst();
		cursor.close();
		return result;
	}
	
	public void deleteTag(int id) {
		Log.d(TAG + ".deleteTag", String.valueOf(id));
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(Tag.TABLE, Tag.KEY_ID + " = ?", new String[] { String.valueOf(id) });
		db.close();
	}
	
	public void deleteTransaction(int id) {
		Log.d(TAG + ".deleteTransaction", String.valueOf(id));
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete("Transactions", "_id = ?", new String[] { String.valueOf(id) });
		db.close();
		
	}
	
	public void updateTagText(int id, String newText) {
		Log.d(TAG + ".updateTagText", String.valueOf(id));
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Tag.COLUMN_NAME, newText);

		db.update(Tag.TABLE,  values, Tag.KEY_ID + " = ?", new String[] { String.valueOf(id) });
	}
	
	public void updateTransaction(int id, String newTitle, float newAmount, Timestamp newDate, int[] tagsIds) {
		Log.d(TAG + ".updateTransaction", String.valueOf(id));
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("Title", newTitle);
		values.put("Amount", newAmount);
		values.put("Date", newDate.toString());

		db.update("Transactions", values, Tag.KEY_ID + " = ?", new String[] { String.valueOf(id) });
		
		// delete all previous assigned tags
		db.delete("Entries", "TransactionId = ?", new String[] { String.valueOf(id) });
		
		for(int i: tagsIds) {
			values.clear();
			values.put("TransactionId", id);
			values.put("TagId", i);
			db.insert("Entries", null, values);
		}
	}
	
	public List<Tag> getAllTags() {
		List<Tag> result = new LinkedList<Tag>();
		String query = "SELECT * FROM " + Tag.TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(query, null);
	 
	    Tag tag = null;
	    if (cursor.moveToFirst()) {
	    	do {
	               tag = new Tag();
	               tag.setId(Integer.parseInt(cursor.getString(0)));
	               tag.setName(cursor.getString(1));
	               tag.setColor(cursor.getInt(2));
	               result.add(tag);
	        } while (cursor.moveToNext());
	    }
	    
	    Log.d(TAG + ".getAllTags", result.toString());
		return result;	
	}
	
	public Cursor getAllTagsCursor(String likeStatement) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query;
		if(likeStatement == "") {
			query = "SELECT _id, Name FROM " + Tag.TABLE + " ORDER BY Name";
		} 
		else {
			query = "SELECT _id, Name FROM " + Tag.TABLE + " WHERE " + Tag.COLUMN_NAME + " LIKE '"+ likeStatement +"%' ORDER BY Name";	
		}
		Log.d(TAG + ".getAllTagsCursor", query);
		return db.rawQuery(query, null);
		//return db.query(Tag.TABLE, null, null, null, null, null, null);
	}
	
	public Cursor getAllTransactionsCursor(String likeStatement, Date period) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query;
		StringBuilder sb = new StringBuilder(
				"SELECT T._id, T.Title, T.Amount, T.Date, " + 
				"ROUND(T.Amount, 2) || ' ' ||  T.Title AS AmountAndTitle, " + 
				"COALESCE(Group_Concat(A.Name, ', '), '') AS TagsList, " + 
				"COALESCE(Group_Concat(A._id, ', '), '') AS TagsIds FROM Transactions AS T " +
				"LEFT JOIN Entries AS E " +
				"ON T._id = E.TransactionId " +
				"LEFT JOIN Tags AS A " +
				"ON E.TagId = A._id ");
		if(period != null) {
			sb.append("WHERE T.Date BETWEEN '" + datesFormatter.format(period) + "' AND 'now' ");
		}
		sb.append("GROUP BY T._id ");				
		if(likeStatement != "") {
			sb.append("HAVING COALESCE(Group_Concat(A.Name, ', '), '') LIKE ('%" + likeStatement + "%') ");
		}
		sb.append("ORDER BY T.Date DESC, A.Name ASC");
		
		query = sb.toString();
		Log.d(TAG + ".getAllTransactionsCursor", query);
		return db.rawQuery(query, null);
	}

	public static final DateFormat datesFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);//SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT); //new SimpleDateFormat("yyyy-MM-dd");

	public Cursor getEntriesAndTags(Date period) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query;
		StringBuilder sb = new StringBuilder(
				"SELECT A.Name, SUM(T.Amount) FROM Transactions AS T " +
				"INNER JOIN Entries AS E " +
				"ON T._id = E.TransactionId " +
				"INNER JOIN Tags AS A " +
				"ON E.TagId = A._id ");
		if(period != null) {
			sb.append("WHERE T.Date BETWEEN '" + datesFormatter.format(period) + "' AND 'now' ");
		}
		sb.append(
				"GROUP BY A.Name " +
				"ORDER BY A.Name DESC");
		query = sb.toString();
		Log.d(TAG + ".getEntriesAndTags", query);
		return db.rawQuery(query, null);
	}
	
	public float getTotal(Date period) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		String query;
		StringBuilder sb = new StringBuilder(
				"SELECT SUM(T.Amount) FROM Transactions AS T ");
		if(period != null) {
			sb.append("WHERE T.Date BETWEEN '" + datesFormatter.format(period) + "' AND 'now' ");
		}
		query = sb.toString();
		Log.d(TAG + ".getTotal", query);
		float result = 0f;
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		while(!c.isAfterLast()) {
			result = c.getFloat(0);
			c.moveToNext();
		}
		c.close();
		return result;
	}

	public void updateChangeAmount(int id, float newAmount) {
		
		Log.d(TAG + ".updateTransaction", String.valueOf(id));
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("Amount", newAmount);

		db.update("Transactions", values, Tag.KEY_ID + " = ?", new String[] { String.valueOf(id) });			
	}
}
