package com.denver.model;


/**
 * POJO class to store departure information.
 * @author  SACHIN PATHADE
 */
public class Departure {

	final private String destination;
	final private String flightGate;
	final private String flightId;
	final private String flightTime;

	public Departure(String flightId, String flightGate,
			String destination, String flightTime) {
		super();
		this.flightId = flightId;
		this.flightGate = flightGate;
		this.destination = destination;
		this.flightTime = flightTime;
	}

	public String getDestination() {
		return destination;
	}

	public String getFlightGate() {
		return flightGate;
	}

	public String getFlightId() {
		return flightId;
	}

	public String getFlightTime() {
		return flightTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Departure other = (Departure) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (flightGate == null) {
			if (other.flightGate != null)
				return false;
		} else if (!flightGate.equals(other.flightGate))
			return false;
		if (flightId == null) {
			if (other.flightId != null)
				return false;
		} else if (!flightId.equals(other.flightId))
			return false;
		if (flightTime == null) {
			if (other.flightTime != null)
				return false;
		} else if (!flightTime.equals(other.flightTime))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((flightGate == null) ? 0 : flightGate.hashCode());
		result = prime * result + ((flightId == null) ? 0 : flightId.hashCode());
		result = prime * result + ((flightTime == null) ? 0 : flightTime.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Departure [flightId=" + flightId + ", flightGate=" + flightGate
		+ ", destination=" + destination + ", flightTime=" + flightTime
		+ "]";
	}
}
