package com.tibco.customwizard.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.browser.DefaultWorkbenchBrowserSupport;
import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;
import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.proxies.CompositeProxy;
import org.nuxeo.xforms.ui.swt.proxies.SWTControlProxy;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.internal.swt.ActionRunner;
import com.tibco.customwizard.internal.swt.CWSWTForm;
import com.tibco.customwizard.internal.swt.CustomWizard;
import com.tibco.customwizard.internal.swt.CustomWizardDialog;
import com.tibco.customwizard.internal.swt.CustomWizardPage;
import com.tibco.customwizard.internal.swt.ErrorDialog;
import com.tibco.customwizard.internal.swt.SWTXFormExtension;
import com.tibco.customwizard.internal.swt.proxies.ScrolledCompositeProxy;
import com.tibco.customwizard.support.IBackgroundAction;
import com.tibco.customwizard.support.IProgressMonitorAware;
import com.tibco.customwizard.support.MessageType;

@SuppressWarnings("restriction")
public class SWTHelper {
	private static SWTXFormExtension xformExtension = new SWTXFormExtension();

	private static final String WIZARD_PROPERTY = "swt.wizard";

	private static Field pagesField;

	private static IWebBrowser brower;
	
	static {
		try {
			pagesField = org.eclipse.jface.wizard.Wizard.class.getDeclaredField("pages");
			pagesField.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openWizardDialog(WizardInstance wizardInstance) throws Exception {
		openWizardDialog(wizardInstance, null);
	}

	public static void openWizardDialog(WizardInstance wizardInstance, Shell parentShell) throws Exception {
		if (Display.getCurrent() == null) {
			PlatformUI.createDisplay();
		}

		WizardConfig wizardConfig = wizardInstance.getWizardConfig();

		IWizard wizard = getWizard(wizardInstance);

		if (wizardConfig.getIcon() != null) {
			Window.setDefaultImage(loadImage(wizardInstance.getWizardConfig(), wizardConfig.getIcon()));
		}

		CustomWizardDialog wizardDialog = new CustomWizardDialog(parentShell, wizard, wizardInstance);

		wizardDialog.setPageGroupList(wizardConfig.getPageGroupList());

		if (parentShell != null) {
			parentShell.setMinimized(true);
			wizardDialog.setBlockOnOpen(false);
		}

		wizardDialog.open();

		if (parentShell != null) {
			wizardDialog.getShell().forceActive();
		}
	}

	public static Wizard createWizard(WizardInstance wizardInstance) throws Exception {
		WizardConfig wizardConfig = wizardInstance.getWizardConfig();
		CustomWizard wizard = new CustomWizard(wizardInstance);
		wizard.setWindowTitle(wizardConfig.getTitle());

		List<PageInstance> pageList = wizardInstance.getPageList();
		for (PageInstance pageInstance : pageList) {
			wizard.addPage((IWizardPage) WizardHelper.getNativePage(pageInstance));
		}

		wizardInstance.setAttribute(WIZARD_PROPERTY, wizard);

		return wizard;
	}

	public static UIForm buildUI(PageInstance pageInstance, Object parent) {
		return xformExtension.getUIBuilder().build(XFormUtils.getXForm(pageInstance), parent);
	}

	public static UIControl createControl(UIControl parent, Class<?> controlClass, int style) throws Exception {
		BuildContext buildContext = new BuildContext(null);
		buildContext.toolkitAdapt = false;
		buildContext.parent = (SWTControlProxy) parent;
		buildContext.style = style;
		AbstractUIControl control = (AbstractUIControl) controlClass.newInstance();
		CWSWTForm uiForm = (CWSWTForm) parent.getElement().getForm().getUI();
		control.create(uiForm, buildContext);
		uiForm.registerControlProxy(control);
		return control;
	}
	
	public static void refreshPage(PageInstance pageInstance) throws Exception {
		Object nativePage = WizardHelper.getNativePage(pageInstance);
		if (nativePage instanceof CustomWizardPage) {
			((CustomWizardPage) nativePage).refresh();
		}
	}

	public static PageInstance getPageInstance(IWizardPage nativePage) throws Exception {
		if (nativePage instanceof CustomWizardPage) {
			return ((CustomWizardPage) nativePage).getPageInstance();
		}
		return null;
	}

	public static IWizardPage createSWTPage(PageInstance pageInstance) {
		IWizardPage nativePage = new CustomWizardPage(pageInstance);
		WizardHelper.setNativePage(pageInstance, nativePage);
		return nativePage;
	}

	public static void resetWizardPages(WizardInstance wizardInstance) {
		IWizard wizard = getWizard(wizardInstance);
		List<IWizardPage> pages = getWizardPages(wizard);
		pages.clear();
		pages.addAll(getWizardPages(wizardInstance.getPageList()));

		IWizardPage currentPage = wizard.getContainer().getCurrentPage();
		Composite pageContainer = currentPage.getControl().getParent();
		for (IWizardPage page : pages) {
			page.setWizard(wizard);
			if (page.getControl() == null) {
				page.createControl(pageContainer);
				if (page.getControl() != null) {
					page.getControl().setVisible(false);
				}
			}
		}

		wizard.getContainer().updateButtons();
	}

	public static void updateWizardButtons(WizardInstance wizardInstance) {
		IWizard wizard = getWizard(wizardInstance);
		if (wizard != null && wizard.getContainer() != null && wizard.getContainer().getCurrentPage() != null) {
			wizard.getContainer().updateButtons();
		}
	}
	
	public static IWizard getWizard(WizardInstance wizardInstance) {
		return (IWizard) wizardInstance.getAttribute(WIZARD_PROPERTY);
	}

	public static Shell getShell(WizardInstance wizardInstance) {
		return getWizard(wizardInstance).getContainer().getShell();
	}
	
	public static void resetWizardInstance(WizardInstance wizardInstance) throws Exception {
		//clean the UI since they are disposed
		for (PageInstance pageInstance : wizardInstance.getPageList()) {
			XFormUtils.getXForm(pageInstance).setUI(null);
		}
	}
	
	public static void executeAction(IActionContext actionContext, ICustomAction action) throws Exception {
		IWizard wizard = getWizard(actionContext.getWizardInstance());
		if (action instanceof IProgressMonitorAware) {
			if (wizard instanceof Wizard) {
				((Wizard) wizard).setNeedsProgressMonitor(true);
			}
			wizard.getContainer().run(true, false, new ActionRunner(actionContext, action));
		} else if (action instanceof IBackgroundAction) {
			Shell shell = wizard.getContainer().getShell();
			Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
			shell.setCursor(waitCursor);
			try {
				action.execute(actionContext);
			} finally {
				shell.setCursor(null);
				waitCursor.dispose();
			}
		} else {
			action.execute(actionContext);
		}
	}

	public static void resetWizardMessage(WizardInstance wizardInstance) {
		IWizard wizard = getWizard(wizardInstance);
		if (wizard != null) {
			wizard.getContainer().updateTitleBar();
		}
	}

	public static void setWizardMessage(WizardInstance wizardInstance, final String message, final int messageType, boolean sync) {
		IWizard wizard = getWizard(wizardInstance);
		if (wizard instanceof Wizard) {
			final WizardDialog wizardDialog = (WizardDialog) ((Wizard) wizard).getContainer();
			if (sync) {
				wizardDialog.getShell().getDisplay().syncExec(new Runnable() {
					public void run() {
						wizardDialog.setErrorMessage(null);
						wizardDialog.setMessage(message, messageType);
					}
				});
			} else {
				wizardDialog.setErrorMessage(null);
				wizardDialog.setMessage(message, messageType);
			}
		}
	}

	public static void openMessage(final String message, final int messageType, boolean sync) {
		if (sync) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					openMessageDialog(Display.getDefault().getActiveShell(), message, messageType);
				}
			});
		} else {
			openMessageDialog(Display.getDefault().getActiveShell(), message, messageType);
		}
	}

	private static void openMessageDialog(Shell shell, String message, int messageType) {
		if (messageType == MessageDialog.WARNING) {
			MessageDialog.openWarning(shell, "Warning", message);
		} else if (messageType == MessageDialog.ERROR) {
			MessageDialog.openWarning(shell, "Error", message);
		} else {
			MessageDialog.openInformation(shell, "Information", message);
		}
	}
	
	public static void openErrorDialog(String errorMessage, IStatus status) {
		ErrorDialog.openError(Display.getDefault().getActiveShell(), errorMessage, status);
	}
	
	public static Shell findActiveShell() {
		if (Display.getCurrent() != null) {
			return Display.getCurrent().getActiveShell();
		}
		return null;
	}
	
	public static void setPageComplete(PageInstance pageInstance, boolean complete) {
		WizardPage wizardPage = (WizardPage) pageInstance.getNativePage();
		if (wizardPage != null) {
			wizardPage.setPageComplete(complete);
		}
	}
	
	public static void redraw(UIControl control) {
		Control swtControl = (Control) control.getControl();
		if (swtControl != null) {
			activeScrolledComposite(control);
			redraw(swtControl);
		}
	}
	
	public static void redraw(Control control) {
		Shell shell = control.getShell();
		shell.setSize(shell.getSize().x + 1, shell.getSize().y + 1);
		shell.setSize(shell.getSize().x - 1, shell.getSize().y - 1);
	}
	
	public static void activeScrolledComposite(UIControl control) {
		if (control instanceof ScrolledCompositeProxy) {
			ScrolledCompositeProxy scControlProxy = (ScrolledCompositeProxy) control;
			ScrolledComposite scControl = (ScrolledComposite) scControlProxy.getControl();
			Point size = scControlProxy.getContainer().computeSize(SWT.DEFAULT, SWT.DEFAULT);
			scControl.setMinSize(size);
		}
	}
	
	public static void removeChildren(UIControl control) {
		Composite container = ((CompositeProxy) control).getContainer();
		if (container != null) {
			Control[] children = container.getChildren();
			for (Control child : children) {
				child.dispose();
			}
		}
	}
	
	
	public static void openURL(URL url) throws Exception {
		if (brower == null) {
			brower = new DefaultWorkbenchBrowserSupport().getExternalBrowser();
		}
		brower.openURL(url);
	}
	
	public static Image loadImage(WizardConfig wizardConfig, String imageFilePath) {
		try {
			File imageFile = WizardHelper.getResourceFile(wizardConfig, imageFilePath);
			InputStream imageStream = new FileInputStream(imageFile);
			Image image = new Image(null, imageStream);
			imageStream.close();
			return image;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static List<IWizardPage> getWizardPages(IWizard wizard) {
		try {
			return (List<IWizardPage>) pagesField.get(wizard);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private static List<IWizardPage> getWizardPages(List<PageInstance> pageList) {
		List<IWizardPage> nativePageList = new ArrayList<IWizardPage>(pageList.size());
		for (PageInstance pageInstance : pageList) {
			nativePageList.add((IWizardPage) WizardHelper.getNativePage(pageInstance));
		}
		return nativePageList;
	}
	
	public static int convertMessageType(int messageType) {
		switch (messageType) {
		case MessageType.NONE:
			return IMessageProvider.NONE;
		case MessageType.INFORMATION:
			return IMessageProvider.INFORMATION;
		case MessageType.WARNING:
			return IMessageProvider.WARNING;
		case MessageType.ERROR:
			return IMessageProvider.ERROR;
		}
		return IMessageProvider.NONE;
	}
}
