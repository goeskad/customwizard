package com.tibco.customwizard.internal.swt;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;

import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.validator.ValidateResult;

public class CustomWizardPage extends WizardPage {
	private PageInstance pageInstance;

	public CustomWizardPage(PageInstance pageInstance) {
		super(pageInstance.getPageConfig().getId());
		this.setTitle(pageInstance.getPageConfig().getTitle());
		this.setDescription(pageInstance.getPageConfig().getDescription());
		this.pageInstance = pageInstance;
	}

	public PageInstance getPageInstance() {
		return pageInstance;
	}

	public void createControl(Composite parent) {
		UIForm uiForm = SWTHelper.buildUI(pageInstance, parent);
		Control[] children = parent.getChildren();
		for (Control control : children) {
			UIControl controlProxy = uiForm.getProxy(control);
			if (controlProxy != null && controlProxy.getElement().getForm().getUI() == uiForm) {
				setControl(control);
				break;
			}
		}
		WizardHelper.validate(pageInstance);
	}

	public boolean isPageComplete() {
		if (getControl() == null) {
			return false;
		}

		ValidateResult validateResult = WizardHelper.getValidateResult(pageInstance);
		if (validateResult != null && !validateResult.isValid()) {
			setErrorMessage(validateResult.getErrorMessage());
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	public void dispose() {
		super.dispose();
		setControl(null);
	}

	public void setPreviousPage(IWizardPage page) {
		// always use wizard to get previous page
	}

	public void refresh() throws Exception {
		Control control = getControl();
		if (control != null) {
			Composite parent = control.getParent();

			control.dispose();
			setControl(null);

			createControl(parent);

			IWizardContainer wizardContainer = getContainer();
			wizardContainer.updateButtons();
			if (wizardContainer instanceof WizardDialog) {
				((WizardDialog) wizardContainer).updateSize();
			}

			if (getControl() != null) {
				SWTHelper.redraw(getControl());
			}
		}
	}

	public String toString() {
		return getTitle();
	}
}
