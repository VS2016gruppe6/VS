package vsue.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VSRemoteObjectManager {

	static private final VSRemoteObjectManager remoteObjectManager = new VSRemoteObjectManager(); // einzelne
																									// class
	static private Map<Integer, Remote> objectMap = new HashMap<>();
	static private Map<Remote, Remote> proxyMap = new HashMap<>();
	static private int objectID = 0;
	final private Object _lock = new Object();
	private ServerSocket socket;
	static boolean flag = false;
	static private Socket _socket;

	public ServerSocket getSocket() {
		return socket;
	}

	public VSRemoteObjectManager() {

		try {
			socket = new ServerSocket(0); // 0:port num,
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static VSRemoteObjectManager getInstance() {

		return remoteObjectManager;
	}

	public void generierenThread() {

		Thread _thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (;;) { // ?
					try {
						// System.out.println("generierenThread");
						_socket = socket.accept();
						Thread vsserver = new Thread(new VSServer(_socket));
						vsserver.start();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		_thread.start();

		return;
	}

	public Remote exportObject(Remote object) throws IOException {
		synchronized (_lock) {
			objectID++;
		}

		VSRemoteReference reference = null; // reference: host,port, ObjectID
		ClassLoader ldr = object.getClass().getClassLoader();
		Class<?>[] intfs = object.getClass().getInterfaces();
		generierenThread();
		try {
			reference = new VSRemoteReference(Inet4Address.getLocalHost().getHostAddress(), socket.getLocalPort(),
					objectID);
			// System.out.println("port of socket is " + socket.getLocalPort());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		VSInvocationHandler handler = new VSInvocationHandler(reference);
		// Proxy-Erzeugung
		Remote proxy = (Remote) Proxy.newProxyInstance(ldr, intfs, handler);
		// find object according to objectID
		objectMap.put(objectID, object);
		proxyMap.put(object, proxy);
		return proxy;
	}

	public Remote getproxy(Remote o) {
		return proxyMap.get(o);
	}

	public Object invokeMethod(int objectID, String genericMethodName, Object[] args) throws Throwable {

		Remote object = objectMap.get(objectID);
		Class<?> c = object.getClass();
		do {
			for (Class<?> intf : c.getInterfaces()) {
				for (Method me : intf.getMethods()) {
					if (!me.toGenericString().equals(genericMethodName)) {
						continue;
					}
					try {
						Object o = me.invoke(object, args);
						if (o instanceof Remote) {         //instanceof: if o is a object from reomte,return remote-object, or return local object
							Object result = getproxy((Remote) o);
							return result;
						}
						return o;
					} catch (InvocationTargetException exc) {
						throw exc.getCause();
					} catch (IllegalAccessException | IllegalArgumentException exc) {
						throw new RemoteException("An error occured while invoking method.", exc);
					}
				}
			}
		} while ((c = c.getSuperclass()) != null);    //superclass exist
		throw new RemoteException("unable to invoke the methode in Impl, the methode is not found");
	}

}
