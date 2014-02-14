public class Punt {
    public int x, y, nr;
    public boolean gebruikt;
    
    public Punt(int nr, int x, int y)
    {
        this.nr = nr;
    	this.x = x;
        this.y = y;
        //zit het punt al in het pad?
        this.gebruikt = false;
    }
}


