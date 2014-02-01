package comparators;

import java.util.Comparator;
import model.Time;

public class TimeComparator implements Comparator<Time> {

	@Override
	public int compare(Time t1, Time t2) {
		
		float startTime1 = t1.getStartTimeFloat();
		float startTime2 = t2.getStartTimeFloat();
		
		if (startTime1 == startTime2)
			return 0;
		
		// t1 is earlier, so higher priority
		if (startTime1 < startTime2)
			return -1;
		
		// t2 is earlier and higher priority
		return 1;
	}

}
