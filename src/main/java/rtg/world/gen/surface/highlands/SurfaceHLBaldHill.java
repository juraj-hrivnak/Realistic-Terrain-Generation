package rtg.world.gen.surface.highlands;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkPrimer;

import rtg.api.biome.BiomeConfig;
import rtg.util.CellNoise;
import rtg.util.CliffCalculator;
import rtg.util.OpenSimplexNoise;

public class SurfaceHLBaldHill extends SurfaceHLBase {

    public SurfaceHLBaldHill(BiomeConfig config, IBlockState top, IBlockState filler) {

        super(config, top, filler);
    }

    @Override
    public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int y, int depth, World world, Random rand, OpenSimplexNoise simplex, CellNoise cell, float[] noise, float river, BiomeGenBase[] base) {

        float c = CliffCalculator.calc(x, y, noise);
        boolean cliff = c > 1.4f ? true : false;
        Block b;

        for(int k = 255; k > -1; k--)
        {
            b = primer.getBlockState((y * 16 + x) * 256 + k).getBlock();
            if(b == Blocks.air)
            {
                depth = -1;
            }
            else if(b == Blocks.stone)
            {
                depth++;

                if(cliff)
                {
                    if(depth > -1 && depth < 2)
                    {
                        if (rand.nextInt(3) == 0) {

                            primer.setBlockState((y * 16 + x) * 256 + k, hcCobble(world, i, j, x, y, k));
                        }
                        else {

                            primer.setBlockState((y * 16 + x) * 256 + k, hcStone(world, i, j, x, y, k));
                        }
                    }
                    else if (depth < 10)
                    {
                        primer.setBlockState((y * 16 + x) * 256 + k, hcStone(world, i, j, x, y, k));
                    }
                }
                else
                {
                    if(depth == 0 && k > 61)
                    {
                        if (rand.nextInt(5) == 0) {
                            primer.setBlockState((y * 16 + x) * 256 + k, Blocks.grass.getDefaultState());
                        } else {
                            primer.setBlockState((y * 16 + x) * 256 + k, topBlock);
                        }
                    }
                    else if(depth < 4)
                    {
                        primer.setBlockState((y * 16 + x) * 256 + k, fillerBlock);
                    }
                }
            }
        }
    }
}