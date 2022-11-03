package org.rossonet.opcua.milo.utils.dtdl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.utils.JsonUtils;

public class InterfaceObject {

	private static final Logger logger = LoggerFactory.getLogger(InterfaceObject.class);

	@SuppressWarnings("unchecked")
	public static InterfaceObject newFromDtdlV2(final String dtdlV2String) throws JsonParseException, IOException {
		final InterfaceObject templateObject = new InterfaceObject();
		final Object data = JsonUtils.fromString(dtdlV2String);
		if (data instanceof Map) {
			for (final Entry<String, Object> v : ((Map<String, Object>) data).entrySet()) {
				switch (v.getKey()) {
				case "@context":
					if (!"dtmi:dtdl:context;2".equals(v.getValue())) {
						throw new IllegalArgumentException(
								"@context must be dtmi:dtdl:context;2 but is " + v.getValue());
					}
					break;
				case "@type":
					if (!"Interface".equals(v.getValue())) {
						throw new IllegalArgumentException("@type must be Interface but is " + v.getValue());
					}
					templateObject.setType(v.getValue());
					break;
				case "@id":
					templateObject.setId(DigitalTwinModelIdentifier.fromString(v.getValue().toString()));
					break;
				case "displayName":
					templateObject.setDisplayName(v.getValue().toString());
					break;
				case "comment":
					templateObject.setComment(v.getValue().toString());
					break;
				case "description":
					templateObject.setDescription(v.getValue().toString());
					break;
				case "schemas":
					templateObject.setSchemas(v.getValue());
					break;
				case "extends":
					templateObject.setExtends(v.getValue());
					break;
				case "contents":
					templateObject.setContents(v.getValue());
					break;
				}
			}
		} else {
			throw new IllegalArgumentException("dtdl must resolve to Map, but is " + data.getClass());
		}
		return templateObject;
	}

	private static void elaborateContents(final InterfaceObject templateObject,
			final List<Map<String, Object>> contents) {
		for (final Map<String, Object> singleContent : contents) {

			if (singleContent.get("@type") instanceof String) {
				switch (singleContent.get("@type").toString()) {
				case "Telemetry":
					templateObject.addTelemetry(singleContent);
					break;
				case "Property":
					templateObject.addProperty(singleContent);
					break;
				case "Command":
					templateObject.addCommand(singleContent);
					break;
				case "Relationship":
					templateObject.addRelationship(singleContent);
					break;
				case "Component":
					templateObject.addComponent(singleContent);
					break;
				default:
					logger.error("type " + singleContent.get("@type").toString() + " is not know");
					break;
				}
			} else if (singleContent.get("@type") instanceof List) {
				@SuppressWarnings("unchecked")
				final List<String> typesList = (List<String>) singleContent.get("@type");
				if (typesList.contains("Telemetry")) {
					templateObject.addTelemetry(singleContent);
				} else if (typesList.contains("Property")) {
					templateObject.addProperty(singleContent);
				} else {
					logger.error("types " + typesList.toArray(new String[0]) + " are not know");
				}
			}
		}
	}

	private final List<CommandObject> commands = new ArrayList<>();

	private String comment;

	private final List<ComponentObject> components = new ArrayList<>();

	private String description;

	private String displayName;

	private DigitalTwinModelIdentifier id;
	private final List<PropertyObject> properties = new ArrayList<>();

	private final List<RelationshipObject> relationships = new ArrayList<>();

	private final List<TelemetryObject> telemetries = new ArrayList<>();

	private String type;

	private InterfaceObject() {

	}

	public List<CommandObject> getCommands() {
		return commands;
	}

	public String getComment() {
		return comment;
	}

	public List<ComponentObject> getComponents() {
		return components;
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

	public List<PropertyObject> getProperties() {
		return properties;
	}

	public List<RelationshipObject> getRelationships() {
		return relationships;
	}

	public List<TelemetryObject> getTelemetries() {
		return telemetries;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Interface [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (type != null) {
			builder.append("type=");
			builder.append(type);
			builder.append(", ");
		}
		if (comment != null) {
			builder.append("comment=");
			builder.append(comment);
			builder.append(", ");
		}
		if (telemetries != null) {
			builder.append("telemetries=");
			builder.append(telemetries);
			builder.append(", ");
		}
		if (properties != null) {
			builder.append("properties=");
			builder.append(properties);
			builder.append(", ");
		}
		if (commands != null) {
			builder.append("commands=");
			builder.append(commands);
			builder.append(", ");
		}
		if (relationships != null) {
			builder.append("relationships=");
			builder.append(relationships);
			builder.append(", ");
		}
		if (components != null) {
			builder.append("components=");
			builder.append(components);
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

	InterfaceObject addCommand(final Map<String, Object> command) {
		commands.add(new CommandObject(command));
		return this;
	}

	InterfaceObject addComponent(final Map<String, Object> component) {
		components.add(new ComponentObject(component));
		return this;
	}

	InterfaceObject addProperty(final Map<String, Object> property) {
		properties.add(new PropertyObject(property));
		return this;
	}

	InterfaceObject addRelationship(final Map<String, Object> relationship) {
		relationships.add(new RelationshipObject(relationship));
		return this;
	}

	InterfaceObject addTelemetry(final Map<String, Object> telemetry) {
		telemetries.add(new TelemetryObject(telemetry));
		return this;
	}

	InterfaceObject setComment(final String comment) {
		this.comment = comment;
		return this;
	}

	@SuppressWarnings("unchecked")
	InterfaceObject setContents(final Object contents) {
		if (contents instanceof List) {
			elaborateContents(this, (List<Map<String, Object>>) contents);
		} else {
			throw new IllegalArgumentException("contents must be a List but is " + contents.getClass().getName());
		}
		return this;
	}

	InterfaceObject setDescription(final String description) {
		this.description = description;
		return this;
	}

	InterfaceObject setDisplayName(final String displayName) {
		this.displayName = displayName;
		return this;
	}

	InterfaceObject setExtends(final Object extendsData) {
		logger.info("extends ->" + extendsData.getClass().getName());
		// TODO completare interprete Extends
		return this;
	}

	InterfaceObject setId(final DigitalTwinModelIdentifier id) {
		this.id = id;
		return this;
	}

	InterfaceObject setSchemas(final Object schemas) {
		logger.info("schemas ->" + schemas.getClass().getName());
		// TODO completare interprete Schemas
		return this;
	}

	void setType(final Object type) {
		this.type = type.toString();

	}

}