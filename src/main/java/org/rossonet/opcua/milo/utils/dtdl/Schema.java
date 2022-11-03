package org.rossonet.opcua.milo.utils.dtdl;

import org.rossonet.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Schema {

	public enum DigitalTwinPrimitive {
		_boolean, _date, _dateTime, _double, _duration, _float, _integer, _long, _string, _time
	}

	private static final Logger logger = LoggerFactory.getLogger(Schema.class);
	private DigitalTwinModelIdentifier digitalTwinModelIdentifier = null;
	private DigitalTwinPrimitive digitalTwinPrimitive = null;

	public Schema(final Object value) {
		if (value instanceof String) {
			if (!(value.toString().contains(":") || value.toString().contains(";"))) {
				try {
					this.digitalTwinPrimitive = DigitalTwinPrimitive.valueOf("_" + value);
				} catch (final IllegalArgumentException a) {
					logger.error("primitive type " + value + " not know\n" + LogHelper.stackTraceToString(a, 4));
				}
			}
			if (this.digitalTwinPrimitive == null) {
				try {
					this.digitalTwinModelIdentifier = DigitalTwinModelIdentifier.fromString(value.toString());
				} catch (final Exception e) {
					logger.error("model identifier type " + value + " not know\n" + LogHelper.stackTraceToString(e, 4));
				}
			}
		} else {
			// TODO elaborare schemi complessi
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Schema [");
		if (digitalTwinModelIdentifier != null) {
			builder.append("digitalTwinModelIdentifier=");
			builder.append(digitalTwinModelIdentifier);
			builder.append(", ");
		}
		if (digitalTwinPrimitive != null) {
			builder.append("digitalTwinPrimitive=");
			builder.append(digitalTwinPrimitive.toString().substring(1));
		}
		builder.append("]");
		return builder.toString();
	}

}