import java.security.Permission;
public class MySecurityManager extends SecurityManager{

    // 检查所有权限
    @Override
    public void checkPermission(Permission perm) {
        // 先放开所有权限
        // super.checkPermission(perm);
    }

    // 检测程序是否可以执行文件
    @Override
    public void checkExec(String cmd) {
        // 什么抛出异常就是禁止什么权限
        throw new SecurityException("checkExec 权限异常"+ cmd);
    }

    // 检测程序是否可以读取文件
    @Override
    public void checkRead(String file) {
        // throw new SecurityException("checkRead 权限异常"+ file);
    }

    // 检测程序是否可以读取文件
    @Override
    public void checkWrite(String file) {
       // throw new SecurityException("checkWrite 权限异常"+ file);
    }

    // 检测程序是否可以删除文件
    @Override
    public void checkDelete(String file) {
       // throw new SecurityException("checkDelete 权限异常"+ file);
    }

    // 检测程序是否可以连接网络
    @Override
    public void checkConnect(String host, int port) {
       // throw new SecurityException("checkConnect 权限异常"+ host + ":" + port);
    }
}