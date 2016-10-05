import java.util.HashMap;

public class Main
{
	
	public static void main(String [ ] args)
	{
		My_Game_Initializer blarg = new My_Game_Initializer();
		
		blarg.Play_Game();
	}
}

///////////////////////////////
class Bobs_Field extends Fields
{
	Bobs_Field(String name)
	{
		super(name, new corn(null), 5); //TODO extend this to a more realistic number
		flammability = 90;
	}
}

///////////////////////////////
class Castle extends Location
{
	Castle(String name)
	{
		super(name);
		Action laugh_at_plebs = new oppress_peasants(null);
		options.put("laugh_at_plebs", laugh_at_plebs);
	}

	class oppress_peasants extends Action
	{
		oppress_peasants(Person who_does)
		{
			super(who_does);
			description = "Oppress some peasants";
		}
	
		public boolean can_be_done(Person by_whom)
		{
			return by_whom.is_Weak();
		}
		
		public void What_Happens()
		{
			helpers.output("Haha! Those fools should have been born with money. Let's make them fight for coppers some more!");
			helpers.finish_output();
			
			for(Person guy : who_is_here.values())
			{
				//TODO
				//guy.know_about_oppressing_peasants(helpers.get_PC());
				guy.react_to_oppressing_peasants(helpers.get_PC());
			}
		}
	}
}


class corn extends consumable_item
{
	corn(Person owner)
	{
		super(owner);
		ability = new eat_tasty_corn();
		rules_description = ability.description;

		name = "corn";
		flavor_text = "a cob with corn on it";
		weight = 1;
		value = 1;
	}
	
	class eat_tasty_corn extends consume
	{
		eat_tasty_corn()
		{
			super(owner);
			description = "eat some tasty corn";
		}
		
		public void Other_Happenings()
		{
			helpers.output("Yum! That was delicious!");
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
		
		unattended_stuff.put("torch", new torch(null));
	}
	
}

class Dungeon_Fact extends Fact
{
	Dungeon_Fact()
	{
		super("dungeons","We have a dungeon. You should visit it some time.");
	}
	
	public void on_learn(Person learner)
	{
		learner.learn_about_location("Dungeon Below Keshie's Castle", 25);
	}
}

class Fields extends Location
{
	Fields(String name, item grow_thing, int how_quickly)
	{
		super(name);
		thing_that_grows = grow_thing;
		time_it_takes = how_quickly;
		time_till_done = how_quickly;
	}
	
	@Override
	public boolean burn_location()
	{
		unattended_stuff.remove(thing_that_grows.get_name());
		return super.burn_location();
	}
	
	@Override
	public void time_passes()
	{
		super.time_passes();
		if(!burning)
		{
			grow();
		}	
	}
	
	void grow()
	{
		time_till_done--;
		if(time_till_done==0)
		{
			time_till_done = time_it_takes;
			add_item(thing_that_grows);
		}
	}
	
	
	
	protected int time_till_done;
	protected int time_it_takes;
	protected item thing_that_grows;
}

///////////////////////////////
class Forge extends Location
{
	Forge(String name)
	{
		super(name);
		flammability = 20;
	} 

}

class Hank_likes_wine extends Fact
{
	Hank_likes_wine()
	{
		super("wine", "Hank *really* likes wine. You could say he has a hankering for it.");
	}
	
	@Override
	public void on_learn(Person learner)
	{
		if(learner instanceof Player_Character)
		{
			((Player_Character) learner).add_action(new give_Hank_wine(learner));
		}
		else if(learner instanceof NPC)
		{
			((NPC) learner).add_action((NPC_Action) new give_Hank_wine(learner)); 
		}
	}
	
	class give_Hank_wine extends Action implements NPC_Action
	{
		give_Hank_wine(Person who_does)
		{
			super(who_does);
			description = "give Hank some wine";
		}
		
		public boolean can_be_done()
		{
			boolean has_wine = by_whom.has_item("wine");
			if(has_wine)
			{
				boolean same_location = by_whom.getCurrent_location().has_present("Hank");
				if(same_location)
				{
					return true;
				}
			}
			
			return false;
		}
		
		public void What_Happens()
		{
			//player loses wine
			by_whom.decrease_item("wine");
			
			NPC Hank = helpers.Get_Person_by_name("Hank");
			Hank.alter_liking(helpers.get_PC(), 30);
			Hank.add_item(new wine(Hank));

			//helpers.get_PC().remove_action(myself);
		}
		
		Action myself = this;

		@Override
		public int how_likely()
		{
			return 0;
		}
	}
}

class health_potion extends consumable_item
{
	health_potion(Person owner)
	{
		super(owner);
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
			super(owner);
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

///////////////////////////////
class Hovel extends Location
{
	Hovel(String name)
	{
		super(name);
		flammability = 75;
	}
}


class My_Game_Initializer extends Game_Initializer
{
	public void Initialize(HashMap<String, Location> places, HashMap<String, NPC> NPCs)
	{
			/////Locations
			Location Blacksmith = new Forge("Blacksmith's");
			Location Bobs_Fields = new Bobs_Field("Bob's Field");
			Location Keshies_Castle = new Castle("Keshie's Castle");
			Location Dungeon = new Dungeon("Dungeon Below Keshie's Castle");
				Dungeon.add_item(new Destromath_the_Desolator());
			Location Bobs_Hovel = new Hovel("Bob's Hovel");
			Location Tavern = new Tavern("The Tavern");
			Location The_Oakenshields = new Hovel("The Oakenshields'");
			places.put("Blacksmith's", Blacksmith);
			places.put("Bob's Field", Bobs_Fields);
			places.put("Keshie's Castle", Keshies_Castle);
			places.put("Dungeon Below Keshie's Castle", Dungeon);
			places.put("Bob's Hovel", Bobs_Hovel);
			places.put("The Tavern", Tavern);
			places.put("The Oakenshields", The_Oakenshields);
			
			
			////Persons
			NPC Bob = new Commoner(Bobs_Fields, "Bob");
			Bob.set_home(Bobs_Hovel);
			Bob.add_action(new steal_wine(Bob));
			Bob.set_personal_greeting("'allo, Govna");
			Bobs_Fields.AddPerson(Bob);
			NPCs.put("Bob", Bob);
			
			NPC Generic_Noble = new Noble(Keshies_Castle, "High Lord Kesh of No-Funnington");
			Generic_Noble.add_fact(new boring_fact("goblins","There are goblins in the mountains. It's as good a plot hook as any."));
			Generic_Noble.add_fact(new Dungeon_Fact());
			Generic_Noble.set_personal_greeting("greetings, peasant");
			Keshies_Castle.AddPerson(Generic_Noble);
			NPCs.put("Noble Kesh", Generic_Noble);
			
			NPC Norton = new Commoner(Tavern, "Norton");
			Norton.set_mood(Mood.angry);
			Norton.set_personal_greeting("hey, you git");
			Tavern.AddPerson(Norton);
			NPCs.put("Norton", Norton);
			
			NPC Hank = new Noble_Hank(Keshies_Castle);
			Hank.add_fact(new Hank_likes_wine());
			Keshies_Castle.AddPerson(Hank);
			NPCs.put("Hank", Hank);
			
			NPC Doug = new Body_Guard(Keshies_Castle, "Doug", Hank);
			Doug.add_fact(new Hank_likes_wine());
			Keshies_Castle.AddPerson(Doug);
			NPCs.put("Doug", Doug);
			
			NPC Morris = new Commoner(Bobs_Fields, "Morris Oakenshield");
			Morris.set_home(The_Oakenshields);
			Bobs_Fields.AddPerson(Morris);
			Morris.set_personal_greeting("Have you met my wife, Elen?");
			NPCs.put("Morris", Morris);
			
			NPC Elen = new Commoner(Keshies_Castle, "Elen Oakenshield");
			Elen.set_home(The_Oakenshields);
			Elen.set_personal_greeting("Have you met my husband, Morris?");
			Keshies_Castle.AddPerson(Elen);
			NPCs.put("Elen", Elen);
			
			Morris.alter_liking(Elen, -5);
			Elen.alter_liking(Morris, -5);
			
	}

	public Player_Character new_Player()
	{
		return new Player_Character(new Start_Location());
	}
}

class Noble_Hank extends Noble
{
	Noble_Hank(Location L)
	{
		super(L, "Hank");
		
		potential_actions.put("drink wine", new drink_wine(this));
		
		personal_greeting = "Do you have any wine?";
	}
	
	class drink_wine extends Action implements NPC_Action
	{
		drink_wine(Person who_does)
		{
			super(who_does);
			description = "drinks some wine";
		}
		
		
		public boolean can_be_done()
		{
			return by_whom.has_item("wine");
		}
		
		@Override
		public void What_Happens()
		{
			mood = Mood.happy;
			
			//decrement wine
			if(helpers.random(1, 3)==1)
			{
				by_whom.decrease_item("wine");
			}
		}


		@Override
		public int how_likely()
		{
			return 30;
		}
	}
}


///////////////////////////////
class Start_Location extends Location
{

}

///////////////////////////////
class Tavern extends Location
{
	Tavern(String name)
	{
		super(name);
		
		wine unattended_wine = new wine(null);
		unattended_wine.set_stolen(true);
		unattended_wine.set_count(100);
		
		unattended_stuff.put("wine", unattended_wine);
		
		flammability = 30;
	}
	
	@Override
	protected int get_flammability()
	{
		int how_much = unattended_stuff.get("wine").get_count();
		
		return Math.max(0, how_much);
	}
}

class torch extends consumable_item
{
	torch(Person owner)
	{
		super(owner);
		ability = new burn();
		rules_description = ability.description;

		name = "torch";
		flavor_text = "it's for lighting things on fire";
		weight = 1;
		value = 1;
	}
	
	class burn extends consume
	{
		burn()
		{
			super(owner);
			description = "burn things";
		}
		
		protected void Other_Happenings()
		{
			helpers.output("Which?");
			helpers.output_partial_list(1, "Building");
			helpers.output_partial_list(2, "Item");
			helpers.finish_output();
			
			int which = helpers.which_one(2) + 1;
			
			if(which==1)
			{
				Location here = owner.getCurrent_location();
				
				if(!here.burn_location())
				{
					helpers.output("it doesn't catch");
				}
			}
			else
			{
				item which_item = helpers.get_PC().PC_select_item();
				
				if(which_item.isFlammable())
				{
					helpers.get_PC().remove_item(which_item);
				}
			}
		}
	}
}

class wine extends consumable_item
{
	wine(Person owner)
	{
		super(owner);
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
			super(owner);
			description = "you drink, and you get drunk";
		}

		protected void Other_Happenings()
		{
			helpers.get_PC().add_drunkeness(5);
		}
	}
}

class steal_wine extends Action implements NPC_Action
{
	steal_wine(Person who_does)
	{
		super(who_does);
		description = " does nothing suspicious";
	}
	
	@Override
	public boolean can_be_done()
	{
		return (helpers.get_PC().getCurrent_location().equals(by_whom.getCurrent_location()) && helpers.get_PC().has_item("wine"));
	}
	
	public void What_Happens()
	{
		helpers.get_PC().steal(new wine(by_whom));
	}

	@Override
	public int how_likely()
	{
		return 10;
	}
}

class Destromath_the_Desolator extends equippable_item
{
	Destromath_the_Desolator()
	{
		super(equip_region.two_handed);
		name = "Destromath the Desolator";
		flavor_text = "An Icy Blade that steals the souls of those it kills";
	}
	
	@Override
	public void on_equip(Person who)
	{
		super.on_equip(who);
		who.injure(10);
		who.increase_attack_power(1000);
		who.increase_defense_power(-1000);
	}
	
	@Override
	public void on_unequip(Person who)
	{
		super.on_unequip(who);
		who.increase_attack_power(-1000);
		who.increase_defense_power(1000);
	}

	@Override
	public item copy_item(int how_many, Person new_owner)
	{
		item new_item  = new Destromath_the_Desolator();
		super.cheaty_copy_item(new_item, how_many, new_owner);
		
		return new_item;
	}
	
}