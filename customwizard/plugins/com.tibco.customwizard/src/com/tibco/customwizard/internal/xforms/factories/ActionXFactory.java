package com.tibco.customwizard.internal.xforms.factories;

import org.nuxeo.xforms.xforms.events.DOMActivateEvent;
import org.nuxeo.xforms.xforms.model.actions.XFAction;
import org.nuxeo.xforms.xforms.model.actions.XFActionGroup;
import org.nuxeo.xforms.xforms.model.controls.XFControlElement;
import org.nuxeo.xforms.xforms.model.controls.XFTrigger;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.action.CustomActionGroup;
import com.tibco.customwizard.action.ICustomAction;
import com.tibco.customwizard.internal.xforms.actions.CustomActionAdapter;
import com.tibco.customwizard.internal.xforms.actions.XFCustomAction;
import com.tibco.customwizard.util.PropertyUtils;
import com.tibco.customwizard.util.XFormUtils;

public class ActionXFactory extends ReferableXFactory {
	public final static ActionXFactory INSTANCE = new ActionXFactory();

	public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
		Object action = super.create(context, uri, localName, qname, attrs);

		Object parent = context.peekObject();
		if (parent instanceof XFActionGroup) {
			((XFActionGroup) parent).addAction(convertAction(action));
		} else if (parent instanceof CustomActionGroup) {
			ICustomAction customAction;
			if (action instanceof ICustomAction) {
				customAction = (ICustomAction) action;
			} else {
				customAction = new CustomActionAdapter((XFAction) action);
			}
			((CustomActionGroup) parent).addAction(customAction);
		} else if (parent instanceof XFControlElement) {
			String event = attrs.getValue("event");
			if (event == null && parent instanceof XFTrigger) {
				event = DOMActivateEvent.ID;
			}

			if (event != null) {
				((XFControlElement) parent).addAction(event, convertAction(action));
			}
		}

		return action;
	}

	private XFAction convertAction(Object action) {
		if (action instanceof XFAction) {
			return (XFAction) action;
		} else if (action instanceof ICustomAction) {
			XFCustomAction xfaction = new XFCustomAction();
			xfaction.setAction((ICustomAction) action);
			return xfaction;
		}
		throw new RuntimeException("Unknow action: " + action.getClass());
	}
	
	protected Object createInternal(XParserGlobalContext context, String uri, String localName, String qname,
			Attributes attrs) {
		String className = attrs.getValue("className");
		checkClassName(context, localName, className);

		Object parent = context.peekObject();
		if (parent instanceof CustomActionGroup) {
			try {
				Class<?> cls = XFormUtils.getContextClassLoader(context).loadClass(className);
				ICustomAction action = (ICustomAction) cls.newInstance();
				PropertyUtils.setAttributes(action, attrs);
				return action;
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			XFCustomAction action = new XFCustomAction();
			action.setClassName(className);
			action.setAttrs(attrs);
			return action;
		}
	}

	private void checkClassName(XParserGlobalContext context, String localName, String className) {
		if (className == null) {
			throw new RuntimeException("The element " + localName + " missed attribute \"className\" in "
					+ context.input.getURL());
		}
	}
}
