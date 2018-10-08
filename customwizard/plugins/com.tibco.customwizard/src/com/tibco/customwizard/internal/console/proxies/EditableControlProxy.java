package com.tibco.customwizard.internal.console.proxies;

import org.nuxeo.xforms.xforms.events.XFormsValueChangedEvent;

import com.tibco.customwizard.internal.xforms.validator.ValidateManager;
import com.tibco.customwizard.support.SystemConsoleProxy;
import com.tibco.customwizard.validator.ValidateResult;
import com.tibco.customwizard.xforms.events.ModifyEvent;

public class EditableControlProxy extends ConsoleControlProxy implements ListableControlProxy {
	private static final String NULL = "[null]";
	private static final String SKIP = "[skip]";
	
	protected boolean editable = true;
	
	protected String value;

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isEditable() {
		return editable;
	}

	public String getXMLValue() {
		return value;
	}

	public void setXMLValue(String value) {
		this.value = value;
		if (requiresValidation()) {
			fireXFormsEvent(new XFormsValueChangedEvent(this));
		}
	}

	public void setLabel(String label) {
		this.label = label;
		if (label.endsWith(":")) {
			this.label = label.substring(0, label.length() - 1);
		}
	}

	public void presentSummay() {
		if (enabled) {
			printLable();
			printDefaultValue();
		}
	}
	
	public void present() {
		if (enabled) {
//			printSplit();
			printTitle();
			printToolTip();
			
			printLable();
			printDefaultValue();
			
			if (isEditable()) {
				String input = readInput();
				if (!input.equals(SKIP)) {
					// if input.length() == 0, means use the default value
					if (input.length() > 0) {
						if (input.equals(NULL)) {
							input = "";
						}
						processInput(input);
					} else {
						processDefaultInput();
					}

					if (!validateInput(input)) {
						present();
					}
				}
			} else {
				printNotEditableMessage();
			}
		}
	}

	protected void processDefaultInput() {
		// do nothing
	}

	protected void printSplit() {
		SystemConsoleProxy.print("\n\n-------------------------------------------------------------------------------");
	}

	protected void printTitle() {
		SystemConsoleProxy.println("\nSpecify " + label + "\n");
	}

	protected void printLable() {
		SystemConsoleProxy.print(label + ": ");
	}

	protected void printToolTip() {
		if (toolTip != null) {
			SystemConsoleProxy.println("Tool tip: " + toolTip + "\n");
		}
	}

	protected void printDefaultValue() {
		if (value != null && value.length() > 0) {
			SystemConsoleProxy.print("[" + value + "]");
		}
	}

	protected void printNotEditableMessage() {
		SystemConsoleProxy.print("\n\n" + label + " is not editable.");
	}
	
	protected String readInput() {
		return SystemConsoleProxy.readLine();
	}
	
	protected boolean validateInput(String input) {
		if (requiresValidation()) {
			fireXFormsEvent(new XFormsValueChangedEvent(this));
			fireXFormsEvent(new ModifyEvent(this, null));
		}
		
		ValidateResult validateResult = getValidateResult();
		if (validateResult == null || validateResult.isValid()) {
			return true;
		} else {
			SystemConsoleProxy.println("\nInvalid: " + validateResult.getErrorMessage());
			return false;
		}
	}

	protected ValidateResult getValidateResult() {
        return ValidateManager.getValidateResult(this);
    }
	
	protected void processInput(String input) {
		value = input;
	}
}
