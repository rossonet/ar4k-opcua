package org.rossonet.opcua.milo.utils.dtdl;

public class DigitalTwinModelIdentifier {
	static DigitalTwinModelIdentifier fromString(final String value) {
		return new DigitalTwinModelIdentifier(value);
	}

	private final String path;

	private final String scheme;
	private final int version;

	private DigitalTwinModelIdentifier(final String value) {
		scheme = value.split(":")[0];
		final String others = value.substring(scheme.length() + 1);
		path = others.split(";")[0];
		version = Integer.valueOf(others.split(";")[1]);
	}

	public String getPath() {
		return path;
	}

	public String[] getPathSegments() {
		if (path.contains(":")) {
			return path.split(":");
		} else {
			return new String[] { path };
		}
	}

	public String getScheme() {
		return scheme;
	}

	public int getVersion() {
		return version;
	}

	public boolean isValid() {
		return (path != null && !path.isEmpty()) && (scheme != null && !scheme.isEmpty() && version > 0);

	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Model Identifier [");
		if (scheme != null) {
			builder.append("scheme=");
			builder.append(scheme);
			builder.append(", ");
		}
		if (path != null) {
			builder.append("path=");
			builder.append(path);
			builder.append(", ");
		}
		builder.append("version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

}
