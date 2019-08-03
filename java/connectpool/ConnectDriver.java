package pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

public class ConnectDriver {
	static class ConnectionHandler implements InvocationHandler{

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if(method.getName().equals("commit"))
			{
		 
				TimeUnit.SECONDS.sleep(1);
			}
			return null; 
		}
		
	}
	public  static final Connection createConnection() {
		return (Connection) Proxy.newProxyInstance(ConnectDriver.class.getClassLoader(), new Class<?>[]{Connection.class}, new ConnectionHandler());
	}
}
