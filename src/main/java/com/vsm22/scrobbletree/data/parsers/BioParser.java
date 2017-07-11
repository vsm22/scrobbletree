package com.vsm22.scrobbletree.data.parsers;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsm22.scrobbletree.data.Bio;

public class BioParser {
	public static Bio parse(Element bioElement) {
		NodeList summaryNodeList = bioElement.getElementsByTagName("summary");
		NodeList contentNodeList = bioElement.getElementsByTagName("content");
		
		Element summaryElement = (summaryNodeList.getLength() > 0) ? (Element) summaryNodeList.item(0) : null;
		Element contentElement = (contentNodeList.getLength() > 0) ? (Element) contentNodeList.item(0) : null;
		
		String summary = (summaryElement != null) ? summaryElement.getTextContent() : null;
		String content = (contentElement != null) ? contentElement.getTextContent() : null;
		
		Map<String, Object> args = new HashMap<>();
		
		args.put("summary", summary);
		args.put("content", content);
		
		Bio newBio = new Bio(args);
		
		return newBio;
	}
}