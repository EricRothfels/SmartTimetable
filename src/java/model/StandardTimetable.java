package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.management.BadAttributeValueExpException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StandardTimetable extends AbstractCourse {

	private List<Course> courses;	
	private String sttUrl;
	boolean populated;
	
	/**
	 * StandardTimetable constructor
	 * @param sttDocument, sttSpecializationAndYear, sttName
	 * @param term, session, campus, status
	 * @throws BadAttributeValueExpException, IOException
	 * @throws InterruptedException, ExecutionException, NoSuchFieldException
	 */
	public StandardTimetable(String sttUrl, String sttName, String session,
			String campus, String status) {
		
		// term 3 for year-long standard timetable
		super(sttName, 3, session, campus);
		this.sttUrl = sttUrl;
		populated = false;
		courses = new ArrayList<Course>();
	}

	
	/**
	 * Populate the list of courses
	 * @param sttDocument
	 * @throws EmptyTermException, ExecutionException 
	 * @throws InterruptedException, IOException 
	 * @throws BadAttributeValueExpException, NoSuchFieldException 
	 */
	public void populateCourses(Document sttDocument) throws Exception {

		Elements elements = sttDocument.getElementsByClass("section1");
		elements.addAll(sttDocument.getElementsByClass("section2"));
/*		
		//Debug:
		for (int i=0; i < elements.size(); i++) {
			System.out.println(i+":zqx:" + elements.get(i));
		}
*/
		for (Element element : elements) {

			Elements tdElements = element.getElementsByTag("td");
			
			String status = tdElements.get(0).text();
			String type = tdElements.get(2).text();
			
			String nameID = tdElements.get(1).text();
			String courseName = nameID.substring(0, nameID.length()-4);
			String activityID = nameID.substring(nameID.length()-3);
			
			//System.out.println("courseName:" +courseName + " activityID:" + activityID + " type:" + type + " status:" + status); // debug
			
			Course course = new Course(courseName, activityID, type, session, campus, status);
			courses.add(course);
		}
		populated = true;
	}

	
	/**
	 * @return the populated
	 */
	public boolean isPopulated() {
		return populated;
	}


	@Override
	public boolean isEmpty() {
		return courses.isEmpty();
	}


	/**
	 * @return the sttUrl
	 */
	public String getSttUrl() {
		return sttUrl;
	}

	/**
	 * @return the courses
	 */
	public List<Course> getCourses() {
		return courses;
	}
}
