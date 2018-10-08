package com.tibco.customwizard.support;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IProgressMonitorAware {
    void setProgressMonitor(IProgressMonitor progressMonitor);
}
