package carpool.webcom;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import carpool.v3.HomeActivity;

public class MessageHandler extends Handler {
	public CPActivity activity;

	public MessageHandler(Looper looper, CPActivity activity) {
		super(looper);
		this.activity = activity;
	}

	public void handleMessage(Message msg) {
		if (msg.obj.equals("[0]MSGRESPONSE")) {
			Toast.makeText(activity, "您的请求已被接受，请等待", Toast.LENGTH_LONG).show();
		} else if (msg.obj.equals("[0]GETON")) {
			Toast.makeText(activity, "上车确认", Toast.LENGTH_LONG).show();
		} else if (msg.obj.equals("[0]GETOFF")) {
			Toast.makeText(activity, "下车确认", Toast.LENGTH_LONG)
					.show();
			CPSession.clear();
		}
	}
}
