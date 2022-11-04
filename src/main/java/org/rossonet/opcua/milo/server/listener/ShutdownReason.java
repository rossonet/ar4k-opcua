package org.rossonet.opcua.milo.server.listener;

public class ShutdownReason {

	public enum ReasonCategory {
		FATAL_ERROR, NORMAL_SHUTDOWN
	}

	private final ShutdownReason.ReasonCategory category;

	private final String reasonDescription;

	public ShutdownReason() {
		this(ReasonCategory.NORMAL_SHUTDOWN, "reason not compiled");
	}

	public ShutdownReason(final ShutdownReason.ReasonCategory category, final String reasonDescription) {
		this.category = category;
		this.reasonDescription = reasonDescription;
	}

	public ShutdownReason.ReasonCategory getCategory() {
		return category;
	}

	public String getReasonDescription() {
		return reasonDescription;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ShutdownReason [category=");
		builder.append(category);
		builder.append(", reasonDescription=");
		builder.append(reasonDescription);
		builder.append("]");
		return builder.toString();
	}

}