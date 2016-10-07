///////////////////////////////


abstract class consumable_item extends item
{
	public consumable_item(Person owner)
	{
		super(owner);
	}
	
	abstract class consume extends Action
	{
		consume(Person who_does)
		{
			super(who_does);
		}
		
		final public void What_Happens()
		{
			String item_name = consumable_item.this.get_name();
			by_whom.decrease_item(item_name);
			Other_Happenings();
		}
		
		abstract protected void Other_Happenings();
	}
}


abstract class equippable_item extends item
{
	equippable_item(equip_region where)
	{
		super(helpers.get_PC());
		equipped = false;
		slot = where;
	}
	
	public void Display()
	{
		super.Display();
		helpers.output(slot.toString()); //I really hope this works
		helpers.finish_output();
	}
	
	public void on_equip(Person who)
	{
		System.err.println("equipped " + this.name + " item");
		equipped = true;
		
	}
	
	public void on_unequip(Person who)
	{
		System.err.println("unequipped " + this.name + " item");
		equipped = false;
	}
	
	private boolean equipped;
}

class gold extends item
{
	gold(Person owner, int how_much)
	{
		super(owner);
		
		name = "gold";
		rules_description = "Money";
		flavor_text = "shiny coins that glitter in the light";
		weight = 1;
		value = 5;
		stolen = false;
		ability = new Nothing(owner);
		slot=equip_region.not_equippable;
		quantity = how_much;
		flammable = true;
	}

	@Override
	public item copy_item(int how_many, Person new_owner)
	{
		item new_item = new gold(new_owner, how_many);
		this.cheaty_copy_item(new_item, how_many, new_owner);
		return new_item;
	}
}

//don't make this abstract. You can have random 'stuff' items
abstract class item
{
	abstract public item copy_item(int how_many, Person new_owner);
//	{
//		item new_item = new item(new_owner);
//		new_item.name = this.name;
//		new_item.rules_description = this.rules_description;
//		new_item.flavor_text = this.flavor_text;
//		new_item.weight = this.weight;
//		new_item.value = this.value;
//		new_item.stolen = this.stolen;
//		new_item.ability = this.ability;
//		new_item.slot = this.slot;
//		new_item.quantity = how_many;
//
//		this.cheaty_copy_item(new_item, how_many, new_owner);
//		
//		return new_item;
//	}
	
	protected void cheaty_copy_item(item new_item, int how_many, Person new_owner)
	{
		new_item.name = this.name;
		new_item.rules_description = this.rules_description;
		new_item.flavor_text = this.flavor_text;
		new_item.weight = this.weight;
		new_item.value = this.value;
		new_item.stolen = this.stolen;
		new_item.ability = this.ability.copy_Action(new_owner);
		new_item.slot = this.slot;
		new_item.owner = new_owner;
	}
	
	item(Person new_owner)
	{
		owner = new_owner;
		name = "a 'thing'";
		rules_description = "no, really, this is just the default item description, it does nothing";
		flavor_text = "a boring 'thing' with no discernable use";
		weight = 0;
		value = 0.0000001;
		stolen = false;
		ability = new Nothing(owner);
		slot=equip_region.not_equippable;
		quantity = 1;
		flammable = true;
	}

	public void Display()
	{
		helpers.output("Name: " +name);
		helpers.output(rules_description);
		helpers.output("Weight: " + weight + " Value: " + value + weight/value);
		helpers.output("Stolen: " + stolen);
		helpers.output("Count: " + quantity);
		helpers.finish_output();
	}
	
	public Action get_abiltiy()
	{
		return ability;
	}
	
	public int get_count()
	{
		return quantity;
	}

	public String get_name()
	{
		return name;
	}
	
	public equip_region get_slot()
	{
		return slot;
	}
	
	public boolean get_stolen()
	{
		return stolen;
	}
	
	public int get_weight()
	{
		return weight;
	}
	
	/**
	 * a check should be done that this does not set to negative. I will error you if you do.
	 * same with going to 0. Delete the item if you're going to 0 of them.
	 * 
	 * @param how_many to add
	 * if negative, removes them
	 */
	public void increase_quantity(int how_many) 
	{
		assert(this.quantity + how_many <= 0);
		
		quantity = quantity + how_many;
	}
	
	public boolean isFlammable()
	{
		return flammable;
	}
	
	public void set_count(int how_many)
	{
		quantity = how_many;
	}
	
	public void set_stolen(boolean is_stolen)
	{
		stolen = is_stolen;
	}
	
	class Nothing extends Action
	{
		Nothing(Person who_does)
		{
			super(who_does);
			description = "nothing will happen";
		}
		
		public void What_Happens()
		{
			helpers.output("surprisingly, nothing happens");
			helpers.finish_output();
		}
		
		@Override
		public Action copy_Action(Person to_whom)
		{
			Action blarg = new Nothing(to_whom);
			blarg.cheaty_copy_action(this, to_whom);
			return blarg;
		}
	}
	
	protected String name;
	protected String rules_description;
	protected String flavor_text;
	protected int weight;
	protected double value;
	protected boolean stolen;
	protected Action ability;
	protected equip_region slot;
	protected int quantity;
	protected boolean flammable;
	protected Person owner;
}

//only here for testing
class lazy_equip extends equippable_item
{
	lazy_equip(equip_region where)
	{
		super(where);
	}

	@Override
	public item copy_item(int how_many, Person new_owner)
	{
		item arg = new lazy_equip(this.slot);
		super.cheaty_copy_item(arg, how_many, new_owner);
		
		return arg;
	}
}



class readable_item extends item
{
	readable_item(String base_name, String base_text, Person owner)
	{
		super(owner);
		
		name = base_name;
		weight = 0;
		value = 0;
		stolen = false;
		read = false;
		ability = new Read(owner);
		text = base_text;
	}
	
	public void Display()
	{
		helpers.output("Name: " + name);
		helpers.output(rules_description);
		helpers.output(flavor_text);
		helpers.output("Weight: " + weight + " Value: " + value + weight/value);
		helpers.output("Stolen: " + stolen);
		helpers.output("Count: " + quantity);
		helpers.output("Read: " + read);
		helpers.finish_output();
	}
	
	class Read extends Action
	{
		Read(Person who_does)
		{
			super(who_does);
			description = "Display the writing";
		}
		
		public void What_Happens()
		{
			helpers.output(text);
			helpers.finish_output();
			
			read = true;
		}

		@Override
		public Action copy_Action(Person to_whom)
		{
			Action new_read = new Read(by_whom);
			new_read.cheaty_copy_action(this, to_whom);
			return new_read;
		}

	}
	
	@Override
	public item copy_item(int how_many, Person new_owner)
	{
		item new_item = new readable_item(this.get_name(), this.flavor_text, new_owner);
		this.cheaty_copy_item(new_item, how_many, new_owner);
		return new_item;
	}
	
	private String text;
	private boolean read;
	

}