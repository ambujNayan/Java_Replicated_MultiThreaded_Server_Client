import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

class Request implements Serializable
{
	private String requestName;
	private InetAddress clientName;
    private int clientPort;
    private int numThreads;

	public Request(String requestName, InetAddress clientName, int clientPort, int numThreads)
	{
		this.requestName=requestName;
		this.clientName=clientName;
		this.clientPort=clientPort;
		this.numThreads=numThreads;
	}

	public String getRequestName()
	{
		return requestName;
	}

	public InetAddress getclientName()
	{
		return clientName;
	}

	public int getPort()
	{
		return clientPort;
	}

	public int getNumThreads()
	{
		return numThreads;
	}
}