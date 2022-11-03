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

	private static void elaborateContents(InterfaceObject templateObject, List<Map<String, Object>> contents) {
		for (final Map<String, Object> singleContent : contents) {
			switch (singleContent.get("@type").toString()) {
			case "Telemetry":
				templateObject.addTelemetry(singleContent);
				break;
			case "Properties":
				templateObject.addProperties(singleContent);
				break;
			case "Commands":
				templateObject.addCommands(singleContent);
				break;
			case "Relationships":
				templateObject.addRelationships(singleContent);
				break;
			case "Components":
				templateObject.addComponents(singleContent);
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static InterfaceObject newFromDtdlV2(String dtdlV2String) throws JsonParseException, IOException {
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
					break;
				case "@id":
					templateObject.setId(v.getValue().toString());
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

	private String comment;

	private String description;
	private String displayName;

	private String id;

	private final List<CommandObject> commands = new ArrayList<>();

	private final List<ComponentObject> components = new ArrayList<>();

	private final List<PropertyObject> properties = new ArrayList<>();

	private final List<RelationshipObject> relationships = new ArrayList<>();

	private final List<TelemetryObject> telemetries = new ArrayList<>();

	private InterfaceObject() {

	}

	InterfaceObject addCommands(Map<String, Object> command) {
		commands.add(new CommandObject(command));
		return this;
	}

	InterfaceObject addComponents(Map<String, Object> component) {
		components.add(new ComponentObject(component));
		return this;
	}

	InterfaceObject addProperties(Map<String, Object> property) {
		properties.add(new PropertyObject(property));
		return this;
	}

	InterfaceObject addRelationships(Map<String, Object> relationship) {
		relationships.add(new RelationshipObject(relationship));
		return this;
	}

	InterfaceObject addTelemetry(Map<String, Object> telemetry) {
		telemetries.add(new TelemetryObject(telemetry));
		return this;
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

	public String getId() {
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

	InterfaceObject setComment(String comment) {
		this.comment = comment;
		return this;
	}

	@SuppressWarnings("unchecked")
	InterfaceObject setContents(Object contents) {
		if (contents instanceof List) {
			elaborateContents(this, (List<Map<String, Object>>) contents);
		} else {
			throw new IllegalArgumentException("contents must be a List but is " + contents.getClass().getName());
		}
		return this;
	}

	InterfaceObject setDescription(String description) {
		this.description = description;
		return this;
	}

	InterfaceObject setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	InterfaceObject setExtends(Object extendsData) {
		logger.info("extends ->" + extendsData.getClass().getName());
		// TODO completare interprete Extends
		return this;
	}

	InterfaceObject setId(String id) {
		this.id = id;
		return this;
	}

	InterfaceObject setSchemas(Object schemas) {
		logger.info("schemas ->" + schemas.getClass().getName());
		// TODO completare interprete Schemas
		return this;
	}

}