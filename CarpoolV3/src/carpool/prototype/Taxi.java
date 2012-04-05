package carpool.prototype;

public class Taxi {
	private String carno;
	private Integer carvolume;
	private String phone;
	private String driver;

	public Taxi(String carno, String driver, Integer carvolume, String phone) {
		this.setCarno(carno);
		this.setCarvolume(carvolume);
		this.setPhone(phone);
		this.setDriver(driver);
	}

	public String getCarno() {
		return carno;
	}

	public void setCarno(String carno) {
		this.carno = carno;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getCarvolume() {
		return carvolume;
	}

	public void setCarvolume(Integer carvolume) {
		this.carvolume = carvolume;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

}
