package model;


import helpers.AddCourseHelper;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.management.BadAttributeValueExpException;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import threadServices.ActivityCallable;
import threadServices.PopulateCourseRunnable;
import webServices.ScrapeWebPage;


/**
 * Class that constructs a new course
 * Course activities or sections are listed by term
 * @author Eric Rothfels
 * @author Scott Mastromatteo
 *
 */
public class Course extends AbstractCourse {
	
	/** list of activities by term
	 * term0 - any term
	 * term 1 and 2 self explanatory
	 * term3 are year-long "1-2" courses
	 * term4 are "other" termed courses eg. distance education course with term C
	 */
	//private ActivityList [] activityLists = new ActivityList[5];
    
	private ActivityList activityList;
        
        private Document courseDocument;
        private String courseUrl;
        
        private boolean finishedLoading = false;


	/**
	 * Course Constructor
	 * @param courseDocument, courseName
	 * @param term, session, campus, courseUrl
	 * @throws BadAttributeValueExpException, IOException, InterruptedException
	 * @throws ExecutionException, NoSuchFieldException, EmptyTermException
	 */
	public Course(String courseName, String session, String campus)
                throws BadAttributeValueExpException, IOException, InterruptedException, ExecutionException {
                super(courseName, null, session, campus);
                
                // get url for course request
                this.courseUrl = ScrapeWebPage.getCourseUrl(courseName, session, campus);
                this.courseDocument = ScrapeWebPage.getDocument(courseUrl).get();
                this.activityList = new ActivityList(courseName);
		
		// check if url is valid
                checkUrl(courseDocument);
                
                // populate course data asychronously
                Thread thread = new Thread(new PopulateCourseRunnable(this));
                thread.start();
                
                // populate course data sychronously
                //populateActivityData();
	}

	
	/**
	 * Course constructor for standard timetables
	 * @param courseName, activityID, type, term
	 * @param session, campus, status
	 * @throws EmptyTermException, ExecutionException, NoSuchFieldException
	 * @throws InterruptedException, IOException, BadAttributeValueExpException 
	 */
	public Course(String courseName, String activityID, String type,
			String session, String campus, String status) 
			throws Exception {
		
		super(courseName, null, session, campus);
                activityList = new ActivityList(courseName);
                
		populateActivity(activityID, type, status);
		// Check if there are activities in course webPage
		if (isEmpty())
			throw new BadAttributeValueExpException("Add " + courseName + " Unsuccessful: No activities found in course.");
		
/*		// I don't think these will be necessary for stt's. could be wrong		
		// set term based on the activities' terms
		setCourseTerm();
		// set priority of activities based on their type
		setActivityPriority();
*/		
	}
	
        
        public void populateActivityData() throws Exception {
            // populate the 3 activity lists by term
            populateActivities();

            // Check if there are activities in course webPage
            if (isEmpty())
                throw new BadAttributeValueExpException("Add " + name + " Unsuccessful: No activities found in course.");

            // set term based on the activities' terms
            //setCourseTerm();

            // set priority of activities based on their type
            setActivityPriority();
            finishedLoading = true;
        }
        

	/**
	 * 
	 * @param activityID, type, status
	 * @throws InterruptedException, ExecutionException, IOException
	 */
	private void populateActivity(String activityID, String type, String status)
			throws Exception {
		
		String activityUrl = ScrapeWebPage.getActivityUrl(name, session, campus);
		int typeInteger = getTypeInteger(type);
                
		ActivityCallable activityCallable = new ActivityCallable(activityUrl, status, activityID, type, typeInteger, name);
		
		addActivityToList(activityCallable.call());
                finishedLoading = true;
	}


	/**
	 * populates activity lists with all activities in the course, sorted by term
	 * @param courseDocument, courseUrl
	 * @throws NoSuchFieldException, InterruptedException
	 * @throws ExecutionException, IOException, EmptyTermException
	 */
	private void populateActivities() throws Exception {
		
		String activityUrl = courseUrl.replaceFirst("3", "5") + "&section=";
		
		Elements elements = courseDocument.body().getElementsByClass("section1");
		elements.addAll(courseDocument.body().getElementsByClass("section2"));
		
/*		
			//Debug:
		for (int i=0; i < elements.size(); i++) {
			System.out.println(i+":zqx:" + elements.get(i));
		}
*/		
		
		for (Element element : elements) {

			Elements tdElements = element.getElementsByTag("td");
			
			String status = tdElements.get(0).text();

			String activityID = tdElements.get(1).text();
			activityID = activityID.substring(activityID.length()-3);
			
			String type = tdElements.get(2).text();
			int typeInteger = getTypeInteger(type);
                        
                        // don't deal with waiting lists for now
                        if (!type.equals("Waiting List")) {
                            ActivityCallable ac = new ActivityCallable(activityUrl, status, activityID, type, typeInteger, name);
                            addActivityToList(ac.call());
                        }
		}
	}
	
	
	private int getTypeInteger(String type) {
		if (type.equals("Lecture")) 
			return 0;
		if (type.equals("Laboratory"))
			return 1;
		if (type.equals("Tutorial"))
			return 2;
		if (type.equals("Waiting List"))
			return 4;
		//		misc. activities
		return 3;
	}


	/** This Code commented out to make the process slimmer, faster, and simpler.
	 * set the course term based on the terms of its activities
	 */
/*	private void setCourseTerm() {
		int total = 0;
		for (int i=0; i < activityLists.length; i++)
			total += activityLists[i].isEmpty() ? 0:1;
		
		// case activities of more than one term contained:
		if (total > 1) {
			term = 0;
			makeTerm0ActivityList();
		}
		else {
			// case activities of only one term contained:
			for (int i=1; i < activityLists.length; i++) {
				if (!activityLists[i].isEmpty()) {
					term = i;
					break;
				}
			}
		} 		
	}

	
	private void makeTerm0ActivityList() {
		for(int i=1; i < activityLists.length; i++) {
			activityLists[0].addAll(activityLists[i]);
			activityLists[i].clear();
		}
	}
*/

	/**
	 * Adds an activity to the appropriate term list
	 * @param activity
	 */
	private void addActivityToList(Activity activity) {
		activityList.checkExistingAndAdd(activity);
	}
	
	
	/**
	 * Set priority of activityList
	 */
	private void setActivityPriority() {
            try {
                for (int i=0; i < ActivityList.getNumActivityTypes(); i++) {
                    if (!activityList.getListOfActivities(i).isEmpty()) {
                        activityList.adjustActivityPriority(i);
                        break;
                    }
                }
            }
            catch (BadAttributeValueExpException e) {
                e.printStackTrace();
            }
	}


	
	public List<Activity> getAllActivities(){
            return activityList.getAllActivities();
                
	}
        
        
	public ActivityList getActivityList() {
            return activityList;
        }
	
	/**
	 * Check if there are activities in course webPage
	 * @throws BadAttributeValueExpException
	 */
	@Override
	public boolean isEmpty() {
            return activityList.isEmpty();
	}
        
        /**
         * @return the course Url
         */
        public String getUrl() {
            return courseUrl;
        }

        
    /**
     * Check if the course given by the url exists
     * @param courseDocument
     * @throws BadAttributeValueExpException 
     */
    private void checkUrl(Document courseDocument) throws BadAttributeValueExpException {
        Elements elements = courseDocument.body().getElementsByAttributeValue("role", "main");
        elements = elements.first().getElementsByClass("section1");
        if (elements.isEmpty()) {
            // remove course from list of courses being built
            finishedLoading = true;
            throw new BadAttributeValueExpException("Add " + name + " Unsuccessful: "
                    + "The requested course is either no longer offered at "
	    			+ campus + "  or is not being offered this session.");
        }
    }

    public boolean isFinishedLoading() {
        return finishedLoading;
    }

    public void setFinishedLoading(boolean finishedLoading) {
        this.finishedLoading = finishedLoading;
    }
}
