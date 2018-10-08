package com.tibco.customwizard.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.tibco.customwizard.action.ICustomAction;

public class WizardConfig extends ConfigElement {
	private WizardConfig parent;
	
    private URL configFile;

    private String sequence;
    
    private String title;

    private String icon;

    private String description;

    private int width;

    private int height;

    private DataModelConfig dataModelConfig;
    
    private IWizardProcessor wizardProcessor;
    
    private IErrorHandler errorHandler;
    
    private ClassLoader extendedClassLoader;

    private ICustomAction postAction;

    private List<PageGroupConfig> pageGroupList = new ArrayList<PageGroupConfig>();
    
    public WizardConfig getParentWizardConfig() {
		return parent;
	}

	public void setParentWizardConfig(WizardConfig parent) {
		this.parent = parent;
	}

	public URL getConfigFile() {
        return configFile;
    }

    public void setConfigFile(URL configURL) {
        this.configFile = configURL;
    }

    public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public IWizardProcessor getWizardProcessor() {
        return wizardProcessor;
    }

    public void setWizardProcessor(IWizardProcessor wizardProcessor) {
        this.wizardProcessor = wizardProcessor;
    }

	public IErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(IErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ClassLoader getExtendedClassLoader() {
        return extendedClassLoader;
    }

    public void setExtendedClassLoader(ClassLoader extendedClassLoader) {
        this.extendedClassLoader = extendedClassLoader;
    }

	public DataModelConfig getDataModelConfig() {
		return dataModelConfig;
	}

	public void setDataModelConfig(DataModelConfig dataModelConfig) {
		this.dataModelConfig = dataModelConfig;
	}

	public ICustomAction getPostAction() {
		return postAction;
	}

	public void setPostAction(ICustomAction postAction) {
		this.postAction = postAction;
	}

	public List<PageGroupConfig> getPageGroupList() {
		return pageGroupList;
	}

	public void setPageGroupList(List<PageGroupConfig> pageGroupList) {
		this.pageGroupList = pageGroupList;
	}
}
