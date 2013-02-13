import java.util.*;
import java.io.*;
public class MyBot implements Bot {

  public static void main(String[] args) {
    Ants.run(new MyBot());
  }

  List<Tile>saves = new ArrayList();
  int bestValue = -10000;
  List<Tile>simulates = new ArrayList();
  public List<Tile>  max(int antIndex, Ants ants, List<Tile> myAnts, Tile enemyAnts) {
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    if(antIndex < myAnts.size()) {
      Tile myAnt = myAnts.get(antIndex);
      for(int k = 0 ; k < 5 ; k ++) {
        Tile sim;
        if(k == 4)
          sim = myAnt;
        else
          sim = ants.tile(myAnt, directions.get(k));
        if(sim.row() < ants.rows() && sim.col() < ants.cols() && sim.row() >=0 && sim.col() >= 0) { 
          if((ants.ilk(sim).isUnoccupied() || ants.ilk(sim).isFood() || ants.ilk(sim).hisAnt()) && !simulates.contains(sim)) {
            sim.parent = myAnt;
            simulates.add(sim);
            max(antIndex +1, ants, myAnts, enemyAnts);
            simulates.remove(sim);
          }
        }
      }
    }
    else {
      int value = min(0, ants, simulates, enemyAnts);
      if(value > bestValue) {
        bestValue = value;
        saves = new ArrayList();
        saves.addAll(simulates);
      }
    }
    return saves;
  }

  public int min(int antIndex, Ants ants, List<Tile> myAnts, Tile enemyAnts) {
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    int minValue = 10000;
    for(int k = 0 ; k < 5 ; k ++) {
      Tile sim;
      if(k == 4)
        sim = enemyAnts;
      else
        sim = ants.tile(enemyAnts, directions.get(k));
      if(sim.row() < ants.rows() && sim.col() < ants.cols() && sim.row() >=0 && sim.col() >= 0) {
        if((ants.ilk(sim).isUnoccupied() || ants.ilk(sim).isFood() || ants.ilk(sim).myAnt())) {
          int value = evaluate(myAnts, sim, ants);
            if(value < minValue)
              minValue = value;
        }
      }
    }
    return minValue;
  }
  int evaluate(List<Tile> myAnts, Tile enemyAnts, Ants ants) {
    int my_dead = 0;
    int enemy_dead = 0;
    int dist = 0;
    int nr = 0;
    for(int i = 0 ; i < myAnts.size() ; i ++)
      dist = dist + ants.my_distance(enemyAnts, myAnts.get(i));
    for(int i = 0 ; i < myAnts.size() ; i ++)
      if(ants.isAlphaDist(myAnts.get(i), enemyAnts))
        nr++;
    if(nr == 1)
      return -10000;
    else if(nr  == 2 || nr == 3 || nr == 4 || nr == 5)
      return 10000;
    else
      return -dist;
  }



  public Tile BFSfood(Tile start, Ants ants) {
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    Tile map[][] = new Tile[ants.rows()][ants.cols()];
    ArrayList<Tile> q = new ArrayList();
    for(int i = 0 ; i < ants.rows() ; i++)
      for(int j = 0 ; j < ants.cols() ; j++){
        map[i][j] = new Tile(i, j);
        map[i][j].p = null;
        map[i][j].dist = 100000;
        map[i][j].c = 0;
      }
    map[start.row()][start.col()].dist = 0;
    q.add(start);
    map[start.row()][start.col()].c = 1;
    while(q.size()!= 0){
      Tile u = q.get(0);
      if(map[u.row()][u.col()].dist >= 30)
        return new Tile(0, 0);
      if(ants.ilk(u).isFood()) {
        Tile ant = BFSant(u, ants).ant;
        if(start.row() == ant.row() && start.col() == ant.col() ) {
          Tile his = BFShisant(u, ants);
          int dist = his.dist;
          if((his.row() == 0 && his.col() == 0) || (!(his.row() == 0 && his.col() == 0) && dist > map[u.row()][u.col()].dist)) {
            Tile food = u;
            while(true){
              if( ( map[u.row()][u.col()].p.row() == start.row()) && map[u.row()][u.col()].p.col() == start.col()) {
                u.food = food;
                return u;
              }
              u = map[u.row()][u.col()].p;
            }
          }
        }
      }
      for(int k = 0 ; k < 4 ; k ++){
        Tile v = ants.tile(u, directions.get(k));
        if(v.row() < ants.rows() && v.col() < ants.cols() && v.row() >=0 && v.col() >= 0)
          if( (ants.ilk(v).isPassable()) && map[v.row()][v.col()].c == 0 ){
            map[v.row()][v.col()].dist = map[u.row()][u.col()].dist + 1;
            map[v.row()][v.col()].p = u;
            map[v.row()][v.col()].c = 1;
            q.add(q.size(), v);
          }
      }
      map[u.row()][u.col()].c = 2;
      q.remove(0);
    }
    Tile pa = new Tile(0, 0);
    pa.food = new Tile(0, 0);
    return pa;
  }

  public List<Tile> BFSants(Tile start, Ants ants){
    int nr = 0;
    List <Tile> myants = new ArrayList();
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    Tile map[][] = new Tile[ants.rows()][ants.cols()];
    ArrayList<Tile> q = new ArrayList();
    for(int i = 0 ; i < ants.rows() ; i++)
      for(int j = 0 ; j < ants.cols() ; j++){
        map[i][j] = new Tile(i, j);
        map[i][j].p = null;
        map[i][j].dist = 100000;
        map[i][j].c = 0;
      }
    map[start.row()][start.col()].dist = 0;
    q.add(start);
    map[start.row()][start.col()].c = 1;
    while(q.size()!= 0){
      Tile u = q.get(0);
      if(map[u.row()][u.col()].dist >= 10 || nr == 5) {
        return myants;
      }
      if(ants.ilk(u).myAnt()) {
        Tile ant = u;
        myants.add(ant);
        nr ++;
      }
      for(int k = 0 ; k < 4 ; k ++){
        Tile v = ants.tile(u, directions.get(k));
        if(v.row() < ants.rows() && v.col() < ants.cols() && v.row() >= 0 && v.col() >= 0)
          if( (ants.ilk(v).isPassable()) && map[v.row()][v.col()].c == 0 ){
            map[v.row()][v.col()].dist = map[u.row()][u.col()].dist + 1;
            map[v.row()][v.col()].p = u;
            map[v.row()][v.col()].c = 1;
            q.add(q.size(), v);
          }
      }
      map[u.row()][u.col()].c = 2;
      q.remove(0);
    }
    return myants; 
  }

  public Tile BFSant(Tile start, Ants ants){
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    Tile map[][] = new Tile[ants.rows()][ants.cols()];
    ArrayList<Tile> q = new ArrayList();
    for(int i = 0 ; i < ants.rows() ; i++)
      for(int j = 0 ; j < ants.cols() ; j++){
        map[i][j] = new Tile(i, j);
        map[i][j].p = null;
        map[i][j].dist = 100000;
        map[i][j].c = 0;
      }
    map[start.row()][start.col()].dist = 0;
    q.add(start);
    map[start.row()][start.col()].c = 1;
    while(q.size()!= 0){
      Tile u = q.get(0);
      if(map[u.row()][u.col()].dist >= 30) {
        Tile pa = new Tile(0, 0);
        pa.ant = new Tile(0, 0);
        return pa;
      }
      if(ants.ilk(u).myAnt()) {
        Tile ant = u;
        while(true){
          if( ( map[u.row()][u.col()].p.row() == start.row()) && map[u.row()][u.col()].p.col() == start.col()) {
            u.ant = ant;
            return u;
          }
          u = map[u.row()][u.col()].p;
          }
      }
      for(int k = 0 ; k < 4 ; k ++){
        Tile v = ants.tile(u, directions.get(k));
        if(v.row() < ants.rows() && v.col() < ants.cols() && v.row() >= 0 && v.col() >= 0)
          if( (ants.ilk(v).isPassable()) && map[v.row()][v.col()].c == 0 ){
            map[v.row()][v.col()].dist = map[u.row()][u.col()].dist + 1;
            map[v.row()][v.col()].p = u;
            map[v.row()][v.col()].c = 1;
            q.add(q.size(), v);
          }
      }
      map[u.row()][u.col()].c = 2;
      q.remove(0);
    }
    Tile pa = new Tile(0, 0);
    pa.ant = new Tile(0, 0);
    return pa; 
  }

  public Tile BFShisant(Tile start, Ants ants){
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    Tile map[][] = new Tile[ants.rows()][ants.cols()];
    ArrayList<Tile> q = new ArrayList();
    for(int i = 0 ; i < ants.rows() ; i++)
      for(int j = 0 ; j < ants.cols() ; j++){
        map[i][j] = new Tile(i, j);
        map[i][j].p = null;
        map[i][j].dist = 100000;
        map[i][j].c = 0;
      }
    map[start.row()][start.col()].dist = 0;
    q.add(start);
    map[start.row()][start.col()].c = 1;
    while(q.size()!= 0){
      Tile u = q.get(0);
      if(map[u.row()][u.col()].dist >= 30) {
        Tile pa = new Tile(0, 0);
        pa.ant = new Tile(0, 0);
        return pa;
      }
      if(ants.ilk(u).hisAnt() && !(u.row() == start.row() && u.col() == start.col())) {
        u.dist = map[u.row()][u.col()].dist;
        return u;
      }
      for(int k = 0 ; k < 4 ; k ++){
        Tile v = ants.tile(u, directions.get(k));
        if(v.row() < ants.rows() && v.col() < ants.cols() && v.row() >= 0 && v.col() >= 0)
          if( (ants.ilk(v).isPassable()) && map[v.row()][v.col()].c == 0 ){
            map[v.row()][v.col()].dist = map[u.row()][u.col()].dist + 1;
            map[v.row()][v.col()].p = u;
            map[v.row()][v.col()].c = 1;
            q.add(q.size(), v);
          }
      }
      map[u.row()][u.col()].c = 2;
      q.remove(0);
    }
    Tile pa = new Tile(0, 0);
    pa.ant = new Tile(0, 0);
    return pa; 
  }

  public Tile BFS(Tile start, Tile to, Ants ants){
    if(to.row() < 0 || to.row() >= ants.rows())
      return start;
    if(to.col() < 0 || to.col() >= ants.cols())
      return start;
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    Tile map[][] = new Tile[ants.rows()][ants.cols()];
    ArrayList<Tile> q = new ArrayList();
    for(int i = 0 ; i < ants.rows() ; i++)
      for(int j = 0 ; j < ants.cols() ; j++){
        map[i][j] = new Tile(i, j);
        map[i][j].p = null;
        map[i][j].dist = 100000;
        map[i][j].c = 0;
      }
    map[start.row()][start.col()].dist = 0;
    q.add(start);
    map[start.row()][start.col()].c = 1;
    while(q.size()!= 0){
      Tile u = q.get(0);
      if( (u.row() == to.row()) && (u.col() == to.col())){
        while(true){
          if( ( map[u.row()][u.col()].p.row() == start.row()) && map[u.row()][u.col()].p.col() == start.col()) {
            u.dist = map[to.row()][to.col()].dist;
            return u;
          }
          u = map[u.row()][u.col()].p;
          }
      }
      for(int k = 0 ; k < 4 ; k ++){
        Tile v = ants.tile(u, directions.get(k));
        if(v.row() < ants.rows() && v.col() < ants.cols() && v.row() >= 0 && v.col() >= 0)
          if( (ants.ilk(v).isUnoccupied() || ants.ilk(v).isFood() || ants.ilk(v).hisAnt()) && map[v.row()][v.col()].c == 0 ){
            map[v.row()][v.col()].dist = map[u.row()][u.col()].dist + 1;
            map[v.row()][v.col()].p = u;
            map[v.row()][v.col()].c = 1;
            q.add(q.size(), v);
          }
      }
      map[u.row()][u.col()].c = 2;
      q.remove(0);
    }
    return start; 
  }

  public Tile BFSexplore(Tile start, Ants ants){
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    Tile map[][] = new Tile[ants.rows()][ants.cols()];
    ArrayList<Tile> q = new ArrayList();
    for(int i = 0 ; i < ants.rows() ; i++)
      for(int j = 0 ; j < ants.cols() ; j++){
        map[i][j] = new Tile(i, j);
        map[i][j].p = null;
        map[i][j].dist = 100000;
        map[i][j].c = 0;
      }
    map[start.row()][start.col()].dist = 0;
    q.add(start);
    map[start.row()][start.col()].c = 1;
    while(q.size()!= 0){
      Tile u = q.get(0);
      if( ants.mapVisit[u.row()][u.col()] == 0){
        while(true){
          if( ( map[u.row()][u.col()].p.row() == start.row()) && map[u.row()][u.col()].p.col() == start.col())
            return u;
          u = map[u.row()][u.col()].p;
          }
      }
      for(int k = 0 ; k < 4 ; k ++){
        Tile v = ants.tile(u, directions.get(k));
        if(v.row() < ants.rows() && v.col() < ants.cols())
          if( (ants.ilk(v).isUnoccupied() || ants.ilk(v).isFood() || ants.ilk(v).hisAnt()) && map[v.row()][v.col()].c == 0 ){
            map[v.row()][v.col()].dist = map[u.row()][u.col()].dist + 1;
            map[v.row()][v.col()].p = u;
            map[v.row()][v.col()].c = 1;
            q.add(q.size(), v);
          }
      }
      map[u.row()][u.col()].c = 2;
      q.remove(0);
    }
    return start; 
  }

  /* testeaza daca exista drum intre t1 si t2 */
  public boolean drum(Ants ants, Tile t1, Tile t2){
    if(t1.row() == t2.row() && t1.col() == t2.col())
      return true;
    List<Aim> dirs  = ants.directions(t1, t2);
    for(int i = 0 ; i < dirs.size() ; i ++){
      Tile destination = ants.tile(t1, dirs.get(i));
      if(ants.ilk(destination).isUnoccupied() || ants.ilk(destination).isFood()) {
        return drum(ants, destination, t2);
      }
    }
    return false;
  }

  /* testeaza daca ants este cea mai apropiata furnica de food */
  public boolean aproapeAnts(Ants ants, Tile food, Tile location){
    Set<Tile> myants = ants.myAnts();
    Iterator <Tile>it = myants.iterator();
    List<Tile> list = new ArrayList();
    while(it.hasNext()){
      Tile t = it.next();
      list.add(t);
    }
    Collections.sort(list, new MyComp(food, ants));
    if(list.get(0).row() == location.row() && list.get(0).col() == location.col())
      return true;
    return false;
  }

  /* muta furnicile de la location spre min_loc */
  public boolean go_hill(Ants ants, Set<Tile> destinations, Tile location, Tile min_loc){
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    boolean issued = false;
    if(min_loc.row() != 0 || min_loc.col() != 0) {
      List<Aim> dirs  = ants.directions(location, min_loc);
      for(int i = 0 ; i < dirs.size() ; i ++){
        Tile destination = ants.tile(location, dirs.get(i));
        if( (ants.ilk(destination).isUnoccupied() || ants.ilk(destination).hisAnt()) && !destinations.contains(destination)) {
          ants.issueOrder(location, dirs.get(i));
          destinations.add(destination);
          issued = true;
          break;
        }
      }
  }
  return issued;
 }

  public Tile go_safe(Ants ants, Tile ant, Tile his) {
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    for(int k = 0 ; k < 4 ; k ++) {
      int ok = 1;
      Tile sim = ants.tile(ant, directions.get(k));
      if(sim.row() < ants.rows() && sim.col() < ants.cols() && sim.row() >=0 && sim.col() >= 0) {
        if(ants.ilk(sim).isPassable()) {
          for(int k1 = 0 ; k1 < 4 ; k1 ++) {
            Tile his_sim = ants.tile(his, directions.get(k1));
            if(sim.row() < ants.rows() && sim.col() < ants.cols() && sim.row() >=0 && sim.col() >= 0) {
              if(ants.ilk(sim).isPassable()) {
                if(ants.isAlphaDist(sim, his_sim))
                  ok = 0;
              }
            }
          }
        }
      }
      if(ok == 1)
        return sim;
    }
    return ant;
  }

  public boolean inSafe(Ants ants, Tile ant, Tile his) {
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    for(int k = 0 ; k < 4 ; k ++) {
      Tile sim = ants.tile(his, directions.get(k));
        if(sim.row() < ants.rows() && sim.col() < ants.cols() && sim.row() >=0 && sim.col() >= 0) {
          if(ants.ilk(sim).isPassable()) {
            if(ants.isAlphaDist(ant, sim))
              return false;
          }
        }
    }
    return true;
  }

  /*muta un pas de la location la to */
  public boolean move_step_safe(Ants ants, Set<Tile> destinations, Tile location, Tile to){
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    boolean issued = false;
    List<Aim> dirs  = ants.directions(location, to);
    for(int i = 0 ; i < dirs.size() ; i ++){
      Tile destination = ants.tile(location, dirs.get(i));
      Tile his = BFShisant(destination, ants);
      if( (ants.ilk(destination).isUnoccupied() || ants.ilk(destination).hisAnt()) && !destinations.contains(destination)
         && inSafe(ants, destination, his)) {
        ants.issueOrder(location, dirs.get(i));
        destinations.add(destination);
        issued = true;
        break;
      }
    }
    return issued;
  }

  /*muta un pas de la location la to */
  public boolean move_step(Ants ants, Set<Tile> destinations, Tile location, Tile to){
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    boolean issued = false;
    List<Aim> dirs  = ants.directions(location, to);
    for(int i = 0 ; i < dirs.size() ; i ++){
      Tile destination = ants.tile(location, dirs.get(i));
      if( (ants.ilk(destination).isUnoccupied() || ants.ilk(destination).hisAnt()) && !destinations.contains(destination)) {
        ants.issueOrder(location, dirs.get(i));
        destinations.add(destination);
        issued = true;
        break;
      }
    }
    return issued;
  }

  public boolean go_randomAnts(Ants ants, Set<Tile> destinations, Tile location, Tile hill){	
    boolean issued = false;
    List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
    if(issued == false){
      Collections.shuffle(directions, new Random(ants.player_seed()));
      for (Aim direction : directions) {
        Tile destination = ants.tile(location, direction);
        if(ants.ilk(destination).isUnoccupied() && !destinations.contains(destination) &&
           (destination.row() <= hill.row() + 5 && destination.row() >= hill.row() - 5) &&
           (destination.col() <= hill.col() + 5 && destination.col() >= hill.col() - 5)) {
          ants.issueOrder(location, direction);
          destinations.add(destination);
          issued = true;
          ants.mapVisit[destination.row()][destination.col()] = 1;
          break;
        }
      }
    }
    return issued;
  }

  public void do_turn(Ants ants) {
    Set<Tile> opHill = ants.opHill();
    List<Tile> opHillList = ants.opHillList();
    int GO = 0;
    Set<Tile> destinations = new HashSet<Tile>();
    Set<Tile> myHill = ants.myHillList();
    List<Tile> myHillList = new ArrayList();  //lista cu musuroaiele mele
    Iterator <Tile> it_hill = myHill.iterator();
    while(it_hill.hasNext()) {
      Tile t = it_hill.next();
      myHillList.add(t);
    }//am myhillList

    List<Tile> attackOponents = new ArrayList();  //pun furnicile potential atacatoare
    for (Tile tile : ants.enemyAnts()) {
      if(myHillList.size() == 2) {
        if(ants.my_distance(tile, myHillList.get(0)) < 20 || 
           ants.my_distance(tile, myHillList.get(1)) < 20 ||
           (((tile.col() <= myHillList.get(0).col()+10) && (tile.col() >= myHillList.get(0).col()-10)) &&
            (myHillList.get(0).row() + ants.rows() - tile.row()) <= 10) ||
           (((tile.col() <= myHillList.get(1).col()+10) && (tile.col() >= myHillList.get(1).col()-10)) &&
            (myHillList.get(1).row() + ants.rows() - tile.row()) <= 10))
          attackOponents.add(tile);
      }
      else {
        if(ants.my_distance(tile, myHillList.get(0)) < 10)
          attackOponents.add(tile);
      }
    }//am furnici potential atacatoare;

    try{  //verific daca am ocupat musuroi
    for(Tile location : ants.myAnts()){
      if(location.row() == opHillList.get(0).row() && location.col() == opHillList.get(0).col()){
        ants.atention(opHillList.get(0)); //atentionez
        opHillList.remove(0); //scot din lista de musuroaie
      }
      else if(location.row() == opHillList.get(1).row() && location.col() == opHillList.get(1).col()){
        ants.atention(opHillList.get(1));
        opHillList.remove(1);
      }
    }
    }
    catch(Exception e){}
    
    List<Tile> myAnts = new ArrayList();
    Iterator<Tile> it_ants = ants.myAnts().iterator();
    while(it_ants.hasNext()) {
      Tile t = it_ants.next();
      myAnts.add(t);
    }//myants
    List<Tile>enemyAnts = new ArrayList();
    Iterator<Tile> his_ants = ants.enemyAnts().iterator();
    while(his_ants.hasNext()) {
      Tile t = his_ants.next();
      enemyAnts.add(t);
    }//enemies

    //updatez harta
    int mapSum = 0;
    for(int i = 0 ; i < ants.rows() ; i ++)
      for(int j = 0 ; j < ants.cols() ; j ++)
        mapSum = mapSum + ants.mapVisit[i][j];
    if(mapSum >=  ants.rows()*ants.cols()-100)
      for(int i = 0 ; i < ants.rows() ; i ++)
        for(int j = 0 ; j < ants.cols() ; j ++)
          ants.mapVisit[i][j] = 0;

    for(int l = 0 ; l < enemyAnts.size() ; l ++) {
      for(int i = enemyAnts.get(l).row() - 4 ; i <= enemyAnts.get(l).row() + 4 ; i ++)
        for(int j  = enemyAnts.get(l).col() - 4  ; j <= enemyAnts.get(l).col() + 4 ; j++)
          if ( i >= 0 && i < ants.rows() && j >= 0 && j < ants.cols())
            ants.mapVisit[i][j] = 1;
      ants.mapVisit[enemyAnts.get(l).row()][enemyAnts.get(l).col()] = 1;
    }
    for(int l = 0 ; l < myAnts.size() ; l ++) {
      for(int i = myAnts.get(l).row() - 4 ; i <= myAnts.get(l).row() + 4 ; i ++)
        for(int j  = myAnts.get(l).col() - 4  ; j <= myAnts.get(l).col() + 4 ; j++)
          if ( i >= 0 && i < ants.rows() && j >= 0 && j < ants.cols())
            ants.mapVisit[i][j] = 1;
      ants.mapVisit[myAnts.get(l).row()][myAnts.get(l).col()] = 1;
    }

    List<Tile> my_moves = new ArrayList();
    List<Tile>dumbOp = new ArrayList();
    for(Tile tile : ants.enemyAnts()) {
      if(BFShisant(tile, ants).dist >= 2)
        dumbOp.add(tile);
      }
      List <Tile> moves = new ArrayList();
    for(int a = 0 ; a < dumbOp.size() ; a++) {
      List<Tile>  pants = BFSants(dumbOp.get(a), ants);
      if(pants.size() >=2) {
          moves = max(0, ants, pants, dumbOp.get(a));
        }
        simulates = new ArrayList();
        saves = new ArrayList();
        bestValue = -10000;
        my_moves.addAll(moves);
    }

    for(Tile location : ants.myAnts()) {
      Tile min_op = new Tile(0, 0);
      Tile min_loc = new Tile(0, 0);
      Tile min_safe = new Tile(0, 0);
      Tile min_atack = new Tile(0, 0);
      List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
      boolean issued = false;

      //caut mancare
      min_loc = BFSfood(location,  ants);
      if(!(min_loc.row() == 0 && min_loc.col() ==0))
        issued = true;

      //daca furnica location e optima pentru una dintre furnicile atacatoare
      if(attackOponents.size() != 0 && issued == false) {
        Collections.sort(attackOponents, new MyComp(location, ants));
        for(int i = 0 ; i < attackOponents.size() ; i ++) {
          Tile t = attackOponents.get(i);
          if(aproapeAnts(ants, t, location) == true){
            min_op = new Tile(t.row(), t.col());
            issued = true;
            break;
          }
        }
      }

      //atac furnici
      if(issued == false)
        for(int a = 0 ;a < my_moves.size() ; a++)
          if(location.row() == my_moves.get(a).parent.row() && location.col() == my_moves.get(a).parent.col()) {
            min_atack = my_moves.get(a);
            issued = true;
            break;
          }

      //mut in siguranta
      if(issued == false) {
        Tile his = BFShisant(location, ants);
        if(inSafe(ants, location, his) == false) {
          min_safe = go_safe(ants, location, his);
          issued = true;
        }
      }

      issued = false;
      if(opHillList.size() != 0 && myAnts.size() > enemyAnts.size() + 20 && GO == 0)
        GO = 1; //activeaza atacul dupa ce vede musuroaiele si trec 700 ture
      if(GO == 0){  //aparare agresiva, inmultire aparare zonala si explorare harta
        //culeg mancare
        if(issued == false){
          if(!(min_loc.row() == 0 && min_loc.col() ==0)){
            issued = move_step(ants, destinations, location, min_loc);
          }
        }

        //aparare agresiva
        if(issued == false) {
          if((min_op.row() != 0 ) && min_op.col() != 0) {
            Tile v = BFS(location, min_op, ants);
            issued = move_step(ants, destinations, location, v);
          }
        }

        //aparare in zona musuroaielor proprii
        if(myAnts.size() >= 100) {
          Collections.sort(myAnts, new MyComp(myHillList.get(0), ants));
          for(int i = 0 ; i <= 5 ; i ++) {
            if(location.row() == myAnts.get(i).row() && location.col() == myAnts.get(i).col())
              if(issued == false) {
                issued = go_randomAnts(ants, destinations, location, myHillList.get(0));
                issued = true;
              }
          }
          if(myHillList.size() == 2) {
            Collections.sort(myAnts, new MyComp(myHillList.get(1), ants));
            for(int i = 0 ; i <= 5 ; i ++) {
              if(location.row() == myAnts.get(i).row() && location.col() == myAnts.get(i).col())
                if(issued == false) {
                  issued = go_randomAnts(ants, destinations, location, myHillList.get(1));
                  issued = true;
                }
            }
          }
        }

        //atac
        if(issued == false) {
          if((min_atack.row() != 0 ) && min_atack.col() != 0) {
            issued = move_step(ants, destinations, location,min_atack);
          }
        }

        //muta in siguranta
        if(issued == false) {
          if((min_safe.row() != 0 ) && min_safe.col() != 0) {
            issued = move_step_safe(ants, destinations, location, min_safe);
          }
        }

        //explorare adevarata
        if(issued == false) {
          Tile v = BFSexplore(location, ants);
          issued = move_step_safe(ants, destinations, location, v);
          if(issued == true)
            ants.mapVisit[v.row()][v.col()] = 1;
        }
      }
      else{
        //aparare agresiva, inmultire, aparare in zona, atac la musuroaie adversar
        //culeg mancare
        if(issued == false){
          if(!(min_loc.row() == 0 && min_loc.col() ==0)){
            issued = move_step(ants, destinations, location, min_loc);
          }
        }

        //aparare agresiva
        if(issued == false) {
          if((min_op.row() != 0 ) && min_op.col() != 0) {
            Tile v = BFS(location, min_op, ants);
            issued = move_step(ants, destinations, location, v);
          }
        }

        //aparare in zona
        if(myAnts.size() >= 70) {
          Collections.sort(myAnts, new MyComp(myHillList.get(0), ants));
          for(int i = 0 ; i <= 5 ; i ++) {
            if(location.row() == myAnts.get(i).row() && location.col() == myAnts.get(i).col())
              if(issued == false) {
                issued = go_randomAnts(ants, destinations, location, myHillList.get(0));
                issued = true;
              }
          }
          if(myHillList.size() == 2) {
            Collections.sort(myAnts, new MyComp(myHillList.get(1), ants));
            for(int i = 0 ; i <= 5 ; i ++) {
              if(location.row() == myAnts.get(i).row() && location.col() == myAnts.get(i).col())
                if(issued == false) {
                  issued = go_randomAnts(ants, destinations, location, myHillList.get(1));
                  issued = true;
                }
            }
          }
        }

        //daca este un singur musuroi se duce dupa el
        if(opHillList.size() == 1) {
          if((issued == false) ){
            if(ants.my_distance(location, opHillList.get(0)) < 60){
              Tile v = BFS(location, opHillList.get(0), ants);
              issued = move_step(ants, destinations, location, v);
            }
            else
              issued = go_hill(ants, destinations, location, opHillList.get(0));  
          }
        }
        //daca sunt 2 musuroaie se duce la cel mai apropiat
        else if(opHillList.size() == 2){
          int d0 = ants.my_distance(location, opHillList.get(0));
          int d1 = ants.my_distance(location, opHillList.get(1));
          if(d0 < d1){
            if((issued == false) ){
              if(ants.my_distance(location, opHillList.get(0)) < 60){
                Tile v = BFS(location, opHillList.get(0), ants);
                issued = move_step(ants, destinations, location, v);
              }
              else
                issued = go_hill(ants, destinations, location, opHillList.get(0));
            }
          }
          else{
            if((issued == false) ){
              if(ants.my_distance(location, opHillList.get(1)) < 60){
                Tile v = BFS(location, opHillList.get(1), ants);
                issued = move_step(ants, destinations, location, v);
              }
              else
                issued = go_hill(ants, destinations, location, opHillList.get(1));
              }
          }
        }
        //muta in siguranta
        if(issued == false) {
          if((min_safe.row() != 0 ) && min_safe.col() != 0) {
            issued = move_step_safe(ants, destinations, location, min_safe);
          }
        }
        //explorare adevarata
        if(issued == false) {
          Tile v = BFSexplore(location, ants);
          issued = move_step_safe(ants, destinations, location, v);
          if(issued == true)
            ants.mapVisit[v.row()][v.col()] = 1;
        }
      }

      if(!issued) {
        destinations.add(location);
      }
    }
  }
}
