import java.net.InetAddress;
import java.net.UnknownHostException;

class UniversalRequest extends Request implements Comparable<UniversalRequest>
{
	private boolean ownRequest;
	private int clock;
	private int serverId;
	private TransferRequest transferrequest;
	private boolean isHalt;

	public UniversalRequest(String requestName, InetAddress clientName, int clientPort, boolean ownRequest, int clock, int serverId, TransferRequest transferrequest, boolean isHalt)
	{
		super(requestName, clientName, clientPort);
		this.ownRequest=ownRequest;
		this.clock=clock;
		this.serverId=serverId;
		this.transferrequest=transferrequest;
		this.isHalt=isHalt;
	}


	public int compareTo(UniversalRequest other)
	{
		if(clock==other.getClockValue())
		{
			return serverId-other.getServerId();
		}
		else
			return clock-other.getClockValue();
	}

	public void setOwnRequest(boolean ownRequest)
	{
		this.ownRequest=ownRequest;
	}

	public TransferRequest getTransferRequest()
	{
		return transferrequest;
	}

	public boolean getOwnRequest()
	{
		return ownRequest;
	}

	public int getClockValue()
	{
		return clock;
	}

	public int getServerId()
	{
		return serverId;
	}

	public boolean getIsHalt()
	{
		return isHalt;
	}
}