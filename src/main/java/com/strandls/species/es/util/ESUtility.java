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
		if (str == null || str.equals("") || str.isEmpty())
			return new ArrayList<Object>();

		String[] y = str.split(",");
		Set<Object> strSet1 = Arrays.stream(y).collect(Collectors.toSet());
		List<Object> strList = new ArrayList<Object>();
		strList.addAll(strSet1);
		return strList;

	}

	private MapAndBoolQuery assignBoolAndQuery(String key, List<Object> values, String path) {
		MapAndBoolQuery andBool = new MapAndBoolQuery();
		andBool.setKey(key);
		andBool.setValues(values);
		andBool.setPath(path);
		return andBool;

	}

	private MapAndMatchPhraseQuery assignAndMatchPhrase(String key, String value, String path) {
		MapAndMatchPhraseQuery andMatchPhrase = new MapAndMatchPhraseQuery();
		andMatchPhrase.setKey(key);
		andMatchPhrase.setValue(value);
		andMatchPhrase.setPath(path);
		return andMatchPhrase;
	}

	private MapOrMatchPhraseQuery assignOrMatchPhrase(String key, String value, String path) {
		MapOrMatchPhraseQuery orMatchPhrase = new MapOrMatchPhraseQuery();
		orMatchPhrase.setKey(key);
		orMatchPhrase.setValue(value);
		orMatchPhrase.setPath(path);
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
			String createdOnMinDate, String revisedOnMinDate, String revisedOnMaxDate, String rank, String path,
			String userId, String attributes, String reference, String description, String hue, String saturation,
			String value, String nameId, Integer colorRange, String traitMin, String traitMax, String rangeNameId,
			String traitsFromDate,String traitsToDate,String traitsNameId,
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
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.USERGROUPID.getValue(), ugList, null));
			}

//			rank filter
			List<Object> rankList = cSTSOT(rank);
			if (!rankList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.RANK_KEYWORD.getValue(), rankList, null));
			}

//			media type
			List<Object> media = cSTSOT(mediaFilter);
			if (!media.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.MEDIA_TYPE_KEYWORD.getValue(), media, null));
			}

//				traits type
			List<Object> traitList = cSTSOT(traits);
			if (!traitList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.FACT_KEYWORD.getValue(), traitList, null));
			}

//			taxon browser 
			List<Object> taxonList = cSTSOT(taxonId);
			if (!taxonList.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.TAXONID.getValue(), taxonList, null));
			}
//		species group
			List<Object> groupId = cSTSOT(sGroup);
			if (!groupId.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.SGROUP.getValue(), groupId, null));
			}

//			scientific name
			if (scientificName != null && !scientificName.equals("") && !scientificName.isEmpty()) {
				orMatchPhraseQueriesnew.add(assignOrMatchPhrase(SpeciesIndex.SYNONYM.getValue(), scientificName, null));
				orMatchPhraseQueriesnew
						.add(assignOrMatchPhrase(SpeciesIndex.SCIENTIFICNAME.getValue(), scientificName, null));

			}

//			fieldDescription filter
			if (path != null && description != null && !path.isEmpty() && !description.isEmpty()) {
				String[] fieldDescription = description.split(",");
				String[] pathList = path.split(",");

				for (int i = 0; i < fieldDescription.length; i++) {

					String nestedPath = SpeciesIndex.FIELD_DATA.getValue().concat("." + pathList[i].toLowerCase());/// fieldData.108

					andMatchPhraseQueries.add(assignAndMatchPhrase(SpeciesIndex.FIELD_PATH.getValue(),
							pathList[i].toLowerCase(), nestedPath));/// nestedPath=fieldData.108

					andMatchPhraseQueries.add(assignAndMatchPhrase(SpeciesIndex.FIELD_DESCRIPTION.getValue(),
							fieldDescription[i].toLowerCase(), nestedPath));/// nestedPath=fieldData.108
				}
			}

//			references filter
			List<Object> references = cSTSOT(reference);
			if (!references.isEmpty()) {
				for (Object o : references) {
					String result = o.toString().toLowerCase();
					andMatchPhraseQueries
							.add(assignAndMatchPhrase(SpeciesIndex.FIELD_REFERENCES.getValue(), result, null));
				}
			}

//			attribution filter
			List<Object> attribution = cSTSOT(attributes);
			if (!attribution.isEmpty()) {
				for (Object o : attribution) {
					String result = o.toString().toLowerCase();
					andMatchPhraseQueries
							.add(assignAndMatchPhrase(SpeciesIndex.FIELD_ATTRIBUTION.getValue(), result, null));
				}
			}
//			contributor browser 
			List<Object> contributor = cSTSOT(userId);
			if (!contributor.isEmpty()) {
				boolAndLists.add(assignBoolAndQuery(SpeciesIndex.FIELD_CONTRIBUTOR.getValue(), contributor, null));
			}

//			common name
			List<Object> cName = cSTSOT(commonName);
			if (!cName.isEmpty()) {
				for (Object o : cName) {
					String result = o.toString().toLowerCase();
					orMatchPhraseQueriesnew.add(assignOrMatchPhrase(SpeciesIndex.COMMONNAME.getValue(), result, null));
				}
			}

//			traits range filter
			List<Object> rangeNameList = cSTSOT(rangeNameId);
			List<Object> maxList = cSTSOT(traitMax);
			List<Object> minList = cSTSOT(traitMin);
			if (!rangeNameList.isEmpty() && !maxList.isEmpty() && !minList.isEmpty()) {
				for (int i = 0; i < rangeNameList.size(); i++) {

					Object vmax = maxList.get(i);
					Object vmin = minList.get(i);

					rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_RANGE_NAMEID.getValue(), rangeNameList.get(i),
							null, SpeciesIndex.TRAITS_RANGE_GROUP.getValue()));
					rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_RANGE_MAX.getValue(), vmin, vmax,
							SpeciesIndex.TRAITS_RANGE_GROUP.getValue()));
					rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_RANGE_MIN.getValue(), vmin, vmax,
							SpeciesIndex.TRAITS_RANGE_GROUP.getValue()));

				}

			}

//			traits color filter
			List<Object> hueList = cSTSOT(hue);
			List<Object> satList = cSTSOT(saturation);
			List<Object> valList = cSTSOT(value);
			List<Object> colorNameIdlist = cSTSOT(nameId);
			if (!hueList.isEmpty() && !satList.isEmpty() && !valList.isEmpty() && !colorNameIdlist.isEmpty()) {

				for (int i = 0; i < colorNameIdlist.size(); i++) {

					Double hmin = !hueList.get(i).toString().equals("0")
							? Double.parseDouble(hueList.get(i).toString()) - colorRange
							: Double.parseDouble(hueList.get(i).toString());
					Double hmax = Double.parseDouble(hueList.get(i).toString()) + colorRange;

					Double vmin = !valList.get(i).toString().equals("0")
							? Double.parseDouble(valList.get(i).toString()) - colorRange
							: Double.parseDouble(valList.get(i).toString());

					Double vmax = Double.parseDouble(valList.get(i).toString()) + colorRange;

					Double smin = !satList.get(i).toString().equals("0")
							? Double.parseDouble(satList.get(i).toString()) - colorRange
							: Double.parseDouble(valList.get(i).toString());
					Double smax = Double.parseDouble(satList.get(i).toString()) + colorRange;

					rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_COLOR_NAMEID.getValue(),
							colorNameIdlist.get(i), null, SpeciesIndex.TRAITS_COLOR_GROUP.getValue()));
					rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_COLOR_VALUE.getValue(), vmin, vmax,
							SpeciesIndex.TRAITS_COLOR_GROUP.getValue()));
					rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_COLOR_SATURATION.getValue(), smin, smax,
							SpeciesIndex.TRAITS_COLOR_GROUP.getValue()));
					rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_COLOR_HUE.getValue(), hmin, hmax,
							SpeciesIndex.TRAITS_COLOR_GROUP.getValue()));

				}
			}
//		Traits date filter

			List<Object> fromDate = cSTSOT(traitsFromDate);
			List<Object> toDate = cSTSOT(traitsToDate);
			List<Object> traitsDateNameIdList = cSTSOT(traitsNameId);
			if ((!fromDate.isEmpty() || !toDate.isEmpty()) && !traitsDateNameIdList.isEmpty()) {

				for (int i = 0; i < traitsDateNameIdList.size(); i++) {
					String traitsMaxDateValue = null;
					String traitsMinDateValue = null;
					Date date = new Date();
					SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");
					try {
						if (fromDate.get(i) != null) {
							traitsMinDateValue = fromDate.get(i).toString();
						}
						if (toDate.get(i) != null) {
							traitsMaxDateValue = toDate.get(i).toString();
						}
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					
					if (traitsMinDateValue != null && traitsMaxDateValue != null) {

						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_NAME_ID.getValue(), "", null,
								SpeciesIndex.FACTS_GROUP.getValue()));

						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_FROM_DATE.getValue(), traitsMinDateValue,
								traitsMaxDateValue, SpeciesIndex.FACTS_GROUP.getValue()));
						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_TO_DATE.getValue(), traitsMinDateValue,
								traitsMaxDateValue, SpeciesIndex.FACTS_GROUP.getValue()));
					}
					
					if (traitsMinDateValue != null && traitsMaxDateValue == null) {

						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_NAME_ID.getValue(), "", null,
								SpeciesIndex.FACTS_GROUP.getValue()));

						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_FROM_DATE.getValue(), traitsMinDateValue,
								out.format(date), SpeciesIndex.FACTS_GROUP.getValue()));
						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_TO_DATE.getValue(), traitsMinDateValue,
								out.format(date), SpeciesIndex.FACTS_GROUP.getValue()));
					}
					
					if (traitsMinDateValue == null && traitsMaxDateValue != null) {
						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_NAME_ID.getValue(), "", null,
								SpeciesIndex.FACTS_GROUP.getValue()));

						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_FROM_DATE.getValue(), out.format(date),
								traitsMaxDateValue, SpeciesIndex.FACTS_GROUP.getValue()));
						rangeAndLists.add(assignAndRange(SpeciesIndex.TRAITS_TO_DATE.getValue(), out.format(date),
								traitsMaxDateValue, SpeciesIndex.FACTS_GROUP.getValue()));
					}

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
