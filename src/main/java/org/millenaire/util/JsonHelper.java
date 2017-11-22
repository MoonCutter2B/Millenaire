package org.millenaire.util;

import org.millenaire.MillCulture;
import org.millenaire.MillCulture.VillageType;

/**
 * Wrapper for the JSON parser
 */
public class JsonHelper {

	public static class VillageTypes {
		
		public VillageTypes() {
			
		}
		
		public VillageTypes(VillageType[] types) {
			this.types = types;
		}
		
		public MillCulture.VillageType[] types;
		
		public void setTypes(VillageType[] types) {
			this.types = types;
		}
		
		public MillCulture.VillageType[] getTypes() {
			return types;
		}
	}
}