import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.PrintWriter;


class ThreadedEchoClientHandler implements Runnable
{
	private int numOperations;
	private ArrayList<ServerDirectory> serverList;
	private PrintWriter fw;

	public ThreadedEchoClientHandler(int numOperations, ArrayList<ServerDirectory> serverList, PrintWriter fw)
	{
		this.numOperations=numOperations;
		this.serverList=serverList;
		this.fw=fw;
	}

	@Override public void run()
	{
		try
		{
			for(int i=0;i<numOperations;i++)
			{
				
				int serverId=(int )(Math.random() * serverList.size());
				int k=0;
				for(k=0; k<serverList.size(); k++)
					if(serverList.get(k).getServerId()==serverId)
						break;
				
				Socket incoming=new Socket("localhost", serverList.get(k).getServerPort());
				
				int fromUID = (int )(Math.random() * 10 + 1);
				int toUID = (int )(Math.random() * 10 + 1);
				//java.io.InputStream inStream=incoming.getInputStream();
				java.io.OutputStream outStream=incoming.getOutputStream();
				ObjectOutputStream os=new ObjectOutputStream(outStream);
				//ObjectInputStream oin=new ObjectInputStream(inStream);

				TransferRequest transferrequest=new TransferRequest("TransferRequest", fromUID, toUID, 10);
				
				fw.println("MULTI-THREADED CLIENT "+serverId+" "+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+" TRANSFER "+fromUID+" "+toUID+" "+10);
				fw.flush();
				
				os.writeObject(transferrequest);
				incoming.close();
			}				
		}
		catch (IOException e)
		{
			System.out.println("Client exception");
			e.printStackTrace();
		} 
	}
}