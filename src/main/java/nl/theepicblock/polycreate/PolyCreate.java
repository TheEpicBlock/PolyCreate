package nl.theepicblock.polycreate;

import com.simibubi.create.AllBlocks;
import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.PolyRegistry;
import net.fabricmc.api.ModInitializer;
import nl.theepicblock.polycreate.blocks.KineticBlockPoly;

public class PolyCreate implements ModInitializer, PolyMcEntrypoint {

	@Override
	public void onInitialize() {
	}

	@Override
	public void registerPolys(PolyRegistry registry) {
		registry.registerBlockPoly(AllBlocks.SHAFT.get(), new KineticBlockPoly(registry));
		registry.registerBlockPoly(AllBlocks.COGWHEEL.get(), new KineticBlockPoly(registry));
		registry.registerBlockPoly(AllBlocks.LARGE_COGWHEEL.get(), new KineticBlockPoly(registry));
	}

}
