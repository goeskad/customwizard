package com.tibco.customwizard.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.nuxeo.xforms.ui.UIControl;
import org.osgi.framework.Bundle;

import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.IClassLoaderFactory;
import com.tibco.customwizard.config.IWizardProcessor;
import com.tibco.customwizard.config.PageConfig;
import com.tibco.customwizard.config.PageGroupConfig;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.console.proxies.ConsoleControlProxy;
import com.tibco.customwizard.internal.support.BaseActionContext;
import com.tibco.customwizard.internal.support.DefaultErrorHandler;
import com.tibco.customwizard.internal.support.MockProgressMonitor;
import com.tibco.customwizard.internal.support.SystemPropertiesDataModel;
import com.tibco.customwizard.internal.swt.SWTProcessor;
import com.tibco.customwizard.support.ConsoleProcessor;
import com.tibco.customwizard.support.IProgressMonitorAware;
import com.tibco.customwizard.support.IWizardConfigLocator;
import com.tibco.customwizard.support.MessageType;
import com.tibco.customwizard.support.SystemConsoleProxy;
import com.tibco.customwizard.validator.ValidateResult;

public class WizardHelper {
    private static final String CONFIG_LOCATION_PROPERTY = "cw.config.location";

    private static final String WIZARD_VALIDATE_RESULT = "validate.result";
    
    private static IWizardConfigLocator wizardConfigLocator;
    
    private static Bundle cwBundle = Platform.getBundle("com.tibco.customwizard");
    
	public static IWizardConfigLocator getWizardConfigLocator() {
		return wizardConfigLocator;
	}

	public static void setWizardConfigLocator(IWizardConfigLocator wizardConfigLocator) {
		WizardHelper.wizardConfigLocator = wizardConfigLocator;
	}

	public static IWizardProcessor getDefaultWizardProcessor(int displayMode) throws Exception {
		if (displayMode == WizardInstance.SWT_MODE) {
			return SWTProcessor.getInstance();
		} else {
			return ConsoleProcessor.getInstance();
		}
	}
    
    public static WizardConfig loadWizardConfig(URL configFile) throws Exception {
		return loadWizardConfig(configFile, IClassLoaderFactory.class.getClassLoader());
	}
	
	public static WizardConfig loadWizardConfig(URL configFile, WizardConfig parent) throws Exception {
		WizardConfig wizardConfig = loadWizardConfig(configFile, parent.getExtendedClassLoader());
		if (wizardConfig.getParentWizardConfig() == null) {
			wizardConfig.setParentWizardConfig(parent);
		}
		return wizardConfig;
	}
    
	public static WizardConfig loadWizardConfig(URL configFile, ClassLoader parent) throws Exception {
		WizardConfig wizardConfig = new WizardConfig();
		wizardConfig.setConfigFile(configFile);
		wizardConfig.setExtendedClassLoader(parent);
		
		XFormUtils.parseWizardConfig(configFile, wizardConfig);
		
		if (wizardConfig.getErrorHandler() == null) {
			wizardConfig.setErrorHandler(new DefaultErrorHandler());
		}
		
		return wizardConfig;
	}
	
	public static WizardInstance createWizardInstance(WizardConfig wizardConfig, int displayMode) throws Exception {
		return createWizardInstance(wizardConfig, wizardConfig.getPageGroupList(), displayMode);
	}

	public static WizardInstance createWizardInstance(WizardConfig wizardConfig, List<PageGroupConfig> pageGroupList, int displayMode)
			throws Exception {
		WizardInstance wizardInstance = new WizardInstance(wizardConfig);
		wizardInstance.setDisplayMode(displayMode);

		wizardInstance.setDataModel(DataModelHelper.createDataModel(wizardConfig));
		
		List<PageInstance> pageList = new ArrayList<PageInstance>();
		for (PageGroupConfig group : pageGroupList) {
			for (PageConfig pageConfig : group.getPageList()) {
				if (group.isVisible()) {
					PageInstance pageInstance = createPage(wizardInstance, pageConfig);
					pageList.add(pageInstance);
				}
			}
		}
		wizardInstance.setPageList(pageList);

		if (WizardHelper.isSWTMode(wizardInstance)) {
			SWTHelper.createWizard(wizardInstance);
		}
		
		return wizardInstance;
	}

	public static PageInstance createPage(WizardInstance wizardInstance, PageConfig pageConfig) throws Exception {
		PageInstance pageInstance = new PageInstance(wizardInstance, pageConfig);
		
		String referenceFile = pageConfig.getReferenceFile();
		if (referenceFile != null) {
			XFormUtils.loadXForm(pageInstance, referenceFile);
		}
		
		if (WizardHelper.isSWTMode(wizardInstance)) {
			SWTHelper.createSWTPage(pageInstance);
		} else if (WizardHelper.isConsoleMode(wizardInstance)) {
			ConsoleHelper.buildUI(pageInstance);
		}
		
		wizardInstance.setPageInstance(pageConfig, pageInstance);
		
		return pageInstance;
	}

	public static boolean isSWTMode(WizardInstance wizardInstance) {
		return wizardInstance.getDisplayMode() == WizardInstance.SWT_MODE;
	}
	
	public static boolean isSWTMode(UIControl control) {
		return isSWTMode(XFormUtils.getWizardInstance(control));
	}
	
	public static boolean isConsoleMode(WizardInstance wizardInstance) {
		return wizardInstance.getDisplayMode() == WizardInstance.CONSOLE_MODE;
	}
	
	public static boolean isConsoleMode(UIControl control) {
		return control instanceof ConsoleControlProxy;
	}
	
	public static void refreshPage(PageInstance pageInstance) throws Exception {
		XFormUtils.refreshXForm(pageInstance);

		if (isSWTMode(pageInstance.getWizardInstance())) {
			SWTHelper.refreshPage(pageInstance);
		} else if (isConsoleMode(pageInstance.getWizardInstance())) {
			ConsoleHelper.buildUI(pageInstance);
		}
	}

	public static ValidateResult getValidateResult(PageInstance pageInstance) {
		return XFormUtils.getValidateResult(pageInstance);
	}

	public static void setWizardValidateResult(WizardInstance wizardInstance, ValidateResult validateResult) {
		wizardInstance.setAttribute(WIZARD_VALIDATE_RESULT, validateResult);
	}

	public static ValidateResult getWizardValidateResult(WizardInstance wizardInstance) {
		return (ValidateResult) wizardInstance.getAttribute(WIZARD_VALIDATE_RESULT);
	}
	
	public static void validate(PageInstance pageInstance) {
		XFormUtils.validate(pageInstance);
	}
	
	public static boolean isValid(PageInstance pageInstance) {
		ValidateResult validateResult = getValidateResult(pageInstance);
		if (validateResult != null && !validateResult.isValid()) {
			return false;
		}
		return true;
	}
	
	public static boolean isValid(WizardInstance wizardInstance) {
		ValidateResult validateResult = getWizardValidateResult(wizardInstance);
		if (validateResult != null && !validateResult.isValid()) {
			return false;
		}
		
		List<PageInstance> pageList = wizardInstance.getPageList();
		for (PageInstance pageInstance : pageList) {
			if (!isValid(pageInstance)) {
				return false;
			}
		}
		return true;
	}
	
    public static Object getNativePage(PageInstance pageInstance) {
    	return pageInstance.getNativePage();
    }
    
    public static void setNativePage(PageInstance pageInstance, Object nativePage) {
    	pageInstance.setNativePage(nativePage);
    }
    
	public static PageInstance getPageInstanceById(WizardInstance wizardInstance, String pageId) {
		List<PageInstance> pageList = wizardInstance.getPageList();
		for (PageInstance pageInstance : pageList) {
			String testPageId = pageInstance.getPageConfig().getId();
			if (testPageId.equals(pageId)) {
				return pageInstance;
			}
		}
		return null;
	}
	
	public static PageConfig getPageConfigById(WizardInstance wizardInstance, String pageId) {
		List<PageGroupConfig> pageGroupList = wizardInstance.getWizardConfig().getPageGroupList();
		for (PageGroupConfig pageGroupConfig : pageGroupList) {
			for (PageConfig pageConfig : pageGroupConfig.getPageList()) {
				String testPageId = pageConfig.getId();
				if (testPageId.equals(pageId)) {
					return pageConfig;
				}
			}
		}
		return null;
	}
	
	public static void setWizardPageByGroupIds(WizardInstance wizardInstance, String[] pageGroupIds) throws Exception {
		List<PageConfig> pageConfigList = new ArrayList<PageConfig>();
		for (String pageGroupId : pageGroupIds) {
			pageConfigList.addAll(WizardHelper.getPageGroupById(wizardInstance.getWizardConfig(), pageGroupId).getPageList());
		}
		setWizardPages(wizardInstance, pageConfigList);
	}
	
	public static void setWizardPages(WizardInstance wizardInstance, List<PageConfig> pageConfigList) throws Exception {
		List<PageInstance> pageList = wizardInstance.getPageList();
		pageList.clear();
		for (PageConfig pageConfig : pageConfigList) {
			PageInstance pageInstance = wizardInstance.getPageInstance(pageConfig);
			if (pageInstance == null) {
				pageInstance = createPage(wizardInstance, pageConfig);
			}
			pageList.add(pageInstance);
		}
		
		if (isSWTMode(wizardInstance)) {
			SWTHelper.resetWizardPages(wizardInstance);
		}
	}
	
    public static PageGroupConfig getPageGroupById(WizardConfig wizardConfig, String pageGroupId) {
        for (PageGroupConfig pageGroup : wizardConfig.getPageGroupList()) {
            if (pageGroupId.equals(pageGroup.getId())) {
                return pageGroup;
            }
        }
        return null;
    }
	
	public static void replaceNextPage(WizardInstance wizardInstance, String pageId) {
		PageInstance currentPage = wizardInstance.getCurrentPage();
		PageInstance targetPage = getPageInstanceById(wizardInstance, pageId);
		if (targetPage == null) {
			throw new ActionException("Can not find the page by id: " + pageId);
		}

		List<PageInstance> pageList = wizardInstance.getPageList();
		int index = pageList.indexOf(currentPage);
		if (index == pageList.size() - 1) {
			pageList.add(targetPage);
		} else {
			pageList.set(index + 1, targetPage);
		}
		
		if (isSWTMode(wizardInstance)) {
			SWTHelper.resetWizardPages(wizardInstance);
		}
	}

	public static File getDefaultWizardConfigFile() {
		String strConfigFile = System.getProperty(CONFIG_LOCATION_PROPERTY);
		File configFile = null;
		if (strConfigFile != null) {
			configFile = new File(strConfigFile);
		} else {
			configFile = new File(Platform.getInstallLocation().getURL().getFile(), "WizardConfig.xml");
			System.setProperty(CONFIG_LOCATION_PROPERTY, configFile.getAbsolutePath());
		}
		return configFile;
	}
	
    public static WizardConfig loadDefaultWizardConfig() throws Exception {
        WizardConfig wizardConfig = loadWizardConfig(toURL(getDefaultWizardConfigFile()));
        return wizardConfig;
    }

	public static void executePostAction(WizardInstance wizardInstance) throws Exception {
		executePostAction(wizardInstance, false);
	}

	public static void executePostAction(WizardInstance wizardInstance, boolean failWhenNoneAction) throws Exception {
		if (wizardInstance.getWizardConfig().getPostAction() != null) {
			executeAction(wizardInstance, wizardInstance.getWizardConfig().getPostAction());
		} else if (failWhenNoneAction) {
			throw new ActionException("Current wizard config doesn't have post action");
		}
	}
    
	public static void executeAction(WizardInstance wizardInstance, ICustomAction action) throws Exception {
		executeAction(new BaseActionContext(wizardInstance), action);
	}
	
	public static void executeAction(IActionContext actionContext, ICustomAction action) throws Exception {
		if (isSWTMode(actionContext.getWizardInstance())) {
			SWTHelper.executeAction(actionContext, action);
		} else {
			if (action instanceof IProgressMonitorAware) {
				((IProgressMonitorAware) action).setProgressMonitor(MockProgressMonitor.INSTANCE);
			}
			action.execute(actionContext);
		}
	}

	public static File getResourceFile(WizardConfig wizardConfig, String path) {
		URL configFile = wizardConfig.getConfigFile();
		File resouceFile = new File(getAbsolutePath(configFile, path));
		if (resouceFile.exists()) {
			return resouceFile;
		} else if (wizardConfig.getParentWizardConfig() != null) {
			return getResourceFile(wizardConfig.getParentWizardConfig(), path);
		}
		return null;
	}
	
    public static String getAbsolutePath(File relativeFile, String path) {
        if (!new File(path).isAbsolute()) {
            if (!relativeFile.isDirectory()) {
                relativeFile = relativeFile.getParentFile();
            }
            return new File(relativeFile, path).getAbsolutePath();
        }
        return path;
    }
    
    public static String getAbsolutePath(URL relativeFile, String path) {
        return getAbsolutePath(new File(relativeFile.getFile()), path);
    }
    
    public static String subtitutionSystemProperties(URL file) throws Exception {
		InputStream in = file.openStream();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] data = new byte[1024];

		try {
			int len = 0;
			while ((len = in.read(data)) != -1) {
				bout.write(data, 0, len);
			}
		} finally {
			in.close();
		}
		String content = new String(bout.toByteArray());
		content = SubtitutionUtils.subtitution(content, SystemPropertiesDataModel.getInstance());
		return content;
	}
	
	public static void resetUIForm(WizardInstance wizardInstance) throws Exception {
		for (PageInstance pageInstance : wizardInstance.getPageList()) {
			XFormUtils.getXForm(pageInstance).reset();
		}
	}
	
	public static void resetWizardMessage(WizardInstance wizardInstance) {
		if (isSWTMode(wizardInstance)) {
			SWTHelper.resetWizardMessage(wizardInstance);
		}
	}
    
	public static void updateWizardButtons(WizardInstance wizardInstance) {
		if (isSWTMode(wizardInstance)) {
			SWTHelper.updateWizardButtons(wizardInstance);
		}
	}
	
	public static void setWizardMessage(WizardInstance wizardInstance, String message, int messageType) {
		setWizardMessage(wizardInstance, message, messageType, false);
	}
    
	public static void setWizardMessage(WizardInstance wizardInstance, final String message, final int messageType, boolean sync) {
		if (isSWTMode(wizardInstance)) {
			SWTHelper.setWizardMessage(wizardInstance, message, SWTHelper.convertMessageType(messageType), sync);
		} else {
			if (messageType == MessageType.WARNING) {
				SystemConsoleProxy.print("[Warning] ");
			} else if (messageType == MessageType.ERROR) {
				SystemConsoleProxy.print("[Error] ");
			}
			SystemConsoleProxy.println(message);
		}
	}

	public static void syncSetMessage(WizardInstance wizardInstance, String message) {
		setWizardMessage(wizardInstance, message, MessageType.NONE, true);
	}
    
	public static IStatus createStatus(String errorMessage, Throwable e) {
		if (errorMessage == null) {
			errorMessage = getRootErrorMessage(e);
		}
		Status status = new Status(IStatus.ERROR, "com.tibco.customwizard", errorMessage, e);
		return status;
	}
	
    public static IStatus logException(String errorMessage, Throwable e) {
    	IStatus status = createStatus(errorMessage, e);
		Platform.getLog(cwBundle).log(status);
    	return status;
    }

	public static void openMessage(WizardInstance wizardInstance, final String message, final int messageType, boolean sync) {
		if (isSWTMode(wizardInstance)) {
			SWTHelper.openMessage(message, messageType, sync);
		} else {
			if (messageType == MessageType.WARNING) {
				SystemConsoleProxy.print("[Warning] ");
			} else if (messageType == MessageType.ERROR) {
				SystemConsoleProxy.print("[Error] ");
			}
			SystemConsoleProxy.println(message);
		}
	}
    
	public static void openMessage(WizardInstance wizardInstance, String message) {
		openMessage(wizardInstance, message, MessageType.INFORMATION, false);
	}
	
	public static void openErrorMessage(WizardInstance wizardInstance, String message) {
		openMessage(wizardInstance, message, MessageType.ERROR, false);
	}
	
	public static void syncOpenMessage(WizardInstance wizardInstance, String message) {
		openMessage(wizardInstance, message, MessageType.INFORMATION, true);
	}
	
	public static void openErrorDialog(WizardInstance wizardInstance, Throwable e) {
		openErrorDialog(wizardInstance, e.getMessage(), e);
	}
    
    public static void openErrorDialog(WizardInstance wizardInstance, String errorMessage, Throwable e) {
    	openErrorDialog(wizardInstance, errorMessage, createStatus(null, e));
    }
    
	public static void openErrorDialog(WizardInstance wizardInstance, String errorMessage, IStatus status) {
		if (isSWTMode(wizardInstance)) {
			SWTHelper.openErrorDialog(errorMessage, status);
		} else {
			SystemConsoleProxy.println("[Error] " + errorMessage);
			if (status.getException() != null) {
				status.getException().printStackTrace();
			}
		}
	}
	
	public static Throwable getRootCause(Throwable e) {
		Throwable cause = e;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}

	public static String getRootErrorMessage(Throwable e) {
		Throwable rootCause = getRootCause(e);
		String root = rootCause.getMessage();

		if (root == null) {
			root = rootCause.toString();
		} else {
			int index = root.indexOf('\n');
			if (index > 0) {
				root = root.substring(0, index);
			}
		}
		return root;
	}
	
	public static void handleError(WizardInstance wizardInstance, Exception e) {
		wizardInstance.getWizardConfig().getErrorHandler().handleError(wizardInstance, e);
	}
	
	public static void redraw(UIControl control) {
		if (isSWTMode(control)) {
			SWTHelper.redraw(control);
		}
	}
	
	public static void openURL(WizardInstance wizardInstance, URL url) throws Exception {
		if (isSWTMode(wizardInstance)) {
			SWTHelper.openURL(url);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static URL toURL(File file) throws MalformedURLException {
		return file.toURL();
	}
}
