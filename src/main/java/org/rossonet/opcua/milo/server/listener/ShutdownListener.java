package org.rossonet.opcua.milo.server.listener;

public interface ShutdownListener {

	public class ShutdownReason {

		public enum ReasonCategory {
			NORMAL_SHUTDOWN, FATAL_ERROR
		}

		private final ReasonCategory category;

		private final String reasonDescription;

		public ShutdownReason() {
			this(ReasonCategory.NORMAL_SHUTDOWN, "reason not compiled");
		}

		public ShutdownReason(ReasonCategory category, String reasonDescription) {
			this.category = category;
			this.reasonDescription = reasonDescription;
		}

		public ReasonCategory getCategory() {
			return category;
		}

		public String getReasonDescription() {
			return reasonDescription;
		}

	}

	public void shutdown(ShutdownReason reason);

}
