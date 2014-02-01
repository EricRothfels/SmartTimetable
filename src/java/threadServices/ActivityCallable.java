package threadServices;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import smartTimetableException.EmptyTermException;
import model.Activity;

public class ActivityCallable implements Callable<Activity> {

	private String courseUrl;
	private String status;
	private String activityID;
	private String type;
	private String courseName;
	private int typeInteger;
	
	public ActivityCallable(String courseUrl, String status,
			String activityID, String type, int typeInteger, String courseName) {
		
		this.courseUrl = courseUrl;
		this.status = status;
		this.activityID = activityID;
		this.type = type;
		this.courseName = courseName;
		this.typeInteger = typeInteger;
	}

	public Activity call() throws NoSuchFieldException, InterruptedException,
		IOException, EmptyTermException, ExecutionException {
		
		// create a new activity
		return new Activity( courseUrl, status, activityID, type, typeInteger, courseName );
	}
}
