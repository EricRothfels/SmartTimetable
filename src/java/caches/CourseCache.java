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
    private static Map<String, Map<String, Course>> courseMap;

    
    private CourseCache() {
        courseMap = new HashMap<>();
    }
    
    public static CourseCache getInstance() {
        if (cache == null)
            cache = new CourseCache();
        return cache;
    }
    
    private static String getSessionKey(String session, String campus) {
        return session + " " + campus;
    }
    
    
    public static void put(String session, String campus, Course course) {
        String sessionKey = getSessionKey(session, campus);
        Map<String, Course> coursemap;
        if (courseMap.containsKey(sessionKey)) {
            coursemap = courseMap.get(sessionKey);
        } else {
            coursemap = new HashMap();
        }
        coursemap.put(course.getCourseName(), course);
        courseMap.put(sessionKey, coursemap);
    }
    
    public static Course get(String session, String campus, String courseName) {
        String sessionKey = getSessionKey(session, campus);
        Map<String, Course> coursemap = courseMap.get(sessionKey);
        if (coursemap != null) {
            return coursemap.get(courseName);
        }
        return null;
    }
    
    public static void remove(String session, String campus, String courseName) {
        String sessionKey = getSessionKey(session, campus);
        Map<String, Course> coursemap = courseMap.get(sessionKey);
        if (coursemap != null) {
            coursemap.remove(courseName);
        }
    }
    
    public static void emptyCache() {
        courseMap.clear();
    }
}
