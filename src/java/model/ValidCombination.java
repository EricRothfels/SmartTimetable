package model;

import app.Preferences;
import comparators.TimeComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ValidCombination {
    // number of weekdays
    private static final int NUM_DAYS = 5;
    private static final int NUM_TERMS = 2;

    /* the priority of a ValidCombination
     * lower value: higher priority
     */
    private int priority = 0;

    // the activities in the ValidCombination
    private List<Activity> activities;
    private final Preferences preferences;

    private List<Time>[][] timeLists;

    private Conflict conflict;

    private boolean isValid;

    /**
     * Constructor
     *
     * @param activities
     */
    public ValidCombination(List<Activity> activities, Preferences preferences) {
        this.activities = activities;
        this.preferences = preferences;
        timeLists = populateTimeLists();

        isValid = !hasTimeConflict();
        if (isValid) {
            computePriority();
        }
    }

    /**
     * Returns a priority computed from a valid list of combinations
     *
     * @param activities
     * @return
     */
    private void computePriority() {
        priority += getStartTimePriority(timeLists);
        priority += getBreakTimePriority(timeLists);
    }

    /**
     * @param timeQueues
     * @param activities
     * @return a priority value based on how close each activity is to the
     * preferredStartTime (lower vals are higher priority)
     */
    private int getStartTimePriority(List<Time>[][] timeLists) {
        int preferredStartTime = preferences.getPreferredStartTime();
        return getTimeDiffPriority(preferredStartTime, timeLists, true);
    }

    private int getTimeDiffPriority(int preferredTime,
            List<Time>[][] timeLists,
            boolean isStartTime) {
        if (preferredTime == -1) {
            return 0;
        }
        int priority = 0;

        for (int i = 0; i < timeLists.length; i++) {
            for (int j = 0; j < timeLists[0].length; j++) {
                if (!timeLists[i][j].isEmpty()) {
                    float actualTime;
                    if (isStartTime) {
                        // get start time of the first, earliest time
                        Time time = timeLists[i][j].get(0);
                        actualTime = time.getStartTimeFloat();
                    } else {
                        // get end time of the last, latest time
                        List<Time> timeList = timeLists[i][j];
                        Time time = timeList.get(timeList.size() - 1);
                        actualTime = time.getEndTimeFloat();
                    }
                    priority += Math.abs(preferredTime - actualTime);
                }
            }
        }
        return priority;
    }

    /**
     * @param timeQueues2
     * @return a priority value based on how close each break time is to the
     * preferredBreakTime (lower vals are higher priority)
     */
    private int getBreakTimePriority(List<Time>[][] timeLists) {
        // get user's preferred amount of time between breaks
        double preferredBreakTime = preferences.getPreferredBreakLength();

        // user has no break time preference
        if (preferredBreakTime == -1.0) {
            return 0;
        }

        int priority = 0;
        for (int i = 0; i < timeLists.length; i++) {
            for (int j = 0; j < timeLists[0].length; j++) {
                List<Time> timeList = timeLists[i][j];

                for (int k = 0; k < timeList.size() - 1; k++) {
                    Time time1 = timeList.get(k);
                    Time time2 = timeList.get(k + 1);
                    float breakTime = time2.getStartTimeFloat() - time1.getEndTimeFloat();
                    priority += Math.abs(preferredBreakTime - breakTime);
                }
            }
        }
        return priority;
    }

    /**
     * Builds a list of priority queues, one for each day of the week, in which
     * times are ordered by their start time
     *
     * @return a priority queue of times organized by day, prioritized by start
     * time
     */
    private List<Time>[][] populateTimeLists() {
        // init the lists
        List<Time>[][] timeLists = new ArrayList[NUM_TERMS][NUM_DAYS];
        for (int i = 0; i < NUM_TERMS; i++) {
            for (int j = 0; j < NUM_DAYS; j++) {
                List<Time> times = new ArrayList(4); // initial size 4
                timeLists[i][j] = times;
            }
        }
        // add activities to the lists, separating by term and day
        for (Activity activity : activities) {
            List<Time> times = activity.getTimes();
            String term = activity.getTerm();

            if (term.equals("1") || term.equals("1-2")) {
                for (Time time : times) {
                    timeLists[0][time.getDayInteger()].add(time);
                }
            }
            if (term.equals("2") || term.equals("1-2")) {
                for (Time time : times) {
                    timeLists[1][time.getDayInteger()].add(time);
                }
            }
        }
        // sort the lists by start time
        TimeComparator tc = new TimeComparator();
        for (int i = 0; i < NUM_TERMS; i++) {
            for (int j = 0; j < NUM_DAYS; j++) {
                Collections.sort(timeLists[i][j], tc);
            }
        }
        return timeLists;
    }

    /**
     * Search for conflicts in the activity combination Stops at the first
     * conflict found
     */
    private Boolean hasTimeConflict() {
        for (int i = 0; i < NUM_TERMS; i++) {
            for (int j = 0; j < NUM_DAYS; j++) {
                float endTime = 0;
                List<Time> timeList = timeLists[i][j];
                int count = 0;
                for (Time time : timeList) {
                    if (time.getStartTimeFloat() < endTime) {
                        // conflict found
                        Activity a1 = timeList.get(count - 1).getActivity();
                        Activity a2 = timeList.get(count).getActivity();
                        conflict = new Conflict(a1, a2);
                        return true;
                    }
                    endTime = time.getEndTimeFloat();
                    count += 1;
                }
            }
        }
        return false;
    }

    /**
     * @return true if no conflicts, false otherwise
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * @return the combination priority based on user preferences
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @return the conflict in the combination
     */
    public Conflict getConflict() {
        return conflict;
    }

    /**
     * @return the list of activities in the combination
     */
    public List<Activity> getActivities() {
        return activities;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.activities);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValidCombination other = (ValidCombination) obj;
        if (!Objects.equals(this.activities, other.activities)) {
            return false;
        }
        return true;
    }
    
    
}
