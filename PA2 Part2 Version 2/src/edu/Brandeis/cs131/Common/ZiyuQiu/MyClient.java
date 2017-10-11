package edu.Brandeis.cs131.Common.ZiyuQiu;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Industry;

public abstract class MyClient extends Client {

    public MyClient(String name, Industry industry) {
    	//the speed is randomly created between 0 and 9
    		//int speed = (int)(Math.random() * 9);
    	//client starts with level 3
        	//int requestLevel = 3;
    	super(name, industry, (int)(Math.random() * 9), 3);  
    }
}
