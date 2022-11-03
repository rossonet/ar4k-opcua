package org.rossonet.opcua.milo.utils.dtdl;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentObject {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ComponentObject.class);

	private String comment;
	private String description;
	private String displayName;
	private DigitalTwinModelIdentifier id;
	private String name;

	private Schema schema;

	ComponentObject(final Map<String, Object> component) {
		for (final Entry<String, Object> record : component.entrySet()) {
			switch (record.getKey()) {
			case "@id":
				this.id = DigitalTwinModelIdentifier.fromString(record.getValue().toString());
				break;
			case "@type":
				if (!"Component".equals(record.getValue())) {
					throw new IllegalArgumentException("@type must be Component but is " + record.getValue());
				}
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

	public String getName() {
		return name;
	}

	public Schema getSchema() {
		return schema;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Component [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (schema != null) {
			builder.append("schema=");
			builder.append(schema);
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
		}
		builder.append("]");
		return builder.toString();
	}

}