package net.md_5.bungee.protocol.packet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(of={"chunkX","chunkZ"})
@EqualsAndHashCode
@NoArgsConstructor
@Getter @Setter
public class InflatedChunkData {

	private int chunkX;
	private int chunkZ;
	private short chunkMask;
	private short extChunkMask;
	private byte[] chunkAndBiomeData;

}
