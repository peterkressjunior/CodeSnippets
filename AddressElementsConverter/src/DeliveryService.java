import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class DeliveryService{
	
	private static final String DELIMITER = " ";
	public int item;
	public Type type;
	public String value;
	public TypeComapator typeComparator;

	public DeliveryService(Integer item, Type type, String value) {
		super();
		this.item = item;
		this.type = type;
		this.value = value;
		this.typeComparator = new TypeComapator();
	}

	public enum Type {
		COMPLETE, DESCRIPTOR, NUMBER, ADD_INFO
	}
	
	class TypeComapator implements Comparator<DeliveryService>{
		@Override
		public int compare(DeliveryService adel0, DeliveryService adel1) {
			return adel0.type.compareTo(adel1.type);
		}
	}
	
	public static List<String> flattenList(List<DeliveryService> deliveryServiceItems){
		HashMap<Integer, TreeSet<DeliveryService>> deliveryServiceHash = 
				new HashMap<Integer, TreeSet<DeliveryService>>();
		List<String> deliveryServiceList = new ArrayList<String>();
		
		for(DeliveryService element : deliveryServiceItems){
			if (deliveryServiceHash.containsKey(element.item)){
				deliveryServiceHash.get(element.item).add(element);
			}else{
				TreeSet<DeliveryService> elementSet = new TreeSet<DeliveryService>(element.typeComparator);
				elementSet.add(element);
				deliveryServiceHash.put(element.item, elementSet);
			}
		}

		Collection<TreeSet<DeliveryService>> deliveryServiceCollection = deliveryServiceHash.values();
		
		Iterator<DeliveryService> iterator;
		DeliveryService ds;
		for(TreeSet<DeliveryService> elementSet : deliveryServiceCollection){
			iterator = elementSet.iterator();
			while(iterator.hasNext()){
				ds = iterator.next();
				
				for(int i = deliveryServiceList.size() - 1; i < ds.item - 1; i++){
					deliveryServiceList.add("");
				}
				
				if (ds.type.equals(DeliveryService.Type.COMPLETE)){
					deliveryServiceList.set(ds.item - 1, ds.value);
					break;
				} else {
					deliveryServiceList.set(ds.item - 1, deliveryServiceList.get(ds.item - 1).concat(DELIMITER + ds.value));
				}
			}
			
		}	
		return deliveryServiceList;
	}
}
