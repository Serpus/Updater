package updater;

public class Helper {
    /**
     * Проверка введённой ветки
     *
     * @param branchName Имя ветки
     * @return Возвращает true, если ветка соответствует формату
     */
    public static boolean checkBranchFormat(final String branchName) {
        if (branchName.contains("hotfix-")) {
            String local = branchName.replace("hotfix-", "");
            return local.matches("\\d+.\\d+") || local.matches("\\d+.\\d+.\\d+") || local.matches("\\d+.\\d+.\\d+.\\d+");
        }
        if (branchName.contains("release-")) {
            String local = branchName.replace("release-", "");
            return local.matches("\\d+.\\d+") || local.matches("\\d+.\\d+.\\d+") || local.matches("\\d+.\\d+.\\d+.\\d+");
        }
        return false;
    }
}
