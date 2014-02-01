/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eric
 */
public class AddCourseHelper {
    
    private static List<String> futureCourses = new ArrayList<>();
    
    /**
     * Inserts a space between the department name and course number if the
     * space does not already exist
     * and capitalizes the string
     */
    public static String processCourseName(String courseName) {

        if (!courseName.contains(" ")) {
            // Add a space between courseCode and courseNumber if necessary
            if (!courseName.substring(courseName.length() - 1).matches("\\d")) {
                courseName = courseName.substring(0, courseName.length() - 4) + " " + courseName.substring(courseName.length() - 4);
            } else {
                courseName = courseName.substring(0, courseName.length() - 3) + " " + courseName.substring(courseName.length() - 3);
            }
        }
         return courseName.toUpperCase();
    }
    
    
    public static void addFutureCourse(String courseName) {
        futureCourses.add(courseName);
    }
    
    public static void removeFutureCourse(String courseName) {
        futureCourses.remove(courseName);
    }
    
    public static Boolean isInFutureCourses(String courseName) {
        return futureCourses.contains(courseName);
    }
    
    public static boolean isEmpty() {
        return futureCourses.isEmpty();
    }
    
    public static String getFirstFutureCourseName() {
        return futureCourses.get(0);
    }
    
    public static void emptyFutureCourseList() {
        futureCourses = new ArrayList<>();
    }
}
