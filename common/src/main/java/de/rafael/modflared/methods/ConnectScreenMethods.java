package de.rafael.modflared.methods;

import de.rafael.modflared.Modflared;
import de.rafael.modflared.interfaces.mixin.IConnectScreen;
import de.rafael.modflared.tunnel.TunnelStatus;
import io.netty.channel.ChannelFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public class ConnectScreenMethods {

    public static ChannelFuture connect(@NotNull InetSocketAddress address, boolean useEpoll, ClientConnection connection) {
        var status = Modflared.TUNNEL_MANAGER.handleConnect(address);
        Modflared.TUNNEL_MANAGER.prepareConnection(status, connection);

        var currentScreen =  MinecraftClient.getInstance().currentScreen;
        if (currentScreen instanceof ConnectScreen connectScreen) {
            ((IConnectScreen) connectScreen).setStatus(status);
        }

        return ClientConnection.connect(status.state() == TunnelStatus.State.USE ? status.runningTunnel().access().tunnelAddress() : address, useEpoll, connection);
    }

}
