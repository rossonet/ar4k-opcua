package org.rossonet.opcua.milo.dtdl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.rossonet.opcua.milo.utils.dtdl.InterfaceObject;
import org.rossonet.opcua.milo.utils.dtdl.Unit;
import org.rossonet.opcua.milo.utils.dtdl.UnitType;

import com.fasterxml.jackson.core.JsonParseException;

public class DtdlParserTests {

	private static final String EXAMPLE_INTERFACE_1 = "{\n" + "    \"@id\": \"dtmi:com:example:Thermostat;1\",\n"
			+ "    \"@type\": \"Interface\",\n" + "    \"displayName\": \"Thermostat\",\n" + "    \"contents\": [\n"
			+ "        {\n" + "            \"@type\": \"Telemetry\",\n" + "            \"name\": \"temp\",\n"
			+ "            \"schema\": \"double\"\n" + "        },\n" + "        {\n"
			+ "            \"@type\": \"Property\",\n" + "            \"name\": \"setPointTemp\",\n"
			+ "            \"writable\": true,\n" + "            \"schema\": \"double\"\n" + "        }\n" + "    ],\n"
			+ "    \"@context\": \"dtmi:dtdl:context;2\"\n" + "}";

	private static final String EXAMPLE_INTERFACE_2 = "{\n" + "    \"@id\": \"dtmi:Phone;2\",\n"
			+ "    \"@type\": \"Interface\",\n" + "    \"displayName\": \"Phone\",\n" + "    \"contents\": [\n"
			+ "        {\n" + "            \"@type\": \"Component\",\n" + "            \"name\": \"frontCamera\",\n"
			+ "            \"schema\": \"dtmi:com:example:Camera;3\"\n" + "        },\n" + "        {\n"
			+ "            \"@type\": \"Component\",\n" + "            \"name\": \"backCamera\",\n"
			+ "            \"schema\": \"dtmi:com:example:Camera;3\"\n" + "        },\n" + "        {\n"
			+ "            \"@type\": \"Component\",\n" + "            \"name\": \"deviceInfo\",\n"
			+ "            \"schema\": \"dtmi:azure:deviceManagement:DeviceInformation;2\"\n" + "        }\n"
			+ "    ],\n" + "    \"@context\": \"dtmi:dtdl:context;2\"\n" + "}";

	private static final String EXAMPLE_INTERFACE_MULTI_PROPERTIES = "{\n"
			+ "    \"@id\": \"dtmi:com:example:Thermostat;1\",\n" + "    \"@type\": \"Interface\",\n"
			+ "    \"displayName\": \"Thermostat\",\n" + "    \"contents\": [\n" + "        {\n"
			+ "            \"@type\": \"Telemetry\",\n" + "            \"name\": \"temp\",\n"
			+ "            \"schema\": \"double\"\n" + "        },\n" + "        {\n"
			+ "            \"@type\": [\"Property\",\"Temperature\"],\n" + "            \"name\": \"setPointTemp\",\n"
			+ "            \"writable\": true,\n" + "            \"schema\": \"double\"\n" + "        }\n" + "    ],\n"
			+ "    \"@context\": \"dtmi:dtdl:context;2\"\n" + "}";

	private static final String EXAMPLE_INTERFACE_MULTI_TELEMETRIES = "{\n"
			+ "    \"@id\": \"dtmi:more:example:LuxMeter;2\",\n" + "    \"@type\": \"Interface\",\n"
			+ "    \"displayName\": \"Lux Meter Type 2\",\n" + "    \"contents\": [\n" + "        {\n"
			+ "            \"@type\": [\"Telemetry\",\"Luminosity\"],\n" + "            \"name\": \"lumen\",\n"
			+ "            \"schema\": \"double\"\n" + "        },\n" + "        {\n"
			+ "            \"@type\": \"Property\",\n" + "            \"name\": \"setPointLumen\",\n"
			+ "            \"writable\": true,\n" + "            \"schema\": \"double\"\n" + "        }\n" + "    ],\n"
			+ "    \"@context\": \"dtmi:dtdl:context;2\"\n" + "}";

	@Test
	public void interfacesExamples1() throws JsonParseException, IOException {

		final InterfaceObject generatedObject = InterfaceObject.newFromDtdlV2(EXAMPLE_INTERFACE_1);
		System.out.println(generatedObject);
		assertEquals("dtmi", generatedObject.getId().getScheme());
		assertEquals("com:example:Thermostat", generatedObject.getId().getPath());
		assertArrayEquals(new String[] { "com", "example", "Thermostat" }, generatedObject.getId().getPathSegments());
		assertEquals(1, generatedObject.getId().getVersion());
		assertEquals("Thermostat", generatedObject.getDisplayName());
		assertEquals(1, generatedObject.getProperties().size());
		assertEquals(1, generatedObject.getTelemetries().size());
		assertEquals("setPointTemp", generatedObject.getProperties().get(0).getName());
		assertEquals("temp", generatedObject.getTelemetries().get(0).getName());
		assertEquals(true, generatedObject.getProperties().get(0).isWritable());
	}

	@Test
	public void interfacesExamples2() throws JsonParseException, IOException {

		final InterfaceObject generatedObject = InterfaceObject.newFromDtdlV2(EXAMPLE_INTERFACE_2);
		System.out.println(generatedObject);
		assertEquals("dtmi", generatedObject.getId().getScheme());
		assertEquals("Phone", generatedObject.getId().getPath());
		assertArrayEquals(new String[] { "Phone" }, generatedObject.getId().getPathSegments());
		assertEquals(2, generatedObject.getId().getVersion());
		assertEquals("Phone", generatedObject.getDisplayName());
		assertEquals(3, generatedObject.getComponents().size());

	}

	@Test
	public void interfacesExamplesMultiProperties() throws JsonParseException, IOException {

		final InterfaceObject generatedObject = InterfaceObject.newFromDtdlV2(EXAMPLE_INTERFACE_MULTI_PROPERTIES);
		System.out.println(generatedObject);
		assertEquals("dtmi", generatedObject.getId().getScheme());
		assertEquals("com:example:Thermostat", generatedObject.getId().getPath());
		assertArrayEquals(new String[] { "com", "example", "Thermostat" }, generatedObject.getId().getPathSegments());
		assertEquals(1, generatedObject.getId().getVersion());
		assertEquals("Thermostat", generatedObject.getDisplayName());
		assertEquals(1, generatedObject.getProperties().size());
		assertEquals(2, generatedObject.getProperties().get(0).getTypes().size());
		assertEquals(1, generatedObject.getTelemetries().size());
		assertEquals("setPointTemp", generatedObject.getProperties().get(0).getName());
		assertEquals("temp", generatedObject.getTelemetries().get(0).getName());
		assertEquals(true, generatedObject.getProperties().get(0).isWritable());
	}

	@Test
	public void interfacesMultiTelemetries() throws JsonParseException, IOException {

		final InterfaceObject generatedObject = InterfaceObject.newFromDtdlV2(EXAMPLE_INTERFACE_MULTI_TELEMETRIES);
		System.out.println(generatedObject);
		assertEquals("dtmi", generatedObject.getId().getScheme());
		assertEquals("more:example:LuxMeter", generatedObject.getId().getPath());
		assertArrayEquals(new String[] { "more", "example", "LuxMeter" }, generatedObject.getId().getPathSegments());
		assertEquals(2, generatedObject.getId().getVersion());
		assertEquals("Lux Meter Type 2", generatedObject.getDisplayName());
		assertEquals(1, generatedObject.getProperties().size());
		assertEquals(1, generatedObject.getTelemetries().size());
		assertEquals("setPointLumen", generatedObject.getProperties().get(0).getName());
		assertEquals("lumen", generatedObject.getTelemetries().get(0).getName());
		assertEquals(true, generatedObject.getProperties().get(0).isWritable());
		assertEquals(2, generatedObject.getTelemetries().get(0).getTypes().size());
	}

	@Test
	public void unitEnumeratorTest() {
		final Unit u = Unit.getUnit("hour");
		System.out.println("name -> " + u.name());
		assertEquals("hour", u.name());
		System.out.println("unit types -> " + u.getUnitTypes());
		assertEquals("[TimeUnit]", u.getUnitTypes().toString());
		for (final UnitType d : u.getUnitTypes()) {
			System.out.println("semantic type -> " + d.getSemanticType());
			assertEquals("[TimeSpan]", d.getSemanticType().toString());
		}

		final Unit u2 = Unit.getUnit("radian");
		System.out.println("name -> " + u2.name());
		assertEquals("radian", u2.name());
		System.out.println("unit types -> " + u2.getUnitTypes());
		assertEquals("[AngleUnit]", u2.getUnitTypes().toString());
		for (final UnitType d2 : u2.getUnitTypes()) {
			System.out.println("semantic type -> " + d2.getSemanticType());
			assertTrue(d2.getSemanticType().toString().contains("Latitude"));
			assertTrue(d2.getSemanticType().toString().contains("Longitude"));
			assertTrue(d2.getSemanticType().toString().contains("Angle"));
		}

		final Unit u3 = Unit.getUnit("byte");
		System.out.println("name -> " + u3.name());
		assertEquals("_byte", u3.name());
		System.out.println("unit types -> " + u3.getUnitTypes());
		assertEquals("[DataSizeUnit]", u3.getUnitTypes().toString());
		for (final UnitType d3 : u3.getUnitTypes()) {
			System.out.println("semantic type -> " + d3.getSemanticType());
			assertEquals("[DataSize]", d3.getSemanticType().toString());
		}
	}

}
