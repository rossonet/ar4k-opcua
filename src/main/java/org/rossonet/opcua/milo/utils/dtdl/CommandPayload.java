package org.rossonet.opcua.milo.utils.dtdl;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandPayload {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CommandPayload.class);

	private String id;
	private String name;
	private String comment;
	private String description;
	private String displayName;

	private Schema schema;

	CommandPayload(Map<String, Object> commandPayload) {
		for (final Entry<String, Object> record : commandPayload.entrySet()) {
			switch (record.getKey()) {
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
}