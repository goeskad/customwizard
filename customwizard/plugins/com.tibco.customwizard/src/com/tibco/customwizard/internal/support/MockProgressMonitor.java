package com.tibco.customwizard.internal.support;

import org.eclipse.core.runtime.IProgressMonitor;

public class MockProgressMonitor implements IProgressMonitor {
	public static final MockProgressMonitor INSTANCE = new MockProgressMonitor();
	
	public void beginTask(String name, int totalWork) {
		System.out.println("beginTask: " + name);
	}

	public void done() {
		System.out.println("done: ");
	}

	public void internalWorked(double work) {
		// TODO Auto-generated method stub
		
	}

	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setCanceled(boolean value) {
		// TODO Auto-generated method stub
		
	}

	public void setTaskName(String name) {
		// TODO Auto-generated method stub
		
	}

	public void subTask(String name) {
		System.out.println("subTask: " + name);
	}

	public void worked(int work) {
		// TODO Auto-generated method stub
		
	}
}
