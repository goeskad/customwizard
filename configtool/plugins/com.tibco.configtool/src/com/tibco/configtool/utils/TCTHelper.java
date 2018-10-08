package com.tibco.configtool.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Combo;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.swt.SWTForm;

import com.tibco.configtool.actions.SaveDataModelAction;
import com.tibco.configtool.internal.support.ActionLogger;
import com.tibco.configtool.internal.support.AsyncCommandExecutor;
import com.tibco.configtool.internal.support.DefaultMessageProvider;
import com.tibco.configtool.internal.support.LaunchStatusChecker;
import com.tibco.configtool.internal.support.ProgressMonitorProxy;
import com.tibco.configtool.internal.urlparser.Constants;
import com.tibco.configtool.support.HelpContext;
import com.tibco.configtool.support.IMessageProvider;
import com.tibco.configtool.support.TCTContext;
import com.tibco.customwizard.action.ActionException;
import com.tibco.customwizard.action.IActionContext;
import com.tibco.customwizard.config.IDataModel;
import com.tibco.customwizard.config.PageConfig;
import com.tibco.customwizard.config.WizardConfig;
import com.tibco.customwizard.instance.PageInstance;
import com.tibco.customwizard.instance.WizardInstance;
import com.tibco.customwizard.support.XMLDataModel;
import com.tibco.customwizard.util.ConsoleHelper;
import com.tibco.customwizard.util.WizardHelper;

public class TCTHelper {
	public static final String fileSeparator = "/";
	
	private static final String DATA_MODEL_FILE = "datamodel.file";
	
	private static final String MESSAGE_PROVIDER = "message.provider";
	
	private static final String SUMMARY_PAGE_ID = "summary";
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private static Method antCallMethod;

	private static TransformerFactory transformerFactory = TransformerFactory.newInstance("org.apache.xalan.processor.TransformerFactoryImpl", TCTHelper.class.getClassLoader());

	public static IMessageProvider getMessageProvider(WizardInstance wizardInstance) throws Exception {
		IMessageProvider messageProvider = (IMessageProvider) wizardInstance.getAttribute(MESSAGE_PROVIDER);
		if (messageProvider == null) {
			IMessageProvider rootMessager = null;
			if (wizardInstance != TCTContext.getInstance().getWizardInstance()) {
				rootMessager = getMessageProvider(TCTContext.getInstance().getWizardInstance());
			}
			
			Properties props = getProperties(wizardInstance, "message.properties", "res/message.properties");
			if (props == null) {
				messageProvider = rootMessager;
			} else {
				messageProvider = new DefaultMessageProvider(rootMessager, props);
			}
			wizardInstance.setAttribute(MESSAGE_PROVIDER, messageProvider);
		}
		return messageProvider;
	}
	
	public static Properties getConfigProperties(WizardInstance wizardInstance) throws Exception {
		return getProperties(wizardInstance, "config.properties", "res/config.properties");
	}

	public static Properties loadProperties(WizardInstance wizardInstance, String file) throws Exception {
		String path = WizardHelper.getAbsolutePath(wizardInstance.getWizardConfig().getConfigFile(), file);
		if (!new File(path).exists()) {
			return null;
		}

		Properties props = new Properties();
		FileInputStream fin = new FileInputStream(path);
		props.load(fin);
		fin.close();

		return props;
	}
	
	private static Properties getProperties(WizardInstance wizardInstance, String key, String file) throws Exception {
		Properties props = (Properties) wizardInstance.getAttribute(key);
		if (props == null) {
			props = loadProperties(wizardInstance, file);
			wizardInstance.setAttribute(key, props);
		}
		return props;
	}

	public static WizardInstance createWizardInstance(WizardConfig wizardConfig, int displayMode) throws Exception {
//		WizardInstance wizardInstance = TCTContext.getInstance().getWizardInstance(wizardConfig);
//		if (wizardInstance == null) {
//			wizardInstance = WizardHelper.createWizardInstance(wizardConfig, displayMode);
//			TCTContext.getInstance().setWizardInstance(wizardConfig, wizardInstance);
//		} else {
//			WizardHelper.resetWizardInstance(wizardInstance);
//		}

		return WizardHelper.createWizardInstance(wizardConfig, displayMode);
	}
	
	public static String toUnixPath(String path) {
		return path.replace('\\', '/');
	}
	
	@SuppressWarnings("deprecation")
	public static URL toURL(File file) throws Exception {
		return file.toURL();
	}
	
	public static void writeFile(File file, byte[] content) throws IOException {
		FileOutputStream fout = new FileOutputStream(file);
		try {
			fout.write(content);
		} finally {
			fout.close();
		}
	}

	public static byte[] readFile(File file) throws IOException {
		return readStream(new FileInputStream(file));
	}

	public static byte[] readStream(InputStream in) throws IOException  {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] data = new byte[1024];

		try {
			int len = 0;
			while ((len = in.read(data)) != -1) {
				bout.write(data, 0, len);
			}
		} finally {
			in.close();
		}
		return bout.toByteArray();
	}
	
	public static void copyFile(File source, File target) throws IOException {
		writeFile(target, readFile(source));
	}
	
	public static void revalidateCurrentPage(WizardInstance wizardInstance) {
		WizardHelper.validate(wizardInstance.getCurrentPage());
		WizardHelper.updateWizardButtons(wizardInstance);
	}

	public static void preValidatePage(WizardInstance wizardInstance, String pageId) throws Exception {
		PageInstance pageInstance = WizardHelper.getPageInstanceById(wizardInstance, pageId);
		pageInstance.performPreAction();
		WizardHelper.validate(pageInstance);
		WizardHelper.updateWizardButtons(wizardInstance);
	}
	
	public static PageInstance getSummaryPage(WizardInstance wizardInstance) {
		return WizardHelper.getPageInstanceById(wizardInstance, SUMMARY_PAGE_ID);
	}
	
	public static PageConfig getSummaryPageConfig(WizardInstance wizardInstance) {
		return WizardHelper.getPageConfigById(wizardInstance, SUMMARY_PAGE_ID);
	}
	
	public static void updateCombo(UIControl comboProxy, Map<String,String> valueMap) {
		String[] values = (String[]) valueMap.keySet().toArray(new String[valueMap.size()]);
		String[] labels = (String[]) valueMap.values().toArray(new String[valueMap.size()]);
		
		updateCombo(comboProxy, values, labels);
	}
	
	public static void updateCombo(UIControl comboProxy, Set<String> valueSet) {
		String[] values = (String[]) valueSet.toArray(new String[valueSet.size()]);

		updateCombo(comboProxy, values, values);
	}

	public static void updateCombo(UIControl comboProxy, String[] values, String[] labels) {
		if (TCTContext.getInstance().isConsoleMode()) {
			ConsoleHelper.updateCombo(comboProxy, values, labels);
		} else {
			Combo combo = (Combo) comboProxy.getControl();
			combo.removeAll();
			for (String label : labels) {
				combo.add(label);
			}
			combo.setData(SWTForm.ITEMS_KEY, values);
		}
		
		String defaultValue = getDataModelValue(comboProxy);
		if (defaultValue != null && defaultValue.length() > 0) {
			comboProxy.setXMLValue(defaultValue);
		}
		
		if (!TCTContext.getInstance().isConsoleMode()) {
			Combo combo = (Combo) comboProxy.getControl();
			if (combo.getSelectionIndex() == -1) {
				combo.select(0);
			}
		}
		
		comboProxy.validate();
		comboProxy.commit();
	}
	
	public static Set<String> getInstances(String configName, String label) {
		File instanceConfigDir = new File(TCTContext.getInstance().getTibcoConfigHome(), configName);
		Set<String> instanceSet = new HashSet<String>();
		File[] instances = instanceConfigDir.listFiles();
		if (instances != null) {
			for (File instance : instances) {
				if (instance.isDirectory()) {
					instanceSet.add(instance.getName());
				}
			}
		}

		if (instanceSet.isEmpty()) {
			throw new ActionException("No " + label + " instance has been found, please go back to create a new "
					+ label + " instance first.");
		}
		return instanceSet;
	}

	public static void callAnt(File buildFile, String targetName, Properties props, WizardInstance wizardInstance,
			File logFile, IProgressMonitor progressMonitor) throws Exception {
		ActionLogger log = new ActionLogger(wizardInstance, logFile);
		try {
			callAnt(buildFile, targetName, props, log, progressMonitor);
		} finally {
			boolean error = log.checkError();
			log.close();
			if (error) {
				throw new ActionException("Exception happened in ant logger");
			}
		}
	}

	private static void callAnt(File buildFile, String targetName, Properties props, PrintStream log,
			IProgressMonitor progressMonitor) throws Exception {
		if (antCallMethod == null) {
			TCTContext context = TCTContext.getInstance();
			String tibcoHome = context.getTibcoHome();
			URL[] urls = new URL[2];
			urls[0] = context.getTctJarUrl();
			urls[1] = toURL(new File(tibcoHome, "tools/lib/antpackage.jar"));
			URLClassLoader antClassLoader = new URLClassLoader(urls);
			Class<?> antCaller = antClassLoader.loadClass("com.tibco.configtool.internal.support.AntCaller");
			antCallMethod = antCaller.getMethod("callAnt", new Class[] { File.class, String.class, Properties.class,
					PrintStream.class, Writer.class });
		}
		antCallMethod.invoke(null, buildFile, targetName, props, log, new ProgressMonitorProxy(progressMonitor));
	}

	public static int execCommand(String[] cmdarray, File dir, IProgressMonitor progressMonitor) throws Exception {
		return execCommand(cmdarray, dir, (PrintStream) null, progressMonitor);
	}

	public static int execCommand(String[] cmdarray, File dir, File logFile, IProgressMonitor progressMonitor)
			throws Exception {
		PrintStream log = new PrintStream(logFile);
		try {
			return execCommand(cmdarray, dir, log, progressMonitor);
		} finally {
			log.close();
		}
	}
	
	public static int execCommand(String[] cmdarray, File dir, PrintStream log, IProgressMonitor progressMonitor)
			throws Exception {
		StringBuffer cmd = new StringBuffer();
		for (String cmdStr : cmdarray) {
			if (cmd.length() > 0) {
				cmd.append(' ');
			}
			cmd.append(cmdStr);
		}
		
		if (log != null) {
			log.println("Exec command: [" + cmd + "] in dir[" + dir.getAbsolutePath() + "]");
		}
		
		Process proc = Runtime.getRuntime().exec(cmdarray, null, dir);
		StringBuffer errors = new StringBuffer();
		String line;

		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		while ((line = reader.readLine()) != null) {
			if (log != null) {
				log.println(line);
			}
			if (progressMonitor != null) {
				progressMonitor.subTask(line);
			}
		}

		reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		while ((line = reader.readLine()) != null) {
			if (log != null) {
				log.println(line);
			}
			errors.append(line);
		}

		proc.waitFor();

		if (errors.length() > 0) {
			throw new ActionException(errors.toString());
		}
		
		return proc.exitValue();
	}

	public static boolean launch(String[] cmdarray, File dir, File logFile, String[] statusCommandArray,
			String returnValue, int interval) throws Exception {
		try {
			LaunchStatusChecker statusChecker = new LaunchStatusChecker(statusCommandArray, dir, returnValue, interval);
			AsyncCommandExecutor executor = new AsyncCommandExecutor(cmdarray, dir, logFile, statusChecker);
			executor.start();
			return statusChecker.checkStatus();
		} catch (Exception e) {
			throw e;
		} catch (Throwable e) {
			throw new ActionException(e);
		}
	}
	
	public static File storeDataModel(IDataModel dataModel, File dir) throws Exception {
		if (dataModel instanceof XMLDataModel) {
			File dataFile = new File(dir, "data.xml");
			FileWriter fileWriter = new FileWriter(dataFile);
			try {
				((XMLDataModel) dataModel).serialize(fileWriter);
			} finally {
				fileWriter.close();
			}
			return dataFile;
		}
		return null;
	}

	public static void processXSL(File xmlFile, File xslFile, File output, Map<String, Object> params) throws Exception {
		Source xmlSource = new StreamSource(xmlFile);
		Source xsltSource = new StreamSource(xslFile);

		// do escape for properties file
		if (output.getName().endsWith(".properties")) {
			String content = new String(readFile(xmlFile));
			content = content.replace("\\", "\\\\");
			xmlSource = new StreamSource(new StringReader(content));
		}
		
		Transformer trans = transformerFactory.newTransformer(xsltSource);
		if (params != null) {
			for (Entry<String, Object> param : params.entrySet()) {
				trans.setParameter(param.getKey(), param.getValue());
			}
		}
		trans.transform(xmlSource, new StreamResult(output.toURI().getPath()));
	}
	
	public static String createSessionId() {
		return df.format(new Date());
	}

	public static String wrapCommand(String command, boolean newWindow) {
		String os = Platform.getOS();
		String wrapper = "";
		if (os.equals(Platform.OS_WIN32)) {
			wrapper = "cmd /c ";
			if (newWindow) {
				wrapper = wrapper + "start ";
			}
		}
		return wrapper + command;
	}

	public static String getDataModelValue(UIControl control) {
		return control.getElement().getNodeReference().getNode().getTextContent();
	}
	
	public static void removeDataModelFile(WizardInstance wizardInstance) {
		wizardInstance.setAttribute(DATA_MODEL_FILE, null);
	}
	
	public static File getDataModelFile(WizardInstance wizardInstance) {
		return (File) wizardInstance.getAttribute(DATA_MODEL_FILE);
	}

	public static File getDataModelFile(IActionContext actionContext) throws Exception {
		WizardInstance wizardInstance = actionContext.getWizardInstance();
		File dataModelFile = getDataModelFile(wizardInstance);
		if (dataModelFile == null) {
			dataModelFile = SaveDataModelAction.getSaveDataModelAction(wizardInstance).saveDataModel(wizardInstance);
			wizardInstance.setAttribute(DATA_MODEL_FILE, dataModelFile);
		}
		return dataModelFile;
	}
	
	public static void resetActionLog(WizardInstance wizardInstance) {
		ActionLogger.setDetailsPanel(wizardInstance, null);
		ActionLogger.setActionLog(wizardInstance, new StringBuffer());
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static void performHelp(URL url) {
		if (url == null) {
			url = HelpContext.getInstance().getHelpByContextId(HelpContext.getDefaultHelpContextId());
		}

		try {
			WizardHelper.openURL(TCTContext.getInstance().getWizardInstance(), url);
		} catch (Exception e) {
			WizardHelper.openErrorDialog(TCTContext.getInstance().getWizardInstance(), e);
		}
	}

	public static boolean isIPv6(String host) {
		boolean matched = false;
		if (!isEmpty(host)) {
			matched = host.matches(Constants.REGEX_IPv6);
		}
		return matched;
	}

	public static String convertHostForIPv6Url(String host) {
		return "[" + host + "]";
	}
}
