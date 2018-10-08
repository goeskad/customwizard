package com.tibco.ert.model.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import com.tibco.amxadministrator.command.line.types.DefaultConnector;
import com.tibco.amxadministrator.command.line.types.Environment;
import com.tibco.amxadministrator.command.line.types.Group;
import com.tibco.amxadministrator.command.line.types.Keystore;
import com.tibco.amxadministrator.command.line.types.ListOfSuperUser;
import com.tibco.amxadministrator.command.line.types.Machine;
import com.tibco.amxadministrator.command.line.types.MessagingBus;
import com.tibco.amxadministrator.command.line.types.MessagingServer;
import com.tibco.amxadministrator.command.line.types.Node;
import com.tibco.amxadministrator.command.line.types.Permissions;
import com.tibco.amxadministrator.command.line.types.Service;
import com.tibco.amxadministrator.command.line.types.ServiceAssembly;
import com.tibco.amxadministrator.command.line.types.SharedResourceDefinition;
import com.tibco.amxadministrator.command.line.types.SubstitutionVariable;
import com.tibco.amxadministrator.command.line.types.Topic;
import com.tibco.amxadministrator.command.line.types.User;
import com.tibco.amxadministrator.command.line.typesBase.AdminClusterBase;
import com.tibco.amxadministrator.command.line.typesBase.ContainerBase;
import com.tibco.amxadministrator.command.line.typesBase.Enterprise;
import com.tibco.amxadministrator.command.line.typesBase.KeystoreBase;
import com.tibco.amxadministrator.command.line.typesBase.LogServiceConfigurationBase;
import com.tibco.amxadministrator.command.line.typesBase.LoggerBase;
import com.tibco.amxadministrator.command.line.typesBase.ModelListType;
import com.tibco.amxadministrator.command.line.typesBase.ServiceUnitBase;
import com.tibco.amxadministrator.command.line.typesBase.SharedResourceProfileBase;
import com.tibco.amxadministrator.command.line.typesBase.UIElementBase;
import com.tibco.amxadministrator.command.line.typesDetailed.CertStore;
import com.tibco.amxadministrator.command.line.typesDetailed.CertifiedType;
import com.tibco.amxadministrator.command.line.typesDetailed.CipherStrength;
import com.tibco.amxadministrator.command.line.typesDetailed.DirectConfiguration;
import com.tibco.amxadministrator.command.line.typesDetailed.DistributedQueueType;
import com.tibco.amxadministrator.command.line.typesDetailed.GroupAndPermission;
import com.tibco.amxadministrator.command.line.typesDetailed.HTTPSharedResourceDefinition;
import com.tibco.amxadministrator.command.line.typesDetailed.IdentitySharedResourceDefinition;
import com.tibco.amxadministrator.command.line.typesDetailed.JDBCConnectionType;
import com.tibco.amxadministrator.command.line.typesDetailed.JDBCSharedResourceDefinition;
import com.tibco.amxadministrator.command.line.typesDetailed.JMSSharedResourceDefinition;
import com.tibco.amxadministrator.command.line.typesDetailed.JNDIConfiguration;
import com.tibco.amxadministrator.command.line.typesDetailed.JNDIConnectionType;
import com.tibco.amxadministrator.command.line.typesDetailed.JNDIProperty;
import com.tibco.amxadministrator.command.line.typesDetailed.JNDISharedResourceDefinition;
import com.tibco.amxadministrator.command.line.typesDetailed.ListOfSubstitutionBinding;
import com.tibco.amxadministrator.command.line.typesDetailed.PublicKeyType;
import com.tibco.amxadministrator.command.line.typesDetailed.ReliableType;
import com.tibco.amxadministrator.command.line.typesDetailed.RendezvousSharedResourceDefinition;
import com.tibco.amxadministrator.command.line.typesDetailed.SSLServerSharedResourceDefinition;
import com.tibco.amxadministrator.command.line.typesDetailed.SubstitutionBinding;
import com.tibco.amxadministrator.command.line.typesDetailed.UserAndPermission;
import com.tibco.amxadministrator.command.line.typesReference.GroupReference;
import com.tibco.amxadministrator.command.line.typesReference.KeystoreEntryReference;
import com.tibco.amxadministrator.command.line.typesReference.MachineReference;
import com.tibco.amxadministrator.command.line.typesReference.MessagingServerReference;
import com.tibco.amxadministrator.command.line.typesReference.NodeReference;
import com.tibco.amxadministrator.command.line.typesReference.SharedResourceDefinitionReference;
import com.tibco.amxadministrator.command.line.typesReference.UserReference;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.model.matrix.patch.PatchModel;
import com.tibco.ert.model.matrix.patch.ServiceAssemblyPatch;
import com.tibco.ert.model.utils.ERTUtil;
import com.tibco.ert.model.utils.ZipUtil;
import com.tibco.matrix.admin.server.services.adminconfiguration.types.ClusterConfigurationDetails;
import com.tibco.matrix.admin.server.services.components.types.ComponentDetails;
import com.tibco.matrix.admin.server.services.deployment.types.ResourceProfileResourceDefinitionBinding;
import com.tibco.matrix.admin.server.services.deployment.types.ServiceAssemblySummary;
import com.tibco.matrix.admin.server.services.deployment.types.ServiceAssemblyWithSUInfo;
import com.tibco.matrix.admin.server.services.deployment.types.ServiceSummary;
import com.tibco.matrix.admin.server.services.deployment.types.ServiceUnitSummary;
import com.tibco.matrix.admin.server.services.deployment.types.TopicDefinitionSummary;
import com.tibco.matrix.admin.server.services.environment.types.EnvironmentSummary;
import com.tibco.matrix.admin.server.services.environment.types.MessagingServerDetails;
import com.tibco.matrix.admin.server.services.inventory.types.MachineProducts;
import com.tibco.matrix.admin.server.services.inventory.types.Product;
import com.tibco.matrix.admin.server.services.loggingconfiguration.types.Logger;
import com.tibco.matrix.admin.server.services.loggingconfiguration.types.LoggingConfiguration;
import com.tibco.matrix.admin.server.services.loggingconfiguration.types.NodeLoggingDetails;
import com.tibco.matrix.admin.server.services.loggingconfiguration.types.SALoggingDetails;
import com.tibco.matrix.admin.server.services.managedresources.types.MgdResNodeAssociation;
import com.tibco.matrix.admin.server.services.managementdaemon.types.ManagementDaemonInfo;
import com.tibco.matrix.admin.server.services.matrixcommon.types.EntityNameAndID;
import com.tibco.matrix.admin.server.services.matrixcommon.types.Version;
import com.tibco.matrix.admin.server.services.node.types.NodeDetails;
import com.tibco.matrix.admin.server.services.plugins.types.Data;
import com.tibco.matrix.admin.server.services.plugins.types.Record;
import com.tibco.matrix.admin.server.services.sharedresources.types.CertStoreConfiguration;
import com.tibco.matrix.admin.server.services.sharedresources.types.EmsResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.HttpResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.IdentityResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.JdbcJndiType;
import com.tibco.matrix.admin.server.services.sharedresources.types.JdbcResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.JdbcSimpleType;
import com.tibco.matrix.admin.server.services.sharedresources.types.JndiProperty;
import com.tibco.matrix.admin.server.services.sharedresources.types.JndiResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.KeyEntryConfiguration;
import com.tibco.matrix.admin.server.services.sharedresources.types.PublicKeyIdentityType;
import com.tibco.matrix.admin.server.services.sharedresources.types.RVCertifiedType;
import com.tibco.matrix.admin.server.services.sharedresources.types.RVDistributedQueueType;
import com.tibco.matrix.admin.server.services.sharedresources.types.RVResourceDetails;
import com.tibco.matrix.admin.server.services.sharedresources.types.ResourceSummary;
import com.tibco.matrix.admin.server.services.sharedresources.types.SSLServerResourceDetails;
import com.tibco.matrix.admin.server.services.svar.types.LocalSubstitutionVariable;
import com.tibco.matrix.admin.server.services.usermgmt.types.GroupInfo;
import com.tibco.matrix.admin.server.services.usermgmt.types.MemberUserInfo;
import com.tibco.matrix.admin.server.services.usermgmt.types.UserInfo;
import com.tibco.security.ObfuscationEngine;

public class EnterpriseExtractor extends BaseERTJob {
	public static final String ZIP_FILE_SUFFIX = "Enterprise";

    private PatchModel patchModel;

    private List<Exception> errors;
    
    private String[] targets;
    
    public EnterpriseExtractor(AdminServicesDelegate delegate) throws Exception {
        this.delegate = delegate;
    }

    public File extractToZipFile(String target, File extractionDir) throws Exception {
        Enterprise enterprise = extract(target);
        File tempDir = new File(extractionDir, ERTUtil.getTimestamp() + "_tmp");
        tempDir.mkdirs();

        try {
            // download keystore
            KeystoreBase[] keystores = enterprise.getKeystoreArray();
            for (KeystoreBase base : keystores) {
                Keystore keystore = (Keystore) base;
                if (keystore.getFileLocation() != null) {
                    updateProgressStatus("Download keystore(" + keystore.getName() + ")");
                    try {
						downloadFile(keystore.getURL(), keystore.getFileLocation(), tempDir);
					} catch (Exception e) {
						errors.add(e);
						updateProgressStatus("Download keystore(" + keystore.getName() + ") failed, cause: " + e.toString());
					}
                    keystore.unsetURL();
                }
            }
            worked(5);
            
            // download sa
			String adminUrl = delegate.getAccessConfig().getAdminUrl();
			List<ServiceAssemblyPatch> serviceAssemblyPatchList = patchModel.getServiceAssemblyPatchList();
			for (ServiceAssemblyPatch serviceAssemblyPatch : serviceAssemblyPatchList) {
				updateProgressStatus("Download service assembly(" + serviceAssemblyPatch.getName() + ")");
				try {
					downloadFile(adminUrl + ".httpbasic/safetch?saId=" + serviceAssemblyPatch.getId() + "&saFileName="
							+ serviceAssemblyPatch.getFileName(), serviceAssemblyPatch.getFileName(), tempDir);
				} catch (Exception e) {
					errors.add(e);
					updateProgressStatus("Download service assembly(" + serviceAssemblyPatch.getName()
							+ ") failed, cause: " + e.toString());
				}
			}
			worked(5);
			
			ERTUtil.saveSeparateEnterprise(tempDir, enterprise);
            if (patchModel.getDbUsers() != null) {
                ERTUtil.writeObject(patchModel.getDbUsers(), new File(tempDir, "users.txt"));
            }
            
			String zipFileName = delegate.getAccessConfig().getHost() + "-" + delegate.getAccessConfig().getPort()
					+ "-" + ERTUtil.getTimestamp() + "_" + ZIP_FILE_SUFFIX + ".zip";
			File zipFile = new File(extractionDir, zipFileName);
			ZipUtil.zip(tempDir, zipFile.getAbsolutePath());

			String message = "Extracted source amx to " + zipFile.getAbsolutePath();
			if (hasErrors()) {
				message = message + "(with errors)";
			}
			updateProgressStatus(message);
			
			ERTUtil.updateTempDir(tempDir);

            return zipFile;
		} catch (Exception e) {
			log.error("Extraction error", e);
			throw e;
		} finally {
			ERTUtil.delete(tempDir);
		}
    }

    public Enterprise extract(String target) throws Exception{
        Enterprise enterprise = Enterprise.Factory.newInstance();
        patchModel = new PatchModel();
        errors = new ArrayList<Exception>();
        target = target == null ? "" : target;
        targets = target.split("\\|");
        for (int i = 0; i < targets.length; i++) {
        	targets[i] = targets[i].trim();
        	targets[i] = targets[i].replace("\"", "");
		}
        
        updateProgressStatus("Start extracting from " + delegate.getAccessConfig().getHost());

		patchModel.setAllPermissions(getDbDelegate().getAllPermissions());
        
		this.fetchUserAndGroup(enterprise);
		if (isTargetMatch("UIElement")) {
			this.fetchUIElement(enterprise);
		}
		worked(5);
		if (isTargetMatch("LogService")) {
			this.fetchLogServices(enterprise);
		}
		worked(3);
		if (isTargetMatch("Machine")) {
			this.fetchMachine(enterprise);
		}
		worked(3);
		if (isTargetMatch("SubstitutionVariable")) {
			this.fetchSubstitutionVariables(enterprise);
		}
		worked(3);
		if (isTargetMatch("Keystore")) {
			this.fetchKeystore(enterprise);
		}
		worked(3);
		if (isTargetMatch("SharedResourceDefinition")) {
			this.fetchSharedResource(enterprise);
		}
		worked(3);
		if (isTargetMatch("Environment")) {
			this.fetchEnvironment(enterprise);
		}
        return enterprise;
    }

	public boolean hasErrors() {
		return !(errors == null  || errors.isEmpty());
	}

	private boolean isTargetMatch(String type) {
		for (String target : targets) {
			if (target.equals("*") || target.startsWith(type)) {
				return true;
			}
		}
		return false;
	}
	
	private void logExtractError(Exception e) {
		logExtractError(e, 0);
	}

	private void logExtractError(Exception e, int indent) {
		errors.add(e);

		int spaces = indent * 4;
		char[] spaceChars = new char[spaces];
		for (int i = 0; i < spaces; i++) {
			spaceChars[i] = ' ';
		}
		updateProgressStatus(new String(spaceChars) + "Extract " + e.getMessage() + " failed, cause: "
				+ e.getCause().toString());
	}

	private void fetchUserAndGroup(Enterprise enterprise) {
		if (isTargetMatch("User") || isTargetMatch("UserGroup")) {
			fetchUser(enterprise);
		}
		worked(2);
		if (isTargetMatch("SuperUser") || isTargetMatch("UserGroup")) {
			fetchSuperUser(enterprise);
		}
		worked(2);
		if (isTargetMatch("Group") || isTargetMatch("UserGroup")) {
			fetchUserGroup(enterprise);
		}
		worked(6);
	}

	private void fetchUser(Enterprise enterprise) {
		try {
			updateProgressStatus("Extracting users");
			ClusterConfigurationDetails adminConfiguration = delegate.getAdminClusterConfiguration();
			if (adminConfiguration.getCurrentAuthenticationScheme().equals("DatabaseAuthentication")) {
				com.tibco.ert.model.matrix.patch.User[] dbUsers = getDbDelegate().getUsers();
				patchModel.setDbUsers(dbUsers);
				User[] users = new User[dbUsers.length];
				for (int i = 0; i < dbUsers.length; i++) {
					users[i] = User.Factory.newInstance();
					users[i].setUsername(dbUsers[i].getName());
					users[i].setPassword("password");
				}
				enterprise.setUserArray(users);
			} else {
				UserInfo[] soapUsers = delegate.getUsers();
				User[] users = new User[soapUsers.length];
				for (int i = 0; i < soapUsers.length; i++) {
					users[i] = User.Factory.newInstance();
					users[i].setUsername(soapUsers[i].getUsername());
					users[i].setPassword("password");
				}
				enterprise.setUserArray(users);
			}
		} catch (Exception e) {
			logExtractError(new Exception("users", e));
		}
	}

    private void fetchSuperUser(Enterprise enterprise) {
        try {
			updateProgressStatus("Extracting super users");
			String[] superUserNames = delegate.getSuperUsers();
			UserReference[] superUsers = new UserReference[superUserNames.length];
			for (int i = 0; i < superUserNames.length; i++) {
			    superUsers[i] = UserReference.Factory.newInstance();
			    superUsers[i].setUsername(superUserNames[i]);
			}
			ListOfSuperUser listOfSuperUser = ListOfSuperUser.Factory.newInstance();
			listOfSuperUser.setUserArray(superUsers);
			enterprise.setListOfSuperUserArray(new ListOfSuperUser[] { listOfSuperUser });
		} catch (Exception e) {
			logExtractError(new Exception("super users", e));
		}
    }

    private void fetchUserGroup(Enterprise enterprise) {
        try {
			updateProgressStatus("Extracting user groups");
			Group root = Group.Factory.newInstance();
			fetchChildGroups(root, null);
			enterprise.setGroupArray(root.getGroupArray());
		} catch (Exception e) {
			logExtractError(new Exception("user groups", e));
		}
    }

    private void fetchChildGroups(Group group, String parentGroupId) throws Exception {
        GroupInfo[] childSoapGroups = delegate.getChildGroups(parentGroupId);
        if (childSoapGroups.length > 0) {
            Group[] childGroups = new Group[childSoapGroups.length];
            for (int i = 0; i < childSoapGroups.length; i++) {
                childGroups[i] = Group.Factory.newInstance();
                childGroups[i].setName(childSoapGroups[i].getName());
                childGroups[i].setDescription(childSoapGroups[i].getDescription());
                fetchUserInGroup(childGroups[i], childSoapGroups[i].getId());
                fetchChildGroups(childGroups[i], childSoapGroups[i].getId());
                //set group refernece map
                GroupReference groupRef = GroupReference.Factory.newInstance();
                groupRef.setName(childSoapGroups[i].getName());
                patchModel.getGroupMap().put(childSoapGroups[i].getId(), groupRef);
            }
            group.setGroupArray(childGroups);
        }
    }

    private void fetchUserInGroup(Group group, String groupId) throws Exception {
        updateProgressStatus("Extracting users from group(" + group.getName() + ")");
        MemberUserInfo[] soapUsers = delegate.getAllUsersInGroup(groupId);
        List<UserReference> userList = new ArrayList<UserReference>();
        for (MemberUserInfo soapUser : soapUsers) {
            if (soapUser.getIsDirectMember()) {
                UserReference user = UserReference.Factory.newInstance();
                user.setUsername(soapUser.getUsername());
                userList.add(user);
            }
        }
        group.setUserArray(userList.toArray(new UserReference[userList.size()]));
    }

    private void fetchLogServices(Enterprise enterprise) {
        try {
        	updateProgressStatus("Extracting log services");
			List<Map<String, String>> dbList = getDbDelegate().getAllLogServices();
			if (dbList.size() > 0) {
			    AdminClusterBase adminClusterBase = AdminClusterBase.Factory.newInstance();
			    for (Map<String, String> dbLogService : dbList) {
			        LogServiceConfigurationBase localLogService = LogServiceConfigurationBase.Factory.newInstance();
			        String destination = dbLogService.get("DESTINATION");
			        if (destination.equals("0")) {
			            localLogService.setDestination("NO");
			        } else if (destination.equals("1")) {
			            localLogService.setDestination("DB");
			            localLogService.setJDBCSharedResourceName(dbLogService.get("JDBCNAME"));
			        } else if (destination.equals("2")) {
			            localLogService.setDestination("FILE");
			            localLogService.setFileName(dbLogService.get("FILENAME"));
			            localLogService.setMaxSize(Integer.parseInt(dbLogService.get("MAXSIZE")));
			            localLogService.setMaxIndex(Integer.parseInt(dbLogService.get("MAXINDEX")));
			        }
			        localLogService.setLogServiceName(dbLogService.get("NAME"));
			        localLogService.setQueueName(dbLogService.get("EMSQUEUENAME"));
			        localLogService.setJMSSharedResourceName(dbLogService.get("JMSNAME"));

			        List<Map<String, String>> dbModelList = getDbDelegate().getModelList();
			        List<String> modelList = new ArrayList<String>();
			        String logServiceId = dbLogService.get("E_ID");
			        for (Map<String, String> dbModel : dbModelList) {
			            if (dbModel.get("E_ID").equals(logServiceId)) {
			                modelList.add(dbModel.get("NAME"));
			            }
			        }
			        if (modelList.size() > 0) {
			            ModelListType modelListType = ModelListType.Factory.newInstance();
			            modelListType.setModelNameArray(modelList.toArray(new String[modelList.size()]));
			            localLogService.setModelList(modelListType);
			        }

			        adminClusterBase.setLogServiceConfiguration(localLogService);
			        break;
			    }
			    enterprise.setAdminCluster(adminClusterBase);
			}
		} catch (Exception e) {
			logExtractError(new Exception("log services", e));
		}
    }

    private void fetchUIElement(Enterprise enterprise) {
        try {
        	updateProgressStatus("Extracting UI elements and permissions");
        	
			Data data = delegate.buildUIResourceTree();
			Record soapRecord = data.getRecord();
			UIElementBase elementBase = UIElementBase.Factory.newInstance();

			convertRecordToUIElementBase(soapRecord, elementBase);

			// filter ui elment
			UIElementBase[] childElements = elementBase.getUIElementArray();
			for (int i = childElements.length - 1; i >= 0; i--) {
			    filterUIElement(childElements[i], elementBase, i);
			}
			if (isContainPermission(elementBase)) {
			    UIElementBase[] array = new UIElementBase[1];
			    array[0] = elementBase;
			    enterprise.setUIElementArray(array);
			}
		} catch (Exception e) {
			logExtractError(new Exception("UI elements and permissions", e));
		}
    }

    private void convertRecordToUIElementBase(Record soapRecord, UIElementBase elementBase) {
        if (soapRecord != null) {
            elementBase.setName(soapRecord.getDisplayName());
            // set permissions
            String type = "UiResourceType";
            long id = soapRecord.getJsxid();
            Permissions localPermissions = getPermissionsWithTypeAndID(type, id);
            if (localPermissions != null)
                elementBase.setPermissions(localPermissions);

            // handle children
            Record[] childRecords = soapRecord.getRecordArray();
            UIElementBase[] childElementBases = new UIElementBase[childRecords.length];
            for (int i = 0; i < childRecords.length; i++) {
                UIElementBase childElementBase = UIElementBase.Factory.newInstance();
                convertRecordToUIElementBase(childRecords[i], childElementBase);
                childElementBases[i] = childElementBase;
            }

            // set children to parent
            elementBase.setUIElementArray(childElementBases);
        }
    }

    private void filterUIElement(UIElementBase childElementBase, UIElementBase parent, int index) {
        if (childElementBase != null) {
            UIElementBase[] childElementBasesOfchild = childElementBase.getUIElementArray();
            for (int i = childElementBasesOfchild.length - 1; i >= 0; i--)
                filterUIElement(childElementBasesOfchild[i], childElementBase, i);
            if (isContainPermission(childElementBase) == false)
                parent.removeUIElement(index);
        }
    }

    private boolean isContainPermission(UIElementBase elementBase) {
        boolean flag = false;
        UIElementBase[] childElementBases = elementBase.getUIElementArray();
        if (elementBase.isSetPermissions() || childElementBases.length > 0) {
            flag = true;
        }
        return flag;
    }

    private Permissions getPermissionsWithTypeAndID(String type, long id) {
        String typeColumn = "ECONTAINER_CLASS";
        String idColumn = "E_CONTAINER";
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Map<String, String> record : patchModel.getAllPermissions()) {
            if (record.get(typeColumn).equals(type)) {
                String idString = record.get(idColumn);
                if (idString != null) {
                    if (Long.parseLong(idString) == id)
                        list.add(record);
                }
            }
        }
        Permissions localPermissions = null;
        if (list.size() > 0) {
            localPermissions = convertSoapPermissionsToLocalPermissions(list);
        }
        return localPermissions;
    }

    private Permissions convertSoapPermissionsToLocalPermissions(List<Map<String, String>> soapPermissions) {
        Permissions localPermissions = Permissions.Factory.newInstance();
        List<UserAndPermission> userPermissionList = new ArrayList<UserAndPermission>();
        List<GroupAndPermission> groupPermissionList = new ArrayList<GroupAndPermission>();
        for (Map<String, String> permission : soapPermissions) {
            String view = "1";
            String edit = "3";
            String owner = "11";
            String permissionType = permission.get("CAPABILITYBITMASK");
            if (permission.get("IDENTITYTYPE").equals("USERNAME")) {
                UserAndPermission userPermission = UserAndPermission.Factory.newInstance();
                UserAndPermission.Permission.Enum permissionValue = null;
                if (permissionType.equals(view)) {
                    permissionValue = UserAndPermission.Permission.VIEW;
                } else if (permissionType.equals(edit)) {
                    permissionValue = UserAndPermission.Permission.EDIT;
                } else if (permissionType.equals(owner)) {
                    permissionValue = UserAndPermission.Permission.OWNER;
                }
                userPermission.setPermission(permissionValue);
                UserReference user = UserReference.Factory.newInstance();
                user.setUsername(permission.get("IDENTITY"));
                userPermission.setUser(user);
                userPermissionList.add(userPermission);
            } else {
                GroupAndPermission groupPermission = GroupAndPermission.Factory.newInstance();
                GroupAndPermission.Permission.Enum permissionValue = null;
                if (permissionType.equals(view)) {
                    permissionValue = GroupAndPermission.Permission.VIEW;
                } else if (permissionType.equals(edit)) {
                    permissionValue = GroupAndPermission.Permission.EDIT;
                } else if (permissionType.equals(owner)) {
                    permissionValue = GroupAndPermission.Permission.OWNER;
                }
                groupPermission.setPermission(permissionValue);
                String groupId = permission.get("IDENTITY");
                groupPermission.setGroup(patchModel.getGroupMap().get(groupId));
                groupPermissionList.add(groupPermission);
            }
        }
        localPermissions.setUserAndPermissionArray(userPermissionList.toArray(new UserAndPermission[userPermissionList.size()]));
        localPermissions.setGroupAndPermissionArray(groupPermissionList.toArray(new GroupAndPermission[groupPermissionList.size()]));

        return localPermissions;
    }

    private void fetchMachine(Enterprise enterprise) {
        try {
			updateProgressStatus("Extracting machines");

			ManagementDaemonInfo[] soapMachines = delegate.getMachines();
			Machine[] machines = new Machine[soapMachines.length];
			for (int i = 0; i < soapMachines.length; i++) {
			    ManagementDaemonInfo soapMachine = soapMachines[i];
			    Machine machine = Machine.Factory.newInstance();
			    machines[i] = machine;

			    //convert machine info
			    machine.setHostName(soapMachine.getHostName());
			    machine.setTibcoHome(soapMachine.getTibcoHome());
			    machine.setManagementURL(soapMachine.getManagementJMXUrl());
			}
			enterprise.setMachineArray(machines);
		} catch (Exception e) {
			logExtractError(new Exception("machines", e));
		}
    }

    private void fetchSubstitutionVariables(Enterprise enterprise) {
        try {
        	updateProgressStatus("Extracting substitution variables");
			com.tibco.matrix.admin.server.services.svar.types.SubstitutionVariable[] soapSVars = delegate.getSVarsInEnterprise();
			SubstitutionVariable[] sVars = new SubstitutionVariable[soapSVars.length];
			for (int i = 0; i < soapSVars.length; i++) {
			    sVars[i] = convertSVar(soapSVars[i]);
			}
			enterprise.setSubstitutionVariableArray(sVars);
		} catch (Exception e) {
			logExtractError(new Exception("substitution variables", e));
		}
    }

    private void fetchKeystore(Enterprise enterprise) {
        try {
			updateProgressStatus("Extracting keystores");
			List<Map<String, String>> keystoreList = getDbDelegate().getAllKeystores();
			Keystore[] keystores = new Keystore[keystoreList.size()];
			for (int i = 0; i < keystores.length; i++) {
			    Map<String, String> dbKeystore = keystoreList.get(i);
			    keystores[i] = Keystore.Factory.newInstance();
			    keystores[i].setName(dbKeystore.get("NAME"));
			    keystores[i].setDescription(dbKeystore.get("DESCRIPTION"));
			    keystores[i].setPassword(new String(ObfuscationEngine.decrypt(dbKeystore.get("PASSWORD"))));
			    String url = dbKeystore.get("STOREURL");
			    keystores[i].setURL(url);
			    int index = url.indexOf("?name=");
			    if (index > 0 && url.substring(index + 6).equals(keystores[i].getName())) {
			        keystores[i].setFileLocation(keystores[i].getName());
			    }
			    
			    //get permissions
			    String keystoreType = "Keystore";
			    Permissions localPermissions = getPermissionsWithTypeAndID(keystoreType, Long.parseLong(dbKeystore.get("ID")));
			    if (localPermissions != null)
			        keystores[i].setPermissions(localPermissions);
			}
			enterprise.setKeystoreArray(keystores);
		} catch (Exception e) {
			logExtractError(new Exception("keystores", e));
		}
    }

	private void fetchEnvironment(Enterprise enterprise) {
		EnvironmentSummary[] soapEnvironments = null;
		try {
			updateProgressStatus("Extracting environments");

			soapEnvironments = delegate.getEnvironments();
		} catch (Exception e) {
			logExtractError(new Exception("environments", e));
		}
		
		if (soapEnvironments == null || soapEnvironments.length == 0) {
			worked(60);
			return;
		}
		
		int eachEnv = 60 / soapEnvironments.length;
		int leftWork = 60 - eachEnv * soapEnvironments.length;

		List<Environment> environmentList = new ArrayList<Environment>(soapEnvironments.length);
		boolean fetchAll = false;
		for (String target : targets) {
			if (target.equals("*") || target.equals("Environment")) {
				fetchAll = true;
				break;
			}
		}

		for (int i = 0; i < soapEnvironments.length; i++) {
			EnvironmentSummary soapEnvironment = soapEnvironments[i];
			String nameMatch = "Environment[@name=" + soapEnvironment.getName() + "]";
			// check if the environment match the target
			boolean fetchThis = fetchAll;
			boolean fetchNode = fetchAll;
			boolean fetchServiceAssembly = fetchAll;
			if (!fetchAll) {
				for (String target : targets) {
					if (target.equals(nameMatch)) {
						fetchThis = true;
					} else if (target.startsWith(nameMatch + "/Node") || target.startsWith("Environment/Node")) {
						fetchNode = true;
					} else if (target.startsWith(nameMatch + "/ServiceAssembly")
							|| target.startsWith("Environment/ServiceAssembly")) {
						fetchServiceAssembly = true;
					}
				}
			}
			Environment environment = Environment.Factory.newInstance();

			// convert enviroment info
			environment.setName(soapEnvironment.getName());
			environment.setDescription(soapEnvironment.getDescription());

			long environmentId = soapEnvironments[i].getEntityId();
			int eachPartOfEnv = eachEnv / 5;
			int leftWorkOfEnv = eachEnv - 5 * eachPartOfEnv;
			// fetch related
			if (fetchThis) {
				fetchMachineInEnvironment(environment, environmentId);
				fetchSharedResourceInEnvironment(environment, environmentId);
				fetchMessagingBusInEnvironment(environment, environmentId);
			}
			worked(eachPartOfEnv);

			if (fetchNode) {
				fetchNode(environment, environmentId);
			}
			worked(eachPartOfEnv * 2);

			if (fetchServiceAssembly) {
				fetchServiceAssembly(environment, environmentId);
			}
			worked(eachPartOfEnv * 2);

			if (fetchThis || (fetchNode && environment.getNodeArray().length > 0)
					|| (fetchServiceAssembly && environment.getServiceAssemblyArray().length > 0)) {
				if (fetchThis) {
					// get permissions
					String environmentType = "Environment";
					Permissions localPermissions = getPermissionsWithTypeAndID(environmentType, environmentId);
					if (localPermissions != null)
						environment.setPermissions(localPermissions);
				}
				environmentList.add(environment);

				if (!fetchAll && targets.length == 1 && targets[0].contains("[@name=")) {
					break;
				}
			}
			worked(leftWorkOfEnv);
		}
		enterprise.setEnvironmentArray(environmentList.toArray(new Environment[environmentList.size()]));
		worked(leftWork);
	}

    private void fetchMachineInEnvironment(Environment environment, long environmentId) {
        try {
			updateProgressStatus("Extracting machines info from environment(" + environment.getName() + ")");

			MachineProducts[] soapMachines;
			try {
			    soapMachines = delegate.getMachinesInEnvironment(environmentId);
			} catch (com.tibco.matrix.administration.command.line.client.inventory.OperationFault ex) {
			    updateProgressStatus("None machine is associated to environment(" + environment.getName() + ")");
			    return;
			}

			List<MachineReference> machineList = new ArrayList<MachineReference>();
			for (int i = 0; i < soapMachines.length; i++) {
			    MachineProducts soapMachine = soapMachines[i];
			    if (soapMachine.getProductArray().length > 0) {
			        MachineReference machine = MachineReference.Factory.newInstance();
			        //convert machine info
			        machine.setHostName(soapMachine.getHostName());
			        Product product = soapMachine.getProductArray()[0];
			        machine.setTibcoHome(product.getTibcoHome());
			        machineList.add(machine);
			    }
			}
			environment.setMachineArray(machineList.toArray(new MachineReference[machineList.size()]));
		} catch (Exception e) {
			logExtractError(new Exception("machines info from environment(" + environment.getName() + ")", e), 1);
		}
    }

    private void fetchSharedResourceInEnvironment(Environment environment, long environmentId) {
        try {
			updateProgressStatus("Extracting shared resouces info from environment(" + environment.getName() + ")");
			ResourceSummary[] soapSharedresources = delegate.getSharedresourcesInEnvironment(environmentId);
			SharedResourceDefinitionReference[] sharedresources = new SharedResourceDefinitionReference[soapSharedresources.length];
			for (int i = 0; i < soapSharedresources.length; i++) {
			    sharedresources[i] = SharedResourceDefinitionReference.Factory.newInstance();
			    sharedresources[i].setName(soapSharedresources[i].getName());
			}
			environment.setSharedResourceDefinitionArray(sharedresources);
		} catch (Exception e) {
			logExtractError(new Exception("shared resouces info from environment(" + environment.getName() + ")", e), 1);
		}
    }

    private void fetchMessagingBusInEnvironment(Environment environment, long environmentId) {
        try {
			updateProgressStatus("Extracting messaging bus info from environment(" + environment.getName() + ")");
			MessagingBus messagingBus = MessagingBus.Factory.newInstance();
			environment.setMessagingBus(messagingBus);
			MessagingServerDetails[] soapMessagingServers = delegate.getMessagingServersInEnvironment(environmentId);
			MessagingServer[] messagingServers = new MessagingServer[soapMessagingServers.length];
			for (int i = 0; i < messagingServers.length; i++) {
			    MessagingServerDetails soapMessagingServer = soapMessagingServers[i];
			    MessagingServer messagingServer = MessagingServer.Factory.newInstance();
			    messagingServers[i] = messagingServer;
			    //convert messaging server info
			    messagingServer.setName(soapMessagingServer.getName());
			    messagingServer.setDescription(soapMessagingServer.getDescription());
			    messagingServer.setUsername(soapMessagingServer.getUsername());
			    messagingServer.setPassword(soapMessagingServer.getPassword());
			    if (soapMessagingServer.getDirectConfiguration() != null) {
			        com.tibco.matrix.admin.server.services.sharedresources.types.EmsResourceConfig.DirectConfiguration soapDirectConfiguration = soapMessagingServer.getDirectConfiguration();
			        DirectConfiguration directConfiguration = DirectConfiguration.Factory.newInstance();
			        directConfiguration.setProviderURL(soapDirectConfiguration.getProviderURL());
			        directConfiguration.setConnectionFactory(soapDirectConfiguration.getTopicConnectionFactory());
			        messagingServer.setDirectConfiguration(directConfiguration);
			    } else {
			        com.tibco.matrix.admin.server.services.sharedresources.types.EmsResourceConfig.JNDIConfiguration soapJndiConfiguration = soapMessagingServer.getJNDIConfiguration();
			        JNDIConfiguration jndiConfiguration = JNDIConfiguration.Factory.newInstance();
			        SharedResourceDefinitionReference jndiReference = SharedResourceDefinitionReference.Factory.newInstance();
			        jndiReference.setName(soapJndiConfiguration.getJNDISharedConfigName());
			        jndiConfiguration.setJNDISharedResourceDefinition(jndiReference);
			        jndiConfiguration.setConnectionFactory(soapJndiConfiguration.getTopicConnectionFactory());
			        jndiConfiguration.setAdminConnectionFactorySSLPwd(soapJndiConfiguration.getConnectionFactorySSLPwd());
			        messagingServer.setJNDIConfiguration(jndiConfiguration);
			    }
			}
			messagingBus.setMessagingServerArray(messagingServers);
			environment.setMessagingBus(messagingBus);
		} catch (Exception e) {
			logExtractError(new Exception("messaging bus info from environment(" + environment.getName() + ")", e), 1);
		}
    }

    private void fetchNode(Environment environment, long environmentId)  {
        try {
			updateProgressStatus("Extracting nodes info from environment(" + environment.getName() + ")");
			NodeDetails[] soapNodes = delegate.getNodes(environmentId);
			List<Node> nodeList = new ArrayList<Node>(soapNodes.length);
			String nameMatch = "Environment[@name=" + environment.getName() + "]/Node";
			for (int i = 0; i < soapNodes.length; i++) {
				NodeDetails soapNode = soapNodes[i];
			    
				boolean fetchThis = false;
				for (String target : targets) {
					if (target.equals("*") || target.equals("Environment/Node") || target.equals(nameMatch)
							|| target.equals(nameMatch + "[@name=" + soapNode.getName() + "]")
							|| target.equals("Environment/Node[@name=" + soapNode.getName() + "]")) {
						fetchThis = true;
						break;
					}
				}
				
				if (fetchThis) {
					Node node = Node.Factory.newInstance();

					// convert node info
					node.setName(soapNode.getName());
					node.setDescription(soapNode.getDescription());
					node.setHostName(soapNode.getNodeData().getHostName());
					node.setTibcoHome(soapNode.getNodeData().getTibcoHome());
					node.setManagementPort(soapNode.getNodeData().getNamingport());
					node.setRunAsNTService(soapNode.getNodeData().getRunAsNTservice());
					MessagingServerReference messagingServer = MessagingServerReference.Factory.newInstance();
					messagingServer.setName(soapNode.getNodeData().getTransportConfigurationName());
					node.setMessagingServer(messagingServer);
					// todo this property is related the target machine
					node.setProductInstallDirectory(soapNode.getNodeData().getProductInstallDirectory());
					Version version = soapNode.getNodeData().getProductVersion();
					node.setProductVersion(version.getMajor() + "." + version.getMinor() + "."
							+ version.getMaintenance() + "." + version.getFragment());

					// fetch related
					try {
						fetchDefaultConnectorInNode(node, soapNode.getPlatformSharedConfig().getEntityID());
						fetchSharedResourceInNode(node, soapNode.getEntityId());
						fetchNodeComponentsAndLoggers(node, soapNode.getEntityId());
						fetchSubstitutionVariablesInNode(node, soapNode.getEntityId());
					} catch (Exception e) {
						logExtractError(
								new Exception("node(" + node.getName() + ") from environment(" + environment.getName()
										+ ")", e), 1);
					}
					
					// get permission
					String nodeType = "MatrixNode";
					Permissions localPermissions = getPermissionsWithTypeAndID(nodeType, soapNode.getEntityId());
					if (localPermissions != null)
						node.setPermissions(localPermissions);

					nodeList.add(node);
				}
			}
			environment.setNodeArray(nodeList.toArray(new Node[nodeList.size()]));
		} catch (Exception e) {
			logExtractError(new Exception("nodes info from environment(" + environment.getName() + ")", e), 1);
		}
    }

    private void fetchSubstitutionVariablesInNode(Node node, long nodeId) throws Exception {
        LocalSubstitutionVariable[] soapSVars = delegate.getSVarsInNode(nodeId);
        SubstitutionVariable[] sVars = new SubstitutionVariable[soapSVars.length];
        for (int i = 0; i < soapSVars.length; i++) {
            sVars[i] = convertSVar(soapSVars[i]);
        }

        node.setSubstitutionVariableArray(sVars);
    }

    private void fetchDefaultConnectorInNode(Node node, long defaultConnectorId) throws Exception {
        updateProgressStatus("Extracting default connector info from node(" + node.getName() + ")");
        HTTPSharedResourceDefinition httpResource = convertHttpResource(delegate.getHttpResourceDetails(defaultConnectorId));
        DefaultConnector defaultConnector = DefaultConnector.Factory.newInstance();
        defaultConnector.setHTTPSharedResourceDefinition(httpResource);
        defaultConnector.setName("defaultHttpConnector");
        node.setDefaultConnector(defaultConnector);
    }

    private void fetchSharedResourceInNode(Node node, long nodeId) throws Exception {
        updateProgressStatus("Extracting shared resources info from node(" + node.getName() + ")");
        MgdResNodeAssociation[] soapSharedresources = delegate.getSharedresourcesInNode(nodeId);
        SharedResourceDefinitionReference[] sharedresources = new SharedResourceDefinitionReference[soapSharedresources.length];
        for (int i = 0; i < soapSharedresources.length; i++) {
            sharedresources[i] = SharedResourceDefinitionReference.Factory.newInstance();
            sharedresources[i].setName(soapSharedresources[i].getSharedConfigName());
        }
        node.setSharedResourceArray(sharedresources);
    }

    private void fetchNodeComponentsAndLoggers(Node node, long nodeId) throws Exception {
        updateProgressStatus("Extracting components info from node(" + node.getName() + ")");
        // get container version information
        ComponentDetails[] soapContainers = delegate.getComponentInNode(nodeId);

        // get node logging configuration
        updateProgressStatus("Extract node logging configurations info from node(" + node.getName() + ")");
        NodeLoggingDetails result = delegate.getLoggingConfigurationInNode(nodeId);

        LoggingConfiguration soapLoggingConfig = result.getNodeLoggingConfiguration();
        LoggerBase logger = convertLogger(soapLoggingConfig);
        node.setLogger(logger);

        // get container logging configurations
        LoggingConfiguration[] soapContainerLoggingConfigs = result.getContainerLoggingConfigurationArray();
        ContainerBase[] containerLoggingConfigs = new ContainerBase[soapContainerLoggingConfigs.length];
        for (int i = 0; i < soapContainerLoggingConfigs.length; i++) {
            EntityNameAndID entity = soapContainerLoggingConfigs[i].getEntityNameAndID();
            ContainerBase container = ContainerBase.Factory.newInstance();
            for (ComponentDetails soapContainer : soapContainers) {
                if (soapContainer.getEntityId() == entity.getEntityID()) {
                    container.setType(soapContainer.getInventorySoftwareName());
                    Version containerVersion = soapContainer.getInventorySoftwareVersion();
                    container.setVersion(containerVersion.getMajor() + "." + containerVersion.getMinor() + "." + containerVersion.getMaintenance() + "." + containerVersion.getFragment());
                }
            }
            container.setLogger(convertLogger(soapContainerLoggingConfigs[i]));
            containerLoggingConfigs[i] = container;
        }
        node.setContainerArray(containerLoggingConfigs);
    }

    private LoggerBase convertLogger(LoggingConfiguration soapLoggingConfig) {
        LoggerBase logger = LoggerBase.Factory.newInstance();
        Logger soapLogger = soapLoggingConfig.getLogger();

        logger.setLevel(soapLogger.getLevel());
        com.tibco.matrix.admin.server.services.loggingconfiguration.types.ApperderType.Enum type = soapLogger.getAppenderSubType();

        if (type.intValue() == 1) {
            com.tibco.amxadministrator.command.line.typesDetailed.ConsoleAppender appender = com.tibco.amxadministrator.command.line.typesDetailed.ConsoleAppender.Factory.newInstance();
            logger.setConsoleAppender(appender);
        }

        if (type.intValue() == 2) {
            com.tibco.matrix.admin.server.services.loggingconfiguration.types.RollingFileAppender soapAppender = soapLogger.getAppender().getRollingFileAppender();
            com.tibco.amxadministrator.command.line.typesDetailed.RollingFileAppender appender = com.tibco.amxadministrator.command.line.typesDetailed.RollingFileAppender.Factory.newInstance();
            appender.setFileLocation(soapAppender.getFile());
            appender.setMaxBackupIndex(soapAppender.getMaxBackupIndex());
            appender.setMaxFileSize(soapAppender.getMaxFileSize());
            logger.setRollingFileAppender(appender);
        }

        if (type.intValue() == 3) {
            com.tibco.amxadministrator.command.line.typesDetailed.JMSAppender appender = com.tibco.amxadministrator.command.line.typesDetailed.JMSAppender.Factory.newInstance();
            logger.setJMSAppender(appender);
        }
        return logger;
    }

    private void fetchServiceAssembly(Environment environment, long environmentId) {
        try {
			updateProgressStatus("Extracting service assembly info from environment(" + environment.getName() + ")");
			ServiceAssemblyWithSUInfo[] soapServiceAssembliesWithSUInfos = delegate.getServiceAssembliesWithSUInfo(environmentId);
			List<ServiceAssembly> serviceAssemblyList = new ArrayList<ServiceAssembly>(soapServiceAssembliesWithSUInfos.length);
			List<ServiceAssemblyPatch> serviceAssemblyPatchList = new ArrayList<ServiceAssemblyPatch>(soapServiceAssembliesWithSUInfos.length);
			String nameMatch = "Environment[@name=" + environment.getName() + "]/ServiceAssembly";
			for (int i = 0; i < soapServiceAssembliesWithSUInfos.length; i++) {
			    ServiceAssemblyWithSUInfo soapServiceAssembliesWithSUInfo = soapServiceAssembliesWithSUInfos[i];
			    ServiceAssemblySummary soapServiceAssembly = soapServiceAssembliesWithSUInfo.getServiceAssemblySummary();
			    
			    boolean fetchThis = false;
				for (String target : targets) {
					if (target.equals("*") || target.equals("Environment/ServiceAssembly") || target.equals(nameMatch)
							|| target.equals(nameMatch + "[@name=" + soapServiceAssembly.getSaEntityNameAndId().getName() + "]")
							|| target.equals("Environment/ServiceAssembly[@name=" + soapServiceAssembly.getSaEntityNameAndId().getName() + "]")) {
						fetchThis = true;
						break;
					}
				}

				if (fetchThis) {
					ServiceAssembly serviceAssembly = ServiceAssembly.Factory.newInstance();
					long serviceAssemblyId = soapServiceAssembly.getSaEntityNameAndId().getEntityID();

					// convert service assembly info
					serviceAssembly.setName(soapServiceAssembly.getSaEntityNameAndId().getName());
					serviceAssembly.setDescription(soapServiceAssembly.getDescription());
					serviceAssembly.setContact(soapServiceAssembly.getContact());
					serviceAssembly.setExposeEndpoints(ServiceAssembly.ExposeEndpoints.Enum
							.forString(soapServiceAssembly.getExposeEndpoints()));
					serviceAssembly.setUseLocalProviders(soapServiceAssembly.getUseLocalProviders());
					serviceAssembly.setImportSharedResourceConfiguration(false);
					serviceAssembly.setArchivePath(soapServiceAssembly.getArchiveFileLocation());
					ServiceUnitSummary[] soapServiceUnits = soapServiceAssembliesWithSUInfo
							.getListOfServiceUnitsSummary().getSeviceUnitSummaryArray();

					// fetch related
					try {
						fetchServiceUnit(environment, serviceAssembly, soapServiceUnits, serviceAssemblyId);
						fetchTopicInServiceAssembly(serviceAssembly, serviceAssemblyId);
						fetchServiceInServiceAssembly(serviceAssembly, serviceAssemblyId);
						fetchSharedResourceInServiceAssembly(serviceAssembly, serviceAssemblyId);
						fetchLoggingConfigurationsInServiceAssembly(serviceAssembly, serviceAssemblyId);
						fetchSubstitutionVariablesInServiceAssembly(serviceAssembly, serviceAssemblyId);
					} catch (Exception e) {
						logExtractError(new Exception("service assembly(" + serviceAssembly.getName()
								+ ") from environment(" + environment.getName() + ")", e), 1);
					}

					// get permissions
					String serviceAssemblyType = "ServiceAssembly";
					Permissions localPermissions = getPermissionsWithTypeAndID(serviceAssemblyType, serviceAssemblyId);
					if (localPermissions != null)
						serviceAssembly.setPermissions(localPermissions);
					
					serviceAssemblyList.add(serviceAssembly);
					
					//patch
					ServiceAssemblyPatch serviceAssemblyPatch = new ServiceAssemblyPatch();
				    serviceAssemblyPatch.setId(serviceAssemblyId + "");
				    serviceAssemblyPatch.setName(serviceAssembly.getName());
				    serviceAssemblyPatch.setFileName(serviceAssembly.getArchivePath());
				    serviceAssemblyPatchList.add(serviceAssemblyPatch);
				}
			}
			environment.setServiceAssemblyArray(serviceAssemblyList.toArray(new ServiceAssembly[serviceAssemblyList.size()]));
			patchModel.addServiceAssemblyPatchList(serviceAssemblyPatchList);
		} catch (Exception e) {
			logExtractError(new Exception("service assembly info from environment(" + environment.getName() + ")", e), 1);
		}
    }

    private void fetchSubstitutionVariablesInServiceAssembly(ServiceAssembly serviceAssembly, long serviceAssemblyId) throws Exception {
        updateProgressStatus("Extracting substitution variables info from service assembly(" + serviceAssembly.getName() + ")");
        com.tibco.matrix.admin.server.services.deployment.types.SubstitutionVariable[] soapSVars = delegate.getServiceAssemblySVars(serviceAssemblyId);
        SubstitutionVariable[] localSVars = new SubstitutionVariable[soapSVars.length];
        for (int i = 0; i < soapSVars.length; i++) {
            SubstitutionVariable localSVar = convertSVar(soapSVars[i]);
            localSVars[i] = localSVar;
        }
        serviceAssembly.setSubstitutionVariableArray(localSVars);
    }

    private SubstitutionVariable convertSVar(com.tibco.matrix.admin.server.services.svar.types.SubstitutionVariable soapSVar) {
        SubstitutionVariable sVar = SubstitutionVariable.Factory.newInstance();
        sVar.setName(soapSVar.getName());
        int integerType = 1, stringType = 2;
        if (soapSVar.getType() == integerType) {
            sVar.setType("Integer");
        } else if (soapSVar.getType() == stringType) {
            sVar.setType("String");
        }
        sVar.setValue(soapSVar.getValue());
        return sVar;
    }

    private SubstitutionVariable convertSVar(com.tibco.matrix.admin.server.services.deployment.types.SubstitutionVariable soapSVar) {
        SubstitutionVariable localSVar = SubstitutionVariable.Factory.newInstance();
        localSVar.setName(soapSVar.getName());
        localSVar.setType(soapSVar.getType());
        localSVar.setValue(soapSVar.getValue());
        return localSVar;
    }

    private void fetchLoggingConfigurationsInServiceAssembly(ServiceAssembly serviceAssembly, long serviceAssemblyId) throws Exception {
        SALoggingDetails result = delegate.getSALoggingConfiguration(serviceAssemblyId);
        updateProgressStatus("Extracting sa logging congigurations info from service assembly(" + serviceAssembly.getName() + ")");
        LoggingConfiguration soapSALoggingConfig = result.getSaLoggingConfiguration();
        LoggerBase logger = convertLogger(soapSALoggingConfig);
        serviceAssembly.setLogger(logger);

        updateProgressStatus("Extracting su logging congigurations info from service assembly(" + serviceAssembly.getName() + ")");
        LoggingConfiguration[] soapSULoggingConfigs = result.getSuLoggingConfigurationArray();
        ServiceUnitBase[] serviceUnitBases = serviceAssembly.getServiceUnitArray();
        // set logger to responding ServiceUnit
        for (LoggingConfiguration soapSULoggingConfig : soapSULoggingConfigs) {
            String name = soapSULoggingConfig.getEntityNameAndID().getName();
            for (ServiceUnitBase serviceUnitBase : serviceUnitBases)
                if (serviceUnitBase.getName().equals(name))
                    serviceUnitBase.setLogger(convertLogger(soapSULoggingConfig));
        }
        serviceAssembly.setServiceUnitArray(serviceUnitBases);
    }

    private File downloadFile(String url, String fileName, File storeDir) throws Exception {
        AdminAccessConfig accessConfig = delegate.getAccessConfig();

        HttpClient client = new HttpClient();
        client.getState().setCredentials(new AuthScope(accessConfig.getHost(), accessConfig.getPort()), new UsernamePasswordCredentials(accessConfig.getUsername(), accessConfig.getPassword()));

        GetMethod get = new GetMethod(url);
        get.setDoAuthentication(true);

        try {
            int status = client.executeMethod(get);
            if (status == HttpStatus.SC_OK) {
                byte[] content = get.getResponseBody();
                File file = new File(storeDir, fileName);
                ERTUtil.writeFile(file, content);
                return file;
            }
            return null;
        } finally {
            // release any connection resources used by the method
            get.releaseConnection();
        }
    }

    private void fetchServiceUnit(Environment environment, ServiceAssembly serviceAssembly, ServiceUnitSummary[] soapServiceUnits, long serviceAssemblyId) throws Exception {
        updateProgressStatus("Extracting service units info from service assembly(" + serviceAssembly.getName() + ")");
        ServiceUnitBase[] serviceUnits = new ServiceUnitBase[soapServiceUnits.length];
        for (int i = 0; i < soapServiceUnits.length; i++) {
            ServiceUnitSummary soapServiceUnit = soapServiceUnits[i];
            ServiceUnitBase serviceUnit = ServiceUnitBase.Factory.newInstance();
            serviceUnits[i] = serviceUnit;

            //convert service unit info
            serviceUnit.setName(soapServiceUnit.getServiceUnitEntity().getName());

            // fetch related
            long serviceUnitId = soapServiceUnits[i].getServiceUnitEntity().getEntityID();
            fetchServiceUnitNodeMapping(environment, serviceUnit, serviceAssemblyId, serviceUnitId);
            fetchSubstitutionVariablesInServiceUnit(serviceUnit, serviceAssemblyId, serviceUnitId);
        }
        serviceAssembly.setServiceUnitArray(serviceUnits);
    }

    private void fetchSubstitutionVariablesInServiceUnit(ServiceUnitBase serviceUnit, long serviceAssemblyId, long serviceUnitId) throws Exception {
        updateProgressStatus("Extracting substitution variables info from service unit(" + serviceUnit.getName() + ")");
        com.tibco.matrix.admin.server.services.deployment.types.SubstitutionVariable[] soapSVars = delegate.getServiceUnitSVars(serviceAssemblyId, serviceUnitId);
        SubstitutionVariable[] localSVars = new SubstitutionVariable[soapSVars.length];
        for (int i = 0; i < soapSVars.length; i++) {
            SubstitutionVariable localSVar = convertSVar(soapSVars[i]);
            localSVars[i] = localSVar;
        }
        serviceUnit.setSubstitutionVariableArray(localSVars);
    }

    private void fetchServiceUnitNodeMapping(Environment environment, ServiceUnitBase serviceUnit, long serviceAssemblyId, long serviceUnitId) throws Exception {
        updateProgressStatus("Extracting node mapping info from service unit(" + serviceUnit.getName() + ")");
        com.tibco.matrix.admin.server.services.deployment.types.NodeDetails[] soapNodes = delegate.getServiceUnitNodeMappings(serviceAssemblyId, serviceUnitId);
        NodeReference[] nodes = new NodeReference[soapNodes.length];
        for (int j = 0; j < soapNodes.length; j++) {
            nodes[j] = NodeReference.Factory.newInstance();
            nodes[j].setNodeName(soapNodes[j].getEntityNameAndID().getName());
            nodes[j].setEnvironmentName(environment.getName());
        }
        serviceUnit.setNodeArray(nodes);
    }

    private void fetchTopicInServiceAssembly(ServiceAssembly serviceAssembly, long serviceAssemblyId) throws Exception {
        updateProgressStatus("Extracting topics info from service unit(" + serviceAssembly.getName() + ")");
        TopicDefinitionSummary[] soapTopics = delegate.getServiceAssemblyTopics(serviceAssemblyId);
        Topic[] topics = new Topic[soapTopics.length];
        for (int i = 0; i < soapTopics.length; i++) {
            TopicDefinitionSummary soapTopic = soapTopics[i];
            Topic topic = Topic.Factory.newInstance();
            topics[i] = topic;
            //convert topic info
            topic.setName(soapTopic.getTopicDefinitionEntityNameAndID().getName());
            topic.setPhysicalName(soapTopic.getPhysicalName());
        }
        serviceAssembly.setTopicArray(topics);
    }

    private void fetchServiceInServiceAssembly(ServiceAssembly serviceAssembly, long serviceAssemblyId) throws Exception {
        updateProgressStatus("Extracting services info from service unit(" + serviceAssembly.getName() + ")");
        ServiceSummary[] soapServices = delegate.getServicesInServiceAssembly(serviceAssemblyId);
        Service[] services = new Service[soapServices.length];
        for (int i = 0; i < soapServices.length; i++) {
            ServiceSummary soapService = soapServices[i];
            Service service = Service.Factory.newInstance();
            services[i] = service;
            //convert topic info
            service.setName(soapService.getServiceNameAndID().getName());
            service.setQueueName(soapService.getQueueName());
        }
        serviceAssembly.setServiceArray(services);
    }

    private void fetchSharedResourceInServiceAssembly(ServiceAssembly serviceAssembly, long serviceAssemblyId) throws Exception {
        updateProgressStatus("Extracting shared resources info from service unit(" + serviceAssembly.getName() + ")");
        ResourceProfileResourceDefinitionBinding[] soapSharedresources = delegate.getSharedresourcesInServiceAssembly(serviceAssemblyId);
        SharedResourceProfileBase[] sharedresources = new SharedResourceProfileBase[soapSharedresources.length];
        for (int i = 0; i < soapSharedresources.length; i++) {
            sharedresources[i] = SharedResourceProfileBase.Factory.newInstance();
            SharedResourceDefinitionReference sharedresourceReference = SharedResourceDefinitionReference.Factory.newInstance();
            sharedresourceReference.setName(soapSharedresources[i].getSharedResourceDefinition().getName());
            sharedresources[i].setName(soapSharedresources[i].getResourceProfileEntityNameAndId().getName());
            sharedresources[i].setSharedResourceDefinition(sharedresourceReference);
        }
        serviceAssembly.setSharedResourceProfileArray(sharedresources);
    }

    private void fetchSharedResource(Enterprise enterprise) {
        try {
			updateProgressStatus("Extracting shared resources");

			ResourceSummary[] soapSharedresources = delegate.getSharedresources();
			SharedResourceDefinition[] sharedresources = new SharedResourceDefinition[soapSharedresources.length];
			for (int i = 0; i < soapSharedresources.length; i++) {
			    ResourceSummary soapSharedresource = soapSharedresources[i];
			    SharedResourceDefinition sharedresource = SharedResourceDefinition.Factory.newInstance();
			    sharedresource.setName(soapSharedresource.getName());
			    sharedresource.setDescription(soapSharedresource.getDescription());

			    String type = soapSharedresource.getType();
			    long resourceId = soapSharedresource.getEntityId();
			    if (type.equals("JMS")) {
			        JMSSharedResourceDefinition jmsResource = convertJmsResource(delegate.getJmsResourceDetails(resourceId));
			        sharedresource.setJMSSharedResourceDefinition(jmsResource);
			    } else if (type.equals("JDBC")) {
			        JDBCSharedResourceDefinition jdbcResource = convertJdbcResource(delegate.getJdbcResourceDetails(resourceId));
			        sharedresource.setJDBCSharedResourceDefinition(jdbcResource);
			    } else if (type.equals("JNDI")) {
			        JNDISharedResourceDefinition jndiResource = convertJndiResource(delegate.getJndiResourceDetails(resourceId));
			        sharedresource.setJNDISharedResourceDefinition(jndiResource);
			    } else if (type.equals("HTTP")) {
			        HTTPSharedResourceDefinition httpResource = convertHttpResource(delegate.getHttpResourceDetails(resourceId));
			        sharedresource.setHTTPSharedResourceDefinition(httpResource);
			    } else if (type.equals("Rendezvous")) {
			        RendezvousSharedResourceDefinition rendezvousResource = convertRendezvousResource(delegate.getRVResourceDetails(resourceId));
			        sharedresource.setRendezvousSharedResourceDefinition(rendezvousResource);
			    } else if (type.equals("IDENTITY")) {
			        IdentitySharedResourceDefinition identityResource = convertIdentityResource(delegate.getIdentityResourceDetails(resourceId));
			        sharedresource.setIdentitySharedResourceDefinition(identityResource);
			    } else if (type.equals("SSL_SERVER")) {
			        SSLServerSharedResourceDefinition sslServerResource = convertSSLServerSharedResource(delegate.getSSLServerResourceDetails(resourceId));
			        sharedresource.setSSLServerSharedResourceDefinition(sslServerResource);
			    }

			    //get permissions
			    String sharedResourceType = "ResourceConfiguration";
			    Permissions localPermissions = getPermissionsWithTypeAndID(sharedResourceType, soapSharedresources[i].getEntityId());
			    if (localPermissions != null)
			        sharedresource.setPermissions(localPermissions);

			    sharedresources[i] = sharedresource;
			}
			enterprise.setSharedResourceDefinitionArray(sharedresources);
		} catch (Exception e) {
			logExtractError(new Exception("SharedResource", e));
		}
    }

    private JMSSharedResourceDefinition convertJmsResource(EmsResourceDetails soapJmsResource) {
        JMSSharedResourceDefinition jmsResource = JMSSharedResourceDefinition.Factory.newInstance();
        jmsResource.setUsername(soapJmsResource.getUsername());
        jmsResource.setPassword(soapJmsResource.getPassword());
        if (soapJmsResource.getDirectConfiguration() != null) {
            com.tibco.matrix.admin.server.services.sharedresources.types.EmsResourceConfig.DirectConfiguration soapDirectConfiguration = soapJmsResource.getDirectConfiguration();
            DirectConfiguration directConfiguration = DirectConfiguration.Factory.newInstance();
            directConfiguration.setProviderURL(soapDirectConfiguration.getProviderURL());
            directConfiguration.setConnectionFactory(soapDirectConfiguration.getTopicConnectionFactory());
            jmsResource.setDirectConfiguration(directConfiguration);
        } else {
            com.tibco.matrix.admin.server.services.sharedresources.types.EmsResourceConfig.JNDIConfiguration soapJndiConfiguration = soapJmsResource.getJNDIConfiguration();
            JNDIConfiguration jndiConfiguration = JNDIConfiguration.Factory.newInstance();
            SharedResourceDefinitionReference jndiReference = SharedResourceDefinitionReference.Factory.newInstance();
            jndiReference.setName(soapJndiConfiguration.getJNDISharedConfigName());
            jndiConfiguration.setJNDISharedResourceDefinition(jndiReference);
            jndiConfiguration.setConnectionFactory(soapJndiConfiguration.getTopicConnectionFactory());
            jndiConfiguration.setAdminConnectionFactorySSLPwd(soapJndiConfiguration.getConnectionFactorySSLPwd());
            jmsResource.setJNDIConfiguration(jndiConfiguration);
        }
        return jmsResource;
    }

    private JDBCSharedResourceDefinition convertJdbcResource(JdbcResourceDetails soapJdbcResource) {
        JDBCSharedResourceDefinition jdbcResource = JDBCSharedResourceDefinition.Factory.newInstance();
        if (soapJdbcResource.getJdbcSimple() != null) {
            JdbcSimpleType soapJdbcConnection = soapJdbcResource.getJdbcSimple();
            JDBCConnectionType jdbcConnection = JDBCConnectionType.Factory.newInstance();
            jdbcConnection.setDatabaseURL(soapJdbcConnection.getUrl());
            jdbcConnection.setJdbcDriver(soapJdbcConnection.getDriver());
            jdbcConnection.setUsername(soapJdbcConnection.getUsername());
            jdbcConnection.setPassword(soapJdbcConnection.getPassword());
            jdbcConnection.setLoginTimeoutSecs(soapJdbcConnection.getLoginTimeout());
            jdbcConnection.setMaxConnections(soapJdbcConnection.getMaxConnections());
            jdbcResource.setJDBCConnection(jdbcConnection);
        } else {
            JdbcJndiType soapJndiConnection = soapJdbcResource.getJdbcJndi();
            JNDIConnectionType jndiConnection = JNDIConnectionType.Factory.newInstance();
            jndiConnection.setJndiSharedResourceDefinitionName(soapJndiConnection.getJndiSharedConfigName());
            jndiConnection.setDataSourceName(soapJndiConnection.getDataSourceName());
            jdbcResource.setJNDIConnection(jndiConnection);
        }
        return jdbcResource;
    }

    private JNDISharedResourceDefinition convertJndiResource(JndiResourceDetails soapJndiResource) {
        JNDISharedResourceDefinition jndiResource = JNDISharedResourceDefinition.Factory.newInstance();
        jndiResource.setContextFactory(soapJndiResource.getConnectionFactory());
        jndiResource.setContextURL(soapJndiResource.getContextURL());
        jndiResource.setUsername(soapJndiResource.getUsername());
        jndiResource.setPassword(soapJndiResource.getPassword());
        jndiResource.setValidateSecurityContext(soapJndiResource.getValidateSecurityContext());

        JndiProperty[] soapJndiProperties = soapJndiResource.getPropertiesArray();
        if (soapJndiProperties != null) {
            JNDIProperty[] jndiProperties = new JNDIProperty[soapJndiProperties.length];
            for (int i = 0; i < soapJndiProperties.length; i++) {
                JndiProperty soapJndiProperty = soapJndiProperties[i];
                JNDIProperty jndiProperty = JNDIProperty.Factory.newInstance();
                jndiProperty.setName(soapJndiProperty.getName());
                JNDIProperty.Type.Enum type = JNDIProperty.Type.Enum.forString(soapJndiProperty.getType().toString());
                jndiProperty.setType(type);
                jndiProperty.setValue(soapJndiProperty.getValue());
                jndiProperties[i] = jndiProperty;
            }
            jndiResource.setApplicationPropertiesArray(jndiProperties);
        }
        return jndiResource;
    }

    private HTTPSharedResourceDefinition convertHttpResource(HttpResourceDetails soapHttpResource) {
        HTTPSharedResourceDefinition httpResource = HTTPSharedResourceDefinition.Factory.newInstance();
        httpResource.setHost(soapHttpResource.getHost());
        httpResource.setPort(soapHttpResource.getPort());
        httpResource.setEnableDnsLookups(soapHttpResource.getEnableDNSLookup());
        httpResource.setMaxPostSizeBytes((int) soapHttpResource.getMaxPostSize());
        httpResource.setRedirectPort(soapHttpResource.getRedirectPort());
        httpResource.setConnectionTimeoutMillis(soapHttpResource.getConnectionTimeout());
        httpResource.setMinThreads(soapHttpResource.getMinThreads());
        httpResource.setMaxThreads(soapHttpResource.getMaxThreads());
        httpResource.setDisableUploadTimeout(soapHttpResource.getDisableUploadTime());
        if (soapHttpResource.getSSLResourceName() != null) {
            SharedResourceDefinitionReference sslReference = SharedResourceDefinitionReference.Factory.newInstance();
            sslReference.setName(soapHttpResource.getSSLResourceName());
            httpResource.setSSLServerSharedResourceDefinition(sslReference);
        }

        if (soapHttpResource.getListOfSubstitutionBindings() != null) {
            com.tibco.matrix.admin.server.services.sharedresources.types.SubstitutionBinding[] soapSbs = soapHttpResource.getListOfSubstitutionBindings().getSubstitutionBindingsArray();
            SubstitutionBinding[] sbs = new SubstitutionBinding[soapSbs.length];
            for (int i = 0; i < soapSbs.length; i++) {
                sbs[i] = SubstitutionBinding.Factory.newInstance();
                sbs[i].setFieldName(soapSbs[i].getFieldName());
                sbs[i].setSvarName(soapSbs[i].getTemplate());
            }
            ListOfSubstitutionBinding lsb = ListOfSubstitutionBinding.Factory.newInstance();
            lsb.setSubstitutionBindingsArray(sbs);
            httpResource.setListOfSubstitutionBinding(lsb);
        }
        return httpResource;
    }

    private SSLServerSharedResourceDefinition convertSSLServerSharedResource(SSLServerResourceDetails soapSSLServerResource) {
        SSLServerSharedResourceDefinition sslServerResource = SSLServerSharedResourceDefinition.Factory.newInstance();
        String ciperType = soapSSLServerResource.getCiperStrength().toString();
        if (ciperType.equals("NO_RESTRICTION")) {
            sslServerResource.setCipherStrength(CipherStrength.NO_RESTRICTION);
        } else if (ciperType.equals("AT_LEAST_128_BIT")) {
            sslServerResource.setCipherStrength(CipherStrength.AT_LEAST_128_BIT);
        } else if (ciperType.equals("AT_LEAST_256_BIT")) {
            sslServerResource.setCipherStrength(CipherStrength.AT_LEAST_256_BIT);
        } else if (ciperType.equals("MORE_THAN_128_BIT")) {
            sslServerResource.setCipherStrength(CipherStrength.MORE_THAN_128_BIT);
        } else if (ciperType.equals("NO_EXPORT_GRADE")) {
            sslServerResource.setCipherStrength(CipherStrength.NO_EXPORT_GRADE);
        }

        boolean clientAuthenticationRequired = soapSSLServerResource.getClientAuthenticationRequired();
        sslServerResource.setClientAuthenticationRequired(clientAuthenticationRequired);

        SharedResourceDefinitionReference identity = SharedResourceDefinitionReference.Factory.newInstance();
        identity.setName(soapSSLServerResource.getIdentity().getName());
        sslServerResource.setIdentitySharedResourceDefinition(identity);

        if (clientAuthenticationRequired) {
            CertStoreConfiguration soapCertStore = soapSSLServerResource.getTrustedCertStores().getCertStoreArray(0);
            CertStore certStore = CertStore.Factory.newInstance();
            certStore.setPassword(soapCertStore.getPassword());
            certStore.setStoreURL(soapCertStore.getCertStoreURL());
            com.tibco.amxadministrator.command.line.typesDetailed.CertStore.StoreType.Enum storeType = com.tibco.amxadministrator.command.line.typesDetailed.CertStore.StoreType.JKS;
            certStore.setStoreType(storeType);
            sslServerResource.setCertStore(certStore);
        }
        return sslServerResource;
    }

    private IdentitySharedResourceDefinition convertIdentityResource(IdentityResourceDetails soapIndentityResource) {
        IdentitySharedResourceDefinition identityResource = IdentitySharedResourceDefinition.Factory.newInstance();

        PublicKeyType publicKey = PublicKeyType.Factory.newInstance();
        PublicKeyIdentityType soapPublicKey = soapIndentityResource.getPkiSingleURLIdentity();

        KeystoreEntryReference keyStore = KeystoreEntryReference.Factory.newInstance();
        KeyEntryConfiguration soapKeyStore = soapPublicKey.getKeystore();
        keyStore.setName(soapKeyStore.getKeyStoreEntity().getName());
        keyStore.setAlias(soapKeyStore.getAlias());
        keyStore.setPassword(soapKeyStore.getAliasPassword());
        publicKey.setKeystoreEntry(keyStore);

        identityResource.setPublicKeyType(publicKey);
        return identityResource;
    }

    private RendezvousSharedResourceDefinition convertRendezvousResource(RVResourceDetails soapRendezvousResource) {
        RendezvousSharedResourceDefinition rendezvousResource = RendezvousSharedResourceDefinition.Factory.newInstance();
        rendezvousResource.setDaemon(soapRendezvousResource.getDaemon());
        try {
            rendezvousResource.setNetwork(Integer.parseInt(soapRendezvousResource.getNetwork()));
        } catch (NumberFormatException e) {
        }
        try {
            rendezvousResource.setService(Integer.parseInt(soapRendezvousResource.getService()));
        } catch (NumberFormatException e) {
        }
        if (soapRendezvousResource.getRVDistributedQueue() != null) {
            DistributedQueueType disributedQueue = DistributedQueueType.Factory.newInstance();
            RVDistributedQueueType soapDisributedQueue = soapRendezvousResource.getRVDistributedQueue();
            disributedQueue.setCmqName(soapDisributedQueue.getCmqName());
            disributedQueue.setSchedulerActivationSecs(soapDisributedQueue.getSchedulerActivation());
            disributedQueue.setSchedulerHeartbeatSecs(soapDisributedQueue.getSchedulerHeartbeat());
            disributedQueue.setSchedulerWeight(soapDisributedQueue.getSchedulerWeight());
            disributedQueue.setWorkerCompleteTimeSecs(soapDisributedQueue.getWorkerCompleteTime());
            disributedQueue.setWorkerTasks(soapDisributedQueue.getWorkerTasks());
            disributedQueue.setWorkerWeight(soapDisributedQueue.getWorkerWeight());
            rendezvousResource.setDistributedQueueType(disributedQueue);
        } else if (soapRendezvousResource.getRVCertified() != null) {
            CertifiedType certified = CertifiedType.Factory.newInstance();
            RVCertifiedType soapCertifed = soapRendezvousResource.getRVCertified();
            certified.setCmName(soapCertifed.getCmName());
            certified.setLedgerFile(soapCertifed.getLedgerFile());
            certified.setMessageTimeoutSecs(soapCertifed.getMessageTimeout());
            certified.setRelayAgent(soapCertifed.getRelayAgent());
            certified.setRequireOldMessages(soapCertifed.getRequireOldMessages());
            certified.setSyncLedgerFile(soapCertifed.getSyncLedgerFile());
            rendezvousResource.setCertifiedType(certified);
        } else {
            ReliableType reliable = ReliableType.Factory.newInstance();
            rendezvousResource.setReliableType(reliable);
        }

        return rendezvousResource;
    }
}
