package cc.seati.SeatiCore.Utils.Records;

public record PlayerPosition(double x, double y, double z, double lookX, double lookY, double lookZ) {
    public boolean isSimilarTo(PlayerPosition pos) {
        return x == pos.x || z == pos.z || lookX == pos.lookX || lookY == pos.lookY || lookZ == pos.lookZ;
    }
}
