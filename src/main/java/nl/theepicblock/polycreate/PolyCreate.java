package nl.theepicblock.polycreate;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllEntityTypes;
import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.impl.poly.block.SimpleReplacementPoly;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import nl.theepicblock.polycreate.blocks.KineticBlockPoly;
import nl.theepicblock.polycreate.entity.PotatoProjectileWizard;

public class PolyCreate implements ModInitializer, PolyMcEntrypoint {

	@Override
	public void onInitialize() {
	}

	@Override
	public void registerPolys(PolyRegistry registry) {
		registry.registerBlockPoly(AllBlocks.SHAFT.get(), new KineticBlockPoly(registry));
		registry.registerBlockPoly(AllBlocks.COGWHEEL.get(), new KineticBlockPoly(registry));
		registry.registerBlockPoly(AllBlocks.LARGE_COGWHEEL.get(), new KineticBlockPoly(registry));
		registry.registerBlockPoly(AllBlocks.CRUSHING_WHEEL.get(), new KineticBlockPoly(registry) {
			@Override
			public BlockState getClientBlock(BlockState input) {
				return Blocks.BARRIER.getDefaultState();
			}
		});
		registry.registerBlockPoly(AllBlocks.CRUSHING_WHEEL_CONTROLLER.get(), new SimpleReplacementPoly(Blocks.SWEET_BERRY_BUSH)); // FIXME, berry bushes behave differently from crushing wheels

		registry.registerEntityPoly(AllEntityTypes.POTATO_PROJECTILE.get(), PotatoProjectileWizard::new);
	}

}
