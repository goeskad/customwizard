package com.tibco.customwizard.internal.console;

import org.nuxeo.xforms.stylesheet.Style;
import org.nuxeo.xforms.stylesheet.StyleProperties;
import org.nuxeo.xforms.ui.AbstractUIBuilder;
import org.nuxeo.xforms.ui.AbstractUIControl;
import org.nuxeo.xforms.ui.UIControl;
import org.nuxeo.xforms.ui.UIForm;
import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xforms.model.controls.XFBody;
import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xforms.model.controls.XFGroup;
import org.nuxeo.xforms.xforms.model.controls.XFInput;
import org.nuxeo.xforms.xforms.model.controls.XFItem;
import org.nuxeo.xforms.xforms.model.controls.XFLabel;
import org.nuxeo.xforms.xforms.model.controls.XFLabeledControl;
import org.nuxeo.xforms.xforms.model.controls.XFOutput;
import org.nuxeo.xforms.xforms.model.controls.XFSecret;
import org.nuxeo.xforms.xforms.model.controls.XFSelect;
import org.nuxeo.xforms.xforms.model.controls.XFSelect1;
import org.nuxeo.xforms.xforms.model.controls.XFSubmit;
import org.nuxeo.xforms.xforms.model.controls.XFTextArea;
import org.nuxeo.xforms.xforms.model.controls.XFTrigger;

import com.tibco.customwizard.internal.console.proxies.ButtonProxy;
import com.tibco.customwizard.internal.console.proxies.ComboProxy;
import com.tibco.customwizard.internal.console.proxies.ConsoleControlProxy;
import com.tibco.customwizard.internal.console.proxies.GroupProxy;
import com.tibco.customwizard.internal.console.proxies.LabelProxy;
import com.tibco.customwizard.internal.console.proxies.PasswordProxy;
import com.tibco.customwizard.internal.console.proxies.TextProxy;

@SuppressWarnings("unchecked")
public class ConsoleUIBuilder extends AbstractUIBuilder {
	protected BuildContext mContext;

	protected ConsoleRegistry registry;
	
	public ConsoleUIBuilder(ConsoleRegistry registry) {
		mContext = new BuildContext(this);
		this.registry = registry;
	}

	public ConsoleRegistry getRegistry() {
		return registry;
	}

	/**
	 * 
	 * @param form
	 * @param composite
	 * @return
	 */
	public UIForm build(XForm form, Object parent) {
		try {
			return super.build(form, parent);
		} finally {
			mContext.reset();
			mContext.form = null;
		}
	}

	// ------------- tree processing ------------

	public UIForm processForm(XForm element, Object parent) {
		mContext.form = new ConsoleForm(element);
		return mContext.form;
	}

	public UIControl processGroup(XFGroup element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = create(element, GroupProxy.class, parent);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}
		return ctrl;
	}

	public UIControl processBody(XFBody element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = create(element, GroupProxy.class, parent);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}
		return ctrl;
	}

	public UIControl processInput(XFInput element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = create(element, TextProxy.class, parent);
			setLabel(element);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}

		return ctrl;
	}

	public UIControl processSecret(XFSecret element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = create(element, PasswordProxy.class, parent);
			setLabel(element);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}
		return ctrl;
	}

	public UIControl processTextArea(XFTextArea element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = create(element, TextProxy.class, parent);
			setLabel(element);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}
		return ctrl;
	}

	public UIControl processSelect(XFSelect element, UIControl parent) {
		throw new RuntimeException("Unsupport");
	}

	public UIControl processFullSelect(XFSelect element, UIControl parent) {
		throw new RuntimeException("Unsupport");
	}


	public UIControl processMinimalSelect(XFSelect element, UIControl parent) {
		throw new RuntimeException("Unsupport");
	}

	public UIControl processSelect1(XFSelect1 element, UIControl parent) {
		return processMinimalSelect1(element, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nuxeo.xf4e.ui.swt.SWTBuilderVisitor#processMinimalSelect1(org.nuxeo
	 * .xf4e.xforms.model.XFSelect1, org.eclipse.swt.widgets.Composite)
	 */
	public UIControl processMinimalSelect1(XFSelect1 element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = createCombo(element, ComboProxy.class, parent);
			setLabel(element);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}

		return ctrl;
	}

	public UIControl processFullSelect1(XFSelect1 element, UIControl parent) {
		throw new RuntimeException("Unsupport");
	}

	public UIControl processTrigger(XFTrigger element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = create(element, ButtonProxy.class, parent);
			setLabel(element);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}

		return ctrl;
	}

	public UIControl processSubmit(XFSubmit element, UIControl parent) {
		throw new RuntimeException("Unsupport");
	}

	public UIControl processLabel(XFLabel element, UIControl parent) {
		return null;
	}

	public UIControl processOutput(XFOutput element, UIControl parent) {
		UIControl ctrl = null;
		try {
			ctrl = create(element, LabelProxy.class, parent);
			String value = element.getText();
			if (value != null)
				ctrl.setLabel(value);
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		} finally {
			mContext.reset();
		}

		return ctrl;
	}

	protected void setLabel(XFLabeledControl element) {
		XFLabel label = element.getLabel();
		if (label != null) {
			mContext.proxy.setLabel(label.getText());
		}
	}

	protected UIControl createCombo(XFSelect element, Class defaultProxy, UIControl parent) throws Exception {
		ComboProxy combo = (ComboProxy) create(element, defaultProxy, parent);
		java.util.List<XFItem> items = element.getItems();
		combo.setItems(items);
		return combo;
	}

	protected ConsoleControlProxy newProxy(Class proxyClass) {
		try {
			return (ConsoleControlProxy) proxyClass.newInstance();
		} catch (Throwable e) {
			e.printStackTrace();
			// TODO
		}
		return null;
	}

	protected AbstractUIControl create(XFControlElement element, Class defaultProxyClass, UIControl parent) throws Exception {
		mContext.element = element;
		mContext.parent = (ConsoleControlProxy) parent;

		Style style = element.getStyle();
		if (style != null) {
			StyleProperties props = element.getStyle().getProperties();
			if (props != null) {
				for (int i = 0, size = props.count(); i < size; i++) {
					String key = props.getKey(i);
					StyleProperty sp = registry.getStyleProperty(key);
					if (sp != null) {
						String value = props.getValue(i);
						if (!sp.process(mContext, value)) {
							// postpone processing of this property
							mContext.ppqueue.add(sp);
							mContext.ppqueue.add(value);
						}
					}
				}
			}
		}

		if (mContext.proxyClass == null) {
			mContext.proxyClass = defaultProxyClass;
		}
		mContext.proxy = newProxy(mContext.proxyClass);
		if (mContext.proxy == null) {
			throw new Exception("Failed to create control of type " + element.type());
		}
		if (parent instanceof GroupProxy) {
			((GroupProxy) parent).addChildControl(mContext.proxy);
		}
		mContext.proxy.create(mContext.form, mContext);
		// this MUST be done now because the form may modify control style so it
		// is preventing
		// to overwrite the user style that is setup in next steps
		// XFElement instanceElement = element.getInstanceElement();
		mContext.form.bind(element, mContext.proxy);
		if (element.hasActions())
			mContext.proxy.setActionsMap(element.getActions());

		// setting tooltip if any
		String value = element.getHint();
		if (value != null) {
			mContext.proxy.setToolTipText(value);
		}

		// third processing step
		// process postponed actions queue if any
		for (int i = 0, size = mContext.ppqueue.size(); i < size; i += 2) {
			StyleProperty sp = (StyleProperty) mContext.ppqueue.get(i);
			sp.process(mContext, (String) mContext.ppqueue.get(i + 1));
		}
		
		return mContext.proxy;
	}
}
