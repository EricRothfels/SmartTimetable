package model;

public class MapEntryConflict {

	private MapEntry mapEntry1;
	private MapEntry mapEntry2;
	
	public MapEntryConflict(MapEntry mapEntry1, MapEntry mapEntry2) {
		this.mapEntry1 = mapEntry1;
		this.mapEntry2 = mapEntry2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mapEntry1 == null) ? 0 : mapEntry1.hashCode());
		result = prime * result
				+ ((mapEntry2 == null) ? 0 : mapEntry2.hashCode());
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
		if (!(obj instanceof MapEntryConflict))
			return false;
		MapEntryConflict other = (MapEntryConflict) obj;
		if (mapEntry1 == null) {
			if (other.mapEntry1 != null)
				return false;
		} else if (!mapEntry1.equals(other.mapEntry1))
			return false;
		if (mapEntry2 == null) {
			if (other.mapEntry2 != null)
				return false;
		} else if (!mapEntry2.equals(other.mapEntry2))
			return false;
		return true;
	}

	/**
	 * @return the mapEntry1
	 */
	public MapEntry getMapEntry1() {
		return mapEntry1;
	}

	/**
	 * @return the mapEntry2
	 */
	public MapEntry getMapEntry2() {
		return mapEntry2;
	}
}
