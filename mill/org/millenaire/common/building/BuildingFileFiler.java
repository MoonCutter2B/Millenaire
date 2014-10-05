package org.millenaire.common.building;

import java.io.File;
import java.io.FilenameFilter;

public class BuildingFileFiler implements FilenameFilter {

	String end;

	public BuildingFileFiler(final String ending) {
		end = ending;
	}

	@Override
	public boolean accept(final File file, final String name) {

		if (!name.endsWith(end)) {
			return false;
		}

		if (name.startsWith(".")) {
			return false;
		}

		return true;
	}
}