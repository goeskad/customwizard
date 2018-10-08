package com.tibco.configtool.internal;

import java.io.File;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.ActionException;

public class ConfigHomeDialog extends TitleAreaDialog implements SelectionListener {
	private Text text;

	public ConfigHomeDialog() {
		super(null);
	}

	protected Control createDialogArea(final Composite parent) {
		try {
			IMessageProvider messageProvider = TCTHelper.getMessageProvider(TCTContext.getInstance().getWizardInstance());

			getShell().setText(messageProvider.getMessage("ConfigHomeDialog.shell.text"));
			this.setTitle(messageProvider.getMessage("ConfigHomeDialog.title"));
			this.setTitleImage(ConfigToolProcessor.titleImage);
			this.setMessage(messageProvider.getMessage("ConfigHomeDialog.description"));
			Composite composite = (Composite) super.createDialogArea(parent);
			Composite mainContainer = new Composite(composite, SWT.NONE);
			GridData gridData = new GridData(GridData.FILL_BOTH);
			mainContainer.setLayoutData(gridData);
			GridLayout gridLayout = new GridLayout(3, false);
			gridLayout.marginLeft = 5;
			gridLayout.marginRight = 5;
			gridLayout.marginTop = 10;
			mainContainer.setLayout(gridLayout);

			Label label = new Label(mainContainer, SWT.NONE);
			label.setText(messageProvider.getMessage("ConfigHomeDialog.browse.label"));
			text = new Text(mainContainer, SWT.BORDER);
			String configHome = TCTContext.getInstance().getTibcoConfigHome();
			if (!TCTContext.getInstance().isConfigHomeUpdated()) {
				configHome = configHome + "/data";
			}
			text.setText(new File(configHome).getAbsolutePath());
			GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
			textGridData.widthHint = 350;
			text.setLayoutData(textGridData);
			Button button = new Button(mainContainer, SWT.PUSH);
			button.setText("Browse");
			button.addSelectionListener(this);

			return composite;
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
		
		//this sentence is added to display a message for DirectoryDialog
		directoryDialog.setMessage("Please select tibco configuration folder");
		
		directoryDialog.setFilterPath(text.getText());
		String currentDir = directoryDialog.open();
		if (currentDir != null) {
			text.setText(currentDir);
		}
	}

	protected void okPressed() {
		TCTContext.getInstance().setTibcoConfigHome(text.getText());
		super.okPressed();
	}
}
