package cn.edu.sysu.secretnote;

public class ParamsSingleton {
	private String userName;
	private static ParamsSingleton params = null;

	private ParamsSingleton() {

	}

	public static ParamsSingleton getInstance() {
		if (params == null) {
			params = new ParamsSingleton();
		}
		return params;
	}

	public void setUserName(String name) {
		userName = name;
	}

	public String getUserName() {
		return userName;
	}
}
