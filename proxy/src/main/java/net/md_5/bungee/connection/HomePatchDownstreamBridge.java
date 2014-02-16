package net.md_5.bungee.connection;

import java.util.logging.Level;

import com.google.common.base.Preconditions;

import net.md_5.bungee.PatchworkConnection;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.packet.BulkChunkData;
import net.md_5.bungee.protocol.packet.ChunkData;
import net.md_5.bungee.protocol.packet.PlayerPosAndLookServer;

public class HomePatchDownstreamBridge extends DownstreamBridge {
	
	public HomePatchDownstreamBridge (ProxyServer bungee, UserConnection con, ServerConnection server){
		super(bungee, con, server);
        Preconditions.checkState( server instanceof PatchworkConnection, "Need PatchworkConnection to bridge" );
	}

    @Override
    public void handle(PlayerPosAndLookServer playerPosAndLook) throws Exception
    {
        bungee.getLogger().log(Level.INFO, "--- HPDsBridge: PlayerPosAndLookServer {0}", playerPosAndLook.toString());
        con.unsafe().sendPacket(playerPosAndLook);
    }

    @Override
    public void handle(ChunkData chunkData) throws Exception
    {
        bungee.getLogger().log(Level.INFO, "--- HPDsBridge: ChunkData {0}", chunkData.toString());
        con.unsafe().sendPacket(chunkData);
    }

    @Override
    public void handle(BulkChunkData bulkChunkData) throws Exception
    {
        bungee.getLogger().log(Level.INFO, "--- HPDsBridge: BulkChunkData {0}", bulkChunkData.toString());
        con.unsafe().sendPacket(bulkChunkData);
    }

    @Override
    public String toString()
    {
        return "[" + con.getName() + "] <-> HomePatchDownstreamBridge <-> [" + ((PatchworkConnection)server).getPatchwork().getName() + "]";
    }
}
