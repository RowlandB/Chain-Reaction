import java.security.InvalidParameterException;
import java.util.Random;
import java.util.Vector;


abstract public class Game_Initializer
{
	abstract public void Initialize(Vector<Location> places, Vector<Person> NPCs);
	
	
	public Player_Character new_Player()
	{
		return new Player_Character(new Start_Location());
	}
	
	
	final public void Play_Game()
	{
		Vector<Location> places = new Vector<Location>(0);
		Vector<Person> NPCs = new Vector<Person> (0);
		
		this.Initialize(places, NPCs);
		
		Player_Character The_Player = this.new_Player();
		
		new helpers(The_Player, places, NPCs);
		
		while(true)
		{
			The_Player.InteractWithEnvironment();
			helpers.time_passes();
			for(int x = 0; x < NPCs.size(); x++)
				NPCs.get(x).Act();
			
			helpers.increment_time();
		}
	}
}

//////////////////////////////
class helpers
{
	helpers(Player_Character hero, Vector<Location> places, Vector<Person> NPCs)
	{
		time_of_day=12;
		PC = hero;
		IO = new Frame_Button_IO_Object();
		NPC_List = NPCs;
		Location_List = places;
	}
	
	public static void time_passes()
	{
		PC.time_passes();
		for(int x=0; x<Location_List.size(); x++)
		{
			Location_List.get(x).time_passes();
		}
	}

	public static void increment_time()
	{
		time_of_day++;
		if(time_of_day>23)
		{
			time_of_day=time_of_day-24;
		}
	}

	public static int Get_Time()
	{
		return time_of_day;
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
	
	static void output_partial_list(int which, String output)
	{
		IO.Partial_List_Output_String(which, output);
	}
	
	public static void output_partial_list(int which, String output, boolean stolen)
	{
		IO.Partial_List_Output_String(which, output, stolen);
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
	
	//static Vector<Location> Get_Locations()
	//{
	//return Location_List;
	//}
	
	static Location Get_Location_by_name(String Location_Name) throws InvalidParameterException
	{
		for(int x=0; x<Location_List.size(); x++)
		{
			if(Location_List.get(x).Where().equals(Location_Name))
			{
				return Location_List.get(x);
			}
		}
		
		throw new InvalidParameterException();
	}
	
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
		
		private static int time_of_day;
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
	