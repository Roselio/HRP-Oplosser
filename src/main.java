import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class main {
    static int aantalpunten;
    static ArrayList<Punt> punten = new ArrayList<Punt>();
    static int aantalpaden;
    static int[][] paden;
    static double[] lengten;
    static double afstanden[][];
    
    static long tijdBruut;
    static double lengteBruut;
    static long tijdElastiek;
    static double lengteElastiek;
    static long tijdGreedy;
    static double lengteGreedy;
    
    static PrintWriter meting;
    static String naamMeting;
    static int aantalKeerUitvoeren;
    static int totaaluitgevoerd;
    static int offset;
    
    public static void main(String[] args) {
  	 kiesPunten(true,8);
  	 
  		 
   	 System.out.println(aantalpunten + " punten");
   	 
   	 aantalKeerUitvoeren = 0;
  	 totaaluitgevoerd = aantalKeerUitvoeren;
  	 offset = 0;//hoeveel eerdere tests je hebt uitgevoerd
  	 naamMeting = String.format("%02d", aantalpunten)+"p � "+aantalKeerUitvoeren+" +"+offset+" ("+UUID.randomUUID().toString()+")";
  	 File dir = new File("resultaten/"+naamMeting);
  	 dir.mkdir();
   	 
   	 int methode = -1;
   	 if (methode == 0){
   		 bruteforce();
   		 try {
  			  elastiek();
  		  } catch (Exception e) {
  			  e.printStackTrace();
  		  }
   	 }
   	 if(methode == 1){
   		 bruteforce();
   	 }
   	 if(methode == 2){
   		 try {
  			  elastiek();
  		  } catch (Exception e) {
  			  e.printStackTrace();
  		  }
   	 }
   	 if(methode == 3){
  		 greedy();
  	 }
   	 
   	 
   	 
   	 if(aantalKeerUitvoeren>3){
   	 	//printXMLBegin();
   		 printCSVBegin();
   	 	while(aantalKeerUitvoeren>0){
   	 		boolean bezig = true;
   	 		kiesPunten(true,aantalpunten);
   	 		try{
   	 			elastiek();
   	 		}catch(Exception e){
   	 			continue;
   	 		}
   	 		if(bezig == true){
   	 			bruteforce();
   	 			greedy();
   	 			printCSVMeting();
   	 			//printXMLMeting();
   	 			aantalKeerUitvoeren--;
   	 		}
   	 	}
   	 	//printXMLEind();
   	 }
    }
    
    public static void puntenOngebruikt(){
    	for(int p = 0; p < aantalpunten; p++){
    		punten.get(p).gebruikt = false;
    	}
    }
    
    public static void greedy(){
    	puntenOngebruikt();
    	
    	System.out.println("Methode: greedy");
   	 
    	ArrayList<Punt> greedypad = new ArrayList<Punt>();
  	 
    	long start_time = System.nanoTime();//start meting

    	
    	//afstanden tussen alle punten bepalen
    	double[][] afstandTotPunt = new double[aantalpunten][aantalpunten];
    	for(int p1 = 0; p1 < aantalpunten; p1++){
    		for(int p2 = 0; p2 < aantalpunten; p2++){
    			afstandTotPunt[p1][p2] = Math.sqrt((punten.get(p1).x-punten.get(p2).x)*(punten.get(p1).x-punten.get(p2).x)+(punten.get(p1).y-punten.get(p2).y)*(punten.get(p1).y-punten.get(p2).y));
    		}
    	}
    	
    	int huidigPunt = 0;
    	greedypad.add(punten.get(huidigPunt));
    	int besteVolgendePunt = 0;
    	double besteVolgendeAfstand = Double.MAX_VALUE;
    	punten.get(huidigPunt).gebruikt = true;
    	lengteGreedy = 0;
    	
    	int aantalkeer = 0;
    	while(aantalkeer < aantalpunten-1){
	    	for(int p = 1; p < aantalpunten; p++){
	    		if(p != huidigPunt){
		    		if(punten.get(p).gebruikt == false){
			    		if(afstandTotPunt[huidigPunt][p] < besteVolgendeAfstand){
			    			besteVolgendePunt = p;
			    			besteVolgendeAfstand = afstandTotPunt[huidigPunt][p];
			    		}
		    		}
	    		}
	    	}
	    	
	    	lengteGreedy = lengteGreedy + besteVolgendeAfstand;
	    	
	    	huidigPunt = besteVolgendePunt;
	    	greedypad.add(punten.get(huidigPunt));
	    	punten.get(huidigPunt).gebruikt = true;
	    	
	    	besteVolgendeAfstand = Double.MAX_VALUE;
	    	aantalkeer++;
    	}
    	
    	//sluit het pad
    	lengteGreedy = lengteGreedy + afstandTotPunt[0][huidigPunt];
    	
    	long end_time = System.nanoTime();//stop meting
    	System.out.println("tijd: "+(end_time - start_time) +"ns");  
  	    System.out.print("Het pad is:");
  	    for (Punt p : greedypad){
       	   System.out.print(p.nr+" -> ");
  	    }
  	    System.out.println("Lengte pad = "+lengteGreedy);
  	    greedyDiagram(punten, greedypad);
  	    tijdGreedy = end_time - start_time;
    }
    
    public static void elastiek() throws Exception{
    	  puntenOngebruikt();
    	  
    	  System.out.println("Methode: elastiek");
    	 
    	  ArrayList<Punt> buitenpad = new ArrayList<Punt>();
    	 
    	  long start_time = System.nanoTime();//start meting

    	  //kijk wat meest linker punt is
    	  int links = 0;
    	  int beste = punten.get(0).x;
    	  for (int p = 0; p < aantalpunten; p++){
    		  if(beste>punten.get(p).x){
    			  beste = punten.get(p).x;
    			  links = p;
    		  }
    	  }
    	  //kijk wat hoogste punt is
    	  int boven = 0;
    	  beste = punten.get(0).y;
    	  for (int p = 0; p < aantalpunten; p++){
    		  if(beste<punten.get(p).y){
    			  beste = punten.get(p).y;
    			  boven = p;
    		  }
    	  }
    	  //kijk wat meest rechter punt is
    	  int rechts = 0;
    	  beste = punten.get(0).x;
    	  for (int p = 0; p < aantalpunten; p++){
    		  if(beste<punten.get(p).x){
    			  beste = punten.get(p).x;
    			  rechts = p;
    		  }
    	  }
    	  //kijk wat laagste punt is
    	  int onder = 0;
    	  beste = punten.get(0).y;
    	  for (int p = 0; p < aantalpunten; p++){
    		  if(beste>punten.get(p).y){
    			  beste = punten.get(p).y;
    			  onder = p;
    		  }
    	  }
    	 
    	  punten.get(links).gebruikt = true;
    	  punten.get(boven).gebruikt = true;
    	  punten.get(rechts).gebruikt = true;
    	  punten.get(onder).gebruikt = true;
    	 
        
    	  buitenpad.add(punten.get(boven));
    	  punten.get(boven).gebruikt = true;
    	  int vorigPunt = boven;
    	  //van boven naar rechts
    	  double Besterc = Double.NEGATIVE_INFINITY;
    	  int Bestep = -1;
    	  if (rechts == boven){
       	   vorigPunt = rechts;
    	  }
    	  else{
       	   while (vorigPunt != rechts){
       		   for (int p = 0; p < aantalpunten; p++){
       			   if (p == vorigPunt) //kan natuurlijk niet
       			   {continue;}
       			   if (punten.get(p).x < punten.get(vorigPunt).x){ //punt ligt links (niet de volgende)
       				   continue;
       			   }
       			   else if (punten.get(p).x > punten.get(vorigPunt).x){ //punt ligt rechts
       				   double rc = (double)(punten.get(p).y - punten.get(vorigPunt).y)/(double)(punten.get(p).x - punten.get(vorigPunt).x);
       				   if (rc > Besterc){ //punt heeft een hogere (=horizontalere) rc (rc=negatief)
       					   Besterc = rc;
       					   Bestep = p;
       					   continue;
       				   }
       				   else if (rc < Besterc){ //punt heeft een lagere rc (niet de volgende)
       					   continue;
       				   }
       			   }
       			   //Als dit wordt uitgevoerd zit het punt op dezelfde breedte of op Ãƒ ©Ãƒ ©n lijn met een ander punt
       			   throw new Exception("Punt "+p+" Ãƒ ©Ãƒ ©n lijn of op dezelfde breedte als "+vorigPunt+", b->r");
       		   }
       		   buitenpad.add(punten.get(Bestep));
       		   vorigPunt = Bestep;
       		   punten.get(Bestep).gebruikt = true;
    			  Besterc = Double.NEGATIVE_INFINITY;
    			  Bestep = -1;
       	   }
    	  }
        
        
    	  //rechts naar onder
    	  Besterc = Double.NEGATIVE_INFINITY;
    	  Bestep = -1;
    	  if (onder == rechts){
       	   vorigPunt = onder;
    	  }
    	  else{
       	   while (vorigPunt != onder){
       		   for (int p = 0; p < aantalpunten; p++){
       			   if (p == vorigPunt) //kan natuurlijk niet
       			   {continue;}
       			   if (punten.get(p).y > punten.get(vorigPunt).y){ //punt ligt boven (niet de volgende)
       				   continue;
       			   }
       			   else if (punten.get(p).y < punten.get(vorigPunt).y){ //punt ligt onder
       				   double rc = (double)(punten.get(p).y - punten.get(vorigPunt).y)/(double)(punten.get(p).x - punten.get(vorigPunt).x);
       				   if (rc > Besterc){ //punt heeft een hogere (=vertikalere) rc (rc=positief)
       					   Besterc = rc;
       					   Bestep = p;
       					   continue;
       				   }
       				   else if (rc < Besterc){ //punt heeft een lagere rc (niet de volgende)
       					   continue;
       				   }    
       			   }
       			   throw new Exception("Punt "+p+" Ãƒ ©Ãƒ ©n lijn of op dezelfde breedte als "+vorigPunt+", r->o");
       		   }
       		   buitenpad.add(punten.get(Bestep));
       		   vorigPunt = Bestep;
       		   punten.get(Bestep).gebruikt = true;
    			  Besterc = Double.NEGATIVE_INFINITY;
    			  Bestep = -1;
       	   }
    	  }
        
    	  //van onder naar links
    	  Besterc = Double.NEGATIVE_INFINITY;
    	  Bestep = -1;
    	  if (links == onder){
       	   vorigPunt = links;
    	  }
    	  else{
       	   while (vorigPunt != links){
       		   for (int p = 0; p < aantalpunten; p++){
       			   if (p == vorigPunt) //kan natuurlijk niet
       			   {continue;}
       			   if (punten.get(p).x > punten.get(vorigPunt).x){ //punt ligt rechts (niet de volgende)
       				   continue;
       			   }
       			   else if (punten.get(p).x < punten.get(vorigPunt).x){ //punt ligt links
       				   double rc = (double)(punten.get(p).y - punten.get(vorigPunt).y)/(double)(punten.get(p).x - punten.get(vorigPunt).x);
       				   if (rc > Besterc){ //punt heeft een hogere (=horizontalere) rc (rc=negatief)
       					   Besterc = rc;
       					   Bestep = p;
       					   continue;
       				   }
       				   else if (rc < Besterc){ //punt heeft een lagere rc (niet de volgende)
       					   continue;
       				   }
       			   }
       			   throw new Exception("Punt "+p+" Ãƒ ©Ãƒ ©n lijn of op dezelfde breedte als "+vorigPunt+", o->l"); 				 
       		   }
       		   buitenpad.add(punten.get(Bestep));
       		   vorigPunt = Bestep;
       		   punten.get(Bestep).gebruikt = true;
    			  Besterc = Double.NEGATIVE_INFINITY;
    			  Bestep = -1;
       	   }
    	  }
        
    	  //links naar boven
    	  Besterc = Double.NEGATIVE_INFINITY;
    	  Bestep = -1;
    	  if (boven == links){
       	   vorigPunt = links;
    	  }
    	  else{
       	   while (vorigPunt != boven){
       		   for (int p = 0; p < aantalpunten; p++){
       			   if (p == vorigPunt) //kan natuurlijk niet
       			   {continue;}
       			   if (punten.get(p).y < punten.get(vorigPunt).y){ //punt ligt onder (niet de volgende)
       				   continue;
       			   }
       			   else if (punten.get(p).y > punten.get(vorigPunt).y){ //punt ligt boven
       				   double rc = (double)(punten.get(p).y - punten.get(vorigPunt).y)/(double)(punten.get(p).x - punten.get(vorigPunt).x);
       				   if (rc > Besterc){ //punt heeft een hogere (=vertikalere) rc (rc=positief)
       					   Besterc = rc;
       					   Bestep = p;
       					   continue;
       				   }
       				   else if (rc < Besterc){ //punt heeft een lagere rc (niet de volgende)
       					   continue;
       				   }
       			   }
       			   throw new Exception("Punt "+p+" Ãƒ ©Ãƒ ©n lijn of op dezelfde breedte als "+vorigPunt+", l->b");
       		   }
       		   buitenpad.add(punten.get(Bestep));
       		   vorigPunt = Bestep;
       		   punten.get(Bestep).gebruikt = true;
    			  Besterc = Double.NEGATIVE_INFINITY;
    			  Bestep = -1;
       	   }
    	  }
    	  //verbind het buitenpad doormiddel van lijnen
          	ArrayList<Lijn> lijnen = new ArrayList<Lijn>();
          	for(int p = 1; p < buitenpad.size(); p++){
        		  lijnen.add(new Lijn(buitenpad.get(p-1),buitenpad.get(p)));
          	}
         	 
          	//maak het tabel waarin de verschillen tussen de mogelijke volgende punten staan
          	double[][] verschil = new double[aantalpunten][aantalpunten*2];
         	 
          	//bepaal hoeveel punten er over zijn
          	int aantaloverigepunten = 0;
          	for(int p=0; p<aantalpunten; p++){
        		  if(punten.get(p).gebruikt == false){
        			  aantaloverigepunten++;
        		  }
          	}
          	if (aantaloverigepunten != 0)
          	{
         	 
          	//kies het eerste punt en lijn als beste keus
          	int bestep=0;
          	int bestel=0;
          	//vul de regel voor elk punt
          	for(int p = 0; p < punten.size();p++){
        		  //als het punt gebruikt is maak dan het verschil maximaal
        		  if(punten.get(p).gebruikt == true){
        			  for(int l = 0; l < lijnen.size(); l++){
        				  verschil[p][l] = Double.MAX_VALUE;
        			  }
        		  }
        		  //het punt zit nog niet in het pad
        		  else{
        			  for(int l=0; l < lijnen.size(); l++){
        				  double lengteNieuwPadStuk = Math.sqrt((lijnen.get(l).p1.x-punten.get(p).x)*(lijnen.get(l).p1.x-punten.get(p).x)+(lijnen.get(l).p1.y-punten.get(p).y)*(lijnen.get(l).p1.y-punten.get(p).y))+Math.sqrt((lijnen.get(l).p2.x-punten.get(p).x)*(lijnen.get(l).p2.x-punten.get(p).x)+(lijnen.get(l).p2.y-punten.get(p).y)*(lijnen.get(l).p2.y-punten.get(p).y));
        				  verschil[p][l] = lengteNieuwPadStuk - lijnen.get(l).lengte;
        				  //kijk of dit een betere keus is dan die er nu is
        				  if(verschil[p][l]<verschil[bestep][bestel]){
        					  bestep=p;
        					  bestel=l;
        				  }
        			  }
        		  }
          	}
          	//laat het pad via het gekozen punt lopen
          	lijnen.add(new Lijn(lijnen.get(bestel).p1,punten.get(bestep)));
          	lijnen.add(new Lijn(lijnen.get(bestel).p2,punten.get(bestep)));
          	//de oude lijn wordt niet meer gebruikt en dus uitgezet
          	lijnen.get(bestel).gebruikt = false;
          	punten.get(bestep).gebruikt = true;
          	//verwijder de gebruikte lijn uit de tabel
          	for(int p = 0; p<aantalpunten; p++){
        		  verschil[p][bestel] = Double.MAX_VALUE;
          	}
          	//verwijder het gebruikte punt uit de tabel
          	for(int l = 0; l<lijnen.size(); l++){
        		  verschil[bestep][l] = Double.MAX_VALUE;
          	}
          	//bepaal hoeveel punten er over zijn
          	aantaloverigepunten = 0;
          	for(int p=0; p<aantalpunten; p++){
        		  if(punten.get(p).gebruikt == false){
        			  aantaloverigepunten++;
        		  }
          	}

          	while(aantaloverigepunten != 0){
     		  //kies eerste punt en lijn als beste keus
        		  bestep=0;
        		  bestel=0;
        		 
        		  //reken de verschillen met de nieuwe lijnen uit voor elk punt dat niet gebruikt is
        		  for(int p = 0;p<aantalpunten;p++){
        			  if(punten.get(p).gebruikt==true){
        				  continue;
        			  }
        			  else{
        				  //eerste nieuwe lijn
        				  double lengteNieuwPadStuk = Math.sqrt((lijnen.get(lijnen.size()-2).p1.x-punten.get(p).x)*(lijnen.get(lijnen.size()-2).p1.x-punten.get(p).x)+(lijnen.get(lijnen.size()-2).p1.y-punten.get(p).y)*(lijnen.get(lijnen.size()-2).p1.y-punten.get(p).y))+Math.sqrt((lijnen.get(lijnen.size()-2).p2.x-punten.get(p).x)*(lijnen.get(lijnen.size()-2).p2.x-punten.get(p).x)+(lijnen.get(lijnen.size()-2).p2.y-punten.get(p).y)*(lijnen.get(lijnen.size()-2).p2.y-punten.get(p).y));
     				  verschil[p][lijnen.size()-2] = lengteNieuwPadStuk - lijnen.get(lijnen.size()-2).lengte;
     				 
        				  //tweede nieuwe lijn
        				  lengteNieuwPadStuk = Math.sqrt((lijnen.get(lijnen.size()-1).p1.x-punten.get(p).x)*(lijnen.get(lijnen.size()-1).p1.x-punten.get(p).x)+(lijnen.get(lijnen.size()-1).p1.y-punten.get(p).y)*(lijnen.get(lijnen.size()-1).p1.y-punten.get(p).y))+Math.sqrt((lijnen.get(lijnen.size()-1).p2.x-punten.get(p).x)*(lijnen.get(lijnen.size()-1).p2.x-punten.get(p).x)+(lijnen.get(lijnen.size()-1).p2.y-punten.get(p).y)*(lijnen.get(lijnen.size()-1).p2.y-punten.get(p).y));
     				  verschil[p][lijnen.size()-1] = lengteNieuwPadStuk - lijnen.get(lijnen.size()-1).lengte;
        			  }
        		  }
        		 
        		  //kijk wat de beste volgende keus is (gebruikte punten en lijnen worden niet meegeteld
        		  for(int p = 0; p < aantalpunten; p++){
        			  if(punten.get(p).gebruikt == false){
        				  for(int l = 0; l<lijnen.size();l++){
        					  if(lijnen.get(l).gebruikt == true){
        						  if(verschil[p][l]<verschil[bestep][bestel]){
        							  bestep=p;
        							  bestel=l;
        						  }
        					  }
        				  }
        			  }
        		  }
        		 
        		  //laat het pad via het gekozen punt lopen
          	   	lijnen.add(new Lijn(lijnen.get(bestel).p1,punten.get(bestep)));
          	   	lijnen.add(new Lijn(lijnen.get(bestel).p2,punten.get(bestep)));
          	   	//de oude lijn wordt niet meer gebruikt en dus uitgezet
          	   	lijnen.get(bestel).gebruikt = false;
          	   	punten.get(bestep).gebruikt = true;
          	   	//verwijder de gebruikte lijn uit de tabel
          	   	for(int p = 0; p<aantalpunten; p++){
          	 		  verschil[p][bestel] = Double.MAX_VALUE;
          	   	}
          	   	//verwijder het gebruikte punt uit de tabel
          	   	for(int l = 0; l<lijnen.size(); l++){
          	 		  verschil[bestep][l] = Double.MAX_VALUE;
          	   	}
          	  	 
          	   	//bepaal hoeveel punten er over zijn
          	   	aantaloverigepunten = 0;
          	   	for(int p=0; p<aantalpunten; p++){
          	 		  if(punten.get(p).gebruikt == false){
          	 			  aantaloverigepunten++;
          	 		  }
          	   	}
       	}
          	}
         	 
          	//bepaal de lengte van het pad
          	lengteElastiek = 0;
          	for(Lijn l : lijnen){
        		  if(l.gebruikt == true){
        			  lengteElastiek = lengteElastiek + l.lengte;
        		  }
          	}
         	 
          	//maak het pad op volgorde
          	ArrayList<Punt> pad = new ArrayList<Punt>();
          	pad.add(punten.get(0));
          	int vorigpunt = 0;
          	int aantalpuntenverwerkt = 0;
          	boolean zoeken = true;
         	 
           		  	 
          	while(aantalpuntenverwerkt != aantalpunten - 1){
        		  for(int l = 0; l<lijnen.size();l++){
        			  if(zoeken == true){
        				  if(lijnen.get(l).gebruikt == true){
        					  if(lijnen.get(l).p1.nr == vorigpunt){
        						  pad.add(lijnen.get(l).p2);
        						  zoeken = false;
        						  vorigpunt = lijnen.get(l).p2.nr;
        						  lijnen.get(l).gebruikt = false;
        					  }
        				  }
        			  }
        		  }
        		  if(zoeken == true){
        			  for(int l = 0; l<lijnen.size();l++){
        				  if(zoeken == true){
        					  if(lijnen.get(l).gebruikt == true){
        						  if(lijnen.get(l).p2.nr == vorigpunt){
        							  pad.add(lijnen.get(l).p1);
        							  zoeken = false;
        							  vorigpunt = lijnen.get(l).p1.nr;
        							  lijnen.get(l).gebruikt = false;
        						  }
        					  }
        				  }
        			  }
        		  }
        		  aantalpuntenverwerkt++;
        		  zoeken = true;
          	}
         	 
         	 
         	 
         	 
    	  long end_time = System.nanoTime();//stop meting
    	  System.out.println("tijd: "+(end_time - start_time) +"ns");  
    	  System.out.print("Het pad is:");
    	  for (Punt p : pad){
       	   System.out.print(p.nr+" -> ");
    	  }
    	  System.out.println("Lengte pad = "+lengteElastiek);
    	  elastiekDiagram(punten, pad);
    	  tijdElastiek = end_time - start_time;
  	}
    
    public static void bruteforce(){
   	 aantalpaden = faculteit(aantalpunten-1);
   	 afstanden = new double[aantalpunten][aantalpunten];
   	 lengten = new double[aantalpaden];
   	 paden = new int[aantalpaden][aantalpunten];
  	 
  	 
   	 System.out.println("Methode: bruteforce");
   	 System.out.println(aantalpaden + " paden");


  	 
   	 ArrayList<Integer> puntids = new ArrayList<Integer>();
   	 for (int i = 1; i < aantalpunten; i++){
   		 puntids.add(i);
   	 }
   	 long start_time = System.nanoTime();//start meting
   	 for (int i = 0; i < aantalpaden; i++)
   	 {
   		 paden[i][0] = 0;
   	 }
   	 vulLaatsteN(0,puntids);
  	 
   	 int kortstepad_id = -1;
   	 double kortstepad_lengte = Double.MAX_VALUE;
   	 for (int i = 0; i < aantalpaden; i++)
   	 {
   		 lengten[i] = lengteVanPad2(i);
   		 if (lengten[i] < kortstepad_lengte)
   		 {
       		 kortstepad_id = i;
       		 kortstepad_lengte = lengten[i];
   		 }
   	 }
   	 long end_time = System.nanoTime();//stop meting
  	 
  	 
   	 /*for (int i = 0; i < aantalpaden; i++)
   	 {
       		 printPad(i);
   	 }*/
   	 System.out.println("Het kortste pad is: ");
   	 printPad(kortstepad_id);
   	 maakDiagram(kortstepad_id);
   	 System.out.println("tijd: "+(end_time - start_time) +"ns");
   	 tijdBruut = end_time - start_time;
    }
    
    private static void kiesPunten(boolean willekeurig, int aantal){
    	punten.clear();
    	boolean willekeurigepunten = willekeurig;
      	if(willekeurigepunten == true){
      		 aantalpunten = aantal;
      		 Random random = new Random();
      		 for (int i = 0; i<aantalpunten; i++){
          		 punten.add(new Punt(i,random.nextInt(999), random.nextInt(999)));
      		 }
      	 }else{
     		  punten.add(new Punt(0,32,679));	//0 A
     		  punten.add(new Punt(1,925,579));	//1 B
     		  punten.add(new Punt(2,322,161));	//2 C
     		  punten.add(new Punt(3,60,247));	//3 D
     		  punten.add(new Punt(4,638,897));	//4 E
     		  punten.add(new Punt(5,934,316));	//5 F
     		  punten.add(new Punt(6,804,925));	//6 G
     		  punten.add(new Punt(7,517,108));	//7 H

      		 aantalpunten = punten.size();
      	 }
      	 for (int p = 0; p < aantalpunten; p++){
      		 printPunt(p);
      	 }
    }
    
    private static int faculteit(int n) {
   	 return (n == 0) ? 1 : n*faculteit(n-1);
    }
    
    private static void vulLaatsteTwee(int index2, int nm1, int nm2)
    {
   	 paden[index2][aantalpunten-2] = nm1;
   	 paden[index2][aantalpunten-1] = nm2;

   	 paden[index2+1][aantalpunten-2] = nm2;
   	 paden[index2+1][aantalpunten-1] = nm1;
    }
    
    private static void vulLaatsteN(int index, ArrayList<Integer> nog)
    {
   	 int l = nog.size();
   	 if (l==2)
   	 {
   		 vulLaatsteTwee(index, nog.get(0), nog.get(1));
   	 }
   	 else
   	 {
   		 for (int j = 0; j < l; j++)
   		 {
       		 ArrayList<Integer> nog2 = new ArrayList<Integer>(nog);
       		 nog2.remove(j);
       		 int l2 = faculteit(l-1);
       		 for (int i = 0; i < l2; i++)
       		 {
           		 paden[index + l2*j + i][aantalpunten-l] = nog.get(j);
       		 }
       		 vulLaatsteN(index + l2*j, nog2);
   		 }
   	 }
    }
    
    private static double lengteVanPad2(int padnr)
    {
   	 double l = 0;
   	 for (int i=0; i<aantalpunten; i++)
   	 {
   		 //l += afstanden[paden[padnr][Math.min(i, (i+1)%aantalpunten)]][paden[padnr][Math.max(i, (i+1)%aantalpunten)]];
   		 l += afstandTussenPunten(punten.get(paden[padnr][i]), punten.get(paden[padnr][(i+1)%aantalpunten]));
   	 }
   	 return l;
    }
    
    private static double afstandTussenPunten(Punt een, Punt twee)
    {
   	 int dx, dy;
   	 dx = (een.x - twee.x);
   	 dy = (een.y - twee.y);
   	 return Math.sqrt(dx*dx+dy*dy);
    }
    
    private static void printPad(int padnr)
    {
   	 System.out.print("Pad("+padnr+"): {0");
   	 for (int i=1; i<aantalpunten; i++)
   	 {
    		 System.out.print(" -> "+paden[padnr][i]);
   	 }
   	 System.out.println("} (" + lengten[padnr] + ")");
   	 lengteBruut = lengten[padnr];
    }

    private static void printPunt(int puntnr)
    {
   	 System.out.println("Punt "+puntnr+" / "+(char)(puntnr+65)+" (" + punten.get(puntnr).x + ", " + punten.get(puntnr).y+")");    
    }

    private static void maakDiagram(int padnr)
    {
   	 PrintWriter writer;
   	 try {
   		 writer = new PrintWriter("resultaten/"+naamMeting+"/"+String.format("%03d",totaaluitgevoerd+1-aantalKeerUitvoeren+offset)+".b.gml", "UTF-8");
   		 writer.println("Creator \"Koen en Jasper\"");
   		 writer.println("Version \"2.10\"");
   		 writer.println("graph");
   		 writer.println("[");
   		 for (int i=0; i<aantalpunten; i++)
   		 {
       		 writer.println("node [ id "+i+" label \""+i+"\" graphics [ x "+punten.get(i).x+" y "+punten.get(i).y+" ] ]");
   		 }
   		 for (int i=0; i<aantalpunten; i++)
   		 {
       		 writer.println("edge [ source "+paden[padnr][i]+" target "+paden[padnr][(i+1)%aantalpunten]+" graphics [ ] ]");
   		 }
   		 writer.println("]");
  		 
   		 writer.close();
   	 } catch (Exception e) {
   		 e.printStackTrace();
   	 }
    }
   
    private static void elastiekDiagram(ArrayList<Punt> punten, ArrayList<Punt> pad)
	{
   	 PrintWriter writer;
    	try {
        	writer = new PrintWriter("resultaten/"+naamMeting+"/"+String.format("%03d",totaaluitgevoerd+1-aantalKeerUitvoeren+offset)+".e.gml", "UTF-8");
        	writer.println("Creator	\"Koen en Jasper\"");
        	writer.println("Version	\"2.10\"");
        	writer.println("graph");
        	writer.println("[");
        	for (Punt p : punten)
        	{
       		 writer.println("node [ id "+p.nr+" label \""+p.nr+"\" graphics [ x "+p.x+" y "+p.y+" ] ]");
        	}
        	for (int i = 0; i< pad.size(); i++)
        	{
       		 //edge [ source 0 target 2 graphics [ ] ]
       		 writer.println("edge [ source "+pad.get(i).nr+" target "+pad.get((i+1)%pad.size()).nr + " graphics [ ] ]");
        	}
        	writer.println("]");
       	 
        	writer.close();
    	} catch (Exception e) {
        	e.printStackTrace();
    	}
	}
    
    private static void greedyDiagram(ArrayList<Punt> punten, ArrayList<Punt> pad)
	{
   	 PrintWriter writer;
    	try {
        	writer = new PrintWriter("resultaten/"+naamMeting+"/"+String.format("%03d",totaaluitgevoerd+1-aantalKeerUitvoeren+offset)+".e.gml", "UTF-8");
        	writer.println("Creator	\"Koen en Jasper\"");
        	writer.println("Version	\"2.10\"");
        	writer.println("graph");
        	writer.println("[");
        	for (Punt p : punten)
        	{
       		 writer.println("node [ id "+p.nr+" label \""+p.nr+"\" graphics [ x "+p.x+" y "+p.y+" ] ]");
        	}
        	for (int i = 0; i< pad.size(); i++)
        	{
       		 //edge [ source 0 target 2 graphics [ ] ]
       		 writer.println("edge [ source "+pad.get(i).nr+" target "+pad.get((i+1)%pad.size()).nr + " graphics [ ] ]");
        	}
        	writer.println("]");
       	 
        	writer.close();
    	} catch (Exception e) {
        	e.printStackTrace();
    	}
	}
    
    private static void printXMLBegin(){
    	try{
    		meting = new PrintWriter("resultaten/"+naamMeting+"/xml.xml", "UTF-8");
    		meting.println("<?xml version='1.0' encoding='UTF-8'?><?mso-application progid='Excel.Sheet'?><Workbook xmlns='urn:schemas-microsoft-com:office:spreadsheet' xmlns:c='urn:schemas-microsoft-com:office:component:spreadsheet' xmlns:html='http://www.w3.org/TR/REC-html40' xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:ss='urn:schemas-microsoft-com:office:spreadsheet' xmlns:x2='http://schemas.microsoft.com/office/excel/2003/xml' xmlns:x='urn:schemas-microsoft-com:office:excel' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><OfficeDocumentSettings xmlns='urn:schemas-microsoft-com:office:office'><Colors><Color><Index>3</Index><RGB>#c0c0c0</RGB></Color><Color><Index>4</Index><RGB>#ff0000</RGB></Color></Colors></OfficeDocumentSettings><ExcelWorkbook xmlns='urn:schemas-microsoft-com:office:excel'><WindowHeight>9000</WindowHeight><WindowWidth>13860</WindowWidth><WindowTopX>240</WindowTopX><WindowTopY>75</WindowTopY><ProtectStructure>False</ProtectStructure><ProtectWindows>False</ProtectWindows></ExcelWorkbook><ss:Worksheet ss:Name='Blad1'><Table ss:StyleID='ta1'><Column ss:Span='2' ss:Width='64,0063'/>");
    		meting.close();
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private static void printXMLMeting(){
    	try{
    		meting = new PrintWriter(new FileWriter("resultaten/"+naamMeting+"/xml.xml", true));
    		meting.println("<Row ss:Height='12,1039'>");
    			meting.println("<Cell><Data ss:Type='Number'>"+tijdElastiek+"</Data></Cell>");
    			meting.println("<Cell><Data ss:Type='Number'>"+lengteElastiek+"</Data></Cell>");
    			meting.println("<Cell><Data ss:Type='Number'>"+tijdBruut+"</Data></Cell>");
    			meting.println("<Cell><Data ss:Type='Number'>"+lengteBruut+"</Data></Cell>");
    		meting.println("</Row>");
    		meting.close();
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private static void printXMLEind(){
    	try{
    		meting = new PrintWriter(new FileWriter("resultaten/"+naamMeting+"/xml.xml", true));
    		meting.println("</Table><x:WorksheetOptions/></ss:Worksheet></Workbook>");
    		meting.close();
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }


	private static void printCSVBegin()
	{
		if (offset == 0)
		{
			try{
				meting = new PrintWriter("resultaten/"+naamMeting+"/resultaten.csv", "UTF-8");
				meting.println("Meting;Elastiek;;Brute-force;;totaal;;;");
				meting.println(";tijd;lengte;tijd;lengte;verschil tijd (b-e);verschil lengte (e-b);tijd verhouding e:b;lengte verhouding e:b");
				meting.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printCSVMeting()
	{
		try{
			int regelnr = totaaluitgevoerd+(offset==0?3:1)-aantalKeerUitvoeren;
    		meting = new PrintWriter(new FileWriter("resultaten/"+naamMeting+"/resultaten.csv", true));
    		String regel = (totaaluitgevoerd+1-aantalKeerUitvoeren+offset)+";"+tijdElastiek+";"+lengteElastiek+";"+tijdBruut+";"+lengteBruut
    				+";=D"+regelnr+"-B"+regelnr+";=C"+regelnr+"-E"+regelnr+";=B"+regelnr+"/D"+regelnr+";=C"+regelnr+"/E"+regelnr;
    		regel = regel.replace('.', ',');
    		meting.println(regel);
    		meting.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}

}