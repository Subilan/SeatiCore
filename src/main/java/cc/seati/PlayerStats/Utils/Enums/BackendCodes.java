package cc.seati.PlayerStats.Utils.Enums;

public enum BackendCodes {
    OK(0),
    ParamError(1101),
    NotFound(1102),
    TargetNotExist(1103),
    AuthenticationFailed(1201),
    Forbidden(1202),
    InvalidToken(1203),
    BadToken(1204),
    DuplicatedUserRegistration(1205),
    DuplicatedMCIDBinding(1206),
    ServerError(1301),
    DatabaseError(1302),
    AliyunError(1303),
    OperationNotApplied(1304);

    private final int code;

    BackendCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
