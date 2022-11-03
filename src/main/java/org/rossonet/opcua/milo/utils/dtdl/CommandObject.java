package org.rossonet.opcua.milo.utils.dtdl;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandObject {

	private static final Logger logger = LoggerFactory.getLogger(CommandObject.class);

	private String commandType;

	private String comment;
	private String description;
	private String displayName;
	private DigitalTwinModelIdentifier id;
	private String name;
	private CommandPayload request;
	private CommandPayload response;

	@SuppressWarnings("unchecked")
	CommandObject(final Map<String, Object> command) {
		for (final Entry<String, Object> record : command.entrySet()) {
			switch (record.getKey()) {
			case "@id":
				this.id = DigitalTwinModelIdentifier.fromString(record.getValue().toString());
				break;
			case "name":
				this.name = record.getValue().toString();
				break;
			case "@type":
				if (!"Command".equals(record.getValue())) {
					throw new IllegalArgumentException("@type must be Command but is " + record.getValue());
				}
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
			case "commandType":
				this.commandType = record.getValue().toString();
				logger.warn("in Command the field commandType is deprecated");
				break;
			case "request":
				this.request = new CommandPayload((Map<String, Object>) record.getValue());
				break;
			case "response":
				this.response = new CommandPayload((Map<String, Object>) record.getValue());
				break;
			}
		}

	}

	public String getCommandType() {
		return commandType;
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

	public CommandPayload getRequest() {
		return request;
	}

	public CommandPayload getResponse() {
		return response;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Command [");
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
		if (commandType != null) {
			builder.append("commandType=");
			builder.append(commandType);
			builder.append(", ");
		}
		if (request != null) {
			builder.append("request=");
			builder.append(request);
			builder.append(", ");
		}
		if (response != null) {
			builder.append("response=");
			builder.append(response);
		}
		builder.append("]");
		return builder.toString();
	}
}