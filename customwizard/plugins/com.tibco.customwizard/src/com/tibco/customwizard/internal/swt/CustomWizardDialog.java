package com.tibco.customwizard.internal.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.tibco.customwizard.config.PageGroupConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;

public class CustomWizardDialog extends WizardDialog {
    private List<PageGroupConfig> pageGroupList = new ArrayList<PageGroupConfig>();

    private boolean showTree;
    private PageGroupTreeViewer treeViewer;

    private WizardInstance wizardInstance;

    public CustomWizardDialog(Shell parentShell, IWizard newWizard, WizardInstance wizardInstance) {
        super(parentShell, newWizard);
        setShellStyle(getShellStyle() | SWT.DIALOG_TRIM);
        this.wizardInstance = wizardInstance;
    }

    public List<PageGroupConfig> getPageGroupList() {
        return pageGroupList;
    }

    public void setPageGroupList(List<PageGroupConfig> pageGroupList) {
        this.pageGroupList = pageGroupList;
    }

    protected Control createDialogArea(Composite parent) {
        final Shell shell = getShell();

        if (wizardInstance.getWizardConfig().getWidth() != 0 && wizardInstance.getWizardConfig().getHeight() != 0)
            shell.setMinimumSize(wizardInstance.getWizardConfig().getWidth(), wizardInstance.getWizardConfig().getHeight());

        if (showTree) {
            SashForm content = new SashForm(parent, SWT.FILL);
            treeViewer = new PageGroupTreeViewer(content);

            treeViewer.setInput(pageGroupList);

            treeViewer.expandAll();
            super.createDialogArea(content);
            content.setWeights(new int[] { 1, 3 });

            Control[] kids = shell.getChildren();
            Composite kid = (Composite) kids[0];
            kids = kid.getChildren();
            kid = (Composite) kids[0];
            GridData gd = new GridData(GridData.FILL_BOTH);
            kid.setLayoutData(gd);
            kids = kid.getChildren();

            kid = (SashForm) kids[0];
            kid.setLayoutData(gd);

            return content;
        } else {
            return super.createDialogArea(parent);
        }
    }

    protected void update() {
        IWizardPage page = getCurrentPage();

        super.update();

        try {
            if (treeViewer != null) {
                treeViewer.updateSelection(page);
			}

			wizardInstance.setCurrentPage(SWTHelper.getPageInstance(page));
			wizardInstance.getCurrentPage().performPreAction();
        } catch (Exception e) {
        	wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
        }
    }
    
    protected void nextPressed() {
        try {
        	wizardInstance.getCurrentPage().performPostAction();
            super.nextPressed();
        } catch (Exception e) {
        	wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
        }
    }

    protected void finishPressed() {
        try {
        	WizardHelper.executePostAction(wizardInstance);

            popParent();
            super.finishPressed();
        } catch (Exception e) {
        	wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
        }
    }

    protected void cancelPressed() {
        try {
            popParent();
            setReturnCode(1);
            super.close();
        } catch (Exception e) {
        	wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
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
}
