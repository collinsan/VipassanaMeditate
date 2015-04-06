package org.dhamma.sg.model;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sessions")
public class Sessions {

    public static final String ID_FIELD = "id";

	@DatabaseField(generatedId = true)
	private int id = -1; // set to -1 to indicate not added to db yet

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static final String ENTRY_FIELD = "entry";
	@DatabaseField
	private String entry; // as entered by user

    public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public static final String COMPLETED_FIELD = "completed";
	@DatabaseField(index = true)
	private boolean completed; // whether this is completed (fully completed or paused...)

	 public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public static final String DURATION_FIELD = "duration";
	@DatabaseField
	private long duration;
	
    public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public static final String ENTRYDATE_FIELD = "entryDate";
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date entryDate;

    public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public static final String UPDATED_FIELD = "updated";
	@DatabaseField(version = true, dataType = DataType.DATE_LONG)
	private Date updated;

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@Override
	public String toString(){
		return "Merits" + '@' + Integer.toHexString(hashCode()) + 
				"[id(" + id + ")][entry(" + entry + ")][isCompleted(" + completed + ")][duration(" + duration + ")][entryDate(" + entryDate + ")][updated(" + updated + ")]";
	}
    
}
