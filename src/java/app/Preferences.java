package app;

import javax.management.BadAttributeValueExpException;

public class Preferences {
        // number of weekdays
	private static final int NUM_DAYS = 5;
        
        // preferred time of earliest class
	private int preferredStartTime = -1;
        private int preferredEndTime = -1;
        
        // preferred break time length between classes
	private double preferredBreakLength = -1;
        
        // preferred days off of school
	private boolean [][] preferredDaysOff = new boolean[2][NUM_DAYS]; // [term][dayOfWeek]
        
        
        /**
         * Construct a new preferences object
         */
        public Preferences() {
            // init preferredDaysOff to false
            for (int i=0; i < 2; i++) {
                for (int j=0; j < NUM_DAYS; j++) {
                    preferredDaysOff[i][j] = false;
                }
            }
        }
	
        
        /**
         * 
         * @param preferredBreakLength 
         */
        public void setPreferredBreakLength(int preferredBreakLength) {
            this.preferredBreakLength = preferredBreakLength;
        }
        
        
        /**
         * 
         * @param preferredStartTime 
         */
        public void setPreferredStartTime(int preferredStartTime) {
            this.preferredStartTime = preferredStartTime;
        }
	/**
         * 
         * @param preferredEndTime 
         */
        public void setPreferredEndTime(int preferredEndTime) {
            this.preferredEndTime = preferredEndTime;
        }
        
        /**
	 * Set a preference for a day on or off
	 * @param preference: true is day off, false is no preference
	 * @param day is the day of the week to change (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri)
	 * @param term [1 | 2] is the term to set the preference for
	 */
	public void setPreferredDayOff(int term, int day, boolean prefferedOff) throws BadAttributeValueExpException {
            if ((term == 1 || term == 2) &&
                day >= 1 && day <= 5) {
                preferredDaysOff[term - 1][day - 1] = prefferedOff;
            } else {
                throw new BadAttributeValueExpException(
                "Invalid params passed to Preferences.setDayOffPreference(): term:" + term + " day:" + day);
            }
	}

	
	/**
	 * get the day off preference for specific day
	 * @param term is the term to get the preference for
	 * @return day off preference (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri), false if not term 1, 2 or 3
	 */
	public boolean getDayOffPreference(int term, int day) throws BadAttributeValueExpException{
            if ((term == 1 || term == 2) &&
                day >= 1 && day <= 5) {
                return preferredDaysOff[term - 1][day - 1];
            } else {
                throw new BadAttributeValueExpException(
                "Invalid params passed to Preferences.getDayOffPreference(): term:" + term + " day:" + day);
            }
	}

	
	/**
	 * @return the preferredStartTime
	 */
	public int getPreferredStartTime() {
            return preferredStartTime;
	}
        /**
	 * @return the preferredEndTime
	 */
	public int getPreferredEndTime() {
            return preferredEndTime;
	}

	/**
	 * @return the preferredBreakLength
	 */
	public double getPreferredBreakLength() {
            return preferredBreakLength;
	}


	/**
	 * @return the preferredDaysOff
	 */
	public boolean[][] getPreferredDaysOff() {
            return preferredDaysOff;
	}
        
        
        
        // TODO user selected breaktime preferences
        //private boolean[][][] userSelectedBreak = new boolean[2][5][29]; //[term][day][time]
	  /**
	 * get user selected break preference for term 1
	 * @param day is the day of the week to change (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri)
	 * @param time is the time of day for the break (1 = 7:00, 2 = 7:30, 3 = 8:00... 28 = 20:30, 29 = 21:00)
	 * @param term is the term to set the preference for
	 * @return true is for break, false is no preference
	 */
	/*public boolean getUserSelectedBreak(int term, int day, int time){
            return userSelectedBreak[term-1][day-1][time-1];
	}
        */
	/**
	 * change user selected break preference for term 1
	 * @param preference: true is for break, false is no preference
	 * @param day is the day of the week to change (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri)
	 * @param time is the time of day for the break (1 = 7:00, 2 = 7:30, 3 = 8:00... 28 = 20:30, 29 = 21:00)
	 * @param term is the term to set the preference for
	 */
	/*public void setUserSelectedBreak(int term, int day, int time, boolean preference){
            userSelectedBreak[term-1][day-1][time-1] = preference;
	}
        */
}
