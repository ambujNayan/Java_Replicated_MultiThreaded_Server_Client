import java.net.InetAddress;
import java.net.UnknownHostException;

class AckRequest extends Request
{
	private int clock;
	private int serverId;
	private UniversalRequest universalrequest;

	public AckRequest(String requestName, InetAddress clientName, int clientPort, int clock, int serverId, UniversalRequest universalrequest)
	{
		super(requestName, clientName, clientPort);
		this.clock=clock;
		this.serverId=serverId;
		this.universalrequest=universalrequest;
	}

	public UniversalRequest getUniversalRequest()
	{
		return universalrequest;
	}

	public int getClockValue()
	{
		return clock;
	}

	public int getServerId()
	{
		return serverId;
	}
}