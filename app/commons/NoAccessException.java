package commons;

/**
 * Member: Freewind
 * Date: 12-10-29
 * Time: 下午9:51
 */
public class NoAccessException extends RuntimeException {

    public final String privilege;
    public final Object[] params;

    public NoAccessException(String privilege, Object... params) {
        this.privilege = privilege;
        this.params = params;
    }

}
