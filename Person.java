import java.util.LinkedList;
import java.util.Vector;

abstract class  Person
{
	public  Person()
	{
		name = "Mysterious Stranger with no distinguishing characteristics";
		
		knowledge_base = new Vector<Fact>();
		
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
	
	public void Chat_with_PC()
	{
		Chat_Greeting();
		
		boolean stay = true;
		while(stay)
		for(int x = 0; x < knowledge_base.size(); x++)
		{
			int y = x +1;
			String output = Integer.toString(y) + ") " + knowledge_base.get(x).Get_Description();
			helpers.output(output);
		}
		helpers.finish_output();

	
		int answer = helpers.which_one(knowledge_base.size());
		
		//TODO maybe do something depending on how character feels about him
		
	}
	
	private void Chat_Greeting()
	{
		if(pc_friendlyness_level == 0)
		{
			helpers.output("Hello, Stranger.");	
		}
		else if(pc_friendlyness_level < -10)
		{
			helpers.output("You've got some nerve, showing your face around here.");	
		}
		else if(pc_friendlyness_level > 10)
		{
			helpers.output("Hello, my good friend.");	
		}
		else if(pc_friendlyness_level > 0)
		{
			helpers.output("Hello, friend.");
		}
		else if(pc_friendlyness_level < 0)
		{
			helpers.output("[disgruntaled silence].");	
		}
		Personal_Greeting();
		helpers.finish_output();
	}
	
	private void Personal_Greeting()
	{
		helpers.output("I know a few things...");
	}
	
	public void GainInformation(Fact knowledge)
	{
		if(!fact_already_known(knowledge))
			knowledge_base.add(knowledge);	
	}

	//Default Action used by Main
	public void Act()
	{
		
		String output = name + " does nothing interesting";
		helpers.output(output);
		helpers.finish_output();
		
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
	
	private boolean fact_already_known(Fact new_info)
	{
		int the_id = new_info.Get_id();
		for(int x = 0; x < knowledge_base.size(); x++)
		{
			if(knowledge_base.get(x).Get_id() == the_id)
				return true;
		}
		return false;
	}
	
	/////////////////////////////
	protected enum Mood
	{
	happy, sad, angry, ambivalent
	}
	
	protected Mood mood;
	protected int pc_friendlyness_level; //0 = neutral, bigger is friendlier
	protected Vector<Fact> knowledge_base;
	protected  int fight_power;
	protected String name;
	protected Location place;
}

abstract class Commoner extends Person
{
	Commoner(Location Start, String their_name)
	{
		name = their_name;
		mood = Mood.angry;
	}
	
	public void Act()
	{
		
	}

}

abstract class Noble extends Person
{
	Noble(Location Start, String their_name)
	{
		name = their_name;
		mood =Mood.ambivalent;
	}
	
	public void Act()
	{
		
	}
	
}

class Bob extends Commoner
{
	Bob(Location L, String N)
	{
		super(L,N);
		//TODO: add some knowledge so that it doesn't error
	}
	
}

class Noble_Kesh extends Noble
{
	Noble_Kesh(Location L, String N)
	{
		super(L,N);
		//TODO: add some knowledge so that it doesn't error
	}
	
}



