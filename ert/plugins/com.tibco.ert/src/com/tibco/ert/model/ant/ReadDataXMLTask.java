package com.tibco.ert.model.ant;

import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ReadDataXMLTask extends Task {
	private String dataFile;

	private Document document;

	private XPath xpath = XPathFactory.newInstance().newXPath();

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public void execute() throws BuildException {
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(dataFile));

			getProject().setProperty("ert.source.host", getValue("/ert/source/host"));
			getProject().setProperty("ert.source.port", getValue("/ert/source/port"));
			getProject().setProperty("ert.source.username", getValue("/ert/source/username"));
			getProject().setProperty("ert.source.password", getValue("/ert/source/password"));
			getProject().setProperty("ert.source.dbpassword", getValue("/ert/source/dbpassword"));
			
			getProject().setProperty("ert.target.host", getValue("/ert/target/host"));
			getProject().setProperty("ert.target.port", getValue("/ert/target/port"));
			getProject().setProperty("ert.target.username", getValue("/ert/target/username"));
			getProject().setProperty("ert.target.password", getValue("/ert/target/password"));
			getProject().setProperty("ert.target.dbpassword", getValue("/ert/target/dbpassword"));
			
			getProject().setProperty("ert.configuration.extraction", getValue("/ert/configuration/extraction"));
			getProject().setProperty("ert.target.datafile", getValue("/ert/target/datafile"));
			
			getProject().setProperty("ert.extraction.target", getValue("/ert/extraction/target"));
			
			getProject().setProperty("ert.configuration.cli", getValue("/ert/configuration/cli"));
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	private String getValue(String key) {
        Node node = findNode(key);
        if (node != null) {
            return node.getTextContent();
        }
        return null;
    }
	
	private Node findNode(String path) {
		try {
			return (Node) xpath.evaluate(path, document.getDocumentElement(), XPathConstants.NODE);
		} catch (XPathException e) {
			return null;
		}
	}
}
