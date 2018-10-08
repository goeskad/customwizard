package com.tibco.configtool.internal.support;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.tibco.configtool.support.TCTContext;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.SystemConsoleProxy;

public class ActionLogger extends PrintStream {
	private static final String DETAILS_PANEL = "tct.detailspanel";

	private static final String ACTION_LOG = "tct.actionlog";
	
	private WizardInstance wizardInstance;

	public ActionLogger(WizardInstance wizardInstance, File file) throws Exception {
		super(file, "UTF-8");
		this.wizardInstance = wizardInstance;
	}

	public static Object getDetailsPanel(WizardInstance wizardInstance) {
		return wizardInstance.getAttribute(DETAILS_PANEL);
	}
	
	public static void setDetailsPanel(WizardInstance wizardInstance, Object detailsPanel) {
		wizardInstance.setAttribute(DETAILS_PANEL, detailsPanel);
	}
	
	public static StringBuffer getActionLog(WizardInstance wizardInstance) {
		return (StringBuffer)wizardInstance.getAttribute(ACTION_LOG);
	}

	public static void setActionLog(WizardInstance wizardInstance, StringBuffer actionLog) {
		wizardInstance.setAttribute(ACTION_LOG, actionLog);
	}
	
	public void write(byte buf[], int off, int len) {
		super.write(buf, off, len);
		if (TCTContext.getInstance().isConsoleMode() || TCTContext.getInstance().isSilentMode()) {
			SystemConsoleProxy.print(new String(buf,off,len));
		} else {
			StringBuffer content = (StringBuffer) wizardInstance.getAttribute(ACTION_LOG);
			content.append(new String(buf, off, len));
			setDetails();
		}
	}
	
	private void setDetails() {
		Object detailsPanel = getDetailsPanel(wizardInstance);
		if (detailsPanel != null) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Text detailsPanel = (Text) getDetailsPanel(wizardInstance);
					if (detailsPanel != null) {
						StringBuffer content = getActionLog(wizardInstance);
						String text = detailsPanel.getText();
						int start = text == null ? 0 : text.length();
						if (start < content.length()) {
							detailsPanel.append(content.substring(start));
						}
					}
				}
			});
		}
	}
}
