package net.md_5.bungee.api.config;

/**
 * Class used to represent a server patchwork to connect to.
 */
public interface PatchworkInfo extends ServerInfo{

    void addPatch(PatchInfo server);

}
