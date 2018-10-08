package com.tibco.configtool.internal.support;

import java.io.File;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntCaller {
	public static void callAnt(File buildFile, String targetName, Properties props, PrintStream log, Writer progressMonitor) throws Exception {
		Project p = new Project();
		// p.init();
		p.setJavaVersionProperty();
		ComponentHelper.getComponentHelper(p).initDefaultDefinitions();

		DefaultLogger antLogger = new DefaultLogger();
		antLogger.setErrorPrintStream(log);
		antLogger.setOutputPrintStream(log);
		antLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(antLogger);

		PrintStream sysout = System.out;
		PrintStream syserr = System.err;
		System.setErr(log);
		System.setOut(log);
		
		//save the current context class loader, in case the ant task may change it
		ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
		try {
			StringBuffer command = new StringBuffer();
			command.append("ant -f ");
			command.append(buildFile.getName());
			if (targetName != null) {
				command.append(" ");
				command.append(targetName);
			}
			if (props != null) {
				for (Entry<Object, Object> entry : props.entrySet()) {
					String pName = (String) entry.getKey();
					String pValue = (String) entry.getValue();
					p.setProperty(pName, pValue);
					command.append(" -D");
					command.append(pName);
					command.append("=");
					command.append(pValue);
				}
			}
			log.println(command.toString());

			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
			helper.parse(p, buildFile);

			targetName = targetName == null ? p.getDefaultTarget() : targetName;
			try {
				p.setProperty(MagicNames.ANT_FILE, buildFile.getAbsolutePath());
				p.fireBuildStarted();
				p.executeTarget(targetName);
				p.fireBuildFinished(null);
			} catch (Exception e) {
				p.fireBuildFinished(e);
				throw e;
			}
		} finally {
			System.setErr(syserr);
			System.setOut(sysout);
			
			//reset the context class loader
			Thread.currentThread().setContextClassLoader(currentCL);
		}
	}
}
