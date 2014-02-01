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
        Course course = null;

        // check if the course is in the cache
        course = courseCache.get(courseName);
        if (course == null) {
            // TODO check if the course is in the database

            // make a new course
            course = new Course(courseName, session, campus);
            courseCache.put(course);
        }
        this.course = course;
        return course;
    }
    
    public String getCourseXml() {
        String xmlResponse = "";
        if (course != null) {
            xmlResponse = "<course>" +
            "<courseName>" + course.getCourseName() + "</courseName>" + 
            "<term>" + course.getTerm() + "</term>" +
            "<url>" + course.getUrl().replaceAll("&", "&amp;") + "</url>" +
         "</course>";
        }
        return xmlResponse;
    }
}
