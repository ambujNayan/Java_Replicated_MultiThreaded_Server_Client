import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LamportClock implements Serializable
{
	private int clock;
  	private Lock bankLock;

	public LamportClock()
	{
		clock=0;
		bankLock=new ReentrantLock();
	}

	public int getClockValue()
	{
		bankLock.lock();
		try
		{
			return clock;
		}
		finally
		{
			bankLock.unlock();
		}
	}

	public void setClockValue(int clock)
	{
		bankLock.lock();
		try
		{
			this.clock=clock;
		}
		finally
		{
			bankLock.unlock();
		}
	}
}