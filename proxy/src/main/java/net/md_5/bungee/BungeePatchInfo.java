package net.md_5.bungee;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Preconditions;

import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.PatchInfo;
import net.md_5.bungee.api.config.PatchworkInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.BungeeServerInfo;

public class BungeePatchInfo extends BungeeServerInfo implements PatchInfo {

	BungeePatchInfo(ServerInfo server, int minX, int maxX, int minZ, int maxZ, int viewDistance, int connectDistance){
		super("", null, "", false); // these parameters won't get used anyway, all overridden with getters here
		this.server = server;
		this.minX = minX;
		this.maxX = maxX;
		this.minZ = minZ;
		this.maxZ = maxZ;
		this.viewDistance = viewDistance;
		this.connectDistance = connectDistance;
	}
	
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
    @Getter
    private NearbyPatches nearbyPatches;
    
    public class NearbyPatches {
    	public final NearbyPatchesInDirection north = new NearbyPatchesInDirection();
    	public final NearbyPatchesInDirection west  = new NearbyPatchesInDirection();
    	public final NearbyPatchesInDirection south = new NearbyPatchesInDirection();
    	public final NearbyPatchesInDirection east  = new NearbyPatchesInDirection();
    	public class NearbyPatchesInDirection {
    		public TreeMap<Integer, PatchInfo> lowerBorder = new TreeMap<Integer, PatchInfo>();
    		public TreeMap<Integer, PatchInfo> upperBorder = new TreeMap<Integer, PatchInfo>();
    	}
    }
    
	/* a block of stubs to get hold of the real server info */
    @Override
	public String getName() {
		return server.getName();
	}
	@Override
	public InetSocketAddress getAddress() {
		return server.getAddress();
	}
	@Override
	public String getMotd() {
		return server.getMotd();
	}
	@Override
	public boolean isRestricted() {
		return ((BungeeServerInfo) server).isRestricted();
	}
	@Override
    public void addPlayer(ProxiedPlayer player)
    {
    	((BungeeServerInfo) server).addPlayer( player );
    }
	@Override
    public void removePlayer(ProxiedPlayer player)
    {
    	((BungeeServerInfo) server).removePlayer( player );
    }
    @Override
    public Collection<ProxiedPlayer> getPlayers()
    {
        return server.getPlayers();
    }
    @Override
    public void sendData(String channel, byte[] data)
    {
        server.sendData(channel, data);
    }
    @Override
    public void ping(final Callback<ServerPing> callback)
    {
        server.ping(callback);
    }
    
	/**
	 * check if the chunk with chunkX, chunkZ is part of the patch
	 * @param chunkX
	 * @param chunkZ
	 * @return
	 */
	public boolean chunkInPatch(int chunkX, int chunkZ)
	{
		return chunkX >= minX/16 && chunkX < maxX/16 && chunkZ >= minZ/16 && chunkZ < maxZ/16;
	}
	
	/**
	 * check if player position fully safe - no need to connect or process other patches
	 * @param x
	 * @param z
	 * @return
	 */
	public boolean isPlayerSafe(double x, double z)
	{
		return x > (double)(minX + 16*connectDistance) && x < (double)(maxX - 16*connectDistance) && z > (double)(minZ + 16*connectDistance) && z < (double)(maxZ - 16*connectDistance);
	}

	public void setupNearbyPatches(PatchworkInfo pw){
		
		for (PatchInfo p: pw.getPatchInfos()){
			if (p.getMaxZ() == minZ){ // Northern Border
				nearbyPatches.north.lowerBorder.put(p.getMinX(), p);
				nearbyPatches.north.upperBorder.put(p.getMaxX(), p);
			}
			if (p.getMaxX() == minX){ // Western Border
				nearbyPatches.west.lowerBorder.put(p.getMinZ(), p);
				nearbyPatches.west.upperBorder.put(p.getMaxZ(), p);
			}
			if (p.getMinZ() == maxZ){ // Southern Border
				nearbyPatches.south.lowerBorder.put(p.getMinX(), p);
				nearbyPatches.south.upperBorder.put(p.getMaxX(), p);
			}
			if (p.getMinX() == maxX){ // Eastern Border
				nearbyPatches.south.lowerBorder.put(p.getMinZ(), p);
				nearbyPatches.south.upperBorder.put(p.getMaxZ(), p);
			}
		}
		
		// and now check for holes in patchwork
		
		// TreeSet<Integer> lowerBorders = new TreeSet<Integer>(nearbyPatches.north.lowerBorder.navigableKeySet());
		// TreeSet<Integer> upperBorders = new TreeSet<Integer>(nearbyPatches.north.upperBorder.navigableKeySet());
		// TODO: this is not as simple as that, we have to avoid double connects as well ....
		
	}
}
