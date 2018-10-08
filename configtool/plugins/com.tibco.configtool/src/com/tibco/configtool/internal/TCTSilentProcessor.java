package com.tibco.configtool.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Set;

import com.tibco.configtool.support.SilentWizardProcessor;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.support.SystemConsoleProxy;

public class TCTSilentProcessor {
	public void process(WizardConfig wizardConfig) throws Exception {
		TCTContext tctContext = TCTContext.getInstance();
		
		SystemConsoleProxy.setConsoleLog(null);
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
		
		if (!validateSilentArgs(tctContext)) {
			return;
		}

		WizardConfig contributor = tctContext.getContributor(tctContext.getSilentContributorId());
		if (contributor.getWizardProcessor() != null) {
			contributor.getWizardProcessor().process(contributor);
		} else {
			new SilentWizardProcessor().process(contributor);
		}
	}

	private boolean validateSilentArgs(TCTContext tctContext) throws Exception {
		if (TCTHelper.isEmpty(tctContext.getSilentContributorId())) {
			SystemConsoleProxy.println("Please sepcify the contributor id.");
			silentUsage();
			return false;
		} else if (TCTHelper.isEmpty(tctContext.getSilentDataFile())) {
			SystemConsoleProxy.println("Please sepcify the contributor properties file.");
			silentUsage();
			return false;
		} else if (!new File(tctContext.getSilentDataFile()).exists()) {
			SystemConsoleProxy.println("The contributor properties file '" + tctContext.getSilentDataFile() + "' doesn't exist.");
			return false;
		} else if (tctContext.getContributor(tctContext.getSilentContributorId()) == null) {
			SystemConsoleProxy.println("Invalide contributor id '" + tctContext.getSilentContributorId() + "'.");
			printContributorList();
			return false;
		}

		return true;
	}

	private void silentUsage() {
		SystemConsoleProxy.println("\nUsage:\n");

		SystemConsoleProxy
				.println("TIBCOConfigurationTool -silentMode -contributor.id <contributor id> -contributor.props <build.properties> -contributor.target <ant target name>");
	}

	private void printContributorList() {
		SystemConsoleProxy.println("\nAvailable contributors:");

		Set<String> contributorIds = TCTContext.getInstance().getContributorMap().keySet();
		for (String contributorId : contributorIds) {
			SystemConsoleProxy.println("\t" + contributorId);
		}
	}
}
