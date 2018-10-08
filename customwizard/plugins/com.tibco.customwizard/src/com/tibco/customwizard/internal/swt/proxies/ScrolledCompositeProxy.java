package com.tibco.customwizard.internal.swt.proxies;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.proxies.CompositeProxy;

public class ScrolledCompositeProxy extends CompositeProxy {
	private Composite content;

	public Control create(BuildContext context, Composite parent, int style) {
		ScrolledComposite ctrl = new ScrolledComposite(parent, style);
		
		content = new Composite(ctrl, SWT.NONE);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));

		ctrl.setContent(content);
		
		ctrl.setExpandHorizontal(true);
		ctrl.setExpandVertical(true);

		return ctrl;
	}
	
	public Composite getContainer() {
		return content;
	}

	public void setContainerLayout(Layout layout) {
		if (layout == null)
			layout = new FillLayout(SWT.VERTICAL); // use by default
		// FillLayout
		content.setLayout(layout);
	}
}
