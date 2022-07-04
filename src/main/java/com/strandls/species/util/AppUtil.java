package com.strandls.species.util;


public class AppUtil {

	public static enum BASE_FOLDERS {
		OBSERVATION("observations"), MY_UPLOADS("myUploads"), USERGROUPS("userGroups"), SPECIES("img"),
		SPECIES_FIELD("img");

		private String folder;

		private BASE_FOLDERS(String folder) {
			this.folder = folder;
		}

		public String getFolder() {
			return folder;
		}
	};
	
	
	public static String getResourceContext(String context) {
		String hasFolder = null;
		if (context == null) {
			return null;
		}
		for (BASE_FOLDERS folders : BASE_FOLDERS.values()) {
			if (folders.name().equalsIgnoreCase(context)) {
				hasFolder = folders.getFolder();
				break;
			}
		}
		return hasFolder;
	}

}
