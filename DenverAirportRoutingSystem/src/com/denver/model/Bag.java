package com.denver.model;

/**
 * POJO class to store BAG information.
 * @author SACHIN PATHADE
 */
public class Bag {
	final private String bagNumber;
	final private String entryPoint;
	final private String flightId;

	public Bag(String bagNumber, String entryPoint, String flightId) {
		super();
		this.bagNumber = bagNumber;
		this.entryPoint = entryPoint;
		this.flightId = flightId;
	}

	public String getBagNumber() {
		return bagNumber;
	}

	public String getEntryPoint() {
		return entryPoint;
	}

	public String getFlightId() {
		return flightId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bag other = (Bag) obj;
		if (bagNumber == null) {
			if (other.bagNumber != null)
				return false;
		} else if (!bagNumber.equals(other.bagNumber))
			return false;
		if (entryPoint == null) {
			if (other.entryPoint != null)
				return false;
		} else if (!entryPoint.equals(other.entryPoint))
			return false;
		if (flightId == null) {
			if (other.flightId != null)
				return false;
		} else if (!flightId.equals(other.flightId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((bagNumber == null) ? 0 : bagNumber.hashCode());
		result = prime * result
		+ ((entryPoint == null) ? 0 : entryPoint.hashCode());
		result = prime * result
		+ ((flightId == null) ? 0 : flightId.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Bag [bagNumber=" + bagNumber + ", entryPoint=" + entryPoint
		+ ", flightId=" + flightId + "]";
	}
}
