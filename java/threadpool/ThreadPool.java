package pool;

//接口，继承Runnable
public interface ThreadPool<Job extends  Runnable>{
	void execute(Job job);
	void shutdown();
	//增加工作线程数
	void addWorker(int nums);
	void removeWorker(int nums);
	//得到正在工作的任务数
	int getJobSize();
}
