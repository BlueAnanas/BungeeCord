package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PlayerLook extends DefinedPacket {

	/*
	 * Source: http://wiki.vg/Protocol#Player_Look
	 */

    private float yaw; // Absolute rotation on the X Axis, in degrees 
    private float pitch; // Absolute rotation on the Y Axis, in degrees 
    private boolean onGround; // True if the client is on the ground, False otherwise
    
    @Override
    public void read(ByteBuf buf)
    {
    	yaw = buf.readFloat();
    	pitch = buf.readFloat();
    	onGround = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf)
    {
    	buf.writeFloat(yaw);
    	buf.writeFloat(pitch);
    	buf.writeBoolean(onGround);
    }

    
    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }

}
