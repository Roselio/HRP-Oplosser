public class Lijn {
	public Punt p1, p2;
	public double lengte;
	public boolean gebruikt;
	
	public Lijn(Punt p1,Punt p2){
		this.p1 = p1;
		this.p2 = p2;
		//bereken de lengte van de lijn doormiddel van pythagoras
		this.lengte = Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
		//zit de lijn nog in het pad?
		this.gebruikt = true;
	}
}