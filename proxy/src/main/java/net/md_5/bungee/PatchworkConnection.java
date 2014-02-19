package net.md_5.bungee;

import lombok.Getter;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;

public class PatchworkConnection extends ServerConnection {

	@Getter
	private BungeePatchworkInfo patchwork;
    // superclass BungeeServerInfo info factually BungeePatchInfo here

	public PatchworkConnection(ChannelWrapper ch, BungeeServerInfo info, BungeePatchworkInfo patchwork) {
		super(ch, info);
        this.patchwork = patchwork;
	}
	
	@Override
	public BungeePatchworkInfo getInfo(){ // ooh, very bad style, but doing otherwise would break existing code
		return patchwork;
	}
	
	public BungeePatchInfo getPatchInfo(){
		return (BungeePatchInfo) info;
	}
	
	public void sendToOtherPatches(DefinedPacket definedPacket){
		
	}

	/**
	 * Check if player position requires activity
	 * @param x
	 * @param z
	 * @return true if activity was performed
	 */
	public boolean playerPosUpdate(double x, double z){
		return false;
	}

}
