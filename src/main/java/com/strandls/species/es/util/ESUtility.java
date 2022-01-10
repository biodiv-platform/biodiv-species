package com.strandls.species.es.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.esmodule.pojo.MapAndBoolQuery;
import com.strandls.esmodule.pojo.MapAndMatchPhraseQuery;
import com.strandls.esmodule.pojo.MapAndRangeQuery;
import com.strandls.esmodule.pojo.MapExistQuery;
import com.strandls.esmodule.pojo.MapOrBoolQuery;
import com.strandls.esmodule.pojo.MapOrMatchPhraseQuery;
import com.strandls.esmodule.pojo.MapOrRangeQuery;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;

public class ESUtility {

	private Logger logger = LoggerFactory.getLogger(ESUtility.class);

	private List<Object> cSTSOT(String str) {
		if (str == null ||  str.equals("")|| str.isEmpty())
			return new ArrayList<Object>();

		String[] y = str.split(",");
		Set<Object> strSet1 = Arrays.stream(y).collect(Collectors.toSet());
		List<Object> strList = new ArrayList<Object>();
		strList.addAll(strSet1);
		return strList;

	}

	private MapAndBoolQuery assignBoolAndQuery(String key, List<Object> values) {
		MapAndBoolQuery andBool = new MapAndBoolQuery();
		andBool.setKey(key);
		andBool.setValues(values);
		return andBool;

	}

//	private MapAndMatchPhraseQuery assignAndMatchPhrase(String key, String value) {
//		MapAndMatchPhraseQuery andMatchPhrase = new MapAndMatchPhraseQuery();
//		andMatchPhrase.setKey(key);
//		andMatchPhrase.setValue(value);
//		return andMatchPhrase;
//	}

	private MapOrMatchPhraseQuery assignOrMatchPhrase(String key, String value) {
		MapOrMatchPhraseQuery orMatchPhrase = new MapOrMatchPhraseQuery();
		orMatchPhrase.setKey(key);
		orMatchPhrase.setValue(value);
		return orMatchPhrase;
	}

	private MapAndRangeQuery assignAndRange(String key, Object start, Object end, String path) {
		MapAndRangeQuery andRange = new MapAndRangeQuery();
		andRange.setKey(key);
		andRange.setStart(start);
		andRange.setEnd(end);
		andRange.setPath(path);
		return andRange;
	}

	public MapSearchQuery getMapSearchQuery(String scientificName, String commonName, String sGroup,
			String userGroupList, String taxonId, String mediaFilter, String traits, String createdOnMaxDate,
			String createdOnMinDate, String revisedOnMinDate, String revisedOnMaxDate, String rank,
			MapSearchParams mapSearchParams) {
		MapSearchQuery mapSearchQuery = new MapSearchQuery();
		List<MapAndBoolQuery> boolAndLists = new ArrayList<MapAndBoolQuery>();
		List<MapOrBoolQuery> boolOrLists = new ArrayList<MapOrBoolQuery>();
		List<MapOrRangeQuery> rangeOrLists = new ArrayList<MapOrRangeQuery>();
		List<MapAndRangeQuery> rangeAndLists = new ArrayList<MapAndRangeQuery>();
		List<MapExistQuery> andMapExistQueries = new ArrayList<MapExistQuery>();
		List<MapAndMatchPhraseQuery> andMatchPhraseQueries = new ArrayList<MapAndMatchPhraseQuery>();
		List<MapOrMatchPhraseQuery> orMatchPhraseQueriesnew = new ArrayList<MapOrMatchPhraseQuery>();

		try {

//		userGroupList
			List<Object> ugList = cSTSOT(userGroupList);
			if (!ugList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.USERGROUPID.getValue(), ugList));
			}

//			rank filter
			List<Object> rankList = cSTSOT(rank);
			if (!rankList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.RANK_KEYWORD.getValue(), rankList));
			}

//			media type
			List<Object> media = cSTSOT(mediaFilter);
			if (!media.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.MEDIA_TYPE_KEYWORD.getValue(), media));
			}

//				traits type
			List<Object> traitList = cSTSOT(traits);
			if (!traitList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.FACT_KEYWORD.getValue(), traitList));
			}

//			taxon browser 
			List<Object> taxonList = cSTSOT(taxonId);
			if (!taxonList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.TAXONID.getValue(), taxonList));
			}
//		species group
			List<Object> groupId = cSTSOT(sGroup);
			if (!groupId.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.SGROUP.getValue(), groupId));
			}

//			scientific name
			List<Object> sciName = cSTSOT(scientificName);
			if (!sciName.isEmpty()) {
				for (Object o : sciName) {
					String result = o.toString().toLowerCase();
					orMatchPhraseQueriesnew.add(assignOrMatchPhrase(SpeciesIndex.SYNONYM.getValue(), result));
					orMatchPhraseQueriesnew.add(assignOrMatchPhrase(SpeciesIndex.SCIENTIFICNAME.getValue(), result));
				}
			}

//			common name
			List<Object> cName = cSTSOT(commonName);
			if (!cName.isEmpty()) {
				for (Object o : cName) {
					String result = o.toString().toLowerCase();
					orMatchPhraseQueriesnew.add(assignOrMatchPhrase(SpeciesIndex.COMMONNAME.getValue(), result));
				}
			}

//		Created on
			String createdOnMaxDateValue = null;
			String createdOnMinDateValue = null;
			Date date = new Date();
			SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");
			try {
				if (createdOnMinDate != null) {
					createdOnMinDateValue = createdOnMinDate;
				}
				if (createdOnMaxDate != null) {
					createdOnMaxDateValue = createdOnMaxDate;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if (createdOnMinDateValue != null && createdOnMaxDateValue != null) {

				rangeAndLists.add(assignAndRange(SpeciesIndex.CREATEDON.getValue(), createdOnMinDateValue,
						createdOnMaxDateValue, null));
			}
			if (createdOnMinDateValue != null && createdOnMaxDateValue == null) {
				rangeAndLists.add(assignAndRange(SpeciesIndex.CREATEDON.getValue(), createdOnMinDateValue,
						out.format(date), null));
			}
			if (createdOnMinDateValue == null && createdOnMaxDateValue != null) {
				rangeAndLists.add(assignAndRange(SpeciesIndex.CREATEDON.getValue(), out.format(date),
						createdOnMaxDateValue, null));
			}

//		revised on

			String revisedOnMaxDateValue = null;
			String revisedOnMinDateValue = null;

			try {
				if (revisedOnMinDate != null) {
					revisedOnMinDateValue = revisedOnMinDate;
				}
				if (revisedOnMaxDate != null) {
					revisedOnMaxDateValue = revisedOnMaxDate;
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			if (revisedOnMinDateValue != null && revisedOnMaxDateValue != null) {

				rangeAndLists.add(assignAndRange(SpeciesIndex.LASTREVISED.getValue(), revisedOnMaxDateValue,
						revisedOnMinDateValue, null));
			}
			if (revisedOnMinDateValue != null && revisedOnMaxDateValue == null) {
				rangeAndLists.add(assignAndRange(SpeciesIndex.LASTREVISED.getValue(), out.format(date),
						revisedOnMinDateValue, null));
			}
			if (revisedOnMinDateValue == null && revisedOnMaxDateValue != null) {
				rangeAndLists.add(assignAndRange(SpeciesIndex.LASTREVISED.getValue(), revisedOnMaxDateValue,
						out.format(date), null));
			}

			/**
			 * combine all the queries
			 * 
			 */
			mapSearchQuery.setAndBoolQueries(boolAndLists);
			mapSearchQuery.setOrBoolQueries(boolOrLists);
			mapSearchQuery.setAndRangeQueries(rangeAndLists);
			mapSearchQuery.setOrRangeQueries(rangeOrLists);
			mapSearchQuery.setAndExistQueries(andMapExistQueries);
			mapSearchQuery.setAndMatchPhraseQueries(andMatchPhraseQueries);
			mapSearchQuery.setOrMatchPhraseQueries(orMatchPhraseQueriesnew);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		mapSearchQuery.setSearchParams(mapSearchParams);
		return mapSearchQuery;
	}

}
