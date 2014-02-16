package net.md_5.bungee;

import lombok.Getter;
import net.md_5.bungee.netty.ChannelWrapper;

public class PatchworkConnection extends ServerConnection {

	@Getter
	private BungeePatchworkInfo patchwork;
	
	public PatchworkConnection(ChannelWrapper ch, BungeeServerInfo target, BungeePatchworkInfo patchwork) {
		super(ch, target);
        this.patchwork = patchwork;
	}
}
