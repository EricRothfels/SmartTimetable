package webServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.BadAttributeValueExpException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
* @author Eric Rothfels
* @author Scott Mastromatteo
*/

public class ScrapeWebPage {

	
	/**
	 * Ensure that the department code entered by user is of correct format
	 * @param department
	 * @throws BadAttributeValueExpException
	 */
	public static String checkSTTInput(String department) throws BadAttributeValueExpException {
		department = department.trim();
		String checkDept = "[A-Z]{2,5}";
	    Pattern deptP = Pattern.compile(checkDept);
	    Matcher deptM = deptP.matcher(department);
	    
		// check for invalid course
	    if (!deptM.matches())
	    	throw new BadAttributeValueExpException("Invalid department");
	    
	    return department;
	}
	
	
	/**
	 *  Modifies: Nothing
	 *  Requires: Course code to be separated from department by a " "
	 *  Effects: Returns URL of UBC course
	 * @param course, session, year
	 * @return
	 */
	public static String getCourseUrl(String course, String session, String campus) throws BadAttributeValueExpException {
	    
            if (course == null)
                throw new BadAttributeValueExpException("Course Code is null");
            if (session == null)
                throw new BadAttributeValueExpException("Session is null");
            
            if(course.indexOf(" ") < 0 || session.indexOf(" ") < 0)
                throw new BadAttributeValueExpException("Invalid Course Code");

            String[] courseCode = course.split(" ");
            String department = courseCode[0];
            String code = courseCode[1];

            String[] sesh = session.split(" ");
            String year = sesh[0];
            session = sesh[1];

            // return the course link
            return "https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=3&dept="
                + department + "&course=" + code + "&sessyr=" + year + "&sesscd=" + session + "&campuscd=" + campus;
	}
	
	
	/**
	 *  Modifies: Nothing
	 *  Requires: Course code to be separated from department by a " "
	 *  Effects: Returns URL of UBC activity
	 * @param course, session, year
	 * @return
	 */
	public static String getActivityUrl(String course, String session, String campus) {	    
					
		String[] courseCode = course.split(" ");
		String department = courseCode[0];
		String code = courseCode[1];
		
		String[] sesh = session.split(" ");
		String year = sesh[0];
		session = sesh[1];
		
		// return the course link
		return "https://courses.students.ubc.ca/cs/main?pname=subjarea&tname=subjareas&req=5&dept="
				+ department + "&course=" + code + "&sessyr=" + year + "&sesscd=" + session + "&campuscd=" + campus
				+ "&section=";
	}
	
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Future<Document> getDocument(String url) throws IOException {
				
		//Create instance of Callable task
		WebpageRequest webpageRequest = new WebpageRequest();
		webpageRequest.setURL(url);
				
		FutureTask<Document> document = new FutureTask<Document>(webpageRequest);		
		//Create a thread using the task object
		new Thread(document).start();

		return document;
	}
	
	
	/**
	 * 	Modifies: Nothing
	 *  Requires: Course code to be separated from department by a " "
	 *  Effects: Returns URL of UBC Vancouver course
	 * @param department
	 * @param session
	 * @param campus
	 * @return
	 */
	public static String getSTTUrl(String department, String session, String campus) {
		String[] sesh = session.split(" ");
		String year = sesh[0];
		session = sesh[1];
		
		return "https://courses.students.ubc.ca/cs/main?pname=sttdept&tname=sttdept&dept=" + department +
				"&sessyr=" + year + "&sesscd=" + session + "&campuscd=" + campus;
	}
	
	
	/**
	 * 
	 * @param profName Name of professor
	 * @return url link to first page of ubc professors with the same first letter of the last name
	 */
	protected static String getRateMyProfURL(String profName) {
		//TODO change to searching for professor
		return "http://www.ratemyprofessors.com/SelectTeacher.jsp?sid=1413&orderby=TLName&letter="
				+ profName.substring(0,1) + "&pageNo=1";
		
		// in the source code of this page we look for the profName. if not found go to next page, until there is no
		// next page or the profName is alphabetically smaller then the currently found prof's name
		// 308188 is what we need:  <div class="profName"><a href="ShowRatings.jsp?tid=308188">Jetter, Reinhard</a></div>
		// the link for Jetter, Reinhard is:
		// http://www.ratemyprofessors.com/ShowRatings.jsp?sid=1413&tid=308188  // sid=1413 denotes UBC
	}
        
        public static List<String> getSessions() throws IOException, InterruptedException, ExecutionException {
            String url = "https://courses.students.ubc.ca/cs/main";
            Document mainPageDoc = getDocument(url).get();
            Elements elements = mainPageDoc.getElementsByClass("breadcrumb");
            
            if (!elements.isEmpty()) {
                 elements = elements.get(0).getElementsByClass("dropdown-menu");
                 if (elements.size() > 1) {
                     elements = elements.get(1).getElementsByTag("a");
                     if (!elements.isEmpty()) {
                        Iterator<Element> elementIterator = elements.iterator();

                        List<String> sessionList = new ArrayList<>(elements.size());
                        while (elementIterator.hasNext()) {
                            Element element = elementIterator.next();
                            sessionList.add(element.text());
                        }
                        return sessionList;
                     }
                 }
            }
           return null;
        }
}