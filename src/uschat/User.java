package uschat;

//用户信息类
public class User{
	private String account;
	private String password;
	private String nickName;

	public User(String account, String password, String nickName) {
		this.account = account;
		this.password = password;
		this.nickName = nickName;
	}
	public User(String account, String nickName) {
		this.account = account;
		this.nickName = nickName;
	}
	public User() {
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}