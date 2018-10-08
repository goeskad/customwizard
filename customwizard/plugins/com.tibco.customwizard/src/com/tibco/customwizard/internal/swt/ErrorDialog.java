package com.tibco.customwizard.internal.swt;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ErrorDialog extends org.eclipse.jface.dialogs.ErrorDialog {
	private IStatus status;
	
	private Button detailsButton;
	private Text detailsPanel;

	public ErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status, int displayMask) {
		super(parentShell, dialogTitle, message, status, displayMask);
		this.status = status;
	}
	
	public static int openError(Shell parentShell, String errorMessage, IStatus status) {
		ErrorDialog dialog = new ErrorDialog(parentShell, "Error", errorMessage, status, status.getSeverity());
		return dialog.open();
	}
	
	protected void buttonPressed(int id) {
		if (id == IDialogConstants.DETAILS_ID) {
			// was the details button pressed?
			toggleDetailsArea();
		} else {
			super.buttonPressed(id);
		}
	}

	protected void createDetailsButton(Composite parent) {
		if (shouldShowDetailsButton()) {
			detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
		}
	}

	private void toggleDetailsArea() {
		Point windowSize = getShell().getSize();
		Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (detailsPanel != null) {
			detailsPanel.dispose();
			detailsPanel = null;
			detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
		} else {
			detailsPanel = createDetailsPanel((Composite) getContents());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			pw.println(status.getMessage());
			if (status.getException() != null) {
				status.getException().printStackTrace(pw);
			}
			detailsPanel.setText(sw.toString());
			detailsPanel.append("");
			detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
			getContents().getShell().layout();
		}
		Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
	}

	protected Text createDetailsPanel(Composite parent) {
		// create the list
		detailsPanel = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		// fill the list
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);
		data.heightHint = 200;
		detailsPanel.setLayoutData(data);
		detailsPanel.setFont(parent.getFont());
		return detailsPanel;
	}
}
