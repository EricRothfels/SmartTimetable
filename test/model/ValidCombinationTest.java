/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import app.Preferences;
import helpers.AddCourseHelper;
import helpers.CourseBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.management.BadAttributeValueExpException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eric
 */
public class ValidCombinationTest {
    
    private static Preferences prefs;
    private static List<Activity> activityList1 = new ArrayList();
    private static List<Activity> activityList2 = new ArrayList();
    private static List<Activity> activityList3 = new ArrayList();
    private static List<Activity> activityList4 = new ArrayList();
    private static List<Activity> activityList5 = new ArrayList();
    
    private static List<Activity> activityList6 = new ArrayList();
    private static List<Activity> activityList7 = new ArrayList();

    
    public ValidCombinationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        populateActivityList("CPSC 420", activityList1); // TTH Term 2, 930-11
        populateActivityList("MATH 422", activityList2); // MWF Term 1, 14-15
        populateActivityList("CPSC 421", activityList3); // MWF Term 1, 8-9
        populateActivityList("MATH 421", activityList4); // TTH Term 2, 11-1230
        populateActivityList("MATH 360", activityList5); // TTH Term 1, 930-11
        
        activityList6.addAll(activityList1);
        activityList6.addAll(activityList4);
        
        activityList7.addAll(activityList2);
        activityList7.addAll(activityList3);
    }
    
    @Before
    public void setUp() {
        prefs = new Preferences();
    }
    
    @After
    public void tearDown() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * Test of getPriority method, of class ValidCombination.
     */
    @Test
    public void testGetPriorityMultiPreferences() throws BadAttributeValueExpException {
        // multiple preferences
        // early pref, 7am
        prefs.setPreferredStartTime(7);
        // no break pref, 0 hrs
        prefs.setPreferredBreakLength(0);
        
        int priority1 = getPriority(activityList6);
        int priority2 = getPriority(activityList7);
        System.out.println("testGetPriorityMultiPreferences() test2:  Priority 1: " + priority1 + "  Priority 2: " + priority2);
        
        prefs.togglePreferredDayOff(2, 4);
        
        priority1 = getPriority(activityList6);
        priority2 = getPriority(activityList7);
        System.out.println("testGetPriorityMultiPreferences() test2:  Priority 1: " + priority1 + "  Priority 2: " + priority2);
    }
    
    /**
     * Test of getPriority method, of class ValidCombination.
     */
    @Test
    public void testGetPriorityNoPreferences() {
        // no preferences
        int priority2 = getPriority(activityList2);
        int priority1 = getPriority(activityList1);
        
        assertEquals("Failed priority test of no preferences", priority1, priority2);
        assertEquals("Failed priority test of no preferences: priorities not zero", priority1, 0);
    }
    
    /**
     * Test of test getStartTimePriority method, of class ValidCombination.
     */
    @Test
    public void testEarlyStartTimePreference() {
        // early pref
        prefs.setPreferredStartTime(7);
        int priority2 = getPriority(activityList2);
        int priority1 = getPriority(activityList1);
        
        assertTrue("Failed early start time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 < priority2);
    }
    
    @Test
    public void testLateStartTimePreference() {
        // late pref
        prefs.setPreferredStartTime(24);
        
        List<Activity> activityList6 = new ArrayList();
        List<Activity> activityList7 = new ArrayList();
        activityList6.addAll(activityList2);
        activityList6.addAll(activityList5);
        activityList7.addAll(activityList3);
        activityList7.addAll(activityList5);
        
        int priority1 = getPriority(activityList6);
        int priority2 = getPriority(activityList7);
        
        assertTrue("Failed late start time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 < priority2);
    }
    
    @Test
    public void test10AMStartTimePreference() {
        // 10am pref
        prefs.setPreferredStartTime(10);
        int priority2 = getPriority(activityList2);
        int priority1 = getPriority(activityList1);
        
        assertTrue("Failed 10am start time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 < priority2);
    }
    
    @Test
    public void test1PMStartTimePreference() {
        // 1pm pref
        prefs.setPreferredStartTime(13);
        int priority2 = getPriority(activityList2);
        int priority1 = getPriority(activityList1);
        
        assertTrue("Failed 1pm start time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 > priority2);
    }

    /**
     * Test of test getBreakTimePriority method, of class ValidCombination.
     */
    @Test
    public void testNoBreakTimePreferrence() {
        prefs.setPreferredBreakLength(0);
        int priority1 = getPriority(activityList6);
        int priority2 = getPriority(activityList7);
        
        assertTrue("Failed no break time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 < priority2);
        
        activityList6.addAll(activityList5);
        priority1 = getPriority(activityList6);
        
        assertTrue("Failed no break time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 < priority2);
        assertEquals("Failed no break time preference. Priority1: " +
                priority1 + " Priority2: ", priority1, 0);
    }
    
    @Test
    public void testLargeBreakTimePreferrence() {
        prefs.setPreferredBreakLength(3);
        int priority1 = getPriority(activityList6);
        int priority2 = getPriority(activityList7);
        
        assertEquals("Failed large break time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1, priority2);
        
        prefs.setPreferredBreakLength(5);
        priority1 = getPriority(activityList6);
        priority2 = getPriority(activityList7);
        
        assertTrue("Failed large break time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 > priority2);
    }
    
    /**
     * Test of test getDaysOffPriority method, of class ValidCombination.
     */
    @Test
    public void testGetDaysOffEqualPriority() throws BadAttributeValueExpException {
        prefs.togglePreferredDayOff(2, 1);
        int priority2 = getPriority(activityList1);
        int priority1 = getPriority(activityList4);
        // both have monday off, term 2
        assertEquals("Failed equal days off preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1, priority2);
        
        priority1 = getPriority(activityList2);
        // both have monday off, term 2
        assertEquals("Failed equal days off time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1, priority2);
        
        prefs.togglePreferredDayOff(2, 1);
        prefs.togglePreferredDayOff(1, 2);
        // both have tuesday off, term 1
        assertEquals("Failed equal days off time preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1, priority2);
    }
    
    /**
     * Test of test getDaysOffPriority method, of class ValidCombination.
     */
    @Test
    public void testGetDaysOffPriority() throws BadAttributeValueExpException {
        prefs.togglePreferredDayOff(1, 4);
        int priority2 = getPriority(activityList2);
        int priority1 = getPriority(activityList5);
        // only activityList2 has thursday off
        assertTrue("Failed days off preference. Priority1: " +
                priority1 + " Priority2: " + priority2, priority1 > priority2);
    }
    
    /**
     * Get the priority number from the validCombination with
     * activities activityList
     * and preferences prefs
     * @param activityList
     * @return the priority
     */
    private int getPriority(List<Activity> activityList) {
        ValidCombination validCombination = new ValidCombination(activityList, prefs);
        return validCombination.getPriority();
    }
    
    /**
     * Populate an activity list used to make a valid combination with a single
     * course's activities
     * @param courseName
     * @param activityList
     * @throws Exception 
     */
    private static void populateActivityList(String courseName, List<Activity> activityList) 
        throws Exception {
        List<Course> courses = new ArrayList<>();
        
        // add a course
        CourseBuilder cb = new CourseBuilder(courseName, "2013 W", "UBC");
        Course course = cb.buildCourse();
        courses.add(course);
        
        // wait until the course has finished loading activities
        while (!course.isFinishedLoading()) {
            Thread.sleep(500);
        }
        activityList.addAll(course.getAllActivities());
        
        Activity activity = activityList.get(0);
        System.out.println(activity.getName() + " " +
                activity.getActivityID() + " startTime: " +
                activity.getTimes().get(0).getStartTimeString());
    }
}