package model;

public class Time {
	
	// start and end times in floating point format
	private float startTimeFloat;
	private float endTimeFloat;
	
	// start and end times in string format
	private String startTimeString;
	private String endTimeString;
	
	// an array of days which all start and end and startTime and endTime
	private String day;
	private int dayInteger;
	
	private String location; 
	private String room;
	private String term;
	
	// Time Constructor
	public Time( String term, String day, String startTime, String endTime, String location, String room ) {
		this.term = term;
		this.day = day;
		this.dayInteger = dayStringToInt(day);
		this.startTimeString = startTime;
		this.endTimeString = endTime;
		this.startTimeFloat = timeStringToFloat(startTime);
		this.endTimeFloat = timeStringToFloat(endTime);
		this.location = location;
		this.room = room;
	}


	/**
	 * @param day
	 * @return integer representation of the day of the week
	 */
	private int dayStringToInt(String day) {
		if (day.equals("Mon"))
			return 0;
		if (day.equals("Tue"))
			return 1;
		if (day.equals("Wed"))
			return 2;
		if (day.equals("Thu"))
			return 3;
		return 4;
	}


	/**
	 *  Convert String time to floating point
	 * @param time
	 * @return
	 */
	private float timeStringToFloat(String time) {
		
		if (time == null || !time.contains(":"))
			return 0;
			
		String [] times = time.split(":");
		
		float hours = Float.parseFloat(times[0]);
		
		if (times[1].equals("30"))
			hours += 0.5;
		
		return hours;
	}	

	
	/**
	 * @return the startTime
	 */
	public float getStartTimeFloat() {
		return startTimeFloat;
	}
	

	/**
	 * @return the endTime
	 */
	public float getEndTimeFloat() {
		return endTimeFloat;
	}

	
	/**
	 * @return the startTime
	 */
	public String getStartTimeString() {
		return startTimeString;
	}
	

	/**
	 * @return the endTime
	 */
	public String getEndTimeString() {
		return endTimeString;
	}
	

	/**
	 * @return the dayOfWeek
	 */
	public String getDay() {
		return day;
	}
	
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * @return the room
	 */
	public String getRoom() {
		return room;
	}
	
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	
	/**
	 * @return the dayInteger
	 */
	public int getDayInteger() {
		return dayInteger;
	}
}
