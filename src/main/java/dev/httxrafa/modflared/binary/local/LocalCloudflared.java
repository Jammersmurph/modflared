package dev.httxrafa.modflared.binary.local;

import dev.httxrafa.modflared.Modflared;
import dev.httxrafa.modflared.binary.Cloudflared;
import dev.httxrafa.modflared.tunnel.RunningTunnel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class LocalCloudflared extends Cloudflared {

    private final String path;

    public LocalCloudflared(String version, String path) {
        super(version);
        this.path = path;
    }

    @Override
    public CompletableFuture<Void> prepare() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String[] buildCommand(RunningTunnel.@NotNull Access access) {
        return access.command(path, false);
    }

    private static @Nullable String resolveCloudflaredPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String cmd = os.contains("win") ? "where" : "which";
        try {
            var proc = new ProcessBuilder(cmd, "cloudflared").start();
            var reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = reader.readLine();
            if (line != null && !line.isBlank()) {
                return line.trim();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public static @Nullable Cloudflared tryCreate() {
        // Check if cloudflared is already installed on the system
        try {
            var builder = new ProcessBuilder("cloudflared", "--version");
            var process = builder.start();
            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String versionString = reader.readLine();
            String version = versionString.split(" ")[2];
            Modflared.LOGGER.info("Cloudflared output: {}", versionString);
            Modflared.LOGGER.info("Cloudflared version {} is already installed on the system", version);
            String resolvedPath = resolveCloudflaredPath();
            if (resolvedPath != null) {
                Modflared.LOGGER.info("Cloudflared resolved to: {}", resolvedPath);
            }
            return new LocalCloudflared(version, resolvedPath != null ? resolvedPath : "cloudflared");
        } catch (Throwable ignored) {
            Modflared.LOGGER.info("Cloudflared is not installed on the system. Downloading it if necessary...");
        }
        return null;
    }

}
