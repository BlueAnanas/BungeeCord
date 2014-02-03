package net.md_5.bungee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.config.PatchInfo;
import net.md_5.bungee.api.config.ServerInfo;

@RequiredArgsConstructor
public class BungeePatchInfo implements PatchInfo {

    @Getter
    private final ServerInfo server;
    @Getter
    private final int minX;
    @Getter
    private final int maxX;
    @Getter
    private final int minZ;
    @Getter
    private final int maxZ;
    @Getter
    private final int viewDistance;
    @Getter
    private final int connectDistance;
    
	@Override
	public String getName() {
		return server.getName();
	}
    
    

}
