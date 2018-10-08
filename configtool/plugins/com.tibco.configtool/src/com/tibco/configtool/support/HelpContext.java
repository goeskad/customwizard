package com.tibco.configtool.support;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.util.WizardHelper;

public class HelpContext {
	private static final String CONTEXT_AMX = "tct.amx";

	private static HelpContext instance = new HelpContext();

	private Map<String, URL> contextUrlMap;

	public static HelpContext getInstance() {
		return instance;
	}

	public static String getDefaultHelpContextId() {
		return CONTEXT_AMX;
	}

	public String getHelpContextId(PageInstance pageInstance) {
		URL configFile = pageInstance.getWizardInstance().getWizardConfig().getConfigFile();
		String pluginName = new File(configFile.getFile()).getParentFile().getName();
		return TCTContext.getInstance().getContributorId(pluginName) + "." + pageInstance.getPageConfig().getId();
	}

	public URL getHelpByContextId(String contextId) {
		return contextUrlMap.get(contextId);
	}

	private HelpContext() {
		TCTContext tctContext = TCTContext.getInstance();
		try {
			init(tctContext);
		} catch (Exception e) {
			WizardHelper.openErrorDialog(tctContext.getWizardInstance(), e);
		}
	}

	private void init(TCTContext tctContext) throws Exception {
		File pluginsDir = tctContext.getContributorDir();
		String[] list = pluginsDir.list();
		for (String helpDocPluginName : list) {
			if (helpDocPluginName.matches("com\\.tibco\\.amx\\.tct\\.help\\.doc_.*")) {
				String helpPluginLocation = TCTHelper.toUnixPath(pluginsDir.getAbsolutePath() + "/" + helpDocPluginName);
				Map<String, URL> contextMap = parseHelpContents(helpPluginLocation);
				Properties helpContextMapping = TCTHelper.loadProperties(tctContext.getWizardInstance(), "res/helpcontextmapping.properties");

				contextUrlMap = new HashMap<String, URL>(contextMap.size());
				for (Object mappingIdObject : helpContextMapping.keySet()) {
					String mappingId = (String) mappingIdObject;
					String contextId = helpContextMapping.getProperty(mappingId);
					contextUrlMap.put(mappingId, contextMap.get(contextId));
				}
				contextUrlMap.put(CONTEXT_AMX, new URL("file://" + helpPluginLocation + "/tib_amx_tct/c_Configuration.htm"));

				break;
			}
		}
	}

	private Map<String, URL> parseHelpContents(String helpPluginLocation) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(helpPluginLocation + "/" + "contexts.xml"));

		NodeList contextList = doc.getElementsByTagName("context");
		Map<String, URL> contextMap = new HashMap<String, URL>(contextList.getLength());
		for (int j = 0; j < contextList.getLength(); j++) {
			Node context = contextList.item(j);
			String id = context.getAttributes().getNamedItem("id").getNodeValue();
			String expression = "/contexts/context[@id='" + id + "']/topic/@href";
			XPath xpath = XPathFactory.newInstance().newXPath();
			String href = (String) xpath.evaluate(expression, doc, XPathConstants.STRING);
			String url = "file://" + helpPluginLocation + href.substring(27);

			contextMap.put(id, new URL(url));
		}

		return contextMap;
	}
}
