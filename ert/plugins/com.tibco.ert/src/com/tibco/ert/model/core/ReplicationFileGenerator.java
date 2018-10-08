package com.tibco.ert.model.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tibco.amxadministrator.command.line.types.DefaultConnector;
import com.tibco.amxadministrator.command.line.types.Machine;
import com.tibco.amxadministrator.command.line.types.Node;
import com.tibco.amxadministrator.command.line.types.User;
import com.tibco.amxadministrator.command.line.typesBase.Enterprise;
import com.tibco.amxadministrator.command.line.typesBase.EnvironmentBase;
import com.tibco.amxadministrator.command.line.typesBase.MachineBase;
import com.tibco.amxadministrator.command.line.typesBase.NodeBase;
import com.tibco.amxadministrator.command.line.typesBase.ServiceAssemblyBase;
import com.tibco.amxadministrator.command.line.typesBase.ServiceUnitBase;
import com.tibco.amxadministrator.command.line.typesBase.UserBase;
import com.tibco.amxadministrator.command.line.typesReference.MachineReference;
import com.tibco.amxadministrator.command.line.typesReference.NodeReference;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.model.utils.ERTUtil;
import com.tibco.ert.model.utils.ZipUtil;
import com.tibco.matrix.admin.server.services.managementdaemon.types.ManagementDaemonInfo;
import com.tibco.matrix.admin.server.services.managementdaemon.types.ManagementDaemonStatus;
import com.tibco.matrix.admin.server.services.managementdaemon.types.MatrixInstallation;
import com.tibco.matrix.admin.server.services.matrixcommon.types.Version;

public class ReplicationFileGenerator extends BaseERTJob {
	public ReplicationFileGenerator(AdminServicesDelegate delegate) throws Exception {
		this.delegate = delegate;
	}

	public File generate(Map<String, String> machineMapping, File targetMachineFile, File sourceDataFile,
			String replicationDir) throws Exception {
		Enterprise targetEnterprise = ERTUtil.loadEnterprise(targetMachineFile);
		MachineBase[] targetMachines = targetEnterprise.getMachineArray();

		File tempDir = new File(replicationDir, ERTUtil.getTimestamp() + "_tmp");
		tempDir.mkdirs();
		try {
			ZipUtil.unzip(sourceDataFile, tempDir);

			Enterprise enterprise = ERTUtil.loadSeparateEnterprise(tempDir);

			updateProgressStatus("Replacing machines info with machine mapping setting");
			replaceMachines(enterprise, machineMapping, targetMachines);

			updateProgressStatus("Binding target machines");
			bindMachines(enterprise, delegate);

			updateProgressStatus("Getting installed software for machines");
			Map<String, MatrixInstallation> installationMap = getInstalledSoftwareForMachines(enterprise, delegate);

			updateProgressStatus("Replacing nodes product info");
			replaceNodesProductInfo(enterprise, installationMap);

			AdminAccessConfig accessConfig = delegate.getAccessConfig();
			checkUserPassword(enterprise, accessConfig.getUsername(), accessConfig.getPassword());

			updateProgressStatus("Writing the replicate data xml");

			ERTUtil.saveSeparateEnterprise(tempDir, enterprise);
			String replicationFileName = ERTUtil.getFileName(sourceDataFile).replace("_Enterprise", "");
			replicationFileName = replicationFileName + "-TO-" + accessConfig.getHost() + "-" + accessConfig.getPort()
					+ "_Enterprise.zip";
			File replicationFile = new File(replicationDir, replicationFileName);
			ZipUtil.zip(tempDir, replicationFile.getAbsolutePath());
			
			return replicationFile;
		} finally {
			ERTUtil.delete(tempDir);
		}
	}

	private void checkUserPassword(Enterprise enterprise, String userName, String password) {
		UserBase[] users = enterprise.getUserArray();
		for (UserBase base : users) {
			User user = (User) base;
			if (user.getUsername().equals(userName)) {
				user.setPassword(password);
				return;
			}
		}
	}

	/**
	 * bind machines to the target admin server
	 * 
	 * @param enterprise
	 * @param delegate
	 * @throws Exception
	 */
	private void bindMachines(Enterprise enterprise, AdminServicesDelegate delegate) throws Exception {
		MachineBase[] machines = enterprise.getMachineArray();
		if (machines.length > 0) {
			ManagementDaemonInfo[] soapMachines = new ManagementDaemonInfo[machines.length];
			for (int i = 0; i < machines.length; i++) {
				Machine machine = (Machine) machines[i];
				ManagementDaemonInfo soapMachine = ManagementDaemonInfo.Factory.newInstance();
				soapMachine.setHostName(machine.getHostName());
				soapMachine.setTibcoHome(machine.getTibcoHome());
				soapMachine.setManagementJMXUrl(machine.getManagementURL());
				soapMachines[i] = soapMachine;
			}
			soapMachines = delegate.connectToManagementDaemons(soapMachines);

			// verify
			for (ManagementDaemonInfo soapMachine : soapMachines) {
				ManagementDaemonStatus status = soapMachine.getMgmtStatus();
				if (!status.getStatus().startsWith("Bound")) {
					String error = "Unable to bind the machine: " + soapMachine.getHostName();
					if (status.getErrorDetails() != null && status.getErrorDetails().getSummary() != null) {
						error = error + ", " + status.getErrorDetails().getSummary();
					}
					throw new Exception(error);
				}
			}
		}
	}

	/**
	 * get installed software in machines
	 * 
	 * @param enterprise
	 * @param delegate
	 * @return installation map
	 * @throws Exception
	 */
	private Map<String, MatrixInstallation> getInstalledSoftwareForMachines(Enterprise enterprise,
			AdminServicesDelegate delegate) throws Exception {
		MachineBase[] machines = enterprise.getMachineArray();
		Map<String, MatrixInstallation> installationMap = new HashMap<String, MatrixInstallation>();
		for (MachineBase machineBase : machines) {
			Machine machine = (Machine) machineBase;
			MatrixInstallation[] matrixInstallations = delegate.getInstalledSoftware(machine);
			if (matrixInstallations.length > 0) {
				MatrixInstallation matrixInstallation = matrixInstallations[0];
				installationMap.put(machine.getHostName() + "(" + machine.getTibcoHome() + ")", matrixInstallation);
			}
		}
		return installationMap;
	}

	/**
	 * relace node product infromation in enterprise with a installation map
	 * 
	 * @param enterprise
	 * @param installationMap
	 * @throws Exception
	 */
	private void replaceNodesProductInfo(Enterprise enterprise, Map<String, MatrixInstallation> installationMap)
			throws Exception {
		EnvironmentBase[] environments = enterprise.getEnvironmentArray();
		for (EnvironmentBase environment : environments) {
			NodeBase[] nodes = environment.getNodeArray();
			for (NodeBase nodeBase : nodes) {
				Node node = (Node) nodeBase;
				MatrixInstallation installation = installationMap.get(node.getHostName() + "(" + node.getTibcoHome()
						+ ")");
				if (installation == null) {
					throw new Exception("Can not find the amx installation in " + node.getHostName());
				}
				node.setProductInstallDirectory(installation.getInstallationDirectory());
				Version productVersion = installation.getVersion();
				if (productVersion != null) {
					String strProductVersion = productVersion.getMajor() + "." + productVersion.getMinor() + "."
							+ productVersion.getMaintenance() + "." + productVersion.getFragment();
					node.setProductVersion(strProductVersion);
				}
			}
		}
	}

	/**
	 * replace machines in the enterprise with a machine map
	 * 
	 * @param enterprise
	 * @param machineMapping
	 * @param targetMachines
	 * @throws Exception
	 */
	private void replaceMachines(Enterprise enterprise, Map<String, String> machineMapping, MachineBase[] targetMachines)
			throws Exception {
		updateProgressStatus("Replacing host name and tibco home");

		StringBuffer targetMachineNames = new StringBuffer();
		for (MachineBase machine : targetMachines) {
			if (targetMachineNames.length() > 0) {
				targetMachineNames.append(',');
			}
			targetMachineNames.append(machine.getHostName() + "(" + machine.getTibcoHome() + ")");
		}

		MachineBase[] machines = enterprise.getMachineArray();
		List<MachineBase> machineList = new ArrayList<MachineBase>();
		for (int i = 0; i < machines.length; i++) {
			Machine machine = (Machine) machines[i];
			String targetMachineName = machineMapping.get(machine.getHostName() + "(" + machine.getTibcoHome() + ")");
			if (targetMachineName.trim().length() > 0) {
				Machine targetMachine = matchTargetMachine(targetMachineName, targetMachines,
						targetMachineNames.toString());
				// replace
				machine.setHostName(targetMachine.getHostName());
				machine.setTibcoHome(targetMachine.getTibcoHome());
				machine.setManagementURL(targetMachine.getManagementURL());

				machineList.add(machine);
			}
		}
		enterprise.setMachineArray(machineList.toArray(new MachineBase[machineList.size()]));

		EnvironmentBase[] environments = enterprise.getEnvironmentArray();
		for (EnvironmentBase environment : environments) {
			MachineReference[] machineReferences = environment.getMachineArray();
			List<MachineReference> machineReferenceList = new ArrayList<MachineReference>();
			for (int i = 0; i < machineReferences.length; i++) {
				MachineReference machineReference = machineReferences[i];
				String targetMachineName = (String) machineMapping.get(machineReference.getHostName() + "("
						+ machineReference.getTibcoHome() + ")");
				if (targetMachineName.trim().length() > 0) {
					Machine targetMachine = matchTargetMachine(targetMachineName, targetMachines,
							targetMachineNames.toString());
					// replace
					machineReference.setHostName(targetMachine.getHostName());
					machineReference.setTibcoHome(targetMachine.getTibcoHome());

					machineReferenceList.add(machineReference);
				}
			}
			environment
					.setMachineArray(machineReferenceList.toArray(new MachineReference[machineReferenceList.size()]));

			NodeBase[] nodes = environment.getNodeArray();
			List<NodeBase> nodeList = new ArrayList<NodeBase>();
			for (int i = 0; i < nodes.length; i++) {
				Node node = (Node) nodes[i];
				String previousHost = node.getHostName();
				String targetMachineName = (String) machineMapping.get(previousHost + "(" + node.getTibcoHome() + ")");
				if (targetMachineName.trim().length() > 0) {
					Machine targetMachine = matchTargetMachine(targetMachineName, targetMachines,
							targetMachineNames.toString());
					// replace
					node.setHostName(targetMachine.getHostName());
					node.setTibcoHome(targetMachine.getTibcoHome());

					// replace for default connector
					DefaultConnector defaultConnector = (DefaultConnector) node.getDefaultConnector();
					if (defaultConnector != null && defaultConnector.getHTTPSharedResourceDefinition() != null) {
						String connectorHost = defaultConnector.getHTTPSharedResourceDefinition().getHost();
						if (connectorHost.equals(previousHost)) {
							defaultConnector.getHTTPSharedResourceDefinition().setHost(node.getHostName());
						}
					}

					nodeList.add(node);
				}
			}
			environment.setNodeArray(nodeList.toArray(new NodeBase[nodeList.size()]));

			// check for service assembliy
			// reload the nodes
			nodes = environment.getNodeArray();
			ServiceAssemblyBase[] serviceAssemblies = environment.getServiceAssemblyArray();
			for (ServiceAssemblyBase serviceAssembliy : serviceAssemblies) {
				ServiceUnitBase[] serviceUnits = serviceAssembliy.getServiceUnitArray();
				for (int i = 0; i < serviceUnits.length; i++) {
					ServiceUnitBase serviceUnit = serviceUnits[i];
					NodeReference[] nodeReferences = serviceUnit.getNodeArray();
					List<NodeReference> nodeReferenceList = new ArrayList<NodeReference>();
					for (int j = 0; j < nodeReferences.length; j++) {
						NodeReference nodeReference = nodeReferences[j];
						for (NodeBase node : nodes) {
							if (node.getName().equals(nodeReference.getNodeName())) {
								nodeReferenceList.add(nodeReference);
								break;
							}
						}
					}
					serviceUnit.setNodeArray(nodeReferenceList.toArray(new NodeReference[nodeReferenceList.size()]));
				}
			}
		}
	}

	/**
	 * return the matched target machine
	 * 
	 * @param targetMachineName
	 * @param targetMachines
	 * @param targetMachineNames
	 * @return target machine
	 * @throws Exception
	 */
	private Machine matchTargetMachine(String targetMachineName, MachineBase[] targetMachines, String targetMachineNames)
			throws Exception {
		int index = targetMachineName.indexOf('(');
		String hostName = targetMachineName.substring(0, index);
		String tibcoHome = targetMachineName.substring(index + 1, targetMachineName.length() - 1);
		for (MachineBase targetMachine : targetMachines) {
			if (targetMachine.getHostName().equals(hostName) && targetMachine.getTibcoHome().equals(tibcoHome)) {
				return (Machine) targetMachine;
			}
		}
		throw new Exception("Cannot find machine: " + targetMachineName + " in target machines [" + targetMachineNames
				+ "]");
	}
}
