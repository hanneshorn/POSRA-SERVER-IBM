package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

/**
 * PolymerRepeatUnitSegmentAssociation generated by hbm2java
 */
public class PolymerRepeatUnitSegmentAssociation implements
		java.io.Serializable {

	private PolymerRepeatUnitSegmentAssociationId id;
	private RepeatUnit repeatUnit;
	private Segment segment;
	private Polymer polymer;
	private char segmentType;

	public PolymerRepeatUnitSegmentAssociation() {
	}

	public PolymerRepeatUnitSegmentAssociation(
			PolymerRepeatUnitSegmentAssociationId id, RepeatUnit repeatUnit,
			Segment segment, Polymer polymer, char segmentType) {
		this.id = id;
		this.repeatUnit = repeatUnit;
		this.segment = segment;
		this.polymer = polymer;
		this.segmentType = segmentType;
	}

	public PolymerRepeatUnitSegmentAssociationId getId() {
		return this.id;
	}

	public void setId(PolymerRepeatUnitSegmentAssociationId id) {
		this.id = id;
	}

	public RepeatUnit getRepeatUnit() {
		return this.repeatUnit;
	}

	public void setRepeatUnit(RepeatUnit repeatUnit) {
		this.repeatUnit = repeatUnit;
	}

	public Segment getSegment() {
		return this.segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public Polymer getPolymer() {
		return this.polymer;
	}

	public void setPolymer(Polymer polymer) {
		this.polymer = polymer;
	}

	public char getSegmentType() {
		return this.segmentType;
	}

	public void setSegmentType(char segmentType) {
		this.segmentType = segmentType;
	}

}