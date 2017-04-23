package de.kress.peter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class World {
	private final Car topCar;
	private final Car[] cars;
	private final int indicator;
	private final String name;
	
	private World(Car[] cars, Car topCar, int indicator, String name) {
		this.cars = cars;
		this.topCar = topCar;
		this.indicator = indicator;
		this.name = name;
	}
	
	public static String serialize(World world) {
		return new JSONObject(world).toString();
	}

	public static World create(String worldJSON) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		JSONObject jsonWorld = new JSONObject(worldJSON);
		
		int indicator = jsonWorld.optInt("indicator");
		String name = jsonWorld.optString("name");
		JSONObject carJSON = jsonWorld.optJSONObject("topCar");
		Car topCar = null;
		if (carJSON != null) {
			topCar = Car.create(carJSON.toString());
		}
		
		JSONArray jsonCars = jsonWorld.optJSONArray("cars");
		ArrayList<Car> carList = new ArrayList<Car>();
		if (jsonCars != null) {
		for(int i=0; i < jsonCars.length(); i++ ) {
			carList.add(Car.create(jsonCars.getJSONObject(i).toString()));
		}
		}
		Car[] cars = carList.toArray(new Car[carList.size()]);

		return new World(cars, topCar, indicator, name);
	}
	
	public int getIndicator() {
		return indicator;
	}

	public Car[] getCars() {
		return cars;
	}
	
	public Car getTopCar() {
		return topCar;
	}
	
	public String getName() {
		return name;
	}
}
