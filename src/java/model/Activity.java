package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import smartTimetableException.EmptyTermException;
import webServices.ScrapeWebPage;

/**
 * This class creates an activity where all attributes of an activity are stored
 * @author Eric Rothfels
 *
 */
public class Activity {
	
	// status: full, blocked etc.
	private final String status;

	// type : lab, lecture etc.
	private final String type;
        // activity type in integer format
	private final int typeInteger;
        
	// activityID : 101, or 2W2, L2A etc.
	private String activityID;
	
	/**
	 *  keeps track of the highest priority section contained in the activityList
	 *  lower number is higher priority
	 *  0: lectures, 1: labs, 2: tutorials, 3: misc. 4: waiting lists
         *  this is used to determine if a conflict is significant or not
	 */
	private int priority;
	
	// a reference to the list it is a part of
	private ActivityList activityList;
        
	private String courseName;
	private String term;
	
	// Each element in this list contains a day in which the activity
	// runs and the start and end time of the activity on the day
	private List<Time> times;
	
        private final String url;
	
	
	/**
	 * Activity Constructor
	 * @param typeInteger 
	 * @param courseUrl, section, status
	 * @param activityID, type, comments
	 * @throws InterruptedException, ExecutionException
	 * @throws IOException, EmptyTermException, NoSuchFieldException
	 */
	public Activity(String courseUrl, String status, String activityID, String type, int typeInteger, String courseName)
			throws InterruptedException, EmptyTermException, ExecutionException, IOException, NoSuchFieldException {
		
		this.times = new ArrayList<Time>();
		this.status = status;
		this.activityID = activityID;
		this.type = type;
		this.typeInteger = typeInteger;
		this.courseName = courseName;
		this.priority = typeInteger;
                this.url = courseUrl+activityID;
		
		Document activityDoc = ScrapeWebPage.getDocument(courseUrl+activityID).get();

		// get term for this activity
		getActivityTerm(activityDoc);
		getActivityTimes(activityDoc);

	}

	
	/**
	 * Get time and location info from activity page
	 * @param activityDoc
	 */
	private void getActivityTimes(Document activityDoc)
			throws InterruptedException, ExecutionException, IOException, EmptyTermException, NoSuchFieldException {
		Elements elements = activityDoc.select("table");
		Elements subelements = elements.get(1).select("td");
		
                for (int i=0; i <= subelements.size()-6; i +=6) {
                    String term = subelements.get(i).text();
                    String dayString = subelements.get(i+1).text();
                    String [] days = dayString.split("\\s");
                    String startTime =  subelements.get(i+2).text();
                    String endTime =  subelements.get(i+3).text();
                    String location =  subelements.get(i+4).text();
                    String room = subelements.get(i+5).text();

                    // add all new times to List<Time> times
                    if (dayString.length() > 0 && !term.equals("Total Seats Remaining:")) {
                        for (String day: days) {
                            Time time = new Time(term, day, startTime, endTime, location, room);
                            times.add(time);
                        }
                    }
		}
	}

	
	private void getActivityTerm(Document activityDoc) throws EmptyTermException {
        
            Elements elements = activityDoc.body().getElementsByAttributeValue("role", "main");
            elements = elements.first().getElementsByTag("b");

            for (Element e : elements) {
                String text = e.ownText();
                if (text.startsWith("Term ")) {
                    term = text.substring(4).trim();
                    return;
                }
            }
            throw new EmptyTermException("Could not find a valid term for activity " + activityID +".");
        }

	/**
	 * Effects: returns activity's term
	 * @return the term current activity belongs to
	 */
	public String getTerm() {
		return term;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activityID == null) ? 0 : activityID.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Activity))
			return false;
		Activity other = (Activity) obj;
		if (activityID == null) {
			if (other.activityID != null)
				return false;
		} else if (!activityID.equals(other.activityID))
			return false;
		return true;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the times
	 */
	public List<Time> getTimes() {
		return times;
	}

	/**
	 * @return the course name
	 */
	public String getName() {
		return courseName;
	}
	
	/**
	 * @return the activityID
	 */
	public String getActivityID() {
		return activityID;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param activityListPriority the activityListPriority to set
	 */
	public void adjustPriority(int activityListPriority) {
		this.priority -= activityListPriority;
	}

	/**
	 * @return the typeInteger
	 */
	public int getTypeInteger() {
		return typeInteger;
	}

	/**
	 * @return the activityList
	 */
	public ActivityList getActivityList() {
		return activityList;
	}

	/**
	 * @param activityList the activityList to set
	 */
	public void setActivityList(ActivityList activityList) {
		this.activityList = activityList;
	}
        
        /**
         * @return activity Url
         */
        public String getUrl() {
            return url;
        }
        
        private void debugFields() {
            // Use for debugging activity fields:
            System.out.println("ACTIVITY.java DATA:");
            System.out.println("term:"+term + " " + "activityID:"+activityID);
            System.out.println("status:"+status + " " + "type:"+type);
        }
}