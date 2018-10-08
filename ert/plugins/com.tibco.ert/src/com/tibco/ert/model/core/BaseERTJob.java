package com.tibco.ert.model.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;

import com.tibco.ert.model.matrix.AdminDBDelegate;
import com.tibco.ert.model.matrix.AdminServicesDelegate;
import com.tibco.ert.model.utils.ERTUtil;

public class BaseERTJob {
	public static final String TARGET_ALL = "*";
	
	public static final String USER_PASSWORD_FILE = "users.txt";

	protected Log log = LogFactory.getLog(getClass());

	private IProgressMonitor progressMonitor;

	protected AdminServicesDelegate delegate;

	private AdminDBDelegate dbDelegate;

	/**
	 * set the progress monitor
	 * 
	 * @param progressMonitor
	 */
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	/**
	 * get the progress monitor
	 * 
	 * @return progress monitor
	 */
	public IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	/**
	 * update the status of progress monitor
	 * 
	 * @param subTask
	 */
	public void updateProgressStatus(String subTask) {
		if (progressMonitor != null) {
			progressMonitor.subTask(subTask);
		}
		log.info(subTask);
	}

	public void worked(int work) {
		if (progressMonitor != null) {
//			progressMonitor.
			progressMonitor.worked(work);
		}
	}
	
	public AdminDBDelegate getDbDelegate() throws Exception {
		if (dbDelegate == null && delegate != null) {
			dbDelegate = ERTUtil.getDBDelegate(delegate);
		}
		return dbDelegate;
	}

	public void release() throws Exception {
		if (dbDelegate != null) {
			dbDelegate.release();
			dbDelegate = null;
		}
	}
}
