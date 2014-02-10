package net.md_5.bungee.connection;

import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.packet.ChunkData;
import net.md_5.bungee.protocol.packet.PlayerPosAndLookServer;

public class PatchworkDownstreamBridge extends DownstreamBridge {
	
    public PatchworkDownstreamBridge(ProxyServer bungee, UserConnection con, ServerConnection server){
    	super(bungee, con, server);
    }
    
    @Override
    public void handle(ChunkData chunkData){
    	this.getBungee().getLogger().info( "Downstream: Packet_ChunkData "+chunkData.getChunkX()+"X "+chunkData.getChunkZ()+"Z grCont="+chunkData.isGroundUpContinuous() );
    }
    
    @Override
    public void handle(PlayerPosAndLookServer playerPosAndLook){
    	this.getBungee().getLogger().info( "Downstream: Packet_Player "+playerPosAndLook.getX()+"X "+playerPosAndLook.getY()+"Y "+playerPosAndLook.getZ()+"Z "
         +playerPosAndLook.getPitch()+"Pitch "+playerPosAndLook.getYaw()+"Yaw OnGround="+playerPosAndLook.isOnGround() );
    }

}
