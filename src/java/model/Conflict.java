package model;

public class Conflict {
	
	private final Activity activity1;
	private final Activity activity2;

	private final int priority;

	
	/**
	 * Construct a conflict
	 * @param activity1, activity2
	 */
	public Conflict(Activity activity1, Activity activity2) {
		this.activity1 = activity1;
		this.activity2 = activity2;
		priority = activity1.getPriority() + activity2.getPriority();
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activity1 == null) ? 0 : activity1.hashCode());
		result = prime * result
				+ ((activity2 == null) ? 0 : activity2.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Conflict))
			return false;
		Conflict other = (Conflict) obj;
		if (activity1 == null) {
			if (other.activity1 != null)
				return false;
		} else if (!activity1.equals(other.activity1))
			return false;
		if (activity2 == null) {
			if (other.activity2 != null)
				return false;
		} else if (!activity2.equals(other.activity2))
			return false;
		return true;
	}

	/**
	 * @return the activity1
	 */
	public Activity getActivity1() {
		return activity1;
	}
	/**
	 * @return the activity2
	 */
	public Activity getActivity2() {
		return activity2;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
}
