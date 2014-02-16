package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.DefinedPacket;

@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = false,of="chunks")
@EqualsAndHashCode(callSuper = false)
public class BulkChunkData extends DefinedPacket {

	/*
	 * Source: http://wiki.vg/Protocol#Map_Chunk_Bulk
	 * unfortunately we can't use Steveice10's MCProtocolLib (https://github.com/Steveice10/MCProtocolLib)
	 * so we reimplement the code from https://github.com/Steveice10/MCProtocolLib/blob/1.7.4/src/main/java/ch/spacebase/mc/protocol/packet/ingame/server/world/ServerMultiChunkDataPacket.java
	 */
	
	@Getter private boolean skylight; // Whether or not the chunk data contains a light nibble array. This is true in the main world, false in the end + nether
	@Getter private byte[] deflatedPayload; // Compressed payload
	@Getter private List<InflatedChunkData> chunks = new LinkedList<>();
	@Getter private boolean unpacked = false;  // if there were changes we need to repack the compressed payload
	
	private void unpackpayload(){
	 
		unpacked = true;
		byte[] inflated = new byte[196864 * chunks.size()];
		Inflater inflater = new Inflater();
		inflater.setInput(deflatedPayload);
		try {
			inflater.inflate(inflated);
		} catch(DataFormatException e) {
			throw new BadPacketException("Bad compressed data format in Bulk Chunk Packet");
		} finally {
			inflater.end();
		}
		int pos = 0;
		for(InflatedChunkData chunk: chunks) {
			int numberOfSections = 0;
			int numberOfAdditionalDataFields = 0;
			for(int ch = 0; ch < 16; ch++) {
				numberOfSections += chunk.getChunkMask() >> ch & 1;
			    numberOfAdditionalDataFields += chunk.getExtChunkMask() >> ch & 1;
			}
			int length = (8192 * numberOfSections + 256) + (2048 * numberOfAdditionalDataFields);
			if (skylight) length += 2048 * numberOfSections;
			chunk.setChunkAndBiomeData(Arrays.copyOfRange(inflated, pos, pos + length));
		}
	}
	
	private void packpayload(){
		
		int length = 0;
		for(InflatedChunkData chunk: chunks) length += chunk.getChunkAndBiomeData().length;
		byte[] newDeflatedPayload = new byte[length];
		Deflater deflater = new Deflater();
		int pos = 0;
		for(InflatedChunkData chunk: chunks){
			deflater.setInput(chunk.getChunkAndBiomeData());
			pos += deflater.deflate(newDeflatedPayload, pos, length-pos);
		}
		deflater.setInput(new byte[0]);
		deflater.finish();
		pos += deflater.deflate(newDeflatedPayload, pos, length-pos);
		if (!deflater.finished()) throw new RuntimeException("Deflate output buffer too short");
		deflater.end();
		deflatedPayload = Arrays.copyOf(newDeflatedPayload, pos);
	}
	
	public void deleteChunk(InflatedChunkData chunk){
		
		if (!unpacked) unpackpayload();
		if (!chunks.remove(chunk)) throw new RuntimeException("Trying to remove unlisted chunk");
	}
	
	public void addChunk(InflatedChunkData chunk){
		
		if (!unpacked) unpackpayload();
		if (!chunks.add(chunk)) throw new RuntimeException("Cannot add chunk to bulk packet");
	}
	
	@Override
	public void read(ByteBuf in){

		int numberOfColumns = in.readShort();
		int deflatedLength = in.readInt();
		skylight = in.readBoolean();
		deflatedPayload = new byte[deflatedLength];
		for (int i = 0; i < deflatedLength; i++) deflatedPayload[i] = in.readByte();
		
		for(int count = 0; count < numberOfColumns; count++) {
			InflatedChunkData chunk = new InflatedChunkData();
			chunk.setChunkX(in.readInt());
			chunk.setChunkZ(in.readInt());
			chunk.setChunkMask(in.readShort());
			chunk.setExtChunkMask(in.readShort());
			chunks.add(chunk);
		}
	}

    @Override
    public void write(ByteBuf buf){
    	
    	if (unpacked) packpayload();
    	buf.writeShort((short) chunks.size());
    	buf.writeInt(deflatedPayload.length);
    	buf.writeBoolean(skylight);
    	buf.writeBytes(deflatedPayload);
    	
		for(InflatedChunkData chunk: chunks) {
			buf.writeInt(chunk.getChunkX());
			buf.writeInt(chunk.getChunkZ());
			buf.writeShort(chunk.getChunkMask());
			buf.writeShort(chunk.getExtChunkMask());
		}
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }

}
