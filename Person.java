import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

abstract class  Person
{
	
	Person(Location L, String S)
	{
		place = L;
		name = S;

		knowledge_base = new Vector<Fact>();
		knowledge_base.add(new boring_fact("done","I have nothing else interesting to talk about"));
		
		potential_actions = new ArrayList<NPC_Action>();
		potential_actions.add(new does_nothing());
		
		location_knowledge = new mobility_controller();
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
		if(difference > 0)
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
		boolean still_talking = true;
		
		while(stay && still_talking)
		{
			for(int x = 0; x < knowledge_base.size(); x++)
			{
				int y = x + 1;
				//TODO: check if the NPC would share this
				
/*				if(knowledge_base.get(x).is_sharable())
				{
					
				}
*/				
				String output = Integer.toString(y) + ") " + knowledge_base.get(x).Get_Description();
				helpers.output(output);
			}
			helpers.finish_output();
			
			int answer = helpers.which_one(knowledge_base.size());
			
			if(knowledge_base.get(answer).Get_Description()=="done")
			{
				still_talking = false;
			}
			else
			{
				
				helpers.output(knowledge_base.get(answer).Get_Fact());
				helpers.get_PC().add_fact(knowledge_base.get(answer));
			}
		}
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
		//'decide' on an action
			//for now, always do nothing
		Action the_action = find_best_action();

		//do the action
		the_action.What_Happens();
		
		//check if the player is in the same location
			//notify player
		if(helpers.get_PC().Get_Location().Where() == this.place.Where())
		{
			helpers.output(the_action.description);
		}
		
	}
	
	
	private Action find_best_action() 
	{
		int action_location = 0;
		int action_value = -1000;
		for(int x=0; x<potential_actions.size(); x++)
		{		
			if(potential_actions.get(x).likelihood + helpers.random(-5, 5) > action_value)
			{
				action_value = potential_actions.get(x).likelihood;
				action_location = x;
			}
		}
		
		return potential_actions.get(action_location);
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
	
	
	class NPC_Action extends Action
	{
		
		protected int likelihood;
	}
	
	class does_nothing extends NPC_Action
	{
		does_nothing()
		{
			String name = Get_Name();
			description =  name + " does nothing";
			likelihood = 5;
		}
		
		public void What_Happens()
		{
			
		}
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
	protected ArrayList<NPC_Action> potential_actions;
	protected mobility_controller location_knowledge;
}

abstract class Commoner extends Person
{
	Commoner(Location Start, String their_name)
	{
		super(Start, their_name);
		mood = Mood.angry;
	}
	
}

abstract class Noble extends Person
{
	Noble(Location Start, String their_name)
	{
		super(Start, their_name);
		mood = Mood.ambivalent;
	}
	
}

class Bob extends Commoner
{
	Bob(Location L, String N)
	{
		super(L,N);
		//TODO: add some knowledge so that it doesn't error
		
		class become_single_minded extends NPC_Action
		{
			become_single_minded()
			{
				String name = Get_Name();
				description =  name + " becomes more single-minded";
				likelihood = 1;
			}
			
			public void What_Happens()
			{
				this.likelihood++;
			}
		}
	
		this.potential_actions.add(new become_single_minded());
	}
}

class Noble_Kesh extends Noble
{
	Noble_Kesh(Location L, String N)
	{
		super(L,N);
		//TODO: add some knowledge so that it doesn't error
		knowledge_base.add(new boring_fact("goblins","There are goblins in the mountains. It's as good a plot hook as any."));
		knowledge_base.add(new Dungeon_Fact("dungeons","We have a dungeon. You should visit it some time."));
	}
	
}


