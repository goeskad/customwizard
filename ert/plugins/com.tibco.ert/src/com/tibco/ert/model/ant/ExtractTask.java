package com.tibco.ert.model.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.tibco.ert.model.core.AdminAccessConfig;
import com.tibco.ert.model.core.EnterpriseExtractor;
import com.tibco.ert.model.core.PasswordObfuscator;
import com.tibco.ert.model.matrix.AdminServicesDelegate;

public class ExtractTask extends Task {
	public void execute() throws BuildException {
		EnterpriseExtractor extractor = null;

		try {
			AdminServicesDelegate delegate = new AdminServicesDelegate(getAdminAccessConfig());

			extractor = new EnterpriseExtractor(delegate);

			extractor.extractToZipFile(getProject().getProperty("ert.extraction.target"), new File(
					getProject().getProperty("ert.configuration.extraction")));
		} catch (Exception e) {
			throw new BuildException(e);
		} finally {
			try {
				extractor.release();
			} catch (Exception e) {
				throw new BuildException(e);
			}
		}
	}

	private AdminAccessConfig getAdminAccessConfig() throws Exception {
		return new AdminAccessConfig(getProject().getProperty("ert.source.host"), Integer.parseInt(getProject()
				.getProperty("ert.source.port")), getProject().getProperty("ert.source.username"),
				PasswordObfuscator.instance.decrypt(getProject().getProperty("ert.source.password")),
				PasswordObfuscator.instance.decrypt(getProject().getProperty("ert.source.dbpassword")));
	}
}
