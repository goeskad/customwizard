package com.tibco.customwizard.internal.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.swt.SWTForm;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xsd.XSDValidationException;

import com.tibco.customwizard.internal.xforms.validator.ValidateManager;
import com.tibco.customwizard.support.IValidatable;
import com.tibco.customwizard.validator.ValidateResult;

public class CWSWTForm extends SWTForm implements IValidatable {
	private List<UIControl> controlList = new ArrayList<UIControl>();
	
	public CWSWTForm(XForm form, Composite parent) {
		super(form, parent);
	}
	
	public void validate() throws XSDValidationException {
		for (UIControl control : controlList) {
			ValidateManager.setValidateResult(control, ValidateManager.validateControl(control));
		}
	}

	public ValidateResult getValidateResult() {
		for (UIControl control : controlList) {
			ValidateResult validateResult = ValidateManager.getValidateResult(control);
			if (validateResult != null && !validateResult.isValid()) {
				return validateResult;
			}
		}
		return ValidateResult.VALID;
	}
	
	public List<UIControl> getControlList() {
		return controlList;
	}

	public void registerControlProxy(AbstractUIControl proxy) {
		super.registerControlProxy(proxy);
		controlList.add(proxy);
	}
}
