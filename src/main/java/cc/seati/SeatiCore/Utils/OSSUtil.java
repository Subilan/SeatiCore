package cc.seati.SeatiCore.Utils;

public class OSSUtil {
    public static void doArchive() {
        CommonUtil.tryExec(() -> CommonUtil.waitFor(
                CommonUtil.runScriptAndWait(ConfigUtil.getOssArchiveScript()),
                ConfigUtil.getOssUploadTimeout()
        ));
    }

    public static void doBackup() {
        CommonUtil.tryExec(() -> CommonUtil.waitFor(
                CommonUtil.runScriptAndWait(ConfigUtil.getOssBackupScript()),
                ConfigUtil.getOssUploadTimeout()
        ));
    }
}
