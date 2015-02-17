package com.gmail.marzipankaiser.argumentreader;

import org.bukkit.Material;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;

public class BukkitArgumentType {

	
	////----------------------------------------------------------------------
	/// Material
	public static class TMaterial implements ArgumentType{
		@Override
		public Material readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			Material res;
			if(ar.tryExpect('#')){
				int id = INTEGER.readAndValidateFrom(ar);
				res = Material.getMaterial(id); // Deprected, but maybe ok?
												 // (no other way...)
			}else{
				String name = IDENTIFIER.readAndValidateFrom(ar);
				res = Material.matchMaterial(name);
				if(res==null){ // Allow CamelCase
					StringBuilder ccname = new StringBuilder();
					for(char c:name.toCharArray()){
						if(Character.isUpperCase(c))
							ccname.append('_');
						ccname.append(Character.toUpperCase(c));
					}
					res = Material.getMaterial(ccname.toString());
				}
				if(res==null){ // Allow removing _ and ignore case
					for(Material m:Material.values()){
						if(m.name().replace("_", "").equalsIgnoreCase(name)){
							res=m;
						}
					}
				}
			}	
			return res;
		}
		@Override
		public String name() {
			return "Material";
		}
	};
	public static final TMaterial MATERIAL = new TMaterial();
	////----------------------------------------------------------------------
}
