package pool;

import java.sql.Connection;
import java.util.LinkedList;

public class ConnectPool {
	private LinkedList<Connection> pool = new LinkedList<Connection>();
	public ConnectPool(int initSize)
	{
		if(initSize>0)
		{
			for (int i = 0; i < initSize; i++) {
				pool.addLast(ConnectDriver.createConnection());
			}
			
		}
	}
	public void releaseConnection(Connection connection)
	{
		if(connection!=null)
		{
			synchronized (pool) {
				pool.addLast(connection);
				pool.notifyAll();
			}
		}
	}
	
	public Connection fetchConnection(long mills) throws InterruptedException {
		synchronized (pool) {
			if(mills<0){
				while (pool.isEmpty()) {
				
						pool.wait();					
				}
				
				return pool.removeFirst();
			}
			else{
				long future=System.currentTimeMillis()+mills;
				long remaining = mills;
				while (pool.isEmpty()&&remaining>0) {
					 
						pool.wait(remaining);
						remaining= future- System.currentTimeMillis();
				}
				Connection result = null;
				if(!pool.isEmpty())
				{
					result= pool.removeFirst();
				}
				
				return result;
			}
		}
		
	}
}
