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

	private String comment;
	private String description;

	private String displayName;
	private DigitalTwinModelIdentifier id;
	private Integer maxMultiplicity;
	private Integer minMultiplicity;
	private String name;
	private final List<PropertyObject> properties = new ArrayList<>();

	private String target;

	private boolean writable;

	@SuppressWarnings("unchecked")
	public RelationshipObject(final Map<String, Object> relationship) {
		for (final Entry<String, Object> record : relationship.entrySet()) {
			switch (record.getKey()) {
			case "@type":
				if (!"Property".equals(record.getValue())) {
					throw new IllegalArgumentException("@type must be Property but is " + record.getValue());
				}
				break;
			case "@id":
				this.id = DigitalTwinModelIdentifier.fromString(record.getValue().toString());
				break;
			case "name":
				this.name = record.getValue().toString();
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

	public DigitalTwinModelIdentifier getId() {
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

	public String getTarget() {
		return target;
	}

	public boolean isWritable() {
		return writable;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Relationship [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (comment != null) {
			builder.append("comment=");
			builder.append(comment);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (displayName != null) {
			builder.append("displayName=");
			builder.append(displayName);
			builder.append(", ");
		}
		if (maxMultiplicity != null) {
			builder.append("maxMultiplicity=");
			builder.append(maxMultiplicity);
			builder.append(", ");
		}
		if (minMultiplicity != null) {
			builder.append("minMultiplicity=");
			builder.append(minMultiplicity);
			builder.append(", ");
		}
		if (properties != null) {
			builder.append("properties=");
			builder.append(properties);
			builder.append(", ");
		}
		if (target != null) {
			builder.append("target=");
			builder.append(target);
			builder.append(", ");
		}
		builder.append("writable=");
		builder.append(writable);
		builder.append("]");
		return builder.toString();
	}

	RelationshipObject setProperties(final List<Map<String, Object>> props) {
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