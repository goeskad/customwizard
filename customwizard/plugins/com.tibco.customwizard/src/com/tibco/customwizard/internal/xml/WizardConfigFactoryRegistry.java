package com.tibco.customwizard.internal.xml;

import org.nuxeo.xforms.xml.XFactoryRegistry;

import com.tibco.customwizard.internal.xml.factories.ActionXFactory;
import com.tibco.customwizard.internal.xml.factories.ClassLoaderFactoryXFactory;
import com.tibco.customwizard.internal.xml.factories.DataModelXFactory;
import com.tibco.customwizard.internal.xml.factories.FileXFactory;
import com.tibco.customwizard.internal.xml.factories.PageXFactory;
import com.tibco.customwizard.internal.xml.factories.PagegroupXFactory;
import com.tibco.customwizard.internal.xml.factories.ParentWizardConfigXFactory;
import com.tibco.customwizard.internal.xml.factories.PluginXFactory;
import com.tibco.customwizard.internal.xml.factories.PostActionXFactory;
import com.tibco.customwizard.internal.xml.factories.URLProviderXFactory;
import com.tibco.customwizard.internal.xml.factories.URLXFactory;
import com.tibco.customwizard.internal.xml.factories.WizardProcessorXFactory;
import com.tibco.customwizard.internal.xml.factories.WizardXFactory;

public class WizardConfigFactoryRegistry extends XFactoryRegistry {

	private static WizardConfigFactoryRegistry sInstance = new WizardConfigFactoryRegistry();
	
	public static WizardConfigFactoryRegistry getInstance() {
		return sInstance;
	}
	
	protected void initialize() {
		super.initialize();
	
		registerFactory("wizard", WizardXFactory.INSTANCE);
		registerFactory("parent", ParentWizardConfigXFactory.INSTANCE);
		registerFactory("classloaderfactory", ClassLoaderFactoryXFactory.INSTANCE);
		registerFactory("wizardprocessor", WizardProcessorXFactory.INSTANCE);
		registerFactory("datamodel", DataModelXFactory.INSTANCE);
		registerFactory("postaction", PostActionXFactory.INSTANCE);
		registerFactory("action", ActionXFactory.INSTANCE);
		registerFactory("pagegroup", PagegroupXFactory.INSTANCE);
		registerFactory("page", PageXFactory.INSTANCE);
		registerFactory("urlprovider", URLProviderXFactory.INSTANCE);
		registerFactory("file", FileXFactory.INSTANCE);
		registerFactory("plugin", PluginXFactory.INSTANCE);
		registerFactory("url", URLXFactory.INSTANCE);
	}
}

