package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

/**
 * Polymer generated by hbm2java
 */
public class Polymer implements java.io.Serializable {

	private Integer polymerId;
	private String name;
	private String externalId;
	private Set polymerRepeatUnitSegmentAssociations = new HashSet(0);
	private Set segmentAssociations = new HashSet(0);

	public Polymer() {
	}

	public Polymer(String name) {
		this.name = name;
	}

	public Polymer(String name, String externalId,
			Set polymerRepeatUnitSegmentAssociations, Set segmentAssociations) {
		this.name = name;
		this.externalId = externalId;
		this.polymerRepeatUnitSegmentAssociations = polymerRepeatUnitSegmentAssociations;
		this.segmentAssociations = segmentAssociations;
	}

	public Integer getPolymerId() {
		return this.polymerId;
	}

	public void setPolymerId(Integer polymerId) {
		this.polymerId = polymerId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Set getPolymerRepeatUnitSegmentAssociations() {
		return this.polymerRepeatUnitSegmentAssociations;
	}

	public void setPolymerRepeatUnitSegmentAssociations(
			Set polymerRepeatUnitSegmentAssociations) {
		this.polymerRepeatUnitSegmentAssociations = polymerRepeatUnitSegmentAssociations;
	}

	public Set getSegmentAssociations() {
		return this.segmentAssociations;
	}

	public void setSegmentAssociations(Set segmentAssociations) {
		this.segmentAssociations = segmentAssociations;
	}

}