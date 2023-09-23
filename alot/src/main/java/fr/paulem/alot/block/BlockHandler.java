package fr.paulem.alot.block;

import fr.paulem.alot.ALOT;
import fr.paulem.alot.CMain;
import fr.paulem.alot.block.blocks.WaterCollector;
import fr.paulem.alot.block.blocks.WaterCollectorFilled;

import java.util.List;
import java.util.stream.Stream;

public class BlockHandler extends CMain {
    public BlockHandler(ALOT main) {
        super(main);
        for (MushroomCustomBlock custom : init()) {
            main.registeredBlocks.add(new CondensedBlock(custom.item(), custom.faces()));
            main.registeredItems.putIfAbsent(custom.itemKey(), custom.item());
        }
    }

    private List<MushroomCustomBlock> init() {
        return Stream.of(new WaterCollector(main), new WaterCollectorFilled(main)).map(MushroomCustomBlock::init).toList();
    }
}
