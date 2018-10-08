package com.tibco.customwizard.support;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.IDataModelLoader;
import com.tibco.customwizard.internal.support.XMLDataModel;
import com.tibco.customwizard.util.WizardHelper;

public class XMLDataModelLoader implements IDataModelLoader {
	public IDataModel createDataModel(File file) throws Exception {
		String content = WizardHelper.subtitutionSystemProperties(WizardHelper.toURL(file));
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(content.getBytes()));
		return new XMLDataModel(document);
	}

	public void loadDataModel(IDataModel currentDataModel, IDataModel targetDataModel) throws Exception {
		loadDataModel(currentDataModel, convertToXPathMap(currentDataModel), convertToXPathMap(targetDataModel));
	}
	
    protected void loadDataModel(IDataModel currentDataModel, Map<String, String> currentMap, Map<String, String> targetMap) throws Exception {
    	Map<String, String> changedMap = new HashMap<String, String>();
		Map<String, String> missedMap = new HashMap<String, String>();
		Map<String, String> additionalMap = new HashMap<String, String>();
		
		for (Entry<String, String> entry : currentMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (targetMap.containsKey(key)) {
				String newValue = targetMap.get(key);
				if (!(newValue == value || (newValue != null && newValue.equals(value)))) {
					changedMap.put(key, value);
				}
			} else {
				missedMap.put(key, value);
			}
		}

		for (Entry<String, String> entry : targetMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (!currentMap.containsKey(key)) {
				additionalMap.put(key, value);
			}
		}
		
		//process the missed, additional, changed properties
		processMissedProperties(missedMap, targetMap, currentDataModel);
		processAdditionalProperties(additionalMap, targetMap, currentDataModel);
		processChangedProperties(changedMap, targetMap, currentDataModel);
	}

	protected void processMissedProperties(Map<String, String> missedMap, Map<String, String> targetMap, IDataModel currentDataModel) throws Exception {
		// as default behavior, we don't allow missed property
		if (missedMap.size() > 0) {
			throw new Exception("Load failed: missing " + missedMap.size() + " properties.");
		}
	}

	protected void processAdditionalProperties(Map<String, String> additionalMap, Map<String, String> targetMap, IDataModel currentDataModel) throws Exception {
		// just ignore the additional properties
	}

	protected void processChangedProperties(Map<String, String> changedMap, Map<String, String> targetMap, IDataModel currentDataModel) throws Exception {
		for (String key : changedMap.keySet()) {
			currentDataModel.setValue(key, targetMap.get(key));
		}
	}

	public void saveDataModel(IDataModel dataModel, File file) throws Exception {
		FileWriter writer = new FileWriter(file);
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(getDocument(dataModel));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
		} finally {
			writer.close();
		}
	}
	
	private Map<String, String> convertToXPathMap(IDataModel dataModel) {
		Map<String, String> map = new HashMap<String, String>();
		Element root = getDocument(dataModel).getDocumentElement();
		convertToXPathMap(root, map, "");
		return map;
	}
	
	private void convertToXPathMap(Node node, Map<String, String> xpathMap, String prefix) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList nodeList = node.getChildNodes();
			int length = nodeList.getLength();
			String thisPath = prefix + "/" + node.getNodeName();
			if (length == 0 || (length == 1 && nodeList.item(0).getNodeType() == Node.TEXT_NODE)) {
				xpathMap.put(thisPath, node.getTextContent());
			} else {
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node child = nodeList.item(i);
					convertToXPathMap(child, xpathMap, thisPath);
				}
			}
		}
	}
	
	public static final Document getDocument(IDataModel dataModel) {
		if (dataModel instanceof XMLDataModel) {
			return ((XMLDataModel) dataModel).getDocument();
		}
		return null;
	}
	
	public static final void addValue(IDataModel dataModel, String key, String value) {
		if (dataModel instanceof XMLDataModel) {
			((XMLDataModel) dataModel).addValue(key, value);
		}
	}
}
