package net.md_5.bungee.api.config;

import lombok.Getter;

/**
 * Class used to represent a server in a patchwork to connect to.
 */
public interface PatchInfo extends ServerInfo{
	
	/**
	 * get the real (unique) ServerInfo of this patch
	 * 
	 * @return the Serverinfo
	 */
	ServerInfo getServer();
	
    /**
     * get the lower border for the x coordinate serviced by this patch
     * 
     * @return
     */
    int getMinX();
    
    /**
     * get the upper border for the x coordinate serviced by this patch
     * 
     * @return
     */
    int getMaxX();
    
    /**
     * get the lower border for the z coordinate serviced by this patch
     * 
     * @return
     */
    int getMinZ();
    
    /**
     * get the upper border for the z coordinate serviced by this patch
     * 
     * @return
     */
    int getMaxZ();
    
    /**
     * get distance in chunks to be rendered from neighbor patch if standing at border
     * 
     * @return
     */
    int getViewDistance();
    
    /**
     * get distance in chunks to the border where player will be logged onto neighbor patch
     * 
     * @return
     */
    int getConnectDistance();

	
}
