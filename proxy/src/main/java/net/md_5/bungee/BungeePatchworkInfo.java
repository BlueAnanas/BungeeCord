package net.md_5.bungee;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Preconditions;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.PatchInfo;
import net.md_5.bungee.api.config.PatchworkInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePatchworkInfo extends BungeeServerInfo implements PatchworkInfo {

	private final LinkedList<PatchInfo> patchInfos = new LinkedList<>();
	private PatchInfo currentPatchInfo;
	
	public BungeePatchworkInfo(String name){
		super(name, null, "", false);  // only name parameter will be used in this class
	}
    
    public Collection<PatchInfo> getPatchInfos(){
    	return patchInfos;
    }
    
    public void addPatch(PatchInfo patchInfo){
    	patchInfos.add(patchInfo);
        if (currentPatchInfo == null) currentPatchInfo = patchInfos.getFirst();
    }

    public void setCurrentPatchInfo(PatchInfo patchInfo) {
        currentPatchInfo = patchInfo;
    }

    public void setCurrentPatchInfo() {
        if (currentPatchInfo == null) currentPatchInfo = patchInfos.getFirst();
    }

	public String getCurrentName() {
		return currentPatchInfo.getName();
	}
	
	/* a block of stubs to get hold of the real server info */
	@Override
	public InetSocketAddress getAddress() {
		return currentPatchInfo.getAddress();
	}
	@Override
	public String getMotd() {
		return currentPatchInfo.getMotd();
	}
	@Override
	public boolean isRestricted() {
		return ((BungeeServerInfo) currentPatchInfo).isRestricted();
	}
	@Override
    public void addPlayer(ProxiedPlayer player)
    {
    	((BungeeServerInfo) currentPatchInfo).addPlayer( player );
    }
	@Override
    public void removePlayer(ProxiedPlayer player)
    {
    	((BungeeServerInfo) currentPatchInfo).removePlayer( player );
    }
    @Override
    public Collection<ProxiedPlayer> getPlayers()
    {
        return currentPatchInfo.getPlayers();
    }
    @Override
    public void sendData(String channel, byte[] data)
    {
    	currentPatchInfo.sendData(channel, data);
    }
    @Override
    public void ping(final Callback<ServerPing> callback)
    {
    	currentPatchInfo.ping(callback);
    }

    /* following methods should be moved to superclass so they don't need to be overridden in both subclasses */
    @Override
    public boolean canAccess(CommandSender player) // TODO: can be moved to superclass - is subclasssecure
    {
        Preconditions.checkNotNull( player, "player" );
        return !isRestricted() || player.hasPermission( "bungeecord.server." + getName() );
    }

    @Override
    public int hashCode() // TODO: can be moved to superclass - is subclasssecure
    {
        return getAddress().hashCode();
    }

    @Override
    public ServerInfo cloneIfNeeded(){
    	BungeePatchworkInfo bpwi = new BungeePatchworkInfo(this.getName());
    	for(PatchInfo bpi: this.getPatchInfos()){
    		bpwi.addPatch(bpi);
    	}
    	bpwi.setCurrentPatchInfo(currentPatchInfo);
    	return bpwi; 
    }


}
