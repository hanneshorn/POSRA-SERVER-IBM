package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

/**
 * Segment generated by hbm2java
 */
public class Segment implements java.io.Serializable {

	private Integer segmentId;
	private Smiles smiles;
	private RepeatUnit repeatUnit;
	private int bondCount;
	private String degree;
	private char type;
	private Set polymerRepeatUnitSegmentAssociations = new HashSet(0);
	private Set segmentAssociationsForSegmentId2 = new HashSet(0);
	private Set segmentAssociationsForSegmentId1 = new HashSet(0);

	public Segment() {
	}

	public Segment(int bondCount, String degree, char type) {
		this.bondCount = bondCount;
		this.degree = degree;
		this.type = type;
	}

	public Segment(Smiles smiles, RepeatUnit repeatUnit, int bondCount,
			String degree, char type, Set polymerRepeatUnitSegmentAssociations,
			Set segmentAssociationsForSegmentId2,
			Set segmentAssociationsForSegmentId1) {
		this.smiles = smiles;
		this.repeatUnit = repeatUnit;
		this.bondCount = bondCount;
		this.degree = degree;
		this.type = type;
		this.polymerRepeatUnitSegmentAssociations = polymerRepeatUnitSegmentAssociations;
		this.segmentAssociationsForSegmentId2 = segmentAssociationsForSegmentId2;
		this.segmentAssociationsForSegmentId1 = segmentAssociationsForSegmentId1;
	}

	public Integer getSegmentId() {
		return this.segmentId;
	}

	public void setSegmentId(Integer segmentId) {
		this.segmentId = segmentId;
	}

	public Smiles getSmiles() {
		return this.smiles;
	}

	public void setSmiles(Smiles smiles) {
		this.smiles = smiles;
	}

	public RepeatUnit getRepeatUnit() {
		return this.repeatUnit;
	}

	public void setRepeatUnit(RepeatUnit repeatUnit) {
		this.repeatUnit = repeatUnit;
	}

	public int getBondCount() {
		return this.bondCount;
	}

	public void setBondCount(int bondCount) {
		this.bondCount = bondCount;
	}

	public String getDegree() {
		return this.degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public char getType() {
		return this.type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public Set getPolymerRepeatUnitSegmentAssociations() {
		return this.polymerRepeatUnitSegmentAssociations;
	}

	public void setPolymerRepeatUnitSegmentAssociations(
			Set polymerRepeatUnitSegmentAssociations) {
		this.polymerRepeatUnitSegmentAssociations = polymerRepeatUnitSegmentAssociations;
	}

	public Set getSegmentAssociationsForSegmentId2() {
		return this.segmentAssociationsForSegmentId2;
	}

	public void setSegmentAssociationsForSegmentId2(
			Set segmentAssociationsForSegmentId2) {
		this.segmentAssociationsForSegmentId2 = segmentAssociationsForSegmentId2;
	}

	public Set getSegmentAssociationsForSegmentId1() {
		return this.segmentAssociationsForSegmentId1;
	}

	public void setSegmentAssociationsForSegmentId1(
			Set segmentAssociationsForSegmentId1) {
		this.segmentAssociationsForSegmentId1 = segmentAssociationsForSegmentId1;
	}

}