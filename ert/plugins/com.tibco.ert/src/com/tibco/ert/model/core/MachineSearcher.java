package com.tibco.ert.model.core;

import com.tibco.amxadministrator.command.line.types.Machine;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.matrix.admin.server.services.managementdaemon.types.ManagementDaemonInfo;
import com.tibco.matrix.admin.server.services.managementdaemon.types.OperationHandler;
import com.tibco.matrix.admin.server.services.managementdaemon.types.SocketDiscoveryConfiguration;
import com.tibco.matrix.admin.server.services.matrixcommon.types.EntityResponse;

public class MachineSearcher extends BaseERTJob {
	public MachineSearcher(AdminServicesDelegate delegate) throws Exception {
		this.delegate = delegate;
	}

	public Machine[] search() throws Exception {
		long searcherId = -1;
		SocketDiscoveryConfiguration[] discoveryConfigurations = delegate.getScoketDiscoveryConfigurations();
		updateProgressStatus("Found " + (discoveryConfigurations == null ? 0 : discoveryConfigurations.length)
				+ "configurations");
		if (discoveryConfigurations != null) {
			for (SocketDiscoveryConfiguration configuration : discoveryConfigurations) {
				if (configuration.getName().equals("ERTSearcher")) {
					updateProgressStatus("Found ERT discovery configuration: " + configuration.getEntityID());
					searcherId = configuration.getEntityID();
					break;
				}
			}
		}

		if (searcherId == -1) {
			updateProgressStatus("Cannot found ERT discovery configuration, will add a new one");
			EntityResponse response = delegate.addSocketDiscoveryConfiguration("ERTSearcher");
			if (response.getStatus().equals("SUCCESS")) {
				searcherId = response.getEntityID();
				updateProgressStatus("Added new discovery configuration: " + searcherId);
			} else {
				throw new Exception("Cannot add socket discovery configuration to "
						+ delegate.getAccessConfig().getHost());
			}
		}

		updateProgressStatus("Start discovery");
		OperationHandler operationHandler = delegate.startDiscovery(searcherId);
		updateProgressStatus("Wait for 5 seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		ManagementDaemonInfo[] soapMachines = delegate.getDiscoveredManagementDaemons(operationHandler);
		updateProgressStatus("Found " + (soapMachines == null ? 0 : soapMachines.length) + " machines");

		Machine[] machines = new Machine[soapMachines.length];
		for (int i = 0; i < soapMachines.length; i++) {
			ManagementDaemonInfo soapMachine = soapMachines[i];
			Machine machine = Machine.Factory.newInstance();
			machines[i] = machine;

			// convert machine info
			machine.setHostName(soapMachine.getHostName());
			machine.setTibcoHome(soapMachine.getTibcoHome());
			machine.setManagementURL(soapMachine.getManagementJMXUrl());
		}

		return machines;
	}
}
