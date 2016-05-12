import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

public class Main
{
	static Vector<Location> places = new Vector<Location>(0);
	static Vector<Person> NPCs = new Vector<Person> (0);
	static int time_of_day = 0;
	
	public static void main(String [ ] args)
	{
		Game_Initializer.Initialize(places,NPCs);
		/////Player
		Player_Character The_Player = new Player_Character(new Start_Location());
				
		new helpers(The_Player);
		
		while(true)
		{
			The_Player.InteractWithEnvironment();
			for(int x = 0; x < NPCs.size(); x++)
				NPCs.get(x).Act();
			time_of_day++;
			while(time_of_day>23)
			{
				time_of_day
			}
		}
		
	}
}

