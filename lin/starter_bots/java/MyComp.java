import java.util.*;
class MyComp implements Comparator{
  Tile location;
  Ants ants;
  MyComp(Tile location, Ants ants){
  	this.location = location;
		this.ants = ants;
	}
	
	public int compare(Object o1, Object o2){
    Tile t1 = (Tile)o1;
    Tile t2 = (Tile)o2;
    int d1 = ants.my_distance(t1, location);
    int d2 = ants.my_distance(t2, location);
    return d1 - d2;
  }
}
