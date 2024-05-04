package io.github.kydzombie.stapitech;

import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import org.apache.logging.log4j.Logger;

public class StapiTech {
    @Entrypoint.Namespace
    public static final Namespace NAMESPACE = Null.get();

    @Entrypoint.Logger
    public static final Logger LOGGER = Null.get();
}
