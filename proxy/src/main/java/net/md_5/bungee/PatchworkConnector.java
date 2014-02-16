package net.md_5.bungee;

import java.util.Queue;

import com.google.common.base.Preconditions;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.HomePatchDownstreamBridge;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftOutput;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;

public class PatchworkConnector extends ServerConnector {
	
	private BungeePatchworkInfo patchwork;
	
	public PatchworkConnector(ProxyServer bungee, UserConnection user, BungeeServerInfo target) {
		super(bungee, user, (BungeeServerInfo) ((BungeePatchworkInfo) target).getCurrentPatchInfo().getServer());
        Preconditions.checkState( target instanceof BungeePatchworkInfo, "Need PatchworkInfo to connect to" );
        patchwork = (BungeePatchworkInfo) target;
	}

    @Override
    public void handle(LoginSuccess loginSuccess) throws Exception  // TODO: do we really need to set the protocol here already, we only await Login 
    {
        Preconditions.checkState( thisState == State.LOGIN_SUCCESS, "Not expecting LOGIN_SUCCESS" );
        ch.setProtocol( Protocol.GAMEONHOMEPATCH );
        thisState = State.LOGIN;

        throw CancelSendSignal.INSTANCE;
    }
    
    @Override
    public void handle(Login login) throws Exception
    {
        Preconditions.checkState( thisState == State.LOGIN, "Not expecting LOGIN" );

        ServerConnection server = new PatchworkConnection( ch, target, patchwork );
        ServerConnectedEvent event = new ServerConnectedEvent( user, server );
        bungee.getPluginManager().callEvent( event );

        ch.write( BungeeCord.getInstance().registerChannels() );
        Queue<DefinedPacket> packetQueue = target.getPacketQueue();
        synchronized ( packetQueue )
        {
            while ( !packetQueue.isEmpty() )
            {
                ch.write( packetQueue.poll() );
            }
        }

        if ( user.getSettings() != null )
        {
            ch.write( user.getSettings() );
        }

        synchronized ( user.getSwitchMutex() )
        {
            if ( user.getServer() == null )
            {
                // Once again, first connection
                user.setClientEntityId( login.getEntityId() );
                user.setServerEntityId( login.getEntityId() ); // TODO: serverEntityId should be stored on ServerConnection

                // Set tab list size, this sucks balls, TODO: what shall we do about packet mutability
                Login modLogin = new Login( login.getEntityId(), login.getGameMode(), (byte) login.getDimension(), login.getDifficulty(),
                        (byte) user.getPendingConnection().getListener().getTabListSize(), login.getLevelType() );

                user.unsafe().sendPacket( modLogin );

                MinecraftOutput out = new MinecraftOutput();
                out.writeStringUTF8WithoutLengthHeaderBecauseDinnerboneStuffedUpTheMCBrandPacket( ProxyServer.getInstance().getName() + " (" + ProxyServer.getInstance().getVersion() + ")" );
                user.unsafe().sendPacket( new PluginMessage( "MC|Brand", out.toArray() ) );
            } else
            {  // TODO: lots of things as we don't know if we have to server hop 
                user.getTabList().onServerChange();

                Scoreboard serverScoreboard = user.getServerSentScoreboard();
                for ( Objective objective : serverScoreboard.getObjectives() )
                {
                    user.unsafe().sendPacket( new ScoreboardObjective( objective.getName(), objective.getValue(), (byte) 1 ) );
                }
                for ( Team team : serverScoreboard.getTeams() )
                {
                    user.unsafe().sendPacket( new net.md_5.bungee.protocol.packet.Team( team.getName() ) );
                }
                serverScoreboard.clear();

                user.sendDimensionSwitch();

                user.setServerEntityId( login.getEntityId() );
                user.unsafe().sendPacket( new Respawn( login.getDimension(), login.getDifficulty(), login.getGameMode(), login.getLevelType() ) );

                // Remove from old servers
                user.getServer().setObsolete( true );
                user.getServer().disconnect( "Quitting" );
            }

            // TODO: Fix this?
            if ( !user.isActive() )
            {
                server.disconnect( "Quitting" );
                // Silly server admins see stack trace and die
                bungee.getLogger().warning( "No client connected for pending server!" );
                return;
            }

            // Add to new server
            // TODO: Move this to the connected() method of DownstreamBridge
            target.addPlayer( user );
            user.getPendingConnects().remove( target );
            user.setDimensionChange( false );

            user.setServer( server );
            ch.getHandle().pipeline().get( HandlerBoss.class ).setHandler( new HomePatchDownstreamBridge( bungee, user, server ) );
        }

        bungee.getPluginManager().callEvent( new ServerSwitchEvent( user ) );

        thisState = State.FINISHED;

        throw CancelSendSignal.INSTANCE;
    }
    
    @Override
    public String toString()
    {
        return "[" + user.getName() + "] <-> PatchworkConnector [" + target.getName() + "]";
    }
}
