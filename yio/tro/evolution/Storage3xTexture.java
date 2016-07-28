package yio.tro.evolution;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by ivan on 25.09.2015.
 */
public class Storage3xTexture {

    TextureRegion normalTexture, lowTexture, lowestTexture;

    public Storage3xTexture() {
    }

    public Storage3xTexture(AtlasLoader atlasLoader, String fileName) {
        setTextures(atlasLoader, fileName);
    }

    public TextureRegion getNormal() {
        return normalTexture;
    }

    public void setNormalTexture(TextureRegion normalTexture) {
        this.normalTexture = normalTexture;
    }

    public TextureRegion getLow() {
        return lowTexture;
    }

    public void setLowTexture(TextureRegion lowTexture) {
        this.lowTexture = lowTexture;
    }

    public TextureRegion getLowest() {
        return lowestTexture;
    }

    public void setLowestTexture(TextureRegion lowestTexture) {
        this.lowestTexture = lowestTexture;
    }

    public TextureRegion getTexture(double zoom, double zoomLev1, double zoomLev2) {
        if (zoom < zoomLev1) return normalTexture;
        if (zoom < zoomLev2) return lowTexture;
        return lowestTexture;
    }

    public void setTextures(AtlasLoader atlasLoader, String fileName) {
        int index = fileName.indexOf(".");
        String name = fileName.substring(0, index);
        String ext = fileName.substring(index + 1, fileName.length());
        setNormalTexture(atlasLoader.getTexture(fileName));
        setLowTexture(atlasLoader.getTexture(name + "_low." + ext));
        setLowestTexture(atlasLoader.getTexture(name + "_lowest." + ext));
    }
}
