package com.vsm22.scrobbletree.data.remote.lastfm.parsers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_Album;
import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_Tag;
import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_Track;

public class LastFM_AlbumParser {
	public static LastFM_Album parse(Element albumElement) {
		NodeList nameNodeList = albumElement.getElementsByTagName("name");
		NodeList artistNameNodeList = albumElement.getElementsByTagName("artist");
		NodeList urlNodeList = albumElement.getElementsByTagName("url");
		NodeList releaseDateNodeList = albumElement.getElementsByTagName("releasedate");
		NodeList imageNodeList = albumElement.getElementsByTagName("image");
		NodeList tagNodeList = albumElement.getElementsByTagName("toptags");
		NodeList trackNodeList = albumElement.getElementsByTagName("tracks");
		
		Element nameElement = (nameNodeList.getLength() > 0) ? (Element) nameNodeList.item(0) : null;
		Element artistNameElement = (artistNameNodeList.getLength() > 0) ? (Element) artistNameNodeList.item(0) : null;
		Element urlElement = (urlNodeList.getLength() > 0) ? (Element) urlNodeList.item(0) : null;
		Element releaseDateElement = (releaseDateNodeList.getLength() > 0) ? (Element) releaseDateNodeList.item(0) : null;
		Element imageSmallUrlElement = (imageNodeList.getLength() > 0) ? (Element) imageNodeList.item(0) : null;
		Element imageMediumUrlElement = (imageNodeList.getLength() > 1) ? (Element) imageNodeList.item(1) : null;
		Element imageLargeUrlElement = (imageNodeList.getLength() > 2) ? (Element) imageNodeList.item(2) : null;
		Element imageExtraLargeUrlElement = (imageNodeList.getLength() > 3) ? (Element) imageNodeList.item(3) : null;
		Element imageMegaUrlElement = (imageNodeList.getLength() > 4) ? (Element) imageNodeList.item(4) : null;
		Element tagListElement = (tagNodeList.getLength() > 0) ? (Element) tagNodeList.item(0) : null;
		Element trackListElement = (trackNodeList.getLength() > 0) ? (Element) trackNodeList.item(0) : null;
 		
		String name = (nameElement != null) ? nameElement.getTextContent() : null;
		String artistName = (artistNameElement != null) ? artistNameElement.getTextContent() : null;
		String url = (urlElement != null) ? urlElement.getTextContent() : null;
		LocalDate releaseDate = (releaseDateElement != null) ? LastFM_DateParser.parse(releaseDateElement.getTextContent()) : null;
		String imageSmallUrl = (imageSmallUrlElement != null) ? imageSmallUrlElement.getTextContent() : null;
		String imageMediumUrl = (imageSmallUrlElement != null) ? imageMediumUrlElement.getTextContent() : null;
		String imageLargeUrl = (imageSmallUrlElement != null) ? imageLargeUrlElement.getTextContent() : null;
		String imageExtraLargeUrl = (imageExtraLargeUrlElement != null) ? imageExtraLargeUrlElement.getTextContent() : null;
		String imageMegaUrl = (imageMegaUrlElement != null) ? imageMegaUrlElement.getTextContent() : null;
		List<LastFM_Tag> tags = (tagListElement != null) ? LastFM_TagListParser.parse(tagListElement) : null;
		List<LastFM_Track> tracks = (trackListElement != null) ? LastFM_TrackListParser.parse(trackListElement) : null;
		
		Map<String, Object> args = new HashMap<>();
		
		if (name != null) args.put("name", name);
		if (artistName != null) args.put("artistName", artistName);
		if (url != null) args.put("url", url);
		if (releaseDate != null) args.put("releaseDate", releaseDate);
		if (imageSmallUrl != null) args.put("imageSmallUrl", imageSmallUrl);
		if (imageMediumUrl != null) args.put("imageMediumUrl", imageMediumUrl);
		if (imageLargeUrl != null) args.put("imageLargeUrl", imageLargeUrl);
		if (tags != null) args.put ("tags", tags);
		if (tracks != null) args.put("tracks", tracks);
		
		LastFM_Album newAlbum = new LastFM_Album(args);
		
		return newAlbum;
	} 
}