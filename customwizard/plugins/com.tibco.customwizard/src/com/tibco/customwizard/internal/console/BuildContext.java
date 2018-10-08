package com.tibco.customwizard.internal.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Layout;
import org.nuxeo.xforms.ui.swt.ILayoutDataProxy;
import org.nuxeo.xforms.ui.swt.ILayoutProxy;
import org.nuxeo.xforms.xforms.model.controls.XFControlElement;

import com.tibco.customwizard.internal.console.proxies.ConsoleControlProxy;

public class BuildContext {
	public ConsoleUIBuilder builder;
	public ConsoleForm form;

	public XFControlElement element;
	public ConsoleControlProxy parent;
	
	public ConsoleControlProxy proxy;
	
	public Class<?> proxyClass;
	public ILayoutProxy layoutProxy;
	public ILayoutDataProxy layoutDataProxy;
	
	public int style;
	public Layout layout;
	public Object layoutData;
	
	@SuppressWarnings("unchecked")
	public List ppqueue = new ArrayList();
	
	public BuildContext(ConsoleUIBuilder builder) {
		this.builder = builder;
	}
	
	public void reset() {
		element = null;
		parent = null;
		proxy = null;
		proxyClass = null;
		layoutProxy = null;
		layoutDataProxy = null;
		style = 0;
		layout = null;
		layoutData = null;
		ppqueue.clear();
	}
}
