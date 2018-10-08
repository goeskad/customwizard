package com.tibco.customwizard.internal.console.proxies;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.xforms.xforms.events.XFormsValueChangedEvent;
import org.nuxeo.xforms.xforms.model.controls.XFItem;
import org.nuxeo.xforms.xforms.model.controls.XFLabel;

import com.tibco.customwizard.support.SystemConsoleProxy;

public class ComboProxy extends EditableControlProxy {
	private List<XFItem> items = new ArrayList<XFItem>();

	private int selectedIndex = 0;

	public String getXMLValue() {
		if (!items.isEmpty()) {
			value = items.get(selectedIndex).getValue();
		}
		return value;
	}

	public void setXMLValue(String value) {
		if (!items.isEmpty()) {
			for (int i = 0; i < items.size(); i++) {
				if (value.equals(items.get(i).getValue())) {
					selectedIndex = i;
					this.value = value;
					
					if (requiresValidation()) {
						fireXFormsEvent(new XFormsValueChangedEvent(this));
					}
					return;
				}
			}
		}
	}

	public void addItem(String value) {
		addItem(value, value);
	}

	public void addItem(String value, String label) {
		XFItem item = new XFItem(form.getForm());
		item.setValue(value);
		XFLabel xLabel = new XFLabel();
		xLabel.setText(label);
		item.setLabel(xLabel);
		items.add(item);
	}

	public void removeAllItems() {
		items.clear();
	}
	
	public void setItems(List<XFItem> items) {
		this.items = items;
	}

	public int getItemCount() {
		return items.size();
	}
	
	public void presentSummay() {
		if (enabled) {
			SystemConsoleProxy.print(label + ": ");
			String value = "";
			if (!items.isEmpty()) {
				XFItem item = items.get(selectedIndex);
				value= item.getLabel() == null ? item.getValue() : item.getLabel().getText();
			}
			SystemConsoleProxy.print("[" + value + "]");
		}
	}
	
	protected void printTitle() {
		SystemConsoleProxy.println("\nChoose " + label + " from the list below.\n");
	}

	protected void printLable() {
		if (!items.isEmpty()) {
			for (int i = 0; i < items.size(); i++) {
				String selected = selectedIndex == i ? "X" : " ";
				XFItem item = items.get(i);
				String label = item.getLabel() != null ? item.getLabel().getText() : item.getValue();
				SystemConsoleProxy.println("[" + selected + "] " + (i + 1) + " - " + label);
			}
		}
	}
	
	protected void printDefaultValue() {
		SystemConsoleProxy.print("\nTo select an item enter its number: [" + (selectedIndex + 1) + "]");
	}

	protected boolean requiresValidation(String input) {
		return !String.valueOf(selectedIndex + 1).equals(input);
	}

	protected boolean validateInput(String input) {
		if (input.length() > 0) {
			String wrongChooseMsg = "\nPlease enter a value between 1 and "
					+ items.size() + "\n";
			try {
				int intInput = Integer.parseInt(input);
				if (intInput < 1 || intInput > items.size()) {
					SystemConsoleProxy.println(wrongChooseMsg);
					return false;
				}
			} catch (NumberFormatException e) {
				SystemConsoleProxy.println(wrongChooseMsg);
				return false;
			}
		}
		return super.validateInput(input);
	}
	
	protected void processInput(String input) {
		try {
			int intInput = Integer.parseInt(input);
			if (intInput > 0 && intInput <= items.size()) {
				selectedIndex = intInput - 1;
				getXMLValue();
			}
		} catch (NumberFormatException e) {
		}
	}
}
