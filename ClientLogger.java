import java.util.*;
import java.io.*;
import java.net.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

class ClientLogger implements Runnable
{
	private PrintWriter fw;

	public ClientLogger(PrintWriter fw)
	{
		this.fw=fw;
	}

	@Override public void run()
	{
		try
		{
			ServerSocket s=new ServerSocket(9000);
		    System.out.println("Client Logger Started");
		    for (;;)
		    {
		       	Socket incoming=s.accept();
				java.io.InputStream inStream=incoming.getInputStream();
				ObjectInputStream oin=new ObjectInputStream(inStream);
				//Response response=(Response) oin.readObject();
				
				//TransferResponse transferresponse=(TransferResponse) response;
				//List<TransferResponse> clResponseList=Collections.synchronizedList(new ArrayList<TransferResponse>());
				List<TransferResponse> clResponseList = (List<TransferResponse>) oin.readObject();
				for(int i=0; i<clResponseList.size(); i++)
				{
					fw.println(clResponseList.get(i).getFromUID()+" "+clResponseList.get(i).getToUID()+" "+clResponseList.get(i).getAmount()+" "+clResponseList.get(i).getTransferred());
				}
				fw.flush();
				incoming.close();
		    }				
		}
		catch (IOException e)
		{
			System.out.println("Client exception");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}