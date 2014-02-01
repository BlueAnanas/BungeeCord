package net.md_5.bungee;

import java.util.ArrayList;
import java.util.Collection;

import net.md_5.bungee.api.config.PatchInfo;
import net.md_5.bungee.api.config.PatchworkInfo;

public class BungeePatchworkInfo extends BungeeServerInfo implements PatchworkInfo {

	private final Collection<PatchInfo> patchInfos = new ArrayList<>();
	
	public BungeePatchworkInfo(String name){
		super(name, null, "", false);
	}
    
    public Collection<PatchInfo> getPatchInfos(){
    	return patchInfos;
    }
    
    public void addPatch(PatchInfo patchInfo){
    	patchInfos.add(patchInfo);
    }


}
