package com.tibco.configtool.utils;

import java.io.File;
import java.net.URI;
import java.util.List;

import com.tibco.configtool.support.TCTContext;
import com.tibco.corona.models.installation.machinemodel.Machine;
import com.tibco.corona.models.installation.machinemodel.ReleaseUnit;
import com.tibco.corona.models.installation.machinemodel.TIBCOInstallation;
import com.tibco.neo.machine.utilities.LockMachineContext;
import com.tibco.neo.machine.utilities.MachineUtilities;
import com.tibco.neo.model.types.ComponentID;
import com.tibco.neo.model.types.VersionNumber;

public class MachineModelUtils {
	private static Machine machine;

	public static synchronized Machine reloadMachine() throws Exception {
		machine = null;
		return getMachine();
	}

	public static synchronized Machine getMachine() throws Exception {
		if (machine == null) {
			machine = getLockMachineContext(TCTContext.getInstance().getMachineModelLocation()).readMachine();
		}
		return machine;
	}

	public static Machine getMachine(String machineModelLocation) throws Exception {
		return getLockMachineContext(machineModelLocation).readMachine();
	}

	private static LockMachineContext getLockMachineContext(String machineModelLocation) throws Exception {
		URI machineModelURI = new File(machineModelLocation).toURI();
		return new MachineUtilities().lockMachine(machineModelURI);

	}

	@SuppressWarnings("unchecked")
	public static List<TIBCOInstallation> getInstallations(Machine machine) {
		return machine.getInstallations();
	}

	@SuppressWarnings("unchecked")
	public static List<ReleaseUnit> getReleaseUnits(TIBCOInstallation inst) {
		return inst.getReleaseUnits();
	}

	public static ReleaseUnit getReleaseUnit(Machine machine, ComponentID componentID, VersionNumber version) {
		List<TIBCOInstallation> insts = getInstallations(machine);
		for (TIBCOInstallation inst : insts) {
			List<ReleaseUnit> rus = getReleaseUnits(inst);
			for (ReleaseUnit releaseUnit : rus) {
				if (releaseUnit.getComponentID().equals(componentID) && releaseUnit.getVersion().equals(version)) {
					return releaseUnit;
				}
			}
		}
		return null;
	}
}
