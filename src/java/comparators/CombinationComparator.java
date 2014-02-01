package comparators;

import java.util.Comparator;
import model.ValidCombination;

public class CombinationComparator implements Comparator<ValidCombination> {

	/**
	 * Returns a positive value if c1 is higher priority than c2, 0 if they are equal in priority, negative value otherwise
	 */
	@Override
	public int compare(ValidCombination c1, ValidCombination c2) {
		int p1 = c1.getPriority();
		int p2 = c2.getPriority();
		
		// equal priority
		if (p1 == p2)
			return 0;
		
		// p1 is higher priority
		else if (p1 < p2)
			return 1;
		
		// p2 is higher priority
		else return -1;
	}

}
