import java.util.*;
class MyCompy implements Comparator{
	public int compare(Object o1, Object o2){
    Tile t1 = (Tile)o1;
    Tile t2 = (Tile)o2;
    return t1.row() - t2.row();
  }
}
