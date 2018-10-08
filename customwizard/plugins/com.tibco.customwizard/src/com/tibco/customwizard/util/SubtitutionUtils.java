package com.tibco.customwizard.util;

import com.tibco.customwizard.config.IDataModel;

public class SubtitutionUtils {
    public static final int STYLE_NONE = 0;

    public static final int STYLE_ESCAPE_XML = 1;

    public static String subtitution(String content, IDataModel dataModel) {
        return subtitution(content, dataModel, STYLE_NONE);
    }

    public static String subtitution(String content, IDataModel dataModel, int style) {
        int index = 0;
        int len = content.length();
        StringBuffer strbuf = new StringBuffer(len);
        while (index < len) {
            int start = content.indexOf("${", index);
            if (start < 0) {
                strbuf.append(content.substring(index, content.length()));
                break;
            }
            int end = content.indexOf('}', start);
            if (end < 0) {
                throw new RuntimeException("Incompleted expression " + content.substring(start, content.length()));
            }
            strbuf.append(content.substring(index, start));
            String expression = content.substring(start + 2, end);
            String value = dataModel.getValue(expression);
            if (value != null) {
                if (style == STYLE_ESCAPE_XML) {
                    value = escapeXML(value);
                }
                strbuf.append(value);
            } else {
                strbuf.append("${" + expression + "}");
            }
            index = end + 1;
        }
        return strbuf.toString();
    }

    private static String escapeXML(String value) {
        value = value.replace("&", "&amp;");
        value = value.replace("<", "&lt;");
        value = value.replace(">", "&gt;");
        value = value.replace("\"", "&quot;");
        return value;
    }
}
