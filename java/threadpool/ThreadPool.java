package pool;

//�ӿڣ��̳�Runnable
public interface ThreadPool<Job extends  Runnable>{
	void execute(Job job);
	void shutdown();
	//���ӹ����߳���
	void addWorker(int nums);
	void removeWorker(int nums);
	//�õ����ڹ�����������
	int getJobSize();
}
