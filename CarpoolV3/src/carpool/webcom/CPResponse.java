package carpool.webcom;

import org.json.JSONException;
import org.json.JSONObject;

public class CPResponse {
	private String content;
	private JSONObject json;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public CPResponse(String content){
		this.content = content;
		try {
			this.setJson(new JSONObject(content));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public JSONObject getJson() {
		return json;
	}
	public void setJson(JSONObject json) {
		this.json = json;
	}
}
