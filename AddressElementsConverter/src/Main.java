import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		List<DeliveryService> deliveryServiceItems = 
				new ArrayList<DeliveryService>();
		deliveryServiceItems.add(new DeliveryService(2, DeliveryService.Type.DESCRIPTOR, "POST BOX"));
		deliveryServiceItems.add(new DeliveryService(1, DeliveryService.Type.COMPLETE, "POST BOX 12 XY"));
		deliveryServiceItems.add(new DeliveryService(2, DeliveryService.Type.NUMBER, "12"));
		deliveryServiceItems.add(new DeliveryService(4, DeliveryService.Type.ADD_INFO, "XY"));
		
		List<String> deliveryServiceList = DeliveryService.flattenList(deliveryServiceItems);
		System.out.println(deliveryServiceList.toString());
	}

	
}
