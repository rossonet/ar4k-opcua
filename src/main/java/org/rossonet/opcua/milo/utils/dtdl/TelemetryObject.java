package org.rossonet.opcua.milo.utils.dtdl;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelemetryObject {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(TelemetryObject.class);
	private String id;
	private String name;
	private String comment;

	private String description;

	private String displayName;

	private Schema schema;

	private String unit;

	public TelemetryObject(Map<String, Object> telemetry) {
		for (final Entry<String, Object> record : telemetry.entrySet()) {
			switch (record.getKey()) {
			case "@id":
				this.id = record.getValue().toString();
				break;
			// TODO gestione @type come lista
			case "@type":
				if (!"Telemetry".equals(record.getValue())) {
					throw new IllegalArgumentException("@type must be Telemetry but is " + record.getValue());
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

}