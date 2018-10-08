package com.tibco.customwizard.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.PageConfig;
import com.tibco.customwizard.config.WizardConfig;

public class WizardInstance {
	public static final int SWT_MODE = 1;
	public static final int CONSOLE_MODE = 2;

	private WizardConfig wizardConfig;

	private Map<PageConfig, PageInstance> pageMap = new HashMap<PageConfig, PageInstance>();
	
	private List<PageInstance> pageList;

	private PageInstance currentPage;
	
	private IDataModel dataModel;

	private int displayMode = SWT_MODE;
	
	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	
	public WizardInstance(WizardConfig wizardConfig) {
		this.wizardConfig = wizardConfig;
	}

	public WizardConfig getWizardConfig() {
		return wizardConfig;
	}

	public PageInstance getPageInstance(PageConfig pageConfig) {
		return pageMap.get(pageConfig);
	}
	
	public void setPageInstance(PageConfig pageConfig, PageInstance pageInstance) {
		pageMap.put(pageConfig, pageInstance);
	}

	public List<PageInstance> getPageList() {
		return pageList;
	}

	public void setPageList(List<PageInstance> pageList) {
		this.pageList = pageList;
	}

	public IDataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(IDataModel dataModel) {
		this.dataModel = dataModel;
	}

	public PageInstance getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(PageInstance currentPage) {
		this.currentPage = currentPage;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}
	
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}
}
