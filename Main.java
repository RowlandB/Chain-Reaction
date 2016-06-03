import java.util.Random;
import java.util.Vector;


public class Main
{
	
	public static void main(String [ ] args)
	{
		My_Game_Initializer blarg = new My_Game_Initializer();
		
		
		blarg.Play_Game();
	}
}

class My_Game_Initializer extends Game_Initializer
{
	public void Initialize(Vector<Location> places, Vector<Person> NPCs)
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
			Person Generic_Peasant = new Bob(Bobs_Fields, Bobs_Hovel);
			Bobs_Fields.AddPerson(Generic_Peasant);
			NPCs.add(Generic_Peasant);
			
			Person Generic_Noble = new Noble_Kesh(Keshies_Castle);
			Keshies_Castle.AddPerson(Generic_Noble);
			NPCs.add(Generic_Noble);
			
			Person Angry_Peasant = new Norton(Tavern);
			Tavern.AddPerson(Angry_Peasant);
			NPCs.add(Angry_Peasant);
			
			Person Hank = new Noble_Hank(Keshies_Castle);
			Keshies_Castle.AddPerson(Hank);
			NPCs.add(Hank);
	}

	public Player_Character new_Player()
	{
		return new Player_Character(new Start_Location());
	}
}

class Bob extends Commoner
{
	Bob(Location L, Location new_home)
	{
		super(L,"Bob");
		
		home = new_home;
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
		
		class steal_wine extends NPC_Action
		{
			steal_wine()
			{
				description = " does nothing suspicious";
				likelihood = 1;
			}
			
			public void What_Happens()
			{
				//TODO actually do it
			}
			
		}
		
		personal_greeting = "'allo, govna'";
	}
	
}

class Norton extends Commoner
{
	Norton(Location L)
	{
		super(L,"Norton");
		
		this.mood=Mood.angry;
		this.pc_friendlyness_level=-1;
		
		personal_greeting = "hey, you git";
	}	
}


class Noble_Kesh extends Noble
{
	Noble_Kesh(Location L)
	{
		super(L,"High Lord Kesh of No-Funnington");
		//TODO: add some knowledge so that it doesn't error
		knowledge_base.add(new boring_fact("goblins","There are goblins in the mountains. It's as good a plot hook as any."));
		knowledge_base.add(new Dungeon_Fact());
	
		personal_greeting = "greetings, peasant";
	}
}

class Noble_Hank extends Noble
{
	Noble_Hank(Location L)
	{
		super(L, "Hank");
		
		knowledge_base.addElement(new Hank_likes_wine());
		
		personal_greeting = "Do you have any wine?";
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
		options.add(new steal_wine());
	}
	
	class steal_wine extends Action
	{
		steal_wine()
		{
			description = "steal some wine";
		}
		
		public void What_Happens()
		{
			helpers.get_PC().add_item(new wine());
		}
		
		//TODO add a can_be_done based on a new PC mechanic 'stealth'
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
		
		public boolean can_be_done()
		{
			return helpers.get_PC().Player_is_Weak();
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



class Dungeon_Fact extends Fact
{
	Dungeon_Fact()
	{
		super("dungeons","We have a dungeon. You should visit it some time.");
	}
	
	public void on_learn()
	{
		helpers.get_PC().learn_about_location("Dungeon Below Keshie's Castle", 25);
	}
}

class boring_fact extends Fact
{
	boring_fact(String descr, String hf)
	{
		super(descr,hf);
	}
}

class Hank_likes_wine extends Fact
{
	Hank_likes_wine()
	{
		super("wine", "Hank *really* likes wine. You could say he has a hankering for it.");
	}
	
	class give_Hank_wine extends Action
	{
		give_Hank_wine()
		{
			description = "give Hank some wine";
		}
		
		public void What_Happens()
		{
			//player loses wine
			helpers.get_PC().decrement_consumable("wine");
			
			helpers.Get_Person_by_name("Hank").change_friendliness(20);
			

			helpers.get_PC().remove_action(myself);
		}
		
		public boolean can_be_done()
		{
			boolean has_wine = helpers.get_PC().has_item("wine");
			if(has_wine)
			{
				boolean same_location = helpers.get_PC().Get_Location().has_present("Hank");
				if(same_location)
				{
					return true;
				}
			}
			
			return false;
		}
		
		Action myself = this;
	}
	
	
	public void on_learn()
	{
		helpers.get_PC().add_action(new give_Hank_wine());
	}
}

class wine extends consumable_item
{
	wine()
	{
		ability = new get_drunk();
		rules_description = ability.description;

		name = "wine";
		flavor_text = "well, it's better than water";
		weight = 0;
		value = 10;
	}

	class get_drunk extends consume
	{
		get_drunk()
		{
			description = "you drink, and you get drunk";
		}

		protected void Other_Happenings()
		{
			// TODO assume the character is a beastly god among men who can't get drunk
		}
	}
}

