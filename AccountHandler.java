import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.omg.CORBA.portable.InputStream;

public class AccountHandler implements Runnable
{
	private Socket incoming;
	private Bank bank;
	private ArrayList<ServerDirectory> serverList;
	private LamportClock lamportClock;
	private PriorityBlockingQueue<UniversalRequest> localQueue;
	List<Integer> ackList;
	private int localServerId;
	private ServerSocket s;
	private boolean halt;
	private PrintWriter fw;
	private Date date;

	public AccountHandler(Socket incoming, Bank bank, ArrayList<ServerDirectory> serverList, LamportClock lamportClock, PriorityBlockingQueue<UniversalRequest> localQueue, List<Integer> ackList, int localServerId, ServerSocket s, PrintWriter fw, Date date)
	{
		this.incoming=incoming;
		this.bank=bank;
		this.serverList=serverList;
		this.lamportClock=lamportClock;
		this.localQueue=localQueue;
		this.ackList=ackList;
		this.localServerId=localServerId;
		this.s=s;
		this.halt=false;
		this.fw=fw;
		this.date=date;
	}

	@Override public void run()
	{
		try
		{
		try
		{
			java.io.InputStream inStream=incoming.getInputStream();
			ObjectInputStream oin=new ObjectInputStream(inStream);
			//OutputStream outStream=incoming.getOutputStream();
			//ObjectOutputStream os=new ObjectOutputStream(outStream);
			Request request=(Request) oin.readObject();
			String actionName=request.getRequestName();

			switch(actionName)
			{
				case "TransferRequest":
					TransferRequest transferrequest=(TransferRequest) request;
					UniversalRequest newUniversalRequest;
					synchronized(lamportClock)
					{
							lamportClock.setClockValue(lamportClock.getClockValue()+1);
							fw.println("MULTI-THREADED CLIENT "+"CLIENT-REQ "+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+" "+lamportClock.getClockValue()+" "+localServerId+" TRANSFER "+transferrequest.getFromUID()+" "+transferrequest.getToUID()+" "+transferrequest.getAmount());
							fw.flush();

							newUniversalRequest=new UniversalRequest("ServerRequest", true, lamportClock.getClockValue(), localServerId, transferrequest);
							localQueue.add(newUniversalRequest);

					if(serverList.size()==0)
					{
						bank.Transfer(transferrequest.getFromUID(), transferrequest.getToUID(), transferrequest.getAmount());
						System.out.println("TRANSFER COMPLETE ");
					}
					else
					{
						for(int k=0; k<serverList.size(); k++)
						{
							Socket s1=new Socket(serverList.get(k).getHostName(), serverList.get(k).getServerPort());
							OutputStream outStream1=s1.getOutputStream();
							ObjectOutputStream os1=new ObjectOutputStream(outStream1);
							try
							{
							//	fw.println("BROADCAST TO server "+ k+ "  "+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+" "+lamportClock.getClockValue()+" "+localServerId+" TRANSFER "+transferrequest.getFromUID()+" "+transferrequest.getToUID()+" "+transferrequest.getAmount());
								//fw.flush();
								os1.writeObject(newUniversalRequest);
							}
							catch (IOException e)
							{
								//e.printStackTrace();
							}
							s1.close();
						}
					}
				}
					break;

				case "HALT":
					//System.out.println("HALT");
					HaltRequest haltrequest=(HaltRequest) request;
					UniversalRequest newUniversalRequest9;
					synchronized(lamportClock)
					{
					  	lamportClock.setClockValue(lamportClock.getClockValue()+20000);

						  newUniversalRequest9=new UniversalRequest("ServerRequest", false, lamportClock.getClockValue(), localServerId, null);
							localQueue.add(newUniversalRequest9);

					if(serverList.size()==0)
					{
						System.out.println("HALT ISSUED !!! ");
						for(int i=0; i<10; i++)
					    {
					      System.out.println("The account "+(i+1)+" has balance: "+bank.GetBalance(i+1));
					    }
					    halt=true;
					}
					else
					{
						for(int k=0; k<serverList.size(); k++)
						{
							Socket s7=new Socket(serverList.get(k).getHostName(), serverList.get(k).getServerPort());
							OutputStream outStream7=s7.getOutputStream();
							ObjectOutputStream os7=new ObjectOutputStream(outStream7);
							try
							{

								os7.writeObject(newUniversalRequest9);

							}
							catch (IOException e)
							{
								//e.printStackTrace();
							}
							s7.close();
						}
					}
				}
					break;

				case "ServerRequest":
					UniversalRequest newUniversalRequest1=(UniversalRequest) request;
					synchronized(lamportClock)
					{
						lamportClock.setClockValue(Math.max(lamportClock.getClockValue(), newUniversalRequest1.getClockValue())+1);

						if(newUniversalRequest1.getTransferRequest()!=null)
						{
							fw.println("MULTI-THREADED CLIENT "+"SRV-REQ "+localServerId+" "+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+" "+newUniversalRequest1.getClockValue()+" "+newUniversalRequest1.getServerId()+" TRANSFER "+newUniversalRequest1.getTransferRequest().getFromUID()+" "+newUniversalRequest1.getTransferRequest().getToUID()+" "+newUniversalRequest1.getTransferRequest().getAmount());
							fw.flush();
						}
						UniversalRequest localNewRequest=new UniversalRequest("ServerRequest", newUniversalRequest1.getOwnRequest(), newUniversalRequest1.getClockValue(), newUniversalRequest1.getServerId(), newUniversalRequest1.getTransferRequest());
						localQueue.add(localNewRequest);
					}
					synchronized(ackList)
					{
						if(newUniversalRequest1.getClockValue()>ackList.get(newUniversalRequest1.getServerId()))
							ackList.set(newUniversalRequest1.getServerId(), newUniversalRequest1.getClockValue()+1);
					}

					for(int k=0; k<serverList.size(); k++)
					{
						Socket s9=new Socket(serverList.get(k).getHostName(), serverList.get(k).getServerPort());
						OutputStream outStream9=s9.getOutputStream();
						ObjectOutputStream os9=new ObjectOutputStream(outStream9);
						AckRequest ackRequest9=new AckRequest("AckRequest", lamportClock.getClockValue(), localServerId, newUniversalRequest1);
						try
						{
							os9.writeObject(ackRequest9);
						}
						catch (IOException e)
						{
							//e.printStackTrace();
						}
						s9.close();
					}

					synchronized(localQueue)
					{
						while(localQueue.size()>0)
						{
							int qqClock=localQueue.peek().getClockValue();

							boolean processs=true;

							synchronized(ackList)
							{
								for(int i=0; i<ackList.size(); i++)
								{
									if(i!=localServerId)
									{
										if(ackList.get(i)<=qqClock)
										{
											processs=false;
											break;
										}
									}
								}
							}

							if(processs)
							{
								if(localQueue.peek().getOwnRequest())
								{
									bank.Transfer(localQueue.peek().getTransferRequest().getFromUID(), localQueue.peek().getTransferRequest().getToUID(), localQueue.peek().getTransferRequest().getAmount());
									//System.out.println("TRANSFER COMPLETE ");
									fw.println("MULTI-THREADED CLIENT "+"PROCESS "+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+" "+localQueue.peek().getClockValue()+" "+localQueue.peek().getServerId());
									fw.flush();
								}
								else
								{
									System.out.println("HALT ISSUED !!! ");
									for(int i=0; i<10; i++)
								    {
								      System.out.println("The accound "+(i+1)+" has balance: "+bank.GetBalance(i+1));
								    }
								    halt=true;
								}
								localQueue.poll();
							}
							else
								break;
						}
					}
					break;

			case "AckRequest":
					//System.out.println("ACK RECV");
					AckRequest ackRequest1=(AckRequest) request;
					synchronized(lamportClock)
					{
						lamportClock.setClockValue(Math.max(lamportClock.getClockValue(), ackRequest1.getClockValue())+1);
					}
					synchronized(ackList)
					{
						if(ackRequest1.getClockValue()>ackList.get(ackRequest1.getServerId()))
							ackList.set(ackRequest1.getServerId(), ackRequest1.getClockValue());
					}

					synchronized(localQueue)
					{
						while(localQueue.size()>0)
						{
							int qClock=localQueue.peek().getClockValue();

							boolean process=true;

							synchronized(ackList)
							{
								for(int i=0; i<ackList.size(); i++)
								{
									if(i!=localServerId)
									{
										if(ackList.get(i)<=qClock)
										{
											process=false;
											break;
										}
									}
								}
							}

							if(process)
							{
								if(localQueue.peek().getOwnRequest())
								{
									bank.Transfer(localQueue.peek().getTransferRequest().getFromUID(), localQueue.peek().getTransferRequest().getToUID(), localQueue.peek().getTransferRequest().getAmount());
									//System.out.println("TRANSFER COMPLETE ");
									fw.println("MULTI-THREADED CLIENT "+"PROCESS "+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+" "+localQueue.peek().getClockValue()+" "+localQueue.peek().getServerId());
									fw.flush();
								}
								else
								{
									System.out.println("HALT ISSUED !!! ");
									for(int i=0; i<10; i++)
								    {
								      System.out.println("The accound "+(i+1)+" has balance: "+bank.GetBalance(i+1));
								    }
								    halt=true;
								}
								localQueue.poll();
							}
							else
								break;
						}
					}
					break;

				default:
					break;
			}
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			incoming.close();
			if(halt)
			{
				Date currDate=new Date();
				System.out.println("Average in milliseconds: "+((currDate.getTime()-date.getTime())/2400));

				s.close();
			}
		}
	}
	catch (IOException e)
	{
		//System.out.println("Server exception");
		//e.printStackTrace();
	}
	}
}
