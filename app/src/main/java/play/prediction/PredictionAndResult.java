package play.prediction;

import org.json.JSONException;
import org.json.JSONObject;

public class PredictionAndResult {

	private String postion;
	private boolean baseHit;
	private boolean homeRun;
	private boolean runScored;

	public String getPostion() {
		return postion;
	}

	public void setPostion(String postion) {
		this.postion = postion;
	}

	public boolean isBaseHit() {
		return baseHit;
	}

	public void setBaseHit(boolean baseHit) {
		this.baseHit = baseHit;
	}

	public boolean isHomeRun() {
		return homeRun;
	}

	public void setHomeRun(boolean homeRun) {
		this.homeRun = homeRun;
	}

	public boolean isRunScored() {
		return runScored;
	}

	public void setRunScored(boolean runScorred) {
		this.runScored = runScorred;
	}

	public void jsonParser(JSONObject jsonObject) {
		try {
			if (jsonObject.has("position")) {
				this.setPostion(jsonObject.getString("position"));
			}
			if (jsonObject.has("basehit")) {
				this.setBaseHit(jsonObject.getBoolean("basehit"));
			}
			if (jsonObject.has("homerun")) {
				this.setHomeRun(jsonObject.getBoolean("homerun"));
			}
			if (jsonObject.has("runscored")) {
				this.setRunScored(jsonObject.getBoolean("runscored"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
