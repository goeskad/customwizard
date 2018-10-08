package com.tibco.customwizard.internal.xml.factories;

import org.nuxeo.xforms.xml.DefaultXFactory;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.config.DataModelConfig;
import com.tibco.customwizard.config.IDataModelLoader;
import com.tibco.customwizard.config.WizardConfig;

public class DataModelXFactory extends DefaultXFactory {
	public final static DataModelXFactory INSTANCE = new DataModelXFactory();

	public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
		WizardConfig wizardConfig = (WizardConfig) context.document;
		DataModelConfig dataModelConfig = new DataModelConfig();
		try {
			dataModelConfig.setFile(attrs.getValue("file"));
			String loaderClassName = attrs.getValue("loader");
			if (loaderClassName != null) {
				IDataModelLoader loader = (IDataModelLoader) wizardConfig.getExtendedClassLoader()
						.loadClass(loaderClassName).newInstance();
				dataModelConfig.setLoader(loader);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		wizardConfig.setDataModelConfig(dataModelConfig);
		return dataModelConfig;
	}
}