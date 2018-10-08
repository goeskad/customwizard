package com.tibco.configtool.internal;

import java.io.File;
import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.tibco.configtool.actions.SaveDataModelAction;
import com.tibco.configtool.internal.support.ActionLogger;
import com.tibco.configtool.support.HelpContext;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;

public class ConfigToolWizardDialog extends WizardDialog {
	private static final int SAVE_ID = 18;
	private static final int LOAD_ID = 19;
	private static final int DETAILS_ID = 20;
	
	private WizardInstance wizardInstance;
	
	private DirectoryDialog loadSessionDialog;
	
	private Composite workArea;
	
	private Text detailsPanel;
	
	public ConfigToolWizardDialog(Shell parentShell, IWizard newWizard, WizardInstance wizardInstance) {
		super(parentShell, newWizard);
		setShellStyle(getShellStyle() | SWT.DIALOG_TRIM);
		this.wizardInstance = wizardInstance;
		setHelpAvailable(true);
	}

	public void setTitleImage(Image newTitleImage) {
		super.setTitleImage(ConfigToolProcessor.titleImage);
	}
	
	public void create() {
		super.create();

		final Shell shell = getShell();

		WizardConfig wizardConfig = wizardInstance.getWizardConfig();
		if (wizardConfig.getWidth() != 0 && wizardConfig.getHeight() != 0) {
			shell.setMinimumSize(wizardConfig.getWidth(), wizardConfig.getHeight());
		}
		
		getParentShell().setMinimized(true);
	}

	protected Control createButtonBar(Composite parent) {
		workArea = parent;
		return super.createButtonBar(parent);
	}
	
	protected void firePageChanged(PageChangedEvent event) {
		super.firePageChanged(event);

        try {
        	IWizardPage page = getCurrentPage();
        	wizardInstance.setCurrentPage(SWTHelper.getPageInstance(page));
        	wizardInstance.getCurrentPage().performPreAction();

        	PageInstance summaryPage = TCTHelper.getSummaryPage(wizardInstance);
			if (summaryPage != null) {
				if (page == summaryPage.getNativePage()) {
					getButton(IDialogConstants.FINISH_ID).setText("Configure");
					getButton(SAVE_ID).setVisible(true);
					getButton(LOAD_ID).setVisible(false);
				} else {
					getButton(IDialogConstants.FINISH_ID).setText(IDialogConstants.FINISH_LABEL);
					getButton(SAVE_ID).setVisible(false);
					if (detailsPanel != null) {
						toggleDetailsArea();
					}
					getButton(DETAILS_ID).setVisible(false);
					if (page == getWizard().getStartingPage()) {
						getButton(LOAD_ID).setVisible(true);
					} else {
						getButton(LOAD_ID).setVisible(false);
					}
				}
			}
        } catch (Exception e) {
        	WizardHelper.handleError(wizardInstance, e);
        }
	}
	
    protected void nextPressed() {
        try {
        	wizardInstance.getCurrentPage().performPostAction();
            super.nextPressed();
        } catch (Exception e) {
        	WizardHelper.handleError(wizardInstance, e);
        }
    }
    
	protected void finishPressed() {
		try {
			PageInstance summaryPage = TCTHelper.getSummaryPage(wizardInstance);
			if (summaryPage != null) {
				IWizardPage page = getCurrentPage();
				if (page != summaryPage.getNativePage()) {
					wizardInstance.getCurrentPage().performPostAction();
					showPage((IWizardPage)summaryPage.getNativePage());
				} else {
					if (detailsPanel != null) {
						toggleDetailsArea();
					} else {
						ActionLogger.setDetailsPanel(wizardInstance, null);
						getButton(DETAILS_ID).setVisible(true);
					}
					ActionLogger.setActionLog(wizardInstance, new StringBuffer());
					
					try {
						getButton(SAVE_ID).setEnabled(false);
						wizardInstance.getCurrentPage().performPostAction();
						getButton(IDialogConstants.BACK_ID).setVisible(false);
						getButton(IDialogConstants.NEXT_ID).setVisible(false);
						getButton(IDialogConstants.FINISH_ID).setVisible(false);
						getButton(IDialogConstants.CANCEL_ID).setText("Close");
					} finally {
						getButton(SAVE_ID).setEnabled(true);
						getButton(SAVE_ID).setVisible(false);
					}
				}
			} else {
				WizardHelper.executePostAction(wizardInstance);

				popParent();
				super.finishPressed();
			}
		} catch (Exception e) {
			WizardHelper.handleError(wizardInstance, e);
		}
	}

	protected void cancelPressed() {
		try {
			popParent();
			setReturnCode(1);
			super.close();
		} catch (Exception e) {
			WizardHelper.handleError(wizardInstance, e);
		}
	}

	protected void popParent() {
		if (getShell() != null && getShell().getParent() != null) {
			((Shell) getShell().getParent()).setMinimized(false);
		}
	}

	public ProgressMonitorPart getProgressMonitor() {
		return (ProgressMonitorPart) super.getProgressMonitor();
	}

	public boolean close() {
		cancelPressed();
		return true;
	}
	
	protected void buttonPressed(int buttonId) {
		if (buttonId == LOAD_ID) {
			loadPressed();
		}else if (buttonId == SAVE_ID) {
			savePressed();
		} else if (buttonId == DETAILS_ID) {
			toggleDetailsArea();
		}else {
			super.buttonPressed(buttonId);
		}
	}

	private void toggleDetailsArea() {
		Point windowSize = getShell().getSize();
		Point oldSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (detailsPanel != null) {
			detailsPanel.dispose();
			detailsPanel = null;
			getButton(DETAILS_ID).setText(IDialogConstants.SHOW_DETAILS_LABEL);
		} else {
			detailsPanel = createDetailsPanel((Composite) getContents());
			detailsPanel.setText(ActionLogger.getActionLog(wizardInstance).toString());
			detailsPanel.append("");
			getButton(DETAILS_ID).setText(IDialogConstants.HIDE_DETAILS_LABEL);
			getContents().getShell().layout();
		}
		ActionLogger.setDetailsPanel(wizardInstance, detailsPanel);
		Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		getShell().setSize(new Point(windowSize.x, windowSize.y + (newSize.y - oldSize.y)));
	}
	
	protected Text createDetailsPanel(Composite parent) {
		// create the list
		detailsPanel = new Text(workArea, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI);
		// fill the list
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);
		data.heightHint = 200;
//		data.horizontalSpan = 2;
		detailsPanel.setLayoutData(data);
		detailsPanel.setFont(workArea.getFont());
		return detailsPanel;
	}
	
	protected void loadPressed() {
		if (loadSessionDialog == null) {
			loadSessionDialog = new DirectoryDialog(getShell());
			loadSessionDialog.setMessage("Load data from the previous session folder");
			loadSessionDialog.setFilterPath(TCTContext.getInstance().getWorkDir().getAbsolutePath());
		}
		String sessionDir = loadSessionDialog.open();
		if (sessionDir != null) {
			File dataFileDir = new File(sessionDir, SaveDataModelAction.SAVE_MODEL_DATA_FOLDER);
			File dataModelFile = new File(dataFileDir, "data.xml");
			try {
				WizardHelper.loadDataModel(wizardInstance, dataModelFile);
			} catch (Exception e) {
				WizardHelper.handleError(wizardInstance, e);
			}
		}
	}
	
	protected void savePressed() {
		try {
			SaveDataModelAction.getSaveDataModelAction(wizardInstance).saveDataModel(wizardInstance);
			WizardHelper.openMessage(wizardInstance, "Session scripts have been saved successfully!");
		} catch (Exception e) {
			WizardHelper.handleError(wizardInstance, e);
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButton(parent, DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
		super.createButton(parent, LOAD_ID, "Load...", false);
		super.createButton(parent, SAVE_ID, "Save", false);
		
		getButton(DETAILS_ID).setVisible(false);
		getButton(SAVE_ID).setVisible(false);
		
		super.createButtonsForButtonBar(parent);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Register help listener on the shell
		newShell.addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent event) {
				String contributorId = TCTContext.getInstance().getContributorId(wizardInstance);
				String contextId = contributorId + "." + wizardInstance.getCurrentPage().getPageConfig().getId();
				URL url = HelpContext.getInstance().getHelpByContextId(contextId);
				if (url == null) {
					url = HelpContext.getInstance().getHelpByContextId(contributorId + ".welcome");
				}

				TCTHelper.performHelp(url);
			}
		});
	}
}
