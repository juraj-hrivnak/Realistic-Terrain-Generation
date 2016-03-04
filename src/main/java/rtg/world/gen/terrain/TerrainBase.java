package rtg.world.gen.terrain;

import rtg.config.rtg.ConfigRTG;
import rtg.util.CellNoise;
import rtg.util.OpenSimplexNoise;

public class TerrainBase 
{
    protected float base; // added as most terrains have this;
    protected final float minOceanFloor; // The lowest Y coord an ocean floor is allowed to be.
    public static final float minimumOceanFloor = 30f; // The lowest Y coord an ocean floor is allowed to be.
    protected final float groundNoiseAmplitudeHills;
    protected float groundNoise;
    protected final float groundVariation;
    protected final float rollingHillsMaxHeight;
    
	public TerrainBase(){
        this(68f);// default to marginally above sea level;
	}

    public TerrainBase(float base) {
        this.base = base;
        this.minOceanFloor = minimumOceanFloor;
        this.groundVariation = 2f;
        this.groundNoise = this.base;
        this.groundNoiseAmplitudeHills = 6f;
        this.rollingHillsMaxHeight = 80f;
    }

    public static final float above(float limited, float limit) {
        if (limited>limit) {
            return limited-limit;
        }
        return 0f;
    }

    public static final float stretched(float toStretch) {
        if (toStretch > 0) {
            return (float)Math.sqrt(toStretch);
        }
        //(else)
        return (-1f)*(float)Math.sqrt((-1f)*toStretch);
    }

    public static final float unsignedPower(float number, float power) {
        if (number > 0) {
            return (float)Math.pow(number,power);
        }
        //(else)
        return (-1f)*(float)Math.pow((-1f)*number,power);
    }
    
	public float generateNoise(OpenSimplexNoise simplex, CellNoise cell, int x, int y, float border, float river)
	{
		return 70f;
	}

    public static float hills(int x, int y, float hillStrength, OpenSimplexNoise simplex, float river) {

        float m = simplex.noise2(x / 200f, y / 200f) * hillStrength * river;
        m *= m / ((hillStrength * 0.1f) + hillStrength);

        float sm = simplex.noise2(x / hillStrength, y / hillStrength) * 8f;
        sm *= m / 20f > 3.75f ? 3.75f : m / 20f;
        m += sm;

        return m;
    }

    public static float groundNoise(int x, int y, float amplitude, OpenSimplexNoise simplex) {
        float h = simplex.noise2(x / 49f, y / 49f) * amplitude;
        h += simplex.noise2(x / 23f, y / 23f) * amplitude/2;
        return h;
    }
    
    public static float getTerrainBase()
    {
        return 68f;
    }
    
    public static float terrainCanyon(int x, int y, OpenSimplexNoise simplex, float river, float[] height, float border, float strength, int heightLength, boolean booRiver)
    {
        //float b = simplex.noise2(x / cWidth, y / cWidth) * cHeigth * river;
        //b *= b / cStrength;
        river *= 1.3f;
        river = river > 1f ? 1f : river;
        float r = simplex.noise2(x / 100f, y / 100f) * 50f;
        r = r < -7.4f ? -7.4f : r > 7.4f ? 7.4f : r;
        float b = (17f + r) * river;
        
        float hn = simplex.noise2(x / 12f, y / 12f) * 0.5f;
        float sb = 0f;
        if(b > 0f)
        {
            sb = b;
            sb = sb < 0f ? 0f : sb > 7f ? 7f : sb;
            sb = hn * sb;
        }
        b += sb;

        float cTotal = 0f;
        float cTemp = 0f;
        
        for(int i = 0; i < heightLength; i += 2)
        {
            cTemp = 0;
            if(b > height[i] && border > 0.6f + (height[i] * 0.015f) + hn * 0.2f)
            {
                cTemp = b > height[i] + height[i + 1] ? height[i + 1] : b - height[i];
                cTemp *= strength;
            }
            cTotal += cTemp;
        }
        
        
        float bn = 0f;
        if(booRiver)
        {
            if(b < 5f)
            {
                bn = 5f - b;
                for(int i = 0; i < 3; i++)
                {
                    bn *= bn / 4.5f;
                }
            }
        }
        else if(b < 5f)
        {
            bn = (simplex.noise2(x / 7f, y / 7f) * 1.3f + simplex.noise2(x / 15f, y / 15f) * 2f) * (5f - b) * 0.2f;
        }
        
        b += cTotal - bn;
        
        return getTerrainBase() + b;
    }
    
    public static float terrainDunes(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river)
    {
        float st = (simplex.noise2(x / 160f, y / 160f) + 0.38f) * (ConfigRTG.duneHeight + 23f);
        st = st < 0.2f ? 0.2f : st;
        
        float h = simplex.noise2(x / 60f, y / 60f) * st * 2f;
        h = h > 0f ? -h : h;
        h += st;
        h *= h / 50f;
        h += st;
        
        if(h < 10f)
        {
            float d = (h - 10f) / 2f;
            d = d > 4f ? 4f : d;
            h += cell.noise(x / 25D, y / 25D, 1D) * d;
            h += simplex.noise2(x / 30f, y / 30f) * d;
            h += simplex.noise2(x / 14f, y / 14f) * d * 0.5f;
        }
        
        return 70f + (h * river);
    }
    
    public static float terrainDuneValley(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river, float valley)
    {
        float h = (simplex.noise2(x / valley, y / valley) + 0.25f) * 65f * river;
        h = h < 1f ? 1f : h;
        
        float r = cell.noise(x / 50D, y / 50D, 1D) * h * 2;
        h += r;
    
        h += simplex.noise2(x / 40f, y / 40f) * 8;
        h += simplex.noise2(x / 14f, y / 14f) * 2;
        
        return 70f + h;
    }
    
    public static float terrainFlatLakes(int x, int y, OpenSimplexNoise simplex, float river)
    {
        float h = simplex.noise2(x / 300f, y / 300f) * 40f * river;
        h = h > 3f ? 3f : h; 
        h += simplex.noise2(x / 50f, y / 50f) * (12f - h) * 0.4f;
        h += simplex.noise2(x / 15f, y / 15f) * (12f - h) * 0.15f;
        
        return 62f + h;
    }
    
    public static float terrainGrasslandFlats(int x, int y, OpenSimplexNoise simplex, float river)
    {
        float h = simplex.noise2(x / 100f, y / 100f) * 7;
        h += simplex.noise2(x / 20f, y / 20f) * 2;
        
        float m = simplex.noise2(x / 180f, y / 180f) * 70f * river;
        m *= m / 40f;
        
        float sm = simplex.noise2(x / 30f, y / 30f) * 8f;
        sm *= m / 20f > 3.75f ? 3.75f : m / 20f;
        m += sm;
        
        float l = simplex.noise2(x / 260f, y / 260f) * 38f;
        l *= l / 25f;
        l = l < -8f ? -8f : l;
        
        return 68f + h + m - l;
    }
    
    public static float terrainGrasslandHills(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river, float vWidth, float vHeight, float hWidth, float hHeight, float lWidth, float lHeight, float bHeight)
    {
        float h = simplex.noise2(x / vWidth, y / vWidth) * vHeight * river;
        h += simplex.noise2(x / 20f, y / 20f) * 2;
        
        float m = simplex.noise2(x / hWidth, y / hWidth) * hHeight * river;
        m *= m / 40f;
        
        float sm = simplex.noise2(x / 30f, y / 30f) * 8f;
        sm *= m / 20f > 3.75f ? 3.75f : m / 20f;
        m += sm;
        
        float cm = cell.noise(x / 25D, y / 25D, 1D) * 12f;
        cm *= m / 20f > 3.75f ? 3.75f : m / 20f;
        m += cm;
        
        float l = simplex.noise2(x / lWidth, y / lWidth) * lHeight;
        l *= l / 25f;
        l = l < 8f ? 8f : l;
        
        h += simplex.noise2(x / 12f, y / 12f) * 3f;
        h += simplex.noise2(x / 5f, y / 5f) * 1.5f;
        
        return bHeight + h + m - l;
    }
    
    public static float terrainGrasslandMountains(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river)
    {
        float h = simplex.noise2(x / 100f, y / 100f) * 7;
        h += simplex.noise2(x / 20f, y / 20f) * 2;
        
        float m = simplex.noise2(x / 230f, y / 230f) * 120f * river;
        m *= m / 35f;
        m = m > 70f ? 70f + (m - 70f) / 2.5f : m;
        
        float c = cell.noise(x / 30f, y / 30f, 1D) * (m * 0.30f);
        
        float sm = simplex.noise2(x / 30f, y / 30f) * 8f + simplex.noise2(x / 8f, y / 8f);
        sm *= m / 20f > 2.5f ? 2.5f : m / 20f;
        m += sm;
        
        m += c;
        
        float l = simplex.noise2(x / 260f, y / 260f) * 38f;
        l *= l / 25f;
        l = l < -8f ? -8f : l;
        
        return 68f + h + m - l;
    }
    
    public static float terrainHighland(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river, float start, float width, float height)
    {
        float h = simplex.noise2(x / width, y / width) * height * river;
        h = h < start ? start + ((h - start) / 4.5f) : h;
        
        if(h > 0f)
        {
            float st = h * 1.5f > 15f ? 15f : h * 1.5f;
            h += cell.noise(x / 70D, y / 70D, 1D) * st;
        }
        
        h += simplex.noise2(x / 20f, y / 20f) * 5f;
        h += simplex.noise2(x / 12f, y / 12f) * 3f;
        h += simplex.noise2(x / 5f, y / 5f) * 1.5f;
        
        return getTerrainBase() + h;
    }
    
    public static float terrainLonelyMountain(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river, float strength, float width, float lakeWidth, float lakeDepth, float terrainHeight)
    {
        float h = simplex.noise2(x / 20f, y / 20f) * 2;
        h += simplex.noise2(x / 7f, y / 7f) * 0.8f;
        
        float m = simplex.noise2(x / width, y / width) * strength * river;
        m *= m / 35f;
        m = m > 70f ? 70f + (m - 70f) / 2.5f : m;
        
        float st = m * 0.7f;
        st = st > 20f ? 20f : st;
        float c = cell.noise(x / 30f, y / 30f, 1D) * (5f + st);
        
        float sm = simplex.noise2(x / 30f, y / 30f) * 8f + simplex.noise2(x / 8f, y / 8f);
        sm *= (m + 10f) / 20f > 2.5f ? 2.5f : (m + 10f) / 20f;
        m += sm;
        
        m += c;
        
        float l = simplex.noise2(x / lakeWidth, y / lakeWidth) * lakeDepth;
        l *= l / 25f;
        l = l < -8f ? -8f : l;
        
        return terrainHeight + h + m - l;
    }
    
    public static float terrainMarsh(int x, int y, OpenSimplexNoise simplex)
    {
        float h = simplex.noise2(x / 130f, y / 130f) * 30f;
        
        h += simplex.noise2(x / 12f, y / 12f) * 2f;
        h += simplex.noise2(x / 18f, y / 18f) * 4f;
        
        h = h < 4f ? 0f : h - 4f;
        
        if(h == 0f)
        {
            h += simplex.noise2(x / 20f, y / 20f) + simplex.noise2(x / 5f, y / 5f);
        }
        
        return 62f + h;
    }
    
    public static float terrainMesa(int x, int y, OpenSimplexNoise simplex, float river, float border)
    {
        float b = simplex.noise2(x / 130f, y / 130f) * 50f * river;
        b *= b / 40f;
        
        float hn = simplex.noise2(x / 12f, y / 12f);
        
        float sb = 0f;
        if(b > 2f)
        {
            sb = (b - 2f) / 2f;
            sb = sb < 0f ? 0f : sb > 5.5f ? 5.5f : sb;
            sb = hn * sb;
        }
        b += sb;
        
        b = b < 0.1f ? 0.1f : b;
    
        float c1 = 0f;
        if(b > 1f)
        {
            c1 = b > 5.5f ? 4.5f : b - 1f;
            c1 *= 3;
        }
    
        float c2 = 0f;
        if(b > 5.5f && border > 0.95f + hn * 0.09f)
        {
            c2 = b > 6f ? 0.5f : b - 5.5f;
            c2 *= 35;
        }
        
        float bn = 0f;
        if(b < 7f)
        {
            float bnh = 5f - b;
            bn += simplex.noise2(x / 70f, y / 70f) * (bnh * 0.4f);
            bn += simplex.noise2(x / 20f, y / 20f) * (bnh * 0.3f);
        }
        
        float w = simplex.noise2(x / 80f, y / 80f) * 25f;
        w *= w / 25f;
        
        b += c1 + c2 + bn - w;
        
        return 74f + b;
    }
    
    public static float terrainMountain(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river)
    {
        float h = simplex.noise2(x / 300f, y / 300f) * 135f * river;
        h *= h / 32f;
        h = h > 150f ? 150f : h;

        if(h > 10f)
        {
            float d = (h - 10f) / 2f > 8f ? 8f : (h - 10f) / 2f;
            h += simplex.noise2(x / 35f, y / 35f) * d;
            h += simplex.noise2(x / 60f, y / 60f) * d * 0.5f;

            if(h > 35f)
            {
                float d2 = (h - 35f) / 1.5f > 30f ? 30f : (h - 35f) / 1.5f;
                h += cell.noise(x / 25D, y / 25D, 1D) * d2;
            }
        }
        
        h += simplex.noise2(x / 28f, y / 28f) * 4;
        h += simplex.noise2(x / 18f, y / 18f) * 2;
        h += simplex.noise2(x / 8f, y / 8f) * 2;
        
        return h + 67f;
    }
    
    public static float terrainMountainRiver(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river)
    {
        float h = simplex.noise2(x / 300f, y / 300f) * 135f * river;
        h *= h / 32f;
        h = h > 150f ? 150f : h;

        /*float bn = 0f;
        if(h < 1f)
        {
            bn = 1f - h;
            for(int i = 0; i < 3; i++)
            {
                bn *= bn * 1.25f;
            }
            
            bn = bn > 3f ? 3f : bn;
        }*/

        if(h < 10f)
        {
            h += simplex.noise2(x / 14f, y / 14f) * (10f - h) * 0.2f;
        }
        
        if(h > 10f)
        {
            float d = (h - 10f) / 2f > 8f ? 8f : (h - 10f) / 2f;
            h += simplex.noise2(x / 35f, y / 35f) * d;
            h += simplex.noise2(x / 60f, y / 60f) * d * 0.5f;

            if(h > 35f)
            {
                float d2 = (h - 35f) / 1.5f > 30f ? 30f : (h - 35f) / 1.5f;
                h += cell.noise(x / 25D, y / 25D, 1D) * d2;
            }
        }

        if(h > 2f)
        {
            float d = (h - 2f) / 2f > 4f ? 4f : (h - 2f) / 2f;
            h += simplex.noise2(x / 28f, y / 28f) * d;
            h += simplex.noise2(x / 18f, y / 18f) * (d / 2f);
            h += simplex.noise2(x / 8f, y / 8f) * (d / 2f);
        }
        
        return h + 67f;// - bn;
    }
    
    public static float terrainMountainSpikes(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river)
    {
        float b = (12f + (simplex.noise2(x / 300f, y / 300f) * 6f));
        float h = cell.noise(x / 200D, y / 200D, 1D) * b * river;
        h *= h * 1.5f;
        h = h > 155f ? 155f : h;
        
        if(h > 2f)
        {
            float d = (h - 2f) / 2f > 8f ? 8f : (h - 2f) / 2f;
            h += simplex.noise2(x / 30f, y / 30f) * d;
            h += simplex.noise2(x / 50f, y / 50f) * d * 0.5f;

            if(h > 35f)
            {
                float d2 = (h - 35f) / 1.5f > 30f ? 30f : (h - 35f) / 1.5f;
                h += cell.noise(x / 25D, y / 25D, 1D) * d2;
            }
        }
        
        h += simplex.noise2(x / 18f, y / 18f) * 3;
        h += simplex.noise2(x / 8f, y / 8f) * 2;
                
        return 45f + h + (b * 2);
    }
    
    public static float terrainOcean(int x, int y, OpenSimplexNoise simplex, float river, float averageFloor)
    {
        float h = simplex.noise2(x / 300f, y / 300f) * 40f * river;
        h = h > 3f ? 3f : h;
        h += simplex.noise2(x / 50f, y / 50f) * (12f - h) * 0.4f;
        h += simplex.noise2(x / 15f, y / 15f) * (12f - h) * 0.15f;

        float floNoise = averageFloor + h;
        floNoise = floNoise < minimumOceanFloor ? minimumOceanFloor : floNoise;

        return floNoise;
    }
    
    public static float terrainPolar(int x, int y, OpenSimplexNoise simplex, float river)
    {
        float st = (simplex.noise2(x / 160f, y / 160f) + 0.38f) * (ConfigRTG.duneHeight + 23f) * river;
        st = st < 0.2f ? 0.2f : st;
        
        float h = simplex.noise2(x / 60f, y / 60f) * st * 2f;
        h = h > 0f ? -h : h;
        h += st;
        h *= h / 50f;
        h += st;
        
        return 70f + h;
    }
    
    public static float terrainSwampMountain(int x, int y, OpenSimplexNoise simplex, CellNoise cell, float river, float width, float heigth)
    {
        float h = simplex.noise2(x / width, y / width) * heigth * river;
        h *= h / 32f;
        h = h > 150f ? 150f : h;

        if(h < 14f)
        {
            h += simplex.noise2(x / 25f, y / 25f) * (14f - h) * 0.8f;
        }
        
        if(h < 6)
        {
            h = 6f - ((6f - h) * 0.07f) + simplex.noise2(x / 20f, y / 20f) + simplex.noise2(x / 5f, y / 5f);
        }
        
        if(h > 10f)
        {
            float d = (h - 10f) / 2f > 8f ? 8f : (h - 10f) / 2f;
            h += simplex.noise2(x / 35f, y / 35f) * d;
            h += simplex.noise2(x / 60f, y / 60f) * d * 0.5f;

            if(h > 35f)
            {
                float d2 = (h - 35f) / 1.5f > 30f ? 30f : (h - 35f) / 1.5f;
                h += cell.noise(x / 25D, y / 25D, 1D) * d2;
            }
        }

        if(h > 2f)
        {
            float d = (h - 2f) / 2f > 4f ? 4f : (h - 2f) / 2f;
            h += simplex.noise2(x / 28f, y / 28f) * d;
            h += simplex.noise2(x / 18f, y / 18f) * (d / 2f);
            h += simplex.noise2(x / 8f, y / 8f) * (d / 2f);
        }
        
        return h + 56f;
    }
    
    public static float terrainSwampRiver(int x, int y, OpenSimplexNoise simplex, float river)
    {
        float h = simplex.noise2(x / 180f, y / 180f) * 40f * river;
        h *= h / 35f;
        
        if(h < 1f)
        {
            h = 1f;
        }
        
        if(h < 4f)
        {
            h += (simplex.noise2(x / 50f, y / 50f) + simplex.noise2(x / 15f, y / 15f)) * (4f - h);
        }
        
        return 60f + h;
    }
}
