package com.tibco.configtool.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.configtool.actions.SaveDataModelAction;
import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.ConsoleProcessor;
import com.tibco.customwizard.support.SystemConsoleProxy;

public class TCTConsoleProcessor extends ConsoleProcessor {
	public static final String USERCHOOSE_SAVE = "S";
	public static final String USERCHOOSE_CONFIGURE = "G";
	
	public void process(WizardConfig wizardConfig) throws Exception {
		File consoleLogDir = new File(TCTContext.getInstance().getTCTHome(),"consolelog");
		consoleLogDir.mkdir();
		File consoleLogFile = new File(consoleLogDir, TCTHelper.createSessionId()+ ".log");
		PrintStream consoleLog = new PrintStream(consoleLogFile);
		SystemConsoleProxy.setConsoleLog(consoleLog);
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
	
		while (true) {
			WizardConfig contributor = selectContributor();
			if (contributor == null) {
				break;
			}
			
			try {
				super.process(contributor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		consoleLog.close();
		System.exit(0);
	}

	protected void executePostAction(WizardInstance wizardInstance) throws Exception {
		if (TCTHelper.getSummaryPage(wizardInstance) == null) {
			super.executePostAction(wizardInstance);
		}
	}
	
	protected WizardInstance createWizardInstance(WizardConfig wizardConfig) throws Exception {
		return TCTHelper.createWizardInstance(wizardConfig, WizardInstance.CONSOLE_MODE);
	}
	
	protected int processUserChoose(String userChoose, int formIndex, WizardInstance wizardInstance) throws Exception {
		if (userChoose.equals(USERCHOOSE_SAVE)) {
			SaveDataModelAction.getSaveDataModelAction(wizardInstance).saveDataModel(wizardInstance);
			SystemConsoleProxy.println("\nScripts saved successfully!\n");
			return formIndex;
		} else if (userChoose.equals(USERCHOOSE_FINISH)) {
			return wizardInstance.getPageList().size() - 1;
		}
		return -1;
	}
	
	protected String confirmUserChoose(XForm xform, int controlCount,
			boolean lastPage, boolean validWizard, boolean canNext,
			boolean canPrevious) {
		boolean canFinish = (!lastPage && validWizard);
		boolean canSave = (lastPage && validWizard);
		
		return confirmUserChoose(controlCount, canNext, canPrevious, canSave, canFinish);
	}
	
	protected String confirmUserChoose(int controlCount, boolean canNext, boolean canPrevious,
			boolean canSave, boolean canFinish) {
		String defaultChoose = (controlCount == 0 ? "N" : "1");
		SystemConsoleProxy.print("\nChoose" + (canNext ? " 'N' for Next Page," : "")
				+ (canPrevious ? " 'P' for Previous Page," : "")
				+ (canSave ? " 'S' for Save, 'G' for Configure," : "")
				+ (canFinish ? " 'F' for Finish," : "")
				+ " 'C' for Cancel, or enter field number [" + defaultChoose
				+ "]");
		String userChoose = SystemConsoleProxy.readLine();
		if (userChoose.length() == 0) {
 			return defaultChoose;
		} else if ((canNext && userChoose.equals(USERCHOOSE_NEXT_PAGE))
				|| (canPrevious && userChoose.equals(USERCHOOSE_PREVIOUS_PAGE))
				|| (canFinish && userChoose.equals(USERCHOOSE_FINISH))
				|| (canSave && userChoose.equals(USERCHOOSE_SAVE))
				|| (canSave && userChoose.equals(USERCHOOSE_CONFIGURE))
				|| userChoose.equals(USERCHOOSE_CANCEL)) {
			return userChoose;
		} else {
			try {
				int intUserChoose = Integer.parseInt(userChoose);
				if (intUserChoose > 0 && intUserChoose <= controlCount) {
					return userChoose;
				}
			} catch (NumberFormatException e) {
			}
		}
		SystemConsoleProxy.println("\nPlease enter correct option.\n");
		return confirmUserChoose(controlCount, canNext, canPrevious, canSave, canFinish);
	}
	
	private WizardConfig selectContributor() {
		List<WizardConfig> contributorList = TCTContext.getInstance().getContributorList();
		SystemConsoleProxy.println("Choose one option from the list below.\n");
		String selected = "X";
		for (int i = 0; i < contributorList.size(); i++) {
			SystemConsoleProxy.println("[" + selected + "] " + (i + 1) + " - " + contributorList.get(i).getTitle());
			selected = " ";
		}
		SystemConsoleProxy.print("\nTo select an item enter its number, or enter 'q' to quit: [1]");

		int selectedIndex = 0;
		String input = SystemConsoleProxy.readLine();
		if (input.length() == 0) {
			return contributorList.get(0);
		} else if (input.equals("q")) {
			return null;
		}
		String wrongChooseMsg = "\nPlease enter a value between 1 and " + contributorList.size() + "\n";
		try {
			int intInput = Integer.parseInt(input);
			if (intInput < 1 || intInput > contributorList.size()) {
				SystemConsoleProxy.println(wrongChooseMsg);
				return selectContributor();
			}
			selectedIndex = intInput - 1;
		} catch (NumberFormatException e) {
			SystemConsoleProxy.println(wrongChooseMsg);
			return selectContributor();
		}

		return contributorList.get(selectedIndex);
	}
}
