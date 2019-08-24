/**
*线程池需要考虑的问题：
1.设置线程池的最大最小值
2.线程数量需要动态更新，指标如何计算呢？可以通过线程的空闲率来计算
3.需要用到共享条件变量，因为线程池阻塞不阻塞取决于当前任务队列是否为空
4.线程池的增员和裁员，该谁干呢？答案就是在开辟一个线程作为管理者线程。
*/
/**
pthread_exit来结束线程是最安全的
*/

#define DEFAULT_TIME 10000
#define DEFAULT_THREAD_VARY 10
struct {
	void* (*function)(void*);//回调函数
	void *arg;//回调函数参数
}threadpool_task_t;

//线程池相关数据结构
struct threadpool_t
{
	pthread_mutex_t lock;//用于锁住本结构体
	pthread_mutex_t thread_counter;//记录忙状态的线程个数锁
	pthread_cond_t queue_not_full;//当任务队列满时，添加任务的线程阻塞，等待次条件变量
	pthread_cond_t queue_not_empty;//
	
	pthread_t* threads;//存放线程池中的线程id
	pthread_t  adjust_tid;//管理者线程，负责就是线程的动态添加还是卸载
	threadpool_task_t* task_queue;//线程池的任务队列
	
	int min_thr_num;//最小线程数
	int max_thr_num;//最da线程数
	int live_thr_num;//当前存活线程数
    int busy_thr_num;//
    int wait_exit_thr_num;
	
	int queue_front;
	int queue_rear;
	int queue_size;//表示当前任务队列的个数
	int queue_max_size;//队列的总容量
	
	int shutdown;//标志位
	
};

threadpool_t* threadpool_create(int min_thr_num,int max_thr_num,int queue_max_size)
{
	threadpool_t* pool=NULL;
	do{
		if((pool=(threadpool_t*)malloc(sizeof(threadpool_t)))==NULL)
		{
			break;
		}
		pool->min_thr_num=min_thr_num;
		pool->max_thr_num=max_thr_num;
		pool->queue_max_size=queue_max_size;
		pool->live_thr_num=min_thr_num;
		pool->busy_thr_num=0;
		pool->wait_exit_thr_num=0;
		pool->queue_front=0;
		pool->queue_rear=0;
		pool->queue_size=0;
		pool->queue_max_size=queue_max_size;
		pool->shutdown=0;
		
		//根据最大线程上限数，给工作线程数组开辟内存
		pool->threads=(pthread_t*)malloc(sizeof(pthread_t)*max_thr_num);
		if(pool->threads==NULL)
		{
			break;
		}
		memset(pool->threads,0,sizeofsizeof(pthread_t)*max_thr_num);
		
		//任务队列也需要进行开辟空间
		pool->task_queue=(threadpool_task_t*)malloc(sizeof(threadpool_task_t)*queue_max_size);
		if(pool->task_queue==NULL)
		{
			break;
		}
		memset(pool->task_queue,0,sizeofsizeof(threadpool_task_t)*queue_max_size);
		//初始化一个锁和条件变量
	if(pthread_mutex_init(&pool->lock,NULL)!=0||pthread_mutex_init(&pool->thread_counter,NULL)!=0||pthread_cond_init(&pool->queue_not_empty,NULL)!=0||pthread_cond_init(&pool->queue_not_full,NULL)!=0)
	{
		break;
	}
		
		
		//启动min_thr——num个线程
		for(int i=0;i<min_thr_num;i++)
		{
			pthread_create(&pool->threads[i],NULL,threadpool_thread,(void*) pool);
			
		}
		//创建管理者线程
		pthread_create(&pool->adjust_tid,NULL,adjust_thread,(void*) pool);
		
		return pool;
	}while(0);
	threadpool_free(pool);//创建失败后，要进行释放
}

//工作线程
void* threadpool_thread(void * threadpool)
{
	threadpool_t* pool=(threadpool_t*)pool;
	threadpool_task_t task;
	while(1)
	{
		//刚创建出线程，等待任务队列有任务，否则就进行阻塞等待任务队列里面有任务在进行环形
		pthread_mutex_lock(&(pool->lock));
		while(pool->queue_size==0&&!pool->shutdown)
		{
			pthread_cond_wait(&(pool->queue_not_empty),&(pool->lock));
		}
		//判断是否有要退出的线程
		if(pool->wait_exit_thr_num>0)
		{
			pool->wait_exit_thr_num--;
			//如果当前活动线程大于最小线程数，那么结束当前线程
			if(pool->live_thr_num>pool->min_thr_num)
			{
				pool->live_thr_num--;
				pthread_mutex_unlock(&(pool->lock));
				pthread_exit(NULL);
			}
		}
		
		//要关闭线程池里面的每个线程，自行退出处理
		if(pool->shutdown)
		{
			pthread_mutex_unlock(&(pool->lock));
			pthread_exit(NULL);
		}
		
		//从任务队列中获取任务,是一个出队操作
		task.function=pool->task_queue[pool->queue_front].function;
		task.arg=pool->task_queue[pool->queue_front].arg;
		//模拟环形队列
		pool->queue_front=(pool->queue_front+1)%pool->queue_max_size;
		pool->queue_size--;
		
		//通知，可以添加新任务了
		pthread_cond_broadcast(&(pool->queue_not_full));
		
		
		//任务取出后立马将线程池解锁
		pthread_mutex_unlock(&(pool->lock));
		
		//**执行任务
		
		//更新忙线程数目
		pthread_mutex_lock(&(pool->thread_counter));
		pool->busy_thr_num++;
		pthread_mutex_unlock(&(pool->thread_counter));
		
		
		(*(task.function))(task.arg);//执行回调函数
		
		//任务结束
		pthread_mutex_lock(&(pool->thread_counter));
		pool->busy_thr_num--;
		pthread_mutex_unlock(&(pool->thread_counter));
		
	}
	pthread_exit(NULL);
}

int threadpool_add(threadpool_t * pool,void*(*function)(void*arg),void*arg)
{
	pthread_mutex_lock(&(pool->lock));
	while(pool->queue_size==pool->queue_max_size&&!pool->shutdown)
	{ 
		pthread_cond_wait(&(pool->queue_not_full),&(pool->lock));
	}
		if(pool->shutdown)
		{
			pthread_mutex_unlock(&(pool->lock));
		}
		
		//清空工作线程 调用的回调函数的参数
		if(pool->task_queue[pool->queue_rear].arg！=NULL)
		{
			free(pool->task_queue[pool->queue_rear].arg);
			pool->task_queue[pool->queue_rear].arg=NULL;
		}
		
		/*添加任务到任务队列中*/
		pool->task_queue[pool->queue_rear].function=function;
		pool->task_queue[pool->queue_rear].arg=arg;
		pool->queue_rear=(pool->queue_rear+1)%pool->queue_max_size;
		
		pthread_cond_signal(&(pool->queue_not_empty));
		pthread_mutex_unlock(&(pool->lock));
		
	return 0;
}


void*adjust_thread(void* threadpool)
{
	int i=0;
	threadpool_t* pool=(threadpool_t*)pool;
	
	while(!pool->shutdown)
	{
		sleep(DEFAULT_TIME);//定时对线程池管理
		
		pthread_mutext_lock(&(pool->lock));
		int queue_size=pool->queue_size;
		int live_thr_num=pool->live_thr_num;
		pthread_mutext_unlock(&([pool->lock));
		
		
		pthread_mutext_lock(&(pool->thread_counter));
		 
		int busy_thr_num=pool->live_thr_num;
		pthread_mutext_unlock(&(pool->thread_counter));
		
		
		//当前任务个数大于线程数，而且存货线程数小于最大个数
		if(queue_size>=MIN_WAIT_TASK_NUM&&live_thr_num<pool->max_thr_num)
		{
			   pthread_mutex_lock(&(pool->lock));
			   int add=0;
			   //一次增加default_thread个线程
			   for(int i=0;i<pool->max_thr_num&&add<DEFAULT_THREAD_VARY&&pool->live_thr_num<pool->max_thr_num;i++)
			   {
				   if(pool->threads[i]==0||!is_thread_alive(pool->threads[i])
				   {
					   pthread_create(&(pool->threads[i]),NULL,threadpool_thread,(void*)pool);
					   add++;
				   }
			   }
			   pthread_mutex_unlock(&(pool->lock));
		}
		
		//卸载多余线程
		if(busy_thr_num*2<live_thr_num&& live_thr_num>pool->min_thr_num)
		{
			pthread_mutex_lock(&(pool->lock));
			pool->wait_exit_thr_num=DEFAULT_THREAD_VARY;
			pthread_mutex_unlock(&(pool->lock));
			   //一次减少default_thread个线程
			   for(int i=0;i<DEFAULT_THREAD_VARY;i++)
			   {
				   //让线程自杀
				    pthread_cond_signal(&(pool->queue_not_empty));
			   }
			   
		}
		
	}
	return NULL;
}

int threadpool_destroy(threadpool_t *pool)
{
	in ti=0;
	if(pool==NULL)
	{
		return -1;
	}
	
	pool->shutdown=1;
	pthread_join(pool->adjust_tid,NULL);
	
	for(int i=0;i<pool->live_thr_num;i++)
	{
		pthread_cond_broadcast(&(pool->queue_not_empty));
	}
	
	//这个是回收线程，防止出现僵尸线程,类似多进程中的waitpid
	for(int i=0;i<pool->live_thr_num;i++)
	{
		pthread_join(pool->threads[i],NULL);
	}
	threadpool_free(pool);
	return 0;
}

int threadpool_free(threadpool_t* pool)
{
	if(pool==NULL)
	{
		return -1;
	}
	if(pool->task_queue)
	{
		free(pool->task_queue);
	}
	if(pool->threads)
	{
		free(pool->threads);
		pthread_mutex_destroy(&(pool->lock));
		pthread_mutex_destroy(&(pool->thread_counter));
		pthread_cond_destroy(&(pool->queue_not_empty));
		pthread_mutex_destroy(&(pool->queue_not_full));
	}
	free(pool);
	pool=NULL;
}

int threadpool_busy_threadnum(threadpool_t*pool)
{
	int busy_threadnum=-1;
	pthread_mutex_lock(&(pool->thread_counter));
	busy_threadnum=pool->busy_thr_num;
	pthread_mutex_unlock(&(pool->thread_counter));
	return busy_threadnum;
}

int is_thread_alive(pthread_t tid)
{
	//该函数其实不是kill线程，而是向线程发送一个signal
	int kill_rc=pthread_kill(tid,0);
	if(kill_rc==ESRCH)
	{
		return 0;
	}
	return 1;
	
}
