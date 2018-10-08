package com.tibco.customwizard.internal.support;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tibco.customwizard.config.IDataModel;

public class XMLDataModel implements IDataModel {
	private Document document;

	private XPath xpath = XPathFactory.newInstance().newXPath();

	public XMLDataModel(Document document) {
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public void setValue(String key, String value) {
		Node node = findNode(key);
		if (node == null) {
			node = createNode(key);
		}
		node.setTextContent(value);
	}

	public String getValue(String key) {
		Node node = findNode(key);
		if (node != null) {
			return node.getTextContent();
		}
		return null;
	}

	public boolean remove(String key) {
		NodeList nodeList = findNodes(key);
		if (nodeList != null && nodeList.getLength() > 0) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				node.getParentNode().removeChild(node);
			}

			return true;
		}
		return false;
	}

	public void addValue(String key, String value) {
		Node node = createNode(key);
		node.setTextContent(value);
	}

	private Node findNode(String path) {
		try {
			return (Node) xpath.evaluate(path, document.getDocumentElement(), XPathConstants.NODE);
		} catch (XPathException e) {
			return null;
		}
	}

	private NodeList findNodes(String path) {
		try {
			return (NodeList) xpath.evaluate(path, document.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathException e) {
			return null;
		}
	}

	private Node createNode(String path) {
		String parentPath = getParentPath(path);
		NodeList parentNodeList = findNodes(parentPath);
		Node parentNode;
		if (parentNodeList == null || parentNodeList.getLength() == 0) {
			parentNode = createNode(parentPath);
		} else {
			parentNode = parentNodeList.item(parentNodeList.getLength() - 1);
		}
		Node node = document.createElement(getTagName(path));
		parentNode.appendChild(node);
		return node;
	}

	private String getTagName(String path) {
		int index = getParentPathIndex(path);

		if (index > 0) {
			int endIndex = path.length();
			if (path.endsWith("/")) {
				endIndex--;
			}
			return path.substring(index + 1, endIndex);
		} else {
			return null;
		}
	}

	private String getParentPath(String path) {
		int index = getParentPathIndex(path);

		if (index > 0) {
			return path.substring(0, index);
		} else {
			return "/";
		}
	}

	private int getParentPathIndex(String path) {
		int index = path.lastIndexOf('/');
		if (index == path.length() - 1) {
			path = path.substring(0, index);
			index = path.lastIndexOf('/');
		}

		return index;
	}
}
