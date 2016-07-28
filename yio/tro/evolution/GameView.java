package yio.tro.evolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.evolution.factor_yio.FactorYio;
import yio.tro.evolution.models.*;

import java.util.ArrayList;

/**
 * Created by ivan on 05.08.14.
 */
public class GameView {

    YioGdxGame yioGdxGame;
    GameController gameController;
    TextureRegion backgroundRegion, backgroundLow, backgroundLowest;
    public FactorYio factorModel, diagramFactor;
    FrameBuffer frameBuffer;
    SpriteBatch batchMovable, batchSolid, batchCache;
    ShapeRenderer shapeRenderer;
    float cx, cy, dw, dh, tf, selSize, defSelSize, bezelsSize;
    public TextureRegion blackCircleTexture;
    TextureRegion animationTextureRegion;
    TextureRegion blackPixel, selectionPixel, selectionTexture;
    TextureRegion branchTexture, branchLow, branchLowest;
    TextureRegion bushTexture, bushLowRes, bushLowestRes;
    public static int BODY_TYPES = 2;
    public static int BODY_COLORS = 4;
    public static int SPOT_TYPES = 3;
    public static int SPOT_COLORS = 2;
    public Storage3xTexture bodies[][];
    public Storage3xTexture spots[][];
    public Storage3xTexture legs[];
    public Storage3xTexture cocoonTextureStorage, cheliceraTexture, tailTexture, mustacheTexture;
    public TextureRegion animalCorpseTexture, animalCorpseTextureLow;
    public TextureRegion diagramPixels[];
    public TextureRegion diagramBackgroundPixel;
    int segments, w, h;
    double zoomLevelOne, zoomLevelTwo;
    OrthographicCamera orthoCam, cacheCam;
    public Rect diagramPos;


    public GameView(YioGdxGame yioGdxGame) { //must be called after creation of GameController and MenuView
        this.yioGdxGame = yioGdxGame;
        gameController = yioGdxGame.gameController;
        factorModel = new FactorYio();
        diagramFactor = new FactorYio();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batchMovable = new SpriteBatch();
        batchSolid = yioGdxGame.batch;
        batchCache = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        createOrthoCam();
        cacheCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        cacheCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        cx = yioGdxGame.w / 2;
        cy = yioGdxGame.h / 2;
        segments = Gdx.graphics.getWidth() / 75;
        if (segments < 12) segments = 12;
        if (segments > 24) segments = 24;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        defSelSize = 0.03f * w;
        bezelsSize = 0.05f * w;
        zoomLevelOne = 0.7;
        zoomLevelTwo = 1.2;
        loadTextures();
    }


    void createOrthoCam() {
        orthoCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        orthoCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        updateCam();
    }


    private void loadAnimalTextures() {
        AtlasLoader atlasLoader = new AtlasLoader("files/ready/atlas_texture.png", "files/ready/atlas_structure.txt", false);
        animalCorpseTexture = atlasLoader.getTexture("corpse.png");
        animalCorpseTextureLow = atlasLoader.getTexture("corpse_low.png");

        cheliceraTexture = new Storage3xTexture(atlasLoader, "chelicera.png");
        mustacheTexture = new Storage3xTexture(atlasLoader, "mustache.png");
        tailTexture = new Storage3xTexture(atlasLoader, "tail.png");

        bodies = new Storage3xTexture[BODY_TYPES][BODY_COLORS];
        for (int i = 0; i < BODY_TYPES; i++) {
            for (int j = 0; j < BODY_COLORS; j++) {
                bodies[i][j] = new Storage3xTexture(atlasLoader, "body_" + i + "_" + j + ".png");
            }
        }

        spots = new Storage3xTexture[SPOT_TYPES][SPOT_COLORS];
        for (int i = 0; i < SPOT_TYPES; i++) {
            for (int j = 0; j < SPOT_COLORS; j++) {
                spots[i][j] = new Storage3xTexture(atlasLoader, "spots_" + i + "_" + j + ".png");
            }
        }

        legs = new Storage3xTexture[3];
        for (int i = 0; i < 3; i++) {
            legs[i] = new Storage3xTexture(atlasLoader, "legs" + (i + 1) + ".png");
        }
    }


    private void loadHerbTextures() {
        AtlasLoader atlasLoader = new AtlasLoader("files/ready/atlas_texture.png", "files/ready/atlas_structure.txt", false);
        branchTexture = atlasLoader.getTexture("branch1.png");
        branchLow = atlasLoader.getTexture("branch1_low.png");
        branchLowest = atlasLoader.getTexture("branch1_lowest.png");

        bushTexture = atlasLoader.getTexture("bush2.png");
        bushLowRes = atlasLoader.getTexture("bush2_low.png");
        bushLowestRes = atlasLoader.getTexture("bush2_lowest.png");

        cocoonTextureStorage = new Storage3xTexture(atlasLoader, "cocoon.png");
    }


    private void loadDiagramPixels() {
        diagramPixels = new TextureRegion[5];
        diagramPixels[0] = loadTextureRegionByName("files/pixels/pixel_green.png", false);
        diagramPixels[1] = loadTextureRegionByName("files/pixels/pixel_red.png", false);
        diagramPixels[2] = loadTextureRegionByName("files/pixels/pixel_blue.png", false);
        diagramPixels[3] = loadTextureRegionByName("files/pixels/pixel_cyan.png", false);
        diagramPixels[4] = loadTextureRegionByName("files/pixels/pixel_yellow.png", false);
//        diagramPixels[5] = loadTextureRegionByName("files/pixels/pixel_red.png", false);
//        diagramPixels[6] = loadTextureRegionByName("files/pixels/pixel_yellow.png", false);
        diagramBackgroundPixel = loadTextureRegionByName("files/pixels/pixel_diagram.png", false);
    }


    void loadTextures() {
        loadAnimalTextures();
        loadHerbTextures();
        loadDiagramPixels();
        backgroundRegion = loadTextureRegionByName("files/game_background.png", true);
        backgroundLow = loadTextureRegionByName("files/game_background_low.png", true);
        backgroundLowest = loadTextureRegionByName("files/game_background_lowest.png", false);
        blackCircleTexture = loadTextureRegionByName("files/black_circle.png", false);
        selectionPixel = loadTextureRegionByName("files/selection_pixel.png", false);
        blackPixel = loadTextureRegionByName("files/black_pixel.png", false);
        selectionTexture = loadTextureRegionByName("files/selection.png", true);
    }


    public static TextureRegion loadTextureRegionByName(String name, boolean antialias) {
        Texture texture = new Texture(Gdx.files.internal(name));
        if (antialias) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion region = new TextureRegion(texture);
        return region;
    }


    void updateCam() {
        orthoCam.update();
        batchMovable.setProjectionMatrix(orthoCam.combined);
        shapeRenderer.setProjectionMatrix(orthoCam.combined);
    }


    public void beginSpawnProcess() {
        factorModel.setDy(0);
        factorModel.beginSpawning(1, 1);
        updateAnimationTexture();
    }


    public void beginDestroyProcess() {
        if (yioGdxGame.gamePaused && factorModel.get() < 1) return;
        factorModel.setDy(0);
        factorModel.beginDestroying(1, 1.5);
        updateAnimationTexture();
    }


    void updateAnimationTexture() {
        frameBuffer.begin();
        batchSolid.begin();
        batchSolid.draw(blackPixel, 0, 0, w, h);
        batchSolid.end();
        renderInternals();
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        animationTextureRegion = new TextureRegion(texture);
        animationTextureRegion.flip(false, true);
    }


    boolean isPosInViewFrame(Point pos, float offset) {
        if (pos.x < gameController.frameX1 - offset) return false;
        if (pos.x > gameController.frameX2 + offset) return false;
        if (pos.y < gameController.frameY1 - offset) return false;
        if (pos.y > gameController.frameY2 + offset) return false;
        return true;
    }


    void renderInternals() {
        batchMovable.begin();
        TextureRegion currentBackground;
        if (gameController.trackerZoom < 0.8) currentBackground = backgroundRegion;
        else if (gameController.trackerZoom < 1.2) currentBackground = backgroundLow;
        else currentBackground = backgroundLowest;
        batchMovable.draw(currentBackground, 0, 0, 2 * w, 2 * h);
        renderModel();
        renderBezels();
        batchMovable.end();
        renderDiagram();
    }


    private void renderModel() {
        renderGrass(gameController.evolutionModel);
        renderCorpses();
        renderAnimals();
        renderHerbs();
        renderSelection();
    }


    void renderDiagram() {
        EvolutionModel evolModel = gameController.evolutionModel;
        if (!evolModel.isGenDiagramVisible()) return;
        if (diagramFactor.get() == 0) return;
        batchSolid.begin();
        Color c = batchSolid.getColor();
        batchSolid.setColor(c.r, c.g, c.b, diagramFactor.get());
        Color fontColor = YioGdxGame.debugFont.getColor();

        batchSolid.draw(diagramBackgroundPixel, diagramPos.x, diagramPos.y, diagramPos.width, 0.8f * diagramPos.height);
        float w = diagramPos.width;
        float h = diagramPos.height;
        float columnWidth = 0.04f * Gdx.graphics.getWidth();
        int genCount[] = evolModel.getGeneticSituation();
        float distanceBetweenColumns = (w - 2 * columnWidth) / (genCount.length - 1);
        float maxNumber = GameController.maxNumberFromArray(genCount);
        float columnHeight = 0.5f * h;
        for (int i = 0; i < genCount.length; i++) {
            setFontColorByIndex(i);
            float numberLineWidth = YioGdxGame.getTextWidth(YioGdxGame.debugFont, "" + genCount[i]);
            float columnX = diagramPos.x + columnWidth + distanceBetweenColumns * i;
            batchSolid.draw(blackPixel, columnX - numberLineWidth / 2 - 0.01f * w, diagramPos.y + 0.03f * h, numberLineWidth + 0.02f * w, 0.1f * h);
            YioGdxGame.debugFont.draw(batchSolid, "" + genCount[i], columnX - numberLineWidth / 2, diagramPos.y + 0.03f * h + YioGdxGame.debugFont.getLineHeight());

            float currentSize = (float) genCount[i] / maxNumber;
            currentSize *= columnHeight;
            batchSolid.draw(getPixelByIndex(i), columnX - columnWidth / 2, diagramPos.y + 0.15f * h, columnWidth, currentSize);
        }
        batchSolid.draw(blackPixel, diagramPos.x + 0.035f * w, diagramPos.y + 0.14f * h, 0.93f * w, 0.01f * h);

        YioGdxGame.debugFont.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a);
        batchSolid.setColor(c.r, c.g, c.b, c.a);
        batchSolid.end();
    }


    TextureRegion getPixelByIndex(int index) {
        while (index >= diagramPixels.length) index -= diagramPixels.length;
        return diagramPixels[index];
    }


    void setFontColorByIndex(int index) {
        BitmapFont font = YioGdxGame.debugFont;
        while (index >= diagramPixels.length) index -= diagramPixels.length;
        switch (index) {
            case 0:
                font.setColor(0.37f, 0.7f, 0.36f, diagramFactor.get());
                break;
            case 1:
                font.setColor(0.7f, 0.36f, 0.46f, diagramFactor.get());
                break;
            case 2:
                font.setColor(0.45f, 0.36f, 0.7f, diagramFactor.get());
                break;
            case 3:
                font.setColor(0.36f, 0.7f, 0.69f, diagramFactor.get());
                break;
            case 4:
                font.setColor(0.7f, 0.71f, 0.39f, diagramFactor.get());
                break;
        }
    }


    void renderBezels() {
        if (gameController.frameY1 < 0)
            batchMovable.draw(blackPixel, -bezelsSize, -bezelsSize, gameController.boundWidth + 2 * bezelsSize, bezelsSize); // bottom
        if (gameController.frameY2 > gameController.boundHeight)
            batchMovable.draw(blackPixel, -bezelsSize, gameController.boundHeight, gameController.boundWidth + 2 * bezelsSize, bezelsSize); // top
        if (gameController.frameX1 < 0)
            batchMovable.draw(blackPixel, -bezelsSize, 0, bezelsSize, gameController.boundHeight); // left
        if (gameController.frameX2 > gameController.boundWidth)
            batchMovable.draw(blackPixel, gameController.boundWidth, 0, bezelsSize, gameController.boundHeight); // right
    }


    void renderSelection() {
        EvolutionSubject selectedSubject = gameController.evolutionModel.getSelectedSubject();
        if (selectedSubject == null) return;
        selSize = gameController.evolutionModel.getSelectionFactor().get() * defSelSize;
        batchMovable.draw(selectionTexture, (float) selectedSubject.x - selSize, (float) selectedSubject.y - selSize, 2 * selSize, 2 * selSize);
        if (selectedSubject instanceof MatrixAnimal) {
            GameObject target = ((MatrixAnimal) selectedSubject).artificialIntelligence.getTarget();
            if (target != null)
                drawFromCenter(batchMovable, blackCircleTexture, (float) target.x, (float) target.y, 0.1f * selSize);
        }
    }


    private void renderPosMap(PosMapYio posMapYio) {
        Color c = batchMovable.getColor();
        batchMovable.setColor(c.r, c.g, c.b, 0.3f);
        for (int i = 0; i < posMapYio.width; i++) {
            for (int j = 0; j < posMapYio.height; j++) {
                float sx = posMapYio.mapPos.x + i * posMapYio.sectorSize;
                float sy = posMapYio.mapPos.y + j * posMapYio.sectorSize;
                float thickness = 0.005f * w;
                drawLine(sx, sy - thickness / 2, sx + posMapYio.sectorSize, sy - thickness / 2, thickness, batchMovable, blackPixel);
                drawLine(sx - thickness / 2, sy, sx - thickness / 2, sy + posMapYio.sectorSize, thickness, batchMovable, blackPixel);
                for (int k = 0; k < posMapYio.getSector(i, j).size(); k++) {
                    if (k > 2) break;
                    batchMovable.draw(blackPixel, sx, sy, posMapYio.sectorSize, posMapYio.sectorSize);
                }
            }
        }
        batchMovable.setColor(c.r, c.g, c.b, c.a);
    }


    void renderGrass(EvolutionModel evolutionModel) {
        Grass grass;
        float currentWidth;
        TextureRegion currentBranchTexture = branchTexture;
        if (gameController.trackerZoom > zoomLevelOne) currentBranchTexture = branchLow;
        if (gameController.trackerZoom > zoomLevelTwo) currentBranchTexture = branchLowest;

        Color c = batchMovable.getColor();
        for (int i = evolutionModel.grass.size() - 1; i >= 0; i--) {
            grass = evolutionModel.grass.get(i);
            currentWidth = grass.length;
            if (gameController.trackerZoom > zoomLevelOne || isInsideViewFrame(grass.x, grass.y, currentWidth)) {
                batchMovable.setColor(c.r, c.g, c.b, grass.factorModel.get());
                batchMovable.draw(currentBranchTexture, (float) grass.herb1.x - 0.5f * currentWidth, (float) grass.herb1.y - 0.5f * currentWidth, 0f, 0.5f * currentWidth, currentWidth, currentWidth, 1, 1, 57.29f * grass.angle);
            }
        }
        batchMovable.setColor(c.r, c.g, c.b, 1);
    }


    void renderCorpses() {
        for (Corpse corpse : gameController.evolutionModel.getCorpses()) {
            drawFromCenterRotated(batchMovable, animalCorpseTexture, corpse.x, corpse.y, corpse.getViewRadius(), corpse.rotateAngle);
        }
    }


    void renderHerbs() {
        TextureRegion currBushTexture = bushTexture;
        if (gameController.trackerZoom > zoomLevelOne) currBushTexture = bushLowRes;
        if (gameController.trackerZoom > zoomLevelTwo) currBushTexture = bushLowestRes;
        TextureRegion cocoonTexture = cocoonTextureStorage.getTexture(gameController.trackerZoom, zoomLevelOne, zoomLevelTwo);

        int x1 = (int) (gameController.frameX1 / gameController.evolutionModel.cacheHerbsCellSize) - 1;
        int x2 = (int) (gameController.frameX2 / gameController.evolutionModel.cacheHerbsCellSize);
        int y1 = (int) (gameController.frameY1 / gameController.evolutionModel.cacheHerbsCellSize) - 1;
        int y2 = (int) (gameController.frameY2 / gameController.evolutionModel.cacheHerbsCellSize);
        ArrayList<PosMapObjectYio> herbsInSector;
        Herb herb;
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                herbsInSector = gameController.evolutionModel.posMapHerbs.getSector(i, j);
                if (herbsInSector == null) continue;
                for (int k = herbsInSector.size() - 1; k >= 0; k--) {
                    herb = (Herb) herbsInSector.get(k);
                    if (!herb.isCocoon()) {
                        batchMovable.draw(currBushTexture, (float) herb.x - herb.radius, (float) herb.y - herb.radius, herb.diameter, herb.diameter);
                    } else {
                        drawFromCenter(batchMovable, cocoonTexture, herb.x, herb.y, herb.getCocoonRadius());
//                        batchMovable.draw(cocoonTexture, (float)herb.x - herb.radius, (float)herb.y - herb.radius, herb.diameter, herb.diameter);
                    }
                }
            }
        }
    }


    private TextureRegion getBodyTextureByAnimal(Animal animal) {
        return animal.getBodyTexture().getTexture(gameController.trackerZoom, zoomLevelOne, zoomLevelTwo);
    }


    void renderAnimals() {
        for (Animal animal : gameController.evolutionModel.getAnimals()) {
            if (gameController.trackerZoom > zoomLevelOne || isInsideViewFrame(animal.x, animal.y, animal.bodySize))
                renderSingleAnimal(animal);
        }
    }


    private TextureRegion getLegsTexture(int legState) {
        return legs[legState].getTexture(gameController.trackerZoom, zoomLevelOne, zoomLevelTwo);
    }


    private void renderSingleAnimal(Animal animal) {
        if (gameController.trackerZoom < zoomLevelTwo) {
            // legs
            drawFromCenterRotated(batchMovable, getLegsTexture(animal.legState), animal.x, animal.y, animal.getViewRadius(), animal.angle);

            // tail
            if (animal.hasTail()) {
                drawFromCenterRotated(batchMovable, tailTexture.getTexture(gameController.trackerZoom, zoomLevelOne, zoomLevelTwo), animal.x, animal.y, animal.getViewRadius(), animal.angle);
            }

            // nose
            if (animal.hasNose()) {
                Storage3xTexture nose = animal.getNoseTexture();
                drawFromCenterRotated(batchMovable, nose.getTexture(gameController.trackerZoom, zoomLevelOne, zoomLevelTwo), animal.x, animal.y, 1.5 * animal.getViewRadius(), animal.angle);
            }
        }

        // body
        TextureRegion currBodyTexture = getBodyTextureByAnimal(animal);
        drawFromCenterRotated(batchMovable, currBodyTexture, animal.x, animal.y, animal.getViewRadius(), animal.angle);

        if (gameController.trackerZoom < zoomLevelTwo) {
            //spots
            Storage3xTexture spotTexture = animal.getSpotTexture();
            if (spotTexture != null)
                drawFromCenterRotated(batchMovable, spotTexture.getTexture(gameController.trackerZoom, zoomLevelOne, zoomLevelTwo), animal.x, animal.y, animal.getViewRadius(), animal.angle);
        }
    }


    public static void drawFromCenter(Batch batch, TextureRegion textureRegion, double cx, double cy, double r) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) (2d * r), (float) (2d * r));
    }


    void drawFromCenterRotated(Batch batch, TextureRegion textureRegion, double cx, double cy, double r, double rotationAngle) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) r, (float) r, (float) (2d * r), (float) (2d * r), 1, 1, 57.29f * (float) rotationAngle);
    }


    public void render() {
        if (factorModel.get() < 0.01) {
            return;
        } else if (factorModel.get() < 0.99) {
            batchSolid.begin();
            float f = (float) factorModel.get();
            Color c = batchSolid.getColor();
            batchSolid.setColor(c.r, c.g, c.b, f);
            batchSolid.draw(animationTextureRegion, 0.2f * w * (1f - f), 0.2f * h * (1f - f), 0.6f * w + 0.4f * w * f, 0.6f * h + 0.4f * h * f);
            batchSolid.end();
            batchSolid.setColor(c.r, c.g, c.b, 1);
        } else {
            batchSolid.begin();
            batchSolid.draw(blackPixel, 0, 0, w, h);
            batchSolid.end();
            renderInternals();
        }
    }


    boolean isInsideViewFrame(double x, double y, double offset) {
        if (x + offset < gameController.frameX1) return false;
        if (x - offset > gameController.frameX2) return false;
        if (y + offset < gameController.frameY1) return false;
        if (y - offset > gameController.frameY2) return false;
        return true;
    }


    public void setDiagram(boolean visible) {
        if (visible) {
            diagramFactor.setValues(0, 0);
            diagramFactor.beginSpawning(1, 1);
            if (gameController.yioGdxGame.gamePaused) {
                diagramFactor.setValues(1, 0);
            }
        } else {
            diagramFactor.setValues(0, 0);
        }
    }


    void move() {
        if (gameController.evolutionModel.isGenDiagramVisible()) diagramFactor.move();
    }


    public static void drawLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion blackPixel) {
        spriteBatch.draw(blackPixel, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f, (float) YioGdxGame.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 / Math.PI * YioGdxGame.angle(x1, y1, x2, y2)));
    }


    public boolean coversAllScreen() {
        return factorModel.get() > 0.99;
    }


    boolean isInMotion() {
        return factorModel.get() > 0.01 && factorModel.get() < 0.99;
    }
}
