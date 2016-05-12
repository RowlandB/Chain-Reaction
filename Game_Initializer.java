import java.util.Vector;



public class Game_Initializer
{
	static void Initialize(Vector<Location> places, Vector<Person> NPCs)
	{
			//TODO: initialize all the Locations
			/////Locations
			Location Blacksmith = new Forge("Blacksmith's");
			Location Bobs_Fields = new Field("Bob's Field");
			Location Keshies_Castle = new Castle("Keshie's Castle");
			Location Dungeon = new Dungeon("Dungeon Below Keshie's Castle");
			Location Bobs_Hovel = new Hovel("Bob's Hovel");
			Location Tavern = new Tavern("The Tavern");
			places.add(Blacksmith);
			places.add(Bobs_Fields);
			places.add(Keshies_Castle);
			places.add(Dungeon);
			places.add(Bobs_Hovel);
			places.add(Tavern);			
			
			//TODO: initialize all the NPCs
			////Persons
			Person Generic_Peasant = new Bob(Bobs_Fields, "Bob");
			Bobs_Fields.AddPerson(Generic_Peasant);
			NPCs.add(Generic_Peasant);
			
			Person Generic_Noble = new Noble_Kesh(Keshies_Castle, "High Lord Kesh of No-Funnington");
			Keshies_Castle.AddPerson(Generic_Noble);
			NPCs.add(Generic_Noble);
			
			Person Angry_Peasant = new Norton(Tavern, "Norton");
			Tavern.AddPerson(Angry_Peasant);
			NPCs.add(Angry_Peasant);
	}
}

class Bob extends Commoner
{
	Bob(Location L, String N)
	{
		super(L,N);
		//TODO: add some knowledge so that it doesn't error
		
		//proof-of-concept, shows we can make actions that change our actions
		/*
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
		*/
		personal_greeting = "'allo, govna'";
	}
	
}

class Norton extends Commoner
{
	Norton(Location L, String N)
	{
		super(L,N);
		
		this.mood=Mood.angry;
		this.pc_friendlyness_level=-1;
		
		personal_greeting = "hey, you git";
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
	
		personal_greeting = "greetings, peasant";
	}
}

///////////////////////////////
class Start_Location extends Location
{



}

///////////////////////////////
class Forge extends Location
{
	Forge(String name)
	{
		super(name);
	} 

}

///////////////////////////////
class Field extends Location
{
	Field(String name)
	{
		super(name);
	}
	

}

///////////////////////////////
class Hovel extends Location
{
	Hovel(String name)
	{
		super(name);
	}
}

///////////////////////////////
class Tavern extends Location
{
	Tavern(String name)
	{
		super(name);
	}
}


///////////////////////////////
class Castle extends Location
{
	Castle(String name)
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
		}
	
		public void What_Happens()
		{
			helpers.output("Haha! Those fools should have been born with money. Let's make them fight for coppers some more!");
			helpers.finish_output();
		}
	}
}

///////////////////////////////
class Dungeon extends Location
{
	Dungeon(String name)
	{
		super(name);
		accessibility = 20;
	}

}

class health_potion extends consumable_item
{
	health_potion()
	{
		ability = new Gain_HP_hp_pot();
		rules_description = ability.description;

		name = "health potion";
		flavor_text = "a vial filled with a red liquid. It smells of cherries";
		weight = 0;
		value = 150;
	}
	
	class Gain_HP_hp_pot extends consume
	{
		Gain_HP_hp_pot()
		{
			description = "Drink a Potion to restore health";
		}
		
		public void Other_Happenings()
		{
			helpers.PC.gain_health(5);
			helpers.output("You feel your wounds re-knit and close");
			helpers.finish_output();
		}
	}

}
