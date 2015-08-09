package com.michaelmatveev.howispendmymoney;

import android.provider.CalendarContract.Colors;

public class Tag {
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	private int id;
	private String name;
	private int color;
	
	public Tag(String name) {
		this.name = name;
		this.color = 0;
	}
	
    public Tag() {
	}
    
    @Override
    public String toString() {    	
    	return this.name;
    }
	// Table name
    public static final String TABLE = "Tags";

    // Columns names
    public static final String KEY_ID = "_id";
    public static final String COLUMN_NAME = "Name";    
    public static final String COLUMN_COLOR = "Color";

    public static final String[] COLUMNS = { KEY_ID, COLUMN_NAME, COLUMN_COLOR };
	
}
