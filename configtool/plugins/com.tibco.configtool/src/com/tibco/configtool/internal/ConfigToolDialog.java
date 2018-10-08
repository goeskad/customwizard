package com.tibco.configtool.internal;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.tibco.configtool.support.TCTContext;
import com.tibco.configtool.utils.TCTHelper;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.util.SWTHelper;
import com.tibco.customwizard.util.WizardHelper;

public class ConfigToolDialog extends TitleAreaDialog {
    public ConfigToolDialog() {
        super(null);
        setHelpAvailable(true);
    }

    public void create() {
		super.create();
    	SWTHelper.redraw(getShell());
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Register help listener on the shell
		newShell.addHelpListener(new HelpListener() {
			public void helpRequested(HelpEvent event) {
				TCTHelper.performHelp(null);
			}
		});
	}
    
    protected Control createDialogArea(final Composite parent) {
    	WizardConfig mainWizardConfig = TCTContext.getInstance().getWizardInstance().getWizardConfig();

        List<WizardConfig> contributorList = TCTContext.getInstance().getContributorList();
        
        getShell().setText(mainWizardConfig.getTitle());
        this.setTitle(mainWizardConfig.getDescription());
        this.setTitleImage(ConfigToolProcessor.titleImage);
        if (mainWizardConfig.getWidth() != 0 && mainWizardConfig.getHeight() != 0) {
			getShell().setMinimumSize(mainWizardConfig.getWidth(), mainWizardConfig.getHeight());
		}
        
        Composite composite = (Composite) super.createDialogArea(parent);
		Composite mainContainer = new Composite(composite, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        mainContainer.setLayoutData(gridData);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginLeft = 20;
        gridLayout.marginTop = 20;
        mainContainer.setLayout(gridLayout);

		for (final WizardConfig contributor : contributorList) {
			Label imageLabel = new Label(mainContainer, SWT.NONE);
			imageLabel.setImage(SWTHelper.loadImage(contributor, contributor.getIcon()));
			
			Composite textContainer = new Composite(mainContainer, SWT.NONE);
			textContainer.setLayout(new GridLayout(1, false));
			Hyperlink hl = new Hyperlink(textContainer, SWT.NONE);
			Label descLabel = new Label(textContainer, SWT.NONE);
			descLabel.setText(contributor.getDescription());
			hl.setText(contributor.getTitle());
			FontData[] fd = hl.getFont().getFontData();
			fd[0].setHeight(10);
	        final Font font = new Font(hl.getDisplay(), fd);
	        hl.setFont(font);
			hl.setUnderlined(true);
			hl.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			hl.addHyperlinkListener(new HyperlinkAdapter() {
				public void linkActivated(HyperlinkEvent e) {
					try {
						openWizardDialog(contributor, getShell());
					} catch (Exception ex) {
						WizardHelper.handleError(TCTContext.getInstance().getWizardInstance(), ex);
					}
				}
			});
		}
		
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return composite;
    }
	
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL,
                true);
    }
    
	public static void openWizardDialog(WizardConfig wizardConfig, Shell parentShell) throws Exception {
		WizardInstance wizardInstance = TCTHelper.createWizardInstance(wizardConfig, WizardInstance.SWT_MODE);
		IWizard wizard = SWTHelper.getWizard(wizardInstance);
		
		ConfigToolWizardDialog wizardDialog = new ConfigToolWizardDialog(parentShell, wizard, wizardInstance);
		wizardDialog.setTitleImage(ConfigToolProcessor.titleImage);
		
		wizardDialog.setBlockOnOpen(false);
		wizardDialog.open();
		wizardDialog.getShell().forceActive();
	}
}
