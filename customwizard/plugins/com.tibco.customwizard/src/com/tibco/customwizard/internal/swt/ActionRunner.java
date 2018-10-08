package com.tibco.customwizard.internal.swt;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.support.IProgressMonitorAware;

public class ActionRunner implements IRunnableWithProgress {
    private IActionContext actionContext;
    private ICustomAction action;
    
    public ActionRunner(IActionContext actionContext, ICustomAction action)
    {
        this.actionContext = actionContext;
        this.action = action;
    }
    
    public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
        try {
            if (action instanceof IProgressMonitorAware) {
                ((IProgressMonitorAware) action).setProgressMonitor(progressMonitor);
            } else {
                progressMonitor.beginTask("", IProgressMonitor.UNKNOWN);
                progressMonitor.subTask("");
            }
            action.execute(actionContext);
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        } finally {
            progressMonitor.done();
        }
    }
}
