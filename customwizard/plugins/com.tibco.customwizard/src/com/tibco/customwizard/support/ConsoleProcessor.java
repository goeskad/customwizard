package com.tibco.customwizard.support;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.xforms.model.XForm;

import com.tibco.customwizard.config.IWizardProcessor;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.console.ConsoleForm;
import com.tibco.customwizard.internal.console.proxies.ConsoleControlProxy;
import com.tibco.customwizard.internal.console.proxies.GroupProxy;
import com.tibco.customwizard.internal.console.proxies.LabelProxy;
import com.tibco.customwizard.internal.console.proxies.ListableControlProxy;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.customwizard.util.XFormUtils;
import com.tibco.customwizard.validator.ValidateResult;

public class ConsoleProcessor implements IWizardProcessor {
	protected static final String USERCHOOSE_NEXT_PAGE = "N";
	protected static final String USERCHOOSE_PREVIOUS_PAGE = "P";
	protected static final String USERCHOOSE_FINISH = "F";
	protected static final String USERCHOOSE_CANCEL = "C";

	private static ConsoleProcessor instance = new ConsoleProcessor();

	public static ConsoleProcessor getInstance() {
		return instance;
	}

	protected ConsoleProcessor() {
	}

	public void process(WizardConfig wizardConfig) throws Exception {
		process(createWizardInstance(wizardConfig));
	}

	public void process(WizardInstance wizardInstance) throws Exception {
		int formIndex = 0;

		while (true) {
			List<PageInstance> pageList = wizardInstance.getPageList();
			PageInstance currentPage = pageList.get(formIndex);
			wizardInstance.setCurrentPage(currentPage);

			currentPage.performPreAction();
			String userChoose = presentPage(currentPage);
			if (userChoose.equalsIgnoreCase(USERCHOOSE_NEXT_PAGE)) {
				formIndex++;
				currentPage.performPostAction();
			} else if (userChoose.equalsIgnoreCase(USERCHOOSE_PREVIOUS_PAGE)) {
				formIndex--;
			} else if (userChoose.equalsIgnoreCase(USERCHOOSE_CANCEL)) {
				return;
			} else {
				formIndex = processUserChoose(userChoose, formIndex, wizardInstance);
				if (formIndex < 0) {
					currentPage.performPostAction();
					break;
				}
			}
		}

		executePostAction(wizardInstance);
	}
	
	protected void executePostAction(WizardInstance wizardInstance) throws Exception {
		WizardHelper.executePostAction(wizardInstance);
	}
	
	protected String presentPage(PageInstance pageInstance) throws Exception {
		XForm form = XFormUtils.getXForm(pageInstance);

		ValidateResult validateResult = WizardHelper.getValidateResult(pageInstance);
		
		SystemConsoleProxy.println("\n===============================================================================");
		SystemConsoleProxy.println(pageInstance.getPageConfig().getTitle());
		SystemConsoleProxy.println("===============================================================================");
		SystemConsoleProxy.println(pageInstance.getPageConfig().getDescription());
		SystemConsoleProxy.println("");
		if (validateResult != null && !validateResult.isValid()) {
			SystemConsoleProxy.println("Invalid: " + validateResult.getErrorMessage());
		}
		
		printUnListableControl(form);

		SystemConsoleProxy.println("");
		// present control list
		List<ListableControlProxy> listableControlList = new ArrayList<ListableControlProxy>();
		getListableControlList((GroupProxy) ((ConsoleForm) form.getUI()).getParentControl(), listableControlList);
		int index = 1;
		for (ListableControlProxy consoleControl : listableControlList) {
			SystemConsoleProxy.print("[" + index + "] ");
			consoleControl.presentSummay();
			SystemConsoleProxy.println("");
			index++;
		}
		
		// process user input
		String userChoose = confirmUserChoose(form, listableControlList.size());
		try {
			int intUserChoose = Integer.parseInt(userChoose);
			ConsoleControlProxy control = (ConsoleControlProxy) listableControlList.get(intUserChoose - 1);
			control.present();
			return presentPage(pageInstance);
		} catch (NumberFormatException e) {
			return userChoose;
		}
	}

	protected void printUnListableControl(XForm form) {
		List<UIControl> controlList = ((ConsoleForm) form.getUI()).getControlList();
		// print label
		for (UIControl consoleControl : controlList) {
			if (consoleControl instanceof LabelProxy) {
				((LabelProxy) consoleControl).present();
			} else if (consoleControl instanceof ListableControlProxy) {
				break;
			}
		}
	}
	
	private void getListableControlList(GroupProxy container, List<ListableControlProxy> listableControlList) throws Exception {
		List<ConsoleControlProxy> controlList = container.getControlList();
		for (ConsoleControlProxy consoleControl : controlList) {
			if (consoleControl.isEnabled()) {
				if (consoleControl instanceof GroupProxy) {
					getListableControlList((GroupProxy) consoleControl, listableControlList);
				} else if (consoleControl instanceof ListableControlProxy) {
					listableControlList.add((ListableControlProxy)consoleControl);
				}
			}
		}
	}

	protected WizardInstance createWizardInstance(WizardConfig wizardConfig) throws Exception {
		WizardInstance wizardInstance = WizardHelper.createWizardInstance(wizardConfig, WizardInstance.CONSOLE_MODE);
		return wizardInstance;
	}

	protected int processUserChoose(String userChoose, int formIndex, WizardInstance wizardInstance) throws Exception {
		return -1;
	}

	protected String confirmUserChoose(XForm xform, int controlCount) {
		WizardInstance wizardInstance = XFormUtils.getWizardInstance(xform);
		PageInstance currentPage = wizardInstance.getCurrentPage();

		int pageIndex = wizardInstance.getPageList().indexOf(currentPage);
		boolean lastPage = pageIndex == wizardInstance.getPageList().size() - 1;
		boolean validWizard = WizardHelper.isValid(wizardInstance);
		boolean validPage = (validWizard || WizardHelper.isValid(currentPage));

		boolean canNext = (!lastPage && validPage);
		boolean canPrevious = (pageIndex > 0);

		return confirmUserChoose(xform, controlCount, lastPage, validWizard, canNext, canPrevious);
	}

	protected String confirmUserChoose(XForm xform, int controlCount, boolean lastPage, boolean validWizard, boolean canNext, boolean canPrevious) {
		String defaultChoose = (controlCount == 0 ? "N" : "1");
		SystemConsoleProxy.print("\nChoose" + (canNext ? " 'N' for Next Page," : "") + (canPrevious ? " 'P' for Previous Page," : "")
				+ (validWizard ? " 'F' for Finish," : "") + " 'C' for Cancel, or enter field number [" + defaultChoose + "]");
		String userChoose = SystemConsoleProxy.readLine();
		if (userChoose.length() == 0) {
			return defaultChoose;
		} else if ((canNext && userChoose.equalsIgnoreCase(USERCHOOSE_NEXT_PAGE)) || (canPrevious && userChoose.equalsIgnoreCase(USERCHOOSE_PREVIOUS_PAGE))
				|| (validWizard && userChoose.equalsIgnoreCase(USERCHOOSE_FINISH)) || userChoose.equalsIgnoreCase(USERCHOOSE_CANCEL)) {
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
		return confirmUserChoose(xform, controlCount, lastPage, validWizard, canNext, canPrevious);
	}
}
