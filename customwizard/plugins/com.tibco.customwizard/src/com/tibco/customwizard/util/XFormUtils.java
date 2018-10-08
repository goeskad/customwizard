package com.tibco.customwizard.util;

import java.io.File;
import java.net.URL;

import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XFInstance;
import org.nuxeo.xforms.xforms.model.XFModel;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.controls.ControlList;
import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xforms.model.controls.XFGroup;
import org.nuxeo.xforms.xml.StringInputSource;
import org.nuxeo.xforms.xml.XInputSource;
import org.nuxeo.xforms.xml.XParser;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.w3c.dom.Document;

import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.xforms.BaseXFormExtension;
import com.tibco.customwizard.internal.xforms.PageInstanceInputSource;
import com.tibco.customwizard.internal.xml.WizardConfigParserConfiguration;
import com.tibco.customwizard.support.IValidatable;
import com.tibco.customwizard.support.XMLDataModelLoader;
import com.tibco.customwizard.validator.ValidateResult;

public class XFormUtils {
	private static final String PAGE_XFORM_KEY = "xform-object";

	private static final BaseXFormExtension baseXFormExtension = new BaseXFormExtension();

	public static WizardInstance getWizardInstance(UIControl control) {
		return getWizardInstance(((AbstractUIControl) control).form.getForm());
	}
	
	public static WizardInstance getWizardInstance(XForm form) {
		return getPageInstance(form).getWizardInstance();
	}
	
	public static PageInstance getPageInstance(XForm form) {
		return getPageInstance(form.getProcessor().getFormSource());
	}

	public static XForm getXForm(PageInstance pageInstance) {
		return (XForm) pageInstance.getAttribute(PAGE_XFORM_KEY);
	}
	
	public static ClassLoader getContextClassLoader(XParserGlobalContext context) {
		WizardInstance wizardInstance = getPageInstance(context.input).getWizardInstance();
		return wizardInstance.getWizardConfig().getExtendedClassLoader();
	}
	
	public static PageInstance getPageInstance(XInputSource input) {
		if (input instanceof PageInstanceInputSource) {
			return ((PageInstanceInputSource) input).getPageInstance();
		}
		return null;
	}

	public static void parseWizardConfig(URL configFile, WizardConfig wizardConfig) throws Exception {
		new XParser(new WizardConfigParserConfiguration(wizardConfig)).parse(new StringInputSource(WizardHelper
				.subtitutionSystemProperties(configFile)));
	}
	
	public static void refreshXForm(PageInstance pageInstance) throws Exception {
		XForm xform = getXForm(pageInstance);
		if (xform != null) {
			Document document = xform.getModel().getInstance().getDocument();
			xform = xform.getProcessor().load(xform.getProcessor().getFormSource());
			XFModel model = new XFModel(xform);
			XFInstance instance = new XFInstance(model);
			instance.setDocument(document);
			model.setInstance(instance);
			xform.addModel(model);
		}
	}
	
	public static ValidateResult getValidateResult(PageInstance pageInstance) {
		XForm xform = getXForm(pageInstance);
		if (xform != null && xform.getUI() instanceof IValidatable) {
			return ((IValidatable) xform.getUI()).getValidateResult();
		}
		return null;
	}

	public static void validate(PageInstance pageInstance) {
		XForm xform = getXForm(pageInstance);
		if (xform != null && xform.getUI() instanceof IValidatable) {
			((IValidatable) xform.getUI()).validate();
		}
	}
	
	public static XForm loadXForm(PageInstance pageInstance, String referenceFile) throws Exception {
		WizardInstance wizardInstance = pageInstance.getWizardInstance();

		File xformFile = WizardHelper.getResourceFile(wizardInstance.getWizardConfig(), referenceFile);
		XForm xform = baseXFormExtension.getXFormsProcessor().load(
				new PageInstanceInputSource(pageInstance, WizardHelper.toURL(xformFile)));
		XFModel model = xform.getModel();
		if (model == null) {
			model = new XFModel(xform);
			xform.addModel(model);
		}
		XFInstance xfInstance = new XFInstance(model);
		model.setInstance(xfInstance);
		xfInstance.setDocument(XMLDataModelLoader.getDocument(wizardInstance.getDataModel()));
		pageInstance.setAttribute(PAGE_XFORM_KEY, xform);
		return xform;
	}
	
	public static void resetXForm(XForm xform, Document document) {
		ControlList controls = xform.getControls();
		for (int i = 0; i < controls.size(); i++) {
			XFControlElement element = controls.getControl(i);
			resetControlNodeReference(element);
		}
		xform.getModel().getInstance().setDocument(document);
		if (xform.getUI() != null) {
			xform.getUI().reset();
		}
	}
	
	private static void resetControlNodeReference(XFControlElement element) {
		element.getNodeReference().invalidate();
		if (element instanceof XFGroup) {
			ControlList controls = ((XFGroup) element).getControls();
			for (int i = 0; i < controls.size(); i++) {
				XFControlElement subElement = controls.getControl(i);
				resetControlNodeReference(subElement);
			}
		} 
	}
}
