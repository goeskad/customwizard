package com.tibco.customwizard.internal.xforms;

import org.nuxeo.xforms.XFormsProcessor;
import org.nuxeo.xforms.ui.UIBuilder;
import org.nuxeo.xforms.xforms.XFFactoryRegistry;
import org.nuxeo.xforms.xforms.events.ClickEvent;
import org.nuxeo.xforms.xforms.events.DOMActivateEvent;
import org.nuxeo.xforms.xforms.events.DOMFocusInEvent;
import org.nuxeo.xforms.xforms.events.DOMFocusOutEvent;
import org.nuxeo.xforms.xforms.events.DoubleClickEvent;
import org.nuxeo.xforms.xforms.events.MouseDownEvent;
import org.nuxeo.xforms.xforms.events.MouseUpEvent;
import org.nuxeo.xforms.xforms.events.XFormsValueChangedEvent;

import com.tibco.customwizard.internal.xforms.factories.ActionGroupXFactory;
import com.tibco.customwizard.internal.xforms.factories.ActionXFactory;
import com.tibco.customwizard.internal.xforms.factories.MatcherXFactory;
import com.tibco.customwizard.internal.xforms.factories.OpenURLActionXFactory;
import com.tibco.customwizard.internal.xforms.factories.PageActionXFactory;
import com.tibco.customwizard.internal.xforms.factories.PageBranchingActionXFactory;
import com.tibco.customwizard.internal.xforms.factories.ReValidateXFactory;
import com.tibco.customwizard.internal.xforms.factories.RefreshXFactory;
import com.tibco.customwizard.internal.xforms.factories.ResetXFactory;
import com.tibco.customwizard.internal.xforms.factories.SaveModelActionXFactory;
import com.tibco.customwizard.internal.xforms.factories.ValidatorXFactory;
import com.tibco.customwizard.internal.xforms.factories.XFormXFactory;
import com.tibco.customwizard.internal.xforms.handlers.UIControlEventHandler;
import com.tibco.customwizard.internal.xforms.handlers.XFormsValueChangedHandler;
import com.tibco.customwizard.xforms.IXFormExtension;
import com.tibco.customwizard.xforms.events.FileChoseEvent;
import com.tibco.customwizard.xforms.events.ModifyEvent;

public class BaseXFormExtension implements IXFormExtension {
	private static XFFactoryRegistry factoryRegistry = new XFFactoryRegistry();
	static {
		factoryRegistry.registerFactory("xform", XFormXFactory.INSTANCE);
		factoryRegistry.registerFactory("actiongroup", ActionGroupXFactory.INSTANCE);
		factoryRegistry.registerFactory("preaction", PageActionXFactory.INSTANCE);
		factoryRegistry.registerFactory("postaction", PageActionXFactory.INSTANCE);
		factoryRegistry.registerFactory("action", ActionXFactory.INSTANCE);
		factoryRegistry.registerFactory("savemodel", SaveModelActionXFactory.INSTANCE);
		factoryRegistry.registerFactory("refresh", RefreshXFactory.INSTANCE);
		factoryRegistry.registerFactory("reset", ResetXFactory.INSTANCE);
		factoryRegistry.registerFactory("revalidate", ReValidateXFactory.INSTANCE);
		factoryRegistry.registerFactory("pagebranching", PageBranchingActionXFactory.INSTANCE);
		factoryRegistry.registerFactory("openurl", OpenURLActionXFactory.INSTANCE);
		factoryRegistry.registerFactory("validator", ValidatorXFactory.INSTANCE);
		factoryRegistry.registerFactory("matcher", MatcherXFactory.INSTANCE);
	}

	public UIBuilder getUIBuilder() {
		return null;
	}

	public XFormsProcessor getXFormsProcessor() {
		CWXFormsProcessor processor = new CWXFormsProcessor();
		processor.setFactoryRegistry(factoryRegistry);
		registerEventHandler(processor);
		return processor;
	}

	protected void registerEventHandler(XFormsProcessor processor) {
		processor.registerEventHandler(XFormsValueChangedEvent.ID, XFormsValueChangedHandler.INSTANCE);
		processor.registerEventHandler(ModifyEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(DOMActivateEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(DOMFocusInEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(DOMFocusOutEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(ClickEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(DoubleClickEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(MouseUpEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(MouseDownEvent.ID, UIControlEventHandler.INSTANCE);
		processor.registerEventHandler(FileChoseEvent.ID, UIControlEventHandler.INSTANCE);
	}
}
