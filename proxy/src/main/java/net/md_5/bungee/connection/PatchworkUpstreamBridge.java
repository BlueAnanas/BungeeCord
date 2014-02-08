package net.md_5.bungee.connection;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.packet.ChunkData;
import net.md_5.bungee.protocol.packet.PlayerPosAndLookServer;

public class PatchworkUpstreamBridge extends UpstreamBridge {
	
    public PatchworkUpstreamBridge(ProxyServer bungee, UserConnection con){
    	super(bungee, con);
    }
    
    @Override
    public void handle(ChunkData chunkData){
    	this.getBungee().getLogger().info( "ChunkData "+chunkData.getChunkX()+"X "+chunkData.getChunkZ()+"Z grCont="+chunkData.isGroundUpContinuous() );
        this.getCon().getServer().getCh().write( chunkData );
    }
    
    @Override
    public void handle(PlayerPosAndLookServer playerPosAndLook){
    	this.getBungee().getLogger().info( "Player "+playerPosAndLook.getX()+"X "+playerPosAndLook.getY()+"Y "+playerPosAndLook.getZ()+"Z "
         +playerPosAndLook.getPitch()+"Pitch "+playerPosAndLook.getYaw()+"Yaw OnGround="+playerPosAndLook.isOnGround() );
        this.getCon().getServer().getCh().write( playerPosAndLook );
    	
    }

}
