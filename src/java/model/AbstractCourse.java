package model;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.management.BadAttributeValueExpException;

public abstract class AbstractCourse {
	
	// the term in which the user wants to take the course
	// term 0 denotes "any term", term 3 denotes year-long "1-2" courses
	// term 1 denotes term 1 courses, term 2 denotes term2 courses, term 4 denotes any others eg. distance education
	protected Integer term;	
	protected String name;
	protected String session;
	protected String campus;
	
	/**
	 * Course Constructor
	 * @param courseDocument, courseName
	 * @param term, session, campus, courseUrl
	 * @throws BadAttributeValueExpException, IOException, InterruptedException
	 * @throws ExecutionException, NoSuchFieldException, EmptyTermException
	 */
	public AbstractCourse(String name, Integer term, String session, String campus) {
		
		this.name = name;
		this.term = term;
		this.session = session;
		this.campus = campus;		
	}
	
	// Check if any activities/courses are contained
	public abstract boolean isEmpty();
	
	
	/**
	 * Convert a term string to an integer
	 * @param term
	 * @return
	 *//*
	public Integer termStringToInteger(String term) {
		if (term.equals("1"))
			return 1;
		if (term.equals("2"))
			return 2;
		if (term.equals("1-2"))
			return 3;
		return 4;
	}
	*/
	
	
	/**
	 * @return the term
	 */
	public Integer getTerm() {
		return term;
	}


	/**
	 * @return the courseName
	 */
	public String getCourseName() {
		return name;
	}


	/**
	 * @return the session
	 */
	public String getSession() {
		return session;
	}


	/**
	 * @return the campus
	 */
	public String getCampus() {
		return campus;
	}
}
