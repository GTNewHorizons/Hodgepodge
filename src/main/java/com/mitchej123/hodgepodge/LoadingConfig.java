package com.mitchej123.hodgepodge;

import java.io.File;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import com.mitchej123.hodgepodge.config.PollutionRecolorConfig;
import com.mitchej123.hodgepodge.util.BlockMatcher;

public class LoadingConfig {

    private Configuration config;

    public BlockMatcher standardBlocks = new BlockMatcher();
    public BlockMatcher liquidBlocks = new BlockMatcher();
    public BlockMatcher doublePlants = new BlockMatcher();
    public BlockMatcher crossedSquares = new BlockMatcher();
    public BlockMatcher blockVine = new BlockMatcher();

    public LoadingConfig(File file) {
        config = new Configuration(file);

        // spotless:off


        // Pollution :nauseous:

        // spotless:on
        if (config.hasChanged()) config.save();
    }

    public void postInitClient() {
        // need to be done later cause it initializes classes
        if (config == null) {
            System.err.println("Didn't load HODGEPODGE");
            config = new Configuration(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        }

        // spotless:off
        standardBlocks.updateClassList(PollutionRecolorConfig.renderStandardBlock);
        liquidBlocks.updateClassList(PollutionRecolorConfig.renderBlockLiquid);
        doublePlants.updateClassList(PollutionRecolorConfig.renderBlockDoublePlant);
        crossedSquares.updateClassList(PollutionRecolorConfig.renderCrossedSquares);
        blockVine.updateClassList(PollutionRecolorConfig.renderblockVine);

//        config.getCategory(Category.POLLUTION_RECOLOR.toString()).setComment(pollutionRecolorComment);
        // spotless:on
        if (config.hasChanged()) config.save();
    }

    /*
     * Defaults
     */

    private static final String pollutionRecolorComment = "Blocks that should be colored by pollution. \n"
            + "\tGrouped by the render type. \n"
            + "\tFormat: [BlockClass]:[colortype] \n"
            + "\tValid types: GRASS, LEAVES, FLOWER, LIQUID \n"
            + "\tAdd [-] first to blacklist.";
}
