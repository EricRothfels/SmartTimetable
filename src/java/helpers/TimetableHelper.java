package helpers;

import app.Preferences;
import comparators.CombinationComparator;
import app.Timetables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import javax.management.BadAttributeValueExpException;
import model.Activity;
import model.ActivityList;
import model.Conflict;
import model.MapEntry;
import model.Time;
import model.ValidCombination;


public class TimetableHelper {
	private static Timetables timetables = Timetables.getInstance();
	private Set<Conflict> conflicts;
	private PriorityQueue<ValidCombination> validCombinationsQueue;
	private List<ValidCombination> validCombinations;
	
	// list of activities to make timetables from
	private List<ActivityList> activityLists;
	
	// keep track of which indices in the array are to be incremented next
	private int listIndex;
	private int typeIndex;
	
	// keep an array of activity indices
	private int [][] activityIndices;
	
	// number of combinations we display to user
	private static final int NUM_DISPLAY = 40;
        
        // max number of iterations for the loop in maketimetables()
        private static final long MAX_ITERATIONS = 1500000;
	
	private boolean finishedCombinations = false;
        
        private final Preferences preferences;
        private final int preferredEndTime;
        private final int preferredStartTime;

        
        private Boolean areTermsEqual(String term, int termInt) {
            if ((term.equals("1") || term.equals("1-2")) && termInt == 0) {
                return true;
            }
            if ((term.equals("2") || term.equals("1-2")) && termInt == 1) {
                return true;
            }       
            return false;
        }
        
        /**
         * 
         * @param activity
         * @param preferences
         * @return 
         */
        private Boolean filterActivity(Activity activity, Preferences preferences) {
            List<Time> times = activity.getTimes();
            
            Boolean valid = true;
            if (preferredEndTime != -1) {
                for (Time time : times) {
                    if (preferredEndTime < time.getEndTimeFloat()) {
                        return false;
                    }
                }
            }
            if (preferredStartTime != -1) {
                for (Time time : times) {
                    if (preferredStartTime > time.getStartTimeFloat()) {
                        return false;
                    }
                }
            }
            
            boolean [][] preferredDaysOff = preferences.getPreferredDaysOff();
		
            for (int i=0; i < preferredDaysOff.length; i++) {
                for (int j=0; j < preferredDaysOff[0].length; j++) {
                    if (preferredDaysOff[i][j]) {
                        for (Time time : times) {
                            if (time.getDayInteger() == j && areTermsEqual(time.getTerm(), i)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }

        private List<ActivityList> filterActivityLists(List<ActivityList> activityLists, Preferences preferences)  throws BadAttributeValueExpException {
            List<ActivityList> filteredLists = new ArrayList<>();
            for(ActivityList list : activityLists) {
                ActivityList filteredList = new ActivityList(list.getCourseName());
                
                for (int i = 0; i < list.getNumActivityTypes(); i++) {
                    for(Activity activity : list.getListOfActivities(i)) {
                        if (filterActivity(activity, preferences)) {
                            filteredList.checkExistingAndAdd(activity);
                        }
                    }
                    if (filteredList.getListOfActivities(i).isEmpty() && !list.getListOfActivities(i).isEmpty()) {
                        // handle case where no activities match the filter
                        throw new BadAttributeValueExpException("No results found for your preferences");
                    }
                }
                filteredLists.add(filteredList);
            }
            return filteredLists;
        }

	/**
	 * Constructor
	 * @param selectedCourses, courses to make timetables from
	 * @throws BadAttributeValueExpException
	 */
	public TimetableHelper(List<ActivityList> activityLists, Preferences preferences) throws BadAttributeValueExpException {
		
		conflicts = new LinkedHashSet<>();
		validCombinations = new ArrayList<>();
		validCombinationsQueue = new PriorityQueue<>(50, new CombinationComparator());
                this.preferences = preferences;
                preferredEndTime = preferences.getPreferredEndTime();
                preferredStartTime = preferences.getPreferredStartTime();
                
		this.activityLists = filterActivityLists(activityLists, preferences);
                
		
		// 5 types of activities (currently): lectures, labs, tutorials, waiting list and misc. see ActivityList.java
		// 4 types if we exclude waiting lists
		activityIndices = new int[activityLists.size()][4];
		
		listIndex = 0;
		typeIndex = 0;
		
		// init indices to zero
		for (int i=0; i < activityIndices.length; i++) {
			for (int j=0; j < activityIndices[0].length; j++) {
				activityIndices[i][j] = 0;
			}
		}
	}
	

        /**
         * Copy constructor
         * @param activityLists
         * @param validCombinations
         * @param conflicts 
         */
	public TimetableHelper(List<ActivityList> activityLists, 
                List<ValidCombination> validCombinations,
                Set<Conflict> conflicts,
                Preferences preferences) {
		this.validCombinations = validCombinations;
		this.conflicts = conflicts;
		this.activityLists = activityLists;
                this.preferences = preferences;
                preferredEndTime = preferences.getPreferredEndTime();
                preferredStartTime = preferences.getPreferredStartTime();
	}


	/**
	 * Iterates through timetable possibilities
         * Makes a ValidCombination if there are no conflicts between courses
         * Else makes a Conflict
         * Stops after going through all possible combinations, or after adding
         * NUM_COMPARE ValidCombinations
	 * @param selectedCourses
	 * @throws BadAttributeValueExpException
	 * @throws CloneNotSupportedException 
	 */
	public void makeTimetables() throws BadAttributeValueExpException {
		
		if (listIndex >= activityIndices.length)
			return;
		computeCombination();
		long i = 0;
		while (true) {
			i++;
			while ( listIndex < activityIndices.length &&
                                activityIndices[listIndex][typeIndex] >=
                                activityLists.get(listIndex).getListOfActivities(typeIndex).size()-1) {
				
				activityIndices[listIndex][typeIndex] = 0;
				typeIndex++;
				if (typeIndex >= activityIndices[0].length) {
					typeIndex = 0;
					listIndex++;
				}
			}
			if (listIndex >= activityIndices.length) {
                            	// the end of the combinations have been reached, indicate with finishedCombinations = true
				finishedCombinations = true;
				break;
			}
			activityIndices[listIndex][typeIndex] += 1;
			typeIndex = 0;
			listIndex = 0;			
			
			if (computeCombination()) {
                            break;
                        }
                        if (i >= MAX_ITERATIONS) {
                            break;
                        }
				
		}  // end while loop
		System.out.println("makeTimetables() loop iterations: " + i);
		populateValidCombinationsList();
		timetables.setTimetables(validCombinationsQueue);
	}
        
        /**
         * Check if activities belonging to same course are in the same term
         * @param activities
         * @return true if all activities belonging to same course are
         * in the same term, false otherwise
         */
        private Boolean termsMatch(List<Activity> activities) {
            Map<String, List<Activity>> map = new HashMap<>();
            for (Activity activity : activities) {
                String name = activity.getName();
                List<Activity> list = map.get(name);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(activity);
                map.put(name, list);
            }
            
            for (List<Activity> list : map.values()) {
                if (list.size() > 1) {
                    String term = list.get(0).getTerm();
                    for (Activity activity : list) {
                        if (!activity.getTerm().equals(term)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
			
        /**
         * 
         * @return
         * @throws BadAttributeValueExpException 
         */
        private boolean computeCombination() throws BadAttributeValueExpException {
            List<Activity> activities = getActivityCombination();
            
            if (termsMatch(activities)) {
                ValidCombination validCombination = new ValidCombination(activities, preferences);
                if (validCombination.isValid()) {
                    // no conflicts
                    validCombinationsQueue.add(validCombination);
                } 
                else {
                    // conflict found
                    Conflict conflict = validCombination.getConflict();

                    // there was a conflict, if both activities are the only option, there are no possible validCombinations, so break 
                    if (isOnlyOption(conflict.getActivity1()) && isOnlyOption(conflict.getActivity2())) {
                        conflicts.clear();
                        conflicts.add(conflict);
                        finishedCombinations = true;
                        return true;
                    }

                    else if (validCombinationsQueue.isEmpty()) {
                        addConflict(conflict);
                    }
                }
            }
            return false;
        }
	

	/**
	 * Adds highest priority NUM_DISPLAY or size timetables to validCombinations list
	 */
	private void populateValidCombinationsList() {
		validCombinations.clear();
		for (int i=0; i < validCombinationsQueue.size() && i < NUM_DISPLAY; i++) {
			// get the highest priority element from the queue and add it to the list
			ValidCombination validCombination = validCombinationsQueue.poll();
			validCombinations.add(validCombination);
		}
		// put all combinations back into the queue
		validCombinationsQueue.addAll(validCombinations);
	}



	/**
	 * Check if the conflicting activity is the only activity of its type.
	 * If it is, there are no possible timetables
	 * @param activity conflicting activity
	 * @return true if the conflicted activity is the only activity of its type, false otherwise
	 * @throws BadAttributeValueExpException
	 */
	private boolean isOnlyOption(Activity activity) throws BadAttributeValueExpException {
		
		int type = activity.getTypeInteger();
		
		List<Activity> activities = activity.getActivityList().getListOfActivities(type);
		
		if (activities.size() == 1)
			return true;	
		return false;
	}	

	
	
	
	/**
	 * 
	 * @param conflicts
	 */
	public void addAllToConflicts(Set<Conflict> conflicts) {
		for (Conflict conflict : conflicts)
			addConflict(conflict);
	}


	/**
	 * 
	 * @param conflict
	 */
	private void addConflict(Conflict newConflict) {
		int newConflictPriority = newConflict.getPriority();
		
		Set<Conflict> removeConflicts = new LinkedHashSet<>();
		for (Conflict conflict : conflicts) {
			int conflictPriority = conflict.getPriority();
			
			// if an existing conflict equals the new conflict or is higher priority than the new one, don't add it
			if (conflict.equals(newConflict) || conflictPriority < newConflictPriority) {
				conflicts.removeAll(removeConflicts);
				return;
			}
			// if an existing conflict is lower priority than the new one, remove existing
			if (conflictPriority > newConflictPriority)
				removeConflicts.add(conflict);
		}
		conflicts.removeAll(removeConflicts);
		conflicts.add(newConflict);
	}
	
	
	/**
	 * Create a conflict object from a pair of mapEntries
	 * @param mapEntry1, mapEntry2
	 * @return conflict
	 * @throws BadAttributeValueExpException
	 */
	private Conflict mapEntryToConflict(MapEntry mapEntry1, MapEntry mapEntry2) throws BadAttributeValueExpException {
		Activity activity1 = activityLists.get(mapEntry1.getListIndex()).getActivity(mapEntry1.getTypeIndex(), mapEntry1.getActivityIndex());
		Activity activity2 = activityLists.get(mapEntry2.getListIndex()).getActivity(mapEntry2.getTypeIndex(), mapEntry2.getActivityIndex());
		return new Conflict(activity1, activity2);
	}

	
	/**
	 * Check if an activity entry exists at a location specified by listInd, typeInd, activityIndices[listInd][typeInd]
	 * @param listInd @param typeInd
	 * @return true if entry exists, false otherwise
	 * @throws BadAttributeValueExpException
	 */
	private boolean entryExists(int listInd, int typeInd) throws BadAttributeValueExpException {
		return activityIndices[listInd][typeInd] <
                        activityLists.get(listInd).getListOfActivities(typeInd).size();
	}
	
	
	/**
	 * Create a conflict object from a pair of mapEntries and add to conflicts
	 * @param mapEntry1,  mapEntry2
	 * @throws BadAttributeValueExpException
	 */
	public void addConflict(MapEntry mapEntry1, MapEntry mapEntry2) throws BadAttributeValueExpException {
		Conflict conflict = mapEntryToConflict(mapEntry1, mapEntry2);
		addConflict(conflict);
	}

	
        /**
         * Get a list of activities from the current position of the indices
         * in the activityIndices array
         * @return activity combination
         * @throws BadAttributeValueExpException 
         */
        private List<Activity> getActivityCombination() throws BadAttributeValueExpException {
            List<Activity> activities = new ArrayList<>();
            for (int i=0; i < activityIndices.length; i++) {
                for (int j = 0; j < activityIndices[0].length; j++) {
                    if (entryExists(i, j)) {
                        Activity activity = activityLists.get(i).getActivity(j, activityIndices[i][j]);
			if (activity != null) {
                            activities.add(activity);
                        }
                    }
                }
            }
            return activities;
        }
	


	/**
	 * @return the activityLists
	 */
	public List<ActivityList> getActivityLists() {
		return activityLists;
	}
	
	/**
	 * @return the validCombinations
	 */
	public List<ValidCombination> getValidCombinations() {
		return validCombinations;
	}

	/**
	 * @param validCombinations the validCombinations to set
	 */
	public void setValidCombinations(List<ValidCombination> validCombinations) {
		this.validCombinations = validCombinations;
	}
	
	/**
	 * @return the conflicts
	 */
	public Set<Conflict> getConflicts() {
		return conflicts;
	}
	
	/**
	 * @param conflicts the conflicts to set
	 */
	public void setConflicts(Set<Conflict> conflicts) {
		this.conflicts = conflicts;
	}
	
	/**
	 * @return the finishedCombinations
	 */
	public boolean isFinishedCombinations() {
		return finishedCombinations;
	}
}
