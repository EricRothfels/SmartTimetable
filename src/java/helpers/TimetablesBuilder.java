/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import app.Preferences;
import threadServices.PopulateSTTRunnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.management.BadAttributeValueExpException;
import model.Activity;
import model.ActivityList;
import model.Conflict;
import model.Course;
import model.StandardTimetable;
import model.Time;
import model.ValidCombination;

/**
 *
 * @author eric
 */
public class TimetablesBuilder {

    //private HttpSession httpSession;
    private List<Course> selectedCourses;
    private TimetableHelper timetableHelper;
    private final Preferences preferences;
    
    public TimetablesBuilder(List<Course> selectedCourses,
                             Preferences prefs) throws Exception {
        this.selectedCourses = selectedCourses;
        this.preferences = prefs;
        this.timetableHelper = init();
    }

    /**
     * Run method: make timetables from selected courses and stts and returns an
     * XML response to be displayed to the user
     */
    private TimetableHelper init() throws Exception {
        boolean courseListIsEmpty = selectedCourses == null || selectedCourses.isEmpty();

        // course list is empty
        if (courseListIsEmpty) {
            throw new BadAttributeValueExpException("Cannot make timetables: No courses have been added.");
        }
        else {
            List<ActivityList> activityLists = populateActivityLists(selectedCourses);
            return new TimetableHelper(activityLists, preferences);
        }
    }
    
    /**
     * Makes one iteration of timetables
     * @return an xml representation of the timetables or conflicts
     * @throws BadAttributeValueExpException 
     */
    public String makeTimetables() throws BadAttributeValueExpException {
        // make the timetables
        timetableHelper.makeTimetables();
        
        Set<Conflict> conflicts = timetableHelper.getConflicts();
        List<ValidCombination> validCombinations = timetableHelper.getValidCombinations();

        System.out.println("validCombinations.size()" + validCombinations.size());
        System.out.println("conflicts.size()" + conflicts.size());
        
        if (validCombinations.isEmpty() && conflicts.isEmpty()) {
            return "<error>No results found for your preferences</error>";
        }

        // there were valid combinations of activities
        if (!validCombinations.isEmpty()) {
            return getXmlResponse(validCombinations);
        } // there were no valid combinations of activities, use the conflicts instead
        else {
            return getXmlResponse(conflicts);
        }
    }

    
    /**
     *
     * @param selectedCourses
     * @return
     * @throws BadAttributeValueExpException
     */
    private List<ActivityList> populateActivityLists(List<Course> selectedCourses) throws BadAttributeValueExpException {

        List<ActivityList> activityLists = new ArrayList<ActivityList>();
        for (Course course : selectedCourses) {
            ActivityList activityList = course.getActivityList(); //getActivityListByTerm(course.getTerm());
            assert (!activityList.isEmpty());
            activityLists.add(activityList);
        }
        return activityLists;
    }

    /**
     * Add course and activity information to all selected stts
     *
     * @param selectedSTTs
     * @throws InterruptedException
     */
    private void populateSTTs(List<StandardTimetable> selectedSTTs) throws InterruptedException {

        List<Thread> threads = new ArrayList<Thread>();

        for (StandardTimetable stt : selectedSTTs) {
            if (!stt.isPopulated()) {
                Thread sttThread = new Thread(new PopulateSTTRunnable(stt));
                sttThread.start();
                threads.add(sttThread);
            }
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private String getXmlResponse(List<ValidCombination> validCombinations) {
        String response = "<timetables>";
        for (ValidCombination combination : validCombinations) {
            response += getXmlResponse(combination.getActivities(), "timetable");
        }
        return response + "</timetables>";
    }

    private String getXmlResponse(Set<Conflict> conflicts) {
        String response = "<conflicts>";
        for (Conflict conflict : conflicts) {
            List<Activity> pairActivities = new ArrayList<>(2);
            pairActivities.add(conflict.getActivity1());
            pairActivities.add(conflict.getActivity2());
            response += getXmlResponse(pairActivities, "conflict");
        }
        return response + "</conflicts>";
    }
    
    private String getXmlResponse(List<Activity> activities, String tag) {
        String result = "";
        result += "<" + tag + ">\n";
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            result += "<activity name=\"" + activity.getActivityList().getCourseName() +
                      "\" id=\"" + activity.getActivityID() +
                      "\" url=\"" + activity.getUrl().replaceAll("&", "&amp;") +
                      "\" type=\"" + activity.getType() +
                      "\" term=\"" + activity.getTerm() + "\">\n";

            List<Time> times = activity.getTimes();
            for (int j = 0; j < times.size(); j++) {
                Time time = times.get(j);
                result += "<time start=\"" + time.getStartTimeString() +
                          "\" end=\"" + time.getEndTimeString() +
                          "\" day=\"" + time.getDay() +
                          "\" term=\"" + time.getTerm() + "\"></time>\n";
            }
            result += "</activity>\n";
        }
        result += "</" + tag + ">\n";
        return result;
    }
}
