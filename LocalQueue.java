import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.PriorityQueue;

class LocalQueue
{
	private PriorityQueue<UniversalRequest> localQueue;
	private Lock bankLock;

	public LocalQueue()
	{
		localQueue=new PriorityQueue<UniversalRequest>();
		bankLock=new ReentrantLock();
	}

	public void add(UniversalRequest universalrequest)
	{
		bankLock.lock();
		try
		{
			localQueue.add(universalrequest);
		}
		finally
		{
			bankLock.unlock();
		}
	}

	public UniversalRequest peek()
	{
		bankLock.lock();
		try
		{
			return localQueue.peek();
		}
		finally
		{
			bankLock.unlock();
		}
	}

	public UniversalRequest poll()
	{
		bankLock.lock();
		try
		{
			return localQueue.poll();
		}
		finally
		{
			bankLock.unlock();
		}
	}

	public int size()
	{
		bankLock.lock();
		try
		{
			return localQueue.size();
		}
		finally
		{
			bankLock.unlock();
		}
	}

}