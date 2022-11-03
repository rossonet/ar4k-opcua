package org.rossonet.opcua.milo.utils.dtdl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationshipObject {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(RelationshipObject.class);

	private String id;
	private String name;
	private String comment;
	private String description;
	private String displayName;
	private Schema schema;
	private boolean writable;

	private Integer maxMultiplicity;

	private Integer minMultiplicity;

	private String target;

	private final List<PropertyObject> properties = new ArrayList<>();

	public RelationshipObject(Map<String, Object> relationship) {
		for (final Entry<String, Object> record : relationship.entrySet()) {
			switch (record.getKey()) {
			case "@type":
				if (!"Property".equals(record.getValue())) {
					throw new IllegalArgumentException("@type must be Property but is " + record.getValue());
				}
				break;
			case "@id":
				this.id = record.getValue().toString();
				break;
			case "name":
				this.name = record.getValue().toString();
				break;
			case "schema":
				this.schema = new Schema(record.getValue());
				break;
			case "comment":
				this.comment = record.getValue().toString();
				break;
			case "description":
				this.description = record.getValue().toString();
				break;
			case "displayName":
				this.displayName = record.getValue().toString();
				break;
			case "maxMultiplicity":
				this.maxMultiplicity = Integer.valueOf(record.getValue().toString());
				break;
			case "minMultiplicity":
				this.minMultiplicity = Integer.valueOf(record.getValue().toString());
				break;
			case "writable":
				this.writable = Boolean.valueOf(record.getValue().toString());
				break;
			case "target":
				this.target = record.getValue().toString();
				break;
			case "properties":
				setProperties((List<Map<String, Object>>) record.getValue());
				break;
			}
		}
	}

	public String getComment() {
		return comment;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	public Integer getMaxMultiplicity() {
		return maxMultiplicity;
	}

	public Integer getMinMultiplicity() {
		return minMultiplicity;
	}

	public String getName() {
		return name;
	}

	public List<PropertyObject> getProperties() {
		return properties;
	}

	public Schema getSchema() {
		return schema;
	}

	public String getTarget() {
		return target;
	}

	public boolean isWritable() {
		return writable;
	}

	RelationshipObject setProperties(List<Map<String, Object>> props) {
		if (props instanceof List) {
			for (final Map<String, Object> property : props) {
				properties.add(new PropertyObject(property));
			}
		} else {
			throw new IllegalArgumentException("properties must be a List but is " + props.getClass().getName());
		}
		return this;
	}

}