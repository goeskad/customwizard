package com.tibco.customwizard.internal.swt;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import com.tibco.customwizard.config.PageGroupConfig;



public class PageGroupTreeViewer extends TreeViewer {
    public PageGroupTreeViewer(Composite parent) {
        super(parent, SWT.BORDER);
        this.setLabelProvider(new LabelProvider());
        this.setContentProvider(new WizardTreeContentProvider());

        this.getTree().addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                TreeItem[] selected = getTree().getSelection();
                Object data = selected[0].getData();
                if (data instanceof IWizardPage) {
                    IWizardPage page = (IWizardPage) data;
                    page.getWizard().getContainer().showPage(page);
                }
            }
        });
    }

    public void updateSelection(IWizardPage page) {
        TreeItem[] roots = getTree().getItems();
        for (TreeItem root : roots) {
            TreeItem[] items = root.getItems();
            for (TreeItem item : items) {
                if (item.getData() == page) {
                    getTree().setSelection(item);
                }
            }
        }
    }
    
    class WizardTreeContentProvider extends ArrayContentProvider implements ITreeContentProvider {
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof PageGroupConfig) {
                PageGroupConfig pageGroup = (PageGroupConfig) parentElement;
                return pageGroup.getPageList().toArray();
            }
            return null;
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            if (element instanceof PageGroupConfig) {
                PageGroupConfig pageGroup = (PageGroupConfig) element;
                return pageGroup.getPageList().isEmpty();
            }
            return false;
        }
    }
}
