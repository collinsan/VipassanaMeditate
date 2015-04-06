package org.dhamma.sg.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "merits")
public class Merits {

    public static final String ID_FIELD = "id";
    public static final String ENTRY_FIELD = "entry";
    public static final String MERIT_FIELD = "merit";
    public static final String ENTRYDATE_FIELD = "entryDate";
    public static final String UPDATED_FIELD = "updated";

	@DatabaseField(generatedId = true)
	private int id = -1; // set to -1 to indicate not added to db yet

	@DatabaseField
	private String entry; // as entered by user

	@DatabaseField(index = true)
	private boolean merit; // whether this is a merit of demerit

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date entryDate;

	@DatabaseField(version = true, dataType = DataType.DATE_LONG)
	private Date updated;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public boolean isMerit() {
		return merit;
	}

	public void setMerit(boolean merit) {
		this.merit = merit;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	@Override
	public String toString(){
		return "Merits" + '@' + Integer.toHexString(hashCode()) + 
				"[id(" + id + ")][entry(" + entry + ")][isMerit(" + merit + ")][entryDate(" + entryDate + ")][updated(" + updated + ")]";
	}
    
}
