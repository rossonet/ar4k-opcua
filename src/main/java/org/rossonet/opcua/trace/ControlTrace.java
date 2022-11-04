package org.rossonet.opcua.trace;

public class ControlTrace {

	private final static ControlTrace INSTANCE = new ControlTrace();

	public static ControlTrace getInstance() {
		return INSTANCE;
	}

	private ControlTrace() {
		// singleton
	}

	public void registerGenerateTypeObjectFromDtdl(String dtdlV2String) {
		// TODO Auto-generated method stub

	}

	public void registerShutdownAction(String reason) {
		// TODO Auto-generated method stub

	}

}
