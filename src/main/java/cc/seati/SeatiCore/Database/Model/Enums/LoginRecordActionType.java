package cc.seati.SeatiCore.Database.Model.Enums;

public enum LoginRecordActionType {
    LOGIN(1),
    LOGOUT(0);

    public final int value;

    LoginRecordActionType(int value) {
        this.value = value;
    }

    public boolean isLogin() {
        return this.value == 1;
    }

    public boolean isLogout() {
        return this.value == 0;
    }

}
