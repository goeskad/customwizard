package com.tibco.customwizard.internal.xforms.factories;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.xforms.xforms.model.XForm;
import org.nuxeo.xforms.xml.DefaultXFactory;
import org.nuxeo.xforms.xml.XParserGlobalContext;
import org.xml.sax.Attributes;

import com.tibco.customwizard.util.XFormUtils;

public abstract class ReferableXFactory extends DefaultXFactory {
	public Object create(XParserGlobalContext context, String uri, String localName, String qname, Attributes attrs) {
		Object object = getReferredObject(context, attrs);
		if (object == null) {
			object = createInternal(context, uri, localName, qname, attrs);
		}
		setReferredObject(context, attrs, object);
		return object;
	}

	private Object getReferredObject(XParserGlobalContext context, Attributes attrs) {
		String refId = attrs.getValue("refid");
		if (refId != null) {
			return getObjectMap(context).get(refId);
		}
		return null;
	}

	private void setReferredObject(XParserGlobalContext context, Attributes attrs, Object object) {
		String id = attrs.getValue("id");
		if (id != null) {
			getObjectMap(context).put(id, object);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getObjectMap(XParserGlobalContext context) {
		XForm form = (XForm) context.document;
		Map<String, Object> objectMap = (Map<String, Object>) XFormUtils.getPageInstance(form).getAttribute(
				"xform.objectmap");
		if (objectMap == null) {
			objectMap = new HashMap<String, Object>();
			XFormUtils.getPageInstance(form).setAttribute("xform.objectmap", objectMap);
		}
		return objectMap;
	}

	protected abstract Object createInternal(XParserGlobalContext context, String uri, String localName, String qname,
			Attributes attrs);
}
