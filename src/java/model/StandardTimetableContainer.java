package model;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.management.BadAttributeValueExpException;


public class StandardTimetableContainer extends AbstractCourse {
	
	private List<StandardTimetable> standardTimetables;
	
	public StandardTimetableContainer(List<StandardTimetable> stts, String name,
			String session, String campus) 
			throws BadAttributeValueExpException, IOException,
			InterruptedException, ExecutionException, NoSuchFieldException {
		
		super(name, 3, session, campus);
		standardTimetables = stts;
	}

	@Override
	public boolean isEmpty() {
		return standardTimetables.isEmpty();
	}

	/**
	 * @return the standardTimetables
	 */
	public List<StandardTimetable> getStandardTimetables() {
		return standardTimetables;
	}
	
	/**
	 * @param string
	 * @return standard timetable with name equal to input string
	 */
	public StandardTimetable getStandardTimetable(String string) {
		for (StandardTimetable stt : standardTimetables) {
			if (string.equals(stt.getCourseName()))
				return stt;
		}
		return null;
	}
}
