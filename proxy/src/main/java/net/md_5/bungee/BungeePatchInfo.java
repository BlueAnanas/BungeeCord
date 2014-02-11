package net.md_5.bungee;

import java.net.InetSocketAddress;
import java.util.Collection;
import com.google.common.base.Preconditions;

import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.PatchInfo;
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

    /* following methods could be moved to superclass so they don't need to be overridden */
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

}
