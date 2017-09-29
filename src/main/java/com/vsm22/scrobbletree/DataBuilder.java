package com.vsm22.scrobbletree;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.vsm22.scrobbletree.data.ArtistInfo;
import com.vsm22.scrobbletree.data.ArtistSearch;
import com.vsm22.scrobbletree.data.ArtistSearchItem;
import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_Album;
import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_ApiAccessorSpec;
import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_Artist;
import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_DocumentBuilder;
import com.vsm22.scrobbletree.data.remote.lastfm.LastFM_ItemFactory;
import com.vsm22.scrobbletree.data.remote.wikimedia.Wiki_ApiAccessor;
import com.vsm22.scrobbletree.data.remote.wikimedia.Wiki_DocumentBuilder;
import com.vsm22.scrobbletree.data.remote.wikimedia.Wiki_Extract;
import com.vsm22.scrobbletree.data.remote.wikimedia.Wiki_ExtractParser;

import com.vsm22.scrobbletree.util.RemoteResourceAccessor;

/**
 * Singleton class for building the middleware data classes used for API responses
 */
public class DataBuilder {
	private static volatile DataBuilder instance;

	private LastFM_ApiAccessorSpec lastFM_ApiAccessorSpec;

	private DataBuilder() {
		lastFM_ApiAccessorSpec = LastFM_ApiAccessorSpec.getInstance();
	}

	public static DataBuilder getInstance() {
		if (instance == null) {
			synchronized(DataBuilder.class) {
				if (instance == null) {
					instance = new DataBuilder();
				}
			}

		}
		return instance;
	}

	/**
	 * Create a data class representing the results of an Artist Search based on a query
	 * @param query - the search query (artist name)
	 * @return - data class representing the artist search results
	 * @throws IOException
	 */
	 ArtistSearch createArtistSearch(String query) throws IOException {
		RequestType requestType = RequestType.GET_ARTIST_SEARCH;
		 List<LastFM_Artist> lastFM_ArtistList;

		// get LastFM data
		String lastFM_requestUrl = lastFM_ApiAccessorSpec.getRequestUrl(requestType, query);

		try ( InputStream inputStream = new BufferedInputStream(RemoteResourceAccessor.getResponseStream(lastFM_requestUrl)) ) {
			Element rootEl = LastFM_DocumentBuilder.getArtistSearchRootElement(inputStream);
			lastFM_ArtistList = LastFM_ItemFactory.createArtistList(rootEl);
		}

		ArtistSearch artistSearch = new ArtistSearch();

		lastFM_ArtistList.forEach(artist -> {
			Map<String, Object> args = new HashMap<>();

			if (artist.getName() != null) args.put("name", artist.getName());
			if (artist.getImageSmallUrl() != null) args.put("imageSmallUrl", artist.getImageSmallUrl());
			if (artist.getImageMediumUrl() != null) args.put("imageMediumUrl", artist.getImageMediumUrl());
			if (artist.getImageExtraLargeUrl() != null) args.put("imageLargeUrl", artist.getImageExtraLargeUrl());
			else if (artist.getImageLargeUrl() != null) args.put("imageLargeUrl", artist.getImageLargeUrl());
			else if (artist.getImageMediumUrl() != null) args.put("imageLargeUrl", artist.getImageMediumUrl());
			else if (artist.getImageSmallUrl() != null) args.put("imageLargeUrl", artist.getImageSmallUrl());
			else args.put("imageLargeUrl", "images/fallback1.png");

			ArtistSearchItem newArtist = new ArtistSearchItem(args);

			artistSearch.add(newArtist);
		});

		return artistSearch;
	}

	/**
	 * Create a data class representing the results of an Artist Info query
	 * @param query - the search query (artist name)
	 * @return - data class representing the artist info
	 * @throws IOException
	 */
	ArtistInfo createArtistInfo(String query) throws IOException {
		LastFM_Artist lastFM_Artist;
		List<LastFM_Album> lastFM_artistAlbums;

		String lastFM_artistInfoUrl = lastFM_ApiAccessorSpec.getRequestUrl(RequestType.GET_ARTIST_INFO, query);
		String lastFM_artistAlbumsUrl = lastFM_ApiAccessorSpec.getRequestUrl(RequestType.GET_ARTIST_ALBUMS, query);

		// get LastFm artist search results
		try (InputStream lfmInputStream = new BufferedInputStream(RemoteResourceAccessor.getResponseStream(lastFM_artistInfoUrl));
			 InputStream lfmArtistAlbumsStream = new BufferedInputStream(RemoteResourceAccessor.getResponseStream(lastFM_artistAlbumsUrl)) ) {

			Element lfmRootEl = (Element) LastFM_DocumentBuilder.getArtistInfoRootElement(lfmInputStream);
			lastFM_Artist = LastFM_ItemFactory.createArtist(lfmRootEl);

			Element lfmArtistAlbumsRootEl = (Element) LastFM_DocumentBuilder.getArtistAlbumsRootElement(lfmArtistAlbumsStream);
			lastFM_artistAlbums = LastFM_ItemFactory.createAlbumList(lfmArtistAlbumsRootEl);
		}

		Map<String, Object> args = new HashMap<>();
		
		if (lastFM_Artist.getName() != null) args.put("name", lastFM_Artist.getName());
		if (lastFM_Artist.getImageSmallUrl() != null) args.put("imageSmallUrl", lastFM_Artist.getImageSmallUrl());
		if (lastFM_Artist.getImageMediumUrl() != null) args.put("imageMediumUrl", lastFM_Artist.getImageMediumUrl());
		if (lastFM_Artist.getImageExtraLargeUrl() != null) args.put("imageLargeUrl", lastFM_Artist.getImageExtraLargeUrl());
		else if (lastFM_Artist.getImageLargeUrl() != null) args.put("imageLargeUrl", lastFM_Artist.getImageLargeUrl());
		else if (lastFM_Artist.getImageMediumUrl() != null) args.put("imageLargeUrl", lastFM_Artist.getImageMediumUrl());
		else if (lastFM_Artist.getImageSmallUrl() != null) args.put("imageLargeUrl", lastFM_Artist.getImageSmallUrl());
		else args.put("imageLargeUrl", "images/fallback1.png");
		if (lastFM_Artist.getBio() != null) args.put("bio", lastFM_Artist.getBio().getContent());
		if (lastFM_Artist.getSimilarArtists() != null) args.put("similarArtists", lastFM_Artist.getSimilarArtists());
		if (lastFM_artistAlbums != null) args.put("albumList", lastFM_artistAlbums);

		return new ArtistInfo(args);
	}

	/**
	 * Create a data class representing a list of similar artists
	 * @param query - the search query (artist name)
	 * @return - data class representing a list of similar artists
	 * @throws IOException
	 */
	ArtistSearch createSimilarArtists(String query) throws IOException {
		List<LastFM_Artist> lfmSimilar_Artists;

		String lastFM_requestUrl = lastFM_ApiAccessorSpec.getRequestUrl(RequestType.GET_SIMILAR_ARTISTS, query);

		// get LastFM similar artists result
		try ( InputStream lfmInputStream = new BufferedInputStream(RemoteResourceAccessor.getResponseStream(lastFM_requestUrl)) ) {
			Element lfmRootEl = (Element) LastFM_DocumentBuilder.getSimilarArtistsRootElement(lfmInputStream);
			lfmSimilar_Artists = LastFM_ItemFactory.createArtistList(lfmRootEl);
		}

		ArtistSearch similarArtists = new ArtistSearch();
		
		lfmSimilar_Artists.stream().forEach(artist -> {			
			Map<String, Object> args = new HashMap<>();
			
			if (artist.getName() != null) args.put("name", artist.getName());
			if (artist.getImageSmallUrl() != null) args.put("imageSmallUrl", artist.getImageSmallUrl());
			if (artist.getImageMediumUrl() != null) args.put("imageMediumUrl", artist.getImageMediumUrl());
			if (artist.getImageExtraLargeUrl() != null) args.put("imageLargeUrl", artist.getImageExtraLargeUrl());
			else if (artist.getImageLargeUrl() != null) args.put("imageLargeUrl", artist.getImageLargeUrl());
			else if (artist.getImageMediumUrl() != null) args.put("imageLargeUrl", artist.getImageMediumUrl());
			else if (artist.getImageSmallUrl() != null) args.put("imageLargeUrl", artist.getImageSmallUrl());
			else args.put("imageLargeUrl", new String("images/fallback1.png"));
			
			ArtistSearchItem newArtist = new ArtistSearchItem(args);

			similarArtists.add(newArtist);
		});
		
		return similarArtists;
	}
}
