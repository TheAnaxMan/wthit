package mcp.mobius.waila.fabric;

import java.nio.file.Path;
import java.util.Optional;

import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.ModInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class FabricCommonService implements ICommonService {

    @Override
    public boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Optional<ModInfo> createModInfo(String namespace) {
        return FabricLoader.getInstance().getModContainer(namespace)
            .map(ModContainer::getMetadata)
            .map(data -> new ModInfo(true, data.getId(), data.getName(), data.getVersion().getFriendlyString()));
    }

}
