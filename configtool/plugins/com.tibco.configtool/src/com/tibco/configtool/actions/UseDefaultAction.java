package com.tibco.configtool.actions;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.support.XFormControlState;

public class UseDefaultAction extends OptionAction {
	private String controlId;

	private String copyFromPrefix;
	private String copyToPrefix;

	private List<String> copySuffixList;

	private XFormControlState state;

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public void setCopyFromPrefix(String copyFromPrefix) {
		this.copyFromPrefix = copyFromPrefix;
	}

	public void setCopyToPrefix(String copyToPrefix) {
		this.copyToPrefix = copyToPrefix;
	}

	public void setCopySuffixList(List<String> copySuffixList) {
		this.copySuffixList = copySuffixList;
	}

	public void setCopySuffixs(String copySuffixs) {
		String[] copySuffixArray = copySuffixs.split(",");
		copySuffixList = new ArrayList<String>();
		for (String copySuffix : copySuffixArray) {
			copySuffixList.add(copySuffix);
		}
	}
	
	protected void selectionAction(boolean selected, XForm form, IActionContext actionContext) {
		if (selected) {
			if (state == null) {
				state = XFormControlState.disable(form.getUI().getControl(controlId));
			}
			doCopyDefault(form, actionContext.getDataModel());
		} else {
			if (state != null) {
				state.restore();
				state = null;
			}
		}
		TCTHelper.revalidateCurrentPage(actionContext.getWizardInstance());
	}

	private void doCopyDefault(XForm form, IDataModel dataModel) {
		if (copyFromPrefix != null) {
			UIControl optionControl = getOptionControl(form);
			optionControl.validate();
			optionControl.commit();

			copyDefault(dataModel);
			
			UIControl confirmPassword = form.getUI().getControl("confirmpassword");
			if (confirmPassword != null) {
				confirmPassword.setXMLValue(dataModel.getValue(copyToPrefix + "/password"));
			}
			form.reset();
		}
	}

	public void copyDefault(IDataModel dataModel) {
		for (String copySuffix : copySuffixList) {
			dataModel.setValue(copyToPrefix + "/" + copySuffix, dataModel.getValue(copyFromPrefix + "/" + copySuffix));
		}
	}
}
