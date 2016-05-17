package teamrtg.rtg.world.biome.terrain;

import teamrtg.rtg.world.gen.ChunkProviderRTG;

public class TerrainLonelyMountain extends TerrainBase {
    private float width;
    private float strength;
    private float lakeDepth;
    private float lakeWidth;
    private float terrainHeight;

	/*
     * width = 230f
	 * strength = 120f
	 * lake = 50f;
	 *
	 * 230f, 120f, 50f
	 */

    public TerrainLonelyMountain(float mountainWidth, float mountainStrength, float depthLake) {
        this(mountainWidth, mountainStrength, depthLake, 260f, 68f);
    }

    public TerrainLonelyMountain(float mountainWidth, float mountainStrength, float depthLake, float widthLake, float height) {
        width = mountainWidth;
        strength = mountainStrength;
        lakeDepth = depthLake;
        lakeWidth = widthLake;
        terrainHeight = height;
    }

    @Override
    public float generateNoise(ChunkProviderRTG provider, int x, int y, float border, float river) {
        return terrainLonelyMountain(x, y, provider.simplex, provider.cell, river, strength, width, terrainHeight);
    }
}
