/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import app.Preferences;
import helpers.AddCourseHelper;
import helpers.ControllerHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.management.BadAttributeValueExpException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Course;
import helpers.CourseBuilder;
import helpers.TimetablesBuilder;
import model.StandardTimetable;
import webServices.ScrapeWebPage;

/**
 *
 * @author Eric Rothfels
 */
@WebServlet(name = "Controller",
    loadOnStartup = 1,
    urlPatterns = {"/addCourse",
    "/makeTimetables",
    "/courseList",
    "/session",
    "/campus",
    "/signup",
    "/about",
    "/populateCourse",
    "/preferences",
    "/removeCourse"})
public class ControllerServlet extends HttpServlet {
    
    private static final String UBCO_CAMPUS = "UBCO";
    private static final String UBC_CAMPUS = "UBC";
    private static String sessionsXml = null;


    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userPath = request.getServletPath();
        HttpSession session = request.getSession();
        // if category page is requested
        switch (userPath) {
            case "/removeCourse":
                removeCourse(request, session);
                userPath = "/courseList";
                break;
            case "/makeTimetables": 
                Preferences prefs = getTimetablePreferences(session);
                makeTimetables(response, session, prefs);
                return;
            case "/courseList":
                break;
            case "/session":
                String getSession = request.getParameter("getSession");
                
                if (getSession != null && getSession.equals("true")) {
                    getSession(response, session);
                } else {
                    changeSession(request, session);
                }
                return;
            case "/preferences":
                changePreferences(request, response, session);
                return;
            case "/campus":
                changeCampus(request, session);
                return;
            case "/populateCourse":
                populateCourse(request, response, session);
                return;
        }
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        forward(userPath, request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");  // ensures that user input is interpreted as
        // 8-bit Unicode (e.g., for Czech characters)

        String userPath = request.getServletPath();
        HttpSession session = request.getSession();
        
        switch (userPath) {
            case "/addCourse":
                addCourse(request, response, session);
                return;
        }
        forward(userPath, request, response);
    }

    
    /**
     * Forwards to specified path
     * This does not change the url of the original request
     * and does not call doGet on the new path
     * @param path, request, response 
     */
    private void forward(String path, HttpServletRequest request, HttpServletResponse response) {
        String url = getJspPath(path);
        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    
    /**
     * Redirects to specified path
     * This changes the url in the browser and call doGet on the new path
     * @param path, request, response
     * @throws IOException 
     */
    private void redirect(String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getContextPath() + path;
        String urlWithSessionID = response.encodeRedirectURL(url);
        response.sendRedirect(urlWithSessionID);
    }


    private String getJspPath(String path) {
        return (path.equals("/")) ? "index.jsp" : "/WEB-INF/servelets" + path + ".jsp";
    }
    
    private static void emptyCourseList(HttpSession session) {
        session.setAttribute("courseList", null);
    }

    /**
     * Populate a course's activity data. Triggered by an ajax call from the UI
     */
    private void populateCourse(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {
        // get user input from request
        String courseName = request.getParameter("courseName");

        if (courseName != null) {
            courseName = AddCourseHelper.processCourseName(courseName);
            String xmlResponse = "";
            
            // find the course to populate in the list of added courses
            List<Course> courses = (List<Course>) httpSession.getAttribute("courseList");
            if (courses != null) {
                for (Course course : courses) {
                    if (course.getCourseName().equals(courseName)) {
                        try {
                            // populate the course's data
                            course.populateActivityData();
                            xmlResponse = "<success>" + courseName + "</success>";
                        } catch (Exception ex) {
                            xmlResponse = "<error>" + ex + "</error>";
                            ex.printStackTrace();
                        } finally {
                            writeXmlResponse(response, xmlResponse);
                            return;
                        }
                    }
                }
            }
            String error = "Course " + courseName + " has not been added to the session course list";
            writeXmlResponse(response, "<error>" + error + "</error>");
        }
    }
    
    private void addCourse(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) {
        // get user input from request
        String courseInput = request.getParameter("courseInput");

        if (courseInput != null) {
            courseInput = AddCourseHelper.processCourseName(courseInput);
            
            List<Course> courses = (List<Course>) httpSession.getAttribute("courseList");
            if (courses == null) {
                courses = new ArrayList<>();
            }
            // check if the user has already added the course
            for (Course course : courses) {
                if (course.getCourseName().equals(courseInput)) {
                    String xmlResponse = "<error>You have already added " + courseInput + "</error>";
                    writeXmlResponse(response, xmlResponse);
                    return;
                }
            }
            String session = (String) httpSession.getAttribute("session");
            String campus = (String) httpSession.getAttribute("campus");
            
            String xmlResponse = "";
            try {
                // build the course:
                CourseBuilder cb = new CourseBuilder(courseInput, session, campus);
                Course course = cb.buildCourse();
                
                // add it to the session
                courses.add(course);
                httpSession.setAttribute("courseList", courses);
                
                xmlResponse = cb.getCourseXml();

            } catch (BadAttributeValueExpException ex) {
                String error = ex.toString().split("BadAttributeValueException: ")[1];
                xmlResponse = "<error>" + error + "</error>";
            } catch (Exception ex) {
                //Logger.getLogger(ControllerServlet.class.getName()).log(Level.WARNING, null, ex);
                xmlResponse = "<error>" + ex + "</error>";
                ex.printStackTrace();
            }
            finally {
                writeXmlResponse(response, xmlResponse);
            }
        }
    }
    

    private void removeCourse(HttpServletRequest request, HttpSession session) {
        String courseInput = request.getParameter("course");

        if (courseInput != null) {
            // remove course from list in user's session
            List<Course> courses = (List<Course>) session.getAttribute("courseList");
            if (courses != null) {
                if (courseInput.equals("REMOVE_ALL")) {
                    courses.clear();
                    session.setAttribute("courseList", courses);
                } else {
                    for (Course course : courses) {
                        if (course.getCourseName().equals(courseInput)) {
                            courses.remove(course);
                            session.setAttribute("courseList", courses);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void makeTimetables(HttpServletResponse response, HttpSession session, Preferences prefs) {
        // build response
        String xmlResponse = null;
        List<Course> courses = (List<Course>) session.getAttribute("courseList");
        
        // check if courses are finished loading
        xmlResponse = ControllerHelper.isCoursesFinishedLoading(courses);
        if (xmlResponse != null) {
            writeXmlResponse(response, xmlResponse);
            return;
        }
        // don't allow multiple makeTimetables() for same user
        Boolean makingTimetables = (Boolean) session.getAttribute("makingTimetables");
        if (makingTimetables != null && makingTimetables) {
            writeXmlResponse(response, xmlResponse);
            return;
        }
        
        try {
            session.setAttribute("makingTimetables", true);
            List<StandardTimetable> stts = (List<StandardTimetable>) session.getAttribute("sttList");
            long start = System.nanoTime();

            TimetablesBuilder ttBuilder = new TimetablesBuilder(courses, prefs);
            xmlResponse = ttBuilder.makeTimetables();

            double time = (System.nanoTime() - start) / 1000000000.0;
            System.out.println("Maketimetables runtime: " + time + "s");

        } catch (Exception ex) {
            xmlResponse = "<error>" + ex + "</error>";
            ex.printStackTrace();
        } finally {
            session.setAttribute("makingTimetables", false);
            writeXmlResponse(response, xmlResponse);
        }
    }

    private void changeSession(HttpServletRequest request, HttpSession session) {
        String sesh = request.getQueryString().replace("%20", " ");
        
        // update the active session
        sessionsXml = sessionsXml.replaceFirst("<activeSession>(.*)</activeSession>",
                "<activeSession>" + sesh + "</activeSession>");
        
        // set the current session
        session.setAttribute("session", sesh.substring(0, sesh.length()-5));
        emptyCourseList(session);
    }

    private void changeCampus(HttpServletRequest request, HttpSession session) {
        String campus = request.getQueryString();
        if (campus != null && campus.trim().equals("Okanagan")) {
            session.setAttribute("campus", UBCO_CAMPUS);
        } else {
            session.setAttribute("campus", UBC_CAMPUS);
        }
        emptyCourseList(session);
    }
    /**
     * Scrape the sessions from the main course website
     * @param response 
     */
    private void getSession(HttpServletResponse response, HttpSession session) {
        if (sessionsXml != null) {
            // the session info has already been scraped and cached, return it
            writeXmlResponse(response, sessionsXml);
        } else {
            // scrape the session info from the ubc course website, and return it
            String xmlResponse = "";
            try {
                // get list of sessions from ubc main course page
                List<String> sessionList = ScrapeWebPage.getSessions();

                xmlResponse += "<sessions>";
                
                // set the current session
                String activeSession = sessionList.get(0);
                session.setAttribute("session", activeSession.substring(0, activeSession.length()-5));
                
                // make the xml response to return to the client
                xmlResponse += "<activeSession>" + activeSession + "</activeSession>";
                
                for (String sesh : sessionList) {
                    xmlResponse += "<session>" + sesh + "</session>";
                }
                xmlResponse += "</sessions>";
                
                // save the session data
                sessionsXml = xmlResponse;
            } catch (Exception ex) {
                xmlResponse = "<error>" + ex + "</error>";
                ex.printStackTrace();
            } finally {
                writeXmlResponse(response, xmlResponse);
            }
        }
    }
    
    /**
     * Return an ajax xml response
     * @param response the http response object
     * @param xmlResponse the xml response data
     */
    private void writeXmlResponse(HttpServletResponse response, String xmlResponse) {
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        try {
            response.getWriter().write(xmlResponse);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the user's timetable preferences from their running session
     * If the user doesn't have an associated preferences object yet, create one
     * @param session
     * @return 
     */
    private Preferences getTimetablePreferences(HttpSession session) {
        Preferences prefs = (Preferences) session.getAttribute("preferences");
        if (prefs == null) {
            prefs = new Preferences();
            session.setAttribute("preferences", prefs);
        }
        return prefs;
    }

    private void changePreferences(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String dayoffTerm1 = request.getParameter("dayoffTerm1");
        String dayoffTerm2 = request.getParameter("dayoffTerm2");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String breakLength = request.getParameter("breakLength");
        
        Preferences prefs = getTimetablePreferences(session);
        try {
            if (dayoffTerm1 != null) {
                String checked = request.getParameter("checked");
                int day = Integer.parseInt(dayoffTerm1);
                
                if (checked != null && checked.equals("true")) {
                    prefs.setPreferredDayOff(1, day, true);
                } else if (checked != null && checked.equals("false")) {
                    prefs.setPreferredDayOff(1, day, false);
                }

            } else if (dayoffTerm2 != null) {
                String checked = request.getParameter("checked");
                int day = Integer.parseInt(dayoffTerm2);
                
                if (checked != null && checked.equals("true")) {
                    prefs.setPreferredDayOff(2, day, true);
                } else if (checked != null && checked.equals("false")) {
                    prefs.setPreferredDayOff(2, day, false);
                }

            } else if (startTime != null) {
                int time = Integer.parseInt(startTime);
                prefs.setPreferredStartTime(time);

            } else if (endTime != null) {
                int time = Integer.parseInt(endTime);
                prefs.setPreferredEndTime(time);

            } else if (breakLength != null) {
                int breakLen = Integer.parseInt(breakLength);
                prefs.setPreferredBreakLength(breakLen);
            }
            session.setAttribute("preferences", prefs);
        } catch (Exception ex) {
            String xmlResponse = "<error>" + ex + "</error>";
            writeXmlResponse(response, xmlResponse);
            ex.printStackTrace();
        }
    }
}