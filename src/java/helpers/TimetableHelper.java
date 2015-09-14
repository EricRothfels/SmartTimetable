package helpers;

import app.Preferences;
import comparators.CombinationComparator;
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
import model.Time;
import model.ValidCombination;

public class TimetableHelper {

    private Set<Conflict> conflicts;
    private PriorityQueue<ValidCombination> validCombinationsQueue;
    private Set<ValidCombination> validCombinations;

    // list of activities to make timetables from
    private List<ActivityList> activityLists;

    // keep track of which indices in the array are to be incremented next
    private int listIndex;
    private int typeIndex;

    // keep an array of activity indices
    private int[][] activityIndices;

    // number of combinations we display to user
    private static final int NUM_DISPLAY = 15;
    
    // max number of valid combinations to look for
    private static final int MAX_NUM_VALID = 60;

    // max number of iterations for the loop in maketimetables()
    private static final long MAX_ITERATIONS = 1500000;

    private final Preferences preferences;
    private final int preferredEndTime;
    private final int preferredStartTime;
    
    
    /**
     * Constructor
     * @throws BadAttributeValueExpException
     */
    public TimetableHelper(List<ActivityList> activityLists, Preferences preferences) throws BadAttributeValueExpException {

        conflicts = new LinkedHashSet<>();
        validCombinations = new LinkedHashSet<>();
        validCombinationsQueue = new PriorityQueue<>(50, new CombinationComparator());
        this.preferences = preferences;
        preferredEndTime = preferences.getPreferredEndTime();
        preferredStartTime = preferences.getPreferredStartTime();

        this.activityLists = filterActivityLists(activityLists, preferences);

		// 5 types of activities (currently): lectures, labs, tutorials, waiting list and misc. see ActivityList.java
        // 4 types if we exclude waiting lists
        activityIndices = new int[this.activityLists.size()][4];

        listIndex = 0;
        typeIndex = 0;

        // init indices to zero
        for (int i = 0; i < activityIndices.length; i++) {
            for (int j = 0; j < activityIndices[0].length; j++) {
                activityIndices[i][j] = 0;
            }
        }
    }

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

        boolean[][] preferredDaysOff = preferences.getPreferredDaysOff();

        for (int i = 0; i < preferredDaysOff.length; i++) {
            for (int j = 0; j < preferredDaysOff[0].length; j++) {
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

    private List<ActivityList> filterActivityLists(List<ActivityList> activityLists, Preferences preferences) throws BadAttributeValueExpException {
        List<ActivityList> filteredLists = new ArrayList<>();
        for (ActivityList list : activityLists) {
            ActivityList filteredList = new ActivityList(list.getCourseName());

            for (int i = 0; i < list.getNumActivityTypes(); i++) {
                for (Activity activity : list.getListOfActivities(i)) {
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
     * Iterates through timetable possibilities Makes a ValidCombination if
     * there are no conflicts between courses Else makes a Conflict Stops after
     * going through all possible combinations, or after adding NUM_COMPARE
     * ValidCombinations
     *
     * @throws BadAttributeValueExpException
     * @throws CloneNotSupportedException
     */
    public void makeTimetables() throws BadAttributeValueExpException {
        if (listIndex >= activityIndices.length) {
            return;
        }
        boolean firstIteration = computeCombination();
        long i = 0;
        while (!firstIteration) {
            i++;
            while (listIndex < activityIndices.length
                    && activityIndices[listIndex][typeIndex]
                    >= activityLists.get(listIndex).getListOfActivities(typeIndex).size() - 1) {

                activityIndices[listIndex][typeIndex] = 0;
                typeIndex++;
                if (typeIndex >= activityIndices[0].length) {
                    typeIndex = 0;
                    listIndex++;
                }
            }
            if (listIndex >= activityIndices.length) {
                // the end of the combinations have been reached
                break;
            }
            activityIndices[listIndex][typeIndex] += 1;
            typeIndex = 0;
            listIndex = 0;

            if (computeCombination()) {
                break;
            }
            if (i >= MAX_ITERATIONS || validCombinations.size() >= MAX_NUM_VALID) {
                break;
            }

        }  // end while loop
        System.out.println("makeTimetables() loop iterations: " + i);
    }

    /**
     * Check if activities belonging to same course are in the same term
     *
     * @param activities
     * @return true if all activities belonging to same course are in the same
     * term, false otherwise
     */
    private Boolean termsMatch(List<Activity> activities) {
        Map<String, List<Activity>> map = new HashMap<>();
        for (Activity activity : activities) {
            String name = activity.getCourseName();
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
     * returns true iff there is a conflict which is the only option
     * so no valid combinations can exist
     * else returns false
     * @return @throws BadAttributeValueExpException
     */
    private boolean computeCombination() throws BadAttributeValueExpException {
        List<Activity> activities = getActivityCombination();

        if (termsMatch(activities)) {
            ValidCombination validCombination = new ValidCombination(activities, preferences);
            if (validCombination.isValid()) {
                // no conflicts
                if (validCombinations.add(validCombination)) {
                    validCombinationsQueue.add(validCombination);
                }
            } else {
                // conflict found
                Conflict conflict = validCombination.getConflict();

                // there was a conflict, if both activities are the only option, there are no possible validCombinations, so break 
                if (isOnlyOption(conflict.getActivity1()) && isOnlyOption(conflict.getActivity2())) {
                    conflicts.clear();
                    conflicts.add(conflict);
                    return true;
                } else if (validCombinationsQueue.isEmpty()) {
                    addConflict(conflict);
                }
            }
        }
        return false;
    }

    /**
     * Check if the conflicting activity is the only activity of its type. If it
     * is, there are no possible timetables
     *
     * @param activity conflicting activity
     * @return true if the conflicted activity is the only activity of its type,
     * false otherwise
     * @throws BadAttributeValueExpException
     */
    private boolean isOnlyOption(Activity activity) throws BadAttributeValueExpException {

        int type = activity.getTypeInteger();

        List<Activity> activities = activity.getActivityList().getListOfActivities(type);

        if (activities.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param conflicts
     */
    public void addAllToConflicts(Set<Conflict> conflicts) {
        for (Conflict conflict : conflicts) {
            addConflict(conflict);
        }
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
            if (conflictPriority > newConflictPriority) {
                removeConflicts.add(conflict);
            }
        }
        conflicts.removeAll(removeConflicts);
        conflicts.add(newConflict);
    }

    /**
     * Check if an activity entry exists at a location specified by listInd,
     * typeInd, activityIndices[listInd][typeInd]
     *
     * @param listInd @param typeInd
     * @return true if entry exists, false otherwise
     * @throws BadAttributeValueExpException
     */
    private boolean entryExists(int listInd, int typeInd) throws BadAttributeValueExpException {
        return activityIndices[listInd][typeInd]
                < activityLists.get(listInd).getListOfActivities(typeInd).size();
    }

    /**
     * Get a list of activities from the current position of the indices in the
     * activityIndices array
     *
     * @return activity combination
     * @throws BadAttributeValueExpException
     */
    private List<Activity> getActivityCombination() throws BadAttributeValueExpException {
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < activityIndices.length; i++) {
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
        List<ValidCombination> validCombinationList = new ArrayList<>();
        for (int i = 0; i < validCombinationsQueue.size() && i < NUM_DISPLAY; i++) {
            // get the highest priority element from the queue and add it to the list
            ValidCombination validCombination = validCombinationsQueue.poll();
            validCombinationList.add(validCombination);
        }
        // put all combinations back into the queue
        //validCombinationsQueue.addAll(validCombinationList);
        return validCombinationList;
    }

    /**
     * @return the conflicts
     */
    public Set<Conflict> getConflicts() {
        return conflicts;
    }
}
