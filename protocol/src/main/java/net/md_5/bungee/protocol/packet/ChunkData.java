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
public class ChunkData extends DefinedPacket {
	
	/*
	 * Source: http://wiki.vg/Protocol#Chunk_Data
	 */

    private int chunkX; // Chunk X coordinate 
    private int chunkZ; // Chunk Z coordinate 
    private boolean groundUpContinuous; // This is True if the packet represents all sections in this vertical column, where the primary bit map specifies exactly which sections are included, and which are air
    private short primaryBitMap; // Bitmask with 1 for every 16x16x16 section which data follows in the compressed data.
    private short addBitMap; // Same as above, but this is used exclusively for the 'add' portion of the payload
	private int compressedSize; // Size of compressed chunk data 
    private byte[] compressedData; // The chunk data is compressed using Zlib Deflate

    @Override
    public void read(ByteBuf buf)
    {
    	chunkX = buf.readInt();
    	chunkZ = buf.readInt();
    	groundUpContinuous = buf.readBoolean();
    	primaryBitMap = buf.readShort();
    	addBitMap = buf.readShort();
    	compressedSize = buf.readInt();
    	compressedData = new byte[ compressedSize ];
        buf.readBytes( compressedData );
    }

    @Override
    public void write(ByteBuf buf)
    {
    	buf.writeInt(chunkX);
    	buf.writeInt(chunkZ);
    	buf.writeBoolean(groundUpContinuous);
    	buf.writeShort(primaryBitMap);
    	buf.writeShort(addBitMap);
    	buf.writeInt(compressedSize);
        buf.writeBytes( compressedData );
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }

}
