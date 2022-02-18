package nl.theepicblock.polycreate;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.PolyRegistry;
import net.fabricmc.api.ModInitializer;
import nl.theepicblock.polycreate.blocks.ShaftPoly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolyCreate implements ModInitializer, PolyMcEntrypoint {

	@Override
	public void onInitialize() {
	}

	@Override
	public void registerPolys(PolyRegistry registry) {
		registry.registerBlockPoly(AllBlocks.SHAFT.get(), new ShaftPoly(registry));
	}
}
