/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.util.List;
import model.Course;

/**
 *
 * @author eric
 */
public class ControllerHelper {
    // check if any courses are in the process of being added
    // if there are, we need to wait until they are finished
    public static String isCoursesFinishedLoading(List<Course> courses) {
        if (courses != null) {
            for (Course course : courses) {
                if (!course.isFinishedLoading()) {
                    String courseName = course.getCourseName();
                    return "<error>Please wait, " + courseName + " is still being processed" + "</error>";
                }
            }
        }
        return null;
    }
}
