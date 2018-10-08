package com.tibco.customwizard.internal.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;
import org.nuxeo.xforms.ui.swt.SWTBuilder;
import org.nuxeo.xforms.ui.swt.SWTRegistry;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xforms.model.controls.XFGroup;
import org.nuxeo.xforms.xforms.model.controls.XFLabel;
import org.nuxeo.xforms.xforms.model.controls.XFSelect1;

import com.tibco.customwizard.internal.swt.proxies.ComboProxy;
import com.tibco.customwizard.util.SWTHelper;

public class CWSWTBuilder extends SWTBuilder {
	public CWSWTBuilder(SWTRegistry registry) {
		super(registry);
	}

	public UIForm processForm(XForm element, Object parent) {
		mContext.form = new CWSWTForm(element, (Composite)parent);
		return mContext.form;
	}
	
	@SuppressWarnings("rawtypes")
	protected AbstractUIControl create(XFControlElement element, Class defaultProxyClass, UIControl parent)
			throws Exception {
		// change the default toolkit flag to false
		this.mContext.toolkitAdapt = false;
		AbstractUIControl control = super.create(element, defaultProxyClass, parent);
		if (control.isEditable()) {
			Control swtControl = (Control) control.getControl();
			swtControl.addListener(SWT.Modify, CWSWTModifyListener.INSTANCE);
		}
		return control;
	}
	
	public Object visitGroup(XFGroup group, Object arg) {
		UIControl ctrl = (UIControl) super.visitGroup(group, arg);
		
		SWTHelper.activeScrolledComposite(ctrl);
		
		return ctrl;
	}
	
	/**
	 * Add an option for editable combobox
	 */
	public Object visitSelect1(XFSelect1 input, Object arg) {
		UIControl parent = (UIControl)arg;
		String appearance = input.getAppearance();
		if ("full".equals(appearance)) {
			return processFullSelect1(input, parent);
		} else {
			XFLabel label = input.getLabel();
			if (label != null) processLabel(label, parent);
			if ("minimal".equals(appearance)) {
				return processMinimalSelect1(input, parent);
			} else if("editable".equals(appearance)){
				return processEditableSelect1(input, parent);
			}else { // compact is the default
				return processSelect1(input, parent);	
			}						
		}
	}
	
	
	public Object processEditableSelect1(XFSelect1 element, UIControl parent) {
		UIControl ctrl = null;
		try {
			mContext.style |= SWT.DROP_DOWN;	
			ctrl = createCombo(element, ComboProxy.class, parent);				
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}
		
		return ctrl;
	}
	
	public UIControl processMinimalSelect1(XFSelect1 element, UIControl parent) {
		UIControl ctrl = null;
		try {
			mContext.style |= SWT.DROP_DOWN | SWT.READ_ONLY;	
			ctrl = createCombo(element, ComboProxy.class, parent);				
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}
		
		return ctrl;
	}
}
