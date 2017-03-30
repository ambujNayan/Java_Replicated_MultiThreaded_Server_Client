import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

class Request implements Serializable
{
	private String requestName;
	private InetAddress clientName;
    private int clientPort;

	public Request(String requestName, InetAddress clientName, int clientPort)
	{
		this.requestName=requestName;
		this.clientName=clientName;
		this.clientPort=clientPort;
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
}