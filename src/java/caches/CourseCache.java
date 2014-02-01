/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package caches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Course;

/**
 *
 * @author eric
 */
public class CourseCache {
    
    private static CourseCache cache = null; 
    private static Map<String, Course> courseMap;
    private static List<Course> courseList;

   
    
    
    private CourseCache() {
        courseMap = new HashMap<String, Course>();
        courseList = new ArrayList<Course>();
    }
    
    
    
    public static CourseCache getInstance() {
        if (cache == null)
            cache = new CourseCache();
        return cache;
    }
    
    public static List<Course> getCourseList() {
        return courseList;
    }
     
    public static Map<String, Course> getCourseMap() {
        return courseMap;
    }
    
    public static void put(Course course) {
        courseMap.put(course.getCourseName(), course);
        courseList.add(course);
    }
    
    public static Course get(String courseName) {
        return courseMap.get(courseName);
    }
    
    public static void remove(String courseName) {
        Course course = courseMap.remove(courseName);
        if (course != null)
            courseList.remove(course);
    }
    
    public static void emptyCache() {
        courseMap = new HashMap<String, Course>();
        courseList = new ArrayList<Course>();
    }
}
