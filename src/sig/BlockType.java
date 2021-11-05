package sig;

public enum BlockType {
    GRASS(
        TextureType.GRASS_TOP,
        TextureType.DIRT,
        TextureType.GRASS2),
    STONE(TextureType.STONE),
    DIRT(TextureType.DIRT),
    PLANKS(TextureType.PLANK),
    SLAB(TextureType.SLAB_TOP,TextureType.SLAB),
    BRICK(TextureType.BRICK),
    TNT(TextureType.TNT_TOP,TextureType.TNT_BOT,TextureType.TNT),
    WEB(TextureType.WEB),
    FLOWER_RED(TextureType.FLOWER_RED),
    FLOWER_YELLOW(TextureType.FLOWER_YELLOW),
    SOLID_WATER(TextureType.SOLID_WATER),
    SAPLING(TextureType.SAPLING),
    COBBLESTONE(TextureType.COBBLESTONE),
    BEDROCK(TextureType.BEDROCK),
    SAND(TextureType.SAND),
    GRAVEL(TextureType.GRAVEL),
    LOG(TextureType.LOG_TOP,TextureType.LOG),
    IRON_BLOCK(TextureType.IRON_BLOCK),
    GOLD_BLOCK(TextureType.GOLD_BLOCK),
    DIAMOND_BLOCK(TextureType.DIAMOND_BLOCK),
    EMERALD_BLOCK(TextureType.EMERALD_BLOCK),
    FIELD_MUSHROOM_RED(TextureType.FIELD_MUSHROOM_RED),
    FIELD_MUSHROOM_BROWN(TextureType.FIELD_MUSHROOM_BROWN),
    SAPLING2(TextureType.SAPLING2),
    GOLD_ORE(TextureType.GOLD_ORE),
    IRON_ORE(TextureType.IRON_ORE),
    COAL_ORE(TextureType.COAL_ORE),
    BOOKSHELF(TextureType.PLANK,TextureType.BOOKSHELF),
    MOSSY_COBBLESTONE(TextureType.MOSSY_COBBLESTONE),
    OBSIDIAN(TextureType.OBSIDIAN),
    GRASS_SIDE_TOP(TextureType.GRASS_SIDE_TOP),
    FIELD_GRASS(TextureType.FIELD_GRASS),
    CRAFTING_TABLE(TextureType.CRAFTING_TABLE_TOP,TextureType.CRAFTING_TABLE_TOP,TextureType.CRAFTING_TABLE_FRONT,TextureType.CRAFTING_TABLE,TextureType.CRAFTING_TABLE),
    FURNACE(TextureType.FURNACE,TextureType.FURNACE,TextureType.FURNACE_FRONT,TextureType.FURNACE_BACK,TextureType.FURNACE),
    DISPENSER(TextureType.FURNACE,TextureType.FURNACE,TextureType.DISPENSER_FRONT,TextureType.FURNACE_BACK,TextureType.FURNACE),
    SPONGE(TextureType.SPONGE),
    GLASS(TextureType.GLASS),
    DIAMOND_ORE(TextureType.DIAMOND_ORE),
    REDSTONE_ORE(TextureType.REDSTONE_ORE),
    LEAVES(TextureType.LEAVES),
    LEAVES2(TextureType.LEAVES2),
    STONE_BRICK(TextureType.STONE_BRICK),
    FIELD_DEAD_GRASS(TextureType.FIELD_DEAD_GRASS),
    FIELD_GRASS2(TextureType.FIELD_GRASS2),
    SPRUCE_SAPLING(TextureType.SAPLING2),
    WOOL(TextureType.WOOL),
    SPAWNER(TextureType.CAGE),
    SNOW(TextureType.SNOW),
    ICE(TextureType.ICE),
    SNOW_DIRT(TextureType.SNOW,TextureType.DIRT,TextureType.SNOW_DIRT),
    CACTUS_TOP(TextureType.CACTUS_TOP,TextureType.CACTUS),
    CACTUS(TextureType.CACTUS_INSIDE_TOP,TextureType.CACTUS),
    CLAY(TextureType.CLAY),
    SUGARCANE(TextureType.SUGARCANE),
    JUKEBOX(TextureType.JUKEBOX_TOP,TextureType.COMMAND_BLOCK_TOP,TextureType.JUKEBOX),
    LILYPAD(TextureType.LILYPAD),
    PODZOL(TextureType.PODZOL_TOP,TextureType.DIRT,TextureType.PODZOL),
    SAPLING3(TextureType.SAPLING3),
    TORCH(TextureType.TORCH),
    DOOR(TextureType.DOOR),
    IRON_DOOR(TextureType.IRON_DOOR),
    LADDER(TextureType.LADDER),
    TRAPDOOR(TextureType.TRAPDOOR),
    IRON_BARS(TextureType.IRON_BARS),
    SOIL_WET(TextureType.WET_SOIL,TextureType.DIRT,TextureType.DIRT),
    WHEAT(TextureType.WHEAT),
    WHEAT_0(TextureType.WHEAT_0),
    WHEAT_1(TextureType.WHEAT_1),
    WHEAT_2(TextureType.WHEAT_2),
    WHEAT_3(TextureType.WHEAT_3),
    WHEAT_4(TextureType.WHEAT_4),
    WHEAT_5(TextureType.WHEAT_5),
    WHEAT_6(TextureType.WHEAT_6),
    LEVER(TextureType.LEVER),
    REDSTONE_TORCH(TextureType.REDSTONE_TORCH),
    MOSSY_STONE_BRICK(TextureType.MOSSY_STONE_BRICK),
    CRACKED_STONE_BRICK(TextureType.CRACKED_STONE_BRICK),
    PUMPKIN(TextureType.PUMPKIN_TOP,TextureType.PUMPKIN,TextureType.PUMPKIN_FRONT,TextureType.PUMPKIN,TextureType.PUMPKIN),
    JACKOLANTERN(TextureType.PUMPKIN_TOP,TextureType.PUMPKIN,TextureType.JACKOLANTERN,TextureType.PUMPKIN,TextureType.PUMPKIN),
    CAKE(TextureType.CAKE_TOP,TextureType.CAKE_BOTTOM,TextureType.CAKE),
    CAKE_EATEN(TextureType.CAKE_TOP,TextureType.CAKE_BOTTOM,TextureType.CAKE,TextureType.CAKE,TextureType.CAKE_EATEN,TextureType.CAKE),
    RED_MUSHROOM(TextureType.RED_MUSHROOM,TextureType.MUSHROOM_INSIDE,TextureType.RED_MUSHROOM,TextureType.MUSHROOM_INSIDE,TextureType.MUSHROOM_INSIDE,TextureType.MUSHROOM_INSIDE),
    BROWN_MUSHROOM(TextureType.BROWN_MUSHROOM,TextureType.MUSHROOM_INSIDE,TextureType.RED_MUSHROOM,TextureType.MUSHROOM_INSIDE,TextureType.MUSHROOM_INSIDE,TextureType.MUSHROOM_INSIDE),
    MUSHROOM_STALK(TextureType.MUSHROOM_STALK),
    VINES(TextureType.VINES),
    LAPIS_LAZULI_BLOCK(TextureType.LAPIS_LAZULI_BLOCK),
    GREEN_WOOL(TextureType.GREEN_WOOL),
    LIME_WOOL(TextureType.LIME_WOOL),
    REDSTONE_REPEATER(TextureType.REDSTONE_REPEATER_OFF),
    REDSTONE_REPEATER_ON(TextureType.REDSTONE_REPEATER_ON),
    LEAVES3(TextureType.LEAVES3),
    LEAVES4(TextureType.LEAVES4),
    BED(TextureType.BED_TOP,TextureType.PLANK,TextureType.BED_FRONT,TextureType.BED_BACK,TextureType.BED_SIDE),
    MELON(TextureType.MELON_TOP,TextureType.MELON),
    CAULDRON(TextureType.CAULDRON_TOP,TextureType.CAULDRON_BOTTOM,TextureType.CAULDRON),
    NETHERRACK(TextureType.NETHERRACK),
    SOUL_SAND(TextureType.SOUL_SAND),
    GLOWSTONE(TextureType.GLOWSTONE),
    STICKY_PISTON(TextureType.STICKY_PISTON_HEAD,TextureType.DROPPER_SIDE,TextureType.PISTON),
    PISTON(TextureType.PISTON_HEAD,TextureType.DROPPER_SIDE,TextureType.PISTON),
    MELON_STEM(TextureType.MELON_STEM),
    MELON_STEM_CONNECTED(TextureType.MELON_STEM_CONNECTED),
    GLASS_PANE(TextureType.GLASS_PANE_SIDE,TextureType.GLASS_PANE_SIDE,TextureType.GLASS,TextureType.GLASS,TextureType.GLASS_PANE_SIDE),
    JUNGLE_LOG(TextureType.JUNGLE_LOG),
    BREWING_STAND(TextureType.BREWING_STAND,TextureType.BREWING_STAND,TextureType.BREWING_STAND_BASE),
    END_PORTAL(TextureType.END_EYE_PORTAL,TextureType.END_STONE,TextureType.END_EYE_PORTAL_SIDE),
    LAPIS_LAZULI_ORE(TextureType.LAPIS_LAZULI_ORE),
    BROWN_WOOL(TextureType.BROWN_WOOL),
    TRACK_TURN(TextureType.TRACK_TURN),
    TRACK(TextureType.TRACK),
    BLACK_WOOL(TextureType.BLACK_WOOL),
    GRAY_WOOL(TextureType.GRAY_WOOL),
    RED_WOOL(TextureType.RED_WOOL),
    PINK_WOOL(TextureType.PINK_WOOL),
    YELLOW_WOOL(TextureType.YELLOW_WOOL),
    POWERED_TRACK(TextureType.POWERED_RAIL_OFF),
    POWERED_TRACK_ON(TextureType.POWERED_RAIL),
    REDSTONE(TextureType.REDSTONE_4),
    REDSTONE_2(TextureType.REDSTONE_2),
    ENCHANTMENT_TABLE(TextureType.ENCHANTMENT_TABLE_TOP,TextureType.ENCHANTMENT_TABLE_BOTTOM,TextureType.ENCHANTMENT_TABLE),
    COCOA_BEAN(TextureType.COCOA_BEAN),
    COCOA_BEAN_0(TextureType.COCOA_BEAN_0),
    COCOA_BEAN_1(TextureType.COCOA_BEAN_1),
    EMERALD_ORE(TextureType.EMERALD_ORE),
    TRIPWIRE_HOOK(TextureType.TRIPWIRE_HOOK),
    TRIPWIRE(TextureType.TRIPWIRE),
    END_STONE(TextureType.END_STONE),
    SANDSTONE(TextureType.SANDSTONE_TOP,TextureType.SANDSTONE_BOTTOM,TextureType.SANDSTONE),
    BLUE_WOOL(TextureType.BLUE_WOOL),
    LIGHT_BLUE_WOOL(TextureType.LIGHT_BLUE_WOOL),
    COMMAND_BLOCK(TextureType.COMMAND_BLOCK_TOP,TextureType.COMMAND_BLOCK),
    PURPLE_WOOL(TextureType.PURPLE_WOOL),
    MAGENTA_WOOL(TextureType.MAGENTA_WOOL),
    DETECTOR_RAIL(TextureType.DETECTOR_RAIL),
    JUNGLE_LEAVES(TextureType.JUNGLE_LEAVES),
    JUNGLE_LEAVES2(TextureType.JUNGLE_LEAVES2),
    SPRUCE_PLANK(TextureType.SPRUCE_PLANK),
    JUNGLE_PLANK(TextureType.JUNGLE_PLANK),
    CARROT(TextureType.CARROT),
    CARROT_0(TextureType.CARROT_0),
    CARROT_1(TextureType.CARROT_1),
    CARROT_2(TextureType.CARROT_2),
    POTATO(TextureType.POTATO),
    POTATO_0(TextureType.CARROT_0),
    POTATO_1(TextureType.CARROT_1),
    POTATO_2(TextureType.CARROT_2),
    WATER(TextureType.WATER),
    CYAN_WOOL(TextureType.CYAN_WOOL),
    ORANGE_WOOL(TextureType.ORANGE_WOOL),
    REDSTONE_LAMP(TextureType.REDSTONE_LAMP),
    REDSTONE_LAMP_OFF(TextureType.REDSTONE_LAMP_OFF),
    CHISELED_STONE_BRICK(TextureType.CHISELED_STONE),
    BIRCH_PLANK(TextureType.BIRCH_PLANK),
    NETHER_BRICK(TextureType.NETHER_BRICK),
    LIGHT_GRAY_WOOL(TextureType.LIGHT_GRAY_WOOL),
    NETHER_WART(TextureType.NETHER_WART),
    NETHER_WART_0(TextureType.NETHER_WART0),
    NETHER_WART_1(TextureType.NETHER_WART1),
    CHISELED_DECORATED_SANDSTONE(TextureType.CHISELED_SANDSTONE_DECORATED),
    CHISELED_SANDSTONE(TextureType.CHISELED_SANDSTONE),
    LAVA(TextureType.LAVA),
    CRACKED_0(TextureType.BREAK_0),
    CRACKED_1(TextureType.BREAK_1),
    CRACKED_2(TextureType.BREAK_2),
    CRACKED_3(TextureType.BREAK_3),
    CRACKED_4(TextureType.BREAK_4),
    CRACKED_5(TextureType.BREAK_5),
    CRACKED_6(TextureType.BREAK_6),
    CRACKED_7(TextureType.BREAK_7),
    CRACKED_8(TextureType.BREAK_8),
    CRACKED_9(TextureType.BREAK_9),
    ;

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int FRONT = 2;
    public static final int RIGHT = 3;
    public static final int LEFT = 4;
    public static final int BACK = 5;

    TextureType sides[] = new TextureType[6];
    /*
        0   TOP
        1   BOTTOM
        2   FRONT
        3   RIGHT
        4   LEFT
        5   BACK
    */
    BlockType(TextureType allSides) {
        for (int i=0;i<6;i++) {
            sides[i]=allSides;
        }
    }
    BlockType(TextureType topbot,TextureType side) {
        sides[TOP]=sides[BOTTOM]=topbot;
        sides[FRONT]=sides[RIGHT]=sides[LEFT]=sides[BACK]=side;
    }
    BlockType(TextureType top,TextureType bottom,TextureType side) {
        sides[TOP]=top;
        sides[BOTTOM]=bottom;
        sides[FRONT]=sides[RIGHT]=sides[LEFT]=sides[BACK]=side;
    }
    BlockType(TextureType top,TextureType bottom,TextureType front,TextureType back,TextureType side) {
        sides[TOP]=top;
        sides[BOTTOM]=bottom;
        sides[FRONT]=front;
        sides[BACK]=back;
        sides[RIGHT]=sides[LEFT]=side;
    }
    BlockType(TextureType top,TextureType bottom,TextureType front,TextureType back,TextureType sideRight,TextureType sideLeft) {
        sides[TOP]=top;
        sides[BOTTOM]=bottom;
        sides[FRONT]=front;
        sides[BACK]=back;
        sides[RIGHT]=sideRight;
        sides[LEFT]=sideLeft;
    }
}
