package de.kress.peter;

import org.json.JSONObject;

public class Car{
	private final String name;
	private final int dors;
	
	private Car(String name, int dors) {
		this.name = name;
		this.dors = dors;
	}
	
	public static String serialize(Car car) throws CloneNotSupportedException {
		return new JSONObject(car).toString();
	}
	
	public static Car create(String jsonCar) {
		JSONObject jsonObject = new JSONObject(jsonCar);
		Car car = null;
		if (jsonObject != null){
			car = new Car(jsonObject.optString("name"),
					jsonObject.optInt("dors"));
		}
		return car;
	}
	
	public String getName() {
		return name;
	}

	public int getDors() {
		return dors;
	}
}
