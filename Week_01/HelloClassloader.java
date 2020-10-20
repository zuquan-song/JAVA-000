import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

public class HelloClassloader extends ClassLoader {
	public static void main(String[] args) {
        try {
            Object obj = new HelloClassloader().findClassByFileName("Hello/Hello.xlass", "Hello").newInstance();
            Method method = obj.getClass().getMethod("hello");
            method.invoke(obj);
        } catch (InstantiationException | IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    private Class<?> findClassByFileName(String filePath, String className) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(filePath).toPath());
        bytes = this.decode(bytes);
        return defineClass(className, bytes, 0, bytes.length);
    }

    private byte[] decode(byte[] bytes) {
	    byte[] newBytes = new byte[bytes.length];
	    for(int i = 0; i < bytes.length; i ++) {
            newBytes[i] = (byte) ~bytes[i];
        }
        return newBytes;
    }
}