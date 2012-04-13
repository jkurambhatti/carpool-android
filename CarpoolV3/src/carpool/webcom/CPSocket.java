package carpool.webcom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class CPSocket extends Socket implements Runnable {
	BufferedReader reader;
	PrintWriter writer;
	static final long timeout = 30000;
	Thread main;
	Queue<Message> messageList = new LinkedList<Message>();
	boolean importanting = false;
	int main_seed;
	Thread waittingThread;
	String main_msg;
	public CPActivity activity;

	public CPSocket(String serverip, int serverport)
			throws UnknownHostException, IOException {
		super(serverip, serverport);
		reader = new BufferedReader(
				new InputStreamReader(this.getInputStream()));
		writer = new PrintWriter(this.getOutputStream(), true);
		main = new Thread(this);
		main.start();
	}

	static class Message {
		String msg;
		int seed;
		public Message(){
			
		}
		public Message(String msg) {
			this.msg = msg;
		}
		public Message(int seed, String msg){
			this.seed = seed;
			this.msg = msg;
		}
		public static Message getFromString(String msg){
			Message message = new Message();
			int seedPos = msg.indexOf(']');
			message.seed = Integer.parseInt(msg.substring(1, seedPos));
			message.msg = msg.substring(seedPos+1);
			return message;
		}
	}

	public String sendMsg(String msg) {
		System.out.println("SEND: " + msg);
		MessageThread mt = new MessageThread(new Message(msg));
		if(Message.getFromString(mt.response).msg.equals("EXIT")){
			main.stop();
//			activity.socket = null;
			return "EXIT";
		}
		return "RES: " + mt.response;
	}

	class MessageThread extends Thread {
		String response;

		public MessageThread(Message msg) {
			importanting = true;
			main_seed = new Random().nextInt();
			writer.println("[" + main_seed + "]" + msg.msg);
			this.run();
		}

		public void run() {
			try {
				waittingThread = Thread.currentThread();
				sleep(timeout);
				response = "TIMEOUT";
			} catch (InterruptedException e) {
				response = main_msg;
			}
		}

		private void sendError(int seed) {
			System.out.println("TIMEOUT:" + seed);
		}
	}

	@Override
	public void run() {
		String msg;
		try {
			while ((msg = reader.readLine()) != null) {
				int seedPos = msg.indexOf(']');
				int seed = Integer.parseInt(msg.substring(1, seedPos));
				System.out.println("FROM SERVER:" + msg + "  " + main_seed);
				if (importanting) {
					if (main_seed == seed) {
						main_msg = msg;
						waittingThread.interrupt();
						importanting = false;
						releaseMessage();
					} else {
						waitOtherMessage(msg);
					}
				} else {
					waitOtherMessage(msg);
					releaseMessage();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void releaseMessage() {
		Message msg;
		while ((msg = messageList.poll()) != null) {
			System.out.println("RELEASE:" + msg.msg);
			activity.append(msg.msg);
		}
	}

	private void waitOtherMessage(String msg) {
		messageList.add(new Message(msg));
	}
}
