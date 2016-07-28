package yio.tro.evolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ivan on 23.09.2015.
 */
public class AtlasLoader {

    String srcName, txtFileName;
    TextureRegion atlasRegion;
    boolean antialias;
    ArrayList<String> fileNames;
    ArrayList<Rect> imageSpecs;
    int rows;

    public AtlasLoader(String srcName, String txtFileName, boolean antialias) {
        this.srcName = srcName;
        this.txtFileName = txtFileName;
        this.antialias = antialias;
        loadEverything();
    }

    private AtlasLoader() {}

    private void loadEverything() {
        atlasRegion = GameView.loadTextureRegionByName(srcName, antialias);
        FileHandle fileHandle = Gdx.files.internal(txtFileName);
        String atlasStructure = fileHandle.readString();
        ArrayList<String> lines = new ArrayList<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(atlasStructure, "\n");
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (token.contains("rows=")) {
                String s = token.substring(5, token.length() - 1);
                rows = Integer.valueOf(s);
            }
            if (token.length() > 5 && !token.contains("compression=") && !token.contains("rows="))
                lines.add(token);
        }
        fileNames = new ArrayList<String>();
        imageSpecs = new ArrayList<Rect>();
        for (String line : lines) {
            int charPos = line.indexOf("#");
            String fileName = line.substring(0, charPos);
            fileNames.add(fileName);
            String sizeString = line.substring(charPos + 1, line.length() - 1);
            int array[] = getArrayFromString(sizeString, 4);
            Rect rect = new Rect(array[0], array[1], array[2], array[3]);
            imageSpecs.add(rect);
        }
    }

    int[] getArrayFromString(String str, int size) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, " ");
        int array[] = new int[size];
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            array[i] = Integer.valueOf(token);
            i++;
        }
        return array;
    }

    public TextureRegion getTexture(String fileName) {
        int index = fileNames.indexOf(fileName);
        TextureRegion result = new TextureRegion(atlasRegion, imageSpecs.get(index).x, imageSpecs.get(index).y, imageSpecs.get(index).width, imageSpecs.get(index).height);
        return result;
    }

}
