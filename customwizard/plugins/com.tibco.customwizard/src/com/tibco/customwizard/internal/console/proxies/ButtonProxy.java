package com.tibco.customwizard.internal.console.proxies;

import org.nuxeo.xforms.xforms.events.DOMActivateEvent;

import com.tibco.customwizard.support.SystemConsoleProxy;

public class ButtonProxy extends ConsoleControlProxy implements ListableControlProxy {
	public void presentSummay() {
		if (enabled) {
			SystemConsoleProxy.print("Action \"" + label + "\"");
		}
	}
	
	public void present() {
		if (enabled) {
//			printSplit();
			printTitle();
			printToolTip();
			printLable();
			readInput();
		}
	}

	protected void printSplit() {
		SystemConsoleProxy.print("\n\n-------------------------------------------------------------------------------");
	}
	
	protected void printTitle() {
		SystemConsoleProxy.println("\nAction \"" + label + "\"\n");
	}

	protected void printToolTip() {
		if (toolTip != null) {
			SystemConsoleProxy.println("Tool tip: " + toolTip + "\n");
		}
	}

	protected void printLable() {
		SystemConsoleProxy.print("Do you want to execute this action?: [no]");
	}

	protected void readInput() {
		String input = SystemConsoleProxy.readLine();
		// if input.length() == 0, means use the default value
		if (input.length() > 0) {
			processInput(input);
		}
	}

	protected void processInput(String input) {
		if (input.equalsIgnoreCase("yes")) {
			fireXFormsEvent(new DOMActivateEvent(this, null));
		}
	}

	protected void executeAction() {
	}
}
