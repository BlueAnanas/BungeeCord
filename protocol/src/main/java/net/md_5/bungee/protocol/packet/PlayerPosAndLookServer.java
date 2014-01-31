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
public class PlayerPosAndLookServer extends DefinedPacket {

	/*
	 * Source: http://wiki.vg/Protocol#Player_Position_And_Look
	 */

    private double X; // X coordinate 
    private double Y; // Y coordinate 
    private double Z; // Z coordinate 
    private float Yaw; // Absolute rotation on the X Axis, in degrees 
    private float Pitch; // Absolute rotation on the Y Axis, in degrees 
    private boolean onGround; // True if the client is on the ground, False otherwise
    
    @Override
    public void read(ByteBuf buf)
    {
    	X = buf.readDouble();
    	Y = buf.readDouble();
    	Z = buf.readDouble();
    	Yaw = buf.readFloat();
    	Pitch = buf.readFloat();
    	onGround = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf)
    {
    	buf.writeDouble(X);
    	buf.writeDouble(Y);
    	buf.writeDouble(Z);
    	buf.writeFloat(Yaw);
    	buf.writeFloat(Pitch);
    	buf.writeBoolean(onGround);
    }

    
    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }

}
