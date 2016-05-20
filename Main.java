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
		Player_Character The_Player = Game_Initializer.new_Player();
		
		new helpers(The_Player, places, NPCs);
		
		while(true)
		{
			The_Player.InteractWithEnvironment();
			for(int x = 0; x < NPCs.size(); x++)
				NPCs.get(x).Act();
			time_of_day++;
			if(time_of_day>23)
			{
				time_of_day=time_of_day-24;
			}
		}
		
	}
}

//////////////////////////////
class helpers
{
	helpers(Player_Character hero, Vector<Location> places, Vector<Person> NPCs)
	{
		PC = hero;
		IO = new Frame_IO_Object();
		NPC_List = NPCs;
		Location_List = places;
	}

//Assumption: We've already output a question with 'size' number of choices
//Yells at the player until they give a viable option (one from the list)
	static int which_one(int size)
	{
		int x = Integer.parseInt(IO.Input_String());
	
		while(x > size || x < 1)
		{
			helpers.output("That's not a selectable option. Try again");
			helpers.finish_output();
			x = Integer.parseInt(IO.Input_String());
		}
		
		return x - 1;
	}
	
	static String input()
	{
		return IO.Input_String();
	}
	
	static void output(String the_output)
	{
		IO.Output_String(the_output);
	}
	
	static void finish_output()
	{
		IO.Output_String("");
		IO.Output_Batch();
	}
	
	static Player_Character get_PC()
	{
		return PC;
	}
	
	//inclusive
	static int random(int min, int max)
	{
		Random random = new Random();
		return random.nextInt(max-min+1)+min;
	}
	
//	static Vector<Location> Get_Locations()
//	{
//		return Location_List;
//	}
	
	static Person Get_Person_by_name(String NPC_name)
	{
		for(int x=0; x<NPC_List.size(); x++)
		{
			if(NPC_List.get(x).Get_Name().equals(NPC_name))
			{
				return NPC_List.get(x);
			}
		}
		
		System.err.println("specified Person does not exist");
		assert(false);
		return new null_person();
	}
	
	static Player_Character PC;
	static IO_Object IO;
	static Vector<Location> Location_List;
	static Vector<Person> NPC_List;
}

enum equip_region
{
	head, body, ring, amulet, one_handed, two_handed, not_equippable
}


class null_person extends Person
{
	null_person()
	{
		super(new Start_Location(), "null");
	}
}