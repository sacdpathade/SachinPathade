package com.denver.model;

/**
 * POJO class to store information for conveyor system route.
 * @author  SACHIN PATHADE
 */
public class ConveyorRoute {
	final private String destination;
	final private String source;
	final private Integer travelTime;

	public ConveyorRoute(String source, String destination, Integer travelTime) {
		super();
		this.source = source;
		this.destination = destination;
		this.travelTime = travelTime;
	}

	public String getDestination() {
		return destination;
	}

	public String getSource() {
		return source;
	}

	public Integer getTravelTime() {
		return travelTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConveyorRoute other = (ConveyorRoute) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ConveyorRoute [Source=" + source + ", Destination=" + destination
		+ ", travelTime=" + travelTime + "]";
	}
}
