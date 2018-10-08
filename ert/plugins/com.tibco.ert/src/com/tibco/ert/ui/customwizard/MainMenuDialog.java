package com.tibco.ert.ui.customwizard;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.config.IClassLoaderFactory;
import com.tibco.customwizard.config.PageGroupConfig;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.util.WizardHelper;
import com.tibco.ert.model.utils.AMXClassLoaderFactory;

public class MainMenuDialog {
	private WizardConfig wizardConfig;
    private ClassLoader classLoader;
    
	public MainMenuDialog(WizardConfig wizardConfig) throws Exception {
		this.wizardConfig = wizardConfig;

		try {
			checkCreateClassLodaer();

			if (classLoader != null) {
				new Thread() {
					public void run() {
						try {
							@SuppressWarnings("rawtypes")
							Class obfuscationEngine = classLoader.loadClass("com.tibco.security.ObfuscationEngine");
							@SuppressWarnings("unchecked")
							Method method = obfuscationEngine.getMethod("encrypt", char[].class);
							method.invoke(null, " ".toCharArray());
							System.out.println("PasswordObfuscator initialized");
						} catch (Throwable e) {
							e.printStackTrace();
						}
					};
				}.start();
			}
		} catch (Exception e) {
		}

		if (wizardConfig.getIcon() != null) {
			Window.setDefaultImage(WizardHelper.loadImage(wizardConfig, wizardConfig.getIcon()));
		}
		
		final Shell shell = new Shell(SWT.CLOSE);
		shell.setImage(Window.getDefaultImage());
		Label label = new Label(shell, 64);
		label.setBounds(130, 50, 450, 50);
		Button logo = new Button(shell, 8);
		logo.setBounds(30, 20, 76, 76);

		logo.setImage(WizardHelper.loadImage(wizardConfig, "ui/icons/tibco.bmp"));
		int i = 0;
		shell.setText(wizardConfig.getTitle());
		label.setText(wizardConfig.getDescription());
		PageGroupSelectAction pageGroupSelectAction = new PageGroupSelectAction();
		for (PageGroupConfig pageGroup : wizardConfig.getPageGroupList()) {
			Button button = new Button(shell, 8);
			button.setBounds(30 + 195 * (i % 3), 150 + 195 * (i / 3), 175, 175);
			button.setImage(WizardHelper.loadImage(wizardConfig, "ui/icons/" + pageGroup.getAttributeValue("icon")));
			button.setData("pagegroup", pageGroup);
			button.addSelectionListener(pageGroupSelectAction);
			i++;
		}

		Button cancel = new Button(shell, 8);
		cancel.setText("Close");
		cancel.setBounds(545, 150 + 195 * ((i - 1) / 3 + 1), 50, 20);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		
		shell.pack();
		shell.setSize(shell.getSize().x + 30, shell.getSize().y + 20);
		shell.open();
		while (!shell.isDisposed())
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
	}

	private void checkCreateClassLodaer() {
		String strCLILocation = wizardConfig.getDataModel().getValue("/ert/configuration/cli");

		try {
			classLoader = AMXClassLoaderFactory.createClassLoader(
					new File(wizardConfig.getConfigFile().getFile()).getParent(), strCLILocation,
					IClassLoaderFactory.class.getClassLoader());
			wizardConfig.setExtendedClassLoader(classLoader);
		} catch (Exception e) {
			throw new ActionException(e.getMessage(), e);
		}
	}
	
	class PageGroupSelectAction extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			try {
				Button button = (Button) event.getSource();
				PageGroupConfig pageGroup = (PageGroupConfig) button.getData("pagegroup");
				if (!pageGroup.getId().equals("configuration")) {
					if (classLoader == null) {
						try {
							checkCreateClassLodaer();
						} catch (Exception e) {
							MessageDialog.openError(button.getShell(), "Error", e.getMessage());
							return;
						}
					}
				}
				List<PageGroupConfig> pageGroupList = new ArrayList<PageGroupConfig>();
				pageGroupList.add(pageGroup);
				WizardHelper.openWizardDialog(wizardConfig, pageGroupList, button.getShell());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
