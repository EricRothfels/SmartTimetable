/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package threadServices;
import helpers.AddCourseHelper;
import model.Course;

/**
 *
 * @author eric
 */
public class PopulateCourseRunnable implements Runnable {
    
    Course course;
    
    public PopulateCourseRunnable(Course course) {
        this.course = course;
    }

    @Override
    public void run() {
        // populate the course's activity data
        course.populateActivityData();
    }
}
