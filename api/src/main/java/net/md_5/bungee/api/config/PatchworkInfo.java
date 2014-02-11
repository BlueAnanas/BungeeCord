package net.md_5.bungee.api.config;

import java.util.Collection;

/**
 * Class used to represent a server patchwork to connect to.
 */
public interface PatchworkInfo extends ServerInfo{

    /**
     * get the list of patches on this patchwork
     * 
     * @return
     */
    public Collection<PatchInfo> getPatchInfos();
    
    /**
     * get the current patch
     * 
     * @return
     */
    public PatchInfo getCurrentPatchInfo();
    
	/**
	 * set the current patch.
	 * 
	 * @param server
	 */
    public void setCurrentPatchInfo(PatchInfo patchInfo);
	
	/**
	 * add a patch to this patchwork.
	 * 
	 * @param server
	 */
	void addPatch(PatchInfo server);

}
