package net.md_5.bungee.protocol;

import net.md_5.bungee.protocol.Protocol.ProtocolDirection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;;

public class MinecraftEncoder extends MessageToByteEncoder<DefinedPacket>
{

    private ProtocolDirection protocolDirection;
    private Protocol protocol;
    private ProtocolDirection formalProtocolDirection;
    @Setter
    private int protocolVersion;

    public MinecraftEncoder(Protocol prot, boolean serv, int protVer){
    	protocol = prot;
    	formalProtocolDirection = ( serv ) ? prot.TO_CLIENT : prot.TO_SERVER;
    	protocolDirection = formalProtocolDirection;
    	protocolVersion = protVer;
    }
    
    public MinecraftEncoder(Protocol prot, String protDirName, int protVer){
    	setProtocolAndDirection(prot, protDirName);
    	protocolVersion = protVer;
    }
    
    public void setProtocolAndDirection(Protocol prot, String protDirName){
    	protocol = prot;
    	try {
			formalProtocolDirection = (ProtocolDirection) Protocol.class.getDeclaredField(protDirName).get(prot);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if (formalProtocolDirection.isEmpty()){
    		// TODO: add checks that formal direction is FROM_HOMEPATCH or FROM_OTHERPATCH
        	try {
				protocolDirection = (ProtocolDirection) Protocol.class.getDeclaredField("TO_CLIENT").get(prot);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} else {
    		protocolDirection = formalProtocolDirection;
    	}
    }
    
    public void setProtocol(Protocol prot){
    	setProtocolAndDirection(prot, formalProtocolDirection.toString());
    }
    
    public void setDirection(String protDirName){
    	setProtocolAndDirection(protocol, protDirName);
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, DefinedPacket msg, ByteBuf out) throws Exception
    {
        DefinedPacket.writeVarInt( protocolDirection.getId( msg.getClass() ), out );
        msg.write( out, protocolDirection, protocolVersion );
    }
}
