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
public class PlayerPosAndLook extends DefinedPacket {

	/*
	 * Source: http://wiki.vg/Protocol#Player_Position_And_Look_2
	 */

    private double x; // X coordinate 
    private double feetY; // Y coordinate 
    private double headY; // Y coordinate 
    private double z; // Z coordinate 
    private float yaw; // Absolute rotation on the X Axis, in degrees 
    private float pitch; // Absolute rotation on the Y Axis, in degrees 
    private boolean onGround; // True if the client is on the ground, False otherwise
    
    @Override
    public void read(ByteBuf buf)
    {
    	x = buf.readDouble();
    	feetY = buf.readDouble();
    	headY = buf.readDouble();
    	z = buf.readDouble();
    	yaw = buf.readFloat();
    	pitch = buf.readFloat();
    	onGround = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf)
    {
    	buf.writeDouble(x);
    	buf.writeDouble(feetY);
    	buf.writeDouble(headY);
    	buf.writeDouble(z);
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
