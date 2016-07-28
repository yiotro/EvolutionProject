package yio.tro.evolution;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by ivan on 12.03.2016.
 */
public class PosMapYio {

    int width, height;
    float sectorSize;
    private ArrayList<PosMapObjectYio> matrix[][];
    RectangleYio mapPos;


    public PosMapYio(RectangleYio mapPos, double sectorSize) {
        this.mapPos = mapPos;
        this.sectorSize = (float) sectorSize;
        width = (int)(mapPos.width / sectorSize) + 1;
        height = (int)(mapPos.height / sectorSize) + 1;
        matrix = new ArrayList[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                matrix[i][j] = new ArrayList<>();
            }
        }
    }


    public void updateObjectPos(PosMapObjectYio objectYio) {
        updateCurrentIndexPoint(objectYio);
        if (!indexPointsAreEqual(objectYio.indexPoint, objectYio.writtenIndexPoint)) {
            removeObjectFromSector(objectYio, objectYio.writtenIndexPoint);
            addObjectToSector(objectYio, objectYio.indexPoint);
            objectYio.writtenIndexPoint.setBy(objectYio.indexPoint);
        }
    }


    private void updateWrittenIndexPoint(PosMapObjectYio objectYio) {
        transformCoorToIndex(objectYio.x, objectYio.y, objectYio.writtenIndexPoint);
    }


    private void updateCurrentIndexPoint(PosMapObjectYio objectYio) {
        transformCoorToIndex(objectYio.x, objectYio.y, objectYio.indexPoint);
    }


    public void removeObject(PosMapObjectYio objectYio) {
        removeObjectFromSector(objectYio, objectYio.writtenIndexPoint);
    }


    private void removeObjectFromSector(PosMapObjectYio objectYio, PMCoor sectorIndex) {
        ListIterator iterator = matrix[sectorIndex.x][sectorIndex.y].listIterator();
        while (iterator.hasNext()) {
            PosMapObjectYio posMapObjectYio = (PosMapObjectYio) iterator.next();
            if (posMapObjectYio == objectYio) {
                iterator.remove();
                return;
            }
        }
    }


    private void addObjectToSector(PosMapObjectYio objectYio, PMCoor sectorIndex) {
        ListIterator iterator = matrix[sectorIndex.x][sectorIndex.y].listIterator();
        iterator.add(objectYio);
    }


    private boolean indexPointsAreEqual(PMCoor p1, PMCoor p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }


    public void addObject(PosMapObjectYio object) {
        updateWrittenIndexPoint(object);
        addObjectToSector(object, object.writtenIndexPoint);
    }


    public void transformCoorToIndex(double x, double y, PMCoor indexPoint) {
        indexPoint.x = (int)((x - mapPos.x) / sectorSize);
        indexPoint.y = (int)((y - mapPos.y) / sectorSize);
    }


    private boolean isCoorInside(int i, int j) {
        return i >= 0 && i < width && j >= 0 && j < height;
    }


    public void clear() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                matrix[i][j].clear();
            }
        }
    }


    public ArrayList<PosMapObjectYio> getSectorByPos(double x, double y) {
        int index_x = (int)((x - mapPos.x) / sectorSize);
        int index_y = (int)((y - mapPos.y) / sectorSize);
        return getSector(index_x, index_y);
    }


    public ArrayList<PosMapObjectYio> getSector(int i, int j) {
        if (!isCoorInside(i, j)) return null;
        return matrix[i][j];
    }
}
