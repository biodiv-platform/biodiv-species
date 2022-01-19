package com.strandls.species.pojo;

import java.util.Map;

public class MapAggregationResponse {

	private Map<String, Long> groupSpeciesName;
	private Map<String, Long> groupUserGroupName;
	private Map<String, Long> groupTraits;
	private Map<String, Long> groupMediaType;
	private Map<String, Long> groupRank;
	private Map<String, Long> groupTraitsName;

	public Map<String, Long> getGroupSpeciesName() {
		return groupSpeciesName;
	}

	public void setGroupSpeciesName(Map<String, Long> groupSpeciesName) {
		this.groupSpeciesName = groupSpeciesName;
	}

	public Map<String, Long> getGroupUserGroupName() {
		return groupUserGroupName;
	}

	public void setGroupUserGroupName(Map<String, Long> groupUserGroupName) {
		this.groupUserGroupName = groupUserGroupName;
	}

	public Map<String, Long> getGroupTraits() {
		return groupTraits;
	}

	public void setGroupTraits(Map<String, Long> groupTraits) {
		this.groupTraits = groupTraits;
	}

	public Map<String, Long> getGroupMediaType() {
		return groupMediaType;
	}

	public void setGroupMediaType(Map<String, Long> groupMediaType) {
		this.groupMediaType = groupMediaType;
	}

	public Map<String, Long> getGroupRank() {
		return groupRank;
	}

	public void setGroupRank(Map<String, Long> groupRank) {
		this.groupRank = groupRank;
	}

	public Map<String, Long> getGroupTraitsName() {
		return groupTraitsName;
	}

	public void setGroupTraitsName(Map<String, Long> groupTraitsName) {
		this.groupTraitsName = groupTraitsName;
	}

}
