/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import caches.CourseCache;
import model.Course;

/**
 *
 * @author eric
 */
public class CourseBuilder {
    
    private static CourseCache courseCache = CourseCache.getInstance();
    
    private String courseName;
    private String session;
    private String campus;
    private Course course;
    
    public CourseBuilder(String courseName, String session, String campus) {
        this.courseName = courseName;
        this.session = session;
        this.campus = campus;
    }


    /**
     * checks to see if the future course(s) have gotten required information
     * then adds the course(s)
     */
    public Course buildCourse() throws Exception {
        // check if the course is in the cache
        course = courseCache.get(session, campus, courseName);
        if (course == null) {
            // TODO check if the course is in the database
            System.err.println("course was NOT in the cache");
            
            // make a new course
            course = new Course(courseName, session, campus);
            courseCache.put(session, campus, course);
        } else {
            System.err.println("course " + course.getCourseName() + " was IN the cache");
        }
        return course;
    }
    
    public String getCourseXml() {
        String xmlResponse = "";
        if (course != null) {
            xmlResponse = "<course>" +
            "<courseName>" + course.getCourseName() + "</courseName>" +
            "<url>" + course.getUrl().replaceAll("&", "&amp;") + "</url>" +
         "</course>";
        }
        return xmlResponse;
    }
}
