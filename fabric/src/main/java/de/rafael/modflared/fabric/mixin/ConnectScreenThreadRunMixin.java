package de.rafael.modflared.fabric.mixin;

import de.rafael.modflared.methods.ConnectScreenMethods;
import io.netty.channel.ChannelFuture;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1")
public abstract class ConnectScreenThreadRunMixin implements Runnable {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect" +
            "(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;"))
    private ChannelFuture connect(@NotNull InetSocketAddress address, boolean useEpoll, ClientConnection connection) {
        return ConnectScreenMethods.connect(address, useEpoll, connection);
    }

}
