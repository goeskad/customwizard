package com.tibco.ert.model.core;

import java.io.File;
import java.util.HashSet;

import org.apache.xmlbeans.XmlObject;

import com.tibco.amxadministrator.command.line.types.Keystore;
import com.tibco.amxadministrator.command.line.types.Permissions;
import com.tibco.amxadministrator.command.line.types.ServiceAssembly;
import com.tibco.amxadministrator.command.line.typesBase.ContainerBase;
import com.tibco.amxadministrator.command.line.typesBase.Enterprise;
import com.tibco.amxadministrator.command.line.typesBase.EnvironmentBase;
import com.tibco.amxadministrator.command.line.typesBase.GroupBase;
import com.tibco.amxadministrator.command.line.typesBase.KeystoreBase;
import com.tibco.amxadministrator.command.line.typesBase.NodeBase;
import com.tibco.amxadministrator.command.line.typesBase.ServiceAssemblyBase;
import com.tibco.amxadministrator.command.line.typesBase.ServiceUnitBase;
import com.tibco.amxadministrator.command.line.typesBase.SharedResourceDefinitionBase;
import com.tibco.amxadministrator.command.line.typesBase.UDDIServerBase;
import com.tibco.amxadministrator.command.line.typesBase.UIElementBase;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.model.matrix.patch.User;
import com.tibco.ert.model.utils.ERTUtil;
import com.tibco.ert.model.utils.ZipUtil;
import com.tibco.matrix.administration.command.line.environment.EnvironmentProcessor;
import com.tibco.matrix.administration.command.line.group.GroupProcessor;
import com.tibco.matrix.administration.command.line.keystore.KeystoreProcessor;
import com.tibco.matrix.administration.command.line.logserviceconfiguration.LogServiceConfigurationProcessor;
import com.tibco.matrix.administration.command.line.machine.MachineProcessor;
import com.tibco.matrix.administration.command.line.sharedresourcedefinition.SharedResourceDefinitionProcessor;
import com.tibco.matrix.administration.command.line.substitutionvariable.SubstitutionVariableProcessor;
import com.tibco.matrix.administration.command.line.uipermissions.UIElementProcessor;
import com.tibco.matrix.administration.command.line.user.UserProcessor;

public class EnterpriseReplicater extends BaseERTJob {
	public EnterpriseReplicater(AdminServicesDelegate delegate) throws Exception {
		this.delegate = delegate;
	}

	public void replicateFromZipFile(String target, File zipFile, boolean startNode, boolean startSA) throws Exception {
		File unZipDir = new File(zipFile.getParent(), ERTUtil.getFileName(zipFile));
		unZipDir.mkdirs();

		try {
			ZipUtil.unzip(zipFile, unZipDir);
			Enterprise enterprise = updateUploadFilePath(unZipDir);
			replicate(target, enterprise, unZipDir);
			
			if (startNode) {
				startNodes(enterprise);
			}
			worked(5);
			
			if (startSA) {
				startServiceAssemblies(enterprise);
			}
			worked(5);
		} catch (Exception e) {
			log.error("Replication error", e);
			throw e;
		} finally {
			ERTUtil.delete(unZipDir);
		}
	}

	/**
	 * execute replication in the target admin server
	 * 
	 * @param target
	 * @param enterprise
	 * @param dataFileDir
	 * @throws Exception
	 */
	public void replicate(String target, Enterprise enterprise, File dataFileDir)
			throws Exception {
		String addAction = "add";
		String editAction = "edit";
		//machine
		updateProgressStatus("Process machine");
		MachineProcessor.processMachines(enterprise, getHashSet(enterprise.getMachineArray()), addAction);
		worked(5);
		//user and group
		updateProgressStatus("Process user and group");
		UserProcessor.processUsers(enterprise, getHashSet(enterprise.getUserArray()), addAction, false, false, true);
		worked(6);
//		SuperUserProcessor.processUsers(enterprise, getHashSet(enterprise.getListOfSuperUserArray()), addAction);
//		worked(3);
		HashSet<XmlObject> groupSet = new HashSet<XmlObject>();
		addGroupSet(groupSet, enterprise.getGroupArray());
		GroupProcessor.processGroups(enterprise, groupSet, addAction, true, true, true);
		worked(8);
		updateUserPassword(dataFileDir);
		worked(1);
		//uipermission
		updateProgressStatus("Process ui permissions");
		HashSet<XmlObject> uiPermissionSet = new HashSet<XmlObject>();
		addUIPermissionSet(uiPermissionSet, enterprise.getUIElementArray());
		UIElementProcessor.processUIElements(enterprise, uiPermissionSet, editAction);
		worked(10);
		//keystore
		updateProgressStatus("Process keystore");
		KeystoreProcessor.processKeystores(enterprise, getHashSet(enterprise.getKeystoreArray()), editAction, true, true, true);
		worked(5);
        //substitutionvariable
		updateProgressStatus("Process substitution variables");
		SubstitutionVariableProcessor.processSubstitutionVariables(enterprise,
				getHashSet(enterprise.getSubstitutionVariableArray()), addAction, true, true, true);
		worked(5);
		//sharedresource
		updateProgressStatus("Process sharedresources");
		SharedResourceDefinitionProcessor.processSharedResourceDefinitions(enterprise,
				getHashSet(enterprise.getSharedResourceDefinitionArray()), editAction, true, true, true);
		worked(5);
		//logservice
		if (enterprise.getAdminCluster() != null && enterprise.getAdminCluster().getLogServiceConfiguration() != null) {
			updateProgressStatus("Process logservice");
			HashSet<XmlObject> logserviceSet = new HashSet<XmlObject>();
			logserviceSet.add(enterprise.getAdminCluster().getLogServiceConfiguration());
			LogServiceConfigurationProcessor.processLogServiceConfiguration(enterprise.getAdminCluster(),
					logserviceSet, editAction, true);
		}
		worked(5);
		//environment
		EnvironmentBase[] environments = enterprise.getEnvironmentArray();
		for (EnvironmentBase environment : environments) {
			updateProgressStatus("Process environment[" + environment.getName() + "]");
			processEnvironment(enterprise, environment);
		}
	}

	private void startNodes(Enterprise enterprise) {
		EnvironmentBase[] environments = enterprise.getEnvironmentArray();
		for (EnvironmentBase environment : environments) {
			HashSet<XmlObject> nodeSet = new HashSet<XmlObject>();
			NodeBase[] nodes = environment.getNodeArray();
			for (NodeBase node : nodes) {
				nodeSet.add(node);
			}
			updateProgressStatus("Install nodes");
			EnvironmentProcessor.processEnvironments(enterprise, nodeSet, "install", true, true, true, true);
			updateProgressStatus("Start nodes");
			EnvironmentProcessor.processEnvironments(enterprise, nodeSet, "start", true, true, true, true);
		}
	}

	private void startServiceAssemblies(Enterprise enterprise) {
		EnvironmentBase[] environments = enterprise.getEnvironmentArray();
		for (EnvironmentBase environment : environments) {
			HashSet<XmlObject> serviceAssemblySet = new HashSet<XmlObject>();
			ServiceAssemblyBase[] serviceAssemblies = environment.getServiceAssemblyArray();
			for (ServiceAssemblyBase serviceAssembly : serviceAssemblies) {
				serviceAssemblySet.add(serviceAssembly);
			}
			updateProgressStatus("Deploy service assembly");
			EnvironmentProcessor.processEnvironments(enterprise, serviceAssemblySet, "deploy", true, true, true, true);
			updateProgressStatus("Start service assembly");
			EnvironmentProcessor.processEnvironments(enterprise, serviceAssemblySet, "start", true, true, true, true);
		}
	}
	
	private void processEnvironment(Enterprise enterprise, EnvironmentBase environment) {
		HashSet<XmlObject> addSet = new HashSet<XmlObject>();
		HashSet<XmlObject> editSet = new HashSet<XmlObject>();
		
		addSet.add(environment);
		editSet.add(environment.getPermissions());
		addToSet(addSet, environment.getMachineArray());
		if (environment.getMessagingBus() != null) {
			addToSet(addSet, environment.getMessagingBus().getMessagingServerArray());
		}
		addToSet(addSet, environment.getSharedResourceDefinitionArray());
		//node
		NodeBase[] nodes = environment.getNodeArray();
		for (NodeBase node : nodes) {
			addSet.add(node);
			editSet.add(node.getPermissions());
//			editSet.add(node.getLogger());
			
			addToSet(addSet, node.getSharedResourceArray());
			editSet.add(node.getDefaultConnector());
			addToSet(editSet, node.getSubstitutionVariableArray());
			ContainerBase[] containers = node.getContainerArray();
			addToSet(editSet, containers);
//			for (ContainerBase container : containers) {
//				editSet.add(container.getLogger());
//			}
		}
		//serviceassembly
		ServiceAssemblyBase[] serviceAssemblies = environment.getServiceAssemblyArray();
		for (ServiceAssemblyBase serviceAssembly : serviceAssemblies) {
			addSet.add(serviceAssembly);
			editSet.add(serviceAssembly.getPermissions());
//			editSet.add(serviceAssembly.getLogger());
			ServiceUnitBase[] serviceUnits = serviceAssembly.getServiceUnitArray();
			for (ServiceUnitBase serviceUnit : serviceUnits) {
				addToSet(addSet, serviceUnit.getNodeArray());
				addToSet(editSet, serviceUnit.getSubstitutionVariableArray());
			}
			addToSet(editSet, serviceAssembly.getSharedResourceProfileArray());
			addToSet(editSet, serviceAssembly.getSubstitutionVariableArray());
			addToSet(editSet, serviceAssembly.getTopicArray());
			addToSet(editSet, serviceAssembly.getServiceArray());
		}
		
		EnvironmentProcessor.processEnvironments(enterprise, addSet, "add", true, true, true, true);
		worked(20);
		EnvironmentProcessor.processEnvironments(enterprise, editSet, "edit", true, true, true, true);
		worked(20);
	}
	
	private void addGroupSet(HashSet<XmlObject> objects, GroupBase[] groups) {
		if (groups != null) {
			for (GroupBase group : groups) {
				objects.add(group);
				addToSet(objects, group.getUserArray());
				addGroupSet(objects, group.getGroupArray());
			}
		}
	}
	
	private void addUIPermissionSet(HashSet<XmlObject> objects, UIElementBase[] uiElements) {
		if (uiElements != null) {
			for (UIElementBase uiElement : uiElements) {
				objects.add(uiElement.getPermissions());
				addUIPermissionSet(objects, uiElement.getUIElementArray());
			}
		}
	}

	private void addPermissionSet(HashSet<XmlObject> objects, XmlObject object) {
		Permissions permissions = null;
		if (object instanceof EnvironmentBase) {
			permissions = ((EnvironmentBase) object).getPermissions();
		} else if (object instanceof NodeBase) {
			permissions = ((NodeBase) object).getPermissions();
		} else if (object instanceof SharedResourceDefinitionBase) {
			permissions = ((SharedResourceDefinitionBase) object).getPermissions();
		} else if (object instanceof ServiceAssemblyBase) {
			permissions = ((ServiceAssemblyBase) object).getPermissions();
		} else if (object instanceof KeystoreBase) {
			permissions = ((KeystoreBase) object).getPermissions();
		} else if (object instanceof UDDIServerBase) {
			permissions = ((UDDIServerBase) object).getPermissions();
		} else if (object instanceof UIElementBase) {
			permissions = ((UIElementBase) object).getPermissions();
		}

		if (permissions != null) {
			objects.add(permissions);
		}
	}

	private HashSet<XmlObject> getHashSet(XmlObject[] array) {
		HashSet<XmlObject> objects = new HashSet<XmlObject>();
		addToSet(objects, array);
		return objects;
	}

	private void addToSet(HashSet<XmlObject> objects, XmlObject[] array) {
		for (XmlObject xmlObject : array) {
			objects.add(xmlObject);
			addPermissionSet(objects, xmlObject);
		}
	}
	
	private Enterprise updateUploadFilePath(File dataFileDir) throws Exception {
		Enterprise enterprise = ERTUtil.loadSeparateEnterprise(dataFileDir);

		// keystore file
		KeystoreBase[] keystores = enterprise.getKeystoreArray();
		for (KeystoreBase base : keystores) {
			Keystore keystore = (Keystore) base;
			if (keystore.getFileLocation() != null) {
				keystore.setFileLocation(new File(dataFileDir, keystore.getFileLocation()).getAbsolutePath());
			}
		}
		enterprise.setKeystoreArray(keystores);

		// sa file
		EnvironmentBase[] environments = enterprise.getEnvironmentArray();
		for (EnvironmentBase environment : environments) {
			ServiceAssemblyBase[] serviceAssemblies = environment.getServiceAssemblyArray();
			for (ServiceAssemblyBase serviceAssemblyBase : serviceAssemblies) {
				ServiceAssembly serviceAssembly = (ServiceAssembly) serviceAssemblyBase;
				serviceAssembly.setArchivePath(new File(dataFileDir, serviceAssembly.getArchivePath())
						.getAbsolutePath());
			}
			environment.setServiceAssemblyArray(serviceAssemblies);
		}
		enterprise.setEnvironmentArray(environments);

		return enterprise;
	}

	private void updateUserPassword(File dataFileDir) throws Exception {
		File file = new File(dataFileDir, USER_PASSWORD_FILE);

		if (file.exists()) {
			updateProgressStatus("Updating the users password by USER_PASSWORD_FILE");
			User[] users = (User[]) ERTUtil.readObject(file);
			int[] result = getDbDelegate().updateUserPassword(users);
			for (int i = 0; i < result.length; i++) {
				log.info("Update user [" + users[i].getName() + "] " + result[i]);
			}
		}
	}
}
