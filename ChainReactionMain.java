import java.util.LinkedList;
import java.util.Vector;
import java.util.Scanner;

public class Main
{
	static Vector<Location> places = new Vector<Location>(0);
	static Vector<Person> NPCs = new Vector<Person> (0);
	
	public static void main(String [ ] args)
	{

		/////Locations
		
		Location SL = new Start_Location();
		Location Blacksmith = new Forge("Blacksmith's");
		Location Bobs_Fields = new Field("Bob's Field");
		Location Keshies_Castle = new Castle("Keshie's Castle");
		places.add(Blacksmith);
		places.add(Bobs_Fields);
		places.add(Keshies_Castle);
		
		
		/////Player
		Player_Character Bob = new Player_Character(SL);
		

		
		////Persons
		Person Generic_Peasant = new Commoner(Bobs_Fields);
		Bobs_Fields.AddPerson(Generic_Peasant);
		NPCs.add(Generic_Peasant);
		
		Person Generic_Noble = new Noble(Keshies_Castle);
		Keshies_Castle.AddPerson(Generic_Noble);
		NPCs.add(Generic_Noble);
		
		
		//TODO: initialize all the NPCs
		
		while(true)
		{
			Bob.InteractWithEnvironment();
			for(int x = 0; x < NPCs.size(); x++)
				NPCs.get(x).Act();
		}
		
	}
}

///////////////////////////////
class Player_Character
{
	
	public  Player_Character(Location Start)
	{
		IO = new System_IO_Object();
		
		this.what_do = new Vector<Action>(0);			//TODO: set an actual length

		what_do.add(new LookAroundtheRoom());
		what_do.add(new Move());
		
		//TODO: initilize each Action
		
		//view inventory
		//use item in inventory
		
		
		this.here = Start;	
	}
	
	
	
	
	public void InteractWithEnvironment()
	{
		int num_player_actions = what_do.size();
		for(int x = 0; x < num_player_actions; x++)
		{
			int y = x+1;
			String output = y +") " + what_do.elementAt(x).description;
			IO.Output_String(output);
		}
		
		Vector<Action> other_options = here.Get_Actions();
		
		for(int z = 0; z < other_options.size(); z++)
		{
			int y = z+1+what_do.size();
			String output = y +") " + other_options.elementAt(z).description;
			IO.Output_String(output);
		}
		
		String player_choice = IO.Input_String();
		
		int answer = Integer.parseInt(player_choice);
		
		if(answer < 1 || answer > (num_player_actions + other_options.size()))
		{
			IO.Output_String("Did you mis-type?");
		}
		
		if(answer <= num_player_actions)
		{
			this.what_do.get(answer-1).What_Happens();
		}
		else
		{
			here.Get_Actions().get(answer-1-num_player_actions).What_Happens();
		}
		
	}
	
	
	
	class LookAroundtheRoom extends Action
	{
		LookAroundtheRoom()
		{
			description = "Check out who's near";
		}
		
		public void What_Happens()
		{
			String people;
			LinkedList<Person> everyone =  here.GetEveryone();
			if(everyone.size() == 0)
			{
				people = "There's no one here but you!";
				IO.Output_String(people);
			}
			else
			{
				IO.Output_String("Present is:");
				for(int x = 0; x <everyone.size(); x++)
				{
					IO.Output_String(everyone.get(x).Get_Name());
				}
			}

		}		
	}
	
	class Move extends Action
	{
		Move()
		{
			description = "Go someplace else";
		}
		
		public void What_Happens()
		{
			IO.Output_String("Where?");
			
			for(int x = 0; x<Main.places.size(); x++)
			{
				int y = x+1;
				String output = y + ") " + Main.places.get(x).Where();
				IO.Output_String(output);
			}
			
			String input = IO.Input_String();
			
			int answer = Integer.parseInt(input);
			
			if(answer < 1 || answer > Main.places.size())
			{
				IO.Output_String("Did you mis-type?");
			}
			else
			{
				Move_Character(Main.places.get(answer-1));
			}
			
			
		}
		
	}
	
	private void Move_Character(Location where_to)
	{
		here = where_to;
	}
	
	private IO_Object IO;
	private Vector<Action> what_do;
	private Location here;
}

///////////////////////////////
class Fact
{
		
		
	
	
	
	
	
}

///////////////////////////////
abstract class Action
{	
	Action()
	{
		IO = new System_IO_Object();
	}
	
	public void What_Happens(){}
	public String description;
	IO_Object IO;
}




///////////////////////////////
abstract class  Person
{
	public  Person()
	{
		name = "Mysterious Stranger with no distinguishing characteristics";
		IO = new System_IO_Object();
	}
	
	public String Get_Name()
	{
		return name;
	}
	
	//Terminating Simple Action
	//Moves the character to a new location
	public void MoveTo(Location new_place)
	{
		place.RemovePerson(this);
		place = new_place;
		new_place.AddPerson(this);
	}
	
	/**Non-Terminating Simple Action
	 * fights another Person*/
	public void Fight(Person opponent)
	{
		int difference = this.fight_power - opponent.fight_power;
		if(difference >0)
		{
			opponent.LoseFight(difference);
		}
		else
		{
			this.LoseFight((difference * -1));
		}
	}
	
	//Non-Terminating, Giving Simple Action
	//Tells everyone in the location a piece of knowledge
	public void Gossip(Fact knowledge)
	{
		LinkedList<Person> everyone =  place.GetEveryone();
		for(int x = 0; x < everyone.size(); x++)
		{
			everyone.get(x).GainInformation(knowledge);
		}
	}
	
	
	public void GainInformation(Fact knowledge)
	{
		knowledge_base.add(knowledge);	
	}

	//Default Action used by Main
	public void Act()
	{
		
		String output = name + " does nothing interesting";
		IO.Output_String(output);
		
	}
	
	
	private void LoseFight(int how_bad)
	{
		if(how_bad > 15)
		{
			//TODO: kill the character
			
		}
		else if(how_bad > 5)
		{
			//TODO: a bit fucked up
			
		}
		else
		{
			//TODO: not much
			
		}
	}
	
	/////////////////////////////
	protected enum Mood
	{
	happy, sad, angry, ambivalent
	}
	
	protected Mood mood;
	protected Vector<Fact> knowledge_base;
	protected  int fight_power;
	protected String name;
	protected Location place;
	protected IO_Object IO;
}

class Commoner extends Person
{
	Commoner(Location Start)
	{
		mood = Mood.angry;
		place = Start;
		name = "Bob, the Blacksmith";	
	}
	
	public void Act()
	{
		
	}

}

class Noble extends Person
{
	Noble(Location Start)
	{
		place = Start;
		name = "The High Lord Kesh of NoFunington";
	}
	
	public void Act()
	{
		
	}
	
}



///////////////////////////////
abstract class Location
{
	Location()
	{
		who_is_here = new LinkedList<Person>();
		options = new Vector<Action>();
	}
	
	public Location(String name)
	{
		
		who_is_here = new LinkedList<Person>();
		options = new Vector<Action>();
		Loc_name = name;
	}
	
	//A location will never remove a person by itself: this should only be called from Person
	public void RemovePerson(Person leaver)
	{
		who_is_here.remove(leaver);		
	}
	
	public void AddPerson(Person arrival)
	{
		who_is_here.add(arrival);
	}
	
	public LinkedList<Person> GetEveryone()
	{
		return who_is_here;
	}
	
	public Vector<Action> Get_Actions()
	{
		return options;
	}
	
	public String Where()
	{
		return Loc_name;
	}
	
	private String Loc_name;
	protected Vector<Action> options;
	protected LinkedList<Person> who_is_here;
}

///////////////////////////////
class Start_Location extends Location
{



}

///////////////////////////////
class Forge extends Location
{
	public Forge(String name)
	{
		super(name);
	} 
	
}

///////////////////////////////
class Field extends Location
{
	public Field(String name)
	{
		super(name);
	}


}

///////////////////////////////
class Castle extends Location
{
	public Castle(String name)
	{
		super(name);
		Action laugh_at_plebs = new oppress_peasants();
		options.add(laugh_at_plebs);
	}

	class oppress_peasants extends Action
	{
			oppress_peasants()
			{
				description = "Oppress some peasants";
				IO = new System_IO_Object();
			}
			
			public void What_Happens()
			{
				IO.Output_String("Haha! Those fools should have been born with money. Let's make them fight for coppers some more!");
			}
		
	}

}


//////////////////////////////
abstract class IO_Object
{
	public String Input_String(){
		return "balls. This shouldn't have been called";
	}
	
	public void Output_String(String output){}
	
}


class System_IO_Object extends IO_Object
{
	System_IO_Object(){}
	
	public String Input_String()
	{
		Scanner keyboard = new Scanner(System.in);
		String the_input = keyboard.nextLine();
		return the_input;
	}
	
	public void Output_String(String output)
	{
		System.out.println(output);
	}
}

