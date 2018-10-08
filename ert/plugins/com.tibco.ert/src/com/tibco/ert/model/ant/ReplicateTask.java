package com.tibco.ert.model.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.tibco.ert.model.core.AdminAccessConfig;
import com.tibco.ert.model.core.EnterpriseReplicater;
import com.tibco.ert.model.core.PasswordObfuscator;
import com.tibco.ert.model.matrix.AdminServicesDelegate;

public class ReplicateTask extends Task {
	public void execute() throws BuildException {
		EnterpriseReplicater replicater = null;

		try {
			AdminServicesDelegate delegate = new AdminServicesDelegate(getAdminAccessConfig());

			replicater = new EnterpriseReplicater(delegate);

			replicater.replicateFromZipFile(getProject().getProperty("ert.replication.target"), new File(getProject()
					.getProperty("ert.target.datafile")), Boolean.parseBoolean(getProject().getProperty(
					"ert.replication.start-node")), Boolean.parseBoolean(getProject().getProperty(
					"ert.replication.start-sa")));

			log("Replication successful!");
		} catch (Exception e) {
			throw new BuildException(e);
		} finally {
			try {
				replicater.release();
			} catch (Exception e) {
				throw new BuildException(e);
			}
		}
	}

	private AdminAccessConfig getAdminAccessConfig() throws Exception {
		return new AdminAccessConfig(getProject().getProperty("ert.target.host"), Integer.parseInt(getProject()
				.getProperty("ert.target.port")), getProject().getProperty("ert.target.username"),
				PasswordObfuscator.instance.decrypt(getProject().getProperty("ert.target.password")),
				PasswordObfuscator.instance.decrypt(getProject().getProperty("ert.target.dbpassword")));
	}
}
