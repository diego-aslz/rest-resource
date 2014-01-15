package restresource.test.support;

import restresource.Id;

import com.google.gson.annotations.Expose;

public class StatusCode {
	@Id
	int customId;
	@Expose
	int status;

	public static String getSite() {
		return "http://localhost:4567/";
	}

	public StatusCode() {
	}

	public StatusCode(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCustomId() {
		return customId;
	}

	public void setCustomId(int customId) {
		this.customId = customId;
	}
}
