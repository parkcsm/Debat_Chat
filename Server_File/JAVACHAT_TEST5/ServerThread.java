import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ServerThread extends Thread {

	private Socket socket;
	private MyService server;
	private PrintWriter pw;
	private Scanner in;

	public ServerThread(Socket socket, MyService server) {
		this.socket = socket;
		this.server = server;

		try {
			pw = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			in = new Scanner(socket.getInputStream());

			String res = "";
			while (in.hasNext()) {
				res = in.nextLine();

				System.out.println("Message : " + res);
				transMsg(res);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void transMsg(String res) {

		StringTokenizer stn = new StringTokenizer(res, "/");

		String type = stn.nextToken();
		String type2 = stn.nextToken();
		String receiver = stn.nextToken();
		String sender = stn.nextToken();
		String msg = stn.nextToken();

		setName(sender); // set Socket_Thread_Name(String) by sender(String)

		server.sendMsg(type, type2, receiver, sender, msg);
	}

	public PrintWriter getPw() {
		return pw;
	}

	Socket getSocket() {
		return socket;
	}

}