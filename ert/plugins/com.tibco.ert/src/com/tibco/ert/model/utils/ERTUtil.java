package com.tibco.ert.model.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import com.tibco.amxadministrator.command.line.types.Environment;
import com.tibco.amxadministrator.command.line.types.Node;
import com.tibco.amxadministrator.command.line.types.ServiceAssembly;
import com.tibco.amxadministrator.command.line.typesBase.Enterprise;
import com.tibco.amxadministrator.command.line.typesBase.EnterpriseDocument;
import com.tibco.amxadministrator.command.line.typesBase.EnvironmentBase;
import com.tibco.ert.model.matrix.AdminDBDelegate;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.matrix.admin.server.services.adminconfiguration.types.ClusterConfigurationDetails;

public class ERTUtil {
	private static SimpleDateFormat tsFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public static AdminDBDelegate getDBDelegate(AdminServicesDelegate delegate) throws Exception {
		ClusterConfigurationDetails configDetails = delegate.getAdminClusterConfiguration();
		return new AdminDBDelegate(configDetails.getDbParams(), delegate.getAccessConfig().getDbPassword());
	}

	public static String getTimestamp() {
		return tsFormat.format(new Date());
	}

	public static String getFileName(File file) {
		String fileName = file.getName();

		int index = fileName.lastIndexOf('.');

		return index == -1 ? fileName : fileName.substring(0, index);
	}

	/**
	 * write content to the file
	 * 
	 * @param file
	 * @param content
	 * @throws Exception
	 */
	public static void writeFile(File file, String content) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			writer.write(content);
		} finally {
			writer.close();
		}
	}

	/**
	 * write a byte array to a file
	 * 
	 * @param file
	 * @param content
	 * @throws Exception
	 */
	public static void writeFile(File file, byte[] content) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			out.write(content);
		} finally {
			out.close();
		}
	}

	public static byte[] readFile(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] data = new byte[1024];

		try {
			int len = 0;
			while ((len = in.read(data)) != -1) {
				bout.write(data, 0, len);
			}
		} finally {
			in.close();
		}
		return bout.toByteArray();
	}

	public static void copyFiles(File sourceDir, File targetDir, FileFilter fileFilter) throws IOException {
		File[] sourceFiles = sourceDir.listFiles(fileFilter);
		for (File sourceFile : sourceFiles) {
			copyFile(sourceFile, new File(targetDir, sourceFile.getName()));
		}
	}

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		byte[] data = readFile(sourceFile);
		writeFile(targetFile, data);
	}

	/**
	 * get enterprise from a xml file
	 * 
	 * @param file
	 * @return enterprise
	 * @throws Exception
	 */
	public static Enterprise loadEnterprise(File file) throws Exception {
		EnterpriseDocument doc = EnterpriseDocument.Factory.parse(file);
		return doc.getEnterprise();
	}

	public static Enterprise loadEnterprise(InputStream in) throws Exception {
		EnterpriseDocument doc = EnterpriseDocument.Factory.parse(in);
		return doc.getEnterprise();
	}

	public static Enterprise loadSeparateEnterprise(File dir) throws Exception {
		Enterprise enterprise = Enterprise.Factory.newInstance();

		// Machine
		File file = new File(dir, "Machine.xml");
		if (file.exists()) {
			enterprise.setMachineArray(loadEnterprise(file).getMachineArray());
		}

		// User&Group
		file = new File(dir, "UserAndGroup.xml");
		if (file.exists()) {
			Enterprise tmp = loadEnterprise(file);
			enterprise.setUserArray(tmp.getUserArray());
			enterprise.setListOfSuperUserArray(tmp.getListOfSuperUserArray());
			enterprise.setGroupArray(tmp.getGroupArray());
		}

		// UIPermission
		file = new File(dir, "UIPermission.xml");
		if (file.exists()) {
			enterprise.setUIElementArray(loadEnterprise(file).getUIElementArray());
		}

		// Keystore
		file = new File(dir, "Keystore.xml");
		if (file.exists()) {
			enterprise.setKeystoreArray(loadEnterprise(file).getKeystoreArray());
		}

		// SharedResource
		file = new File(dir, "SharedResource.xml");
		if (file.exists()) {
			enterprise.setSharedResourceDefinitionArray(loadEnterprise(file).getSharedResourceDefinitionArray());
		}

		// SubstitutionVariable
		file = new File(dir, "SubstitutionVariable.xml");
		if (file.exists()) {
			enterprise.setSubstitutionVariableArray(loadEnterprise(file).getSubstitutionVariableArray());
		}

		// AdminCluster
		file = new File(dir, "AdminCluster.xml");
		if (file.exists()) {
			enterprise.setAdminCluster(loadEnterprise(file).getAdminCluster());
		}

		// Environment
		List<EnvironmentBase> environmentList = new ArrayList<EnvironmentBase>();
		File[] files = dir.listFiles();
		for (File testFile : files) {
			if (testFile.getName().startsWith("Environment") && testFile.getName().endsWith(".xml")) {
				EnvironmentBase[] environments = loadEnterprise(testFile).getEnvironmentArray();
				for (EnvironmentBase environment : environments) {
					environmentList.add(environment);
				}
			}
		}

		// Node
		for (File testFile : files) {
			if (testFile.getName().startsWith("Node") && testFile.getName().endsWith(".xml")) {
				EnvironmentBase[] environments = loadEnterprise(testFile).getEnvironmentArray();
				for (EnvironmentBase nodeEnvironment : environments) {
					for (EnvironmentBase environment : environmentList) {
						if (environment.getName().equals(nodeEnvironment.getName())) {
							environment.setNodeArray(nodeEnvironment.getNodeArray());
							break;
						}
					}
				}
			}
		}

		// ServiceAssembly
		for (File testFile : files) {
			if (testFile.getName().startsWith("ServiceAssembly") && testFile.getName().endsWith(".xml")) {
				EnvironmentBase[] environments = loadEnterprise(testFile).getEnvironmentArray();
				for (EnvironmentBase saEnvironment : environments) {
					for (EnvironmentBase environment : environmentList) {
						if (environment.getName().equals(saEnvironment.getName())) {
							environment.setServiceAssemblyArray(saEnvironment.getServiceAssemblyArray());
							break;
						}
					}
				}
			}
		}

		enterprise.setEnvironmentArray(environmentList.toArray(new EnvironmentBase[environmentList.size()]));
		return enterprise;
	}

	/**
	 * save enterprise to a xml file
	 * 
	 * @param file
	 * @param enterprise
	 * @throws Exception
	 */
	public static void saveEnterprise(File file, Enterprise enterprise) throws Exception {
		EnterpriseDocument doc = EnterpriseDocument.Factory.newInstance();
		doc.setEnterprise(enterprise);
		writeFile(file, doc.toString());
	}

	public static void saveSeparateEnterprise(File dir, Enterprise enterprise) throws Exception {
		EnterpriseDocument doc = EnterpriseDocument.Factory.newInstance();
		doc.setEnterprise(enterprise);

		Enterprise tmpEnterprise;

		// UIPermission
		if (enterprise.getUIElementArray().length > 0) {
			tmpEnterprise = Enterprise.Factory.newInstance();
			tmpEnterprise.setUIElementArray(enterprise.getUIElementArray());
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "UIPermission.xml"), doc.toString());
		}

		// Machine
		if (enterprise.getMachineArray().length > 0) {
			tmpEnterprise = Enterprise.Factory.newInstance();
			tmpEnterprise.setMachineArray(enterprise.getMachineArray());
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "Machine.xml"), doc.toString());
		}
		
		// SharedResource
		if (enterprise.getSharedResourceDefinitionArray().length > 0) {
			tmpEnterprise = Enterprise.Factory.newInstance();
			tmpEnterprise.setSharedResourceDefinitionArray(enterprise.getSharedResourceDefinitionArray());
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "SharedResource.xml"), doc.toString());
		}

		// Keystore
		if (enterprise.getKeystoreArray().length > 0) {
			tmpEnterprise = Enterprise.Factory.newInstance();
			tmpEnterprise.setKeystoreArray(enterprise.getKeystoreArray());
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "Keystore.xml"), doc.toString());
		}

		// User&Group
		if (enterprise.getUserArray().length > 0 || enterprise.getGroupArray().length > 0) {
			tmpEnterprise = Enterprise.Factory.newInstance();
			tmpEnterprise.setUserArray(enterprise.getUserArray());
			tmpEnterprise.setListOfSuperUserArray(enterprise.getListOfSuperUserArray());
			tmpEnterprise.setGroupArray(enterprise.getGroupArray());
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "UserAndGroup.xml"), doc.toString());
		}

		// SubstitutionVariable
		if (enterprise.getSubstitutionVariableArray().length > 0) {
			tmpEnterprise = Enterprise.Factory.newInstance();
			tmpEnterprise.setSubstitutionVariableArray(enterprise.getSubstitutionVariableArray());
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "SubstitutionVariable.xml"), doc.toString());
		}

		// AdminCluster
		if (enterprise.getAdminCluster() != null) {
			tmpEnterprise = Enterprise.Factory.newInstance();
			tmpEnterprise.setAdminCluster(enterprise.getAdminCluster());
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "AdminCluster.xml"), doc.toString());
		}

		// Environment
		tmpEnterprise = Enterprise.Factory.newInstance();
		EnvironmentBase[] environments = enterprise.getEnvironmentArray();
		for (EnvironmentBase environment : environments) {
			Environment tmpEnvironment = Environment.Factory.newInstance();

			tmpEnvironment.setName(environment.getName());
			tmpEnvironment.setDescription(((Environment)environment).getDescription());

			// Node
			if (environment.getNodeArray().length > 0) {
				tmpEnvironment.setNodeArray(environment.getNodeArray());
				tmpEnterprise.setEnvironmentArray(new Environment[] { tmpEnvironment });
				doc.setEnterprise(tmpEnterprise);
				writeFile(new File(dir, "Node_Environment(" + environment.getName() + ").xml"), doc.toString());
				tmpEnvironment.setNodeArray(new Node[0]);
			}

			// ServiceAssembly
			if (environment.getServiceAssemblyArray().length > 0) {
				tmpEnvironment.setServiceAssemblyArray(environment.getServiceAssemblyArray());
				tmpEnterprise.setEnvironmentArray(new Environment[] { tmpEnvironment });
				doc.setEnterprise(tmpEnterprise);
				writeFile(new File(dir, "ServiceAssembly_Environment(" + environment.getName() + ").xml"),
						doc.toString());
			}

			// Environment
			tmpEnvironment = (Environment)environment;
			tmpEnvironment.setNodeArray(new Node[0]);
			tmpEnvironment.setServiceAssemblyArray(new ServiceAssembly[0]);
			tmpEnterprise.setEnvironmentArray(new EnvironmentBase[] { tmpEnvironment });
			doc.setEnterprise(tmpEnterprise);
			writeFile(new File(dir, "Environment(" + environment.getName() + ").xml"), doc.toString());
		}
	}

	public static Document parseXML(String xmlConent) throws Exception {
		SAXBuilder builder = new SAXBuilder(false);

		Document doc = builder.build(new StringReader(xmlConent));
		return doc;
	}

	public static void writeObject(Object obj, File file) throws Exception {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		try {
			out.writeObject(obj);
		} finally {
			out.close();
		}
	}

	public static Object readObject(File file) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		try {
			return in.readObject();
		} finally {
			in.close();
		}
	}

	public static void updateTempDir(File sourceDir) throws IOException {
		File tempDir = new File(sourceDir.getParent(), "temp");
		if (tempDir.exists()) {
			File[] tempFiles = tempDir.listFiles();
			for (File tempFile : tempFiles) {
				if (!new File(sourceDir, tempFile.getName()).exists()) {
					delete(tempFile);
				}
			}

			File[] files = sourceDir.listFiles();
			for (File file : files) {
				File tempFile = new File(tempDir, file.getName());
				updateFile(file, tempFile);
			}
		} else {
			sourceDir.renameTo(tempDir);
		}
	}

	private static void updateFile(File sourceFile, File targetFile) throws IOException {
		if (targetFile.exists()) {
			byte[] sourceContent = readFile(sourceFile);
			if (sourceFile.length() == targetFile.length()) {
				byte[] targetContent = readFile(targetFile);
				for (int i = 0; i < sourceContent.length; i++) {
					if (sourceContent[i] != targetContent[i]) {
						writeFile(targetFile, sourceContent);
						return;
					}
				}
			} else {
				writeFile(targetFile, sourceContent);
			}
		} else {
			sourceFile.renameTo(targetFile);
		}
	}

	public static void delete(File file) {
		if (file.isDirectory()) {
			File[] subFile = file.listFiles();
			if (subFile != null) {
				for (int i = 0; i < subFile.length; i++) {
					delete(subFile[i]);
				}
			}
		}

		file.delete();
	}
}
