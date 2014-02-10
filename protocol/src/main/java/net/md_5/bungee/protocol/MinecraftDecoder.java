package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import net.md_5.bungee.protocol.Protocol.ProtocolDirection;
import lombok.Setter;

public class MinecraftDecoder extends ByteToMessageDecoder
{

    private ProtocolDirection protocolDirection;
    private Protocol protocol;
    private ProtocolDirection formalProtocolDirection;
    @Setter
    private int protocolVersion;

    public MinecraftDecoder(Protocol prot, boolean serv, int protVer){
    	protocol = prot;
    	formalProtocolDirection = ( serv ) ? prot.TO_SERVER : prot.TO_CLIENT;
    	protocolDirection = formalProtocolDirection;
    	protocolVersion = protVer;
    }
    
    public MinecraftDecoder(Protocol prot, String protDirName, int protVer){
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
    {
        ByteBuf copy = in.copy(); // TODO

        int packetId = DefinedPacket.readVarInt( in );
        
        DefinedPacket packet = null;
        if ( protocolDirection.hasPacket( packetId ) )
        {
            packet = protocolDirection.createPacket( packetId );
            packet.read( in, protocolDirection, protocolVersion );
            if ( in.readableBytes() != 0 )
            {
                throw new BadPacketException( "Did not read all bytes from packet " + packet.getClass() + " " + packetId + " Protocol " + protocolDirection );
            }
        } else
        {
            in.skipBytes( in.readableBytes() );
        }
        
        out.add( new PacketWrapper( packet, copy ) );
    }
}
