package com.strandls.species.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.strandls.species.pojo.Reference;

/**
 * Parsing of the free-text cells of the species batch upload sheet. Stateless, no
 * lookups: the preview service resolves the parsed values.
 */
public final class SpeciesBatchParser {

	/** Separators for lists of emails */
	private static final Pattern LIST_SEPARATOR = Pattern.compile("[\\n;,]+");

	/** References keep commas (they appear in citations), so only new line and ; split */
	private static final Pattern REFERENCE_SEPARATOR = Pattern.compile("[\\n;]+");

	private static final Pattern URL = Pattern.compile("^https?://\\S+$", Pattern.CASE_INSENSITIVE);

	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private SpeciesBatchParser() {
	}

	public static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	public static String trim(String value) {
		return value == null ? "" : value.trim();
	}

	/** Splits on , ; and new lines, trimming and dropping blanks */
	public static List<String> splitList(String value) {
		return split(value, LIST_SEPARATOR);
	}

	private static List<String> split(String value, Pattern separator) {
		if (isBlank(value))
			return new ArrayList<>();
		return Arrays.stream(separator.split(value)).map(String::trim).filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
	}

	public static boolean isUrl(String value) {
		return value != null && URL.matcher(value.trim()).matches();
	}

	public static boolean isEmail(String value) {
		return value != null && EMAIL.matcher(value.trim()).matches();
	}

	/** Whole number or null */
	public static Long toLong(String value) {
		if (isBlank(value))
			return null;
		String v = value.trim();
		// Excel often turns ids into "123.0"
		if (v.endsWith(".0"))
			v = v.substring(0, v.length() - 2);
		try {
			return Long.parseLong(v);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * "Title | URL", "Title" or "URL", one per line or ; separated. A bare URL is used
	 * as its own title, since biodiv-species skips references without a title.
	 */
	public static List<Reference> parseReferences(String value) {
		List<Reference> references = new ArrayList<>();
		for (String item : split(value, REFERENCE_SEPARATOR)) {
			int bar = item.indexOf('|');
			String title;
			String url;
			if (bar >= 0) {
				title = item.substring(0, bar).trim();
				url = item.substring(bar + 1).trim();
				if (url.isEmpty())
					url = null;
				if (title.isEmpty())
					title = url;
			} else if (isUrl(item)) {
				title = item;
				url = item;
			} else {
				title = item;
				url = null;
			}
			if (isBlank(title))
				continue;
			Reference reference = new Reference();
			reference.setTitle(title);
			reference.setUrl(url);
			references.add(reference);
		}
		return references;
	}

	/** Lower case with everything but letters and digits removed: "CC-BY 4.0" == "cc by 4.0" */
	public static String normalize(String value) {
		return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", "");
	}
}
