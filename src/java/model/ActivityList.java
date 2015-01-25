package model;

import java.util.ArrayList;
import java.util.List;

import javax.management.BadAttributeValueExpException;

public class ActivityList {
	
	/** 5 lists of activities:
	 * 0: lectures, 1: labs, 2: tutorials, 3: misc. (excludes waiting lists)
	 */
	private List<Activity>[] activityLists;
	
	private static final int NUM_ACTIVITY_TYPES = 4;
	
	// name of containing course
	private final String courseName;
	
	/**
	 * Constructor - Initialize lists
	 * @param name 
	 */
	public ActivityList(String name) {
		courseName = name;
		activityLists = new List[NUM_ACTIVITY_TYPES];
		for (int i=0; i < NUM_ACTIVITY_TYPES; i++)
			activityLists[i] = new ArrayList<Activity>();
	}
	
	/**
	 * @return true if all activity lists are empty, false otherwise
	 */
	public boolean isEmpty() {
		for (int i=0; i < activityLists.length; i++)
			if (!activityLists[i].isEmpty())
				return false;
		return true;
	}
	
	
	/**
	 * Checks existing activities in given list. Adds activity to list and sets its priority if
	 * the activity is not already contained.
	 * @param activity to be added
	 * @param i list number
	 */
	public void checkExistingAndAdd(Activity activity) {
		int type = activity.getTypeInteger();
		List<Activity> activities = activityLists[type];
		for (Activity a : activities) {
			if (a.equals(activity))
				return;
		}
		activities.add(activity);
		activity.setActivityList(this);
	}


	/**
	 * @param activityTypeIndex - the type of activity (0: lectures, 1: labs, 2: tutorials, 3: misc, 4: waiting lists)
	 * @return a list of activities indexed by activityTypeIndex
	 * @throws BadAttributeValueExpException
	 */
	public List<Activity> getListOfActivities(int activityTypeIndex) throws BadAttributeValueExpException {
		if (activityTypeIndex < 0 || activityTypeIndex >= activityLists.length)
			throw new BadAttributeValueExpException("Invalid index " + activityTypeIndex + " passed to ActivityList.getActivityList(int i)");
		return activityLists[activityTypeIndex];
	}
	
	
	/**
	 * Get an activity by its location indices
	 * @param activityTypeIndex (0: lectures, 1: labs, 2: tutorials, 3: misc, 4: waiting lists)
	 * @param activityIndex the location in the list of activities of type given by activityTypeIndex
	 * @return the activity at the given indices
	 * @throws BadAttributeValueExpException
	 */
	public Activity getActivity(int activityTypeIndex, int activityIndex) throws BadAttributeValueExpException {
		
		if (activityTypeIndex >= NUM_ACTIVITY_TYPES)
			throw new BadAttributeValueExpException("activityTypeIndex " + activityTypeIndex + " is out of range");
		
		List<Activity> activityList = getListOfActivities(activityTypeIndex);
		if (activityList.isEmpty())
			return null;
		
		if (activityIndex >= activityList.size())
			throw new BadAttributeValueExpException("activityIndex " + activityIndex + " is out of range");
		
		return activityList.get(activityIndex);
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		for (int i=0; i < activityLists.length; i++)
			activityLists[i].clear();
	}


	/**
	 * 
	 * @param activityList
	 */
	public void addAll(ActivityList activityList) {
		try {
			for (int i=0; i < NUM_ACTIVITY_TYPES; i++) {
				List<Activity> source = activityList.getListOfActivities(i);
				List<Activity> dest = activityLists[i];
				for (Activity activity : source) {
					dest.add(activity);
					activity.setActivityList(this);
				}
			}
		}
		catch (BadAttributeValueExpException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the numActivityTypes
	 */
	public static int getNumActivityTypes() {
		return NUM_ACTIVITY_TYPES;
	}
	

	/**
	 * @param priority the priority to set
	 */
	public void adjustActivityPriority(int priority) {
		for (List<Activity> activities : activityLists)
			for (Activity activity : activities)
				activity.adjustPriority(priority);
	}


	/**
	 * @return the courseName
	 */
	public String getCourseName() {
		return courseName;
	}
	
	public List<Activity> getAllActivities(){
		ArrayList<Activity> activities = new ArrayList<Activity>();
		
		for(List<Activity> list : activityLists)
			for(Activity activity : list)
				activities.add(activity);
		
		return activities;
	}
}
