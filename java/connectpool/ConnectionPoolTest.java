package pool;

import java.lang.Thread.State;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPoolTest {
	static ConnectPool pool = new ConnectPool(10);
	static CountDownLatch start = new CountDownLatch(1);
	static CountDownLatch end;
	
	public static void main(String[] args) throws InterruptedException {
		int threadCnt=10;
		end = new CountDownLatch(threadCnt);
		int count =20;
		AtomicInteger got = new AtomicInteger();
		AtomicInteger notgot = new AtomicInteger();
		for(int i= 0 ;i < threadCnt ; i++)
		{
			Thread thread = new Thread(new ConnectionRunner(count, got, notgot),"CountDown");
			thread.start();
			
		}
		start.countDown();
		end.await();
		System.out.println("totla invoke:"+threadCnt*count);
		System.out.println("got connect:"+got);
		System.out.println("notgot:"+notgot);
	}
	
	static class ConnectionRunner implements Runnable{
		int count =20;
		AtomicInteger got = new AtomicInteger();
		AtomicInteger notgot = new AtomicInteger();
		 public ConnectionRunner(int count , AtomicInteger got ,AtomicInteger notgot) {
                 this.count= count;
                 this.got= got;
                 this.notgot= notgot;
		 }
		@Override
		public void run() {
           try {
        	   start.await();
		} catch (Exception e) {
			// TODO: handle exception
		}
           while(count>0)
           {
        	   
        	   Connection connection = null;
			try {
				connection = pool.fetchConnection(1000);
			
				 if (connection!=null) {
						try {
							connection.createStatement();
							 
							connection.commit();
							 
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							 
							pool.releaseConnection(connection);
							got.incrementAndGet();
						}
					}else {
						 
					   notgot.incrementAndGet();	
					}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally{
				count--;
				 
			}
			 
           }
           end.countDown();
		}
		
	}
}
