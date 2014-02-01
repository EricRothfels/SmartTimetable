package model;

public class MapEntry {
	
	// activity location variables
	private int listIndex;
	private int typeIndex;
	private int activityIndex;
	
	public MapEntry (int listIndex, int typeIndex, int activityIndex) {
		this.listIndex = listIndex;
		this.typeIndex = typeIndex;
		this.activityIndex = activityIndex;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + activityIndex;
		result = prime * result + listIndex;
		result = prime * result + typeIndex;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MapEntry))
			return false;
		MapEntry other = (MapEntry) obj;
		if (activityIndex != other.activityIndex)
			return false;
		if (listIndex != other.listIndex)
			return false;
		if (typeIndex != other.typeIndex)
			return false;
		return true;
	}


	/**
	 * @return the listIndex
	 */
	public int getListIndex() {
		return listIndex;
	}

	/**
	 * @return the typeIndex
	 */
	public int getTypeIndex() {
		return typeIndex;
	}

	/**
	 * @return the activityIndex
	 */
	public int getActivityIndex() {
		return activityIndex;
	}
	
	/**
	 * @return the listIndex
	 */
	public void setActivityIndex(int index) {
		activityIndex = index;
	}
}
