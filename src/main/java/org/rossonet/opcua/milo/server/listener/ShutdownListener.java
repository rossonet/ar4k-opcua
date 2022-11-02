package org.rossonet.opcua.milo.server.listener;

public interface ShutdownListener {

	public class ShutdownReason {

		public enum ReasonCategory {
			FATAL_ERROR, NORMAL_SHUTDOWN
		}

		private final ReasonCategory category;

		private final String reasonDescription;

		public ShutdownReason() {
			this(ReasonCategory.NORMAL_SHUTDOWN, "reason not compiled");
		}

		public ShutdownReason(final ReasonCategory category, final String reasonDescription) {
			this.category = category;
			this.reasonDescription = reasonDescription;
		}

		public ReasonCategory getCategory() {
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

	public void shutdown(ShutdownReason reason);

}
