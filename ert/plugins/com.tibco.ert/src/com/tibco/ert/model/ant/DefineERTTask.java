package com.tibco.ert.model.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.tibco.ert.model.utils.AMXClassLoaderFactory;

public class DefineERTTask extends Task {
	public void execute() throws BuildException {
		try {
			String strCLILocation = getProject().getProperty("ert.configuration.cli");
			String ertHome = new File(getProject().getProperty("ert.home")).getAbsolutePath();
			System.setProperty("ert.home", ertHome);
			getProject().setProperty("ert.home", ertHome);
			
			ClassLoader classLoader = AMXClassLoaderFactory.createClassLoader(ertHome, strCLILocation,
					Task.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(classLoader);
			
			getProject().addTaskDefinition("ExtractTask", classLoader.loadClass("com.tibco.ert.model.ant.ExtractTask"));
			getProject().addTaskDefinition("ReplicateTask", classLoader.loadClass("com.tibco.ert.model.ant.ReplicateTask"));
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
}
