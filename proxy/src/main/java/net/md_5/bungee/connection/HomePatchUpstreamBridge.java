package net.md_5.bungee.connection;

import java.util.logging.Level;

import net.md_5.bungee.PatchworkConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.packet.PlayerLook;
import net.md_5.bungee.protocol.packet.PlayerOnGround;
import net.md_5.bungee.protocol.packet.PlayerPosAndLook;
import net.md_5.bungee.protocol.packet.PlayerPosition;

import com.google.common.base.Preconditions;

public class HomePatchUpstreamBridge extends UpstreamBridge {

	public HomePatchUpstreamBridge (ProxyServer bungee, UserConnection con){
		super(bungee, con);
        Preconditions.checkState( con.getServer() instanceof PatchworkConnection, "Need PatchworkConnection to bridge" );
	}

    @Override
    public void handle(PlayerPosAndLook playerPosAndLook) throws Exception
    {
    	((PatchworkConnection)con.getServer()).sendToOtherPatches(playerPosAndLook);
    	if (((PatchworkConnection)con.getServer()).playerPosUpdate(playerPosAndLook.getX(), playerPosAndLook.getZ())) {
        	bungee.getLogger().log(Level.INFO, "--- HPUsBridge: patchwork action {0}", playerPosAndLook.toString());
        }
    }

    @Override
    public void handle(PlayerLook playerLook) throws Exception
    {
    	((PatchworkConnection)con.getServer()).sendToOtherPatches(playerLook);
    }

    @Override
    public void handle(PlayerPosition playerPosition) throws Exception
    {
    	((PatchworkConnection)con.getServer()).sendToOtherPatches(playerPosition);
    	if (((PatchworkConnection)con.getServer()).playerPosUpdate(playerPosition.getX(), playerPosition.getZ())) {
        	bungee.getLogger().log(Level.INFO, "--- HPUsBridge: patchwork action {0}", playerPosition.toString());
        }
    }

    @Override
    public void handle(PlayerOnGround playerOnGround) throws Exception
    {
    	((PatchworkConnection)con.getServer()).sendToOtherPatches(playerOnGround);
        if (!playerOnGround.isOnGround()) bungee.getLogger().log(Level.INFO, "--- HPUsBridge: player left ground");
    }

    @Override
    public String toString()
    {
        return "[" + con.getName() + "] <-> HomePatchUpstreamBridge <-> [" + ((PatchworkConnection)con.getServer()).getPatchwork().getName() + "]";
    }
}
