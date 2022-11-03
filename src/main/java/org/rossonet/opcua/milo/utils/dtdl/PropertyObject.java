package org.rossonet.opcua.milo.utils.dtdl;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyObject {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(PropertyObject.class);
	private String id;
	private String name;
	private String comment;

	private String description;

	private String displayName;

	private Schema schema;

	private String unit;

	private boolean writable;

	PropertyObject(Map<String, Object> property) {
		for (final Entry<String, Object> record : property.entrySet()) {
			switch (record.getKey()) {
			case "@id":
				this.id = record.getValue().toString();
				break;
			// TODO gestione @type come lista
			case "@type":
				if (!"Property".equals(record.getValue())) {
					throw new IllegalArgumentException("@type must be Property but is " + record.getValue());
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
			case "unit":
				this.unit = record.getValue().toString();
				break;
			case "writable":
				this.writable = Boolean.valueOf(record.getValue().toString());
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

	public String getName() {
		return name;
	}

	public Schema getSchema() {
		return schema;
	}

	public String getUnit() {
		return unit;
	}

	public boolean isWritable() {
		return writable;
	}

}