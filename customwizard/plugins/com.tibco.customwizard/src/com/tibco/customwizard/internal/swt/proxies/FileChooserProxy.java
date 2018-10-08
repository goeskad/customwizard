package com.tibco.customwizard.internal.swt.proxies;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nuxeo.xforms.ui.swt.BuildContext;
import org.nuxeo.xforms.ui.swt.proxies.SWTControlProxy;

import com.tibco.customwizard.xforms.events.FileChoseEvent;

public class FileChooserProxy extends SWTControlProxy implements SelectionListener {
    private String browseType = "dir";

    private int style;

    private boolean showText = true;
    
    private String filterExtensions;

    private String buttonText = "Browse";
    
    private Shell shell;

    private Dialog dialog;

    private Button button;

	//this sentence is added to display a message for DirectoryDialog
    private String dialogMessage = "";
    
   //for tool-951
	public void setFilterExtensions(String filterExtensions) {
		this.filterExtensions = filterExtensions;
	}

	protected void installListeners() {
        super.installListeners();
        if (showText) {
            Text text = (Text) control;
            text.addSelectionListener(this);
        }
    }
    
    public boolean isEditable() {
        return true;
    }
    
	public void setXMLValue(String value) {
		((Text) control).setText(value == null ? "" : value);
	}

	public String getXMLValue() {
		return ((Text) control).getText().replace('\\', '/');
	}
    
    public void widgetDefaultSelected(SelectionEvent e) {
        fireValidateEvent(this);
    }
    
    public void widgetSelected(SelectionEvent e) {
        // do nothing
    }
    
    private void setChoosedFile(String file) {
        setXMLValue(file);
        fireValidateEvent(this);
        getForm().getForm().getProcessor().dispatch(new FileChoseEvent(this, null));
    }

    private Dialog getDialog() {
        if (dialog == null) {
            if (browseType.equals("dir")) {
                DirectoryDialog directoryDialog = new DirectoryDialog(shell);

                //this sentence is added to display a message for DirectoryDialog
                directoryDialog.setMessage(dialogMessage);
          
                directoryDialog.setFilterPath(getXMLValue());
                dialog = directoryDialog;
            } else {
                FileDialog fileDialog = new FileDialog(shell, style);
                fileDialog.setFilterPath(getXMLValue());
                if (filterExtensions != null) {
                    fileDialog.setFilterExtensions(filterExtensions.split(","));
                }
                dialog = fileDialog;
            }
        }
        
      //set filter for filechooser
        if (dialog instanceof FileDialog)
        {
        	if (filterExtensions != null) {
                ((FileDialog)dialog).setFilterExtensions(filterExtensions.split(","));
            } else {
            	((FileDialog)dialog).setFilterExtensions(new String[]{"*.*"});
            }
        }
        return dialog;
    }

    private void openDialog() {
        if (browseType.equals("dir")) {
            DirectoryDialog directoryDialog = (DirectoryDialog) getDialog();
            String currentDir = directoryDialog.open();
            if (currentDir != null) {
                setChoosedFile(currentDir);
            }
        } else {
            FileDialog fileDialog = (FileDialog) getDialog();
            String firstFile = fileDialog.open();

            if (firstFile != null) {
                String currentDir = fileDialog.getFilterPath();
                String[] selectedFiles = fileDialog.getFileNames();
                StringBuffer strbuf = new StringBuffer();
                for (String selectedFile : selectedFiles) {
                    if (strbuf.length() > 0) {
                        strbuf.append(";");
                    }
                    strbuf.append(currentDir + File.separator + selectedFile);
                }
                setChoosedFile(strbuf.toString());
            }
        }
    }

	public void setToolTipText(String value) {
		((Text) control).setToolTipText(value);
		button.setToolTipText(value);
	}

    public Control create(BuildContext context, Composite parent, int style) {
        shell = parent.getShell();

        this.style = style;
        String showTextStr = context.element.getAttribute("showText");
        if (showTextStr != null) {
            showText = Boolean.parseBoolean(showTextStr);
        }
        String tmp = context.element.getAttribute("browseType");
        browseType = tmp == null ? browseType : tmp;
        filterExtensions = context.element.getAttribute("filterExtensions");
        tmp = context.element.getAttribute("buttonText");
        buttonText = tmp == null ? buttonText : tmp;

        //these two sentences are added to display a message for DirectoryDialog
        tmp = context.element.getAttribute("dialogMessage");
        dialogMessage = tmp == null ? dialogMessage : tmp;
       
        Control control = new Text(parent, style);
        if (!showText) {
            control.setVisible(false);
        }
        button = new Button(parent, SWT.PUSH);
        button.setText(buttonText);
        button.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                openDialog();
            }
        });

        return control;
    }
}
