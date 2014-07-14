package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

/**
 * PolymerRepeatUnitSegmentAssociationId generated by hbm2java
 */
public class PolymerRepeatUnitSegmentAssociationId implements
		java.io.Serializable {

	private int prsId;
	private int polymerId;
	private int segmentId;
	private int repeatUnitId;

	public PolymerRepeatUnitSegmentAssociationId() {
	}

	public PolymerRepeatUnitSegmentAssociationId(int prsId, int polymerId,
			int segmentId, int repeatUnitId) {
		this.prsId = prsId;
		this.polymerId = polymerId;
		this.segmentId = segmentId;
		this.repeatUnitId = repeatUnitId;
	}

	public int getPrsId() {
		return this.prsId;
	}

	public void setPrsId(int prsId) {
		this.prsId = prsId;
	}

	public int getPolymerId() {
		return this.polymerId;
	}

	public void setPolymerId(int polymerId) {
		this.polymerId = polymerId;
	}

	public int getSegmentId() {
		return this.segmentId;
	}

	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}

	public int getRepeatUnitId() {
		return this.repeatUnitId;
	}

	public void setRepeatUnitId(int repeatUnitId) {
		this.repeatUnitId = repeatUnitId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof PolymerRepeatUnitSegmentAssociationId))
			return false;
		PolymerRepeatUnitSegmentAssociationId castOther = (PolymerRepeatUnitSegmentAssociationId) other;

		return (this.getPrsId() == castOther.getPrsId())
				&& (this.getPolymerId() == castOther.getPolymerId())
				&& (this.getSegmentId() == castOther.getSegmentId())
				&& (this.getRepeatUnitId() == castOther.getRepeatUnitId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getPrsId();
		result = 37 * result + this.getPolymerId();
		result = 37 * result + this.getSegmentId();
		result = 37 * result + this.getRepeatUnitId();
		return result;
	}

}