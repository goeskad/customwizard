package com.tibco.customwizard.internal.console.proxies;

import java.util.ArrayList;
import java.util.List;

public class GroupProxy extends ConsoleControlProxy {
	private List<ConsoleControlProxy> controlList = new ArrayList<ConsoleControlProxy>();

	public void present() {
		//do nothing
	}

	public void addChildControl(ConsoleControlProxy control) {
		control.setParent(this);
		controlList.add(control);
	}

	public List<ConsoleControlProxy> getControlList() {
		return controlList;
	}
}
