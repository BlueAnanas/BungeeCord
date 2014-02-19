package net.md_5.bungee.connection;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import com.google.common.base.Preconditions;

import net.md_5.bungee.PatchworkConnection;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.packet.BulkChunkData;
import net.md_5.bungee.protocol.packet.ChunkData;
import net.md_5.bungee.protocol.packet.InflatedChunkData;
import net.md_5.bungee.protocol.packet.PlayerPosAndLookServer;

public class HomePatchDownstreamBridge extends DownstreamBridge {
	
	public HomePatchDownstreamBridge (ProxyServer bungee, UserConnection con, ServerConnection server){
		super(bungee, con, server);
        Preconditions.checkState( server instanceof PatchworkConnection, "Need PatchworkConnection to bridge" );
	}

    @Override
    public void handle(PlayerPosAndLookServer playerPosAndLook) throws Exception
    {
    	if (((PatchworkConnection)server).playerPosUpdate(playerPosAndLook.getX(), playerPosAndLook.getZ())) {
        	bungee.getLogger().log(Level.INFO, "--- HPDsBridge: patchwork action {0}", playerPosAndLook.toString());
        }
    }

    @Override
    public void handle(ChunkData chunkData) throws Exception
    {
    	if (!((PatchworkConnection)server).getPatchInfo().chunkInPatch(chunkData.getChunkX(), chunkData.getChunkZ())) {
        	bungee.getLogger().log(Level.INFO, "--- HPDsBridge: dropped chunk {0}", chunkData.toString());
        	throw CancelSendSignal.INSTANCE;
        }
    }

    @Override
    public void handle(BulkChunkData bulkChunkData) throws Exception
    {
    	List<InflatedChunkData> removeChunks = new LinkedList<>(); // we can throw away the packet without unpacking when all chunks are off area with this local list
    	boolean chunksLeft = false;
    	for (InflatedChunkData chunk: bulkChunkData.getChunks()){
    		if (!((PatchworkConnection)server).getPatchInfo().chunkInPatch(chunk.getChunkX(), chunk.getChunkZ())) {
    			removeChunks.add(chunk);
    		} else {
    			chunksLeft = true;
    		}
    	}
    	if (chunksLeft) {
        	for (InflatedChunkData chunk: removeChunks) bulkChunkData.deleteChunk(chunk);
        	if (!removeChunks.isEmpty()) bungee.getLogger().log(Level.INFO, "--- HPDsBridge: dropped part bulk of chunks {0}", bulkChunkData.toString());
    	} else {
    		bungee.getLogger().log(Level.INFO, "--- HPDsBridge: dropped full bulk of chunks {0}", bulkChunkData.toString());
        	throw CancelSendSignal.INSTANCE;
    	}
    }

    @Override
    public String toString()
    {
        return "[" + con.getName() + "] <-> HomePatchDownstreamBridge <-> [" + ((PatchworkConnection)server).getPatchwork().getName() + "]";
    }
}
